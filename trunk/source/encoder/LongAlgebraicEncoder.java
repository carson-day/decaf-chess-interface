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

package decaf.encoder;

import decaf.moveengine.Move;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;

/**
 * A Long Algebraic move encoder.
 */
public class LongAlgebraicEncoder implements MoveEncoder {
	private static final String CASTLE_KINGSIDE = "O-O";

	private static final String CASTLE_QUEENSIDE = "O-O-O";

	private static final String CAPTURE = "x";

	private static final String MOVE = "-";

	private static final String EN_PASSANT = "ep";

	/**
	 * coordinates[0] == rank coordinates[1] == file.
	 */
	public String encode(int[] coordinates) {
		return encode(coordinates[0], coordinates[1]);
	}

	public String encode(int rank, int file) {
		return "" + getAlgebraicFile(file) + getAlgebraicRank(rank);
	}

	public static int getAlgebraicRank(int rank) {
		// assert rank > -1 && rank < 8 : rank;
		return Math.abs(8 - rank);
	}

	public static int[] stringToCoordinates(String algebraicCoordinates) {
		// assert algebraicCoordinates != null : "algebraicCoordinates cant be
		// null";
		// assert algebraicCoordinates.length() == 2 : "algebraicCoordinates
		// must be 2 in length " + algebraicCoordinates;
		return new int[] { rankFromChar(algebraicCoordinates.charAt(1)),
				fileFromChar(algebraicCoordinates.charAt(0)) };
	}

	public static int rankFromChar(char rankCharacter) {
		switch (rankCharacter) {
		case '8':
			return 0;
		case '7':
			return 1;
		case '6':
			return 2;
		case '5':
			return 3;
		case '4':
			return 4;
		case '3':
			return 5;
		case '2':
			return 6;
		case '1':
			return 7;
		}
		throw new IllegalArgumentException("Invalid rank char: "
				+ rankCharacter);
	}

	public static int fileFromChar(char fileCharacter) {
		switch (fileCharacter) {
		case 'A':
		case 'a': {
			return 0;
		}
		case 'B':
		case 'b': {
			return 1;
		}
		case 'C':
		case 'c': {
			return 2;
		}
		case 'D':
		case 'd': {
			return 3;
		}
		case 'E':
		case 'e': {
			return 4;
		}
		case 'F':
		case 'f': {
			return 5;
		}
		case 'G':
		case 'g': {
			return 6;
		}
		case 'H':
		case 'h': {
			return 7;
		}
		}
		throw new IllegalArgumentException("Invalid file character: "
				+ fileCharacter);
	}

	public static char getAlgebraicFile(int file) {
		switch (file) {
		case 7: {
			return 'h';
		}
		case 6: {
			return 'g';
		}
		case 5: {
			return 'f';
		}
		case 4: {
			return 'e';
		}
		case 3: {
			return 'd';
		}
		case 2: {
			return 'c';
		}
		case 1: {
			return 'b';
		}
		case 0: {
			return 'a';
		}
		}
		// assert file > -1 && file < 8 : file;
		throw new IllegalArgumentException("Invalid file: " + file);
	}

	/**
	 * Completely ignores position.
	 */
	public String encode(Move move, Position position) {
		return move.isCastleKingside() ? CASTLE_KINGSIDE : move
				.isCastleQueenside() ? CASTLE_QUEENSIDE
				: move.isDropMove() ? pieceToString(move.getDroppedPiece())
						+ "@" + getAlgebraicFile(move.getEndCoordinates()[1])
						+ getAlgebraicRank(move.getEndCoordinates()[0]) : ""
						+ getAlgebraicFile(move.getStartFile())
						+ getAlgebraicRank(move.getStartRank())
						+ (move.isCapture() ? MOVE : MOVE)
						+ ""
						+ getAlgebraicFile(move.getEndFile())
						+ getAlgebraicRank(move.getEndRank())
						+ (move.getPromotedPiece() != Piece.EMPTY ? "="
								+ pieceToString(move.getPromotedPiece()) : "");
	}

	public Move decode(String moveString, Position position) {
		// TO DO: add in decoding of drop moves.

		Move result = null;
		if (moveString.equalsIgnoreCase(CASTLE_KINGSIDE)) {
			result = new Move(true, position.isWhitesMove());
		} else if (moveString.equalsIgnoreCase(CASTLE_QUEENSIDE)) {
			result = new Move(false, position.isWhitesMove());
		} else if (moveString.length() < 5) {
			throw new IllegalArgumentException("Invalid move length "
					+ moveString.length() + " " + moveString);
		} else if (moveString.length() > 5 && !moveString.endsWith(EN_PASSANT)) {
			throw new IllegalArgumentException(
					"Move cant be longer than 5 characters if it doesnt end with "
							+ EN_PASSANT + " " + moveString);
		} else {
			int[] startCoordinates = stringToCoordinates(moveString.substring(
					0, 2));
			int[] endCoordinates = stringToCoordinates(moveString.substring(3,
					5));

			result = Move
					.createMove(startCoordinates, endCoordinates, position);
		}
		return result;
	}

	public String pieceToString(int piece) {
		switch (piece) {
		case Piece.WP:
		case Piece.BP: {
			return "p";
		}
		case Piece.WN:
		case Piece.BN: {
			return "n";
		}
		case Piece.WB:
		case Piece.BB: {
			return "b";
		}
		case Piece.WR:
		case Piece.BR: {
			return "r";
		}
		case Piece.WQ:
		case Piece.BQ: {
			return "q";
		}
		default: {
			throw new IllegalArgumentException("Invalid drop piece: " + piece);
		}
		}
	}
}