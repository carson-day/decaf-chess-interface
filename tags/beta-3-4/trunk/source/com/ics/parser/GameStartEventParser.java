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

public class GameStartEventParser extends InboundEventParser {
	private static final Logger LOGGER = Logger
			.getLogger(GameStartEventParser.class);

	public GameStartEventParser(Object source) {
		super(source, false, false);

		style12Parser = new Style12EventParser(source);
	}

	/*
	 * Setting ivariable gameinfo provides the interface with extra
	 * notifications when the start starts a game or simul or a game is
	 * observed.
	 * 
	 * Example output: - <g1> 1 p=0 t=blitz r=1 u=1,1 it=5,5 i=8,8 pt=0
	 * rt=1586E,2100 ts=1,0
	 * 
	 * (note the - was added so as not to confuse interfaces displaying this
	 * helpfile)
	 * 
	 * This is in the format: - <g1> game_number p=private(1/0) t=type
	 * r=rated(1/0) u=white_registered(1/0),black_registered(1/0)
	 * it=initial_white_time,initial_black_time
	 * i=initial_white_inc,initial_black_inc pt=partner's_game_number(or 0 if
	 * none) rt=white_rating(+ provshow character),black_rating(+ provshow
	 * character) ts=white_uses_timeseal(0/1),black_uses_timeseal(0/1)
	 * 
	 * Note any new fields will be appended to the end so the interface must be
	 * able to handle this.
	 * 
	 * See Also: iset ivariables
	 */

	/**
	 * Creating: MiloBot (++++) GuestZSHF (++++) unrated wild/5 15 0 {Game 7
	 * (MiloBot vs. GuestZSHF) Creating unrated wild/5 match.}
	 * 
	 * <12>RNBKQBNR PPPPPPPP -------- -------- -------- -------- pppppppp
	 * rnbkqbnr W -1 0 0 0 0 0 7 MiloBot GuestZSHF -1 15 0 39 39 900 900 1 none
	 * (0:00) none 1 0 0
	 */

	/**
	 * Creating: kingeeyore (1332) CDay (1725E) rated standard 15 0 {Game 52
	 * (kingeeyore vs. CDay) Creating rated standard match.}
	 * 
	 * <12>rnbqkbnr pppppppp -------- -------- -------- -------- PPPPPPPP
	 * RNBQKBNR W -1 1 1 1 1 0 52 kingeeyore CDay -1 15 0 39 39 900 900 1 none
	 * (0:00) none 1 0 0
	 */

	/**
	 * Creating: CDay (----) GuestMSQH (++++) unrated untimed 0 0 {Game 74 (CDay
	 * vs. GuestMSQH) Creating unrated untimed match.}
	 * 
	 * <12>rnbqkbnr pppppppp -------- -------- -------- -------- PPPPPPPP
	 * RNBQKBNR W -1 1 1 1 1 0 74 CDay GuestMSQH 1 0 0 39 39 0 0 1 none (0:00)
	 * none 0 0 0
	 * 
	 * 
	 * You are now observing game 106.
	 * 
	 * Game 106: Fridsam (1961) tripitaka (1991) rated blitz 5 0
	 * 
	 * <12>-----bk- -----p-- ----p-p- -pNp---- -PqP-P-- -QP---P- -----K--
	 * -------- B -1 0 0 0 0 2 106 Fridsam tripitaka 0 5 0 17 17 104 152 35
	 * Q/b2-b3 (0:10) Qb3 0 1 0
	 * 
	 * 
	 * 
	 */

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {

		if (text.length() > 600) {
			return null;
		}
		String originalText = text;
		String gameStartText = null;
		if (text.startsWith(PREFIX_IDENTIFIER)) {
			gameStartText = text;
		} else {
			int gameStartIndex = text.indexOf(INNER_PREFIX_IDENTIFIER);
			if (gameStartIndex != -1)
				gameStartText = text.substring(gameStartIndex + 2);
			else
				return null;
		}
		StringTokenizer tok = new StringTokenizer(gameStartText, " ()\r\n");
		tok.nextToken();

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
			LOGGER.warn("Ignoring game start message. This is either a defect "
					+ "or style is not set to 12.");
			return null;
		}

		MoveEvent style12event = (MoveEvent) style12Parser
				.parseInsideGameStart(gameStartText.substring(style12Index));

		// The creation message seems like its not always accurate.
		boolean isWhiteReallyWhite = ParserUtil.removeTitles(
				style12event.getWhiteName()).equalsIgnoreCase(whiteName);

		return new GameStartEvent(getSource(), "" + id++,
				trimOutNonUserMessage(originalText), style12event.getGameId(),
				getPartnersGameId(originalText), isWhiteReallyWhite ? whiteName
						: blackName, isWhiteReallyWhite ? whiteRating
						: blackRating, isWhiteReallyWhite ? blackName
						: whiteName, isWhiteReallyWhite ? blackRating
						: whiteRating, gameTypeDescription, isRated, gameType,
				style12event);
	}

	protected int getPartnersGameId(String msg) {
		int ptIndex = msg.indexOf("pt=");
		if (ptIndex != -1) {
			StringTokenizer tok = new StringTokenizer(msg.substring(ptIndex),
					"=\n\r ");
			if (tok.hasMoreTokens()) {
				tok.nextToken();
			}
			int gameId = Integer.parseInt(tok.nextToken());
			gameId = gameId == 0 ? -1 : gameId;
			tok.nextToken();

			return gameId;
		} else {
			return -1;
		}
	}

	protected String trimOutNonUserMessage(String msg) {
		String result = msg;
		int style12Index = msg.indexOf("<12>");
		if (style12Index != -1) {
			result = result.substring(0, style12Index);
		}
		int g1Index = msg.indexOf("<g1>");
		if (g1Index != -1) {
			result = result.substring(0, g1Index);
		}
		return result.trim();
	}

	private static final String RATED_IDENTIFIER = "rated";

	private static final String PREFIX_IDENTIFIER = "Creating: ";

	// \n\r is replaced \n in ICSCommunicationsDriver
	private static final String INNER_PREFIX_IDENTIFIER = "\nCreating: ";

	protected Style12EventParser style12Parser;

	protected static int id;
}