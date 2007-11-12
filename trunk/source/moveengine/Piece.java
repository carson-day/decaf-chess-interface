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
 * A class containing chess piece constants.
 */
public interface Piece {
	// **WARNING* If you change any constant values you will effect the
	// methods PieceUtil.
	public static final int WHITE_PAWN = 1;

	public static final int WHITE_ROOK = 2;

	public static final int WHITE_KNIGHT = 3;

	public static final int WHITE_BISHOP = 4;

	public static final int WHITE_QUEEN = 5;

	public static final int WHITE_KING = 6;

	public static final int BLACK_PAWN = 7;

	public static final int BLACK_ROOK = 8;

	public static final int BLACK_KNIGHT = 9;

	public static final int BLACK_BISHOP = 10;

	public static final int BLACK_QUEEN = 11;

	public static final int BLACK_KING = 12;

	public static final int EMPTY = 0;

	public static final int WP = WHITE_PAWN;

	public static final int WR = WHITE_ROOK;

	public static final int WN = WHITE_KNIGHT;

	public static final int WB = WHITE_BISHOP;

	public static final int WQ = WHITE_QUEEN;

	public static final int WK = WHITE_KING;

	public static final int BP = BLACK_PAWN;

	public static final int BR = BLACK_ROOK;

	public static final int BN = BLACK_KNIGHT;

	public static final int BB = BLACK_BISHOP;

	public static final int BQ = BLACK_QUEEN;

	public static final int BK = BLACK_KING;

	public static final int NILL = EMPTY;
}