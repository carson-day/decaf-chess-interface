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

import decaf.util.CoordinatesUtil;
import decaf.util.PieceUtil;

/**
 * This class just provides the bare essentials about a chess move.
 */
public class Move implements Cloneable, Serializable, Coordinates, Piece,
		Comparable {
	private int startRank;

	private int startFile;

	private int endRank;

	private int endFile;

	private boolean isWhitesMove;

	private boolean isDoublePawnPush;

	private int epPawnRank = -1;

	private int epPawnFile = -1;

	private int capturedPiece = PieceUtil.NILL;

	private int pieceMoving = PieceUtil.NILL;

	private int promotedPiece = PieceUtil.NILL;

	private int hash;

	private boolean isHashSet = false;

	private boolean isEnPassant;

	private boolean isCastleKingside;

	private boolean isCastleQueenside;

	private int droppedPiece;

	public Move(int startRank, int startFile, int endRank, int endFile,
			int pieceMoving, int capturedPiece, boolean isWhitesMove)
			throws InvalidMoveException {
		if (!CoordinatesUtil.isInBounds(startRank, startFile))
			throw new InvalidMoveException("Invalid startRank/startFile "
					+ CoordinatesUtil.getDefaultCoordinates(startRank,
							startFile));
		if (!CoordinatesUtil.isInBounds(endRank, endFile))
			throw new InvalidMoveException("Invalid endRank/endFile "
					+ CoordinatesUtil.getDefaultCoordinates(startRank,
							startFile));
		// if(PieceUtil.isEmpty(pieceMoving))
		// throw new InvalidMoveException(
		// "Piece moving cant be empty. "
		// + "Use Move.createMove if you want to avoid worrying about this.");
		// if((isWhitesMove && PieceUtil.isBlackPiece(pieceMoving))
		// || (!isWhitesMove && PieceUtil.isWhitePiece(pieceMoving)))
		// throw new InvalidMoveException("Invalid piece moving: "
		// + PieceUtil.getDefaultPiece(pieceMoving));
		// if(!PieceUtil.isEmpty(capturedPiece)
		// && (isWhitesMove && PieceUtil.isWhitePiece(capturedPiece) ||
		// !isWhitesMove
		// && PieceUtil.isBlackPiece(capturedPiece)))
		// throw new InvalidMoveException(
		// "Invalid captured piece: "
		// + PieceUtil.getDefaultPiece(capturedPiece)
		// + " Use Move.createMove if you do not want to worry about this.");
		// if(PieceUtil.isKing(capturedPiece))
		// throw new InvalidMoveException("CapturedPiece can not be a king. "
		// + PieceUtil.getDefaultPiece(capturedPiece));

		this.startRank = startRank;
		this.startFile = startFile;
		this.endRank = endRank;
		this.endFile = endFile;
		this.isWhitesMove = isWhitesMove;
		this.pieceMoving = pieceMoving;
		this.capturedPiece = capturedPiece;
	}

	public Move(int[] startCoordinates, int[] endCoordinates, int pieceMoving,
			int capturedPiece, boolean isWhitesMove)
			throws InvalidMoveException {
		this(startCoordinates[0], startCoordinates[1], endCoordinates[0],
				endCoordinates[1], pieceMoving, capturedPiece, isWhitesMove);
	}

	/**
	 * @param doublePawnPushFile
	 *            0 based double pawn push file.
	 */
	public Move(int doublePawnPushFile, boolean isWhitesMove)
			throws InvalidMoveException {
		this(isWhitesMove ? RANK_2 : RANK_7, doublePawnPushFile,
				isWhitesMove ? RANK_4 : RANK_5, doublePawnPushFile,
				isWhitesMove ? PieceUtil.WP : PieceUtil.BP, PieceUtil.EMPTY,
				isWhitesMove);
		this.isDoublePawnPush = true;
	}

	/**
	 * Should only be used for enpassant captures.
	 * 
	 * @param startCoordinates
	 *            The coordinates of the pawn moving before the capture
	 * @param endFile
	 *            The file the pawn is on after the capture.
	 */
	public Move(int[] startCoordinates, int endFile, boolean isWhitesMove)
			throws InvalidMoveException {
		this(startCoordinates, new int[] {
				isWhitesMove ? startCoordinates[0] - 1
						: startCoordinates[0] + 1, endFile },
				isWhitesMove ? PieceUtil.WP : PieceUtil.BP,
				isWhitesMove ? PieceUtil.BP : PieceUtil.WP, isWhitesMove);

		if ((startCoordinates[0] != RANK_5 && isWhitesMove)
				|| (startCoordinates[0] != RANK_4 && !isWhitesMove))
			throw new InvalidMoveException(
					"Invalid startRank for ep captures: "
							+ CoordinatesUtil
									.getDefaultCoordinates(startCoordinates));

		this.epPawnRank = startCoordinates[0];
		this.epPawnFile = endFile;

		if (!CoordinatesUtil.isInBounds(epPawnRank, epPawnFile))
			throw new InvalidMoveException("Invalid En Passant Rank/File "
					+ CoordinatesUtil.getDefaultCoordinates(epPawnRank,
							epPawnFile));

		isEnPassant = true;
	}

	/**
	 * Should only be used for castling moves.
	 * 
	 * @param isCastleKingside
	 *            True if castling kingside, false if castling queenside.
	 */
	public Move(boolean isCastleKingside, boolean isWhitesMove)
			throws InvalidMoveException {
		this(isWhitesMove ? E1 : E8, isCastleKingside && isWhitesMove ? H1
				: isCastleKingside && !isWhitesMove ? H8 : !isCastleKingside
						&& isWhitesMove ? A1 : A8, isWhitesMove ? PieceUtil.WK
				: PieceUtil.BK, PieceUtil.EMPTY, isWhitesMove);
		this.isCastleKingside = isCastleKingside;
		this.isCastleQueenside = !isCastleKingside;
	}

	/**
	 * Creates a promotion.
	 */
	public Move(int promotedPiece, int[] startCoordinates,
			int[] endCoordinates, int capturedPiece, boolean isWhitesMove) {
		CoordinatesUtil.assertValid(startCoordinates);
		CoordinatesUtil.assertValid(endCoordinates);
		PieceUtil.assertValidAndNotEmpty(promotedPiece);
		this.startRank = startCoordinates[0];
		this.startFile = startCoordinates[1];
		this.endRank = endCoordinates[0];
		this.endFile = endCoordinates[1];
		this.isWhitesMove = isWhitesMove;
		this.capturedPiece = capturedPiece;
		this.pieceMoving = isWhitesMove ? Piece.WP : Piece.BP;
		this.promotedPiece = promotedPiece;
	}

	/**
	 * Used for droppable chess (bughouse or crazyhouse)
	 */
	public Move(int droppedPiece, int[] endCoordinates, boolean isWhitesMove) {
		CoordinatesUtil.assertValid(endCoordinates);
		if (isWhitesMove) {
			PieceUtil.assertValidWhiteDropPiece(droppedPiece);
		} else {
			PieceUtil.assertValidBlackDropPiece(droppedPiece);
		}
		this.endRank = endCoordinates[0];
		this.endFile = endCoordinates[1];
		this.isWhitesMove = isWhitesMove;
		this.droppedPiece = droppedPiece;
	}

	/**
	 * Attempts to figure out which constructor to invoke e.g. invokes the one
	 * for castling if its a castling move invokes the one for ep if its an en
	 * passant capture invokes the one for double pawn push if its a double pawn
	 * push invokes the defaults if it cant figure out which one to invoke.
	 * 
	 * @param startCoordinates
	 * @param endCoordinates
	 * @return
	 */
	public static Move createMove(int[] startCoordinates, int[] endCoordinates,
			Position position) throws InvalidMoveException {
		CoordinatesUtil.assertValid(startCoordinates);
		CoordinatesUtil.assertValid(endCoordinates);

		boolean isWhitesMove = position.isWhitesMove();
		int pieceCaptured = position.get(endCoordinates);
		int pieceMoving = position.get(startCoordinates);

		if (PieceUtil.isPawn(pieceMoving)) {
			if (isWhitesMove && startCoordinates[0] == RANK_2
					&& endCoordinates[0] == RANK_4
					&& startCoordinates[1] == endCoordinates[1]
					&& pieceCaptured == NILL) {
				return new Move(startCoordinates[1], true);
			} else if (!isWhitesMove && startCoordinates[0] == RANK_7
					&& endCoordinates[0] == RANK_5
					&& startCoordinates[1] == endCoordinates[1]
					&& pieceCaptured == NILL) {
				return new Move(startCoordinates[1], false);
			} else if (isWhitesMove
					&& startCoordinates[0] == RANK_5
					&& endCoordinates[0] == RANK_6
					&& ((startCoordinates[1] == endCoordinates[1] - 1) || (startCoordinates[1] == endCoordinates[1] + 1))
					&& pieceCaptured == NILL) {
				return new Move(startCoordinates, endCoordinates[1], true);
			} else if (!isWhitesMove
					&& startCoordinates[0] == RANK_4
					&& endCoordinates[0] == RANK_3
					&& ((startCoordinates[1] == endCoordinates[1] - 1) || (startCoordinates[1] == endCoordinates[1] + 1))
					&& pieceCaptured == NILL) {
				return new Move(startCoordinates, endCoordinates[1], false);
			} else {
				return new Move(startCoordinates, endCoordinates, pieceMoving,
						pieceCaptured, isWhitesMove);
			}
		} else if (PieceUtil.isKing(pieceMoving)) {
			if (isWhitesMove
					&& CoordinatesUtil.equals(E1, startCoordinates)
					&& (CoordinatesUtil.equals(H1, endCoordinates) || CoordinatesUtil
							.equals(G1, endCoordinates))) {
				return new Move(true, true);
			} else if (isWhitesMove
					&& CoordinatesUtil.equals(E1, startCoordinates)
					&& (CoordinatesUtil.equals(A1, endCoordinates) || CoordinatesUtil
							.equals(C1, endCoordinates))) {
				return new Move(false, true);
			} else if (!isWhitesMove
					&& CoordinatesUtil.equals(E8, startCoordinates)
					&& (CoordinatesUtil.equals(H8, endCoordinates) || CoordinatesUtil
							.equals(G8, endCoordinates))) {
				return new Move(true, false);
			} else if (!isWhitesMove
					&& CoordinatesUtil.equals(E8, startCoordinates)
					&& (CoordinatesUtil.equals(A8, endCoordinates) || CoordinatesUtil
							.equals(C8, endCoordinates))) {
				return new Move(false, false);
			} else {
				return new Move(startCoordinates, endCoordinates, pieceMoving,
						pieceCaptured, isWhitesMove);
			}
		} else {
			return new Move(startCoordinates, endCoordinates, pieceMoving,
					pieceCaptured, isWhitesMove);
		}
	}

	public int getStartRank() {
		return startRank;
	}

	public int getStartFile() {
		return startFile;
	}

	public int getEndRank() {
		return endRank;
	}

	public int getEndFile() {
		return endFile;
	}

	public boolean isWhitesMove() {
		return isWhitesMove;
	}

	public int getEnPassantPawnRank() {
		return epPawnRank;
	}

	public int getEnPassantPawnFile() {
		return epPawnFile;
	}

	public int[] getEnPassantCoordinates() {
		return new int[] { epPawnRank, epPawnFile };
	}

	public boolean isEnPassant() {
		return isEnPassant;
	}

	public boolean isCastleKingside() {
		return isCastleKingside;
	}

	public boolean isCastleQueenside() {
		return isCastleQueenside;
	}

	public boolean isDoublePawnPush() {
		return isDoublePawnPush;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cnse) {
			throw new RuntimeException(cnse.toString());
		}
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Move))
			return false;

		final Move move = (Move) o;

		if (capturedPiece != move.capturedPiece)
			return false;
		if (endFile != move.endFile)
			return false;
		if (endRank != move.endRank)
			return false;
		if (epPawnFile != move.epPawnFile)
			return false;
		if (epPawnRank != move.epPawnRank)
			return false;
		if (isCastleKingside != move.isCastleKingside)
			return false;
		if (isCastleQueenside != move.isCastleQueenside)
			return false;
		if (isDoublePawnPush != move.isDoublePawnPush)
			return false;
		if (isEnPassant != move.isEnPassant)
			return false;
		if (isWhitesMove != move.isWhitesMove)
			return false;
		if (pieceMoving != move.pieceMoving)
			return false;
		if (startFile != move.startFile)
			return false;
		if (startRank != move.startRank)
			return false;
		if (droppedPiece != move.droppedPiece)
			return false;
		if (promotedPiece != move.promotedPiece)
			return false;
		return true;
	}

	public int compareTo(Object move) {
		// assert move instanceof Object : "Move must be of type object";

		int hash1 = hashCode();
		int hash2 = move.hashCode();

		return hash1 < hash2 ? -1 : hash1 > hash2 ? 1 : 0;
	}

	public int hashCode() {
		if (!isHashSet) {
			hash = startRank;
			hash = 29 * hash + startFile;
			hash = 29 * hash + endRank;
			hash = 29 * hash + endFile;
			hash = 29 * hash + (isWhitesMove ? 1 : 0);
			hash = 29 * hash + (isDoublePawnPush ? 1 : 0);
			hash = 29 * hash + epPawnRank;
			hash = 29 * hash + epPawnFile;
			hash = 29 * hash + capturedPiece;
			hash = 29 * hash + pieceMoving;
			hash = 29 * hash + promotedPiece;
			hash = 29 * hash + droppedPiece;
			hash = 29 * hash + (isEnPassant ? 1 : 0);
			hash = 29 * hash + (isCastleKingside ? 1 : 0);
			hash = 29 * hash + (isCastleQueenside ? 1 : 0);

			isHashSet = true;
		}
		return hash;
	}

	public String toString() {
		return CoordinatesUtil.getDefaultMove(this);
	}

	public int[] getStartCoordinates() {
		return new int[] { startRank, startFile };
	}

	public int getPromotedPiece() {
		return promotedPiece;
	}

	public boolean isPromotion() {
		return promotedPiece != Piece.NILL;
	}

	public int[] getEndCoordinates() {
		return new int[] { endRank, endFile };
	}

	public boolean isCapture() {
		return capturedPiece != PieceUtil.NILL;
	}

	public int getCapturedPiece() {
		return capturedPiece;
	}

	public int getPieceMoving() {
		return pieceMoving;
	}

	public boolean isCastling() {
		return isCastleKingside() || isCastleQueenside();
	}

	/**
	 * Returns the opposite move. If the position is symetrical returns the
	 * equivalent move for the other player. e.g. a3 -> a6 nf3 -> nf6
	 */
	public Move getOpposite() {
		Move result = (Move) clone();

		// file remains the same rank changes.
		result.startRank = Math.abs(7 - startRank);
		result.endRank = Math.abs(7 - endRank);

		result.epPawnRank = epPawnRank != -1 ? Math.abs(7 - epPawnRank) : -1;

		// flip
		result.isWhitesMove = !isWhitesMove;
		result.capturedPiece = PieceUtil.getOpposite(capturedPiece);
		result.pieceMoving = PieceUtil.getOpposite(pieceMoving);

		return result;
	}

	/**
	 * @return Returns the droppedPiece.
	 */
	public int getDroppedPiece() {
		return droppedPiece;
	}

	public boolean isDropMove() {
		return droppedPiece != Piece.NILL;
	}

}