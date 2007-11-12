package decaf.gui.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class HorizontalHoldingsPanel extends HoldingsPanelBase {

	private class HorizontalLayout implements LayoutManager2 {
		public void layoutContainer(Container arg0) {
			// ChessBoardSquare squares[] = getOrderToAddPiecesIn();
			int width = arg0.getSize().width;
			int height = arg0.getSize().height;

			int squareSide = height / 4;

			pawnSquare.setBounds(0, 0, squareSide, squareSide);
			knightSquare.setBounds(squareSide, 0, squareSide, squareSide);
			queenSquare.setBounds(0, squareSide, squareSide, squareSide);
			bishopSquare.setBounds(0, 2 * squareSide, squareSide, squareSide);
			rookSquare.setBounds(0, 3 * squareSide, squareSide, squareSide);
		}

		public void addLayoutComponent(Component arg0, Object arg1) {
			// TODO Auto-generated method stub

		}

		public float getLayoutAlignmentX(Container arg0) {
			// TODO Auto-generated method stub
			return 0.5F;
		}

		public float getLayoutAlignmentY(Container arg0) {
			// TODO Auto-generated method stub
			return 0.5F;
		}

		public void invalidateLayout(Container arg0) {
			// TODO Auto-generated method stub

		}

		public Dimension maximumLayoutSize(Container arg0) {

			return new Dimension(10000, 10000);
		}

		public void addLayoutComponent(String arg0, Component arg1) {
			// TODO Auto-generated method stub

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

	public HorizontalHoldingsPanel(boolean isWhiteDropPanel) {
		super(isWhiteDropPanel);
	}

	protected void setupLayout() {
		removeAll();
		setLayout(new HorizontalLayout());
		add(pawnSquare);
		add(knightSquare);
		add(queenSquare);
		add(bishopSquare);
		add(rookSquare);
	}
}
