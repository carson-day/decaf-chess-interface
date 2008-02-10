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
package decaf.messaging.ics.nongameparser;

import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.inform.ClosedEvent;

public class ClosedEventParser extends NonGameEventParser {
	public ClosedEventParser(int icsId) {
		super(icsId);
	}

	public IcsNonGameEvent parse(String text) {
		if (text.length() < 100 && text.indexOf(IDENTIFIER) != -1)
			return new ClosedEvent(getIcsId(), text);
		else
			return null;

	}

	private static final String IDENTIFIER = "You are no longer receiving match requests.";
}