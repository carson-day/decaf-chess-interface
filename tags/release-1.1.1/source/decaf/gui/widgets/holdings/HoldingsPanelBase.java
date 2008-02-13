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

package decaf.gui.widgets.holdings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.Border;

import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.widgets.ChessBoardSquare;
import decaf.gui.widgets.Disposable;
import decaf.moveengine.Piece;
import decaf.util.BorderUtil;
import decaf.util.TextProperties;

public abstract class HoldingsPanelBase extends JPanel implements Piece,
		Preferenceable, Disposable {

	public class DecoratedChessBoardSquare extends ChessBoardSquare {

		public DecoratedChessBoardSquare(Preferences preferences,
				String boardID, boolean isWhiteSquare, int dropPiece) {
			super(preferences, boardID, isWhiteSquare, dropPiece);
		}

		public int getNumberOfPieces() {
			return numberOfPieces;
		}

		public void setNumberOfPieces(int pieces) {
			this.numberOfPieces = pieces;
			repaint();
		}

		public void setPreferences(Preferences skin) {
			if (skin != null) {
				super.setPreferences(skin);

				setBackground(skin.getBoardPreferences().getDropSquareColor());

				repaint();
			}
		}

		protected void onDragStart() {
			numberOfPieces--;
			if (numberOfPieces == 0) {
				setChessPieceTransparent(true);
				repaint();
			} else {
				repaint();
			}
		}

		// A successful drop will work because the holdings will be set and the
		// square redrawn from ChessBoardSquare.

		protected void onUnsuccessfulDropEnd() {
			numberOfPieces++;
			super.onUnsuccessfulDropEnd();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (numberOfPieces > 1) {
				TextProperties dropSquareLabelProperties = preferences
						.getBoardPreferences()
						.getDropSquareLabelTextProperties();

				Font font = new Font(dropSquareLabelProperties.getFont()
						.getFontName(), dropSquareLabelProperties.getFont()
						.getStyle(), Math.min(getHeight(), getWidth()) / 3);

				String label = "" + numberOfPieces;
				g.setFont(font);

				// get bounding rectangle:
				FontMetrics metrics = g.getFontMetrics();
				Rectangle2D fontBounds = metrics.getStringBounds(label, 0,
						label.length(), g);

				g.setColor(dropSquareLabelProperties.getForeground());
				g.drawString(label, 0, metrics.getAscent());
			}
		}

		private int numberOfPiecesPreDrag;

		private int numberOfPieces;

	}

	private static final String EMPTY_BOARD_ID = "<EMPTY>";

	private int numPawns;

	private int numKnights;

	private int numBishops;

	private int numRooks;

	private int numQueens;

	protected DecoratedChessBoardSquare pawnSquare;

	protected DecoratedChessBoardSquare knightSquare;

	protected DecoratedChessBoardSquare bishopSquare;

	protected DecoratedChessBoardSquare rookSquare;

	protected DecoratedChessBoardSquare queenSquare;

	private boolean representsLightPieces;

	private String boardId;

	private Preferences preferences;

	private Dimension lastSizeConstraint;

	private HoldingsPanelBase thisBase = this;

	public HoldingsPanelBase(boolean isWhiteDropPanel) {
		representsLightPieces = isWhiteDropPanel;
		this.boardId = boardId;

		pawnSquare = new DecoratedChessBoardSquare(preferences, EMPTY_BOARD_ID,
				isWhiteDropPanel, representsLightPieces ? WP : BP);
		knightSquare = new DecoratedChessBoardSquare(preferences,
				EMPTY_BOARD_ID, isWhiteDropPanel, representsLightPieces ? WN
						: BN);
		bishopSquare = new DecoratedChessBoardSquare(preferences,
				EMPTY_BOARD_ID, isWhiteDropPanel, representsLightPieces ? WB
						: BB);
		rookSquare = new DecoratedChessBoardSquare(preferences, EMPTY_BOARD_ID,
				isWhiteDropPanel, representsLightPieces ? WR : BR);
		queenSquare = new DecoratedChessBoardSquare(preferences,
				EMPTY_BOARD_ID, isWhiteDropPanel, representsLightPieces ? WQ
						: BQ);
		
		add(pawnSquare);
		add(knightSquare);
		add(bishopSquare);
		add(queenSquare);
		add(rookSquare);

		setupLayout();

		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				getLayout().layoutContainer(thisBase);
			}

		});
	}

	public synchronized ChessBoardSquare[] getSquaresWithPieces() {
		List<ChessBoardSquare> result = new ArrayList<ChessBoardSquare>(5);

		//add squares with pieces.
		if (getPawnCount() > 0) {
			result.add(pawnSquare);
		}
		if (getKnightCount() > 0) {
			result.add(knightSquare);
		}
		if (getBishopCount() > 0) {
			result.add(bishopSquare);
		}
		if (getRookCount() > 0) {
			result.add(rookSquare);
		}
		if (getQueenCount() > 0) {
			result.add(queenSquare);
		}
		
		//add empty squares.
		if (getPawnCount() == 0) {
			result.add(pawnSquare);
		}
		if (getKnightCount() == 0) {
			result.add(knightSquare);
		}
		if (getBishopCount() == 0) {
			result.add(bishopSquare);
		}
		if (getRookCount() == 0) {
			result.add(rookSquare);
		}
		if (getQueenCount() == 0) {
			result.add(queenSquare);
		}

		return result.toArray(new ChessBoardSquare[0]);
	}

	public boolean isMoveable() {
		return pawnSquare.isMoveable();
	}

	public void setMoveable(boolean isMoveable) {
		pawnSquare.setMoveable(isMoveable);
		knightSquare.setMoveable(isMoveable);
		bishopSquare.setMoveable(isMoveable);
		rookSquare.setMoveable(isMoveable);
		queenSquare.setMoveable(isMoveable);
	}

	public boolean isRepresentsLightPieces() {
		return representsLightPieces;
	}

	public void setRepresentsLightPieces(boolean representsLightPieces) {
		this.representsLightPieces = representsLightPieces;

		pawnSquare.setDropPiece(representsLightPieces ? WP : BP);
		knightSquare.setDropPiece(representsLightPieces ? WN : BN);
		bishopSquare.setDropPiece(representsLightPieces ? WB : BB);
		rookSquare.setDropPiece(representsLightPieces ? WR : BR);
		queenSquare.setDropPiece(representsLightPieces ? WQ : BQ);

		invalidate();
		validate();
	}

	public Dimension getLastSizeConstraint() {
		return lastSizeConstraint;
	}

	public void setLastSizeConstraint(Dimension lastSizeConstraint) {
		this.lastSizeConstraint = lastSizeConstraint;

		System.err.println("Setting size constraint "
				+ lastSizeConstraint.width + " " + lastSizeConstraint.height);
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
		pawnSquare.setBoardId(boardId);
		knightSquare.setBoardId(boardId);
		bishopSquare.setBoardId(boardId);
		rookSquare.setBoardId(boardId);
		queenSquare.setBoardId(boardId);
	}

	private void initializePieceDecorators() {
		setPieceLabels();
	}

	private void setPieceLabels() {
		if (getPawnCount() == 0) {
			pawnSquare.clear();
		} else {
			pawnSquare.setPiece(representsLightPieces ? Piece.WP : Piece.BP);
		}
		if (getBishopCount() == 0) {
			bishopSquare.clear();
		} else {
			bishopSquare.setPiece(representsLightPieces ? Piece.WB : Piece.BB);
		}
		if (getKnightCount() == 0) {
			knightSquare.clear();
		} else {
			knightSquare.setPiece(representsLightPieces ? Piece.WN : Piece.BN);
		}
		if (getRookCount() == 0) {
			rookSquare.clear();
		} else {
			rookSquare.setPiece(representsLightPieces ? Piece.WR : Piece.BR);
		}
		if (getQueenCount() == 0) {
			queenSquare.clear();
		} else {
			queenSquare.setPiece(representsLightPieces ? Piece.WQ : Piece.BQ);
		}

		pawnSquare.setNumberOfPieces(getPawnCount());
		knightSquare.setNumberOfPieces(getKnightCount());
		bishopSquare.setNumberOfPieces(getBishopCount());
		rookSquare.setNumberOfPieces(getRookCount());
		queenSquare.setNumberOfPieces(getQueenCount());
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;

		if (preferences != null) {

			pawnSquare.setPreferences(preferences);
			knightSquare.setPreferences(preferences);
			bishopSquare.setPreferences(preferences);
			rookSquare.setPreferences(preferences);
			queenSquare.setPreferences(preferences);

			initializePieceDecorators();

			setBackground(preferences.getBoardPreferences()
					.getBackgroundControlsColor());
			setupLayout();
			Color dropSquareColor = preferences.getBoardPreferences()
					.getDropSquareColor();

			Border dropSquareBorder = BorderUtil.intToBorder(preferences
					.getBoardPreferences().getDropSquareBorder());

			pawnSquare.setBackground(dropSquareColor);
			knightSquare.setBackground(dropSquareColor);
			bishopSquare.setBackground(dropSquareColor);
			queenSquare.setBackground(dropSquareColor);
			rookSquare.setBackground(dropSquareColor);
			pawnSquare.setBorder(dropSquareBorder);
			knightSquare.setBorder(dropSquareBorder);
			bishopSquare.setBorder(dropSquareBorder);
			queenSquare.setBorder(dropSquareBorder);
			rookSquare.setBorder(dropSquareBorder);
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	private boolean intArraysAreEqual(int ai[], int ai1[]) {
		boolean flag = false;
		if (ai.length == ai1.length) {
			flag = true;
			Arrays.sort(ai);
			Arrays.sort(ai1);
			for (int i = 0; flag && i < ai.length; i++)
				flag = ai[i] == ai1[i];

		}
		return flag;
	}

	public void setFromPieceArray(int ai[]) {
		if (intArraysAreEqual(ai, getPieceArray()))
			return;
		int pawn = 0;
		int knight = 0;
		int bishop = 0;
		int rook = 0;
		int queen = 0;
		if (representsLightPieces) {
			for (int j1 = 0; j1 < ai.length; j1++)
				switch (ai[j1]) {
				case Piece.WP:
					pawn++;
					break;

				case Piece.WN:
					knight++;
					break;

				case Piece.WB:
					bishop++;
					break;

				case Piece.WR:
					rook++;
					break;

				case Piece.WQ:
					queen++;
					break;

				default:
					throw new RuntimeException("Invalid piece encountered: "
							+ ai[j1]);
				}

		} else {
			for (int k1 = 0; k1 < ai.length; k1++)
				switch (ai[k1]) {
				case Piece.BP:
					pawn++;
					break;

				case Piece.BN:
					knight++;
					break;

				case Piece.BB:
					bishop++;
					break;

				case Piece.BR:
					rook++;
					break;

				case Piece.BQ:
					queen++;
					break;

				default:
					throw new RuntimeException("Invalid piece encountered: "
							+ ai[k1]);
				}

		}
		setPawnCount(pawn);
		setKnightCount(knight);
		setBishopCount(bishop);
		setRookCount(rook);
		setQueenCount(queen);
		pawnSquare.setBoardId(pawn != 0 ? boardId : "<EMPTY>");
		knightSquare.setBoardId(knight != 0 ? boardId : "<EMPTY>");
		bishopSquare.setBoardId(bishop != 0 ? boardId : "<EMPTY>");
		rookSquare.setBoardId(rook != 0 ? boardId : "<EMPTY>");
		queenSquare.setBoardId(queen != 0 ? boardId : "<EMPTY>");
		setPieceLabels();

		invalidate();
		validate();
		repaint();
	}

	public int numPiecesFromSquareIdentifier(int i) {
		int j;
		if (representsLightPieces)
			switch (i) {
			case 100: // 'd'
				j = getPawnCount();
				break;

			case 101: // 'e'
				j = getKnightCount();
				break;

			case 102: // 'f'
				j = getBishopCount();
				break;

			case 103: // 'g'
				j = getRookCount();
				break;

			case 104: // 'h'
				j = getQueenCount();
				break;

			default:
				throw new RuntimeException("Invalid holding square: " + i);
			}
		else
			switch (i) {
			case 201:
				j = getPawnCount();
				break;

			case 202:
				j = getKnightCount();
				break;

			case 203:
				j = getBishopCount();
				break;

			case 204:
				j = getRookCount();
				break;

			case 205:
				j = getQueenCount();
				break;

			default:
				throw new RuntimeException("Invalid holding square: " + i);
			}
		return j;
	}

	public int[] getPieceArray() {
		int ai[] = null;
		int i = getPawnCount() + getKnightCount() + getBishopCount()
				+ getRookCount() + getQueenCount();
		ai = new int[i];
		int j = 0;
		for (int k = 0; k < getPawnCount(); k++)
			ai[j++] = representsLightPieces ? Piece.WP : Piece.BP;

		for (int l = 0; l < getKnightCount(); l++)
			ai[j++] = representsLightPieces ? Piece.WN : Piece.BN;

		for (int i1 = 0; i1 < getBishopCount(); i1++)
			ai[j++] = representsLightPieces ? Piece.WB : Piece.BB;

		for (int j1 = 0; j1 < getRookCount(); j1++)
			ai[j++] = representsLightPieces ? Piece.WR : Piece.BR;

		for (int k1 = 0; k1 < getQueenCount(); k1++)
			ai[j++] = representsLightPieces ? Piece.WQ : Piece.BQ;

		return ai;
	}

	public int getPawnCount() {
		return numPawns;
	}

	public int getKnightCount() {
		return numKnights;
	}

	public int getBishopCount() {
		return numBishops;
	}

	public int getRookCount() {
		return numRooks;
	}

	public int getQueenCount() {
		return numQueens;
	}

	private synchronized void setPawnCount(int i) {
		if (i == numPawns)
			return;
		if (i < 0) {
			throw new RuntimeException("newPawnCount can't be negative.");
		} else {
			numPawns = i;
			return;
		}
	}

	private synchronized void setKnightCount(int i) {
		if (i == numKnights)
			return;
		if (i < 0) {
			throw new RuntimeException("newKnightCount can't be negative.");
		} else {
			numKnights = i;
			return;
		}
	}

	private synchronized void setBishopCount(int i) {
		if (i == numBishops)
			return;
		if (i < 0) {
			throw new RuntimeException("newBishopCount can't be negative.");
		} else {
			numBishops = i;
			return;
		}
	}

	private synchronized void setRookCount(int i) {
		if (i == numRooks)
			return;
		if (i < 0) {
			throw new RuntimeException("newRookCount can't be negative.");
		} else {
			numRooks = i;
			return;
		}
	}

	private synchronized void setQueenCount(int i) {
		if (i == numQueens)
			return;
		if (i < 0) {
			throw new RuntimeException("newQueenCount can't be negative.");
		} else {
			numQueens = i;
			return;
		}
	}

	protected abstract void setupLayout();

	public void dispose() {
		removeAll();
		if (pawnSquare != null) {
			pawnSquare.dispose();
		}
		if (knightSquare != null) {
			knightSquare.dispose();
		}
		if (bishopSquare != null) {
			bishopSquare.dispose();
		}
		if (rookSquare != null) {
			rookSquare.dispose();
		}
		if (queenSquare != null) {
			queenSquare.dispose();
		}
	}

}