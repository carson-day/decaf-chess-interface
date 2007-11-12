/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2007  Carson Day (carsonday@gmail.com)
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
package decaf.com.ics;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import decaf.com.CommunicationsDriver;
import decaf.com.ics.parser.BugClosedEventParser;
import decaf.com.ics.parser.BugOpenEventParser;
import decaf.com.ics.parser.CShoutEventParser;
import decaf.com.ics.parser.ChallengeEventParser;
import decaf.com.ics.parser.ChannelTellEventParser;
import decaf.com.ics.parser.ClosedEventParser;
import decaf.com.ics.parser.DroppableHoldingsChangedEventParser;
import decaf.com.ics.parser.GameEndEventParser;
import decaf.com.ics.parser.GameStartEventParser;
import decaf.com.ics.parser.IllegalMoveEventParser;
import decaf.com.ics.parser.InboundEventParser;
import decaf.com.ics.parser.KibitzEventParser;
import decaf.com.ics.parser.NotificationEventParser;
import decaf.com.ics.parser.ObservingGameStartEventParser;
import decaf.com.ics.parser.OpenEventParser;
import decaf.com.ics.parser.PartnerTellEventParser;
import decaf.com.ics.parser.PartnershipCreatedEventParser;
import decaf.com.ics.parser.PartnershipEndedEventParser;
import decaf.com.ics.parser.RemovingObservedGameEventParser;
import decaf.com.ics.parser.ShoutEventParser;
import decaf.com.ics.parser.Style12EventParser;
import decaf.com.ics.parser.TellEventParser;
import decaf.com.ics.parser.UnavailInfoEventParser;
import decaf.com.ics.parser.UserNameChangedEventParser;
import decaf.com.ics.parser.VariablesEventParser;
import decaf.com.ics.parser.WhisperEventParser;
import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.game.GameStartEvent;
import decaf.com.inboundevent.inform.ConnectedEvent;
import decaf.com.inboundevent.inform.DisconnectedEvent;
import decaf.com.inboundevent.inform.ResponseTimeEvent;
import decaf.com.inboundevent.inform.UserNameChangedEvent;
import decaf.com.outboundevent.OutboundEvent;
import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferences;
import decaf.gui.util.PropertiesConstants;
import decaf.gui.util.PropertiesManager;
import decaf.gui.util.StringUtility;

