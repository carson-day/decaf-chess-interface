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

import decaf.com.inboundevent.game.GameEndEvent;
import decaf.com.inboundevent.game.RemovingObservedGameEvent;
import decaf.event.Subscriber;
import decaf.gui.ChessAreaControllerBase;
import decaf.gui.Disposable;
import decaf.gui.util.User;

// Referenced classes of package caffeine.gui:
// SoundManager, ControllerBase, ChessArea

public class GameEndSubscriber implements Subscriber, Disposable {
	private ChessAreaControllerBase controller;

	private static User user = User.getInstance();

	public GameEndSubscriber(ChessAreaControllerBase controller) {
		this.controller = controller;
	}

	public void dispose() {
		controller = null;
	}

	public void inform(RemovingObservedGameEvent event) {
		if (controller.isActive()) {
			controller.setActive(false);
		}
	}

	public void inform(GameEndEvent event) {
		if (!controller.isBughouse()) {
			handleGameEnd(controller.isUserWhite(), event.getScore(), event
					.getDescription());
		} else {
			boolean gameRepresentsPlayersBoard = event.getWhiteName()
					.equalsIgnoreCase(user.getHandle())
					|| event.getBlackName().equalsIgnoreCase(user.getHandle());

			if (gameRepresentsPlayersBoard) {
				handleGameEnd(controller.isUserWhite(), event.getScore(), event
						.getDescription());
			} else {
				handleGameEnd(controller.isPartnerWhite(), event.getScore(),
						event.getDescription());
			}
		}
	}

	private void handleGameEnd(boolean userIsWhite, int score,
			String description) {
		if ((userIsWhite && score == GameEndEvent.WHITE_WON)
				|| (!userIsWhite && score == GameEndEvent.BLACK_WON)) {
			controller.handleUserWon(description);
		} else if ((!userIsWhite && score == GameEndEvent.WHITE_WON)
				|| (userIsWhite && score == GameEndEvent.BLACK_WON)) {
			controller.handleUserLost(description);
		} else if (score == GameEndEvent.DRAW) {
			controller.handleUserDrew(description);
		} else {
			controller.handleGameStopped(description);
		}
	}
}