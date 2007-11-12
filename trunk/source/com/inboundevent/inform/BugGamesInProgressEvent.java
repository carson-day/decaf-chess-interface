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

public class BugGamesInProgressEvent extends InboundEvent {

	public BugGamesInProgressEvent(Object source, String messageId,
			String text, List list) {
		super(source, messageId, text);
		gamesInProgress = list;
	}

	public int getNumberOfGamesInProgress() {
		return gamesInProgress.size();
	}

	public BugGameInProgress getBugGameInProgressAt(int i) {
		return (BugGameInProgress) gamesInProgress.get(i);
	}

	public String toString() {
		String result = "<BugGamesInProgressEvent>" + super.toString();

		for (int i = 0; i < getNumberOfGamesInProgress(); i++)
			result = result + "\n" + getBugGameInProgressAt(i);

		return result + "</BugGamesInProgressEvent>";
	}

	private List gamesInProgress;
}