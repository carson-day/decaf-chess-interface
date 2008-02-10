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

import org.apache.log4j.Logger;

import decaf.util.CoordinatesUtil;
import decaf.util.PieceUtil;

public class PositionUtil implements Piece, Coordinates {
	private static final Logger LOGGER = Logger.getLogger(PositionUtil.class);

	// TO DO:
	// Add in drop piece holdings.

	// **************** MOVE GENERATION AND IS IN CHECK LOGIC
	// *********************
	// All attempts are made from this point on to make the code as efficent as
	// possible.
	// Ideas to try:
	// add in bit validations
	// Move to JNI for speed.
	// Remove Move object for easy JNI portability and speed.
	// Make isWhitesMove a parameter instead of calculating it from boardState
	// in many of the methods.

	// This number is inflated
	private static final int MAXIMUM_MOVES = 100;

	private static final int DROP_MAX_MOVES = MAXIMUM_MOVES + 320;

	public static final int WHITE_CAN_CASTLE_QUEENSIDE_STATE = 2;

	public static final int WHITE_CAN_CASTLE_KINGSIDE_STATE = 4;

	public static final int BLACK_CAN_CASTLE_QUEENSIDE_STATE = 8;

	public static final int BLACK_CAN_CASTLE_KINGSIDE_STATE = 16;

	public static final int IS_WHITES_MOVE_STATE = 32;

	public static final int A_FILE_STATE = 64;

	public static final int B_FILE_STATE = 128;

	public static final int C_FILE_STATE = 256;

	public static final int D_FILE_STATE = 512;

	public static final int E_FILE_STATE = 1024;

	public static final int F_FILE_STATE = 2048;

	public static final int G_FILE_STATE = 4096;

	public static final int H_FILE_STATE = 8192;

	// public static final int CHECK_STATE = 16384;
	// public static final int CHECKMATE_STATE = 32768;
	// public static final int STALEMATE_STATE = 65536;

	private static final int[][] WHITE_CASTLE_THRU_CHECK_KINGSIDE = new int[][] {
			E1, F1, G1 };

	private static final int[][] WHITE_CASTLE_THRU_CHECK_QUEENSIDE = new int[][] {
			E1, D1, C1 };

	private static final int[][] BLACK_CASTLE_THRU_CHECK_KINGSIDE = new int[][] {
			E8, F8, G8 };

	private static final int[][] BLACK_CASTLE_THRU_CHECK_QUEENSIDE = new int[][] {
			E8, D8, C8 };

	/**
	 * up/down 2 over 1 up/down 1 over 2
	 */
	private static final int[] N_RANK_PATTERN = { 2, 2, -2, -2, 1, 1, -1, -1 };

	/**
	 * up/down 2 over 1 up/down 1 over 2
	 */
	private static final int[] N_FILE_PATTERN = { 1, -1, 1, -1, 2, -2, 2, -2 };

	/**
	 * up over 1 down 1 over 1
	 */
	private static final int[] B_RANK_PATTERN = { 1, 1, -1, -1 };

	/**
	 * up over 1 down 1 over 1
	 */
	private static final int[] B_FILE_PATTERN = { 1, -1, 1, -1 };

	/**
	 * up over 0 down 1 over 0
	 */
	private static final int[] R_RANK_PATTERN = { 1, -1, 0, 0 };

	/**
	 * up over 1 down 1 over 1
	 */
	private static final int[] R_FILE_PATTERN = { 0, 0, 1, -1 };

	/**
	 * Bishop and rook combined
	 */
	private static final int[] Q_RANK_PATTERN = { 1, 1, -1, -1, 1, -1, 0, 0 };

	/**
	 * Bishop and rook combined
	 */
	private static final int[] Q_FILE_PATTERN = { 1, -1, 1, -1, 0, 0, 1, -1 };

	/**
	 * Same as queen.
	 */
	private static final int[] K_RANK_PATTERN = Q_RANK_PATTERN;

	/**
	 * Same as queen
	 */
	private static final int[] K_FILE_PATTERN = Q_FILE_PATTERN;

	/**
	 * Up 1 only. The rest of the moves are handled without patterns.
	 */
	private static final int[] BP_RANK_PATTERN = { 1 };

	/**
	 * Up 1 only. The rest of the moves are handled without patterns.
	 */
	private static final int[] WP_RANK_PATTERN = { -1 };

	/**
	 * up 1 only. Start move up 2,takes,and ep are dealt with as special cases.
	 */
	private static final int[] P_UP_1_FILE_PATTERN = { 0 };

	/**
	 * check to right and left.
	 */
	private static final int[] P_X_FILE_PATTERN = { 1, -1 };

	/**
	 * check down 1 right and left.
	 */
	private static final int[] WP_X_RANK_PATTERN = { -1, -1 };

	/**
	 * check up 1 right and left.
	 */
	private static final int[] BP_X_RANK_PATTERN = { 1, 1 };

	private static final int[] BLACK_LEGAL_CONTENTS = { Piece.EMPTY, WP, WN,
			WB, WQ, WR };

	private static final int[] WHITE_LEGAL_CONTENTS = { Piece.EMPTY, BP, BN,
			BB, BQ, BR };

	private static final int[] BP_X_LEGAL_CONTENTS = { WP, WN, WB, WQ, WR };

	private static final int[] WP_X_LEGAL_CONTENTS = { BP, BN, BB, BQ, BR };

	private static final int[] BB_BQ = new int[] { BB, BQ };

	private static final int[] WB_WQ = new int[] { WB, WQ };

	private static final int[] WR_WQ = new int[] { WQ, WR };

	private static final int[] BR_BQ = new int[] { BQ, BR };

