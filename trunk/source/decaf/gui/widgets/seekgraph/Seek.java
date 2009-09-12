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
package decaf.gui.widgets.seekgraph;

public class Seek {

	public enum GameColor {
		white, black, na
	}

	public enum GameType {
		standard, blitz, lightning, wild, crazyhouse, suicide
	}

	private static final int USUAL_GAME = 40;

	int ad;

	int rating;

	String name;

	int mins;

	int incr;

	boolean rated;

	String type;

	GameColor color;

	public Seek(int ad, int rating, String name, int mins, int incr,
			boolean rated) {
		super();
		this.ad = ad;
		this.rating = rating;
		this.name = name;
		this.mins = mins;
		this.incr = incr;
		this.rated = rated;
		this.color = GameColor.na;
		this.type = GameType.blitz.name();
	}

	public int getAdNumber() {
		return ad;
	}

	public GameColor getColor() {
		return color;
	}

	public int getIncr() {
		return incr;
	}

	public int getMins() {
		return mins;
	}

	public String getName() {
		return name;
	}

	public int getRating() {
		return rating;
	}

	public String getType() {
		return type;
	}

	public int getX() {
		return getMins() * 60 + getIncr() * USUAL_GAME;
	}

	public int getY() {
		return getRating();
	}

	public boolean isComputer() {
		return getName().endsWith("(C)");
	}

	public boolean isInterestingType() {
		return type.startsWith(GameType.wild.name())
				|| type.startsWith(GameType.crazyhouse.name())
				|| type.startsWith(GameType.suicide.name());
	}

	public boolean isRated() {
		return rated;
	}

	public void setAdNumber(int number) {
		ad = number;
	}

	public void setIncr(int incr) {
		this.incr = incr;
	}

	public void setMins(int mins) {
		this.mins = mins;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRated(boolean rated) {
		this.rated = rated;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return ad + " " + rating + " " + name + " " + mins + " " + incr;
	}
}