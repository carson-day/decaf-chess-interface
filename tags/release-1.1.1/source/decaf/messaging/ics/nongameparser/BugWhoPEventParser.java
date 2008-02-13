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

import decaf.gui.widgets.bugseek.BugWhoPTeam;
import decaf.gui.widgets.seekgraph.Seek;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.inform.BugWhoPEvent;
import decaf.messaging.inboundevent.inform.SoughtEvent;

public class BugWhoPEventParser extends NonGameEventParser {

	private static final Logger LOGGER = Logger
			.getLogger(BugWhoPEventParser.class);

	/**
	 * Partnerships not playing bughouse 1261 lrzal / ++++ newbface(U) 1701
	 * Comaladama / 1829 Dolus
	 * 
	 * 2 partnerships displayed.
	 */

	public BugWhoPEventParser(int icsId) {
		super(icsId);
	}

	@Override
	public IcsNonGameEvent parse(String text) {
		if (text.startsWith(PARTNERSHIPS_NOT_PLAYING)) {
			StringTokenizer lines = new StringTokenizer(text, "\r\n");
			String currentLine = lines.nextToken();
			List<BugWhoPTeam> teams = new LinkedList<BugWhoPTeam>();

			while (lines.hasMoreTokens()) {
				currentLine = lines.nextToken();

				if (currentLine.trim().equals("")
						|| currentLine.endsWith(PARTNERSHIPS_DISPLAYED)) {
					break;
				}

				// Modify the line so it parses nicely. There are cases where
				// modifiers are between rating and handle without spaces.
				// This code just insures if this happens the lines are altered
				// so they parse nicely.
				int lastIndexSearched = 0;
				
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
						
						//run it again just to make sure the second one isnt foobared as well.
						int newModifierIndex = currentLine.indexOf(MODIFIERS.charAt(i));
						if (newModifierIndex != -1 && newModifierIndex != modifierIndex && newModifierIndex != modifierIndex + 1)
						{
							if (currentLine.charAt(newModifierIndex - 1) != ' ') {
								currentLine = currentLine.substring(0,
										newModifierIndex)
										+ " "
										+ currentLine.substring(newModifierIndex,
												currentLine.length());
							}							
						}
						//There is no need to run it more than two times
					}
					
				}

				StringTokenizer teamTokenizer = new StringTokenizer(
						currentLine, " ");
				BugWhoPTeam team = new BugWhoPTeam();
				team.setPlayer1Rating(teamTokenizer.nextToken());

				String player1Name = teamTokenizer.nextToken();
				char player1Modifier = getModifier(player1Name);
				if (player1Modifier != 0) {
					team.setPlayer1Handle(player1Name.substring(1, player1Name
							.length()));
					team.setPlayer1Modifier(player1Modifier);
				} else {
					team.setPlayer1Handle(player1Name);
				}

				teamTokenizer.nextToken();

				team.setPlayer2Rating(teamTokenizer.nextToken());
				String player2Name = teamTokenizer.nextToken();
				char player2Modifier = getModifier(player2Name);
				if (player2Modifier != 0) {
					team.setPlayer2Handle(player2Name.substring(1, player2Name
							.length()));
					team.setPlayer2Modifier(player2Modifier);
				} else {
					team.setPlayer2Handle(player2Name);
				}

				teams.add(team);
			}

			return new BugWhoPEvent(getIcsId(), text, teams);
		} else {
			return null;
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

	private static final String PARTNERSHIPS_NOT_PLAYING = "Partnerships not playing bughouse";

	private static final String PARTNERSHIPS_DISPLAYED = "displayed.";

	/*
	 *  ^ involved in a game
	 *  ~ running a simul match
	 *  : not open for a match
	 *  # examining a game
	 *  . inactive for 5 minutes or longer, or if "busy" is set
	 * 
	 * not busy
	 *  & involved in a tournament
	 */
	private static final String MODIFIERS = "^~:#.&";

}