	private static final int[] WR_WQ_WK = new int[] { WR, WQ, WK };

	private static final int[] BR_BQ_BK = new int[] { BR, BQ, BK };

	private static final int[] WB_WQ_WK = new int[] { WB, WQ, WK };

	private static final int[] BB_BQ_BK = new int[] { BB, BQ, BK };

	private static final int[] BB_BQ_BK_BP = new int[] { BB, BQ, BK, BP };

	private static final int[] WB_WQ_WK_WP = new int[] { WB, WQ, WK, WP };

	private static final int[][] BLACK_LEVEL_1_CAPTURES = { WB_WQ_WK, // top
			// left
			WR_WQ_WK, // top center
			WB_WQ_WK, // top right
			WR_WQ_WK, // left
			WR_WQ_WK, // right
			WB_WQ_WK_WP, // bottom left
			WR_WQ_WK, // bottom center
			WB_WQ_WK_WP, // bottom right
	};

	private static final int[][] BLACK_LEVEL_2_CAPTURES = { WB_WQ, WR_WQ,
			WB_WQ, WR_WQ, WR_WQ, WB_WQ, WR_WQ, WB_WQ };

	private static final int[][] WHITE_LEVEL_1_CAPTURES = { BB_BQ_BK_BP,
			BR_BQ_BK, BB_BQ_BK_BP, BR_BQ_BK, BR_BQ_BK, BB_BQ_BK, BR_BQ_BK,
			BB_BQ_BK };

	private static final int[][] WHITE_LEVEL_2_CAPTURES = { BB_BQ, BR_BQ,
			BB_BQ, BR_BQ, BR_BQ, BB_BQ, BR_BQ, BB_BQ };

	public static final String dumpMoves(Move[] moves) {
		String result = "";
		for (int i = 0; i < moves.length; i++) {
			result += (i == 0 ? "{" : ",") + moves[i];
		}
		return result + "}";
	}

	/**
	 * This method performs no validation. Returns the board state after the
	 * move was made. Adjusts board with the move made.
	 * 
	 * @throws IllegalArgumentException
	 *             if the move was invalid.
	 */
	public static final int getBoardState(int[][] board, boolean isWhitesMove,
			int lastDPPushFile, boolean whiteCanCastleKingside,
			boolean whiteCanCastleQueenside, boolean blackCanCastleKingside,
			boolean blackCanCastleQueenside) {
		int result = 0;

		if (isWhitesMove) {
			result |= IS_WHITES_MOVE_STATE;
		}
		if (whiteCanCastleKingside) {
			result |= WHITE_CAN_CASTLE_KINGSIDE_STATE;
		}
		if (whiteCanCastleQueenside) {
			result |= WHITE_CAN_CASTLE_QUEENSIDE_STATE;
		}

		if (blackCanCastleKingside) {
			result |= BLACK_CAN_CASTLE_KINGSIDE_STATE;
		}

		if (blackCanCastleQueenside) {
			result |= BLACK_CAN_CASTLE_QUEENSIDE_STATE;
		}

		switch (lastDPPushFile) {
		case 0: {
			result |= A_FILE_STATE;
			break;
		}
		case 1: {
			result |= B_FILE_STATE;
			break;
		}
		case 2: {
			result |= C_FILE_STATE;
			break;
		}
		case 3: {
			result |= D_FILE_STATE;
			break;
		}
		case 4: {
			result |= E_FILE_STATE;
			break;
		}
		case 5: {
			result |= F_FILE_STATE;
			break;
		}
		case 6: {
			result |= G_FILE_STATE;
			break;
		}
		case 7: {
			result |= H_FILE_STATE;
			break;
		}
		}
		return result;// setMateStates(board,result);
	}

	/**
	 * Rolls back the last move made. Since state can not be determined from the
	 * information passed in boardState is not used in this method.
	 */
	public static final void rollbackMove(int[][] board, Move move) {
		if (move.isCastling()) {
			if (move.isCastleKingside() && move.isWhitesMove()) {
				board[E1[0]][E1[1]] = WK;
				board[F1[0]][F1[1]] = Piece.EMPTY;
				board[G1[0]][G1[1]] = Piece.EMPTY;
				board[H1[0]][H1[1]] = WR;
			} else if (move.isCastleQueenside() && move.isWhitesMove()) {
				board[A1[0]][E1[1]] = WR;
				board[B1[0]][B1[1]] = Piece.EMPTY;
				board[C1[0]][C1[1]] = Piece.EMPTY;
				board[D1[0]][D1[1]] = Piece.EMPTY;
				board[E1[0]][E1[1]] = WK;
			} else if (move.isCastleKingside() && !move.isWhitesMove()) {
				board[E8[0]][E8[1]] = BR;
				board[F8[0]][F8[1]] = Piece.EMPTY;
				board[G8[0]][G8[1]] = Piece.EMPTY;
				board[H8[0]][H8[1]] = BK;
			} else if (move.isCastleQueenside() && !move.isWhitesMove()) {
				board[A8[0]][E8[1]] = BR;
				board[B8[0]][B8[1]] = Piece.EMPTY;
				board[C8[0]][C8[1]] = Piece.EMPTY;
				board[D8[0]][D8[1]] = Piece.EMPTY;
				board[E8[0]][E8[1]] = BK;
			}
		} else if (move.isDropMove()) {
			board[move.getEndRank()][move.getEndFile()] = Piece.EMPTY;
		} else {
			board[move.getStartRank()][move.getStartFile()] = move
					.getPieceMoving();

			if (move.isEnPassant()) {
				board[move.getEnPassantPawnRank()][move.getEnPassantPawnFile()] = move
						.getCapturedPiece();
				board[move.getEndRank()][move.getEndFile()] = Piece.EMPTY;
			} else {
				board[move.getEndRank()][move.getEndFile()] = move
						.getCapturedPiece(); // returns Piece.EMPTY if none
				// captured.
			}
		}
	}

