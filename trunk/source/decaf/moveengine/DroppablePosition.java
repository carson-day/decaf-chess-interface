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
 * A position that supports droppable chess games such as crazyhosue and
 * bughouse, where a player can drop pieces a piece (other than a king) for a
 * move. Pawns can not be dropped on 1st or 8th ranks.
 */
public class DroppablePosition extends Position {
	private int[] whiteHoldings;

	private int[] blackHoldings;

	/**
	 * Constructs the begining position of a droppable chess game where neither
	 * side has any pieces.
	 */
	public DroppablePosition() {
		super();
		setWhiteHoldings(new int[] {});
		setBlackHoldings(new int[] {});
	}

	/**
	 * Constructs the begining position of a droppable chess game with the
	 * specified state.
	 */
	public DroppablePosition(int[][] board, boolean whiteCanCastleKingside,
			boolean whiteCanCastleQueenside, boolean blackCanCastleKingside,
			boolean blackCanCastleQueenside, int lastMoveDoublePawnPushFile,
			boolean isWhitesMove, int[] whiteHoldings, int[] blackHoldings) {
		super(board, whiteCanCastleKingside, whiteCanCastleQueenside,
				blackCanCastleKingside, blackCanCastleQueenside,
				lastMoveDoublePawnPushFile, isWhitesMove);
		this.whiteHoldings = whiteHoldings;
		this.blackHoldings = blackHoldings;
	}

	/**
	 * @return Returns the blackHoldings.
	 */
	@Override
	public int[] getBlackHoldings() {
		return blackHoldings;
	}

	/**
	 * Returns a list of all of the legal moves from this position.
	 */
	@Override
	public Move[] getLegalMoves() {
		return PositionUtil
				.getLegalDroppableChessMoves(getBoard(), getBoardState(),
						isWhitesMove() ? whiteHoldings : blackHoldings);
	}

	/**
	 * @return Returns the whiteHoldings.
	 */
	@Override
	public int[] getWhiteHoldings() {
		return whiteHoldings;
	}

	/**
	 * @param blackHoldings
	 *            The blackHoldings to set.
	 */
	@Override
	public void setBlackHoldings(int[] blackHoldings) {
		if (blackHoldings == null || blackHoldings.length == 0) {
			this.blackHoldings = new int[] {};
		} else {
			this.blackHoldings = new int[blackHoldings.length];

			for (int i = 0; i < blackHoldings.length; i++) {
				if (!PieceUtil.isBlackPiece(blackHoldings[i])) {
					throw new IllegalArgumentException(
							"blackHoldings must contain all black pieces");
				}
				if (PieceUtil.isKing(blackHoldings[i])) {
					throw new IllegalArgumentException(
							"blackHoldings can not contain a king");
				}
			}
		}
		this.blackHoldings = blackHoldings;
	}

	/**
	 * @param whiteHoldings
	 *            The whiteHoldings to set.
	 */
	@Override
	public void setWhiteHoldings(int[] whiteHoldings) {
		if (whiteHoldings == null || whiteHoldings.length == 0) {
			this.whiteHoldings = new int[] {};
		} else {
			this.whiteHoldings = new int[whiteHoldings.length];
			for (int i = 0; i < whiteHoldings.length; i++) {
				if (!PieceUtil.isWhitePiece(whiteHoldings[i])) {
					throw new IllegalArgumentException(
							"whiteHoldings must contain all white pieces");
				}
				if (PieceUtil.isKing(whiteHoldings[i])) {
					throw new IllegalArgumentException(
							"whiteHoldings can not contain a king");
				}
				this.whiteHoldings[i] = whiteHoldings[i];
			}
		}
	}
}