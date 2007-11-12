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

import decaf.com.inboundevent.game.MoveEvent;
import decaf.event.Subscriber;
import decaf.gui.ChessAreaControllerBase;
import decaf.gui.Disposable;
import decaf.moveengine.Position;

// Referenced classes of package caffeine.gui:
// SoundManager, ControllerBase, ChessArea

public class Style12Subscriber implements Subscriber, Disposable {
	private ChessAreaControllerBase controller;

	private boolean isPartnersBoard;

	public Style12Subscriber(ChessAreaControllerBase controller,
			boolean isPartnersBoard) {
		this.controller = controller;
		this.isPartnersBoard = isPartnersBoard;
	}

	public Style12Subscriber(ChessAreaControllerBase controller) {
		this(controller, false);
	}

	public void dispose() {
		controller = null;
	}

	public void inform(MoveEvent style12event) {
		String move = style12event.getPrettyNotation();
		Position position = style12event.getPosition();
		int timeTaken = style12event.getTimeTakenForLastMove();
		boolean isWhitesMove = !style12event.isWhitesMove();
		int whiteTime = style12event.getWhiteRemainingTime();
		int blackTime = style12event.getBlackRemainingTime();
		int[] whiteDropPieces = style12event.getHoldingsChangedEvent() == null ? null
				: style12event.getHoldingsChangedEvent().getWhiteHoldings();
		int[] blackDropPieces = style12event.getHoldingsChangedEvent() == null ? null
				: style12event.getHoldingsChangedEvent().getBlackHoldings();
		int lagLastMove = style12event.getlagInMillis();
		boolean isClockTicking = style12event.isClockTicking();

		if (move == null) {
			// code to handle a refresh correctly.
			isWhitesMove = !isWhitesMove;
		}

		if (!isPartnersBoard) {
			controller.handleUserPositionUpdate(move, position, timeTaken,
					whiteTime, blackTime, whiteDropPieces, blackDropPieces,
					lagLastMove, isClockTicking);
		} else {
			controller.handlePartnerPositionUpdate(move, position, timeTaken,
					whiteTime, blackTime, whiteDropPieces, blackDropPieces,
					lagLastMove, isClockTicking);
		}
	}
}