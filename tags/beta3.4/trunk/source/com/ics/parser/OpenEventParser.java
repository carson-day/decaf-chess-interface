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

package decaf.com.ics.parser;

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.inform.OpenEvent;

public class OpenEventParser extends InboundEventParser {
	public OpenEventParser(Object source) {
		super(source, false, false);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		if (text.length() < 100) {
			if (text.indexOf(IDENTIFIER) != -1)
				return new OpenEvent(getSource(), "" + id++, text);
			else
				return null;
		}
		return null;
	}

	private static final String IDENTIFIER = "You are now open to receive match requests.";

	private static int id;
}