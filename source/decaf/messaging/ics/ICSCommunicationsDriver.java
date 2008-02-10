/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Carson Day (carsonday@gmail.com)
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package decaf.messaging.ics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.messaging.inboundevent.IcsInboundEvent;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.inform.NotificationEvent;
import decaf.messaging.inboundevent.inform.ResponseTimeEvent;
import decaf.messaging.inboundevent.inform.UserNameChangedEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.resources.ResourceManager;
import decaf.resources.ResourceManagerFactory;
import decaf.thread.ThreadManager;
import decaf.util.PropertiesConstants;

public class ICSCommunicationsDriver implements Subscriber, Preferenceable {

	private static final Logger LOGGER = Logger
			.getLogger(ICSCommunicationsDriver.class);

	private static final String LEGAL_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 "
			+ "!@#$%^&*()-=_+`~[{]}\\|;:'\",<.>/?";

	private static final ResourceManager resourceManager = ResourceManagerFactory
			.getManager();

	private static final String INTERFACE = resourceManager.getString(
			PropertiesConstants.DECAF_PROPERTIES, PropertiesConstants.VERSION);

	private MessagingLog messageLog = new MessagingLog();

	private static final String PROMPT = "fics%";

	private static final int PROMPT_LENGTH = PROMPT.length();

	private static final String LOGIN_PROMPT = "login:";

	private static final String PASSWORD_PROMPT = "password:";

	private static final int ICS_ID = 0;

	private static final int READ_BUFFER_SIZE = 25000;

	private TimesealStrategy timesealSource;

	private InputStream inputStream;

	private List<Runnable> cachedSendMessages = new LinkedList<Runnable>();

	private boolean isConnecting = true;

	private FicsParser ficsParser = new FicsParser(ICS_ID);

	private ICSOutboundMessageHandler outboundMessageHandler;

	private long lastMessageSentMillis;

	private boolean isTimesealEnabled;

	private Preferences preferences;

	private int port;

	private String url;

	private Timer delayTimer;

	public ICSCommunicationsDriver(Preferences preferences) {

		this.preferences = preferences;

		if (preferences.getChatPreferences().isPreventingIdleLogout()) {
			delayTimer = new Timer();
			delayTimer.schedule(new TimerTask() {
				public void run() {
					EventService.getInstance().publish(
							new OutboundEvent("date"));
				}
			}, 60000 * 50, 60000 * 50);
		}
		outboundMessageHandler = new ICSOutboundMessageHandler(this);
		messageLog.setPreferences(preferences);
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		messageLog.setPreferences(preferences);
	}

	public String getDriverDescription() {
		return "ICS Driver";
	}

	public void disconnect() {
		try {
			outboundMessageHandler.dispose();
		} catch (Exception e) {
		}

		try {
			if (timesealSource != null) {
				timesealSource.disconnect();
				inputStream = null;
			}
		} catch (Exception e) {
		}

		if (delayTimer != null) {
			delayTimer.cancel();
		}

		timesealSource = null;
	}

	public void connect(String url, int port, String userName, String password,
			boolean isConnectingAsGuest, boolean isTimesealEnabled)
			throws IOException

	{
		isConnecting = true;

		this.isTimesealEnabled = isTimesealEnabled;
		this.url = url;
		this.port = port;
		boolean isApplet = ResourceManagerFactory.getManager().getString("os",
				"os").equals("applet");

		if (isTimesealEnabled) {
			String timesealExe = getTimesealSystemProp();
			if (timesealExe == null) {
				timesealExe = ResourceManagerFactory.getManager().getString(
						"Timeseal", "timeseal");
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Using timeseal from system property: "
							+ timesealExe);
				}
			}

