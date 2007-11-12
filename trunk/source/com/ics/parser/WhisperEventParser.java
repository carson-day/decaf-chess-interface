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

import java.util.StringTokenizer;

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.chat.WhisperEvent;

public class WhisperEventParser extends InboundEventParser {
	public WhisperEventParser(Object source) {
		super(source, false, true);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		if (text.length() < 1500) {
			StringTokenizer stringtokenizer = new StringTokenizer(text, " ");
			if (stringtokenizer.hasMoreTokens()) {
				String s1 = stringtokenizer.nextToken();
				if (stringtokenizer.hasMoreTokens()) {
					String s2 = stringtokenizer.nextToken();
					if (s2.equals(IDENTIFIER)) {
						int i = text.indexOf(IDENTIFIER);
						int j = text.indexOf("[");
						int k = text.indexOf("]");
						return new WhisperEvent(getSource(), "" + id++, text,
								text.substring(i + IDENTIFIER.length()),
								ParserUtil.removeTitles(s1), Integer
										.parseInt(text.substring(j + 1, k)));
					}
				}
			}
			return null;
		}
		return null;
	}

	private static final String IDENTIFIER = "whispers:";

	private static int id;
}