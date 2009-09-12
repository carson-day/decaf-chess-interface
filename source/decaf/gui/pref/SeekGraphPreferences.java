/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Sergei Kozyrenko (kozyr82@gmail.com)
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

package decaf.gui.pref;

import java.awt.Color;
import java.io.Serializable;

public class SeekGraphPreferences implements Cloneable, Serializable {

	public static SeekGraphPreferences getDefault() {
		SeekGraphPreferences result = new SeekGraphPreferences();
		return result;
	}

	private boolean showUnrated;

	private boolean showComputer;
	private int[][] hscale = { { 1300, 1 }, { 1500, 2 }, { 1700, 2 },
			{ 1900, 2 }, { 2100, 1 }, { 2500, 1 } };

	private int hstart = 1000;
	private int[][] vscale;

	private int vstart;
	private Color computerColor;
	private Color ratedColor;
	private Color unratedColor;

	private Color manyColor;

	public SeekGraphPreferences() {

		hscale = new int[][] { { 1300, 1 }, { 1500, 2 }, { 1700, 2 },
				{ 1900, 2 }, { 2100, 1 }, { 2500, 1 } };
		hstart = 1000;

		vscale = new int[][] { { 1, 1 }, { 3, 2 }, { 5, 2 }, { 10, 1 },
				{ 15, 1 }, { 20, 1 } };
		vstart = 0;

		showUnrated = true;
		showComputer = true;

		computerColor = Color.gray;
		unratedColor = Color.cyan;
		ratedColor = Color.red;
		manyColor = Color.green;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Color getComputerColor() {
		return computerColor;
	}

	public int[][] getHscale() {
		return hscale;
	}

	public int getHstart() {
		return hstart;
	}

	public Color getManyColor() {
		return manyColor;
	}

	public Color getRatedColor() {
		return ratedColor;
	}

	public Color getUnratedColor() {
		return unratedColor;
	}

	public int[][] getVscale() {
		return vscale;
	}

	public int getVstart() {
		return vstart;
	}

	public boolean isShowComputer() {
		return showComputer;
	}

	public boolean isShowUnrated() {
		return showUnrated;
	}

	public void setComputerColor(Color computerColor) {
		this.computerColor = computerColor;
	}

	public void setHscale(int[][] hscale) {
		this.hscale = hscale;
	}

	public void setHstart(int hstart) {
		this.hstart = hstart;
	}

	public void setManyColor(Color manyColor) {
		this.manyColor = manyColor;
	}

	public void setRatedColor(Color ratedColor) {
		this.ratedColor = ratedColor;
	}

	public void setShowComputer(boolean showComputer) {
		this.showComputer = showComputer;
	}

	public void setShowUnrated(boolean showUnrated) {
		this.showUnrated = showUnrated;
	}

	public void setUnratedColor(Color unratedColor) {
		this.unratedColor = unratedColor;
	}

	public void setVscale(int[][] vscale) {
		this.vscale = vscale;
	}

	public void setVstart(int vstart) {
		this.vstart = vstart;
	}
}
