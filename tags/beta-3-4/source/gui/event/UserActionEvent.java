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
package decaf.gui.event;

import decaf.gui.util.CoordinatesUtil;
import decaf.gui.widgets.ChessBoardSquare;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;

public class UserActionEvent {
	private int[] startCoordinates;

	private int[] endCoordinates;

	private int[] hotSquareCoordinates;

	private int dropPiece;

	private String boardId;

	public UserActionEvent(ChessBoardSquare startSquare,
			ChessBoardSquare endSquare, String boardId) {
		if (startSquare.isDropSquare()) {
			dropPiece = startSquare.getDropPiece();
			endCoordinates = new int[] { endSquare.getRank(),
					endSquare.getFile() };
		} else {
			dropPiece = Piece.EMPTY;
			startCoordinates = new int[] { startSquare.getRank(),
					startSquare.getFile() };
			endCoordinates = new int[] { endSquare.getRank(),
					endSquare.getFile() };
		}
	}

	/**
	 * Returns true if this was a crazyhosue or bughouse piece drop.
	 */
	public boolean isDrop() {
		return dropPiece != Piece.EMPTY;
	}

	/**
	 * Returns the piece dropped if this is a crazyhosue or bughouse piece drop,
	 * otherwise returns emtpy.
	 */
	public int getDropPiece() {
		return dropPiece;
	}

	/**
	 * Returns 0 based rank and file. returns null if this was a dropEvent.
	 */
	public int[] getStartCoordinates() {
		return startCoordinates;
	}

	/**
	 * Returns 0 based rank and file.
	 */
	public int[] getEndCoordinates() {
		return endCoordinates;
	}

	/**
	 * @return Returns the boardId.
	 */
	public String getBoardId() {
		return boardId;
	}

	public boolean areStartAndEndSameSquare() {
		return !isDrop() && getStartCoordinates()[0] == getEndCoordinates()[0]
				&& getStartCoordinates()[1] == getEndCoordinates()[1];
	}

	public boolean isPromotion(Position position) {
		return (position.isWhitesMove()
				&& getEndCoordinates()[0] == CoordinatesUtil.RANK_8 && position
				.get(getStartCoordinates()) == Piece.WHITE_PAWN)
				|| (!position.isWhitesMove()
						&& getEndCoordinates()[0] == CoordinatesUtil.RANK_1 && position
						.get(getStartCoordinates()) == Piece.BLACK_PAWN);
	}
}