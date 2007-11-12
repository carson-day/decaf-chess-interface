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
import decaf.com.inboundevent.chat.PartnerTellEvent;

public class PartnerTellEventParser extends InboundEventParser {
	public PartnerTellEventParser(Object source) {
		super(source, false, true);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		if (text.length() < 1500) {
			int i = text.indexOf(PTELL_IDENTIFIER);
			if (i != -1) {
				StringTokenizer stringtokenizer = new StringTokenizer(text, " ");
				String s1 = stringtokenizer.nextToken();
				if (stringtokenizer.nextToken().equals("(your"))
					return new PartnerTellEvent(getSource(), "" + id++, text,
							ParserUtil.removeTitles(s1), text.substring(i
									+ PTELL_IDENTIFIER.length(), text.length()));
			}
			return null;
		}
		return null;
	}

	private static final String PTELL_IDENTIFIER = "(your partner) tells you: ";

	private static int id;
}