package decaf.gui.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class LeftRightHoldingsPanel extends HoldingsPanelBase {

	public static final int LEFT_ORIENTATION = 1;

	public static final int RIGHT_ORIENTATION = 2;

	private int orientation;

	private class LeftRightLayout implements LayoutManager2 {
		public void layoutContainer(Container container) {

			int height = container.getSize().height;
			int width = container.getSize().width;
			int squareSide = height / 8;
			int x = 0, y = 0;

			if (orientation == LEFT_ORIENTATION) {
				x = width - squareSide;
				y = 0;
			} else {
				x = 0;
				y = 3 * squareSide;
			}

			pawnSquare.setBounds(x, y, squareSide, squareSide);
			knightSquare.setBounds(x, y + squareSide, squareSide, squareSide);
			queenSquare.setBounds(x, y + 2 * squareSide, squareSide, squareSide);
			bishopSquare.setBounds(x, y + 3 * squareSide, squareSide,squareSide);
			rookSquare.setBounds(x, y + 4 * squareSide, squareSide, squareSide);

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

	public LeftRightHoldingsPanel(boolean isWhiteDropPanel) {
		super(isWhiteDropPanel);
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	protected void setupLayout() {
		removeAll();
		setLayout(new LeftRightLayout());
		add(pawnSquare);
		add(knightSquare);
		add(queenSquare);
		add(bishopSquare);
		add(rookSquare);
	}
}
