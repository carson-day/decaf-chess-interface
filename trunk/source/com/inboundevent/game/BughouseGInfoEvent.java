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

public class BughouseGInfoEvent extends GameEvent {
	/**
	 * Uses board1 game id for the gameId in GameEvent.
	 */
	public BughouseGInfoEvent(Object source, String messageId, String text,
			int gameId, String whiteNameBoard1, String whiteRatingBoard1,
			String blackNameBoard1, String blackRatingBoard1,
			String whiteNameBoard2, String whiteRatingBoard2,
			String blackNameBoard2, String blackRatingBoard2,
			String gameDescription, boolean isRated, int initialTime,
			int initialInc) {
		super(source, messageId, text, gameId);

		this.whitesNameBoard1 = whiteNameBoard1;
		this.whitesRatingBoard1 = whiteRatingBoard1;
		this.blacksNameBoard1 = blackNameBoard1;
		this.blacksRatingBoard1 = blackRatingBoard1;
		this.whitesNameBoard2 = whiteNameBoard2;
		this.whitesRatingBoard2 = whiteRatingBoard2;
		this.blacksNameBoard2 = blackNameBoard2;
		this.blacksRatingBoard2 = blackRatingBoard2;
		this.gameDescription = gameDescription;
		this.isRated = isRated;
		this.initialTime = initialTime;
		this.initialInc = initialInc;
	}

	public String getWhitesNameBoard1() {
		return whitesNameBoard1;
	}

	public String getWhitesRatingBoard1() {
		return whitesRatingBoard1;
	}

	public String getBlacksNameBoard1() {
		return blacksNameBoard1;
	}

	public String getBlacksRatingBoard1() {
		return blacksRatingBoard1;
	}

	public String getWhitesNameBoard2() {
		return whitesNameBoard2;
	}

	public String getWhitesRatingBoard2() {
		return whitesRatingBoard2;
	}

	public String getBlacksNameBoard2() {
		return blacksNameBoard2;
	}

	public String getBlacksRatingBoard2() {
		return blacksRatingBoard2;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public boolean isRated() {
		return isRated;
	}

	public int getInitialTime() {
		return initialTime;
	}

	public int getInitialInc() {
		return initialInc;
	}

	public String toString() {
		return "<BughouseGInfoEvent>" + super.toString() + "<whitesNameBoard1>"
				+ whitesNameBoard1 + "</whitesNameBoard1>"
				+ "<whitesRatingBoard1>" + whitesRatingBoard1
				+ "</whitesRatingBoard1>" + "<blacksNameBoard1>"
				+ blacksNameBoard1 + "</blacksNameBoard1>"
				+ "<blacksRatingBoard1>" + blacksRatingBoard1
				+ "</blacksRatingBoard1>" + "<whitesNameBoard2>"
				+ whitesNameBoard2 + "</whitesNameBoard2>"
				+ "<whitesRatingBoard2>" + whitesRatingBoard2
				+ "</whitesRatingBoard2>" + "<blacksNameBoard2>"
				+ blacksNameBoard2 + "</blacksNameBoard2>"
				+ "<blacksRatingBoard2>" + blacksRatingBoard2
				+ "</blacksRatingBoard2>" + "<gameDescription>"
				+ gameDescription + "</gameDescription>" + "<initialTime>"
				+ initialTime + "</initialTime>" + "<initialInc>" + initialInc
				+ "</initialInc>" + "</BughouseGInfoEvent>";
	}

	private String whitesNameBoard1;

	private String whitesRatingBoard1;

	private String blacksNameBoard1;

	private String blacksRatingBoard1;

	private String whitesNameBoard2;

	private String whitesRatingBoard2;

	private String blacksNameBoard2;

	private String blacksRatingBoard2;

	private String gameDescription;

	private boolean isRated;

	private int initialTime;

	private int initialInc;
}