public class ICSCommunicationsDriver implements CommunicationsDriver,
		Subscriber {
	private static final Logger LOGGER = Logger
			.getLogger(ICSCommunicationsDriver.class);

	private static final String LEGAL_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890 "
			+ "!@#$%^&*()-=_+`~[{]}\\|;:'\",<.>/?";

	private static final PropertiesManager propertiesManager = PropertiesManager
			.getInstance();

	private static final String INTERFACE = propertiesManager.getString(
			PropertiesConstants.DECAF_PROPERTIES, PropertiesConstants.VERSION);

	private static final String TIMESEAL_EXE = propertiesManager.getString(
			PropertiesConstants.DECAF_PROPERTIES, PropertiesConstants.TIMESEAL);

	private static final String PROMPT = "fics%";

	private static final int PROMPT_LENGTH = PROMPT.length();

	private static final String LOGIN_PROMPT = "login:";

	private static final String PASSWORD_PROMPT = "password:";

	private static class SendMessageThreadPoolExecutor extends
			ThreadPoolExecutor {
		private boolean isPaused;

		private ReentrantLock pauseLock = new ReentrantLock();

		private Condition unpaused = pauseLock.newCondition();

		public SendMessageThreadPoolExecutor() {
			super(3, 10, 60 * 5, TimeUnit.SECONDS,
					new LinkedBlockingQueue<Runnable>());
		}

		protected void beforeExecute(Thread t, Runnable r) {
			super.beforeExecute(t, r);
			pauseLock.lock();
			try {
				while (isPaused)
					unpaused.await();
			} catch (InterruptedException ie) {
				t.interrupt();
			} finally {
				pauseLock.unlock();
			}
		}

		public void pause() {
			pauseLock.lock();
			try {
				isPaused = true;
			} finally {
				pauseLock.unlock();
			}
		}

		public void resume() {
			pauseLock.lock();
			try {
				isPaused = false;
				unpaused.signalAll();
			} finally {
				pauseLock.unlock();
			}
		}
	}

	private SendMessageThreadPoolExecutor sendMessageExecutor = new SendMessageThreadPoolExecutor();

	private static final int READ_BUFFER_SIZE = 25000;

	private boolean isConnectingAsGuest;

	private String user;

	private String url;

	private boolean isTimesealEnabled;

	private int port;

	private Socket socket;

	private InputStream bufferedInStream;

	private DataOutputStream dataOutStream;

	private EventService eventService;

	private List classesToHideFromUser;

	private List compositeParsers;

	private List chatParsers;

	private List nonCompositeNonChatParsers;

	private UserNameChangedEventParser userNameChangedParser;

	private static int disconnectedEventId;

	private static int connectionEventId;

	private static int inboundEventId;

	// start at 100000 so it will be unique from those created in the
	// GameStartEventParser.
	private static int gameStartEventId = 100000;

	private ICSOutboundMessageHandler outboundMessageHandler;

	private long lastMessageSentMillis;

	private Preferences preferences;

	private Timer delayTimer;

	public ICSCommunicationsDriver(Preferences preferences) {
		eventService = EventService.getInstance();
		classesToHideFromUser = new LinkedList();

		compositeParsers = new LinkedList();
		chatParsers = new LinkedList();
		nonCompositeNonChatParsers = new LinkedList();

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

		initParsers();

		outboundMessageHandler = new ICSOutboundMessageHandler(this);
	}

	private void logCommunication(String message) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(message);
		}
	}

	public String getDriverDescription() {
		return "ICS Driver";
	}

	private String getTimesealSystemProp() {
		return System.getProperty("decaf.gui.ics.timeseal");
	}

	public void connect(String url, int port, String userName, String password,
			boolean isConnectingAsGuest, boolean isTimesealEnabled)
			throws IOException

	{
		sendMessageExecutor.pause();
		this.url = url;
		this.port = port;
		this.isConnectingAsGuest = isConnectingAsGuest;
		this.isTimesealEnabled = isTimesealEnabled;
		String timesealExe = getTimesealSystemProp();

		if (timesealExe == null) {
			timesealExe = PropertiesManager.getInstance().getString("Timeseal",
					"timeseal");
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

		if (isConnectingAsGuest
				&& (userName == null || userName.trim().equals(""))) {
			userName = "g";
		}

		if (!isTimesealEnabled) {
			LOGGER
					.error("Connecting without timeseal is currently unsupported. Connecting with timeseal.");
		}

		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(new String[] { timesealExe, url,
				"" + port });

		dataOutStream = new DataOutputStream(process.getOutputStream());
		bufferedInStream = process.getInputStream();

		dataOutStream.flush();

		// LOGGER.info("Created socket connection to " + url + " port=" + port);

		LoginHandler loginHandler = new LoginHandler(userName, password);
		Thread loginThread = new Thread(loginHandler);
		loginThread.start();

		try {
			EventService.getInstance().publish(
					new ConnectedEvent(this, "" + connectionEventId++,
							"Connecting to " + url + ":" + port + ".",
							userName, url, port));
			loginThread.join();
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			throw new RuntimeException("Unexpected interruption occured");
		}

		if (loginHandler.isLoggedIn) {
			sendMessageExecutor.resume();
			InboundMessageHandler messageHandler = new InboundMessageHandler();
			Thread thread = new Thread(messageHandler);
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.start();

			UserNameChangedEvent nameChangedEvent = new UserNameChangedEvent(
					"blah", "1", "fool", userName);

			eventService.publish(nameChangedEvent);

			sendMsg("set style 12");
			sendMsg("iset defprompt 1");
			sendMsg("iset gameinfo 1");
			sendMsg("iset allresults 1");
			sendMsg("iset premove "
					+ (preferences.getBoardPreferences().getPremoveType() == BoardPreferences.NO_PREMOVE ? "0"
							: "1"));
			sendMsg("set interface " + INTERFACE);
			sendMsg("iset lock 1");
			sendMsg("set bell 0");

			sendLoginScript();

		} else {
			LOGGER.error("Logged failed");
			EventService.getInstance().publish(
					new ConnectedEvent(this, "" + connectionEventId++,
							"Disconnected from  " + url + ":" + port + ".",
							userName, url, port));
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
					sendMsg(line);
				}
				line = reader.readLine();
			}
		} catch (IOException ioe) {
			LOGGER.error("Error occured sending LoginScript", ioe);
		} finally {
			try {
				reader.close();
			} catch (IOException ioe) {
			}
		}
	}

	public boolean isConnected() {
		return socket == null;
	}

	public void disconnect() {
		outboundMessageHandler.dispose();
		try {
			if (socket != null)
				socket.close();
		} catch (Exception e) {
		}
		try {
			if (dataOutStream != null)
				dataOutStream.close();
		} catch (Exception e) {
		}

		try {
			if (bufferedInStream != null)
				bufferedInStream.close();
		} catch (Exception ioexception) {
		}

		if (delayTimer != null) {
			delayTimer.cancel();
		}

		/*
		 * if (communicationsOut != null) { try { communicationsOut.close(); }
		 * catch (IOException ioe) { } }
		 */

		socket = null;
		dataOutStream = null;
		bufferedInStream = null;
	}

	void handlePublishingEventAndLogging(OutboundEvent event) {
		if (!event.isHidingFromUser() && event.getText() != null
				&& !event.getText().equals(""))
			messageArrived(event.getText());
		if (event.getResponseEventTypeToHideFromUser() != null) {
			classesToHideFromUser.add(event
					.getResponseEventTypeToHideFromUser().getName());
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

		StringTokenizer tok = new StringTokenizer(message, " ");
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
			eventService
					.publish(new InboundEvent(
							this,
							"" + inboundEventId++,
							"You are changing the style to something other "
									+ " than 12. Decaffeinate will now not be able to "
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
				eventService.publish(new InboundEvent(this, ""
						+ inboundEventId++,
						"Outgoing message contained invalid chracters \""
								+ illegalCharacters
								+ "\" it was sent after being trimmed to \""
								+ message
								+ "\" to avoid disconnection from fics."));
			}
		}

		return message;
	}

	private void sendNonBlockedMessage(final String message) throws IOException {

		// This method does not handle timeseal ack messages.
		// That code has been moved to SocketReader.
		String filteredMessage = filterOutboundMessage(message);

		if (filteredMessage == null) {
			LOGGER.warn("Attempted to send null message");
			return;
		}

		filteredMessage += "\r\n";

		if (dataOutStream == null) {
			eventService.publish(new InboundEvent(this, "" + inboundEventId++,
					"Connection is broken, unable to send message: "
							+ filteredMessage));
		} else {
			lastMessageSentMillis = System.currentTimeMillis();
			dataOutStream.write(filteredMessage.getBytes());
			dataOutStream.flush();
			logCommunication("out: " + filteredMessage);

		}
	}

	void sendMsg(final String message) {

		sendMessageExecutor.execute(new Runnable() {
			public void run() {
				try {
					sendNonBlockedMessage(message);
				} catch (IOException ioe) {
				}
			}
		});
	}

	/**
	 * First calls handleMessageTransformation. If it returns true does nothing.
	 * Otherwise, sends the event to the event service.
	 */
	private void sendEvent(InboundEvent event) {
		logCommunication("publishing event: " + event);

		if (classesToHideFromUser.contains(event.getClass().getName())) {
			classesToHideFromUser.remove(event.getClass().getName());
			event.setShowingToUser(false);
		}


		eventService.publish(event);
		
	}

	private void beginingMessageArrived(String text) {
		InboundEvent textReceivedEvent = new InboundEvent(this, "0", text);
		sendEvent(textReceivedEvent);
	}

	private void messageArrived(String text) {
		synchronized (this) {
			logCommunication("raw in: " + text);

			publishInboundEvents(text);
		}
	}

	private void addParser(InboundEventParser parser) {
		if (parser.isComposite()) {
			compositeParsers.add(parser);
		} else if (parser.isChatEvent()) {
			chatParsers.add(parser);
		} else {
			nonCompositeNonChatParsers.add(parser);
		}
	}

	private void initParsers() {
		addParser(new IllegalMoveEventParser(this));
		addParser(new GameStartEventParser(this));
		addParser(new Style12EventParser(this));
		addParser(new DroppableHoldingsChangedEventParser(this));
		addParser(new GameEndEventParser(this));
		addParser(new GameEndEventParser(this, true));
		addParser(new RemovingObservedGameEventParser(this));
		// add one for board2.
		addParser(new RemovingObservedGameEventParser(this, true));
		// addParser(new AvailableBugTeamsEventParser(this));
		// addParser(new AvailInfoEventParser(this));
		addParser(new BugClosedEventParser(this));
		// addParser(new BugGamesInProgressEventParser(this));
		addParser(new BugOpenEventParser(this));
		addParser(new ChallengeEventParser(this));
		addParser(new ChannelTellEventParser(this));
		addParser(new ClosedEventParser(this));
		addParser(new CShoutEventParser(this));

		addParser(new ObservingGameStartEventParser(this));
		// addParser(new GInfoEventParser(this));
		addParser(new KibitzEventParser(this));
		addParser(new NotificationEventParser(this));
		addParser(new OpenEventParser(this));
		addParser(new PartnershipCreatedEventParser(this));
		addParser(new PartnershipEndedEventParser(this));
		addParser(new PartnerTellEventParser(this));
		addParser(new ShoutEventParser(this));
		// Removed there is a bug if you type sought all
		// addParser(new SoughtEventParser(this));
		addParser(new TellEventParser(this));
		addParser(new UnavailInfoEventParser(this));
		addParser(new VariablesEventParser(this));
		addParser(new WhisperEventParser(this));
		// add the GameEndEventParser for bughouse board 2

		userNameChangedParser = new UserNameChangedEventParser(this);
	}

	private void publishInboundEvents(String text) {

		// LOGGER.debug("Received text block: " + text);
		// long startTime = System.currentTimeMillis();
		try {
			if (text.length() < 1500) {

				text = StringUtility
						.replaceStringWithString(text, "\n\r", "\n");
				// logger.logMessage(text);
				// first see if its a chat event.
				boolean isHandled = false;

				// handle chat events first to avoid someone chatting text that
				// will
				// parse as another event.
				for (Iterator i = chatParsers.iterator(); !isHandled
						&& i.hasNext();) {
					InboundEvent e = ((InboundEventParser) i.next())
							.parse(text);
					if (e != null) {
						isHandled = true;
						sendEvent(e);
					}
				}

				for (Iterator i = nonCompositeNonChatParsers.iterator(); !isHandled
						&& i.hasNext();) {
					InboundEvent e = ((InboundEventParser) i.next())
							.parse(text);
					if (e != null) {
						isHandled = true;
						sendEvent(e);
					}
				}

				// handle composites
				if (!isHandled) {
					for (Iterator i = compositeParsers.iterator(); i.hasNext();) {
						InboundEvent e = ((InboundEventParser) i.next())
								.parse(text);
						if (e != null) {
							isHandled = true;
							sendEvent(e);
						}
					}
				}

				if (!isHandled) {
					isHandled = true;
					sendEvent(new InboundEvent(this, "" + inboundEventId++,
							text));
				}
			} else {
				sendEvent(new InboundEvent(this, "" + inboundEventId++, text));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Error occured parsing InboundEvent. "
					+ "This is a bug that needs to be fixed. "
					+ "Offending text=\n" + text, e);
			throw new RuntimeException("Error publishing inbound event:", e);
		} finally {
			// if (LOGGER.isDebugEnabled()) {
			// LOGGER.debug("parse time"
			// + (System.currentTimeMillis() - startTime));
			// }
		}
	}

	private void connectionLost() {
		eventService.publish(new DisconnectedEvent(this, ""
				+ disconnectedEventId++, "Connection " + url + ":" + port
				+ " has been lost.", user, url, port));
	}

	private class LoginHandler implements Runnable {
		private String userName;

		private String password;

		public boolean isLoggedIn = false;

		public LoginHandler(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}

		public boolean isLoggedIn() {
			return isLoggedIn;
		}

		public void run() {
			StringBuffer buffer = new StringBuffer(READ_BUFFER_SIZE);
			boolean hasSeenLoginPrompt = false;
			try {
				// handle log in messages
				while (true) {
					char currentChar = (char) bufferedInStream.read();
					if (currentChar == 65535) {
						break;
					} else {
						buffer.append(currentChar);
					}
					if (buffer.length() >= LOGIN_PROMPT.length()
							&& buffer.substring(
									buffer.length() - LOGIN_PROMPT.length())
									.equals(LOGIN_PROMPT)) {
						if (hasSeenLoginPrompt) {
							break;
						}
						if (userName != null && !userName.equals("")) {
							sendNonBlockedMessage(userName);

							if (isConnectingAsGuest) {
								sendNonBlockedMessage("\r\n");
							}
							beginingMessageArrived(buffer.toString());
							buffer = new StringBuffer(READ_BUFFER_SIZE);
							hasSeenLoginPrompt = true;
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
						// || buffer.charAt(buffer.length() - 1) == '\r') {
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
			long start = System.currentTimeMillis();
			int chars = 0;
			try {
				try {
					boolean ignoreWhiteSpace = false;
					// handle all other messages
					do {
						char currentChar = (char) bufferedInStream.read();

						if (lastMessageSentMillis != -1) {
							EventService.getInstance().publish(
									new ResponseTimeEvent(System
											.currentTimeMillis()
											- lastMessageSentMillis));
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

						if (buffer.length() >= PROMPT.length()) {
							if (endsWithPrompt(buffer)) {
								trimPrompt(buffer);
								trimTail(buffer);
								messageArrived(buffer.toString());
								// if (LOGGER.isDebugEnabled()) {
								// LOGGER
								// .debug("Read in command "
								// + (System
								// .currentTimeMillis() - start) + " chars=" +
								// chars);
								// }
								buffer.delete(0, buffer.length());
								ignoreWhiteSpace = true;
								start = System.currentTimeMillis();
								chars = 0;
							} else if (buffer.length() == buffer.capacity()) {
								sendEvent(new InboundEvent(null, null, buffer
										.toString(), true));
								buffer.delete(0, buffer.length());
								start = System.currentTimeMillis();
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