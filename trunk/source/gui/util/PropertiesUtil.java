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

import java.util.ResourceBundle;

/**
 * 
 */
public class PropertiesUtil {
	private static final ResourceBundle chessProperties = ResourceBundle
			.getBundle("decaf.moveengine.moveengine");

	private PropertiesUtil() {
	}

	public static Object getInstanceOf(String key) {
		Object result = null;
		String resultTemp = chessProperties.getString(key);

		if (resultTemp == null || resultTemp.trim().equals("")) {
			throw new IllegalStateException("Could not find property " + key);
		}

		try {
			result = Class.forName(resultTemp).newInstance();
		} catch (ClassNotFoundException cnfe) {
			throw new IllegalStateException("Could not find class " + key);
		} catch (InstantiationException ie) {
			throw new IllegalStateException(
					"Error instantiationg default instance of " + key);
		} catch (IllegalAccessException iae) {
			throw new IllegalStateException(
					"Default constructor is not public in class " + key);
		}
		return result;
	}

	public static boolean getBoolean(String key) {
		boolean result = false;
		String resultTemp = chessProperties.getString(key);

		if (resultTemp == null || resultTemp.trim().equals("")) {
			throw new IllegalStateException("Could not find property " + key);
		}

		if (resultTemp.equalsIgnoreCase("true")) {
			result = true;
		} else if (resultTemp.equalsIgnoreCase("false")) {
			result = false;
		} else {
			throw new IllegalStateException("Invalid property encountered "
					+ key + " = " + resultTemp);
		}
		return result;
	}
}