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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import decaf.gui.widgets.ChessBoard;
import decaf.gui.widgets.ChessBoardSquare;

public class LeftRightHoldingsPanel extends HoldingsPanelBase {

	private class LeftRightLayout implements LayoutManager2 {
		public void addLayoutComponent(Component arg0, Object arg1) {

		}

		public void addLayoutComponent(String arg0, Component arg1) {

		}

		public float getLayoutAlignmentX(Container arg0) {
			return 0.5F;
		}

		public float getLayoutAlignmentY(Container arg0) {
			return 0.5F;
		}

		public void invalidateLayout(Container arg0) {

		}

		public void layoutContainer(Container container) {

			int height = container.getSize().height;
			int width = container.getSize().width;
			int squareSide = board != null ? board.getSquareSide() : height / 8;
			int x = 0, y = 0;
			ChessBoardSquare[] squares = getSquaresWithPieces();

			if (orientation == LEFT_ORIENTATION) {
				x = width - squareSide;
				y = 0;
			} else {
				x = 0;
				y = height - squares.length * squareSide;
			}

			for (int i = 0; i < squares.length; i++) {
				switch (i) {
				case 0: {
					squares[0].setBounds(x, y, squareSide, squareSide);

					break;
				}
				case 1: {
					squares[1].setBounds(x, y + squareSide, squareSide,
							squareSide);
					break;
				}
				case 2: {
					squares[2].setBounds(x, y + 2 * squareSide, squareSide,
							squareSide);
					break;
				}
				case 3: {
					squares[3].setBounds(x, y + 3 * squareSide, squareSide,
							squareSide);
					break;
				}
				case 4: {
					squares[4].setBounds(x, y + 4 * squareSide, squareSide,
							squareSide);

					break;
				}
				default: {
					throw new IllegalStateException(
							"Invalid number of squares with pieces "
									+ squares.length);
				}
				}

			}
		}

		public Dimension maximumLayoutSize(Container arg0) {

			return new Dimension(10000, 10000);
		}

		public Dimension minimumLayoutSize(Container arg0) {
			// TODO Auto-generated method stub
			return new Dimension(0, 0);
		}

		public Dimension preferredLayoutSize(Container arg0) {
			// TODO Auto-generated method stub
			return new Dimension(0, 0);
		}

		public void removeLayoutComponent(Component arg0) {
			// TODO Auto-generated method stub

		}
	}

	public static final int LEFT_ORIENTATION = 1;

	public static final int RIGHT_ORIENTATION = 2;

	private int orientation;

	private ChessBoard board;

	public LeftRightHoldingsPanel(ChessBoard board, boolean isWhiteDropPanel) {
		super(isWhiteDropPanel);
		this.board = board;
	}

	@Override
	public Dimension getMinimumSize() {
		if (board != null) {
			return (new Dimension(board.getSquareSide(),
					board.getSquareSide() * 4));
		} else {
			return super.getMinimumSize();
		}
	}

	public int getOrientation() {
		return orientation;
	}

	@Override
	public Dimension getPreferredSize() {
		if (board != null) {
			return (new Dimension(board.getSquareSide(),
					board.getSquareSide() * 4));
		} else {
			return super.getPreferredSize();
		}
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	@Override
	protected void setupLayout() {
		removeAll();
		setLayout(new LeftRightLayout());
	}
}
