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
package decaf.gui.widgets.bugseek;

public class BugWhoGGame {
	private int game1Id;

	private int game2Id;

	private String game1Description;

	private String game2Description;

	public String getGame1Description() {
		return game1Description;
	}

	public void setGame1Description(String game1Description) {
		this.game1Description = game1Description;
	}

	public int getGame1Id() {
		return game1Id;
	}

	public void setGame1Id(int game1Id) {
		this.game1Id = game1Id;
	}

	public String getGame2Description() {
		return game2Description;
	}

	public void setGame2Description(String game2Description) {
		this.game2Description = game2Description;
	}

	public int getGame2Id() {
		return game2Id;
	}

	public void setGame2Id(int game2Id) {
		this.game2Id = game2Id;
	}

}
