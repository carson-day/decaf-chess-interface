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

import decaf.com.inboundevent.game.GameTypes;

/**
 * @author carsonday
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ParserUtil {

	/**
	 * Returns the game type constant for the specified identifier.
	 * 
	 */
	public static int identifierToGameType(String identifier) {
		int result = -1;

		if (identifier.indexOf(SUICIDE_IDENTIFIER) != -1)
			result = GameTypes.SUICIDE;
		else if (identifier.indexOf(BUGHOUSE_IDENTIFIER) != -1)
			result = GameTypes.BUGHOUSE;
		else if (identifier.indexOf(CRAZYHOUSE_IDENTIFIER) != -1)
			result = GameTypes.CRAZYHOUSE;
		else if (identifier.indexOf(STANDARD_IDENTIFIER) != -1)
			result = GameTypes.STANDARD;
		else if (identifier.indexOf(WILD_IDENTIFIER) != -1)
			result = GameTypes.WILD;
		else if (identifier.indexOf(LIGHTNING_IDENTIFIER) != -1)
			result = GameTypes.LIGHTNING;
		else if (identifier.indexOf(BLITZ_IDENTIFIER) != -1)
			result = GameTypes.BLITZ;
		else if (identifier.indexOf(ATOMIC_IDENTIFIER) != -1)
			result = GameTypes.ATOMIC;
		else if (identifier.indexOf(LOSERS_IDENTIFIER) != -1)
			result = GameTypes.LOSERS;
		else if (identifier.indexOf(UNTIMED_IDENTIFIER) != -1)
			result = GameTypes.UNTIMED;

		else
			throw new IllegalArgumentException("Unknown identifier "
					+ identifier + " encountered. Please contact cday "
					+ "and get him to add this new game.");

		return result;
	}

	public static int removeRatingDecorators(String rating) {
		String ratingWithoutDecorators = "";

		for (int i = 0; i < rating.length(); i++) {
			if (Character.isDigit(rating.charAt(i))) {
				ratingWithoutDecorators += rating.charAt(i);
			}
		}
		return Integer.parseInt(ratingWithoutDecorators);
	}

	public static String removeTitles(String playerName) {
		StringTokenizer stringtokenizer = new StringTokenizer(playerName,
				"()~!@#$%^&*_+|}{';/., :[]");
		if (stringtokenizer.hasMoreTokens())
			return stringtokenizer.nextToken();
		else
			return playerName;
	}

	private static final String BLITZ_IDENTIFIER = "blitz";

	private static final String LIGHTNING_IDENTIFIER = "lightning";

	private static final String WILD_IDENTIFIER = "wild";

	private static final String STANDARD_IDENTIFIER = "standard";

	private static final String SUICIDE_IDENTIFIER = "suicide";

	private static final String ATOMIC_IDENTIFIER = "atomic";

	private static final String BUGHOUSE_IDENTIFIER = "bughouse";

	private static final String LOSERS_IDENTIFIER = "losers";

	private static final String CRAZYHOUSE_IDENTIFIER = "crazyhouse";

	private static final String UNTIMED_IDENTIFIER = "untimed";
}