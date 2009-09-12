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

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import decaf.util.CoordinatesUtil;
import decaf.util.PieceUtil;
import decaf.util.PropertiesUtil;

/**
 * An immutable thread safe class describing a chess position. Position is laid
 * out like this:
 * 
 * <pre>
 *                
 *                 
 *                  
 *                   
 *                    
 *                       8  [0] 00 01 02 03 04 05 06 07
 *                       7  [1] 00 01 02 03 04 05 06 07
 *                       6  [2] 00 01 02 03 04 05 06 07
 *                       5  [3] 00 01 02 03 04 05 06 07
 *                       4  [4] 00 01 02 03 04 05 06 07
 *                       3  [5] 00 01 02 03 04 05 06 07
 *                       2  [6] 00 01 02 03 04 05 06 07
 *                       1  [7] 00 01 02 03 04 05 06 07
 *                     
 *                              a  b  c  d  e  f  g  h
 *                     
 *                      e.g. [0][0] = a8, [0][7] = h8 , [7][7] = h1
 *                     
 *                     
 *                    
 *                   
 *                  
 *                 
 * </pre>
 * 
 * Coordinates contains algebraic coordinates into the above position for
 * convinence.
 */
public class Position implements Cloneable, Serializable, Piece, Coordinates {

	private static final Logger LOGGER = Logger.getLogger(Position.class);

	static final PositionEncoder DEFAULT_ENCODER = new AsciiPositionEncoder();

	private int[][] board;

	private int castleState;

	private int lastDoublePawnPushFile = -1;

	private boolean isWhitesMove;

	private boolean isInCheck;

	private boolean isInCheckSet;

	private boolean isInCheckMate;

	private boolean isInCheckMateSet;

	private boolean isInStaleMate;

	private boolean isInStaleMateSet;

	private int[] whiteKingCoordinates;

	private int[] blackKingCoordinates;

	private Move[] legalMoves;

	private Map<Move, Position> moveToPosition;

	private int[] whitesHoldings;

	private int[] blacksHoldings;

	public static final int[][] INITIAL_POSITION = {
			new int[] { PieceUtil.BLACK_ROOK, PieceUtil.BLACK_KNIGHT,
					PieceUtil.BLACK_BISHOP, PieceUtil.BLACK_QUEEN,
					PieceUtil.BLACK_KING, PieceUtil.BLACK_BISHOP,
					PieceUtil.BLACK_KNIGHT, PieceUtil.BLACK_ROOK },
			new int[] { PieceUtil.BLACK_PAWN, PieceUtil.BLACK_PAWN,
					PieceUtil.BLACK_PAWN, PieceUtil.BLACK_PAWN,
					PieceUtil.BLACK_PAWN, PieceUtil.BLACK_PAWN,
					PieceUtil.BLACK_PAWN, PieceUtil.BLACK_PAWN },
			new int[] { PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY },
			new int[] { PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY },
			new int[] { PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY },
			new int[] { PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY, PieceUtil.EMPTY,
					PieceUtil.EMPTY, PieceUtil.EMPTY },
			new int[] { PieceUtil.WHITE_PAWN, PieceUtil.WHITE_PAWN,
					PieceUtil.WHITE_PAWN, PieceUtil.WHITE_PAWN,
					PieceUtil.WHITE_PAWN, PieceUtil.WHITE_PAWN,
					PieceUtil.WHITE_PAWN, PieceUtil.WHITE_PAWN },
			new int[] { PieceUtil.WHITE_ROOK, PieceUtil.WHITE_KNIGHT,
					PieceUtil.WHITE_BISHOP, PieceUtil.WHITE_QUEEN,
					PieceUtil.WHITE_KING, PieceUtil.WHITE_BISHOP,
					PieceUtil.WHITE_KNIGHT, PieceUtil.WHITE_ROOK } };

