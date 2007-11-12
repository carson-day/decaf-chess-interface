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

/**
 * An event providing detailed game information. Bughouse events should use
 * BughouseGInfoEvent.
 */
public class GInfoEvent extends GameEvent implements GameTypes {

	/**
	 * Intended to be used when someone is examining a game.
	 */
	public GInfoEvent(Object source, String messageId, String text, int gameId,
			String whiteName, String blackName, String examiner,
			String description) {
		super(source, messageId, text, gameId);
		this.whitesName = whiteName;
		this.blacksName = blackName;
		this.examiner = examiner;
		this.gameDescription = description;
		this.gameType = EXAMINING;
	}

	/**
	 * Intended to be used when someone is not examining a game.
	 */
	public GInfoEvent(Object source, String messageId, String text, int gameId,
			String whiteName, String whiteRating, String blackName,
			String blackRating, String description, int gameType,
			boolean isRated, int initialTime, int initialInc, boolean isPrivate) {
		super(source, messageId, text, gameId);
		this.whitesName = whiteName;
		this.whitesRating = whiteRating;
		this.blacksName = blackName;
		this.blacksRating = blackRating;
		this.gameDescription = description;
		this.gameType = gameType;
		this.isRated = isRated;
		this.initialTime = initialTime;
		this.initialInc = initialInc;
		this.isPrivate = isPrivate;
	}

	public String getWhitesName() {
		return whitesName;
	}

	public String getWhitesRating() {
		return whitesRating;
	}

	public String getBlacksName() {
		return blacksName;
	}

	public String getBlacksRating() {
		return blacksRating;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public int getGameType() {
		return gameType;
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

	public String getExaminer() {
		return examiner;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public String toString() {
		return "<GInfoEvent>" + super.toString() + "<whiteName>" + whitesName
				+ "</whitesName>" + "<whitesRating>" + whitesRating
				+ "</whitesRating>" + "<blacksName>" + blacksName
				+ "</blacksName>" + "<blacksRating>" + blacksRating
				+ "</blacksRating>" + "<gameDescription>" + gameDescription
				+ "</gameDescription>" + "<gameType>" + gameType
				+ "</gameType>" + "<initialTime>" + initialTime
				+ "</initialTime>" + "<initialInc>" + initialInc
				+ "</initialInc>" + "<isRated>" + isRated + "</isRated>"
				+ "<isPrivate>" + isPrivate + "</isPrivate>" + "<examiner>"
				+ examiner + "</examiner>" + "</GInfoEvent>";
	}

	private String whitesName;

	private String whitesRating;

	private String blacksName;

	private String blacksRating;

	private String gameDescription;

	private int gameType;

	private boolean isRated;

	private boolean isPrivate;

	private int initialTime;

	private int initialInc;

	private String examiner;
}