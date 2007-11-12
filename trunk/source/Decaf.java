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
package decaf;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import decaf.com.ics.ICSCommunicationsDriver;
import decaf.event.EventService;
import decaf.gui.GUIManager;
import decaf.gui.LoginDialog;
import decaf.gui.pref.Preferences;
import decaf.gui.util.PropertiesConstants;
import decaf.gui.util.PropertiesManager;
import decaf.gui.util.User;

public class Decaf {
	private static final Logger LOGGER = Logger.getLogger(Decaf.class);

	public Decaf() {
		initLog4j();
		LOGGER.info("Executing Decaffinate "
				+ PropertiesManager.getInstance().getString(
						PropertiesConstants.DECAF_PROPERTIES,
						PropertiesConstants.VERSION));
		// force load of all singleton classes
		User user = User.getInstance();
		EventService eventService = EventService.getInstance();
		Preferences preferences = Preferences.loadPreferences();
		LoginDialog loginDialog = new LoginDialog(preferences);
		LOGGER.info("Initializing GUI Manager.");
		GUIManager.getInstance().init(preferences);
		LOGGER.info("Initializing fics driver.");
		ICSCommunicationsDriver driver = new ICSCommunicationsDriver(
				preferences);
		LOGGER.info("Created driver.");
		try {
			if (loginDialog.isLoggingInAsGuestWithoutHandle()
					|| loginDialog.isLoggingInAsGuest()) {
				driver.connect(loginDialog.getServer(), loginDialog.getPort(),
						loginDialog.getUserName(), loginDialog
								.isLoggingInAsGuest() ? "g" : loginDialog
								.getPassword(), true, loginDialog
								.isTimesealEnabled());
			} else {
				driver.connect(loginDialog.getServer(), loginDialog.getPort(),
						loginDialog.getUserName(), loginDialog
								.isLoggingInAsGuest() ? "GUEST" : loginDialog
								.getPassword(), false, loginDialog
								.isTimesealEnabled());
			}
		} catch (Exception e) {
			throw new RuntimeException("Communications dirver error:", e);
		}
	}

	public static void main(String[] args) throws Throwable {
		try {
			Decaf caffeine = new Decaf();
		} catch (Throwable t) {
			t.printStackTrace();
			LOGGER.error(t);
			throw t;
		}
	}
	
	private void initLog4j()
	{
		FileInputStream in = null;
		try
		{
			Properties properties = new Properties();
			properties.load(in = new FileInputStream ("properties/log4j.properties"));
			PropertyConfigurator.configure(properties);
		}
		catch (IOException ioe)
		{
			System.err.println("Error loading log4j.properties");
		}
		finally
		{
			try
			{
				in.close();
			}
			catch(IOException ioe)
			{}
		}
	}
}