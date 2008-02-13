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
package decaf.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;

import decaf.resources.ResourceManagerFactory;

public class GUIUtil {
	private static String defaultFont;

	private GUIUtil() {
	}

	public static Component getGreatestParent(Component component) {
		Object obj;
		for (obj = component; ((Component) (obj)).getParent() != null; obj = ((Component) (obj))
				.getParent())
			;
		return ((Component) (obj));
	}

	public static Point getPoint(String bundle, String propertyName) {
		Point result = null;
		String property = ResourceManagerFactory.getManager().getString(bundle,
				propertyName);
		if (property == null) {
			throw new RuntimeException("Could not find " + bundle + "."
					+ property);
		}
		String[] ints = property.split(",");
		try {
			result = new Point(Integer.parseInt(ints[0]), Integer
					.parseInt(ints[1]));
		} catch (Exception e) {
			throw new RuntimeException("Error loading " + bundle + "."
					+ property, e);
		}
		return result;
	}

	public static Dimension getDimension(String bundle, String propertyName) {
		Dimension result = null;
		String property = ResourceManagerFactory.getManager().getString(bundle,
				propertyName);
		if (property == null) {
			throw new RuntimeException("Could not find " + bundle + "."
					+ property);
		}
		String[] ints = property.split(",");
		try {
			result = new Dimension(Integer.parseInt(ints[0]), Integer
					.parseInt(ints[1]));
		} catch (Exception e) {
			throw new RuntimeException("Error loading " + bundle + "."
					+ property, e);
		}
		return result;
	}

	public static String getDefaultFont() {
		if (defaultFont == null) {
			String font = ResourceManagerFactory.getManager().getString("os",
					"defaultFonts");
			String[] fonts = font.split(",");
			String[] fontNames = GraphicsEnvironment
					.getLocalGraphicsEnvironment()
					.getAvailableFontFamilyNames();

			for (int i = 0; defaultFont == null && i < fonts.length; i++) {
				for (int j = 0; defaultFont == null && j < fontNames.length; j++) {
					if (fontNames[j].equalsIgnoreCase(fonts[i])) {
						defaultFont = fontNames[j];
					}
				}
			}

			if (defaultFont == null) {
				defaultFont = "Monospaced";
			}
		}
		return defaultFont;
	}

}