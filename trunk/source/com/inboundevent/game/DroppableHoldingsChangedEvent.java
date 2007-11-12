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
package decaf.com.inboundevent.game;

import decaf.gui.util.StringUtility;
import decaf.moveengine.Piece;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class DroppableHoldingsChangedEvent extends GameEvent implements Piece {
	public String toString() {
		return "<DroppableHoldingsChangedEvent><whiteHoldings>"
				+ StringUtility.intArrayToString(lightHoldings)
				+ "</whiteHoldings>" + "<blackHoldings>"
				+ StringUtility.intArrayToString(darkHoldings)
				+ "</blackHoldings>" + "</DroppableHoldingsChangedEvent>";
	}

	public DroppableHoldingsChangedEvent(Object source, String messageId,
			String text, int gameId, int blackHoldings[], int whiteHoldings[]) {
		super(source, messageId, text, gameId);
		darkHoldings = blackHoldings;
		lightHoldings = whiteHoldings;
	}

	public int[] getWhiteHoldings() {
		return lightHoldings;
	}

	public int[] getBlackHoldings() {
		return darkHoldings;
	}

	private int darkHoldings[];

	private int lightHoldings[];
}