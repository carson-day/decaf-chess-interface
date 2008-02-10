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
import decaf.messaging.inboundevent.inform.PartnershipCreatedEvent;

public class PartnershipCreatedEventParser extends NonGameEventParser {
	public PartnershipCreatedEventParser(int icsId) {
		super(icsId);
	}

	public IcsNonGameEvent parse(String text) {
		if (text.length() < 100) {
			int i = text.indexOf(IDENTIFIER);
			if (i != -1) {
				StringTokenizer stringtokenizer = new StringTokenizer(text
						.substring(i + "You agree to be".length(), text
								.length()), " '");
				return new PartnershipCreatedEvent(getIcsId(), text,
						stringtokenizer.nextToken());
			}
			int j = text.indexOf(IDENTIFIER_2);
			if (j != -1) {
				StringTokenizer stringtokenizer1 = new StringTokenizer(text,
						" ");
				String s1 = stringtokenizer1.nextToken();
				String s2 = stringtokenizer1.nextToken();
				if (s2.equals("agrees"))
					return new PartnershipCreatedEvent(getIcsId(), text, s1);
			}
			return null;
		}
		return null;

	}

	private static final String IDENTIFIER = "You agree to be";

	private static final String IDENTIFIER_2 = "agrees to be your partner.";
}