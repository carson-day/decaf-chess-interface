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

import org.apache.log4j.Logger;

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.game.GameStartEvent;
import decaf.com.inboundevent.game.MoveEvent;

/**
 * 
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ObservingGameStartEventParser extends GameStartEventParser {
	private static final Logger LOGGER = Logger
			.getLogger(ObservingGameStartEventParser.class);

	public ObservingGameStartEventParser(Object source) {
		super(source);
	}

	/**
	 * You are now observing game 27. Game 27: Nusbaum (2118) MIHAILOP (2206)
	 * rated lightning 1 0 <12>r---r--- -p---p-k -n----pp p-pp----
	 * P--PnN--qPPQPBP- ------NP -----RK- W 2 0 0 0 0 0 27 Nusbaum MIHAILOP 0 1
	 * 0 30 32 31 28 22 P/c7-c5 (0:03) c5 0 1 0
	 * 
	 * You will no longer be following rln's games. You will now be following
	 * fetar's games. You are now observing game 171. Game 171: faraonfaraon
	 * (1440) fetar ( 989) rated bughouse 3 0
	 * 
	 * <12> rnbqkbnr pppppppp -------- -------- -------- -------- PPPPPPPP
	 * RNBQKBNR W -1 1 1 1 1 0 171 faraonfaraon fetar 0 3 0 39 39 180 180 1 none
	 * (0:00) none 0 1 0 <b1> game 171 white [] black []
	 * 
	 */

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		if (text.length() < 600) {
			String originalText = text;
			String gameStartText = null;
			int whoYouWereFollowingIndex = text
					.indexOf(WHOM_YOU_WERE_FOLLOWING);
			if (text.startsWith(NO_LONGER_FOLLOWING)
					|| text.startsWith(FOLLOWING)) {
				int gameStartTextIndex = text.indexOf(PREFIX_IDENTIFIER);

				if (gameStartTextIndex != -1) {
					gameStartText = text;
				} else {
					return null;
				}
			} else if (whoYouWereFollowingIndex > 0
					&& whoYouWereFollowingIndex < 50) {
				gameStartText = text;
			} else if (text.startsWith(PREFIX_IDENTIFIER)) {
				gameStartText = text;
			} else {
				return null;
			}
			int gameIndex = text.indexOf("Game ");
			gameStartText = gameStartText.substring(gameIndex, gameStartText
					.length());

			StringTokenizer tok = new StringTokenizer(gameStartText, " ():\r\n");
			tok.nextToken();
			int gameNumber = Integer.parseInt(tok.nextToken());
			String whiteName = ParserUtil.removeTitles(tok.nextToken());
			String whiteRating = tok.nextToken();
			String blackName = ParserUtil.removeTitles(tok.nextToken());
			String blackRating = tok.nextToken();
			String ratedToken = tok.nextToken();
			boolean isRated = ratedToken.equalsIgnoreCase(RATED_IDENTIFIER);
			String gameTypeDescription = tok.nextToken();
			int gameType = ParserUtil.identifierToGameType(gameTypeDescription);

			int style12Index = gameStartText.indexOf("<12>");
			if (style12Index == -1) {
				LOGGER
						.warn("A Game Start message did not contain a style 12 identifier."
								+ text);
				LOGGER
						.warn("Ignoring game start message. This is either a defect "
								+ "or style is no set to 12.");
				return null;
			}
			MoveEvent style12event = (MoveEvent) style12Parser
					.parseInsideGameStart(gameStartText.substring(style12Index));

			// The creation message seems like its not always accurate.
			boolean isWhiteReallyWhite = ParserUtil.removeTitles(
					style12event.getWhiteName()).equalsIgnoreCase(whiteName);

			return new GameStartEvent(getSource(), "" + id++,
					trimOutNonUserMessage(originalText), style12event
							.getGameId(), getPartnersGameId(originalText),
					isWhiteReallyWhite ? whiteName : blackName,
					isWhiteReallyWhite ? whiteRating : blackRating,
					isWhiteReallyWhite ? blackName : whiteName,
					isWhiteReallyWhite ? blackRating : whiteRating,
					gameTypeDescription, isRated, gameType, style12event);
		}
		return null;
	}

	private static final String WHOM_YOU_WERE_FOLLOWING = ", whom you are following,";

	private static final String NO_LONGER_FOLLOWING = "You will no longer be following ";

	private static final String FOLLOWING = "You will now be following ";

	private static final String RATED_IDENTIFIER = "rated";

	private static final String PREFIX_IDENTIFIER = "You are now observing";

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