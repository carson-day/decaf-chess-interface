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

public class BugGamesInProgressEventParser extends InboundEventParser {
	public BugGamesInProgressEventParser(Object source) {
		super(source, true, false);
	}

	/**
	 * Usage: bugwho [g|p|u]
	 * 
	 * This command lists current bughouse games, partnerships and/or available
	 * partners. "Bugwho g" lists current bughouse games, paired by bughouse
	 * matches (different from the "games" command). "Bugwho p" lists current
	 * bughouse partnerships that are not playing bughouse games, paired by
	 * partnerships. "Bugwho u" lists users who are open for bughouse
	 * partnerships. Lastly, "bugwho" will show all three kinds of information.
	 * Here is a sample "bugwho" display:
	 * ______________________________________________________________________________
	 * 1 bughouse game in progress 15 2253 Ananke 2002 Dharma [ Br 3 0] 2:08 -
	 * 2:03 (35-36) B: 18 60 1670 Freud 1757 Jung [ Br 3 0] 1:37 - 2:27 (43-42)
	 * W: 17
	 * 
	 * 1 partnership not playing bughouse 2007^Confucious /.1675 Mencius
	 * 
	 * Unpartnered players with bugopen on
	 * 
	 * 1737^Kirk 1399 Spock
	 * ______________________________________________________________________________
	 * 
	 * Note that the games list has information similar to that of a "games"
	 * listing, and that the lists for partnerships and unpartnered players have
	 * codes similar to those of the "who" listing.
	 */

	/**
	 * 0 is game1d (Integer) 1 is white rating (String) 2 is white name (String)
	 * 3 is black rating (String) 4 is black name (String) 5 is time (Integer) 6
	 * is inc (Integer) 7 is isRated (Boolean)
	 */
	private static Object[] parseLine(String line) {
		Object[] result = new Object[8];

		StringTokenizer tok = new StringTokenizer(line.trim(), " []");
		result[0] = new Integer(tok.nextToken());
		result[1] = tok.nextToken();
		result[2] = tok.nextToken();
		result[3] = tok.nextToken();
		result[4] = tok.nextToken();
		String gameType = tok.nextToken();
		result[7] = new Boolean(gameType.indexOf("r") != -1);
		result[5] = new Integer(tok.nextToken());
		result[6] = new Integer(tok.nextToken());
		return result;
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		/*
		 * int beginIndex = text.indexOf(IDENTIFIER); if (beginIndex != -1) {
		 * text = text.substring(beginIndex, text.length()); StringTokenizer
		 * newLinedTokenizer = new StringTokenizer(text, "\r\n"); LinkedList
		 * list = new LinkedList(); newLinedTokenizer.nextToken();
		 * 
		 * while (newLinedTokenizer.hasMoreTokens()) { String line1 =
		 * newLinedTokenizer.nextToken();
		 * 
		 * if (line1.trim().equals("") || line1.indexOf(END_IDENTIFIER) != -1) {
		 * break; } Object[] game1Info = parseLine(line1); Object[] game2Info =
		 * parseLine(newLinedTokenizer.nextToken());
		 * 
		 * list.add(new BugGameInProgress(((Integer) game1Info[0]) .intValue(),
		 * ((Integer) game2Info[0]).intValue(), game1Info[2].toString(),
		 * game1Info[1].toString(), game1Info[4].toString(),
		 * game1Info[3].toString(), game2Info[2].toString(),
		 * game2Info[1].toString(), game2Info[4].toString(),
		 * game2Info[3].toString(), ((Boolean) game1Info[7]).booleanValue(),
		 * ((Integer) game1Info[5]).intValue(), ((Integer)
		 * game1Info[6]).intValue())); }
		 * 
		 * return new BugGamesInProgressEvent(getSource(), "" + id++, text,
		 * list); } else { return null; }
		 */
		return null;
	}

	private static final String IDENTIFIER = "Bughouse games in progress";

	private static final String END_IDENTIFIER = "displayed.";

	private static int id;
}