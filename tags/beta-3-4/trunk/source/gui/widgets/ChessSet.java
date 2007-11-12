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
package decaf.gui.widgets;

import java.io.Serializable;

import decaf.moveengine.Piece;


// Referenced classes of package caffeine.gui.widgets:
// Skinnable, Skin, ChessPiece

public abstract class ChessSet implements Piece, Serializable {

	public ChessSet() {

	}

	public boolean equals(Object o) {
		if (o != null) {
			return getDescription().equals(((ChessSet) o).getDescription());
		} else {
			return false;
		}

	}

	public int hashCode() {
		return getDescription().hashCode();
	}

	public abstract String getDescription();

	public abstract ChessSet cloneChessSet();

	public ChessPiece createChessPiece(int i) {
		ChessPiece chesspiece = null;
		switch (i) {
		case Piece.WP:
			chesspiece = createLightPawn();
			break;

		case Piece.BP:
			chesspiece = createDarkPawn();
			break;

		case Piece.WN:
			chesspiece = createLightKnight();
			break;

		case Piece.BN:
			chesspiece = createDarkKnight();
			break;

		case Piece.WB:
			chesspiece = createLightBishop();
			break;

		case Piece.BB:
			chesspiece = createDarkBishop();
			break;

		case Piece.WR:
			chesspiece = createLightRook();
			break;

		case Piece.BR:
			chesspiece = createDarkRook();
			break;

		case Piece.WQ:
			chesspiece = createLightQueen();
			break;

		case Piece.BQ:
			chesspiece = createDarkQueen();
			break;

		case Piece.WK:
			chesspiece = createLightKing();
			break;

		case Piece.BK:
			chesspiece = createDarkKing();
			break;

		default:
			throw new RuntimeException("Invalid piece type " + i);
		}
		return chesspiece;
	}

	public abstract ChessPiece createLightPawn();

	public abstract ChessPiece createDarkPawn();

	public abstract ChessPiece createLightRook();

	public abstract ChessPiece createDarkRook();

	public abstract ChessPiece createLightKnight();

	public abstract ChessPiece createDarkKnight();

	public abstract ChessPiece createLightBishop();

	public abstract ChessPiece createDarkBishop();

	public abstract ChessPiece createLightQueen();

	public abstract ChessPiece createDarkQueen();

	public abstract ChessPiece createLightKing();

	public abstract ChessPiece createDarkKing();

}