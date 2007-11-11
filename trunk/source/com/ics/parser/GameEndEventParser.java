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
import decaf.com.inboundevent.game.GameEndEvent;

public class GameEndEventParser extends InboundEventParser {
	public GameEndEventParser(Object source) {
		this(source, false);
	}

	/**
	 * @param isBughouseGame2Parser
	 *            True if this GameEndEventParser is just parsing board2
	 */
	public GameEndEventParser(Object source, boolean isBughouseGame2Parser) {
		super(source, true, false);

		this.isBughouseGame2Parser = isBughouseGame2Parser;
	}

	/** {Game 85 (Pulmannen vs. chineseremainder) Pulmannen's partner won} 1-0 */

	/**
	 * {Game 128 (RubberDog vs. PoindexterIII) PoindexterIII checkmated} 1-0
	 * 
	 * {Game 91 (CDay vs. dolaphin) dolaphin's partner won} 0-1
	 * 
	 * Bughouse rating adjustment: 1929 --> 1924 Bughouse rank: 121/373 Bughouse
	 * hrank: 120/369
	 */
	/**
	 * BUGHOUSE GAME END. {Game 69 (GuestSJFB vs. guestcday) GuestSJFB resigns}
	 * 0-1 No ratings adjustment done.
	 * 
	 * {Game 71 (GuestVBXH vs. GuestVGYV) GuestVBXH's partner won} 1-0
	 * 
	 * 
	 * {Game 21 (cdaysgoat vs. cday) Game aborted by mutual agreement} *
	 */
	/**
	 * Game 112: Your opponent, adventuretwo, has lost contact or quit.
	 * 
	 * {Game 112 (cday vs. adventuretwo) adventuretwo forfeits by disconnection}
	 * 1-0
	 */
	/*
	 * Your opponent has aborted the game on move one.
	 * 
	 * {Game 50 (guestbiteme vs. cday) Game aborted on move 1} *
	 */

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {

		if (text.length() > 600) {
			return null;
		}
		// make sure this is not a game start event.
		if (text.indexOf(GAME_START_ID) != -1) {
			return null;
		}

		text = text.trim();
		int prefixId = text.indexOf(PREFIX_IDENTIFIER);
		if (prefixId != -1 && text.indexOf("}") != -1) {
			text = text.substring(prefixId, text.length());
			int textIndex = 0;

			if (isBughouseGame2Parser && textIndex != -1) {
				int secondIndex = text.lastIndexOf(PREFIX_IDENTIFIER);
				if (secondIndex == textIndex) {
					textIndex = -1;
				} else {
					textIndex = secondIndex;
				}
			}

			if (textIndex != -1) {
				text = text.substring(textIndex);

				StringTokenizer tok = new StringTokenizer(text, " ()");

				// parse past {Game
				tok.nextToken();

				int gameId = Integer.parseInt(tok.nextToken());

				String whiteName = tok.nextToken();

				// parse past vs.
				tok.nextToken();

				String blackName = tok.nextToken();

				// find description. Its between ) and }
				int closingParenIndex = text.indexOf(")");
				int closingBraceIndex = text.indexOf("}");

				if (closingParenIndex == -1 || closingBraceIndex == -1) {
					throw new IllegalArgumentException(
							"Could not find description in gameEndEvent:"
									+ text);
				}
				String description = text.substring(closingParenIndex + 1,
						closingBraceIndex).trim();

				String afterClosingBrace = text.substring(
						closingBraceIndex + 1, text.length()).trim();

				int score = -1;

				if (description.indexOf("aborted") != -1)
					score = GameEndEvent.ABORTED;
				else if (description.indexOf("adjourned") != -1)
					score = GameEndEvent.ADJOURNED;
				else if (description.indexOf('*') != -1)
					score = GameEndEvent.UNDETERMINED;
				else if (afterClosingBrace.startsWith("0-1"))
					score = GameEndEvent.BLACK_WON;
				else if (afterClosingBrace.startsWith("1-0"))
					score = GameEndEvent.WHITE_WON;
				else
					score = GameEndEvent.DRAW;

				// look for rating adjustment.
				// int ratingAdjustmentIndex = text.indexOf(RATING_ADJUSTMENT);
				/**
				 * Bughouse rating adjustment: 1929 --> 1924 Bughouse rank:
				 * 121/373 Bughouse hrank: 120/369
				 */
				/*
				 * if (!isBughouseGame2Parser && ratingAdjustmentIndex != -1) {
				 * 
				 * StringTokenizer ratingAdjustmentTok = new
				 * StringTokenizer(text.substring(ratingAdjustmentIndex),"\n
				 * ->/"); ratingAdjustmentTok.nextToken(); //rating
				 * ratingAdjustmentTok.nextToken(); //adjustment: int oldRating =
				 * Integer.parseInt(ratingAdjustmentTok.nextToken()); int
				 * newRating =
				 * Integer.parseInt(ratingAdjustmentTok.nextToken());
				 * ratingAdjustmentTok.nextToken(); //type
				 * ratingAdjustmentTok.nextToken(); //rank:
				 * 
				 * int rank = Integer.parseInt(tok.nextToken());
				 * 
				 * ratingAdjustmentTok.nextToken(); // /xxx
				 * ratingAdjustmentTok.nextToken(); // type
				 * ratingAdjustmentTok.nextToken(); // hrank: int hrank =
				 * Integer.parseInt(tok.nextToken());
				 * 
				 * 
				 * return new GameEndEvent(getSource(), "" + id++, text.trim(),
				 * gameId, whiteName, blackName, description, score, newRating,
				 * oldRating, rank, hrank); } else {
				 */
				return new GameEndEvent(getSource(), "" + id++,
						isBughouseGame2Parser ? "" : text.trim(), gameId,
						whiteName, blackName, description, score);
				// }
			}
		}
		return null;
	}

	private static final String PREFIX_IDENTIFIER = "{Game ";

	private static final String INNER_IDENTIFIER = "\n\r{Game ";

	private static final String RATING_ADJUSTMENT = "rating adjustment:";

	private static final String GAME_START_ID = "Creating: ";

	private boolean isBughouseGame2Parser;

	private static int id;
}