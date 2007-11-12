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

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.game.GInfoEvent;

public class GInfoEventParser extends InboundEventParser {
	public GInfoEventParser(Object source) {
		super(source, true, false);
	}

	/*
	 * Game 6: Game information.
	 * 
	 * Lieven (1540) vs GuestFRZK (0) unrated Bughouse game. Time controls: 180
	 * 0 Time of starting: Sat May 11, 07:41 PDT 2002 White time 2:31 Black time
	 * 2:19 The clock is not paused Partner is playing game: GuestCPKM (0) vs.
	 * Dadus (1020) 21 halfmoves have been made. Fifty move count started at
	 * halfmove 21 (100 moves until a draw). White may castle both kingside and
	 * queenside. Black may castle both kingside and queenside. Double pawn push
	 * didn't occur.
	 */

	/**
	 * Game 1: Game information.
	 * 
	 * LectureBot is examining LectureBot vs LectureBot. 6 halfmoves have been
	 * made. Fifty move count started at halfmove 1 (95 moves until a draw).
	 * White may not castle. Black may not castle. Double pawn push didn't
	 * occur.
	 */

	/**
	 * Game 88: Game information.
	 * 
	 * bakoenin (1708) vs RyoSaeba (2298) private rated Blitz game. Time
	 * controls: 180 0 Time of starting: Sat May 11, 16:39 PDT 2002 White time
	 * 1:18 Black time 1:43 The clock is not paused No further information
	 * available as the game is private.
	 */

