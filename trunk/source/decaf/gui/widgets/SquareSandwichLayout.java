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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

import org.apache.log4j.Logger;

/**
 * A layout which always makes the Center component a perfect square. This
 * layout manager requres a NORTH,SOUTH,EAST,WEST and CENTER copmonent. They all
 * must be added. Optionally you can set a percentage on east/west components
 * for the percentage of width they must occupy. You may do the same for height
 * of north/south components. The left over space is distributed to make the
 * square centered.
 */
public class SquareSandwichLayout implements LayoutManager2 {

	private static final Logger LOGGER = Logger
			.getLogger(SquareSandwichLayout.class);

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

	private double westWeight;

	private double eastWeight;

	private double northWeight;

	private double southWeight;

	private int maxWestWidth;
	private int maxEastWidth;
	private int maxSouthWidth;
	private int maxNorthWidth;

	private int centerSpacing;

	public SquareSandwichLayout() {
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

	public void addLayoutComponent(String name, Component component) {
		addLayoutComponent(component, name);
	}

	public double getEastWeight() {
		return eastWeight;
	}

	public float getLayoutAlignmentX(Container target) {
		return 0.5F;
	}

	public float getLayoutAlignmentY(Container target) {
		return 0.5F;
	}

	public int getMaxEastWidth() {
		return maxEastWidth;
	}

	public int getMaxWestWidth() {
		return maxWestWidth;
	}

	public double getNorthWeight() {
		return northWeight;
	}

	public double getSouthWeight() {
		return southWeight;
	}

	public double getWestWeight() {
		return westWeight;
	}

	public void invalidateLayout(Container target) {

	}

	public void layoutContainer(Container container) {
		validateAllComponents();

		int width = container.getSize().width;
		int height = container.getSize().height;

		int eastWidth = eastWeight == 0.0 || !eastComponent.isVisible() ? 0
				: (int) (width * eastWeight);
		int westWidth = westWeight == 0.0 || !westComponent.isVisible() ? 0
				: (int) (width * westWeight);
		int northHeight = northWeight == 0.0 || !northComponent.isVisible() ? 0
				: (int) (height * northWeight);
		int southHeight = southWeight == 0.0 || !southComponent.isVisible() ? 0
				: (int) (height * southWeight);

		int leftOverWidth = width - (eastWidth + westWidth);
		int leftOverHeight = height - (northHeight + southHeight);

		int squareSide = Math.min(leftOverWidth, leftOverHeight);

		int widthMinusSquareDivide2 = (width - squareSide) / 2;
		int heightMinusSquareDivide2 = (height - squareSide) / 2;

		if (widthMinusSquareDivide2 >= eastWidth
				&& widthMinusSquareDivide2 >= westWidth) {
			// If there is enough left over width that both sides desire, evenly
			// distrubute it.
			eastWidth = widthMinusSquareDivide2;
			westWidth = widthMinusSquareDivide2;
		} else {
			int x = (width - (eastWidth + westWidth + squareSide)) / 2;
			if (x >= 0) {
				eastWidth = eastWidth + x;
				westWidth = westWidth + x;
			}
		}
		if (heightMinusSquareDivide2 >= northHeight
				&& heightMinusSquareDivide2 >= southHeight) {
			// If there is enough left over height that both sides desire,
			// evenly distrubute it.
			northHeight = heightMinusSquareDivide2;
			southHeight = heightMinusSquareDivide2;
		} else {
			int y = (height - (northHeight + southHeight + squareSide)) / 2;
			if (y >= 0) {
				northHeight = northHeight + y;
				southHeight = southHeight + y;
			}
		}

		if (maxNorthWidth != 0 && width > maxNorthWidth) {
			int center = width / 2;
			northComponent.setBounds(center - maxNorthWidth / 2, 0,
					maxNorthWidth, northHeight);
		} else {
			northComponent.setBounds(0, 0, width, northHeight);
		}

		if (maxWestWidth != 0 && westWidth > maxWestWidth) {
			westComponent.setBounds(westWidth - maxWestWidth, northHeight,
					maxWestWidth, squareSide);
		} else {
			westComponent.setBounds(0, northHeight, westWidth, squareSide);
		}

		centerComponent.setBounds(westWidth + centerSpacing, northHeight
				+ centerSpacing, squareSide - 2 * centerSpacing, squareSide - 2
				* centerSpacing);

		if (maxEastWidth != 0 && eastWidth > maxEastWidth) {
			eastComponent.setBounds(westWidth + squareSide, northHeight,
					maxEastWidth, squareSide);
		} else {
			eastComponent.setBounds(westWidth + squareSide, northHeight,
					eastWidth, squareSide);
		}

		if (maxSouthWidth != 0 && width > maxSouthWidth) {
			int center = width / 2;
			southComponent.setBounds(center - maxSouthWidth / 2, northHeight
					+ squareSide, maxSouthWidth, southHeight);
		} else {
			southComponent.setBounds(0, northHeight + squareSide, width,
					southHeight);
		}
	}

	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(10000, 10000);
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(0, 0);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(0, 0);
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

	public void setEastWeight(double eastWeight) {
		this.eastWeight = eastWeight;
	}

	public void setMaxEastWidth(int maxEastWidth) {
		this.maxEastWidth = maxEastWidth;
	}

	public void setMaxNorthWidth(int maxNorthWidth) {
		this.maxNorthWidth = maxNorthWidth;
	}

	public void setMaxSouthWidth(int maxSouthWidth) {
		this.maxSouthWidth = maxSouthWidth;
	}

	public void setMaxWestWidth(int maxWestWidth) {
		this.maxWestWidth = maxWestWidth;
	}

	public void setNorthWeight(double northWeight) {
		this.northWeight = northWeight;
	}

	public void setSouthWeight(double southWeight) {
		this.southWeight = southWeight;
	}

	public void setWestWeight(double westWeight) {
		this.westWeight = westWeight;
	}

	private void validateAllComponents() {
		if (northComponent == null || southComponent == null
				|| eastComponent == null || westComponent == null
				|| centerComponent == null) {
			throw new IllegalArgumentException(
					"This layout manager requres a NORTH,SOUTH,EAST,WEST and CENTER copmonent.");
		}
	}

}