			if (timesealExe == null) {
				LOGGER
						.fatal("Could not find ./properties/Timeseal.properties with a timeseal property.");
				throw new RuntimeException(
						"Could not find ./properties/Timeseal.properties with a timeseal property.");
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Using timeseal: " + timesealExe);
				}
			}

			// timesealSource = new SocketTimesealStrategy();
			timesealSource = new ProcessTimesealStrategy(timesealExe);
		} else {
			timesealSource = new NoTimesealStrategy();
		}

		EventService.getInstance().publish(
				new NotificationEvent(ICS_ID, "Connecting to "
						+ url
						+ ":"
						+ port
						+ (isTimesealEnabled ? " with Timeseal."
								: " without Timeseal.")));

		try {
			timesealSource.connect(url, port);
			inputStream = timesealSource.getInputStream();

		} catch (Exception ioe) {
			LOGGER.error(ioe);
			EventService.getInstance().publish(
					new NotificationEvent(ICS_ID,
							"Could not establish connection."));
			throw new RuntimeException(ioe);
		}

		LoginHandler loginHandler = new LoginHandler(userName, password);
		Thread loginThread = new Thread(loginHandler);
		loginThread.start();

		try {
			loginThread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			throw new RuntimeException("Unexpected interruption occured");
		}

		if (loginHandler.isLoggedIn) {

			InboundMessageHandler messageHandler = new InboundMessageHandler();
			Thread thread = new Thread(messageHandler);
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.start();

			LOGGER.debug(loginHandler.userName);
			UserNameChangedEvent nameChangedEvent = new UserNameChangedEvent(
					ICS_ID, "Logging you in as " + loginHandler.userName,
					loginHandler.userName, isConnectingAsGuest);

			EventService.getInstance().publish(nameChangedEvent);

			sendNonBlockedMessage("iset defprompt 1");
			sendNonBlockedMessage("iset gameinfo 1");
			sendNonBlockedMessage("iset ms 1");
			sendNonBlockedMessage("iset allresults 1");
			sendNonBlockedMessage("iset premove "
					+ (preferences.getBoardPreferences().getPremoveType() == BoardPreferences.NO_PREMOVE ? "0"
							: "1"));
			sendNonBlockedMessage("iset smartmove "
					+ (preferences.getBoardPreferences().isSmartMoveEnabled() ? "1"
							: "0"));
			sendNonBlockedMessage("set interface " + INTERFACE);

			sendNonBlockedMessage("set style 12");
			sendNonBlockedMessage("set bell 0");

			if (!isApplet) {
				sendLoginScript();
			} else {
				sendNonBlockedMessage("set noescape on");
				sendNonBlockedMessage("set autoflag on");
				sendNonBlockedMessage("set seek off");
			}

			try {
				Thread.sleep(250);
			} catch (InterruptedException ie) {
			}

			sendNonBlockedMessage("iset lock 1");
			isConnecting = false;

			// execute the cached messages.
			for (Runnable runnable : cachedSendMessages) {
				ThreadManager.execute(runnable);
			}
			cachedSendMessages.clear();

		} else {
			LOGGER.error("Logged failed");
			EventService.getInstance().publish(
					new NotificationEvent(ICS_ID, "Disconnected from  " + url
							+ ":" + port + "."));
			disconnect();
		}
	}

	private void sendLoginScript() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"properties/LoginScript.txt"));
			String line = reader.readLine();

			while (line != null && !line.equals("")) {
				line = line.trim();
				if (!line.startsWith("#")) {
					sendNonBlockedMessage(line);
				}
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			try {
				reader = new BufferedReader(new FileReader(new File(
						ResourceManagerFactory.getManager().getDecafUserHome()
								.getAbsolutePath()
								+ "/properties/LoginScript.txt")));

				String line = reader.readLine();

				while (line != null && !line.equals("")) {
					line = line.trim();
					if (!line.startsWith("#")) {
						sendNonBlockedMessage(line);
					}
					line = reader.readLine();
				}

			} catch (IOException ioe2) {
				LOGGER.error("Could not find properties/LoginScript.txt");
			}

		} finally {
			try {
				reader.close();
			} catch (IOException ioe) {
			}
		}
	}

	void handlePublishingEventAndLogging(OutboundEvent event) {
		if (event.getText() != null && !event.getText().equals("")) {
			if (event.getResponseEventTypeToHideFromUser() != null) {
				ficsParser.hideNextClassFromUser(event
						.getResponseEventTypeToHideFromUser());
			}
			if (!event.isHidingFromUser()) {
				messageArrived(event.getText());
			}
		}
	}

	/**
	 * Returns null if nothing should be sent. Otherwise returns the string to
	 * send.
	 */
	private String filterOutboundMessage(String message) {
		String illegalCharacters = "";
		message = message.trim();
		String upcasedMessage = message.trim().toUpperCase();
		String firstWord = null, secondWord = null, thirdWord = null;

		StringTokenizer tok = new StringTokenizer(upcasedMessage, " ");
		if (tok.hasMoreTokens()) {
			firstWord = tok.nextToken();
			if (tok.hasMoreTokens()) {
				secondWord = tok.nextToken();
				if (tok.hasMoreTokens()) {
					thirdWord = tok.nextToken();
				}
			}
		}

		if ((firstWord != null && secondWord != null && thirdWord != null
				&& firstWord.equalsIgnoreCase("SET")
				&& secondWord.equalsIgnoreCase("STYLE") && !thirdWord
				.equals("12"))
				|| (firstWord != null && secondWord != null
						&& firstWord.equalsIgnoreCase("SET") && secondWord
						.equals("12"))) {
			EventService
					.getInstance()
					.publish(
							new IcsNonGameEvent(
									ICS_ID,
									"You are changing the style to something other "
											+ " than 12. Decaf will now not be able to "
											+ "interpet game events correctly. To fix this set "
											+ "style 12."));
		} else {

			for (int i = 0; i < message.length(); i++) {
				char currentChar = message.charAt(i);
				if (LEGAL_CHARACTERS.indexOf(currentChar) == -1) {
					illegalCharacters += currentChar;
					message = message.substring(0, i)
							+ (i == message.length() - 1 ? "" : message
									.substring(i + 1));
					i--;
				}
			}

			if (illegalCharacters.length() != 0) {
				EventService
						.getInstance()
						.publish(
								new IcsNonGameEvent(
										ICS_ID,
										"Outgoing message contained invalid chracters \""
												+ illegalCharacters
												+ "\" it was sent after being trimmed to \""
												+ message
												+ "\" to avoid disconnection from fics."));
			}
		}

		return message;
	}

	private void logCommunication(String message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(message);
		}
	}

	private String getTimesealSystemProp() {
		return System.getProperty("decaf.gui.ics.timeseal");
	}

	private void sendNonBlockedMessage(final String message) throws IOException {

		// This method does not handle timeseal ack messages.
		// That code has been moved to SocketReader.
		String filteredMessage = filterOutboundMessage(message);

		if (filteredMessage == null) {
			LOGGER.warn("Attempted to send null message");
			return;
		}
		filteredMessage += "\n";

		lastMessageSentMillis = System.currentTimeMillis();
		timesealSource.sendMsg(filteredMessage);

		if (LOGGER.isDebugEnabled()) {
			logCommunication("out: " + filteredMessage);
		}
		if (preferences.getLoggingPreferences().isLoggingEnabled()) {
			messageLog.logOutbound(message);
		}
	}

	void sendMsg(final String message) {

		Runnable runnable = new Runnable() {
			public void run() {
				try {
					sendNonBlockedMessage(message);
				} catch (IOException ioe) {
				}
			}
		};

		if (isConnecting) {
			cachedSendMessages.add(runnable);
		} else {
			ThreadManager.execute(new Runnable() {
				public void run() {
					try {
						sendNonBlockedMessage(message);
					} catch (IOException ioe) {
					}
				}
			});
		}
	}

	/**
	 * First calls handleMessageTransformation. If it returns true does nothing.
	 * Otherwise, sends the event to the event service.
	 */
	private void sendEvent(IcsInboundEvent event) {
		EventService.getInstance().publish(event);

	}

	private void beginingMessageArrived(String text) {
		IcsInboundEvent textReceivedEvent = new IcsNonGameEvent(ICS_ID, text);
		sendEvent(textReceivedEvent);
	}

	private void messageArrived(String text) {

		publishInboundEvents(text);
	}

	private void publishInboundEvents(String text) {
		IcsInboundEvent[] events = ficsParser.parse(new StringBuffer(text));

		for (int i = 0; i < events.length; i++) {
			sendEvent(events[i]);
			if (preferences.getLoggingPreferences().isLoggingEnabled()) {
				messageLog.log(events[i]);
			}
		}
	}

	private void connectionLost() {
		EventService.getInstance().publish(
				new NotificationEvent(ICS_ID, "Connection " + url + ":" + port
						+ " has been lost."));
	}

	private void handleResponseTime(long responseTime) {
		EventService.getInstance().publish(
				new ResponseTimeEvent(ICS_ID, responseTime));
	}

	private class LoginHandler implements Runnable {
		private String userName;

		private String password;

		private boolean isUnnamedGuest;

		public boolean isLoggedIn = false;

		private boolean isGuest;

		public LoginHandler(String userName, String password) {
			this.userName = userName;
			this.password = password;

			if (userName == null || userName.equals("")) {
				this.userName = "g";
			}
			isUnnamedGuest = this.userName.equalsIgnoreCase("g");
			isGuest = password == null || password.trim().equals("");
		}

		public boolean isLoggedIn() {
			return isLoggedIn;
		}

		public void run() {
			StringBuffer buffer = new StringBuffer(READ_BUFFER_SIZE);
			boolean hasSeenLoginPrompt = false;
			boolean hasSentUserName = false;
			try {
				// handle log in messages
				while (true) {
					char currentChar = (char) inputStream.read();

					if (currentChar == 65535) {
						break;
					} else {
						buffer.append(currentChar);
					}

					// For future socket timeseal.
					if (buffer.length() > 2
							&& buffer.charAt(buffer.length() - 3) == '['
							&& buffer.charAt(buffer.length() - 2) == 'G'
							&& buffer.charAt(buffer.length() - 1) == ']') {
						LOGGER.error("Sent ack");
						timesealSource.sendAck();
						buffer.delete(buffer.length() - 3, buffer.length());
						continue;
					}

					if (buffer.length() >= LOGIN_PROMPT.length()
							&& buffer.substring(
									buffer.length() - LOGIN_PROMPT.length())
									.equals(LOGIN_PROMPT)) {
						if (hasSeenLoginPrompt) {
							beginingMessageArrived(buffer.toString());
							buffer = new StringBuffer(READ_BUFFER_SIZE);
							break;
						}
						if (userName != null && !userName.equals("")) {
							sendNonBlockedMessage(userName);
							beginingMessageArrived(buffer.toString());
							buffer = new StringBuffer(READ_BUFFER_SIZE);
							hasSeenLoginPrompt = true;
							hasSentUserName = true;
						}
					} else if ((isGuest || isUnnamedGuest) && hasSentUserName
							&& (currentChar == ';' || currentChar == ':')) {
						if (isUnnamedGuest && currentChar == ';') {
							int lastSpace = buffer.lastIndexOf(" ");

							userName = buffer.substring(lastSpace + 2, buffer
									.length() - 2);

							LOGGER.debug("Set user name to " + userName);
							isUnnamedGuest = false;
							beginingMessageArrived(buffer.toString());
							buffer = new StringBuffer(READ_BUFFER_SIZE);
						} else if (currentChar == ':') {
							sendNonBlockedMessage("");
							beginingMessageArrived(buffer.toString());
							buffer = new StringBuffer(READ_BUFFER_SIZE);
							isUnnamedGuest = false;
							isGuest = false;
						}
					} else if (buffer.length() >= PASSWORD_PROMPT.length()
							&& buffer.substring(
									buffer.length() - PASSWORD_PROMPT.length())
									.equals(PASSWORD_PROMPT)) {
						if (password != null && !(password.equals(""))) {
							sendNonBlockedMessage(password);
							beginingMessageArrived(buffer.toString());
							buffer = new StringBuffer(READ_BUFFER_SIZE);
						}
					} else if (buffer.length() == buffer.capacity()) {
						beginingMessageArrived(buffer.substring(0, buffer
								.length() - 1)
								+ "\r");
						buffer = new StringBuffer(READ_BUFFER_SIZE);
					} else if (buffer.length() >= PROMPT.length()
							&& buffer
									.substring(buffer.length() - PROMPT_LENGTH)
									.equals(PROMPT)) {
						beginingMessageArrived(buffer.substring(0,
								buffer.length() - PROMPT_LENGTH).trim());
						isLoggedIn = true;
						break;
					}
				}

				if (!isLoggedIn) {
					beginingMessageArrived(buffer.toString());
					throw new IOException("Error logging in");
				}

			} catch (IOException ioe) {
				isLoggedIn = false;
				beginingMessageArrived(buffer.toString());
				ioe.printStackTrace();
				// disconnect();
			}
		}
	}

	private class InboundMessageHandler implements Runnable {

		public InboundMessageHandler() {

		}

		private void trimPrompt(StringBuffer message) {
			/*
			 * iv_defprompt
			 * 
			 * Setting ivariable defprompt forces the user's prompt to 'fics% '
			 * or if the user has ptime set to 'hh:mm_fics% '. This is to make
			 * it possible to parse data if the user has changed the prompt.
			 * 
			 * See Also: iset ivariables
			 */

			if (message.length() >= PROMPT.length()) {
				if (message.charAt(message.length() - PROMPT.length()) == '_') {
					message.delete(message.length() - PROMPT.length() + 6,
							message.length());
				} else {
					message.delete(message.length() - PROMPT.length(), message
							.length());
				}
			}
		}

		private void trimTail(StringBuffer message) {
			while (message.length() > 0
					&& (message.charAt(message.length() - 1) == '\n'
							|| message.charAt(message.length() - 1) == '\r' || message
							.charAt(message.length() - 1) == ' ')) {
				message.deleteCharAt(message.length() - 1);
			}
		}

		private boolean endsWithPrompt(StringBuffer message) {
			boolean endsWithPrompt = true;
			String stringMessage = message.toString();
			// A hack to fix what happens when your partner moves first before
			// you receive the position.
			if (message.length() < 600 && stringMessage.startsWith("Creating")
					&& stringMessage.indexOf("bughouse") != -1
					&& stringMessage.indexOf("<g1>") != -1
					&& stringMessage.indexOf("<12>") == -1) {
				endsWithPrompt = false;
			} else {
				int bufferIndex = message.length() - 1;
				for (int i = PROMPT.length() - 1; endsWithPrompt && i > -1; i--) {
					endsWithPrompt = message.charAt(bufferIndex--) == PROMPT
							.charAt(i);
				}
			}
			return endsWithPrompt;
		}

		public void run() {
			StringBuffer buffer = new StringBuffer(READ_BUFFER_SIZE);
			int chars = 0;
			try {
				try {
					boolean ignoreWhiteSpace = false;
					// handle all other messages
					do {
						char currentChar = (char) inputStream.read();

						if (lastMessageSentMillis != -1) {
							handleResponseTime(System.currentTimeMillis()
									- lastMessageSentMillis);
							lastMessageSentMillis = -1;
						}

						chars++;
						if (currentChar == 65535) {
							break;
						} else {
							if (ignoreWhiteSpace
									&& Character.isWhitespace(currentChar)) {
								// eat it
							} else {
								ignoreWhiteSpace = false;
								buffer.append(currentChar);
							}
						}

						// For future socket timeseal.
						if (buffer.length() > 2
								&& buffer.charAt(buffer.length() - 3) == '['
								&& buffer.charAt(buffer.length() - 2) == 'G'
								&& buffer.charAt(buffer.length() - 1) == ']') {
							timesealSource.sendAck();
							buffer.delete(buffer.length() - 3, buffer.length());
							continue;
						}

						if (buffer.length() >= PROMPT.length()) {
							if (endsWithPrompt(buffer)) {
								trimPrompt(buffer);
								trimTail(buffer);
								messageArrived(buffer.toString());
								buffer.delete(0, buffer.length());
								ignoreWhiteSpace = true;
								chars = 0;
							} else if (buffer.length() == buffer.capacity()) {
								sendEvent(new IcsNonGameEvent(ICS_ID, buffer
										.toString()));
								buffer.delete(0, buffer.length());
								chars = 0;

							}
						}
					} while (true);
				} catch (Throwable exception) {
					LOGGER.error(exception);
					throw new RuntimeException(exception);
				}
				messageArrived(buffer.toString());
				connectionLost();
			} catch (Throwable throwable) {
				throwable.printStackTrace();
			} finally {
				disconnect();
			}
		}
	}
}