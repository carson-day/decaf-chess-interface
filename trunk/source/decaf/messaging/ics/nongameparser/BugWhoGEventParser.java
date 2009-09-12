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

import decaf.gui.widgets.bugseek.BugWhoGGame;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.inform.BugWhoGEvent;

public class BugWhoGEventParser extends NonGameEventParser {

	private static final Logger LOGGER = Logger
			.getLogger(BugWhoGEventParser.class);

	private static final String GAMES_IN_PROGRESS = "Bughouse games in progress";

	private static final String DISPLAYED = "displayed.";

	/**
	 * Bughouse games in progress 65 1613 crankinhaus 1692 RooRooBear [ Br 2 0]
	 * 1:12 - 1:35 (35-36) B: 19 280 1794 Tinker 1847 sadness [ Br 2 0] 1:13 -
	 * 1:32 (43-42) B: 18
	 * 
	 * 27 1986 Nathaniel 1964 PariahCare [ Br 3 0] 1:37 - 1:25 (31-41) B: 25 110
	 * 1514 HawaiianKin 1615 Poindexter [ Br 3 0] 1:35 - 1:19 (47-37) B: 30
	 * 
	 * 66 2130 gorbunaak 1713 FigureOfLi [ Br 2 0] 1:03 - 1:30 (28-33) W: 19 187
	 * 2029 nikechessni 1799 Jlexa [ Br 2 0] 1:17 - 1:19 (50-45) W: 18
	 * 
	 * 3 games displayed.
	 * 
	 * 2 partnerships displayed.
	 */

	public BugWhoGEventParser(int icsId) {
		super(icsId);
	}

	@Override
	public IcsNonGameEvent parse(String text) {
		if (text.startsWith(GAMES_IN_PROGRESS)) {
			StringTokenizer lines = new StringTokenizer(text, "\r\n");
			String currentLine = lines.nextToken();
			List<BugWhoGGame> games = new LinkedList<BugWhoGGame>();

			while (lines.hasMoreTokens()) {
				currentLine = lines.nextToken();

				if (currentLine.endsWith(DISPLAYED)) {
					break;
				} else if (currentLine.trim().equals("")) {
					continue;
				}

				int spaceIndex = currentLine.indexOf(" ", 1);
				BugWhoGGame game = new BugWhoGGame();

				if (spaceIndex == -1) {
					break;
				} else {
					try {
						game.setGame1Id(Integer.parseInt(currentLine.substring(
								0, spaceIndex).trim()));
						game.setGame1Description(currentLine.substring(
								spaceIndex + 1, currentLine.length()).trim());
						currentLine = lines.nextToken();
						spaceIndex = currentLine.indexOf(" ", 1);
						game.setGame2Id(Integer.parseInt(currentLine.substring(
								0, spaceIndex).trim()));
						game.setGame2Description(currentLine.substring(
								spaceIndex + 1, currentLine.length()).trim());
						games.add(game);
					} catch (Exception e) {
						LOGGER.error("Unexpected error occured:", e);
						break;
					}
				}
			}

			return new BugWhoGEvent(getIcsId(), text, games);
		} else {
			return null;
		}
	}

}
