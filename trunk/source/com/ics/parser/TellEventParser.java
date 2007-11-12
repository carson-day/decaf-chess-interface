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
import java.util.regex.Pattern;

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.chat.TellEvent;

public class TellEventParser extends InboundEventParser {
	public TellEventParser(Object source) {
		super(source, false, true);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		if (text.length() < 1500) {
			text = text.trim();
			StringTokenizer tok = new StringTokenizer(text, " \r\n");
			if (tok.hasMoreTokens()) {
				String s1 = tok.nextToken();
				if (tok.hasMoreTokens()) {
					String s2 = tok.nextToken();
					if (s2.equals("says:")) {
						int toldIndex = text.indexOf(TOLD);
						String message = text;
						if (toldIndex != -1) {
							message = text.substring(0, toldIndex);
						}
						return new TellEvent(getSource(), "" + id++, text,
								ParserUtil.removeTitles(s1), message);

					} else if (s2.equals("tells")) {
						if (tok.hasMoreTokens()) {
							String s3 = tok.nextToken();
							String message = text;
							int toldIndex = text.indexOf(TOLD);
							if (toldIndex != -1) {
								message = text.substring(0, toldIndex);
							}

							if (s3.equals("you:")) {
								return new TellEvent(getSource(), "" + id++,
										text, ParserUtil.removeTitles(s1),
										message);
							}
						}
					}
				}
			}
			return null;
		}
		return null;
	}

	public InboundEvent parseRegEx(String text) {
		CharSequence sequence = text.subSequence(0, text.length());

		if (TELLS_REGEX.matcher(sequence).matches()) {
			return new TellEvent(text, "" + id++, text, TELLS_FORM_REGEX
					.split(sequence)[0], TELLS_MESSAGE_REGEX.split(sequence)[1]);
		}
		return null;
	}

	private static final Pattern TELLS_REGEX = Pattern
			.compile("\\D* tells you: .*");

	private static final Pattern TELLS_FORM_REGEX = Pattern
			.compile(" tells you: .*");

	private static final Pattern TELLS_MESSAGE_REGEX = Pattern
			.compile("\\D* tells you: ");

	private static final String IDENTIFIER = "tells you: ";

	private static final String TOLD = "\n(told";

	private static final String IDENTIFIER_2 = "says: ";

	private static int id;
}