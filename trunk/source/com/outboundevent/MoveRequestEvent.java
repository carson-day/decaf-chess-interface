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

package decaf.com.outboundevent;

public class MoveRequestEvent extends GameOutboundEvent {
	private String move;

	public MoveRequestEvent(int gameId, String move, boolean isHiding) {
		super(gameId, isHiding);
		this.move = move;
	}

	public MoveRequestEvent(int gameId, String move, Class hideResponseEventType) {
		super(gameId, hideResponseEventType);
		this.move = move;
	}

	public String getMove() {
		return move;
	}
}