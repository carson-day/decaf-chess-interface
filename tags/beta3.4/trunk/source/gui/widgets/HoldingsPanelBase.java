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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.border.Border;

import decaf.gui.Disposable;
import decaf.gui.event.UserActionListener;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.BorderUtil;
import decaf.gui.util.TextProperties;
import decaf.moveengine.Piece;

// Referenced classes of package caffeine.gui.widgets:
// ChessBoardSquare, Skinnable, Skin, ChessSet,
// GUIMoveListener, ChessPiece

public abstract class HoldingsPanelBase extends JPanel implements Piece,
		Preferenceable, Disposable {

	public class ChessPieceNumberDecorator extends ChessPiece {

		public void setSizeConstraint(Dimension dimension) {
			decoratee.setSizeConstraint(dimension);
		}

		public ChessPiece cloneChessPiece() {
			return decoratee.cloneChessPiece();
		}

		public int getNumberOfPieces() {
			return numberOfPieces;
		}

		public void setNumberOfPieces(int i) {
			numberOfPieces = i;
			setTransparent(i == 0);
		}

		public void setPreferences(Preferences skin) {
			this.preferences = preferences;
			dummyPanel.setBackground(preferences.getBoardPreferences()
					.getDropSquareColor());
			repaint();
		}

		public void paint(Graphics g) {
			if (numberOfPieces > 0) {
				decoratee.setBounds(getBounds());
				decoratee.paint(g);
			}
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

		public void dispose() {
			removeAll();
			dummyPanel.removeAll();
			decoratee = null;
			preferences = null;
			dummyPanel = null;
		}

		private ChessPiece decoratee;

		private int numberOfPieces;

		private Preferences preferences;

		private JPanel dummyPanel;

		public ChessPieceNumberDecorator(ChessPiece chesspiece,
				Preferences preferences) {
			super(chesspiece.getType());
			setTransparent(true);
			decoratee = chesspiece;
			numberOfPieces = 0;
			this.preferences = preferences;
			dummyPanel = new JPanel();
			dummyPanel.setBackground(preferences.getBoardPreferences()
					.getDropSquareColor());
			dummyPanel.add(decoratee);
		}
	}

	private static final String EMPTY_BOARD_ID = "<EMPTY>";

	private int numPawns;

	private int numKnights;

	private int numBishops;

	private int numRooks;

	private int numQueens;

	protected ChessBoardSquare pawnSquare;

	protected ChessBoardSquare knightSquare;

	protected ChessBoardSquare bishopSquare;

	protected ChessBoardSquare rookSquare;

	protected ChessBoardSquare queenSquare;

	private ChessPieceNumberDecorator pawnNumberPiece;

	private ChessPieceNumberDecorator knightNumberPiece;

	private ChessPieceNumberDecorator bishopNumberPiece;

	private ChessPieceNumberDecorator rookNumberPiece;

	private ChessPieceNumberDecorator queenNumberPiece;

	private boolean representsLightPieces;

	private String boardId;

	private Preferences preferences;

	private Dimension lastSizeConstraint;

	public HoldingsPanelBase(boolean isWhiteDropPanel) {
		representsLightPieces = isWhiteDropPanel;
		this.boardId = boardId;

		pawnSquare = new ChessBoardSquare(preferences, "<EMPTY>",
				isWhiteDropPanel, representsLightPieces ? WP : BP);
		knightSquare = new ChessBoardSquare(preferences, "<EMPTY>",
				isWhiteDropPanel, representsLightPieces ? WN : BN);
		bishopSquare = new ChessBoardSquare(preferences, "<EMPTY>",
				isWhiteDropPanel, representsLightPieces ? WB : BB);
		rookSquare = new ChessBoardSquare(preferences, "<EMPTY>",
				isWhiteDropPanel, representsLightPieces ? WR : BR);
		queenSquare = new ChessBoardSquare(preferences, "<EMPTY>",
				isWhiteDropPanel, representsLightPieces ? WQ : BQ);

		pawnSquare.setBoardId("<EMPTY>");
		knightSquare.setBoardId("<EMPTY>");
		bishopSquare.setBoardId("<EMPTY>");
		rookSquare.setBoardId("<EMPTY>");
		queenSquare.setBoardId("<EMPTY>");

		setupLayout();
	}
	
	

	public boolean isRepresentsLightPieces() {
		return representsLightPieces;
	}



	public void setRepresentsLightPieces(boolean representsLightPieces) {
		this.representsLightPieces = representsLightPieces;
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

	public void addUserActionListener(UserActionListener guimovelistener) {
		pawnSquare.addGUIMoveListener(guimovelistener);
		knightSquare.addGUIMoveListener(guimovelistener);
		bishopSquare.addGUIMoveListener(guimovelistener);
		rookSquare.addGUIMoveListener(guimovelistener);
		queenSquare.addGUIMoveListener(guimovelistener);
	}

	public void removeUserActionListener(UserActionListener guimovelistener) {
		pawnSquare.removeGUIMoveListener(guimovelistener);
		knightSquare.removeGUIMoveListener(guimovelistener);
		bishopSquare.removeGUIMoveListener(guimovelistener);
		rookSquare.removeGUIMoveListener(guimovelistener);
		queenSquare.removeGUIMoveListener(guimovelistener);
	}

	private void initializePieceDecorators() {

		if (pawnNumberPiece != null) {
			pawnNumberPiece.dispose();
		}
		if (knightNumberPiece != null) {
			knightNumberPiece.dispose();
		}
		if (bishopNumberPiece != null) {
			bishopNumberPiece.dispose();
		}
		if (rookNumberPiece != null) {
			rookNumberPiece.dispose();
		}
		if (queenNumberPiece != null) {
			queenNumberPiece.dispose();
		}

		pawnNumberPiece = new ChessPieceNumberDecorator(preferences
				.getBoardPreferences().getSet().createChessPiece(
						representsLightPieces ? Piece.WHITE_PAWN
								: Piece.BLACK_PAWN), preferences);
		knightNumberPiece = new ChessPieceNumberDecorator(preferences
				.getBoardPreferences().getSet().createChessPiece(
						representsLightPieces ? Piece.WHITE_KNIGHT
								: Piece.BLACK_KNIGHT), preferences);
		bishopNumberPiece = new ChessPieceNumberDecorator(preferences
				.getBoardPreferences().getSet().createChessPiece(
						representsLightPieces ? Piece.WHITE_BISHOP
								: Piece.BLACK_BISHOP), preferences);
		rookNumberPiece = new ChessPieceNumberDecorator(preferences
				.getBoardPreferences().getSet().createChessPiece(
						representsLightPieces ? Piece.WHITE_ROOK
								: Piece.BLACK_ROOK), preferences);
		queenNumberPiece = new ChessPieceNumberDecorator(preferences
				.getBoardPreferences().getSet().createChessPiece(
						representsLightPieces ? Piece.WHITE_QUEEN
								: Piece.BLACK_QUEEN), preferences);

		pawnSquare.setPiece(pawnNumberPiece);
		knightSquare.setPiece(knightNumberPiece);
		bishopSquare.setPiece(bishopNumberPiece);
		rookSquare.setPiece(rookNumberPiece);
		queenSquare.setPiece(queenNumberPiece);

		setPieceLabels();
	}

	private void setPieceLabels() {
		pawnNumberPiece.setNumberOfPieces(getPawnCount());
		knightNumberPiece.setNumberOfPieces(getKnightCount());
		bishopNumberPiece.setNumberOfPieces(getBishopCount());
		rookNumberPiece.setNumberOfPieces(getRookCount());
		queenNumberPiece.setNumberOfPieces(getQueenCount());
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
				case Piece.WP: // '\001'
					pawn++;
					break;

				case Piece.WN: // '\002'
					knight++;
					break;

				case Piece.WB: // '\003'
					bishop++;
					break;

				case Piece.WR: // '\004'
					rook++;
					break;

				case Piece.WQ: // '\005'
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

		// setupLayout();
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
			ai[j++] = getPawnCount();

		for (int l = 0; l < getKnightCount(); l++)
			ai[j++] = getKnightCount();

		for (int i1 = 0; i1 < getBishopCount(); i1++)
			ai[j++] = getBishopCount();

		for (int j1 = 0; j1 < getRookCount(); j1++)
			ai[j++] = getRookCount();

		for (int k1 = 0; k1 < getQueenCount(); k1++)
			ai[j++] = getQueenCount();

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
		if (pawnSquare != null) {
			pawnSquare.dispose();
			pawnSquare = null;
		}
		if (knightSquare != null) {
			knightSquare.dispose();
			knightSquare = null;
		}
		if (bishopSquare != null) {
			bishopSquare.dispose();
			bishopSquare = null;
		}
		if (rookSquare != null) {
			rookSquare.dispose();
			rookSquare = null;
		}
		if (queenSquare != null) {
			queenSquare.dispose();
			queenSquare = null;
		}
		if (pawnNumberPiece != null) {
			pawnNumberPiece.dispose();
			pawnNumberPiece = null;
		}
		if (knightNumberPiece != null) {
			knightNumberPiece.dispose();
			knightNumberPiece = null;
		}
		if (bishopNumberPiece != null) {
			bishopNumberPiece.dispose();
			bishopNumberPiece = null;
		}
		if (rookNumberPiece != null) {
			rookNumberPiece.dispose();
			rookNumberPiece = null;
		}
		if (queenNumberPiece != null) {
			queenNumberPiece.dispose();
			queenNumberPiece = null;
		}

		boardId = null;
		preferences = null;
	}

}