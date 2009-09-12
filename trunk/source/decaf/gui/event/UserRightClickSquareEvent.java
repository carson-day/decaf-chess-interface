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
package decaf.gui.event;

import decaf.event.Event;
import decaf.gui.widgets.ChessBoardSquare;

public class UserRightClickSquareEvent implements Event {
	private ChessBoardSquare source;

	private boolean isEmpty;

	private String boardId;

	public UserRightClickSquareEvent(ChessBoardSquare source, boolean isEmpty,
			String boardId) {
		super();
		this.source = source;
		this.isEmpty = isEmpty;
		this.boardId = boardId;
	}

	public String getBoardId() {
		return boardId;
	}

	public ChessBoardSquare getSource() {
		return source;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public void setSource(ChessBoardSquare source) {
		this.source = source;
	}

}
