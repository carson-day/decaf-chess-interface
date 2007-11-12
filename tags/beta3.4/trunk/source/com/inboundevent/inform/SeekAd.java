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

public class SeekAd {
	private int gameId;

	private String rating;

	private String playerName;

	private int time;

	private int inc;

	private boolean isRated;

	private String gameDescription;

	private boolean isWhiteSpecified;

	private boolean isBlackSpecified;

	private int lowRange;

	private int highRange;

	private boolean isManual;

	private boolean isFormula;

	/**
	 * @param player1Status
	 *            a constant defined in PlayerAvailability.
	 * @param player2Status
	 *            a constant defined in PlayerAvailability.*
	 */
	public SeekAd(int gameId, String rating, String playerName, int time,
			int inc, boolean isRated, String gameDescription,
			boolean isWhiteSpecified, boolean isBlackSpecified, int lowRange,
			int highRange, boolean isManual, boolean isFormula) {
		this.gameId = gameId;
		this.rating = rating;
		this.playerName = playerName;
		this.time = time;
		this.inc = inc;
		this.isRated = isRated;
		this.gameDescription = gameDescription;
		this.isWhiteSpecified = isWhiteSpecified;
		this.isBlackSpecified = isBlackSpecified;
		this.lowRange = lowRange;
		this.highRange = highRange;
		this.isManual = isManual;
		this.isFormula = isFormula;
	}

	public boolean equals(Object object) {
		try {
			SeekAd seekGame = (SeekAd) object;
			return gameId == seekGame.gameId;
		} catch (ClassCastException classcastexception) {
			return false;
		}
	}

	public String toString() {
		return "<SeekAd>\n" + "   <gameId>" + gameId + "</gameId>\n"
				+ "   <rating>" + rating + "</rating>\n" + "   <playerName>"
				+ playerName + "</playerName>\n" + "   <time>" + time
				+ "</time>\n" + "   <inc>" + inc + "</inc>\n" + "   <isRated>"
				+ isRated + "</isRated>\n" + "   <gameDescription>"
				+ gameDescription + "</gameDescription>\n"
				+ "   <isWhiteSpecified>" + isWhiteSpecified
				+ "</isWhiteSpecified>\n" + "   <isBlackSpecified>"
				+ isBlackSpecified + "</isBlackSpecified>\n" + "   <lowRange>"
				+ lowRange + "</lowRange>\n" + "   <highRange>" + highRange
				+ "</highRange>\n" + "   <isManual>" + isManual
				+ "</isManual>\n" + "   <isFormula>" + isFormula
				+ "</isFormula>\n" + "</SeekAd>\n";
	}

	public int getGameId() {
		return gameId;
	}

	public String getRating() {
		return rating;
	}

	public String getPlayerName() {
		return playerName;
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

	public String getGameDescription() {
		return gameDescription;
	}

	public boolean isWhiteSpecified() {
		return isWhiteSpecified;
	}

	public boolean isBlackSpecified() {
		return isBlackSpecified;
	}

	public int getLowRange() {
		return lowRange;
	}

	public int getHighRange() {
		return highRange;
	}

	public boolean isManual() {
		return isManual;
	}

	public boolean isFormula() {
		return isFormula;
	}

}