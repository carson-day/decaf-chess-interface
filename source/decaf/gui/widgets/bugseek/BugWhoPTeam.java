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

public class BugWhoPTeam {

	private String player1Rating;

	private String player2Rating;

	private String player1Handle;

	private String player2Handle;

	private char player1Modifier;

	private char player2Modifier;

	public String getPlayer1Handle() {
		return player1Handle;
	}

	public char getPlayer1Modifier() {
		return player1Modifier;
	}

	public String getPlayer1Rating() {
		return player1Rating;
	}

	public String getPlayer2Handle() {
		return player2Handle;
	}

	public char getPlayer2Modifier() {
		return player2Modifier;
	}

	public String getPlayer2Rating() {
		return player2Rating;
	}

	public void setPlayer1Handle(String player1Handle) {
		this.player1Handle = player1Handle;
	}

	public void setPlayer1Modifier(char player1Modifier) {
		this.player1Modifier = player1Modifier;
	}

	public void setPlayer1Rating(String player1Rating) {
		this.player1Rating = player1Rating;
	}

	public void setPlayer2Handle(String player2Handle) {
		this.player2Handle = player2Handle;
	}

	public void setPlayer2Modifier(char player2Modifier) {
		this.player2Modifier = player2Modifier;
	}

	public void setPlayer2Rating(String player2Rating) {
		this.player2Rating = player2Rating;
	}

}
