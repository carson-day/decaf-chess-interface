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
package decaf.sound;

public class SoundKeys {
	public static final String ALERT_KEY = "ALERT";

	public static final String GAME_START_KEY = "GAME_START";

	public static final String ILLEGAL_MOVE_KEY = "ILLEGAL_MOVE";

	public static final String LOSE_KEY = "LOSE";

	public static final String MOVE_KEY = "MOVE";

	public static final String OBS_GAME_END_KEY = "OBS_GAME_END";

	public static final String OBS_MOVE_KEY = "OBS_MOVE";

	public static final String WIN_KEY = "WIN";

	public static final String CHAT_KEY = "CHAT";

	public static final String[][] SOUNDS_TO_LOAD = new String[][] {
			new String[] { "alert.wav", ALERT_KEY },
			new String[] { "chat.wav", CHAT_KEY },
			new String[] { "gameStart.wav", GAME_START_KEY },
			new String[] { "illegalMove.wav", ILLEGAL_MOVE_KEY },
			new String[] { "lose.wav", LOSE_KEY },
			new String[] { "move.wav", MOVE_KEY },
			new String[] { "obsGameEnd.wav", OBS_GAME_END_KEY },
			new String[] { "obsMove.wav", OBS_MOVE_KEY },
			new String[] { "win.wav", WIN_KEY }, };
}