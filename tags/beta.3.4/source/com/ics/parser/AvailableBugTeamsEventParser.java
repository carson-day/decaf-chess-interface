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

import java.util.LinkedList;
import java.util.StringTokenizer;

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.inform.AvailableBugTeam;
import decaf.com.inboundevent.inform.AvailableBugTeamsEvent;
import decaf.com.inboundevent.inform.PlayerAvailability;

public class AvailableBugTeamsEventParser extends InboundEventParser {
	public AvailableBugTeamsEventParser(Object source) {
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

	/*
	 * Bughouse games in progress 111 2303 LINDEGREN 2182 killabug [ Br 3 0]
	 * 2:01 - 0:52 (47-49) B: 17 139 2346 LinusO 2479 Bugzilla [ Br 3 0] 2:13 -
	 * 0:46 (31-29) B: 24
	 * 
	 * 1 game displayed.
	 * 
	 * Partnerships not playing bughouse 1212P logicboy / 1402 Partner 1316
	 * Multani / 1608 family 1826 ^Tabak / 1866 ^jesusga 2377 :DragonSlayr /
	 * 2681 :VABORIS
	 * 
	 * 4 partnerships displayed.
	 * 
	 * Unpartnered players with bugopen on
	 * 
	 * 2320 venomous(FM) 1478P Pierlu 1191P^Pininahaystack 2231 .spurs
	 * 1441P^kevinfleming 1161P IamTheGame 2048 .FilthyAnimal(FM) 1439P^monimo
	 * 1110P uncino 1948 ^mandevil 1416P Rivaldo 1078P.smithytwo 1874 SavageAn
	 * 1406 tiberiansun 1051P^medvedko 1834 CDay 1384 .crazyalex 1018E.MAdBorg
	 * 1834 .Grateful 1382E^sautov 968P^RZO 1726P^Firebutton 1342 .qiaoh
	 * 915P.delznic 1709 Hitmonchan 1336P amerb ---- ^Montpellier 1702 :Testudo
	 * 1326 ^PawnEater(TM) ---- ^marck 1683P^gawk 1303P^Rovertje ---- ^Salamon
	 * 1606P.spurted(*) 1291 Cannes ---- ^ErMonnezza 1593 Comaladama 1272
	 * ^jerryio ---- ^trotto 1592E WillSFourteen 1265P^prograf ---- ^pacip 1582
	 * BillJr 1262E^Schelli ---- :FricFrac 1503 KenGriffey 1213P^Credit ----
	 * ^bdgl
	 */

	/**
	 * Status codes: ^ involved in a game ~ running a simul match : not open for
	 * a match # examining a game . inactive for 5 minutes or longer, or if
	 * "busy" is set not busy & involved in a tournament
	 */

	/**
	 * 
	 * Partnerships not playing bughouse
	 * 
	 * 1238 BugHolio / ---- bdgl
	 * 
	 * 1278 jackers / 1197^Multani
	 * 
	 * 1496 Comaladama / 1677^NoJoking
	 * 
	 * 1412 crazyalex / 2176:Tecumseh
	 */

	/**
	 * Returns a String[3] where 0 is players rating, 1 is Name, 2 is player
	 * status (space if available).
	 */
	private static String[] getPartnerInfo(boolean isFirstPlayerInString,
			String line) {
		/*
		 * String[] result = new String[3]; int slashIndex = line.indexOf("/");
		 * 
		 * line = isFirstPlayerInString ? line.substring(0, slashIndex).trim() :
		 * line.substring(slashIndex + 1, line.length()).trim(); StringTokenizer
		 * tok = new StringTokenizer(line, " ^-:#.&", true); int tokenCount =
		 * tok.countTokens();
		 * 
		 * if (line.startsWith("----")) //handle never played case. {
		 * tok.nextToken(); tok.nextToken(); tok.nextToken(); tok.nextToken();
		 * String token1 = tok.nextToken(); String token2 = tok.nextToken();
		 * 
		 * result[0] = "----"; if (token2.length() == 1) { result[1] =
		 * tok.nextToken(); result[2] = token2; } else { result[1] = token2;
		 * result[2] = token1; } } else if (tokenCount == 3) { result[0] =
		 * tok.nextToken(); result[2] = tok.nextToken(); //tokens are being
		 * returned. result[1] = tok.nextToken(); } else if (tokenCount == 4) {
		 * result[0] = tok.nextToken(); String token = tok.nextToken(); if
		 * (token.equals(" ")) { result[2] = tok.nextToken(); //tokens are being
		 * returned. result[1] = tok.nextToken(); } else { throw new
		 * IllegalArgumentException( "Unable to parse partner info in: " +
		 * line); } } else { throw new IllegalArgumentException( "Unable to
		 * parse partner info in: " + line); } return result;
		 */
		return null;
	}

	private static int playerAvailabilityToConstant(String playerAvailability) {
		if (playerAvailability.equals(" ")) {
			return PlayerAvailability.AVAILABLE;
		} else if (playerAvailability.equals("^")) {
			return PlayerAvailability.PLAYING;
		} else if (playerAvailability.equals("-")) {
			return PlayerAvailability.RUNNING_SIMUL;
		} else if (playerAvailability.equals("#")) {
			return PlayerAvailability.EXAMINING;
		} else if (playerAvailability.equals(".")) {
			return PlayerAvailability.IDLE;
		} else if (playerAvailability.equals("&")) {
			return PlayerAvailability.IN_TOURNEY;
		} else if (playerAvailability.equals(":")) {
			return PlayerAvailability.CLOSED;
		}

		else {
			throw new IllegalArgumentException(
					"Unknown playerAvailability encountered: "
							+ playerAvailability);
		}
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {

		int partnersNotPlayingIndex = text.indexOf(EVENT_START_ID);
		if (partnersNotPlayingIndex != -1) {
			String messageText = text.substring(partnersNotPlayingIndex, text
					.length());

			StringTokenizer newLineTok = new StringTokenizer(messageText,
					LINE_SEPARATORS);
			LinkedList linkedlist = new LinkedList();

			newLineTok.nextToken(); // parse past first line.

			while (newLineTok.hasMoreTokens()) {
				String currentLine = newLineTok.nextToken().trim();
				if (currentLine.equals("")
						|| currentLine.indexOf(END_LINE_ID) != -1) {
					break;
				} else {
					String[] player1Info = getPartnerInfo(true, currentLine);
					String[] player2Info = getPartnerInfo(false, currentLine);
					AvailableBugTeam team = new AvailableBugTeam(ParserUtil
							.removeTitles(player1Info[1]), player1Info[0],
							playerAvailabilityToConstant(player1Info[2]),
							ParserUtil.removeTitles(player2Info[1]),
							player2Info[0],
							playerAvailabilityToConstant(player2Info[2]));
					linkedlist.add(team);

				}
			}
			return new AvailableBugTeamsEvent(getSource(), "" + id++,
					messageText, linkedlist);
		} else {
			return null;
		}
	}

	private static final String EVENT_START_ID = "Partnerships not playing bughouse";

	private static final String LINE_SEPARATORS = "\r\n";

	private static final String END_LINE_ID = "displayed.";

	private static int id;
}