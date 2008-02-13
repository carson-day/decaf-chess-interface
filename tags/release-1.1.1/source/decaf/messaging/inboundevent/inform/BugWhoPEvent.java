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
package decaf.messaging.inboundevent.inform;

import java.util.List;

import decaf.gui.widgets.bugseek.BugWhoPTeam;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;

public class BugWhoPEvent extends IcsNonGameEvent {

	private List<BugWhoPTeam> teams;

	public BugWhoPEvent(int icsId, String text, List<BugWhoPTeam> teams) {
		super(icsId, text);
		this.teams = teams;
	}

	public List<BugWhoPTeam> getTeams() {
		return teams;
	}

	public void setTeams(List<BugWhoPTeam> teams) {
		this.teams = teams;
	}
}