	/**
	 * Makes the specified move on the specified board. This method performs NO
	 * validations it is up to the caller to validate the move. If
	 * updateBoardState is true returns the boards state after the move was made
	 * otherwise returns the boardState passed in.
	 * 
	 * @throws IllegalArgumentException
	 *             if the mvoe was invalid.
	 */
	public static final int makeMove(int[][] board, int boardState, Move move,
			boolean updateBoardState) {
		int lastDoublePawnPushFile = -1;
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		boolean whiteCanCastleQueenside = (boardState & WHITE_CAN_CASTLE_QUEENSIDE_STATE) != 0;
		boolean whiteCanCastleKingside = (boardState & WHITE_CAN_CASTLE_KINGSIDE_STATE) != 0;
		boolean blackCanCastleKingside = (boardState & WHITE_CAN_CASTLE_KINGSIDE_STATE) != 0;
		boolean blackCanCastleQueenside = (boardState & WHITE_CAN_CASTLE_QUEENSIDE_STATE) != 0;

		if (move.isDropMove()) {
			board[move.getEndRank()][move.getEndFile()] = move
					.getDroppedPiece();
		} else if (move.isPromotion()) {
			board[move.getStartRank()][move.getStartFile()] = Piece.EMPTY;
			board[move.getEndRank()][move.getEndFile()] = move
					.getPromotedPiece();
		} else if (move.isCastling()) {
			if (move.isCastleKingside() && move.isWhitesMove()) {
				board[E1[0]][E1[1]] = Piece.EMPTY;
				board[F1[0]][F1[1]] = WR;
				board[G1[0]][G1[1]] = WK;
				board[H1[0]][H1[1]] = Piece.EMPTY;
			} else if (move.isCastleQueenside() && move.isWhitesMove()) {
				board[A1[0]][A1[1]] = Piece.EMPTY;
				board[B1[0]][B1[1]] = Piece.EMPTY;
				board[C1[0]][C1[1]] = WK;
				board[D1[0]][D1[1]] = WR;
				board[E1[0]][E1[1]] = Piece.EMPTY;
			} else if (move.isCastleKingside() && !move.isWhitesMove()) {
				board[E8[0]][E8[1]] = Piece.EMPTY;
				board[F8[0]][F8[1]] = BR;
				board[G8[0]][G8[1]] = BK;
				board[H8[0]][H8[1]] = Piece.EMPTY;
			} else if (move.isCastleQueenside() && !move.isWhitesMove()) {
				board[A8[0]][A8[1]] = Piece.EMPTY;
				board[B8[0]][B8[1]] = Piece.EMPTY;
				board[C8[0]][C8[1]] = BK;
				board[D8[0]][D8[1]] = BR;
				board[E8[0]][E8[1]] = Piece.EMPTY;
			}

			if (updateBoardState) {
				if (isWhitesMove) {
					whiteCanCastleQueenside = false;
					whiteCanCastleKingside = false;
				} else {
					blackCanCastleQueenside = false;
					blackCanCastleKingside = false;
				}
			}
		} else {
			board[move.getStartRank()][move.getStartFile()] = Piece.EMPTY;
			board[move.getEndRank()][move.getEndFile()] = move.getPieceMoving();

			if (move.isEnPassant()) {
				board[move.getEnPassantPawnRank()][move.getEnPassantPawnFile()] = Piece.EMPTY;
			}
			if (updateBoardState) {

				if (move.isDoublePawnPush()) {
					lastDoublePawnPushFile = move.getStartFile();
				} else if (PieceUtil.isKing(move.getPieceMoving())) {
					if (isWhitesMove) {
						whiteCanCastleQueenside = false;
						whiteCanCastleKingside = false;
					} else {
						blackCanCastleQueenside = false;
						blackCanCastleKingside = false;
					}
				} else if (move.getPieceMoving() == WHITE_ROOK
						&& move.getStartRank() == RANK_1
						&& move.getStartFile() == H) {
					whiteCanCastleKingside = false;
				} else if (move.getPieceMoving() == WHITE_ROOK
						&& move.getStartRank() == RANK_1
						&& move.getStartFile() == A) {
					whiteCanCastleQueenside = false;
				} else if (move.getPieceMoving() == BLACK_ROOK
						&& move.getStartRank() == RANK_8
						&& move.getStartFile() == H) {
					blackCanCastleKingside = false;
				} else if (move.getPieceMoving() == BLACK_ROOK
						&& move.getStartRank() == RANK_8
						&& move.getStartFile() == A) {
					blackCanCastleQueenside = false;
				}
			}
		}

		if (updateBoardState) {
			isWhitesMove = !isWhitesMove;
			return getBoardState(board, isWhitesMove, lastDoublePawnPushFile,
					whiteCanCastleKingside, whiteCanCastleQueenside,
					blackCanCastleKingside, blackCanCastleQueenside);
		} else {
			return boardState;
		}
	}

