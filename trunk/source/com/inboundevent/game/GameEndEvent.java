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
package decaf.com.inboundevent.game;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class GameEndEvent extends GameEvent {

	public String toString() {
		return "<GameEndEvent>" + super.toString() + "<whiteName>" + lightName
				+ "</whiteName>" + "<blackName>" + darkName + "</blackName>"
				+ "<description>" + description + "</description>" + "<score>"
				+ score + "</score>" + "<getResultString>" + getResultString()
				+ "</getResultString>" + "<oldRating>" + oldRating
				+ "</oldRating>" + "<newRating>" + newRating + "</newRating>"
				+ "<ratingDelta>" + ratingDelta + "</ratingDelta>"
				+ "<newRank>" + newRank + "</getResnewRankultString>"
				+ "<newHRank>" + newHRank + "</newHRank>" + "</GameEndEvent>";
	}

	public GameEndEvent(Object source, String messageId, String text,
			int gameId, String whiteName, String blackName, String description,
			int score) {
		this(source, messageId, text, gameId, whiteName, blackName,
				description, score, -1, -1, -1, -1);
	}

	public GameEndEvent(Object source, String messageId, String text,
			int gameId, String whiteName, String blackName, String description,
			int score, int newRating, int oldRating, int newRank, int newHRank) {
		super(source, messageId, text, gameId);
		this.lightName = whiteName;
		this.darkName = blackName;
		this.description = description;
		this.score = score;
		this.newRating = -1;
		this.oldRating = -1;
		this.newRank = -1;
		this.newHRank = -1;
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

	/**
	 * Returns -1 if not supported.
	 */
	public int getOldRating() {
		return oldRating;
	}

	/**
	 * Returns -1 if not supported.
	 */
	public int getNewHRank() {
		return newHRank;
	}

	/**
	 * Returns -1 if not supported.
	 */
	public int getNewRating() {
		return newRating;
	}

	/**
	 * Returns -1 if not supported.
	 */
	public int getRatingDelta() {
		if (newRating != -1 && oldRating != -1) {
			return oldRating - newRating;
		} else {
			return -1;
		}
	}

	/**
	 * Returns -1 if not supported.
	 */
	public int getNewRank() {
		return newRank;
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

	private int oldRating;

	private int newRating;

	private int ratingDelta;

	private int newRank;

	private int newHRank;

	private int score;
}