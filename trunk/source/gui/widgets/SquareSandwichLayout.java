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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * A layout which always makes the Center component a perfect square. This
 * layout manager requres a NORTH,SOUTH,EAST,WEST and CENTER copmonent. They all
 * must be added. If type NORTH_SOUTH_FIXED is used the north/south components
 * are guarenteed to atleast have a percentage of the height. If type
 * EAST_WEST_FIXED is used the east/west components are guarenteed to atleast
 * have a percentage of the width. If type WEST_FIXED is used the east component
 * is guarenteed to atleast have a percentage of the width. If type NONE_FIXED
 * is used the the center component will be made as big as possible(keeping it a
 * perfect square).
 */
public class SquareSandwichLayout implements LayoutManager2 {
	public static final int NONE_FIXED = 0;

	public static final int NORTH_SOUTH_FIXED = 1;

	public static final int EAST_WEST_FIXED = 2;

	public static final int WEST_FIXED = 3;

	public static final String NORTH = BorderLayout.NORTH;

	public static final String SOUTH = BorderLayout.SOUTH;

	public static final String EAST = BorderLayout.EAST;

	public static final String WEST = BorderLayout.WEST;

	public static final String CENTER = BorderLayout.CENTER;

	private Component northComponent;

	private Component southComponent;

	private Component eastComponent;

	private Component westComponent;

	private Component centerComponent;

	private int type;

	public SquareSandwichLayout(int type) {
		validateType(type);
		this.type = type;
	}

	public void addLayoutComponent(String name, Component component) {
		addLayoutComponent(component, name);
	}

	public void removeLayoutComponent(Component component) {
		if (northComponent == component) {
			northComponent = null;
		} else if (southComponent == component) {
			southComponent = null;
		} else if (eastComponent == component) {
			eastComponent = null;
		} else if (westComponent == component) {
			westComponent = null;
		} else if (centerComponent == component) {
			centerComponent = null;
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(0, 0);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(0, 0);
	}

	public void addLayoutComponent(Component comp, Object constraints) {
		if (constraints.equals(NORTH)) {
			northComponent = comp;
		} else if (constraints.equals(SOUTH)) {
			southComponent = comp;
		} else if (constraints.equals(EAST)) {
			eastComponent = comp;
		} else if (constraints.equals(WEST)) {
			westComponent = comp;
		} else if (constraints.equals(CENTER)) {
			centerComponent = comp;
		} else {
			throw new IllegalArgumentException(
					"Constraint must equal one of the direction constants in SquareSandwich Layout");
		}
	}

	public float getLayoutAlignmentX(Container target) {
		return 0.5F;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0.5F;
	}

	public void invalidateLayout(Container target) {

	}

	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(10000, 10000);
	}

	private void validateType(int type) {
		if (type != NONE_FIXED && type != NORTH_SOUTH_FIXED
				&& type != EAST_WEST_FIXED && type != WEST_FIXED) {
			throw new IllegalArgumentException(
					"Invalid type. Must be a constant defined in SquareSandwichLayout. type "
							+ type);
		}

	}

	private void validateAllComponents() {
		if (northComponent == null || southComponent == null
				|| eastComponent == null || westComponent == null
				|| centerComponent == null) {
			throw new IllegalArgumentException(
					"This layout manager requres a NORTH,SOUTH,EAST,WEST and CENTER copmonent.");
		}
	}

	private void adjustComponents(int[] northBounds, int[] westBounds,
			int[] eastBounds, int[] southBounds, int[] centerBounds) {
		northComponent.setBounds(northBounds[0], northBounds[1],
				northBounds[2], northBounds[3]);
		westComponent.setBounds(westBounds[0], westBounds[1], westBounds[2],
				westBounds[3]);
		eastComponent.setBounds(eastBounds[0], eastBounds[1], eastBounds[2],
				eastBounds[3]);
		southComponent.setBounds(southBounds[0], southBounds[1],
				southBounds[2], southBounds[3]);
		centerComponent.setBounds(centerBounds[0], centerBounds[1],
				centerBounds[2], centerBounds[3]);
	}

	public void layoutContainer(Container container) {
		validateAllComponents();

		int width = container.getSize().width;
		int height = container.getSize().height;

		if (type == NORTH_SOUTH_FIXED) {
			int eightyPercentOfHeight = (int) (height * .85F);
			int squareSide = Math.min(width, eightyPercentOfHeight);
			int northSouthHeight = (height - squareSide) / 2;
			int eastWestWidth = (width - squareSide) / 2;

			adjustComponents(new int[] { eastWestWidth, 0, squareSide,
					northSouthHeight },
					new int[] { 0, 0, eastWestWidth, height }, new int[] {
							width - eastWestWidth, 0, eastWestWidth, height },
					new int[] { eastWestWidth, height - northSouthHeight,
							squareSide, northSouthHeight }, new int[] {
							eastWestWidth, northSouthHeight, squareSide,
							squareSide });
		} else if (type == EAST_WEST_FIXED) {
			int eightyPercentOfWidth = (int) (width * .8F);
			int squareSide = Math.min(height, eightyPercentOfWidth);
			int northSouthHeight = (height - squareSide) / 2;
			int eastWestWidth = (width - squareSide) / 2;

			adjustComponents(
					new int[] { 0, 0, width, northSouthHeight },
					new int[] { 0, northSouthHeight, eastWestWidth, squareSide },
					new int[] { width - eastWestWidth, northSouthHeight,
							eastWestWidth, squareSide },
					new int[] { 0, height - northSouthHeight, width,
							northSouthHeight }, new int[] { eastWestWidth,
							northSouthHeight, squareSide, squareSide });
		} else if (type == WEST_FIXED) {

			int eightyPercentOfWidth = (int) (width * .8F);
			int squareSide = Math.min(height, eightyPercentOfWidth);
			int northSouthHeight = (height - squareSide) / 2;

			int leftOverWidth = (width - squareSide);

			int eastWidth = 0;
			int westWidth = leftOverWidth;

			if (leftOverWidth > (int) squareSide * .25F) {
				eastWidth = (int) ((leftOverWidth - (squareSide * .25F)) / 2.0);
				westWidth = leftOverWidth - eastWidth;
			}

			adjustComponents(new int[] { 0, 0, width, northSouthHeight },
					new int[] { 0, northSouthHeight, eastWidth, squareSide },
					new int[] { width - westWidth, northSouthHeight, westWidth,
							squareSide },
					new int[] { 0, height - northSouthHeight, width,
							northSouthHeight }, new int[] { eastWidth,
							northSouthHeight, squareSide, squareSide });
		} else
		// type == NONE_FIXED
		{
			int squareSide = Math.min(height, width);
			int northSouthHeight = (height - squareSide) / 2;
			int eastWestWidth = (width - squareSide) / 2;

			adjustComponents(
					new int[] { 0, 0, width, northSouthHeight },
					new int[] { 0, northSouthHeight, eastWestWidth, squareSide },
					new int[] { width - eastWestWidth, northSouthHeight,
							eastWestWidth, squareSide },
					new int[] { 0, height - northSouthHeight, width,
							northSouthHeight }, new int[] { eastWestWidth,
							northSouthHeight, squareSide, squareSide });
		}
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}