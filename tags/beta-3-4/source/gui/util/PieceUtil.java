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
package decaf.gui.util;

import decaf.encoder.AsciiPositionEncoder;
import decaf.encoder.PositionEncoder;
import decaf.moveengine.Piece;

/**
 * A class containing utility methods for chess pieces.
 */
public class PieceUtil implements Piece {

	private static final PositionEncoder DEFAULT_POSITION_ENCODER = new AsciiPositionEncoder();

	private PieceUtil() {
	}

	public static boolean isValid(int piece) {
		return piece >= 0 && piece < 13;
	}

	public static void assertValid(int piece) {
		if (!isValid(piece)) {
			throw new IllegalArgumentException("Invalid piece: " + piece);
		}
	}

	public static void assertValidAndNotEmpty(int piece) {
		assertValid(piece);
		if (piece == Piece.EMPTY) {
			throw new IllegalArgumentException("Piece is empty");
		}
	}

	/**
	 * Returns true if the piece specified is a valid drop piece in droppable
	 * chess (bughouse or crazyhouse).
	 */
	public static boolean assertValidWhiteDropPiece(int piece) {
		return piece == WP || piece == WN || piece == WB || piece == WR
				|| piece == WQ;
	}

	/**
	 * Returns true if the piece specified is a valid drop piece in droppable
	 * chess (bughouse or crazyhouse).
	 */
	public static boolean assertValidBlackDropPiece(int piece) {
		return piece == BP || piece == BN || piece == BB || piece == BR
				|| piece == BQ;
	}

	public static boolean containsPawn(int[] pieces) {
		boolean result = false;
		for (int i = 0; i < pieces.length; i++) {
			result = isPawn(pieces[i]);
		}
		return result;
	}

	public static boolean containsKnight(int[] pieces) {
		boolean result = false;
		for (int i = 0; i < pieces.length; i++) {
			result = isKnight(pieces[i]);
		}
		return result;

	}

	public static boolean containsRook(int[] pieces) {
		boolean result = false;
		for (int i = 0; i < pieces.length; i++) {
			result = isRook(pieces[i]);
		}
		return result;

	}

	public static boolean containsBishop(int[] pieces) {
		boolean result = false;
		for (int i = 0; i < pieces.length; i++) {
			result = isBishop(pieces[i]);
		}
		return result;

	}

	public static boolean containsQueen(int[] pieces) {
		boolean result = false;
		for (int i = 0; i < pieces.length; i++) {
			result = isQueen(pieces[i]);
		}
		return result;

	}

	public static boolean isWhitePiece(int piece) {
		assertValid(piece);
		return piece > 0 && piece < 7;
	}

	public static boolean isEmpty(int piece) {
		assertValid(piece);
		return piece == EMPTY;
	}

	public static boolean isBlackPiece(int piece) {
		assertValid(piece);
		return piece < 13 && piece > 6;
	}

	public static boolean isBishop(int piece) {
		assertValid(piece);
		return piece == WHITE_BISHOP || piece == BLACK_BISHOP;
	}

	public static boolean isPawn(int piece) {
		assertValid(piece);
		return piece == WHITE_PAWN || piece == BLACK_PAWN;
	}

	public static boolean isKnight(int piece) {
		assertValid(piece);
		return piece == WHITE_KNIGHT || piece == BLACK_KNIGHT;
	}

	public static boolean isRook(int piece) {
		assertValid(piece);
		return piece == WHITE_ROOK || piece == BLACK_ROOK;
	}

	public static boolean isQueen(int piece) {
		assertValid(piece);
		return piece == WHITE_QUEEN || piece == BLACK_QUEEN;
	}

	public static boolean isKing(int piece) {
		assertValid(piece);
		return piece == WHITE_KING || piece == BLACK_KING;
	}

	/**
	 * Returns true if piece is empty or the opposite color of isColorWhite.
	 */
	public static boolean isEmptyOrOpposite(int piece, boolean isColorWhite) {
		assertValid(piece);
		return isEmpty(piece) || isOpposite(piece, isColorWhite);
	}

	/**
	 * Returns true if piece is the opposite color of isColorWhite.
	 */
	public static boolean isOpposite(int piece, boolean isColorWhite) {
		assertValid(piece);
		return !isEmpty(piece) && (isColorWhite && isBlackPiece(piece))
				|| (!isColorWhite && isWhitePiece(piece));
	}

	/**
	 * Returns the piece of the opposite color or empty if piece is empty.
	 */
	public static int getOpposite(int piece) {
		assertValid(piece);
		return piece == EMPTY ? EMPTY : isWhitePiece(piece) ? piece + 6
				: piece - 6;
	}

	public static String getDefaultPiece(int piece) {
		if (isValid(piece))
			return DEFAULT_POSITION_ENCODER.encode(piece);
		else
			return "" + piece;
	}
}