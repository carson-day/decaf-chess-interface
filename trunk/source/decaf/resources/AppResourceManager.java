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
package decaf.resources;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.BughousePreferences;
import decaf.gui.pref.ChatPreferences;
import decaf.gui.pref.LoggingPreferences;
import decaf.gui.pref.LoginPreferences;
import decaf.gui.pref.Preferences;
import decaf.gui.pref.SeekGraphPreferences;
import decaf.gui.pref.SpeechPreferences;

public class AppResourceManager implements ResourceManager {
	Logger LOGGER = Logger.getLogger(ResourceManager.class);

	private static final String RESOURCES_DIR = "./Resources";

	private static final File DECAF_USER_HOME = new File(System
			.getProperty("user.home")
			+ "/.Decaf");

	static final String PREFERENCES_PATH = "preferences.obj";

	private PropertiesManager manager = new PropertiesManager();

	public URL getUrl(String file) {
		try {
			return new URL("file:Resources/" + file);
		} catch (MalformedURLException me) {
			throw new RuntimeException(me);
		}
	}

	public Image getImage(String imageName) {
		try {
			return ImageIO.read(new File(RESOURCES_DIR + "/" + imageName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getInt(String bundleName, String resourceKey) {
		return manager.getInt(bundleName, resourceKey);
	}

	public File getDecafUserHome() {
		return DECAF_USER_HOME;
	}

	public boolean isApplet() {
		return false;
	}

	public String getString(String bundleName, String resourceKey) {
		return manager.getString(bundleName, resourceKey);
	}

	public Preferences loadDefaultPreferences() {
		Preferences result = new Preferences();
		result.setLoggingPreferences(LoggingPreferences.getDefault());
		result.setBughousePreferences(BughousePreferences.getDefault());
		result.setChatPreferences(ChatPreferences.getDefault());
		result.setBoardPreferences(BoardPreferences.getDefault());
		result.setLoginPreferences(LoginPreferences.getDefault());
		result.setSpeechPreferences(SpeechPreferences.getDefault());
		result.setSeekGraphPreferences(SeekGraphPreferences.getDefault());

		result.setRememberChatLocation(result.getChatPreferences()
				.getChatWindowPoint());
		result.setRememberChatDimension(result.getChatPreferences()
				.getChatWindowDimension());
		result.setRememberBugEarLocation(result.getBughousePreferences()
				.getBugEarPoint());
		result.setRememberBugEarDimension(result.getBughousePreferences()
				.getBugEarDimension());
		result.setRememberBugLocation(result.getBughousePreferences()
				.getGameWindowPoint());
		result.setRememberBugDimension(result.getBughousePreferences()
				.getGameWindowDimension());
		result.setRememberBugSliderPosition(result.getBughousePreferences()
				.getBoardSplitterLocation());
		result.setRememberChessLocation(result.getBoardPreferences()
				.getGameWindowPoint());
		result.setRememberChessDimension(result.getBoardPreferences()
				.getGameWindowSize());
		
		result.setRememberBugSeekDimension(new Dimension(750,400));
		result.setRememberBugSeekLocation(new Point(0,0));
		result.setRememberSeekDimension(new Dimension(640, 480));
		result.setRememberSeekLocation(new Point(0,0));

		return result;
	}

	public Preferences loadPreferences() {
		Preferences result = null;
		try {
			createDecafDirectory();
			File file = new File(DECAF_USER_HOME, PREFERENCES_PATH);
			ObjectInputStream objectIn = new ObjectInputStream(
					new FileInputStream(file));
			result = (Preferences) objectIn.readObject();
			objectIn.close();
		} catch (Throwable ioe) {
			LOGGER.warn("Could not load preferences: " + PREFERENCES_PATH
					+ " Loading default preferences.");
			result = loadDefaultPreferences();
		}
		return result;
	}

	public void savePerferences(Preferences preferences) {
		try {
			createDecafDirectory();
			File file = new File(DECAF_USER_HOME, PREFERENCES_PATH);
			ObjectOutputStream objOut = new ObjectOutputStream(
					new FileOutputStream(file));
			objOut.writeObject(preferences);
			objOut.close();

		} catch (Exception ioe) {
			LOGGER.error("Error occured saving options: " + PREFERENCES_PATH,
					ioe);
			ioe.printStackTrace();
		}
	}

	private void createDecafDirectory() {
		DECAF_USER_HOME.mkdir();
	}

	public String[] getBackgroundNames() {
		List<String> result = new LinkedList<String>();

		File file = new File(RESOURCES_DIR);
		File[] files = file.listFiles(new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg1.startsWith("SQUARE.")
						&& arg1.indexOf("LIGHT") != -1;
			}

		});

		for (int i = 0; i < files.length; i++) {
			
			StringTokenizer tok = new StringTokenizer(files[i].getName(), ".");
		     tok.nextToken();
			String name = tok.nextToken();
			if (name.equalsIgnoreCase("CROP"))
			{
				name = tok.nextToken();
			}
			result.add(name);
		}
		return (String[]) result.toArray(new String[0]);
	}

	public String[] getChessSetNames() {
		List<String> result = new LinkedList<String>();

		File file = new File(RESOURCES_DIR);
		File[] files = file.listFiles(new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				return arg1.startsWith("SET.") && arg1.indexOf("WBISHOP") != -1;
			}

		});

		for (int i = 0; i < files.length; i++) {
			StringTokenizer tok = new StringTokenizer(files[i].getName(), ".");
			tok.nextToken();
			result.add(tok.nextToken());
		}
		return (String[]) result.toArray(new String[0]);
	}

