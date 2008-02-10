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
package decaf.gui.widgets.movelist;

import java.util.LinkedList;
import java.util.List;

import decaf.moveengine.Position;

public class MoveListModel {
	private List<MoveListModelMove> moves = new LinkedList<MoveListModelMove>();

	private boolean isInProgress = true;

	private String resultString;

	private Position startingPosition;

	public MoveListModel(Position startingPosition) {
		this.startingPosition = startingPosition;
	}

	public Position getStartingPosition() {
		return startingPosition;
	}

	public int getSize() {
		return moves.size();
	}

	public MoveListModelMove getMove(int index) {
		return moves.get(index);
	}

	public String getResultString(String result) {
		if (isInProgress) {
			return "In Progress";
		} else {
			return resultString;
		}
	}

	public boolean isInProgress() {
		return isInProgress;
	}

	public void end(String resultString) {
		this.isInProgress = false;
		this.resultString = resultString;
	}

	public void append(MoveListModelMove moveListModelMove) {
		if (isInProgress) {
			moves.add(moveListModelMove);
		} else {
			throw new IllegalStateException("Game is not in progress");
		}
	}

	public long getWhiteElapsedTime(int halfMoveIndex, int inc) {
		long result = 0;
		for (int i = 0; i <= halfMoveIndex; i += 2) {
			if (moves.size() > halfMoveIndex) {
				result += moves.get(i).getTimeTakenMillis() - inc * 1000;
			}
		}
		return result;
	}

	public long getBlackElapsedTime(int halfMoveIndex, int inc) {
		long result = 0;
		for (int i = 1; i <= halfMoveIndex; i += 2) {
			if (moves.size() > halfMoveIndex) {
				result += moves.get(i).getTimeTakenMillis() - inc * 1000;
			}
		}
		return result;
	}

}
