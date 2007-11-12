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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import decaf.gui.Disposable;
import decaf.gui.event.UserActionListener;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.BorderUtil;
import decaf.gui.util.CoordinatesUtil;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;

public class ChessBoard extends JPanel implements Piece, Preferenceable,
		Disposable {
	private static final Logger LOGGER = Logger.getLogger(ChessBoard.class);

	private boolean isWhiteOnTop;

	private String id;

	private ChessBoardSquare squares[][];

	private Preferences preferences;

	private Vector guiMoveListeners;

	private Position position;

	private List selectedSquares = new LinkedList();

	private class BoardLayout implements LayoutManager2 {
		public void layoutContainer(Container arg0) {

			int width = arg0.getSize().width;
			int height = arg0.getSize().height;

			int squareSide = width > height ? height / 8 : width / 8;

			int x = 0;
			int y = 0;

			if (isWhiteOnTop) {
				for (int i = 7; i > -1; i--) {
					for (int j = 7; j > -1; j--) {

						squares[i][j].setBounds(x, y, squareSide, squareSide);
						x += squareSide;
					}
					x = 0;
					y += squareSide;

				}
			} else {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < squares[i].length; j++) {
						squares[i][j].setBounds(x, y, squareSide, squareSide);
						x += squareSide;
					}
					x = 0;
					y += squareSide;
				}
			}

		}

		public void addLayoutComponent(Component arg0, Object arg1) {

		}

		public float getLayoutAlignmentX(Container arg0) {

			return 0.5F;
		}

		public float getLayoutAlignmentY(Container arg0) {
			return 0.5F;
		}

		public void invalidateLayout(Container arg0) {

		}

		public Dimension maximumLayoutSize(Container arg0) {

			return new Dimension(10000, 10000);
		}

		public void addLayoutComponent(String arg0, Component arg1) {

		}

		public Dimension minimumLayoutSize(Container arg0) {
			return new Dimension(0, 0);
		}

		public Dimension preferredLayoutSize(Container arg0) {
			return new Dimension(0, 0);
		}

		public void removeLayoutComponent(Component arg0) {
		}
	}

	/*
	 * private KeyboardInputHandler handler;
	 * 
	 * public KeyboardInputHandler getKeyboardHandler() { return handler; }
	 */

	public void dispose() {
		if (guiMoveListeners != null) {
			guiMoveListeners.removeAllElements();
			guiMoveListeners = null;
		}

		if (squares != null) {
			for (int i = 0; i < squares.length; i++) {
				for (int j = 0; j < squares[i].length; j++) {
					squares[i][j].dispose();
				}
			}
		}
		squares = null;
		preferences = null;
		position = null;
	}

	public ChessBoard() {
		this.id = id;
		this.isWhiteOnTop = true;
		guiMoveListeners = new Vector(10);
		setupChessBoardSquares();
		setupLayout();
		// handler = new KeyboardInputHandler();
		setFocusable(true);
		// addKeyListener(getKeyboardHandler());
		/*
		 * for (int i = 0; i < squares.length; i++) { for (int j = 0; j <
		 * squares[i].length; j++) {
		 * squares[i][j].addKeyListener(getKeyboardHandler()); } }
		 */
	}

	public void setBoardId(String id) {
		this.id = id;
		for (int i = 0; i < 8; i++) {

			for (int j = 0; j < squares[i].length; j++) {
				squares[i][j].setBoardId(id);
			}
		}
	}

	public Dimension getChessBoardSquareSize() {
		return squares[0][0].getSize();
	}

	public void addUserActionListener(UserActionListener guimovelistener) {
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				squares[i][j].addGUIMoveListener(guimovelistener);
			}
		}

	}

	public void removeUserActionListener(UserActionListener guimovelistener) {
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				squares[i][i].removeGUIMoveListener(guimovelistener);
			}
		}
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		setBorder(BorderUtil.intToBorder(preferences.getBoardPreferences()
				.getChessBoardBorder()));
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				squares[i][j].setPreferences(preferences);
			}
		}

	}

	public Preferences getPreferences() {
		return preferences;
	}

	public Position getPosition() {
		return (Position) position.clone();
	}

	public void setPosition(Position position) {
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				get(i, j).changePiece(position.get(i, j));
			}
		}
		this.position = position;
	}

	public void flip() {
		isWhiteOnTop = !isWhiteOnTop;
	}

	public void setWhiteOnTop(boolean isWhiteOnTop) {
		if (!this.isWhiteOnTop == isWhiteOnTop) {
			flip();
		}
	}

	protected void setupLayout() {
		removeAll();
		setLayout(new BoardLayout());
		for (int i = 7; i > -1; i--) {
			for (int j = 7; j > -1; j--) {
				add(squares[i][j]);
			}
		}
	}

	public boolean isWhiteOnTop() {
		return isWhiteOnTop;
	}

	protected ChessBoardSquare get(int rank, int file) {
		return squares[rank][file];
	}

	protected ChessBoardSquare get(int[] coordinates) {
		return squares[coordinates[0]][coordinates[1]];
	}

	public void selectSquare(int[] coordinates) {
		if (CoordinatesUtil.isInBounds(coordinates)) {
			ChessBoardSquare square = squares[coordinates[0]][coordinates[1]];
			selectedSquares.add(square);
			square.select();
		} else {
		}
	}

	public void unselectSquare(int[] coordinates) {
		if (CoordinatesUtil.isInBounds(coordinates)) {
			ChessBoardSquare square = squares[coordinates[0]][coordinates[1]];

			for (int i = 0; i < selectedSquares.size(); i++) {
				if (selectedSquares.get(i) == square) {
					selectedSquares.remove(i);
					i--;
				}
			}
			square.unselect();
		} else {
		}

	}

	public void unselectAllSquares() {
		for (int i = 0; i < selectedSquares.size(); i++) {
			ChessBoardSquare square = (ChessBoardSquare) selectedSquares.get(i);
			square.unselect();
		}
		selectedSquares.clear();
	}

	private void setupChessBoardSquares() {
		squares = new ChessBoardSquare[8][];
		boolean isWhiteSquare = false;
		for (int i = 0; i < 8; i++) {
			squares[i] = new ChessBoardSquare[8];
			isWhiteSquare = !isWhiteSquare;

			for (int j = 0; j < squares[i].length; j++) {
				squares[i][j] = new ChessBoardSquare(preferences, "<EMPTY>",
						isWhiteSquare, i, j);
				squares[i][j].setFocusable(true);
				isWhiteSquare = !isWhiteSquare;
			}
		}
	}
}