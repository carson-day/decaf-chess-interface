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

package decaf.com.inboundevent.inform;

public class AvailableBugTeam {

	/**
	 * @param player1Status
	 *            a constant defined in PlayerAvailability.
	 * @param player2Status
	 *            a constant defined in PlayerAvailability.*
	 */
	public AvailableBugTeam(String player1Name, String player1Rating,
			int player1Status, String player2Name, String player2Rating,
			int player2Status) {
		this.player1Name = player1Name;
		this.player1Rating = player1Rating;
		this.player1Status = player1Status;
		this.player2Name = player2Name;
		this.player2Rating = player2Rating;
		this.player2Status = player2Status;
		isAvailable = (player1Status == PlayerAvailability.AVAILABLE || player1Status == PlayerAvailability.IDLE)
				&& (player2Status == PlayerAvailability.AVAILABLE || player2Status == PlayerAvailability.IDLE);
	}

	public boolean equals(Object obj) {
		try {
			AvailableBugTeam availablebugteam = (AvailableBugTeam) obj;
			return player1Name.equals(availablebugteam.getPlayer1Name())
					&& player1Rating
							.equals(availablebugteam.getPlayer1Rating())
					&& player1Status == availablebugteam.getPlayer1Status()
					&& player2Name.equals(availablebugteam.getPlayer2Name())
					&& player2Rating
							.equals(availablebugteam.getPlayer2Rating())
					&& player2Status == availablebugteam.getPlayer2Status();
		} catch (ClassCastException classcastexception) {
			return false;
		}
	}

	public String getPlayer1Name() {
		return player1Name;
	}

	public String getPlayer1Rating() {
		return player1Rating;
	}

	/**
	 * Returns a constant defined in PlayerAvailability.
	 */
	public int getPlayer1Status() {
		return player1Status;
	}

	public String getPlayer2Name() {
		return player2Name;
	}

	public String getPlayer2Rating() {
		return player2Rating;
	}

	/**
	 * Returns a constant defined in PlayerAvailability.
	 */
	public int getPlayer2Status() {
		return player2Status;
	}

	/**
	 * Returns true if this team is available to be matched.
	 */
	public boolean isAvailable() {
		return isAvailable;
	}

	public String toString() {
		return "<AvaiableBugTeam>" + "<player1Name>" + player1Name
				+ "</player1Name>" + "<player1Rating>" + player1Rating
				+ "</player1Rating>" + "<player2Name>" + player1Status
				+ "</player2Name>" + "<player2Rating>" + player2Rating
				+ "</player2Rating>" + "<player2Status>" + player2Status
				+ "</player2Status>" + "<isAvailable>" + isAvailable
				+ "</isAvailable>" + "</AvaiableBugTeam>";
	}

	private String player1Name;

	private String player1Rating;

	private String player2Name;

	private String player2Rating;

	private int player1Status;

	private int player2Status;

	private boolean isAvailable;
}