	private class PropertiesManager {
		HashMap<String, Properties> bundleNameToProperties = new HashMap<String, Properties>();

		File[] propertiesDirectories = null;

		public PropertiesManager() {

			String overrideDirs = System.getProperty(
					"decaf.gui.util.PropertiesManager.propertiesDirs", null);
			if (overrideDirs == null) {
				// The properties directory in DECAF_USER_HOME is added so vista
				// users can change the settings.
				propertiesDirectories = new File[] {
						new File(DECAF_USER_HOME.getAbsolutePath()
								+ "/properties"), new File("./properties") };
			} else {
				String[] dirs = overrideDirs.split(",");
				propertiesDirectories = new File[dirs.length + 1];

				for (int i = 0; i < dirs.length; i++) {

					propertiesDirectories[i] = new File(dirs[i]);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Adding properties override directory: "
								+ dirs[i]);
					}
				}
				propertiesDirectories[dirs.length] = new File("properties");
			}

		}

		private void save(String bundleName, String header,
				Properties properties) {
			try {
				properties.store(new FileOutputStream("./properties/"
						+ bundleName + ".properties", false), header);
			} catch (IOException ioe) {
				throw new RuntimeException(ioe);
			}
		}

		private Properties getProperties(String bundleName) {
			Properties result = bundleNameToProperties.get(bundleName);

			if (result == null) {
				result = new Properties();
				boolean isLoaded = false;
				for (int i = 0; !isLoaded && i < propertiesDirectories.length; i++) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Searching for " + bundleName + " in :"
								+ propertiesDirectories[i].getAbsolutePath());
					}

					try {
						result.load(new FileInputStream(new File(
								propertiesDirectories[i], bundleName
										+ ".properties")));
						isLoaded = true;

					} catch (IOException ioe) {
					}
				}

				if (!isLoaded) {
					LOGGER.error("Could not find bundleName " + bundleName);
					throw new RuntimeException("Could not find bundleName "
							+ bundleName);
				}

				bundleNameToProperties.put(bundleName, result);
			}
			return result;
		}

		public String getString(String bundleName, String resourceKey) {

			try {
				return getProperties(bundleName).getProperty(resourceKey);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}

		public int getInt(String bundleName, String resourceKey) {
			try {
				return Integer.parseInt(getProperties(bundleName).getProperty(
						resourceKey));
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}

		}
	}
}
