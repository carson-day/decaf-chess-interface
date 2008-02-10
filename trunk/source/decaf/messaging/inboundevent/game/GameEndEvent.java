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
package decaf.messaging.inboundevent.game;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class GameEndEvent extends GameEvent {

	public String toString() {
		return "GameEndEvent: icsId=" + getIcsId() + " gameId=" + getGameId();
	}

	public GameEndEvent(int icsId, int gameId, String whiteName,
			String blackName, String description, int score) {
		super(icsId, gameId);
		this.lightName = whiteName;
		this.darkName = blackName;
		this.description = description;
		this.score = score;

	}

	public String getWhiteName() {
		return lightName;
	}

	public String getBlackName() {
		return darkName;
	}

	public String getDescription() {
		return description;
	}

	public String getResultString() {
		switch (score) {
		case WHITE_WON:
			return "1-0";
		case BLACK_WON:
			return "0-1";
		case DRAW:
			return "1/2-1/2";
		case ADJOURNED:
			return "Adjourned";
		case ABORTED:
			return "Aborted";
		case UNDETERMINED:
			return "???";
		default:
			throw new IllegalArgumentException("Invalid score encontered: "
					+ score);
		}
	}

	public int getScore() {
		return score;
	}

	public static final int WHITE_WON = 0;

	public static final int BLACK_WON = 1;

	public static final int DRAW = 2;

	public static final int ADJOURNED = 3;

	public static final int ABORTED = 4;

	public static final int UNDETERMINED = 5;

	private String lightName;

	private String darkName;

	private String description;

	private int score;
}