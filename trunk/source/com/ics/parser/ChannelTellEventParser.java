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
import decaf.com.inboundevent.chat.ChannelTellEvent;

public class ChannelTellEventParser extends InboundEventParser {
	public ChannelTellEventParser(Object source) {
		super(source, false, true);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		if (text.length() < 1500) {
			int i = text.indexOf("): ");
			if (i != -1) {
				StringTokenizer stringtokenizer = new StringTokenizer(text, ":");
				if (stringtokenizer.hasMoreTokens()) {
					String s1 = stringtokenizer.nextToken();
					int j = s1.lastIndexOf(")");
					int k = s1.lastIndexOf("(");
					if (k < j && k != -1 && j != -1)
						try {
							return new ChannelTellEvent(getSource(), "" + id++,
									text, Integer.parseInt(text.substring(
											k + 1, j)), ParserUtil
											.removeTitles(s1), text.substring(i
											+ "): ".length()));
						} catch (NumberFormatException numberformatexception) {
						}
				}
			}
			return null;
		}
		return null;
	}

	private static final String IDENTIFIER = "): ";

	private static int id;
}