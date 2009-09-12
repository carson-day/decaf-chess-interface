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
import decaf.messaging.inboundevent.chat.KibitzEvent;

public class KibitzEventParser extends NonGameEventParser {
	public KibitzEventParser(int icsId) {
		super(icsId);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	@Override
	public IcsNonGameEvent parse(String text) {
		if (text.length() < 1500) {
			StringTokenizer stringtokenizer = new StringTokenizer(text, " ");
			if (stringtokenizer.hasMoreTokens()) {
				String s1 = stringtokenizer.nextToken();
				if (stringtokenizer.hasMoreTokens()) {
					String s2 = stringtokenizer.nextToken();
					if (s2.equals("kibitzes:")) {
						int i = text.indexOf("kibitzes:");
						int j = text.indexOf("[") + 1;
						int k = text.indexOf("]");
						try {
							return new KibitzEvent(getIcsId(), text, text
									.substring(i + "kibitzes:".length()),
									ParserUtil.removeTitles(s1), Integer
											.parseInt(text.substring(j, k)));
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
				}
			}
			return null;
		}
		return null;
	}
}