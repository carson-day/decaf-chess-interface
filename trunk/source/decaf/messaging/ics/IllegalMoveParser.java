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

import org.apache.log4j.Logger;

import decaf.messaging.inboundevent.game.IllegalMoveEvent;

public class IllegalMoveParser {
	private int icsId;

	private static final Logger LOGGER = Logger
			.getLogger(IllegalMoveParser.class);

	public IllegalMoveParser(int icsId) {
		this.icsId = icsId;
	}

	public IllegalMoveEvent parse(String illegalMoveLine) {
		LOGGER.debug(illegalMoveLine);
		String s1 = illegalMoveLine.substring(13);
		int j = s1.indexOf(")");
		return new IllegalMoveEvent(icsId, s1.substring(0, j));
	}

}
