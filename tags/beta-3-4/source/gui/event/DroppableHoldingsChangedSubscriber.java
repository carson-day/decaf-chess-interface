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

package decaf.gui.event;

import decaf.com.inboundevent.game.DroppableHoldingsChangedEvent;
import decaf.event.Subscriber;
import decaf.gui.ChessAreaControllerBase;
import decaf.gui.Disposable;

// Referenced classes of package caffeine.gui:
// ChessArea

public class DroppableHoldingsChangedSubscriber implements Subscriber,
		Disposable {

	private ChessAreaControllerBase controller;

	private boolean isPartnersBoard;

	public DroppableHoldingsChangedSubscriber(
			ChessAreaControllerBase controller, boolean isPartnersBoard) {
		this.controller = controller;
		this.isPartnersBoard = isPartnersBoard;
	}

	public DroppableHoldingsChangedSubscriber(ChessAreaControllerBase controller) {
		this.controller = controller;
		isPartnersBoard = false;
	}

	public void dispose() {
		controller = null;
	}

	public void inform(DroppableHoldingsChangedEvent event) {
		if (isPartnersBoard) {
			controller.updatePartnerDropPieces(event.getWhiteHoldings(), event
					.getBlackHoldings());
		} else {
			controller.updateUserDropPieces(event.getWhiteHoldings(), event
					.getBlackHoldings());

		}
	}
}