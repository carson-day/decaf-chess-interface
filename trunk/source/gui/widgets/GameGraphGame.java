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

public class GameGraphGame {
	private int gameId;

	private String type;

	private int time;

	private int rating;

	private String description;

	private boolean isRated;

	public GameGraphGame(int gameId, String gameType, int time, int rating,
			boolean isRated, String description) {
		this.gameId = gameId;
		this.type = gameType;
		this.time = time;
		this.rating = rating;
		this.isRated = isRated;
		this.description = description;
	}

	public boolean isRated() {
		return isRated;
	}

	public String getDescription() {
		return description;
	}

	public int getGameId() {
		return gameId;
	}

	public String getType() {
		return type;
	}

	public int getTime() {
		return time;
	}

	public int getRating() {
		return rating;
	}
}