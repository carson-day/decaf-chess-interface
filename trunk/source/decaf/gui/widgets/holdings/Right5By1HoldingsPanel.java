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

public class Right5By1HoldingsPanel extends HoldingsPanelBase {

	private class HorizontalLayout implements LayoutManager2 {
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

		public void layoutContainer(Container arg0) {
			// ChessBoardSquare squares[] = getOrderToAddPiecesIn();
			int width = arg0.getSize().width;
			int height = arg0.getSize().height;

			int squareSide = board != null ? board.getSquareSide() : width / 5;

			ChessBoardSquare[] squares = getSquaresWithPieces();

			for (int i = 0; i < squares.length; i++) {
				switch (i) {
				case 0: {
					squares[0].setBounds(0, 0, squareSide, squareSide);

					break;
				}
				case 1: {
					squares[1].setBounds(squareSide, 0, squareSide, squareSide);
					break;
				}
				case 2: {
					squares[1].setBounds(2 * squareSide, 0, squareSide,
							squareSide);
					break;
				}
				case 3: {
					squares[1].setBounds(3 * squareSide, 0, squareSide,
							squareSide);
					break;
				}
				case 4: {
					squares[1].setBounds(4 * squareSide, 0, squareSide,
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
			return new Dimension(board.getChessBoardSquareSize().width * 3,
					board.getChessBoardSquareSize().width);
		}

		public Dimension preferredLayoutSize(Container arg0) {
			return new Dimension(board.getChessBoardSquareSize().width * 3,
					board.getChessBoardSquareSize().width);
		}

		public void removeLayoutComponent(Component arg0) {
		}
	}

	private ChessBoard board;

	public Right5By1HoldingsPanel(ChessBoard board, boolean isWhiteDropPanel) {
		super(isWhiteDropPanel);
		this.board = board;
	}

	public Dimension minimumLayoutSize(Container arg0) {
		return new Dimension(board.getChessBoardSquareSize().width * 2, board
				.getChessBoardSquareSize().width * 4);
	}

	public Dimension preferredLayoutSize(Container arg0) {
		return new Dimension(board.getChessBoardSquareSize().width * 2, board
				.getChessBoardSquareSize().width * 4);
	}

	@Override
	protected void setupLayout() {
		setLayout(new HorizontalLayout());
	}
}