	public static final int[][] EMPTY_POSITION = {
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL },
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL },
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL },
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL },
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL },
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL },
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL },
			new int[] { PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL, PieceUtil.NILL,
					PieceUtil.NILL, PieceUtil.NILL }, };

	/**
	 * Constructs the begining position of a chess game.
	 */
	public Position() {
		setCastleState(true, true, true, true);
		setLastMoveDoublePawnPushFile(-1);
		setBoard(INITIAL_POSITION);
		setWhitesMove(true);
	}

	public Position(int[][] board, boolean whiteCanCastleKingside,
			boolean whiteCanCastleQueenside, boolean blackCanCastleKingside,
			boolean blackCanCastleQueenside, int lastMoveDoublePawnPushFile,
			boolean isWhitesMove) {
		setBoard(board);
		setCastleState(whiteCanCastleKingside, whiteCanCastleQueenside,
				blackCanCastleKingside, blackCanCastleQueenside);
		setLastMoveDoublePawnPushFile(lastMoveDoublePawnPushFile);
		setWhitesMove(isWhitesMove);
	}

	/**
	 * Returns a deep copy of the position.
	 */
	public boolean wasLastMoveDoublePawnPush() {
		return lastDoublePawnPushFile != -1;
	}

	public boolean whiteCanCastleQueenside() {
		return (castleState & PositionUtil.WHITE_CAN_CASTLE_QUEENSIDE_STATE) != 0;
	}

	public boolean whiteCanCastleKingside() {
		return (castleState & PositionUtil.WHITE_CAN_CASTLE_KINGSIDE_STATE) != 0;
	}

	public boolean blackCanCastleQueenside() {
		return (castleState & PositionUtil.BLACK_CAN_CASTLE_QUEENSIDE_STATE) != 0;
	}

	public boolean blackCanCastleKingside() {
		return (castleState & PositionUtil.BLACK_CAN_CASTLE_KINGSIDE_STATE) != 0;
	}

	public int[] getBlackHoldings() {
		return blacksHoldings;
	}

	public int[] getWhiteHoldings() {
		return whitesHoldings;
	}

	public void addBlackHolding(int piece) {
		if (blacksHoldings == null) {
			PieceUtil.assertValidBlackDropPiece(piece);
			blacksHoldings = new int[] { piece };
		} else {
			int[] blackHoldingsOld = blacksHoldings;
			blacksHoldings = new int[blacksHoldings.length + 1];
			for (int i = 0; i < blackHoldingsOld.length; i++) {
				blacksHoldings[i] = blackHoldingsOld[i];
			}
			blacksHoldings[blacksHoldings.length - 1] = piece;
		}
	}

	public void addWhiteHolding(int piece) {
		if (whitesHoldings == null) {
			PieceUtil.assertValidWhiteDropPiece(piece);
			whitesHoldings = new int[] { piece };
		} else {
			int[] whiteHoldingsOld = whitesHoldings;
			whitesHoldings = new int[whitesHoldings.length + 1];
			for (int i = 0; i < whiteHoldingsOld.length; i++) {
				whitesHoldings[i] = whiteHoldingsOld[i];
			}
			whitesHoldings[whitesHoldings.length - 1] = piece;
		}
	}

	public void setBlackHoldings(int[] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			PieceUtil.assertValidBlackDropPiece(pieces[i]);
		}
		blacksHoldings = pieces;
	}

	public void setWhiteHoldings(int[] pieces) {
		for (int i = 0; i < pieces.length; i++) {
			PieceUtil.assertValidWhiteDropPiece(pieces[i]);
		}
		whitesHoldings = pieces;
	}

	/**
	 * Returns -1 if the last move was not a pawn push, otherwise returns the
	 * file of the pawn push.
	 */
	public int getLastMoveDoublePawnPushFile() {
		return lastDoublePawnPushFile;
	}

	public boolean isWhitesMove() {
		return isWhitesMove;
	}

	public void isValid(Move move) throws IllegalMoveException {
		if (isWhitesMove() != move.isWhitesMove()) {
			throw new IllegalMoveException(move, this,
					"It is not the players turn.", null);
		}

		if (isWhitesMove()) {
			if (move.isCastleQueenside() && !whiteCanCastleQueenside()) {
				throw new IllegalMoveException(move, this,
						"White can not castle queenside.", null);
			} else if (move.isCastleKingside() && !whiteCanCastleKingside()) {
				throw new IllegalMoveException(move, this,
						"White can not castle kingside.", null);
			}
		} else {
			if (move.isCastleQueenside() && !blackCanCastleQueenside()) {
				throw new IllegalMoveException(move, this,
						"Black can not castle queenside.", null);
			} else if (move.isCastleKingside() && !blackCanCastleKingside()) {
				throw new IllegalMoveException(move, this,
						"Black can not castle kingside.", null);
			}
		}

		if (isCheckmate() || isStalemate()) {
			throw new IllegalMoveException(move, this,
					"ChessGame is already over.", null);
		}

		Move[] possibleMoves = getLegalMoves();

		if (!contains(possibleMoves, move)) {
			throw new IllegalMoveException(move, this, move.toString()
					+ " is not a possible move." + "\n Legal moves: "
					+ PositionUtil.dumpMoves(possibleMoves), null);
		}
	}

	private boolean contains(Move[] array, Move move) {
		boolean result = false;
		for (int i = 0; !result && i < array.length; i++) {
			result = array[i].equals(move);
		}
		return result;
	}

	public boolean isCheck() {
		synchronized (this) {
			synchronized (this) {
				if (!isInCheckSet) {
					// Run all pseudo methods on the kings square for the
					// opposite color.
					isInCheckSet = true;
					isInCheck = PositionUtil.isInCheck(board, PositionUtil
							.getKingCoordinates(this.isWhitesMove(), board),
							this.isWhitesMove());
				}
			}
		}
		return isInCheck;
	}

	private void setToBoardState(int boardState) {

		int dpPawnPushFile = PositionUtil.stateToLastDPPush(boardState);
		setLastMoveDoublePawnPushFile(dpPawnPushFile);
		setCastleState(
				(boardState & PositionUtil.WHITE_CAN_CASTLE_KINGSIDE_STATE) != 0,
				(boardState & PositionUtil.WHITE_CAN_CASTLE_QUEENSIDE_STATE) != 0,
				(boardState & PositionUtil.BLACK_CAN_CASTLE_KINGSIDE_STATE) != 0,
				(boardState & PositionUtil.BLACK_CAN_CASTLE_QUEENSIDE_STATE) != 0);
		setWhitesMove((boardState & PositionUtil.IS_WHITES_MOVE_STATE) != 0);
	}

	private void setCastleState(boolean whiteCanCastleKingside,
			boolean whiteCanCastleQueenside, boolean blackCanCastleKingside,
			boolean blackCanCastleQueenside) {
		castleState = 0;
		if (whiteCanCastleQueenside)
			castleState |= PositionUtil.WHITE_CAN_CASTLE_QUEENSIDE_STATE;
		if (whiteCanCastleKingside)
			castleState |= PositionUtil.WHITE_CAN_CASTLE_KINGSIDE_STATE;
		if (blackCanCastleQueenside)
			castleState |= PositionUtil.BLACK_CAN_CASTLE_QUEENSIDE_STATE;
		if (blackCanCastleKingside)
			castleState |= PositionUtil.BLACK_CAN_CASTLE_KINGSIDE_STATE;
	}

	public int getBoardState() {
		int result = 0;
		result |= castleState;

		if (isWhitesMove()) {
			result |= PositionUtil.IS_WHITES_MOVE_STATE;
		}

		switch (getLastMoveDoublePawnPushFile()) {
		case 0: {
			result |= PositionUtil.A_FILE_STATE;
			break;
		}
		case 1: {
			result |= PositionUtil.B_FILE_STATE;
			break;
		}
		case 2: {
			result |= PositionUtil.C_FILE_STATE;
			break;
		}
		case 3: {
			result |= PositionUtil.D_FILE_STATE;
			break;
		}
		case 4: {
			result |= PositionUtil.E_FILE_STATE;
			break;
		}
		case 5: {
			result |= PositionUtil.F_FILE_STATE;
			break;
		}
		case 6: {
			result |= PositionUtil.G_FILE_STATE;
			break;
		}
		case 7: {
			result |= PositionUtil.H_FILE_STATE;
			break;
		}
		}
		return result;
	}

	public boolean isCheckmate() {
		synchronized (this) {
			synchronized (this) {
				if (!isInCheckMateSet) {
					isInCheckMateSet = true;
					isInCheckMate = isCheck() && getLegalMoves().length == 0;
				}
			}

		}
		return isInCheckMate;
	}

	public boolean isStalemate() {
		synchronized (this) {
			synchronized (this) {
				if (!isInStaleMateSet) {
					isInStaleMateSet = true;
					isInStaleMate = !isCheck() && getLegalMoves().length == 0;
				}
			}
		}
		return isInStaleMate;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Position))
			return false;

		final Position chessPosition = (Position) o;

		if (!boardEquals(chessPosition.board))
			return false;
		if (castleState != chessPosition.castleState)
			return false;
		if (isWhitesMove != chessPosition.isWhitesMove)
			return false;
		if (lastDoublePawnPushFile != chessPosition.lastDoublePawnPushFile)
			return false;
		return true;
	}

	public int hashCode() {
		int result = castleState;
		result = 29 * result + lastDoublePawnPushFile;
		result = 29 * result + (isWhitesMove ? 1 : 0);
		return result;
	}

	public String toString() {
		return DEFAULT_ENCODER.encode(this);
	}

	/**
	 * The equivalent of this.oppositeRanks().oppositeFiles().
	 */
	public Position opposite() {
		Position result = (Position) clone();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				result
						.setInternal(CoordinatesUtil.getOpposite(i, j), get(i,
								j));
			}
		}
		result.removeCaching();
		return result;
	}

	/**
	 * Position formed if all of the ranks became their opposite. e.g. 1 -> 8, 2 ->
	 * 7, ...
	 */
	public Position oppositeRanks() {
		Position result = (Position) clone();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				int oppositeRank = CoordinatesUtil.getOppositeRank(i);
				result.setInternal(oppositeRank, j, get(i, j));
			}
		}
		result.removeCaching();
		return result;
	}

	/**
	 * Position formed if all of the files became their opposite. e.g. a->h,
	 * b->g , ...
	 */
	public Position oppositeFiles() {
		Position result = (Position) clone();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				int oppositeFile = CoordinatesUtil.getOppositeFile(j);
				result.setInternal(i, oppositeFile, get(i, j));
			}
		}
		result.removeCaching();
		return result;
	}

	/**
	 * Changes all pieces to the piece of the opposite color.
	 */
	public Position reversePieces() {
		Position result = (Position) clone();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				result.board[i][j] = PieceUtil.getOpposite(board[i][j]);
			}
		}
		result.removeCaching();
		return result;
	}

	/**
	 * Returns a position where result.isWhitesMove() = !this.isWhitesMove().
	 * 
	 * @return
	 */
	public Position reverseToMove() {
		Position result = (Position) clone();
		result.isWhitesMove = !isWhitesMove;
		result.removeCaching();
		return result;
	}

	public Object clone() {
		try {
			Position result = (Position) super.clone();
			result.board = getDeepCopyOfBoard();
			return result;
		} catch (CloneNotSupportedException cnse) {
			throw new RuntimeException(cnse.toString());
		}
	}

	/**
	 * Returns board[coordinates[0]][coordinates[1]]
	 * 
	 * @return
	 */
	public int get(int[] coordinates) {
		CoordinatesUtil.assertValid(coordinates);
		return board[coordinates[0]][coordinates[1]];
	}

	/**
	 * Returns board[rank][file]
	 * 
	 * @return
	 */
	public int get(int rank, int file) {
		CoordinatesUtil.assertInBounds(rank, file);
		return board[rank][file];
	}

	/**
	 * Returns a list of all of the legal moves from this position.
	 */
	public Move[] getLegalMoves() {

			legalMoves = PositionUtil.getLegalMoves(board, getBoardState(),
					isWhitesMove() ? getWhiteHoldings() : getBlackHoldings());

		return legalMoves;
	}

	/**
	 * Returns a deep copy of the board used in this position.
	 */
	public int[][] getBoard() {
		return getDeepCopyOfBoard();
	}

	public Position makeMove(Move move) throws IllegalMoveException {
		return makeMove(move, true);
	}

	/**
	 * Returns a position with no pieces on the board. The position is set to
	 * whitesMove and castling priviledges are true for white and black.
	 * 
	 * @return
	 */
	public static Position getEmpty() {
		return new Position(EMPTY_POSITION, true, true, true, true, -1, true);
	}

	/**
	 * Similar to set(coordinates,piece) but allows for multiple pieces to be
	 * set at one time. coordinatesArray contains all of the coordinates and
	 * pieceArray contains the coresponding pieces to set.
	 * 
	 * @return
	 */
	public Position set(int[][] coordinatesArray, int[] pieceArray) {
		if (coordinatesArray == null) {
			throw new IllegalArgumentException("coordinatesArray can't be null");
		} else if (pieceArray == null) {
			throw new IllegalArgumentException("pieceArray can't be null.");
		} else if (coordinatesArray.length != pieceArray.length) {
			throw new IllegalArgumentException(
					"coordinatesArray and pieceArray must be  the same size.");
		}
		Position result = (Position) clone();
		for (int i = 0; i < coordinatesArray.length; i++) {
			result.setInternal(coordinatesArray[i], pieceArray[i]);
		}
		result.removeCaching();
		return result;
	}

	/**
	 * Creates a new position from this position with the specified coordiantes
	 * set to piece.
	 */
	public Position set(int[] coordinates, int piece) {
		CoordinatesUtil.assertValid(coordinates);
		PieceUtil.assertValid(piece);

		Position result = (Position) clone();
		result.setInternal(coordinates, piece);
		result.removeCaching();
		return result;
	}

	/**
	 * Creates a new position from this position with the specified coordiantes
	 * set to piece.
	 */
	public Position set(int rank, int file, int piece) {
		Position result = (Position) clone();
		result.setInternal(rank, file, piece);
		result.removeCaching();
		return result;
	}

	public Position makeMove(Move move, boolean isValidating)
			throws IllegalMoveException {
		Position result = null;
		
		if (isValidating) {
			isValid(move);
		}
		result = (Position) clone();
		result.removeCaching();

		result.setToBoardState(PositionUtil.makeMove(result.board, result
				.getBoardState(), move, true));
		return result;
	}

	public int getNumPieces(int chessPiece) {
		int result = 0;

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (board[i][j] == chessPiece) {
					result++;
				}
			}
		}
		return result;
	}

	public int[] getWhiteCapturedPieces() {
		int numRooks = getNumPieces(Piece.WR);
		int numPawns = getNumPieces(Piece.WP);
		int numBishops = getNumPieces(Piece.WB);
		int numQueens = getNumPieces(Piece.WQ);
		int numKnights = getNumPieces(Piece.WN);

		List<Integer> result = new LinkedList<Integer>();
		for (int i = 0; i < 8 - numPawns; i++) {
			result.add(Piece.WP);
		}
		for (int i = 0; i < 2 - numRooks; i++) {
			result.add(Piece.WR);
		}
		for (int i = 0; i < 2 - numBishops; i++) {
			result.add(Piece.WB);
		}
		for (int i = 0; i < 2 - numKnights; i++) {
			result.add(Piece.WB);
		}
		if (numQueens == 0) {
			result.add(Piece.WQ);
		}

		int[] resultArray = new int[result.size()];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = result.get(i);
		}
		return resultArray;

	}

	public int[] getBlackCapturedPieces() {
		int numRooks = getNumPieces(Piece.BR);
		int numPawns = getNumPieces(Piece.BP);
		int numBishops = getNumPieces(Piece.BB);
		int numQueens = getNumPieces(Piece.BQ);
		int numKnights = getNumPieces(Piece.BN);

		List<Integer> result = new LinkedList<Integer>();
		for (int i = 0; i < 8 - numPawns; i++) {
			result.add(Piece.BP);
		}
		for (int i = 0; i < 2 - numRooks; i++) {
			result.add(Piece.BR);
		}
		for (int i = 0; i < 2 - numBishops; i++) {
			result.add(Piece.BB);
		}
		for (int i = 0; i < 2 - numKnights; i++) {
			result.add(Piece.BB);
		}
		if (numQueens == 0) {
			result.add(Piece.BQ);
		}

		int[] resultArray = new int[result.size()];
		for (int i = 0; i < resultArray.length; i++) {
			resultArray[i] = result.get(i);
		}
		return resultArray;
	}

	/**
	 * Sets position[coordinates[0]][coordinates[1]] to chessPiece.
	 * 
	 * @param coordinates
	 * @param chessPiece
	 */
	private void setInternal(int[] coordinates, int chessPiece) {
		// assert coordinates != null : "coordiantes cant be null";
		// assert coordinates.length == 2 : "coordinates must be 2 in length";
		setInternal(coordinates[0], coordinates[1], chessPiece);
	}

	/**
	 * Sets position[rank][file] to chessPiece.
	 */
	private void setInternal(int rank, int file, int chessPiece) {
		// assert PieceUtil.isValid(chessPiece) : "Invalid chess piece: " +
		// chessPiece;
		// assert CoordinatesUtil.isInBounds(rank , file) : "coordinates must be
		// in bounds.";
		board[rank][file] = chessPiece;
	}

	private void removeCaching() {
		isInCheckSet = false;
		isInCheckMateSet = false;
		isInStaleMateSet = false;
		legalMoves = null;
		moveToPosition = null;
		if (blackKingCoordinates != null) {
			blackKingCoordinates = get(blackKingCoordinates) == BK ? blackKingCoordinates
					: null;
		}
		if (whiteKingCoordinates != null) {
			whiteKingCoordinates = get(whiteKingCoordinates) == WK ? whiteKingCoordinates
					: null;
		}
	}

	/**
	 * Returns a deep copy of the position.
	 */
	private int[][] getDeepCopyOfBoard() {
		int[][] result = new int[8][8];
		for (int i = 0; i < 8; i++) {
			System.arraycopy(board[i], 0, result[i] = new int[8], 0, 8);
		}
		return result;
	}

	private void setBoard(int[][] board) {
		if (board == null) {
			throw new IllegalArgumentException("board cant be null");
		} else if (board.length != 8) {
			throw new IllegalArgumentException(
					"Invalid number of ranks on board. "
							+ PositionUtil.dumpBoard(board));
		}

		for (int i = 0; i < board.length; i++) {
			if (board[i].length != 8) {
				throw new IllegalArgumentException("Invalid number of files "
						+ board[i].length + " on rank " + i
						+ PositionUtil.dumpBoard(board));
			}
			for (int j = 0; j < board.length; j++) {
				if (!PieceUtil.isValid(board[i][j])) {
					throw new IllegalArgumentException(
							"Invalid chess piece encountered at rank " + i
									+ " file " + j
									+ PositionUtil.dumpBoard(board));
				}
			}
		}

		this.board = board;
	}

	private void setLastMoveDoublePawnPushFile(int lastMoveDoublePawnPushFile) {

		if (lastMoveDoublePawnPushFile < -1 || lastMoveDoublePawnPushFile > 7) {
			throw new IllegalArgumentException("Invalid pawn file "
					+ lastMoveDoublePawnPushFile);
		}
		lastDoublePawnPushFile = lastMoveDoublePawnPushFile;
	}

	/**
	 * Returns true if this position is equivalent to board.
	 */
	private boolean boardEquals(int[][] board) {
		// assert board != null : "Board cant be null";
		// assert board.length == 8 : "Board must have 8 ranks";
		boolean result = true;
		for (int i = 0; result && i < 8; i++) {
			// assert board[i] != null : "Board cant have null files";
			// assert board[i].length == 8: "Board must have 8 files";

			for (int j = 0; j < 8; j++) {
				// assert PieceUtil.isValid(board[i][j]) : "Board contains an
				// invalid piece: " + board[i][j];
				result = get(i, j) == board[i][j];
			}
		}
		return result;
	}

	private void setWhitesMove(boolean isWhitesMove) {
		this.isWhitesMove = isWhitesMove;
	}

}