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

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import decaf.gui.widgets.bugseek.UnpartneredBugger;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.inform.BugWhoUEvent;

public class BugWhoUEventParser extends NonGameEventParser {

	private static final Logger LOGGER = Logger
			.getLogger(BugWhoUEventParser.class);

	/**
	 * Unpartnered players with bugopen on
	 * 
	 * 2200 Horkko 1204P^Serp ---- .KandinSki 2131 PowerPanda 1193P^Vancechess
	 * ---- ^kartghel 2067 .SilentScope 1185P Montchalin ---- ^kedron 2020
	 * .sighlintschoop 1174P^BurtForFun ---- .marmes 1987 .Nakonec 1066P^paniq
	 * ---- .masterlooser 1976 ^UndefeatedMonkey 1046P.RAH ---- Mertian 1922
	 * .Wir 935P^DanielCLA ---- ^mrgomm 1901 .Darnock 909 ^blackwater ----
	 * Nerzhul(C) 1865E^Surely 764P noloveexpected ---- nesz 1848E.Iamabugplayer
	 * 698E^Sphoro ---- ^nkb 1803 :TheRaven ---- ^abcdfgh ---- .OSNeutral 1776
	 * Tinker ---- ^alanmike ---- ^paysandu 1740 cday ---- ^bdeutsch ----
	 * philyao 1664P:radrain ---- ^CandleKnight ---- ^QFM 1624P^Ovnis ----
	 * ^Coxybleue ---- ^ranadipb 1566 .oub ---- :Doxxas ---- ^RGrove 1533 :plk
	 * ---- ^ElJunio ---- ^Rkubiak 1487 :felahs ---- ^EMann ---- ^Skyfox
	 * 1464P^dad ---- ^Hezekiah ---- ^surfertony 1452E^WildPudge ---- hubbles
	 * ---- surios 1361E:Rochester(SR) ---- ^JaiMan ---- ^thesonicvision 1327
	 * ^luckybbad(SR)(TM) ---- ^jbaslon ---- ^ThiefTest 1292P.bonesaw ---- jstig
	 * ---- ^utigard 1225P^neuphillyman ---- ^JyHwk ---- .YUDPLAZA
	 * 
	 * 72 players displayed (of 781). (*) indicates system administrator.
	 * 
	 */

	public BugWhoUEventParser(int icsId) {
		super(icsId);
	}

	@Override
	public IcsNonGameEvent parse(String text) {
		if (text.startsWith(UNPARTNERED_PLAYERS)) {
			StringTokenizer lines = new StringTokenizer(text, "\r\n");
			String currentLine = lines.nextToken();
			List<UnpartneredBugger> buggers = new LinkedList<UnpartneredBugger>();

			while (lines.hasMoreTokens()) {
				currentLine = lines.nextToken();

				if (currentLine.trim().equals("") || currentLine.endsWith(END)) {
					break;
				}

				// Modify the line so it parses nicely. There are cases where
				// modifiers are between rating and handle without spaces.
				// This code just insures if this happens the lines are altered
				// so they parse nicely.
				for (int i = 0; i < MODIFIERS.length(); i++) {
					int modifierIndex = currentLine
							.indexOf(MODIFIERS.charAt(i));
					if (modifierIndex != -1) {
						if (currentLine.charAt(modifierIndex - 1) != ' ') {
							currentLine = currentLine.substring(0,
									modifierIndex)
									+ " "
									+ currentLine.substring(modifierIndex,
											currentLine.length());
						}

						// run it again just to make sure the second one isnt
						// foobared as well.
						int newModifierIndex = currentLine.indexOf(MODIFIERS
								.charAt(i));
						if (newModifierIndex != -1
								&& newModifierIndex != modifierIndex
								&& newModifierIndex != modifierIndex + 1) {
							if (currentLine.charAt(newModifierIndex - 1) != ' ') {
								currentLine = currentLine.substring(0,
										newModifierIndex)
										+ " "
										+ currentLine.substring(
												newModifierIndex, currentLine
														.length());
							}
						}
						// There is no need to run it more than two times
					}

				}

				StringTokenizer lineTokenizer = new StringTokenizer(
						currentLine, " ");
				UnpartneredBugger unpartneredBugger = new UnpartneredBugger();
				String rating = lineTokenizer.nextToken();
				String name = lineTokenizer.nextToken();
				populateBugger(unpartneredBugger,name,rating);
				buggers.add(unpartneredBugger);
				
				if (lineTokenizer.countTokens() >= 2)
				{
					UnpartneredBugger unpartneredBugger2 = new UnpartneredBugger();
					 rating = lineTokenizer.nextToken();
					 name = lineTokenizer.nextToken();
					populateBugger(unpartneredBugger2,name,rating);
					buggers.add(unpartneredBugger2);					
				}
				if (lineTokenizer.countTokens() >= 2)
				{
					UnpartneredBugger unpartneredBugger3 = new UnpartneredBugger();
					 rating = lineTokenizer.nextToken();
					 name = lineTokenizer.nextToken();
					populateBugger(unpartneredBugger3,name,rating);	
					buggers.add(unpartneredBugger3);
					
				}
			}

			return new BugWhoUEvent(getIcsId(), text, buggers);
		} else {
			return null;
		}
	}

	private void populateBugger(UnpartneredBugger bugger, String name,
			String rating) {
		if (rating.endsWith("E")) {
			bugger.setRating(rating.substring(0, rating.length() - 1));
			bugger.setRatingModifier('E');
		} else if (rating.endsWith("P")) {
			bugger.setRating(rating.substring(0, rating.length() - 1));
			bugger.setRatingModifier('P');
		} else {
			bugger.setRating(rating);
		}

		char modifier = getModifier(name);

		if (modifier == 0) {
			bugger.setHandle(name);
		} else {
			bugger.setHandle(name.substring(1, name.length()));
			bugger.setHandleModifier(name.charAt(0));
		}
	}

	private char getModifier(String name) {
		int modifiersIndex = MODIFIERS.indexOf(name.charAt(0));

		if (modifiersIndex != -1) {
			return MODIFIERS.charAt(modifiersIndex);
		} else {
			return 0;
		}
	}

	private static final String UNPARTNERED_PLAYERS = "Unpartnered players with bugopen on";

	private static final String END = "(*) indicates system administrator.";

	/*
	 * ^ involved in a game ~ running a simul match : not open for a match #
	 * examining a game . inactive for 5 minutes or longer, or if "busy" is set
	 * 
	 * not busy & involved in a tournament
	 */
	private static final String MODIFIERS = "^~:#.&";

}