	/*
	 * Game 28: Game information.
	 * 
	 * JellyRoll (1699) vs TRUPLAYA (1654) rated Bughouse game. Time controls:
	 * 180 0 Time of starting: Mon May 13, 21:57 PDT 2002 White time 2:41 Black
	 * time 2:47 The clock is not paused Partner is playing game: Teleman (1740)
	 * vs. sgs (1712) 15 halfmoves have been made. Fifty move count started at
	 * halfmove 15 (100 moves until a draw). White may castle both kingside and
	 * queenside. Black may castle both kingside and queenside. Double pawn push
	 * didn't occur.
	 */
	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		/*
		 * text = text.trim(); if (text.startsWith(IDENTIFIER) &&
		 * text.indexOf(IDENTIFIER_3) != -1) { StringTokenizer tok = new
		 * StringTokenizer(text, " \r\n"); if (tok.countTokens() > 4) {
		 * tok.nextToken(); int gameId; try { String gameIdString =
		 * tok.nextToken();
		 * 
		 * //parse out: gameId = Integer.parseInt(gameIdString.substring(0,
		 * gameIdString.length() - 1)); } catch (NumberFormatException
		 * numberformatexception) { //return null it was not a GInfoEvent return
		 * null; } if (tok.nextToken().equals(IDENTIFIER) &&
		 * tok.nextToken().equals("information.")) { if
		 * (text.indexOf(EXAMINING_IDENTIFIER) != -1) { String examiner =
		 * tok.nextToken(); tok.nextToken(); tok.nextToken(); String whiteName =
		 * FicsParserUtil .stripTitlesFromName(tok.nextToken());
		 * tok.nextToken(); String blackName = FicsParserUtil
		 * .stripTitlesFromName(tok.nextToken());
		 * 
		 * return new GInfoEvent(getSource(), "" + id++, text, gameId,
		 * whiteName, blackName, examiner, examiner + " " + EXAMINING_IDENTIFIER + " " +
		 * whiteName + " vs " + blackName + "."); } else { String whiteName =
		 * FicsParserUtil .stripTitlesFromName(tok.nextToken()); String
		 * whiteRating = tok.nextToken(); whiteRating = whiteRating.substring(1,
		 * whiteRating .length() - 1); tok.nextToken(); //parse past vs. String
		 * blackName = FicsParserUtil .stripTitlesFromName(tok.nextToken());
		 * String blackRating = tok.nextToken(); blackRating =
		 * blackRating.substring(1, blackRating .length() - 1); String
		 * privateOrRatedToken = tok.nextToken(); boolean isPrivate =
		 * privateOrRatedToken .equalsIgnoreCase(PRIVATE_IDENTIFIER); if
		 * (isPrivate) { privateOrRatedToken = tok.nextToken(); } boolean
		 * isRated = !privateOrRatedToken .equalsIgnoreCase(UNRATED_IDENTIFIER);
		 * int gameType = identifierToGameType(tok.nextToken());
		 * tok.nextToken(); tok.nextToken(); tok.nextToken(); int time =
		 * Integer.parseInt(tok.nextToken()); int inc =
		 * Integer.parseInt(tok.nextToken());
		 * 
		 * if (gameType == GInfoEvent.BUGHOUSE) { int otherBoardIndex =
		 * text.indexOf(IDENTIFIER_2); if (otherBoardIndex != -1) {
		 * StringTokenizer otherBoardTok = new StringTokenizer(
		 * text.substring(otherBoardIndex + IDENTIFIER_2.length(), text
		 * .length()), " ()\r\n"); String whiteNameBoard2 = FicsParserUtil
		 * .stripTitlesFromName(otherBoardTok .nextToken()); String
		 * whiteRatingBoard2 = otherBoardTok .nextToken();
		 * otherBoardTok.nextToken(); //parse past vs. String blackNameBoard2 =
		 * FicsParserUtil .stripTitlesFromName(otherBoardTok .nextToken());
		 * String blackRatingBoard2 = otherBoardTok .nextToken();
		 * 
		 * return new BughouseGInfoEvent(getSource(), "" + id++, text, gameId,
		 * whiteName, whiteRating, blackName, blackRating, whiteNameBoard2,
		 * whiteRatingBoard2, blackNameBoard2, blackRatingBoard2, whiteName +
		 * "/" + blackNameBoard2 + " vs " + blackName + "/" + blackNameBoard2,
		 * isRated, time, inc); } else { throw new RuntimeException( "ERROR::
		 * Could not find " + IDENTIFIER_2 + " for a bughouse GInfo message in
		 * text " + text + "."); } } else { return new GInfoEvent(getSource(), "" +
		 * id++, text, gameId, whiteName, whiteRating, blackName, blackRating,
		 * "", gameType, isRated, time, inc, isPrivate); } } } else { throw new
		 * IllegalArgumentException( "Error parsing GInfoEvent: Second token was
		 * not " + IDENTIFIER + " and the one after that was not information " +
		 * text); } } else { throw new IllegalArgumentException( "Error parsing
		 * GInfoEvent: Token count was <=4" + text); } }
		 */
		return null;
	}

	private int identifierToGameType(String identifier) {
		int result = -1;
		if (identifier.equalsIgnoreCase(STANDARD_IDENTIFIER))
			result = GInfoEvent.STANDARD;
		else if (identifier.equalsIgnoreCase(BLITZ_IDENTIFIER))
			result = GInfoEvent.BLITZ;
		else if (identifier.equalsIgnoreCase(LIGHTNING_IDENTIFIER))
			result = GInfoEvent.LIGHTNING;
		else if (identifier.equalsIgnoreCase(SUICIDE_IDENTIFIER))
			result = GInfoEvent.SUICIDE;
		else if (identifier.equalsIgnoreCase(BUGHOUSE_IDENTIFIER))
			result = GInfoEvent.BUGHOUSE;
		else if (identifier.equalsIgnoreCase(CRAZYHOUSE_IDENTIFIER))
			result = GInfoEvent.CRAZYHOUSE;
		else if (identifier.equalsIgnoreCase(UNTIMED_IDENTIFIER))
			result = GInfoEvent.UNTIMED;
		else if (identifier.indexOf(WILD_IDENTIFIER) != -1)
			result = GInfoEvent.WILD;
		else if (identifier.indexOf(LOSERS_IDENTIFIER) != -1)
			result = GInfoEvent.LOSERS;
		else if (identifier.indexOf(ATOMIC_IDENTIFIER) != -1)
			result = GInfoEvent.ATOMIC;
		else
			throw new IllegalArgumentException("Unrecognized identifier "
					+ identifier);
		return result;
	}

	private static final String STANDARD_IDENTIFIER = "Standard";

	private static final String BLITZ_IDENTIFIER = "Blitz";

	private static final String LIGHTNING_IDENTIFIER = "Lightning";

	private static final String SUICIDE_IDENTIFIER = "Suicide";

	private static final String BUGHOUSE_IDENTIFIER = "Bughouse";

	private static final String CRAZYHOUSE_IDENTIFIER = "Crazyhouse";

	private static final String UNTIMED_IDENTIFIER = "Untimed";

	private static final String EXAMINING_IDENTIFIER = "is examining";

	private static final String WILD_IDENTIFIER = "Wild";

	private static final String ATOMIC_IDENTIFIER = "Atomic";

	private static final String LOSERS_IDENTIFIER = "Losers";

	private static final String UNRATED_IDENTIFIER = "unrated";

	private static final String PRIVATE_IDENTIFIER = "private";

	private static final String IDENTIFIER = "Game";

	private static final String IDENTIFIER_3 = "Game Information";

	private static final String IDENTIFIER_2 = "Partner is playing game: ";

	private static int id;
}