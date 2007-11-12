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

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class XmlUtil {
	/**
	 * This method is awfully inefficient. It should only be used for testing or
	 * if an exception occurs and you need a log dump of an object.
	 */
	public static String toXml(Object object) {
		// TO DO: Investigate more efficient ways of performing this operation.
		// Perhapse using Jakarta commons.
		ByteArrayOutputStream byteOut = null;

		try {
			byteOut = new ByteArrayOutputStream(100000);
			XMLEncoder encoder = new XMLEncoder(byteOut);
			encoder.writeObject(object);
			encoder.flush();
			return new String(byteOut.toByteArray());
		} finally {
			try {
				byteOut.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}