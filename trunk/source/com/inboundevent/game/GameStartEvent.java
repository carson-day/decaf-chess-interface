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
// InboundEvent, MoveRequestEvent

public class GameStartEvent extends GameEvent implements GameTypes {

	/**
	 * @param moveEvent
	 *            should never be null. If a move has not been made it should
	 *            just reflect the current position on the chess board.
	 */
	public GameStartEvent(Object source, String messageId, String text,
			int gameId, int partnersGameId, String whiteName,
			String whiteRating, String blackName, String blackRating,
			String description, boolean isRated, int gameType,
			MoveEvent moveEvent) {
		super(source, messageId, text, gameId);
		this.whiteName = whiteName;
		this.whiteRating = whiteRating;
		this.blackName = blackName;
		this.blackRating = blackRating;
		this.gameDescription = description;
		this.gameType = gameType;
		this.isRated = isRated;
		this.firstEvent = moveEvent;
		this.partnersGameId = partnersGameId;
	}

	public String toString() {
		return "<GameStartEvent>" + "<whiteName>"
				+ whiteName + "</whiteName>" + "<whiteRating>" + whiteRating
				+ "</whiteRating>" + "<blackName>" + blackName + "</blackName>"
				+ "<blackRating>" + blackRating + "</blackRating>"
				+ "<gameDescription>" + gameDescription + "</gameDescription>"
				+ "<gameType>" + gameType + "</gameType>" + "<firstEvent>"
				+ firstEvent + "</firstEvent>" + "<isRated>" + isRated
				+ "</isRated>" + "</GameStartEvent>";
	}

	public String getWhiteName() {
		return whiteName;
	}

	public String getBlackName() {
		return blackName;
	}

	public String getWhiteRating() {
		return whiteRating;
	}

	public String getBlackRating() {
		return blackRating;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public boolean isRated() {
		return isRated;
	}

	/**
	 * Returns -1 if there is none, otherwise returns the number of the partners
	 * game.
	 */
	public int partnersGameId() {
		return partnersGameId;
	}

	public int getGameType() {
		return gameType;
	}

	public MoveEvent getInitialInboundChessMoveEvent() {
		return firstEvent;
	}

	private String whiteName;

	private String blackName;

	private String whiteRating;

	private String blackRating;

	private String gameDescription;

	private int gameType;

	private boolean isRated;

	private MoveEvent firstEvent;

	private int partnersGameId;
}