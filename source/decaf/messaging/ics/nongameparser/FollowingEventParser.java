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

import java.util.StringTokenizer;

import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.inform.BugClosedEvent;
import decaf.messaging.inboundevent.inform.FollowingEvent;
import decaf.messaging.inboundevent.inform.NotFollowingEvent;

public class FollowingEventParser extends NonGameEventParser {

	public FollowingEventParser(int icsId) {
		super(icsId);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public IcsNonGameEvent parse(String text) {
		if (text.length() < 200) {
			int identifierIndex = text.indexOf(IDENTIFIER);
			if (identifierIndex != -1) {
				StringTokenizer tok = new StringTokenizer(text.substring(
						identifierIndex + IDENTIFIER.length(), text.length()),
						" '(");
				return new FollowingEvent(getIcsId(), tok.nextToken(), text);
			} else {
				if (text.indexOf(IDENTIFIER2) != -1) {
					return new NotFollowingEvent(getIcsId(), text);
				} else {
					return null;
				}
			}

		} else
			return null;
	}

	// You will now be following pindik's games.
	private static final String IDENTIFIER = "You will now be following";

	private static final String IDENTIFIER2 = "You will not follow any player's games.";

}