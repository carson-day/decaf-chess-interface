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

public class TopBottomHoldingsPanel extends HoldingsPanelBase {

	private class VerticalLayout implements LayoutManager2 {
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
			ChessBoardSquare[] squares = getSquaresWithPieces();

			int squareSide = board != null ? board.getSquareSide()
					: height < MIN_HEIGHT ? MIN_HEIGHT
							: height > MAX_HEIGHT ? MAX_HEIGHT : height;

			int startPoint = container.getWidth();
			int controlWidth = squareSide * squares.length;

			int startX = startPoint / 2 - controlWidth / 2;

			int y = orientation == SOUTH_ORIENTATION ? 0 : height - squareSide;

			for (int i = 0; i < squares.length; i++) {
				switch (i) {
				case 0: {
					squares[0].setBounds(startX, y, squareSide, squareSide);

					break;
				}
				case 1: {
					squares[1].setBounds(startX + squareSide, y, squareSide,
							squareSide);
					break;
				}
				case 2: {
					squares[2].setBounds(startX + squareSide * 2, y,
							squareSide, squareSide);
					break;
				}
				case 3: {
					squares[3].setBounds(startX + squareSide * 3, y,
							squareSide, squareSide);
					break;
				}
				case 4: {
					squares[4].setBounds(startX + squareSide * 4, y,
							squareSide, squareSide);
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
			return new Dimension(0, 0);
		}

		public Dimension preferredLayoutSize(Container arg0) {
			return new Dimension(0, 0);
		}

		public void removeLayoutComponent(Component arg0) {

		}
	}

	private static final int MIN_HEIGHT = 15;

	private static final int MAX_HEIGHT = 40;

	public static final int NORTH_ORIENTATION = 1;

	public static final int SOUTH_ORIENTATION = 2;

	private int orientation = NORTH_ORIENTATION;

	private ChessBoard board;

	public TopBottomHoldingsPanel(ChessBoard board, boolean isWhiteDropPanel) {
		super(isWhiteDropPanel);
		this.board = board;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	@Override
	protected void setupLayout() {
		setLayout(new VerticalLayout());
	}
}
