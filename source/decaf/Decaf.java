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
package decaf;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import decaf.dialog.LoginPanel;
import decaf.event.EventService;
import decaf.gui.GUIManager;
import decaf.gui.User;
import decaf.gui.pref.Preferences;
import decaf.messaging.ics.ICSCommunicationsDriver;
import decaf.resources.AppResourceManager;
import decaf.resources.ResourceManagerFactory;
import decaf.thread.ThreadManager;
import decaf.util.PropertiesConstants;

public class Decaf {
	private static final Logger LOGGER = Logger.getLogger(Decaf.class);

	private boolean userClickedLogin = false;

	public Decaf() {
		ResourceManagerFactory.init(new AppResourceManager());
		
		//Added to fix some lockup problems between swing dnd and awt dnd in ChessBoardSquare.
		System.setProperty("suppressSwingDropSupport","true");
		
		initLog4j();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing Decaffinate "
					+ ResourceManagerFactory.getManager().getString(
							PropertiesConstants.DECAF_PROPERTIES,
							PropertiesConstants.VERSION));
		}

		// force load of all singleton classes
		EventService.getInstance();
		User user = User.getInstance();
		final Preferences preferences = ResourceManagerFactory.getManager()
				.loadPreferences();

		String userName, password, server;
		int port;
		boolean isTimesealEnabled, isGuest;

		if (preferences.getLoginPreferences().isAutoLogin()) {
			userName = preferences.getLoginPreferences().getDefaultUserName();
			password = preferences.getLoginPreferences().getDefaultPassword();
			server = preferences.getLoginPreferences().getServer();
			port = preferences.getLoginPreferences().getServerPort();
			isGuest = preferences.getLoginPreferences().isDefaultGuestEnabled();
			isTimesealEnabled = preferences.getLoginPreferences()
					.isDefaultTimesealEnabled();
		} else {
			LoginPanel loginDialog = showLoginDialog(preferences);
			userName = loginDialog.getUserName();
			password = loginDialog.getPassword();
			server = loginDialog.getServer();
			port = loginDialog.getPort();
			isGuest = loginDialog.isLoggingInAsGuest();
			isTimesealEnabled = loginDialog.isTimesealEnabled();
		}
		GUIManager.getInstance().init(preferences);

		final String finalUserName = userName;
		final String finalPassowrd = password;
		final String finalServer = server;
		final int finalPort = port;
		final boolean finalIsGuest = isGuest;
		final boolean finalIsTimesealEnabled = isTimesealEnabled;

		// Ugly but break off a new thread to give the swing event thread a
		// chance to update.
		ThreadManager.execute(new Runnable() {
			public void run() {
				final ICSCommunicationsDriver driver = new ICSCommunicationsDriver(
						preferences);
				GUIManager.getInstance().setDriver(driver);

				try {
					if (finalIsGuest) {
						driver.connect(finalServer, finalPort, finalUserName,
								null, true, finalIsTimesealEnabled);
					} else {
						driver.connect(finalServer, finalPort, finalUserName,
								finalPassowrd, false, finalIsTimesealEnabled);
					}
				} catch (Exception e) {
					LOGGER.error(e);
					throw new RuntimeException("Communications dirver error:",
							e);
				}
				
				Runtime.getRuntime().addShutdownHook(new Thread () {
					public void run() {
						try
						{
							driver.disconnect();
						}
						catch (Exception e)
						{}
						
						Runtime.getRuntime().halt(0);
					}
				});
			}
		});
	}

	private LoginPanel showLoginDialog(Preferences preferences) {
		final JDialog dialog = new JDialog((Frame) null, "Decaf Login");
		dialog.setModal(true);
		LoginPanel panel = new LoginPanel(preferences);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(panel, BorderLayout.CENTER);
		dialog.setLocation(new Point(250, 150));

		panel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				userClickedLogin = true;
				dialog.setVisible(false);
			}
		});
		dialog.pack();
		dialog.setVisible(true);

		if (!userClickedLogin) {
			System.exit(1);
		}
		return panel;
	}

	public static void main(String[] args) throws Throwable {
		try {
			// in 1.6 all swing calls must be from the Swing Thread so:
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					Decaf caffeine = new Decaf();
				}
			});
		} catch (Throwable t) {
			t.printStackTrace();
			LOGGER.error(t);
			throw t;
		}
	}

	private void initLog4j() {
		FileInputStream in = null;
		try {
			Properties properties = new Properties();
			properties.load(in = new FileInputStream(
					"properties/log4j.properties"));
			PropertyConfigurator.configure(properties);
		} catch (IOException ioe) {
			try {
				Properties properties = new Properties();
				properties.load(in = new FileInputStream(new File(
						ResourceManagerFactory.getManager().getDecafUserHome()
								.getAbsolutePath()
								+ "/properties/log4j.properties")));
				PropertyConfigurator.configure(properties);
			} catch (IOException ioe2) {
				System.err.println("Error loading log4j.properties");
			}
		} finally {
			try {
				in.close();
			} catch (IOException ioe) {
			}
		}
	}
}