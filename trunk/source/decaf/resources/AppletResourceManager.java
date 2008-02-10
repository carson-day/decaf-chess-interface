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
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

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

public class AppletResourceManager implements ResourceManager {
	Logger LOGGER = Logger.getLogger(ResourceManager.class);

	private PropertiesManager manager = new PropertiesManager();

	public AppletResourceManager() {
	}

	public URL getUrl(String file) {
		return getClass().getResource("/" + file);
	}

	public Image getImage(String imageName) {
		try {
			return ImageIO
					.read(getClass().getResourceAsStream("/" + imageName));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public int getInt(String bundleName, String resourceKey) {
		return manager.getInt(bundleName, resourceKey);
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
		return loadDefaultPreferences();
	}

	public void savePerferences(Preferences preferences) {
	}

	public String[] getBackgroundNames() {
		return new String[] { "MapleCherry" };
	}

	public String[] getChessSetNames() {
		return new String[] { "BOOK", "MONO", "WCN" };
	}

	public File getDecafUserHome() {
		throw new UnsupportedOperationException();
	}

	public boolean isApplet() {
		return true;
	}

	private class PropertiesManager {
		HashMap<String, Properties> bundleNameToProperties = new HashMap<String, Properties>();

		public PropertiesManager() {

		}

		private Properties getProperties(String bundleName) {
			Properties result = bundleNameToProperties.get(bundleName);

			if (result == null) {
				result = new Properties();
				boolean isLoaded = false;

				try {
					result.load(getClass().getResourceAsStream(
							"/properties/" + bundleName + ".properties"));
					isLoaded = true;

				} catch (IOException ioe) {
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
