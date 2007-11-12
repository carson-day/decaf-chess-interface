package decaf.gui.widgets;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class VerticalHoldingsPanel extends HoldingsPanelBase {

	private static final int MIN_HEIGHT = 15;

	private static final int MAX_HEIGHT = 40;

	public static final int NORTH_ORIENTATION = 1;

	public static final int SOUTH_ORIENTATION = 2;

	private int orientation = NORTH_ORIENTATION;

	private class VerticalLayout implements LayoutManager2 {
		public void layoutContainer(Container container) {
			int height = container.getSize().height;

			int squareSide = height < MIN_HEIGHT ? MIN_HEIGHT
					: height > MAX_HEIGHT ? MAX_HEIGHT : height;

			int startPoint = container.getWidth();
			int controlWidth = squareSide * 5;

			int startX = startPoint / 2 - controlWidth / 2;

			int y = orientation == NORTH_ORIENTATION ? 0 : height - squareSide;

			pawnSquare.setBounds(startX, y, squareSide, squareSide);
			knightSquare.setBounds(startX + squareSide, y, squareSide,
					squareSide);
			queenSquare.setBounds(startX + squareSide * 2, y, squareSide,
					squareSide);
			bishopSquare.setBounds(startX + squareSide * 3, y, squareSide,
					squareSide);
			rookSquare.setBounds(startX + squareSide * 4, y, squareSide,
					squareSide);
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

	public VerticalHoldingsPanel(boolean isWhiteDropPanel) {
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
		setLayout(new VerticalLayout());
		add(pawnSquare);
		add(knightSquare);
		add(queenSquare);
		add(bishopSquare);
		add(rookSquare);
	}
}
