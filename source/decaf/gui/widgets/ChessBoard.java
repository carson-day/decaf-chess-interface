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
package decaf.gui.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import decaf.gui.event.UserMoveInputListener;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;
import decaf.util.BorderUtil;
import decaf.util.CoordinatesUtil;

public class ChessBoard extends JPanel implements Piece, Preferenceable,
		Disposable {
	private static final Logger LOGGER = Logger.getLogger(ChessBoard.class);

	private boolean isWhiteOnTop;

	private String id;

	private ChessBoardSquare squares[][];

	private Preferences preferences;

	private Position position;

	private int squareSide;
	
	private int coordinatesHeight;

	private JLabel[] rankLabels = new JLabel[] { new JLabel("8"),
			new JLabel("7"), new JLabel("6"), new JLabel("5"), new JLabel("4"),
			new JLabel("3"), new JLabel("2"), new JLabel("1") };

	private JLabel[] fileLabels = new JLabel[] { new JLabel("a"),
			new JLabel("b"), new JLabel("c"), new JLabel("d"), new JLabel("e"),
			new JLabel("f"), new JLabel("g"), new JLabel("h") };

	private class BoardLayout implements LayoutManager2 {
		public void layoutContainer(Container arg0) {

			int width = arg0.getSize().width;
			int height = arg0.getSize().height;

			int x, y, xInit;

			if (getPreferences().getBoardPreferences().isShowingCoordinates()) {
				squareSide = width > height ? height / 8 : width / 8;

				int charWidth = SwingUtilities.computeStringWidth(rankLabels[0]
						.getFontMetrics(rankLabels[0].getFont()), "1") + 5;
				int charHeight = rankLabels[0].getFontMetrics(
						rankLabels[0].getFont()).getHeight() + 5;

				squareSide -= Math.round(charWidth / 8.0);

				x = charWidth;
				xInit = charWidth;
				y = 0;

				for (int i = 0; i < rankLabels.length; i++) {
					int multiplier = (isWhiteOnTop ? 7 - i : i);
					rankLabels[i].setLocation(0, (int) (squareSide * multiplier
							+ squareSide / 2 - .4 * charHeight));
					rankLabels[i].setSize(rankLabels[i].getPreferredSize());
				}

				for (int i = 0; i < fileLabels.length; i++) {
					int multiplier = (isWhiteOnTop ? 7 - i : i);
					fileLabels[i].setLocation((int) (charHeight * .4
							+ squareSide * multiplier + squareSide / 2),
							(int) (squareSide * 8));
					fileLabels[i].setSize(fileLabels[i].getPreferredSize());
				}
				
				coordinatesHeight = charHeight;

			} else {
				squareSide = width > height ? height / 8 : width / 8;
				x = 0;
				xInit = 0;
				y = 0;
				coordinatesHeight = 0;
			}

			if (isWhiteOnTop) {
				for (int i = 7; i > -1; i--) {
					for (int j = 7; j > -1; j--) {

						squares[i][j].setBounds(x, y, squareSide, squareSide);
						x += squareSide;
					}
					x = xInit;
					y += squareSide;

				}
			} else {
				for (int i = 0; i < 8; i++) {
					for (int j = 0; j < squares[i].length; j++) {
						squares[i][j].setBounds(x, y, squareSide, squareSide);
						x += squareSide;
					}
					x = xInit;
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

	public ChessBoard() {
		this.isWhiteOnTop = false;
		setupChessBoardSquares();
		setupLayout();
		setFocusable(false);
	}

	public void setUserMoveInputListener(UserMoveInputListener listener) {
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				squares[i][j].setUserMoveInputListener(listener);
			}
		}
	}

	public boolean isMoveable() {
		return squares[0][0].isMoveable();

	}

	public void setMoveable(boolean isMoveable) {
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				squares[i][j].setMoveable(isMoveable);
			}
		}
	}

	public int getSquareSide() {
		return squareSide;
	}

	public void dispose() {

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

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		setBorder(BorderUtil.intToBorder(preferences.getBoardPreferences()
				.getChessBoardBorder()));
		for (int i = 0; i < squares.length; i++) {
			for (int j = 0; j < squares[i].length; j++) {
				squares[i][j].setPreferences(preferences);
			}
		}

		for (int i = 0; i < fileLabels.length; i++) {
			fileLabels[i].setBackground(preferences.getBoardPreferences()
					.getBackgroundControlsColor());
			fileLabels[i].setForeground(preferences.getBoardPreferences()
					.getControlLabelTextProperties().getForeground());
			fileLabels[i].setFont(preferences.getBoardPreferences()
					.getControlLabelTextProperties().getFont());
		}

		for (int i = 0; i < rankLabels.length; i++) {
			rankLabels[i].setBackground(preferences.getBoardPreferences()
					.getBackgroundControlsColor());
			rankLabels[i].setForeground(preferences.getBoardPreferences()
					.getControlLabelTextProperties().getForeground());
			rankLabels[i].setFont(preferences.getBoardPreferences()
					.getControlLabelTextProperties().getFont());
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
				get(i, j).setPiece(position.get(i, j));
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

		for (int i = 0; i < fileLabels.length; i++) {
			add(fileLabels[i]);
		}

		for (int i = 0; i < rankLabels.length; i++) {
			add(rankLabels[i]);
		}
	}

	public boolean isWhiteOnTop() {
		return isWhiteOnTop;
	}

	public ChessBoardSquare get(int rank, int file) {
		return squares[rank][file];
	}

	public ChessBoardSquare get(int[] coordinates) {
		return squares[coordinates[0]][coordinates[1]];
	}

	public void selectSquare(int[] coordinates) {
		if (CoordinatesUtil.isInBounds(coordinates)) {
			ChessBoardSquare square = squares[coordinates[0]][coordinates[1]];
			square.select();
		} else {
		}
	}
	
	public void preSelectSquare(int[] coordinates, int index) {
		if (CoordinatesUtil.isInBounds(coordinates)) {
			ChessBoardSquare square = squares[coordinates[0]][coordinates[1]];
			square.preSelect(index);
		} else {
		}
	}

	public void unselectSquare(int[] coordinates) {
		if (CoordinatesUtil.isInBounds(coordinates)) {
			ChessBoardSquare square = squares[coordinates[0]][coordinates[1]];
   		    square.unselect();
		} else {
		}

	}

	public void unselectAllSquares() {
		for (int i = 7; i > -1; i--) {
			for (int j = 7; j > -1; j--) {
				squares[i][j].unselect();
			}
		}
	}
	
	

	public int getCoordinatesHeight() {
		return coordinatesHeight;
	}

	public void setCoordinatesHeight(int coordinatesHeight) {
		this.coordinatesHeight = coordinatesHeight;
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