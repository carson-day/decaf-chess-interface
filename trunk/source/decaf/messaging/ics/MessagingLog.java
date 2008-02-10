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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.messaging.inboundevent.IcsInboundEvent;
import decaf.messaging.inboundevent.chat.ChannelTellEvent;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.chat.TellEvent;
import decaf.resources.ResourceManagerFactory;
import decaf.thread.ThreadManager;

/**
 * A thread safe message logger.
 */
public class MessagingLog implements Preferenceable {
	Logger LOGGER = Logger.getLogger(MessagingLog.class);

	private Preferences preferences;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");

	private boolean isLogsCreated;

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public void log(final IcsInboundEvent event) {
		if (!ResourceManagerFactory.getManager().isApplet()
				&& preferences.getLoggingPreferences().isLoggingEnabled()) {
			ThreadManager.execute(new Runnable() {
				public void run() {
					if ((event instanceof IcsNonGameEvent)) {
						String date = getDate();

						if (event instanceof TellEvent
								&& preferences.getLoggingPreferences()
										.isLoggingPersonalTells()) {
							TellEvent tellEvent = (TellEvent) event;
							appendToFile(tellEvent.getSender(), date, tellEvent
									.getText());
						} else if (event instanceof ChannelTellEvent
								&& preferences.getLoggingPreferences()
										.isLoggingChannels()) {
							ChannelTellEvent channelTellEvent = (ChannelTellEvent) event;
							appendToFile("Channel"
									+ channelTellEvent.getChannel(), date,
									channelTellEvent.getText());
						}

						if (preferences.getLoggingPreferences()
								.isLoggingConsole()) {
							IcsNonGameEvent nonGameEvent = (IcsNonGameEvent) event;
							appendToFile("Console", date, nonGameEvent
									.getText());
						}
					}

				}
			});
		}
	}

	public void logOutbound(final String msg) {
		if (!ResourceManagerFactory.getManager().isApplet()
				&& preferences.getLoggingPreferences().isLoggingEnabled()) {
			ThreadManager.execute(new Runnable() {
				public void run() {

					if (msg != null && !msg.equals("")) {
						String date = getDate();
						if (preferences.getLoggingPreferences()
								.isLoggingConsole()) {
							appendToFile("Console", date, msg);
						}

						String[] words = msg.split(" ");

						if (words.length > 2 && words[0].equals("t")
								|| words[0].equals("tell")) {
							try {
								Integer.parseInt(words[1]);
								// ignore channel tells.
							} catch (NumberFormatException nfe) {
								// capture direct tells.
								appendToFile(words[1], date, msg);
							}
						}
					}
				}
			});
		}
	}

	private void createLogsDirectory() {
		if (!isLogsCreated) {
			File file = new File(ResourceManagerFactory.getManager()
					.getDecafUserHome(), "logs");
			file.mkdir();
			isLogsCreated = true;
		}
	}

	private synchronized void appendToFile(String fileName, String timestamp,
			String message) {
		try {
			createLogsDirectory();
			File file = new File(ResourceManagerFactory.getManager()
					.getDecafUserHome()
					+ "/logs", fileName + ".txt");
			if (file.length() >= preferences.getLoggingPreferences()
					.getMaxFileSize()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("purging 400 lines from file " + file);
				}

				// Remove first 1k lines from the file and rewrite it.
				File newFile = new File(ResourceManagerFactory.getManager()
						.getDecafUserHome()
						+ "/logs", "tmp" + timestamp);
				BufferedReader reader = new BufferedReader(new FileReader(file));
				PrintWriter writer = new PrintWriter(new FileWriter(file));

				int counter = 0;
				String line = reader.readLine();

				while (line != null) {
					if (++counter > 400) {
						writer.println(line);
					}
					line = reader.readLine();
				}

				writer.println("[" + timestamp + "] " + message);
				reader.close();
				writer.flush();
				writer.close();
				file.delete();
				newFile.renameTo(file);
			} else {
				PrintWriter printOut = new PrintWriter(new FileWriter(file,
						true));
				printOut.println("[" + timestamp + "] " + message);
				printOut.close();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private String getDate() {
		return DATE_FORMAT.format(new Date());
	}
}
