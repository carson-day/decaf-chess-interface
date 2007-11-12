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

/**
 * A Listener for chess moves.
 */
public interface MoveListener {
	/**
	 * Invoked when a move is being attempted.
	 * 
	 * @param position
	 *            The position before the move is made.
	 * @param move
	 *            The move being made.
	 */
	public void makingMove(Position position, Move move);

	/**
	 * Invoked after a move is successfully made.
	 * 
	 * @param position
	 *            The position after the move.
	 * @param move
	 *            The last move made.
	 */
	public void moveMade(Position position, Move move);

	/**
	 * Invoked when an illegalMove is made.
	 * 
	 * @param position
	 *            The position before the illegal move.
	 * @param reason
	 *            The Exception containing the reason why it was illegal.
	 */
	public void moveRejected(Position position, IllegalMoveException reason);
}