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

package decaf.messaging.outboundevent;

import decaf.messaging.inboundevent.game.GameTypes;

public class MatchRequestEvent extends OutboundEvent implements GameTypes {
	public MatchRequestEvent(int time, int increment, int gameType,
			String playerToMatch, boolean isHidingFromUser) {
		super(isHidingFromUser);
		this.time = time;
		this.increment = increment;
		this.gameType = gameType;
		this.playerToMatch = playerToMatch;
	}

	public int getTime() {
		return time;
	}

	public int getIncrement() {
		return increment;
	}

	public int getGameType() {
		return gameType;
	}

	public String getPlayerToMatch() {
		return playerToMatch;
	}

	private int time;

	private int increment;

	private int gameType;

	private String playerToMatch;
}