	/**
	 * Returns all of the legal moves in droppable chess (crazyhouse or
	 * bughouse).
	 * 
	 * @param board
	 * @param boardState
	 * @param playersHoldings
	 *            The pieces the player is holding that he/she can drop for a
	 *            move.
	 * @return
	 */
	public static final Move[] getLegalDroppableChessMoves(final int[][] board,
			final int boardState, final int[] playersHoldings) {
		boolean isDroppable = playersHoldings != null
				|| playersHoldings.length != 0;
		Move[] moves = new Move[isDroppable ? DROP_MAX_MOVES : MAXIMUM_MOVES];
		int lastMovesIndex = -1;

		// get psudo legal moves.
		lastMovesIndex = getPsuedoLegalMoves(moves, lastMovesIndex, board,
				boardState);

		lastMovesIndex = getPseudoDropMoves(moves, lastMovesIndex, board,
				boardState, playersHoldings);

		Move[] trimmedMoves = new Move[isDroppable ? DROP_MAX_MOVES
				: MAXIMUM_MOVES];
		int lastUsedTrimmedIndex = -1;
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		int[] kingCoordinates = getKingCoordinates(isWhitesMove, board);

		// now trim all illegals.
		for (int i = 0; i <= lastMovesIndex; i++) {
			Move move = moves[i];
			boolean isLegal = false;

			if (move.isCastling()) {
				if (move.isCastleKingside()) {
					isLegal = !isInCheck(board,
							isWhitesMove ? WHITE_CASTLE_THRU_CHECK_KINGSIDE
									: BLACK_CASTLE_THRU_CHECK_KINGSIDE,
							isWhitesMove);
				} else {
					isLegal = !isInCheck(board,
							isWhitesMove ? WHITE_CASTLE_THRU_CHECK_QUEENSIDE
									: BLACK_CASTLE_THRU_CHECK_QUEENSIDE,
							isWhitesMove);
				}
			} else {
				// MAKE THE MOVE
				makeMove(board, boardState, move, false);

				// DETERMINE IF IN CHECK AFTER MOVE
				//
				// THE FOLLOWING CODE IS NOT THREAD SAFE AND ALTERS BOARD.
				int pieceMoving = move.getPieceMoving();
				if (isWhitesMove && pieceMoving == WK || !isWhitesMove
						&& pieceMoving == BK) {
					// The king moved so the kings coordinates are the end
					// rank/file of the move.
					// *Note - castling is already taken care of so no need to
					// worry about what the end
					// coordinate is if that is the case).
					isLegal = !isInCheck(board, move.getEndCoordinates(),
							isWhitesMove);
				} else {
					isLegal = !isInCheck(board, kingCoordinates, isWhitesMove);
				}

				// ROLL BACK THE MOVE PRESERVING THE BOARDS STATE.
				rollbackMove(board, move);
			}

			if (isLegal) {
				trimmedMoves[++lastUsedTrimmedIndex] = move;
			}

		}

		// trim result and return it.
		int resultSize = lastUsedTrimmedIndex + 1;
		Move[] result = new Move[resultSize];
		System.arraycopy(trimmedMoves, 0, result, 0, resultSize);

		return result;
	}

	/**
	 * Returns an array of all of the legal moves. ***WARNING*** This method
	 * WILL alter board while it is executing. However when the method is
	 * finished board should be in the same state as it was past in as. Thus it
	 * is up to the invoker to handle all synchronization.
	 */
	public static final Move[] getLegalMoves(final int[][] board,
			final int boardState, final int[] availableDropPieces) {
		return getLegalDroppableChessMoves(board, boardState,
				availableDropPieces == null ? new int[] {}
						: availableDropPieces);
	}

	public static final boolean contains(int[] array, int integer) {
		boolean result = false;
		for (int i = 0; !result && i < array.length; i++) {
			result = array[i] == integer;
		}
		return result;
	}

	/**
	 * Returns true if 0 <= endRank & endFile <=8, state.get(endRank,endFile) is
	 * not a king, state.get(endRank,endFile) is Piece.EMPTY or opposite color
	 * of whose move it is.
	 */
	public static final boolean isPseudoLegal(final int[][] board,
			int boardState, int endRank, int endFile,
			int[] possibleEndCoordContents) {
		boolean result = false;
		if (CoordinatesUtil.isInBounds(endRank, endFile)) {
			result = contains(possibleEndCoordContents, board[endRank][endFile]);
		}
		return result;
	}

	public static final int addPatternMoves(Move[] moves, int lastMovesIndex,
			final int[][] board, final int boardState, int startRank,
			int startFile, int pieceMoving,
			boolean moveAlongPatternUtilBlocked,
			int[] endCoordinatesLegalPieces, int[] addToRankPattern,
			int[] addToFilePattern) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;

		// assert addToRankPattern != null : "Rank pattern cant be null.";
		// / assert addToFilePattern != null : "File pattern cant be null.";
		// assert addToRankPattern.length == addToFilePattern.length : "Rank
		// pattern must be the same length as file pattern";
		// assert addToRankPattern.length != 0 : "Rank pattern must contain
		// atleast one int";

