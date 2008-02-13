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
package decaf.messaging.inboundevent.game;

import java.util.HashMap;
import java.util.Map;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent, MoveRequestEvent

public class GameStartEvent extends GameEvent implements GameTypes {

	/**
	 * @param moveEvent
	 *            should never be null. If a move has not been made it should
	 *            just reflect the current position on the chess board.
	 */
	public GameStartEvent(int icsId, int gameId, Map<String, String> g1Params) {
		super(icsId, gameId);
		this.g1Params = g1Params;
	}

	public MoveEvent getFirstEvent() {
		return firstEvent;
	}

	public void setFirstEvent(MoveEvent firstEvent) {
		this.firstEvent = firstEvent;
	}

	public String getWhiteName() {
		return firstEvent.getWhiteName();
	}

	public String getBlackName() {
		return firstEvent.getBlackName();
	}

	public String getWhiteRating() {
		return g1Params.get("rt").split(",")[0];
	}

	public String getBlackRating() {
		return g1Params.get("rt").split(",")[1];
	}

	public String getGameDescription() {
		return isExamining ? "Examining" : g1Params.get("t");
	}

	public boolean isRated() {
		return g1Params.get("r").equals("1");
	}

	/**
	 * Returns -1 if there is none, otherwise returns the number of the partners
	 * game.
	 */
	public int partnersGameId() {
		int result = Integer.parseInt(g1Params.get("pt"));
		return result == 0 ? -1 : result;
	}

	public MoveEvent getInitialInboundChessMoveEvent() {
		return firstEvent;
	}

	public String getG1Param(String paramName) {
		return g1Params.get(paramName);
	}

	public void setG1Params(HashMap<String, String> g1params) {
		this.g1Params = g1params;
	}

	public boolean isBughouse() {
		return isExamining ? false : getGameDescription().equalsIgnoreCase(
				"bughouse");
	}

	public boolean isCrazyhouse() {
		return isExamining ? false : getGameDescription().equalsIgnoreCase(
				"crazyhouse");
	}

	private boolean isExamining;

	private MoveEvent firstEvent;

	private Map<String, String> g1Params;

	public boolean isExamining() {
		return isExamining;
	}

	public void setExamining(boolean isExamining) {
		this.isExamining = isExamining;
	}
}