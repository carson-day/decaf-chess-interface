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
package decaf.moveengine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import decaf.gui.util.PropertiesUtil;
import decaf.gui.util.ThreadUtil;


public class ChessGame implements Cloneable {
	private Position currentPosition;

	private List moveListeners;

	private boolean isForking;

	private boolean isForkingSet;

	private List positionList;

	public ChessGame() {
		moveListeners = new LinkedList();
		currentPosition = new Position();
		positionList = new LinkedList();
		positionList.add(currentPosition);
	}

	public ChessGame(Position initialPosition) {
		this.currentPosition = initialPosition;
		moveListeners = new LinkedList();
		positionList = new LinkedList();
		positionList.add(currentPosition);
	}

	public void setForkingMoveListener(boolean isForking) {
		synchronized (this) {
			this.isForking = isForking;
			isForkingSet = true;
		}
	}

	public boolean isForkingMoveListener() {
		if (!isForkingSet) {
			synchronized (this) {
				setForkingMoveListener(PropertiesUtil
						.getBoolean("chess.game.movelistener.fork"));
			}
		}
		return isForking;
	}

	public void makeMove(Move move) throws IllegalMoveException {
		synchronized (this) {
			makingMove(getCurrentPosition(), move);
			try {
				Position newPosition = getCurrentPosition().makeMove(move);
				changePosition(newPosition);
			} catch (IllegalMoveException ime) {
				illegalMove(getCurrentPosition(), ime);
				throw ime;
			}
			moveCompleted(getCurrentPosition(), move);
		}
	}

	/**
	 * Returns a Move[] of all of the available Move objects in the current
	 * position. Returns an empty array if there are no moves.
	 */
	public Move[] getAvailableMoves() {
		synchronized (this) {
			Move[] result = null;
			if (currentPosition.isCheckmate() || currentPosition.isStalemate()) {
				result = new Move[] {};
			} else {
				result = currentPosition.getLegalMoves();
			}
			return result;
		}
	}

	private void changePosition(Position newPosition) {
		positionList.add(newPosition);
		currentPosition = newPosition;
	}

	public Position getCurrentPosition() {
		synchronized (this) {
			return currentPosition;
		}
	}

	/**
	 * Returns the number of half moves made during this game.
	 */
	public int getNumberOfHalfMovesMade() {
		synchronized (this) {
			return positionList.size() - 1;
		}
	}

	/**
	 * Returns the position at the specified halfMoveIndex. 0 will return the
	 * initial game position. getNumberOfHalfMovesMade() will return the current
	 * position.
	 */
	public Position getPosition(int halfMoveIndex) {
		synchronized (this) {
			return (Position) positionList.get(halfMoveIndex);
		}
	}

	public void addMoveListener(MoveListener moveListener) {
		synchronized (this) {
			moveListeners.add(moveListener);
		}
	}

	public void removeMoveListener(MoveListener moveListener) {
		synchronized (this) {
			moveListeners.remove(moveListener);
		}
	}

	private void makingMove(Position position, Move move) {
		for (Iterator i = moveListeners.iterator(); i.hasNext();) {
			MoveListener listener = (MoveListener) i.next();
			if (isForkingMoveListener()) {
				try {
					ThreadUtil.invokeInThread(listener, "makingMove",
							new Object[] { position, move });
				} catch (Throwable e) {
					throw new RuntimeException(e.toString());
				}
			} else {
				listener.makingMove(position, move);
			}
		}
	}

	private void moveCompleted(Position position, Move move) {
		for (Iterator i = moveListeners.iterator(); i.hasNext();) {
			MoveListener listener = (MoveListener) i.next();
			if (isForkingMoveListener()) {
				try {
					ThreadUtil.invokeInThread(listener, "moveMade",
							new Object[] { position, move });
				} catch (Throwable e) {
					throw new RuntimeException(e.toString());
				}
			} else {
				listener.moveMade(position, move);
			}
		}
	}

	private void illegalMove(Position position,
			IllegalMoveException illegalMoveException) {
		for (Iterator i = moveListeners.iterator(); i.hasNext();) {
			MoveListener listener = (MoveListener) i.next();
			if (isForkingMoveListener()) {
				try {
					ThreadUtil.invokeInThread(listener, "moveRejected",
							new Object[] { position, illegalMoveException });
				} catch (Throwable e) {
					throw new RuntimeException(e.toString());
				}
			} else {
				listener.moveRejected(position, illegalMoveException);
			}
		}
	}

	public Object clone() {
		try {
			synchronized (this) {
				return super.clone();
			}
		}
		// should never happen since Position is cloneable.
		catch (CloneNotSupportedException cnse) {
			throw new RuntimeException(cnse.toString());
		}
	}
}