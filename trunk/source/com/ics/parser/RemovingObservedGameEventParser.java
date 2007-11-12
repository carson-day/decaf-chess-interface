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
import decaf.com.inboundevent.game.RemovingObservedGameEvent;

public class RemovingObservedGameEventParser extends InboundEventParser {
	private boolean isBoard2;

	public RemovingObservedGameEventParser(Object source) {
		this(source, false);
	}

	/**
	 * This constructor looks only for the second RemovingObservedGameEvent in
	 * the text it parses. It is intended for bughouse games.
	 */
	public RemovingObservedGameEventParser(Object source, boolean isBoard2) {
		super(source, true, false);
		this.isBoard2 = isBoard2;
	}

	/** Removing game 99 from observation list. */

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {

		if (text.length() < 600) {
			if (isBoard2) {
				int firstIndex = text.indexOf(IDENTIFIER);
				int lastIndex = text.lastIndexOf(IDENTIFIER);
				if (lastIndex != -1 && lastIndex > firstIndex) {
					text = text.substring(lastIndex);
				} else {
					return null;
				}
			}
			if (text.trim().indexOf(IDENTIFIER) == 0) {
				StringTokenizer tokenizer = new StringTokenizer(text, " \n");
				tokenizer.nextToken();
				tokenizer.nextToken();
				return new RemovingObservedGameEvent(getSource(), "" + id++,
						text, Integer.parseInt(tokenizer.nextToken()));
			} else {
				return null;
			}
		}
		return null;
	}

	private static final String IDENTIFIER = "Removing game ";

	private static int id;
}