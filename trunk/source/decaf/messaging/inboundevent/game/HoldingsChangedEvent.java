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

import decaf.moveengine.Piece;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class HoldingsChangedEvent extends GameEvent implements Piece {
	private int darkHoldings[];

	private int lightHoldings[];

	public HoldingsChangedEvent(int icsId, int gameId, int blackHoldings[],
			int whiteHoldings[]) {
		super(icsId, gameId);
		darkHoldings = blackHoldings;
		lightHoldings = whiteHoldings;
	}

	public int[] getBlackHoldings() {
		return darkHoldings;
	}

	public int[] getWhiteHoldings() {
		return lightHoldings;
	}

	@Override
	public String toString() {
		return "HoldingsChangedEvent: icsId=" + getIcsId() + " gameId="
				+ getGameId();
	}
}