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
import decaf.messaging.inboundevent.chat.ShoutEvent;

public class ShoutEventParser extends NonGameEventParser {
	public ShoutEventParser(int icsId) {
		super(icsId);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public IcsNonGameEvent parse(String text) {
		if (text.length() < 1500) {
			if (text.startsWith(SHOUT_1)) {
				StringTokenizer stringtokenizer = new StringTokenizer(text
						.substring("SHOUT_1".length()), " ");
				String s1 = ParserUtil
						.removeTitles(stringtokenizer.nextToken());
				return new ShoutEvent(getIcsId(), text, ParserUtil
						.removeTitles(s1));

			}
			StringTokenizer stringtokenizer1 = new StringTokenizer(text, " ");
			if (stringtokenizer1.hasMoreTokens()) {
				String s2 = stringtokenizer1.nextToken();
				if (stringtokenizer1.hasMoreTokens()) {
					String s3 = stringtokenizer1.nextToken();
					if (s3.equals(SHOUT_2))
						return new ShoutEvent(getIcsId(), text, ParserUtil
								.removeTitles(s2));
				}
			}
			return null;
		}
		return null;
	}

	private static final String SHOUT_1 = "-->";

	private static final String SHOUT_2 = "shouts:";
}