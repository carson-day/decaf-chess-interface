/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Carson Day (carsonday@gmail.com)
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

import decaf.util.PieceUtil;

/**
 * Black pieces are lower case and white pieces are upper case.
 * 
 * <pre>
 *                
 *                 
 *                  
 *                   
 *                    
 *                     
 *                     --- --- --- --- --- --- --- ---
 *                     : r : n : b : q : k : b : n : r :   Whites  move.
 *                     --- --- --- --- --- --- --- ---
 *                     : p : p : p : p : p : p : p : p :   White can castle  Kingside  Queenside
 *                     --- --- --- --- --- --- --- ---
 *                     :   :   :   :   :   :   :   :   :   Black can castle  Kingside  Queenside
 *                     --- --- --- --- --- --- --- ---
 *                     :   :   :   :   :   :   :   :   :
 *                     --- --- --- --- --- --- --- ---
 *                     :   :   :   :   :   :   :   :   :
 *                     --- --- --- --- --- --- --- ---
 *                     :   :   :   :   :   :   :   :   :
 *                     --- --- --- --- --- --- --- ---
 *                     : P : P : P : P : P : P : P : P :
 *                     --- --- --- --- --- --- --- ---
 *                     : R : N : B : Q : K : B : N : R :
 *                     --- --- --- --- --- --- --- ---
 *                     
 *                     
 *                    
 *                   
 *                  
 *                 
 * </pre>
 */
public class AsciiPositionEncoder implements PositionEncoder {

	private static final String NEWLINE = System.getProperty("line.separator");

	private static final String RANK_SEPARATOR = " --- --- --- --- --- --- --- ---";

	private static final String FILE_SEPARATOR = ":";

	public String encode(Position position) {
		String result = NEWLINE + RANK_SEPARATOR + NEWLINE + FILE_SEPARATOR;
		String toMove = (position.isWhitesMove() ? "   Whites " : "   Blacks")
				+ " move.";
		String whiteCastling = "   White can castle "
				+ (position.whiteCanCastleKingside() ? " Kingside " : "")
				+ (position.whiteCanCastleQueenside() ? " Queenside " : "");
		String blackCastling = "   Black can castle "
				+ (position.blackCanCastleKingside() ? " Kingside " : "")
				+ (position.blackCanCastleQueenside() ? " Queenside " : "");
		String doublePawnPush = (position.wasLastMoveDoublePawnPush() ? "   Last move was double pawn push on the "
				+ LongAlgebraicEncoder.getAlgebraicFile(position
						.getLastMoveDoublePawnPushFile()) + " file."
				: "");
		String whiteHoldings = "   White holdings "
				+ pieceArrayToString(position.getWhiteHoldings());
		String blackHoldings = "   Black holdings "
				+ pieceArrayToString(position.getBlackHoldings());
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				result += " " + encode(position.get(i, j)) + " "
						+ FILE_SEPARATOR;
			}
			switch (i) {
			case 0: {
				result += toMove;
				break;
			}
			case 1: {
				result += whiteCastling;
				break;
			}
			case 2: {
				result += blackCastling;
				break;
			}
			case 3: {
				result += doublePawnPush;
				break;
			}
			case 4: {
				result += whiteHoldings;
				break;
			}
			case 5: {
				result += blackHoldings;
				break;
			}
			}
			result += NEWLINE + RANK_SEPARATOR + NEWLINE
					+ (i != 7 ? FILE_SEPARATOR : "");
		}
		return result;
	}

	public String pieceArrayToString(int[] pieceArray) {
		String result = "";
		if (pieceArray != null) {
			for (int i = 0; i < pieceArray.length; i++) {
				result += PieceUtil.getDefaultPiece(pieceArray[i]);
			}
		}
		return result;
	}

	public String encode(int[][] board) {
		String result = NEWLINE + RANK_SEPARATOR + NEWLINE + FILE_SEPARATOR;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				result += " " + encode(board[i][j]) + " " + FILE_SEPARATOR;
			}
			result += NEWLINE + RANK_SEPARATOR + NEWLINE
					+ (i != 7 ? FILE_SEPARATOR : "");
		}
		return result;
	}

	public String encode(int piece) {
		// assert chess.util.PieceUtil.isValid(piece) : piece;

		switch (piece) {
		case decaf.util.PieceUtil.EMPTY: {
			return " ";
		}

		case decaf.util.PieceUtil.WP: {
			return "P";
		}

		case decaf.util.PieceUtil.BP: {
			return "p";
		}
		case decaf.util.PieceUtil.WN: {
			return "N";
		}
		case decaf.util.PieceUtil.BN: {
			return "n";
		}
		case decaf.util.PieceUtil.WB: {
			return "B";
		}
		case decaf.util.PieceUtil.BB: {
			return "b";
		}
		case decaf.util.PieceUtil.WR: {
			return "R";
		}
		case decaf.util.PieceUtil.BR: {
			return "r";
		}
		case decaf.util.PieceUtil.WQ: {
			return "Q";
		}
		case decaf.util.PieceUtil.BQ: {
			return "q";
		}
		case decaf.util.PieceUtil.WK: {
			return "K";
		}
		case decaf.util.PieceUtil.BK: {
			return "k";
		}
		}
		throw new IllegalStateException("Invalid piece " + piece);
	}
}