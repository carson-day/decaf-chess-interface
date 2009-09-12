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

import java.awt.BorderLayout;
import java.awt.Container;

import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.gui.ChessAreaControllerBase;
import decaf.gui.GUIManager;
import decaf.gui.widgets.ChessAreaToolbar;
import decaf.gui.widgets.Disposable;
import decaf.messaging.inboundevent.game.MoveEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.moveengine.Position;

public class Style12Subscriber implements Subscriber, Disposable {
	private ChessAreaControllerBase controller;

	private boolean isPartnersBoard;

	public Style12Subscriber(ChessAreaControllerBase controller) {
		this(controller, false);
	}

	public Style12Subscriber(ChessAreaControllerBase controller,
			boolean isPartnersBoard) {
		this.controller = controller;
		this.isPartnersBoard = isPartnersBoard;
	}

	public void dispose() {
		controller = null;
	}

	public void inform(MoveEvent style12Event) {
		final String move = style12Event.getPrettyNotation();
		final Position position = style12Event.getPosition();
		final long timeTaken = style12Event.getTimeTakenForLastMove();
		final long whiteTime = style12Event.getWhiteRemainingTime();
		final long blackTime = style12Event.getBlackRemainingTime();
		final int[] whiteDropPieces = style12Event.getHoldingsChangedEvent() == null ? null
				: style12Event.getHoldingsChangedEvent().getWhiteHoldings();
		final int[] blackDropPieces = style12Event.getHoldingsChangedEvent() == null ? null
				: style12Event.getHoldingsChangedEvent().getBlackHoldings();
		final int lagLastMove = style12Event.getlagInMillis();
		final boolean isClockTicking = style12Event.isClockTicking();

		// We were made an examiner of a game we were observing.
		if (controller.isObserving() && !controller.isExamining()
				&& style12Event.getRelation() == 2) {
			controller.setPlaying(true);
			controller.setExamining(true);
			controller.setObserving(false);
			controller.setValidating(false);
			controller
					.setDroppable(style12Event.getHoldingsChangedEvent() != null);

			controller.getChessArea().setup(
					"" + controller.getGameId(),
					style12Event.getWhiteName(),
					"",
					style12Event.getBlackName(),
					"",
					controller.isDroppable(),
					false,
					style12Event.getPosition(),
					controller.getPreferences().getBoardPreferences()
							.isShowingLag());

			controller.hideMoveList();
			controller.setInitialTimeSecs(0);
			controller.setInitialIncSecs(0);

			controller.getFrame().setTitle("Examining");

			if (controller.getPreferences().getBoardPreferences()
					.isShowingToolbar()) {

				ChessAreaToolbar toolbar = new ChessAreaToolbar(controller
						.getPreferences(), controller);
				GUIManager.getInstance().addKeyForwarder(toolbar);
				Container container = controller.getFrame().getContentPane();
				container.remove(controller.getCommandToolbar());
				container.add(toolbar, BorderLayout.NORTH);
				controller.setCommandToolbar(toolbar);
				container.invalidate();
			}

			controller.getChessArea().setWhiteTime(
					style12Event.getWhiteRemainingTime());
			controller.getChessArea().setBlackTime(
					style12Event.getBlackRemainingTime());

			if (!style12Event.isClockTicking()) {
				controller.getChessArea().startOrStopClocksWithoutTicking();
			}

			if (controller.isDroppable()) {
				controller.getChessArea().setWhiteDropPieces(
						style12Event.getHoldingsChangedEvent()
								.getWhiteHoldings());
				controller.getChessArea().setBlackDropPieces(
						style12Event.getHoldingsChangedEvent()
								.getBlackHoldings());
			}

			controller.handleGameStarted();
			controller.unsubscribe();
			controller.subscribe();
			controller.getChessArea().invalidate();
			if (controller.getCommandToolbar() != null) {
				controller.getCommandToolbar().invalidate();
			}
			controller.getFrame().validate();

			EventService.getInstance().publish(
					new OutboundEvent("moves " + controller.getGameId()));
			controller.clearPremove();
		}

		if (!isPartnersBoard) {
			controller.handleUserPositionUpdate(move, position, timeTaken,
					whiteTime, blackTime, whiteDropPieces, blackDropPieces,
					lagLastMove, isClockTicking, style12Event
							.getVerboseNotation());
		} else {
			controller.handlePartnerPositionUpdate(move, position, timeTaken,
					whiteTime, blackTime, whiteDropPieces, blackDropPieces,
					lagLastMove, isClockTicking, style12Event
							.getVerboseNotation());
		}
	}
}