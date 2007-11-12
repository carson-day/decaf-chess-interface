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

import decaf.moveengine.Position;

public class MoveEvent extends GameEvent {
	public MoveEvent(Object source, String messageId, String text, int gameId,
			Position position, int numberOfMovesSinceLastIrreversible,
			String whiteName, String blackName, int relation, int initialTime,
			int initialInc, int whiteStrength, int blackStrength,
			int whiteRemainingTime, int blackRemainingTime,
			int standardChessMoveNumber, String verboseNotation,
			int timeTakenForLastMove, String prettyNotation,
			boolean isWhiteOnTop, boolean isClockTicking, int lagInMillis,
			DroppableHoldingsChangedEvent holdingsChangedEvent) {
		super(source, messageId, text, gameId);
		this.position = position;
		this.numberOfMovesSinceLastIrreversible = numberOfMovesSinceLastIrreversible;
		this.whiteName = whiteName;
		this.blackName = blackName;
		this.relation = relation;
		this.initialTime = initialTime;
		this.initialInc = initialInc;
		this.whiteStrength = whiteStrength;
		this.blackStrength = blackStrength;
		this.whiteRemainingTime = whiteRemainingTime;
		this.blackRemainingTime = blackRemainingTime;
		this.standardChessMoveNumber = standardChessMoveNumber;
		this.verboseNotation = verboseNotation;
		this.timeTakenForLastMove = timeTakenForLastMove;
		this.prettyNotation = prettyNotation;
		this.isWhiteOnTop = isWhiteOnTop;
		this.isClockTicking = isClockTicking;
		this.lagInMillis = lagInMillis;
		this.holdingsChangedEvent = holdingsChangedEvent;
	}

	// public String toString()
	// {
	// return XmlUtil.toXml(this);
	// }

	public static final int ISOLATED_POSITION_RELATION = -3;

	public static final int OBSERVING_EXAMINED_GAME_RELATION = -2;

	public static final int EXAMINING_GAME_RELATION = 2;

	public static final int PLAYING_OPPONENTS_MOVE_RELATION = -1;

	public static final int PLAYING_MY_MOVE_RELATION = 1;

	public static final int OBSERVING_GAME_RELATION = 0;

	private static final String NEWLINE = "\n";

	private DroppableHoldingsChangedEvent holdingsChangedEvent;

	private Position position;

	private int numberOfMovesSinceLastIrreversible;

	private String whiteName;

	private String blackName;

	private int relation;

	private int initialTime;

	private int initialInc;

	private int whiteStrength;

	private int blackStrength;

	private int whiteRemainingTime;

	private int blackRemainingTime;

	private int standardChessMoveNumber;

	private String verboseNotation;

	private int timeTakenForLastMove;

	private String prettyNotation;

	private boolean isWhiteOnTop;

	private boolean isClockTicking;

	private int lagInMillis;

	public String getWhiteName() {
		return whiteName;
	}

	public String getBlackName() {
		return blackName;
	}

	public DroppableHoldingsChangedEvent getHoldingsChangedEvent() {
		return holdingsChangedEvent;
	}

	public int getlagInMillis() {
		return lagInMillis;
	}

	public Position getPosition() {
		return position;
	}

	public boolean isWhitesMove() {
		return position.isWhitesMove();
	}

	public int getDoublePawnPushFile() {
		return position.getLastMoveDoublePawnPushFile();
	}

	public boolean isWhiteAbleToCastleShort() {
		return position.whiteCanCastleKingside();
	}

	public boolean isWhiteAbleToCastleLong() {
		return position.whiteCanCastleQueenside();
	}

	public boolean isBlackAbleToCastleShort() {
		return position.blackCanCastleKingside();
	}

	public boolean isBlackAbleToCastleLong() {
		return position.blackCanCastleQueenside();
	}

	public int getNumberOfMovesSinceLastIrreversible() {
		return numberOfMovesSinceLastIrreversible;
	}

	/**
	 * Returns one of the relation constants.
	 */
	public int getRelation() {
		return relation;
	}

	public int getInitialTime() {
		return initialTime;
	}

	public int getInitialInc() {
		return initialInc;
	}

	public int getWhiteStrength() {
		return whiteStrength;
	}

	public int getBlackStrength() {
		return blackStrength;
	}

	/**
	 * Returns whites remaining time in seconds.
	 */
	public int getWhiteRemainingTime() {
		return whiteRemainingTime;
	}

	/**
	 * Returns blacks remaining time in seconds.
	 */
	public int getBlackRemainingTime() {
		return blackRemainingTime;
	}

	public int getStandardChessMoveNumber() {
		return standardChessMoveNumber;
	}

	public String getVerboseNotation() {
		return verboseNotation;
	}

	/**
	 * Returns the amount of time taken for the last move in seconds.
	 */
	public int getTimeTakenForLastMove() {
		return timeTakenForLastMove;
	}

	public String getPrettyNotation() {
		return prettyNotation;
	}

	public boolean isWhiteOnTop() {
		return isWhiteOnTop;
	}

	public boolean isClockTicking() {
		return isClockTicking;
	}
}