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

import java.util.List;

import decaf.com.inboundevent.InboundEvent;

public class AvailableBugTeamsEvent extends InboundEvent {

	public AvailableBugTeamsEvent(Object source, String messageId, String text,
			List list) {
		super(source, messageId, text);
		availableTeams = list;
	}

	public int getNumberOfAvailableTeams() {
		return availableTeams.size();
	}

	public AvailableBugTeam getAvailableBugTeam(int i) {
		return (AvailableBugTeam) availableTeams.get(i);
	}

	public String toString() {
		String result = "<AvailableBugTeamsEvent>" + super.toString();

		for (int i = 0; i < getNumberOfAvailableTeams(); i++)
			result += getAvailableBugTeam(i).toString();

		return result + "</AvailableBugTeamsEvent>";
	}

	private List availableTeams;
}