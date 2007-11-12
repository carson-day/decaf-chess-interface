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
package decaf.gui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertiesManager {
	Logger LOGGER = Logger.getLogger(PropertiesManager.class);

	private static final PropertiesManager singletonInstance = new PropertiesManager();

	HashMap<String, Properties> bundleNameToProperties = new HashMap<String, Properties>();

	File[] propertiesDirectories = null;

	public static PropertiesManager getInstance() {
		return singletonInstance;
	}

	private PropertiesManager() {

		String overrideDirs = System.getProperty(
				"decaf.gui.util.PropertiesManager.propertiesDirs", null);
		if (overrideDirs == null) {
			propertiesDirectories = new File[] { new File("./properties") };
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

	private void save(String bundleName, String header, Properties properties) {
		try {
			properties.store(new FileOutputStream("./properties/" + bundleName
					+ ".properties", false), header);
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