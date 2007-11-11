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
package decaf.com.inboundevent.inform;

import decaf.gui.util.StringUtility;

public class BugGameInProgress {

	public BugGameInProgress(int gameId1, int gameId2, String game1WhiteName,
			String game1WhiteRating, String game1BlackName,
			String game1BlackRating, String game2WhiteName,
			String game2WhiteRating, String game2BlackName,
			String game2BlackRating, boolean isRated, int time, int inc) {
		this.game1Id = gameId1;
		this.game2Id = gameId2;
		this.game1WhiteName = game1WhiteName;
		this.game1WhiteRating = game1WhiteRating;
		this.game1BlackName = game1BlackName;
		this.game1BlackRating = game1BlackRating;
		this.game2WhiteName = game2WhiteName;
		this.game2WhiteRating = game2WhiteRating;
		this.game2BlackName = game2BlackName;
		this.game2BlackRating = game2BlackRating;
		this.isRated = isRated;
		this.time = time;
		this.inc = inc;
	}

	public boolean equals(Object obj) {
		BugGameInProgress bugGame = (BugGameInProgress) obj;
		return game1Id == bugGame.getGame1Id()
				&& game2Id == bugGame.getGame2Id();
	}

	public int getGame1Id() {
		return game1Id;
	}

	public int getGame2Id() {
		return game2Id;
	}

	public String getGame1WhiteName() {
		return game1WhiteName;
	}

	public String getGame1WhiteRating() {
		return game1WhiteRating;
	}

	public String getGame1BlackName() {
		return game1BlackName;
	}

	public String getGame1BlackRating() {
		return game1BlackRating;
	}

	public String getGame2WhiteName() {
		return game2WhiteName;
	}

	public String getGame2WhiteRating() {
		return game2WhiteRating;
	}

	public String getGame2BlackName() {
		return game2BlackName;
	}

	public String getGame2BlackRating() {
		return game2BlackRating;
	}

	public int getTime() {
		return time;
	}

	public int getInc() {
		return inc;
	}

	public boolean isRated() {
		return isRated;
	}

	public String toString() {
		return "Game "
				+ StringUtility.toRightPaddedFixedLenString("(" + game1Id + "/"
						+ game2Id + ")", 11)
				+ " "
				+ StringUtility
						.toRightPaddedFixedLenString(game1WhiteRating, 5)
				+ StringUtility.toRightPaddedFixedLenString(game1WhiteName, 12)
				+ "/"
				+ StringUtility
						.toRightPaddedFixedLenString(game2BlackRating, 5)
				+ StringUtility.toRightPaddedFixedLenString(game2BlackName, 12)
				+ " vs "
				+ StringUtility
						.toRightPaddedFixedLenString(game1BlackRating, 5)
				+ StringUtility.toRightPaddedFixedLenString(game1BlackName, 12)
				+ "/"
				+ StringUtility
						.toRightPaddedFixedLenString(game2WhiteRating, 5)
				+ StringUtility.toRightPaddedFixedLenString(game2WhiteName, 12);
	}

	private int game1Id;

	private int game2Id;

	private String game1WhiteName;

	private String game1WhiteRating;

	private String game1BlackName;

	private String game1BlackRating;

	private String game2WhiteName;

	private String game2WhiteRating;

	private String game2BlackName;

	private String game2BlackRating;

	private int time;

	private int inc;

	private boolean isRated;
}