		if (moveAlongPatternUtilBlocked) {
			for (int i = 0; i < addToRankPattern.length; i++) {
				boolean stopPatternSearch = false;

				int endRank = startRank;
				int endFile = startFile;

				do {
					endRank += addToRankPattern[i];
					endFile += addToFilePattern[i];

					if (CoordinatesUtil.isInBounds(endRank, endFile)) {
						int candidateContents = board[endRank][endFile];
						if (isPseudoLegal(board, boardState, endRank, endFile,
								endCoordinatesLegalPieces)) {
							moves[++lastMovesIndex] = new Move(startRank,
									startFile, endRank, endFile, pieceMoving,
									candidateContents, isWhitesMove);

						}

						stopPatternSearch = candidateContents != Piece.EMPTY;
					} else {
						stopPatternSearch = true;
					}
				} while (!stopPatternSearch);
			}
		} else {
			for (int i = 0; i < addToRankPattern.length; i++) {
				int endRank = startRank + addToRankPattern[i];
				int endFile = startFile + addToFilePattern[i];

				if (isPseudoLegal(board, boardState, endRank, endFile,
						endCoordinatesLegalPieces)) {
					moves[++lastMovesIndex] = new Move(startRank, startFile,
							endRank, endFile, pieceMoving,
							board[endRank][endFile], isWhitesMove);
				}
			}
		}
		return lastMovesIndex;
	}

	public static final int getPseudoDropMoves(Move[] moves,
			int lastMovesIndex, int[][] board, int boardState,
			int dropHoldings[]) {
		if (dropHoldings != null && dropHoldings.length != 0) {
			boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
			boolean isPawnDroppable = false;
			boolean isKnightDroppable = false;
			boolean isBishopDroppable = false;
			boolean isRookDroppable = false;
			boolean isQueenDroppable = false;

			for (int i = 0; i < dropHoldings.length; i++) {
				switch (dropHoldings[i]) {
				case WP:
				case BP: {
					isPawnDroppable = true;
					break;
				}
				case WN:
				case BN: {
					isKnightDroppable = true;
					break;
				}
				case WB:
				case BB: {
					isBishopDroppable = true;
					break;
				}
				case WR:
				case BR: {
					isRookDroppable = true;
					break;
				}
				case WQ:
				case BQ: {
					isQueenDroppable = true;
					break;
				}
				default: {
					throw new IllegalArgumentException(
							"Invalid drop piece encountered: "
									+ dropHoldings[i]);
				}
				}
			}

			for (int rank = 0; rank < 8; rank++) {
				for (int file = 0; file < 8; file++) {
					if (board[rank][file] == Piece.EMPTY) {
						if (isPawnDroppable && rank != 0 && rank != 7) {
							moves[++lastMovesIndex] = new Move(
									isWhitesMove ? WP : BP, new int[] { rank,
											file }, isWhitesMove);
						}
						if (isKnightDroppable) {
							moves[++lastMovesIndex] = new Move(
									isWhitesMove ? WN : BN, new int[] { rank,
											file }, isWhitesMove);
						}
						if (isBishopDroppable) {
							moves[++lastMovesIndex] = new Move(
									isWhitesMove ? WB : BB, new int[] { rank,
											file }, isWhitesMove);
						}
						if (isRookDroppable) {
							moves[++lastMovesIndex] = new Move(
									isWhitesMove ? WR : BR, new int[] { rank,
											file }, isWhitesMove);
						}
						if (isQueenDroppable) {
							moves[++lastMovesIndex] = new Move(
									isWhitesMove ? WQ : BQ, new int[] { rank,
											file }, isWhitesMove);
						}
					}
				}
			}
		}
		return lastMovesIndex;
	}

	/**
	 * Returns an Piece.EMPTY List if no moves are found. Illegal moves from
	 * moving into check are not taken into consideration.
	 * 
	 * @return A list of chess.Move objects.
	 * @see decaf.moveengine.Move
	 */
	public static final int getPsuedoKnightMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		return addPatternMoves(moves, lastMovesIndex, board, boardState,
				startRank, startFile, isWhitesMove ? PieceUtil.WN
						: PieceUtil.BN, false,
				isWhitesMove ? WHITE_LEGAL_CONTENTS : BLACK_LEGAL_CONTENTS,
				N_RANK_PATTERN, N_FILE_PATTERN);
	}

	/**
	 * Returns an Piece.EMPTY List if no moves are found. Illegal moves from
	 * moving into check are not taken into consideration.
	 * 
	 * @return A list of chess.Move objects.
	 * @see decaf.moveengine.Move
	 */
	public static final int getPsuedoBishopMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		return addPatternMoves(moves, lastMovesIndex, board, boardState,
				startRank, startFile, isWhitesMove ? PieceUtil.WB
						: PieceUtil.BB, true,
				isWhitesMove ? WHITE_LEGAL_CONTENTS : BLACK_LEGAL_CONTENTS,
				B_RANK_PATTERN, B_FILE_PATTERN);
	}

	/**
	 * Returns an Piece.EMPTY List if no moves are found. Illegal moves from
	 * moving into check are not taken into consideration.
	 * 
	 * @return A list of chess.Move objects.
	 * @see decaf.moveengine.Move
	 */
	public static final int getPsuedoQueenMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		return addPatternMoves(moves, lastMovesIndex, board, boardState,
				startRank, startFile, isWhitesMove ? PieceUtil.WQ
						: PieceUtil.BQ, true,
				isWhitesMove ? WHITE_LEGAL_CONTENTS : BLACK_LEGAL_CONTENTS,
				Q_RANK_PATTERN, Q_FILE_PATTERN);
	}

	/**
	 * Returns an Piece.EMPTY List if no moves are found. Illegal moves from
	 * moving into check are not taken into consideration.
	 * 
	 * @return A list of chess.Move objects.
	 * @see decaf.moveengine.Move
	 */
	public static final int getPsuedoRookMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		return addPatternMoves(moves, lastMovesIndex, board, boardState,
				startRank, startFile, isWhitesMove ? PieceUtil.WR
						: PieceUtil.BR, true,
				isWhitesMove ? WHITE_LEGAL_CONTENTS : BLACK_LEGAL_CONTENTS,
				R_RANK_PATTERN, R_FILE_PATTERN);
	}

	/**
	 * Returns an Piece.EMPTY List if no moves are found. Illegal moves from
	 * moving into check are not taken into consideration.
	 * 
	 * @return A list of chess.Move objects.
	 * @see decaf.moveengine.Move
	 */
	public static final int getPsuedoPawnMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		// add move up 1 square moves.
		int result = getPsuedoPawnUpOneMoves(moves, lastMovesIndex, board,
				boardState, startRank, startFile);

		// add move forward 2 squares on first move.
		result = getPseudoDoublePawnPushMoves(moves, result, board, boardState,
				startRank, startFile);

		// add in captures right and left.
		result = getPseudoPawnCaptures(moves, result, board, boardState,
				startRank, startFile);

		// add ep moves.
		result = getPawnEPMoves(moves, result, board, boardState, startRank,
				startFile);

		return result;
	}

	/**
	 * Returns all possible pawn moves moving up 1 square. Illegal moves from
	 * moving into check are not taken into consideration.
	 * 
	 * @return The last entry of the movesArray populated.
	 * @see decaf.moveengine.Move
	 */
	public static final int getPsuedoPawnUpOneMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		int result = lastMovesIndex;
		int[] filePattern = P_UP_1_FILE_PATTERN;
		int[] rankPattern = isWhitesMove ? WP_RANK_PATTERN : BP_RANK_PATTERN;
		int[] endCoordinatesLegalPieces = new int[] { Piece.EMPTY };

		for (int i = 0; i < rankPattern.length; i++) {
			int endRank = startRank + rankPattern[i];
			int endFile = startFile + filePattern[i];

			if (isPseudoLegal(board, boardState, endRank, endFile,
					endCoordinatesLegalPieces)) {
				if ((isWhitesMove && endRank == RANK_8)
						|| (!isWhitesMove && endRank == RANK_1)) {
					result = addAllPromotions(moves, result,
							board[endRank][endFile], new int[] { startRank,
									startFile },
							new int[] { endRank, endFile }, isWhitesMove);
				} else {
					moves[++result] = new Move(startRank, startFile, endRank,
							endFile, isWhitesMove ? WHITE_PAWN : BLACK_PAWN,
							board[endRank][endFile], isWhitesMove);
				}
			}
		}
		return result;
	}

	/**
	 * Returns -1 if last move was not a double pawn push.
	 */
	public static final int stateToLastDPPush(int boardState) {
		if ((boardState & A_FILE_STATE) != 0) {
			return 0;
		} else if ((boardState & B_FILE_STATE) != 0) {
			return 1;
		} else if ((boardState & C_FILE_STATE) != 0) {
			return 2;
		} else if ((boardState & D_FILE_STATE) != 0) {
			return 3;
		} else if ((boardState & E_FILE_STATE) != 0) {
			return 4;
		} else if ((boardState & F_FILE_STATE) != 0) {
			return 5;
		} else if ((boardState & G_FILE_STATE) != 0) {
			return 6;
		} else if ((boardState & H_FILE_STATE) != 0) {
			return 7;
		} else {
			return -1;
		}
	}

	public static final int getPawnEPMoves(Move[] moves, int lastMovesIndex,
			final int[][] board, int boardState, int startRank, int startFile) {
		int epFile = stateToLastDPPush(boardState);
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;

		if (epFile != -1) {
			if (isWhitesMove && startRank == RANK_5) {
				int leftOne = startFile - 1;
				int rightOne = startFile + 1;

				if (leftOne == epFile
						&& PieceUtil.isPawn(board[RANK_5][leftOne])
						&& PieceUtil.isBlackPiece(board[RANK_5][leftOne])
						&& PieceUtil.isEmpty(board[RANK_6][leftOne])) {
					moves[++lastMovesIndex] = new Move(new int[] { startRank,
							startFile }, leftOne, true);
				} else if (rightOne == epFile
						&& PieceUtil.isPawn(board[RANK_5][rightOne])
						&& PieceUtil.isBlackPiece(board[RANK_5][rightOne])
						&& PieceUtil.isEmpty(board[RANK_6][rightOne])) {
					moves[++lastMovesIndex] = new Move(new int[] { startRank,
							startFile }, rightOne, true);
				}
			} else if (!isWhitesMove && startRank == RANK_4) {
				int leftOne = startFile + 1;
				int rightOne = startFile - 1;
				if (rightOne == epFile
						&& PieceUtil.isPawn(board[RANK_4][rightOne])
						&& PieceUtil.isWhitePiece(board[RANK_4][rightOne])
						&& PieceUtil.isEmpty(board[RANK_3][rightOne])) {
					moves[++lastMovesIndex] = new Move(new int[] { startRank,
							startFile }, startFile - 1, false);
				} else if (leftOne == epFile
						&& PieceUtil.isPawn(board[RANK_4][leftOne])
						&& PieceUtil.isWhitePiece(board[RANK_4][leftOne])
						&& PieceUtil.isEmpty(board[RANK_3][leftOne])) {
					moves[++lastMovesIndex] = new Move(new int[] { startRank,
							startFile }, leftOne, false);
				}
			}
		}
		return lastMovesIndex;
	}

	public static final int getPseudoPawnCaptures(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		int result = lastMovesIndex;
		int[] filePattern = P_X_FILE_PATTERN;
		int[] rankPattern = isWhitesMove ? WP_X_RANK_PATTERN
				: BP_X_RANK_PATTERN;
		int[] endCoordinatesLegalPieces = isWhitesMove ? WP_X_LEGAL_CONTENTS
				: BP_X_LEGAL_CONTENTS;

		for (int i = 0; i < rankPattern.length; i++) {
			int endRank = startRank + rankPattern[i];
			int endFile = startFile + filePattern[i];

			if (isPseudoLegal(board, boardState, endRank, endFile,
					endCoordinatesLegalPieces)) {
				if ((isWhitesMove && endRank == RANK_8)
						|| (!isWhitesMove && endRank == RANK_1)) {
					result = addAllPromotions(moves, result,
							board[endRank][endFile], new int[] { startRank,
									startFile },
							new int[] { endRank, endFile }, isWhitesMove);
				} else {
					moves[++result] = new Move(startRank, startFile, endRank,
							endFile, isWhitesMove ? WHITE_PAWN : BLACK_PAWN,
							board[endRank][endFile], isWhitesMove);
				}
			}
		}

		return result;
	}

	private static final int addAllPromotions(Move[] moves, int lastMovesIndex,
			int capturedPiece, int[] startCoordinates, int[] endCoordinates,
			boolean isWhitesMove) {
		moves[++lastMovesIndex] = new Move(isWhitesMove ? Piece.WHITE_KNIGHT
				: Piece.BLACK_KNIGHT, startCoordinates, endCoordinates,
				capturedPiece, isWhitesMove);
		moves[++lastMovesIndex] = new Move(isWhitesMove ? Piece.WHITE_BISHOP
				: Piece.BLACK_BISHOP, startCoordinates, endCoordinates,
				capturedPiece, isWhitesMove);
		moves[++lastMovesIndex] = new Move(isWhitesMove ? Piece.WHITE_ROOK
				: Piece.BLACK_ROOK, startCoordinates, endCoordinates,
				capturedPiece, isWhitesMove);
		moves[++lastMovesIndex] = new Move(isWhitesMove ? Piece.WHITE_QUEEN
				: Piece.BLACK_QUEEN, startCoordinates, endCoordinates,
				capturedPiece, isWhitesMove);
		return lastMovesIndex;
	}

	public static final int getPseudoDoublePawnPushMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;

		if (isWhitesMove) {
			if (startRank == Move.RANK_2
					&& PieceUtil.isEmpty(board[RANK_3][startFile])
					&& PieceUtil.isEmpty(board[RANK_4][startFile])) {
				moves[++lastMovesIndex] = new Move(startFile, true);
			}
		} else {
			if (startRank == Move.RANK_7
					&& PieceUtil.isEmpty(board[Move.RANK_6][startFile])
					&& PieceUtil.isEmpty(board[Move.RANK_5][startFile])) {
				moves[++lastMovesIndex] = new Move(startFile, false);
			}

		}
		return lastMovesIndex;
	}

	public static final int getPsuedoKingNonCastlingMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		return addPatternMoves(moves, lastMovesIndex, board, boardState,
				startRank, startFile, isWhitesMove ? PieceUtil.WK
						: PieceUtil.BK, false,
				isWhitesMove ? WHITE_LEGAL_CONTENTS : BLACK_LEGAL_CONTENTS,
				K_RANK_PATTERN, K_FILE_PATTERN);

	}

	public static final int getPsuedoKingCastlingMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		boolean canCastleKingside = isWhitesMove ? (boardState & WHITE_CAN_CASTLE_KINGSIDE_STATE) != 0
				: (boardState & BLACK_CAN_CASTLE_KINGSIDE_STATE) != 0;
		boolean canCastleQueenside = isWhitesMove ? (boardState & WHITE_CAN_CASTLE_QUEENSIDE_STATE) != 0
				: (boardState & BLACK_CAN_CASTLE_QUEENSIDE_STATE) != 0;

		if (canCastleKingside) {
			if (isWhitesMove && startFile == Move.E && startRank == Move.RANK_1
					&& board[E1[0]][E1[1]] == PieceUtil.WK
					&& board[F1[0]][F1[1]] == PieceUtil.EMPTY
					&& board[G1[0]][G1[1]] == PieceUtil.EMPTY
					&& board[H1[0]][H1[1]] == PieceUtil.WR) {
				moves[++lastMovesIndex] = new Move(true, true);
			} else if (!isWhitesMove && startFile == Move.E
					&& startRank == Move.RANK_8
					&& board[E8[0]][E8[1]] == PieceUtil.BK
					&& board[F8[0]][F8[1]] == PieceUtil.EMPTY
					&& board[G8[0]][G8[1]] == PieceUtil.EMPTY
					&& board[H8[0]][H8[1]] == PieceUtil.BR) {

				moves[++lastMovesIndex] = new Move(true, false);
			}
		}

		if (canCastleQueenside) {
			if (isWhitesMove && startFile == Move.E && startRank == Move.RANK_1
					&& board[A1[0]][A1[1]] == PieceUtil.WR
					&& board[B1[0]][B1[1]] == PieceUtil.EMPTY
					&& board[C1[0]][C1[1]] == PieceUtil.EMPTY
					&& board[D1[0]][D1[1]] == PieceUtil.EMPTY
					&& board[E1[0]][E1[1]] == PieceUtil.WK) {
				moves[++lastMovesIndex] = new Move(false, true);
			} else if (!isWhitesMove && startFile == Move.E
					&& startRank == Move.RANK_8
					&& board[A8[0]][A8[1]] == PieceUtil.BR
					&& board[B8[0]][B8[1]] == PieceUtil.EMPTY
					&& board[C8[0]][C8[1]] == PieceUtil.EMPTY
					&& board[D8[0]][D8[1]] == PieceUtil.EMPTY
					&& board[E8[0]][E8[1]] == PieceUtil.BK) {
				moves[++lastMovesIndex] = new Move(false, false);
			}
		}
		return lastMovesIndex;
	}

	/**
	 * Returns an Piece.EMPTY List if no moves are found. Illegal moves from
	 * moving into check are not taken into consideration EXCEPT when castling
	 * through check.
	 * 
	 * @return A list of chess.Move objects.
	 * @see decaf.moveengine.Move
	 */
	public static final int getPsuedoKingMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState,
			int startRank, int startFile) {
		lastMovesIndex = getPsuedoKingCastlingMoves(moves, lastMovesIndex,
				board, boardState, startRank, startFile);
		return getPsuedoKingNonCastlingMoves(moves, lastMovesIndex, board,
				boardState, startRank, startFile);
	}

	public static final int getPsuedoLegalMoves(Move[] moves,
			int lastMovesIndex, final int[][] board, int boardState) {
		boolean isWhitesMove = (boardState & IS_WHITES_MOVE_STATE) != 0;
		for (int rank = 0; rank < 8; rank++) {
			for (int file = 0; file < 8; file++) {
				int contents = board[rank][file];
				if (isWhitesMove ? PieceUtil.isWhitePiece(contents) : PieceUtil
						.isBlackPiece(contents)) {
					switch (contents) {
					case WP:
					case BP: {
						lastMovesIndex = getPsuedoPawnMoves(moves,
								lastMovesIndex, board, boardState, rank, file);
						break;
					}
					case WR:
					case BR: {
						lastMovesIndex = getPsuedoRookMoves(moves,
								lastMovesIndex, board, boardState, rank, file);
						break;
					}

					case WN:
					case BN: {
						lastMovesIndex = getPsuedoKnightMoves(moves,
								lastMovesIndex, board, boardState, rank, file);
						break;

					}
					case WB:
					case BB: {
						lastMovesIndex = getPsuedoBishopMoves(moves,
								lastMovesIndex, board, boardState, rank, file);
						break;

					}
					case WQ:
					case BQ: {
						lastMovesIndex = getPsuedoQueenMoves(moves,
								lastMovesIndex, board, boardState, rank, file);
						break;

					}
					case WK:
					case BK: {
						lastMovesIndex = getPsuedoKingMoves(moves,
								lastMovesIndex, board, boardState, rank, file);

					}
					}
				}
			}
		}
		return lastMovesIndex;
	}

	public static final boolean isInCheck(final int[][] board,
			int[][] coordinates, boolean isWhite) {
		boolean result = false;
		for (int i = 0; !result && i < coordinates.length; i++) {
			result = isInCheck(board, coordinates[i], isWhite);
		}
		return result;
	}

	public static final boolean isInCheck(int[][] board, int[] coordinates,
			boolean isWhite) {
		boolean result = false;

		// test knights first.
		int knight = isWhite ? BN : WN;
		for (int i = 0; !result && i < N_RANK_PATTERN.length; i++) {
			int[] testCoordinates = { coordinates[0] + N_RANK_PATTERN[i],
					coordinates[1] + N_FILE_PATTERN[i] };
			if (CoordinatesUtil.isInBounds(testCoordinates)) {
				result = board[testCoordinates[0]][testCoordinates[1]] == knight;
			}

		}

		// Everything besides knights.
		if (!result) {
			int[][] level1 = isWhite ? WHITE_LEVEL_1_CAPTURES
					: BLACK_LEVEL_1_CAPTURES;
			int[][] level2 = isWhite ? WHITE_LEVEL_2_CAPTURES
					: BLACK_LEVEL_2_CAPTURES;
			for (int i = 0; !result && i < 8; i++) {
				int[] testCoordinates = pivot(i, coordinates);
				if (CoordinatesUtil.isInBounds(testCoordinates)) {
					int contents = board[testCoordinates[0]][testCoordinates[1]];
					result = contains(level1[i], contents);

					while (!result && contents == Piece.EMPTY) {
						testCoordinates = pivot(i, testCoordinates);
						if (CoordinatesUtil.isInBounds(testCoordinates)) {
							contents = board[testCoordinates[0]][testCoordinates[1]];
							result = contains(level2[i], contents);
						} else {
							break;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 0 top left 1 top center 2 top right 3 left 4 right 5 bottom left 6 bottom
	 * center 7 bottom right
	 */
	public static final int[] pivot(int location, int[] coordinates) {
		// sassert location >= 0 && location <= 8 : "Invalid location " +
		// location;
		int[] result = null;

		switch (location) {
		case 0: // top left
		{
			result = new int[] { coordinates[0] - 1, coordinates[1] - 1 };
			break;
		}

		case 1: // top center
		{
			result = new int[] { coordinates[0] - 1, coordinates[1] };
			break;
		}
		case 2: // top right
		{
			result = new int[] { coordinates[0] - 1, coordinates[1] + 1 };
			break;
		}
		case 3: // left
		{
			result = new int[] { coordinates[0], coordinates[1] - 1 };
			break;
		}
		case 4: // right
		{
			result = new int[] { coordinates[0], coordinates[1] + 1 };
			break;
		}
		case 5: // bottom left
		{
			result = new int[] { coordinates[0] + 1, coordinates[1] - 1 };
			break;
		}
		case 6: // bottom center
		{
			result = new int[] { coordinates[0] + 1, coordinates[1] };
			break;
		}
		case 7: // bottom right
		{
			result = new int[] { coordinates[0] + 1, coordinates[1] + 1 };
			break;
		}
		}
		return result;
	}

	public static final int[] getKingCoordinates(boolean searchForWhiteKing,
			int[][] board) {
		int[] result = null;
		if (searchForWhiteKing) {
			for (int rank = 7; result == null && rank > -1; rank--) {
				for (int file = 0; file < board.length; file++) {
					if (board[rank][file] == WK) {
						result = new int[] { rank, file };
					}
				}
			}
		} else {
			for (int rank = 0; result == null && rank < board.length; rank++) {
				for (int file = 0; file < board.length; file++) {
					if (board[rank][file] == BK) {
						result = new int[] { rank, file };
					}
				}
			}
		}

		if (result == null) {
			throw new IllegalArgumentException("Could not find "
					+ (searchForWhiteKing ? "white" : "black") + " king. "
					+ dumpBoard(board));
		}
		return result;
	}

	public static final String dumpBoard(int[][] board) {
		// assert board != null : "board cant be null.";
		return Position.DEFAULT_ENCODER.encode(board);
	}
}