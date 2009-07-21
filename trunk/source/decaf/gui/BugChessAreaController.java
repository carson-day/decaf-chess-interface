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
package decaf.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.gui.widgets.BugChessArea;
import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.ChessAreaToolbar;
import decaf.gui.widgets.Disposable;
import decaf.messaging.inboundevent.game.GameStartEvent;
import decaf.messaging.inboundevent.game.MoveEvent;
import decaf.messaging.inboundevent.inform.MoveListEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.moveengine.Position;

public class BugChessAreaController extends ChessAreaControllerBase implements
		Disposable {

	private static final Logger LOGGER = Logger
			.getLogger(BugChessAreaController.class);

	private static final String INVALID_GAME_TYPE = "BughouseChessAreaController does not handle "
			+ "anything besides bughouse. Try ChessAreaController instead.";

	public BugChessAreaController() {
		setFrame(new ChessAreaFrame());
		getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getFrame().setIconImage(GUIManager.DECAF_ICON);
		setBughouse(true);
		setChessArea(new ChessArea());
		setPartnersChessArea(new ChessArea());

		setBughouseChessArea(new BugChessArea(getChessArea(),
				getPartnersChessArea()));

		Container contentPane = getFrame().getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(getBughouseChessArea(), BorderLayout.CENTER);
	}

	public void dispose() {
		super.dispose();
	}

	public void recycle() {
		synchronized (this) {
			unsubscribe();
			if (this.getChessArea() != null) {
				getChessArea().recycle();
			}
			if (this.getBughouseChessArea() != null) {
				getPartnersChessArea().recycle();
			}

			if (getCommandToolbar() != null) {
				getFrame().getContentPane().remove(getCommandToolbar());
				getCommandToolbar().dispose();
			}

			WindowListener[] listeners = getFrame().getWindowListeners();
			for (int i = 0; i < listeners.length; i++) {
				getFrame().removeWindowListener(listeners[i]);
			}
		}
	}

	public void setup(GameStartEvent bugStartEvent) {
		long startTime = System.currentTimeMillis();

		synchronized (this) {

			MoveEvent game1MoveEvent = bugStartEvent
					.getInitialInboundChessMoveEvent();

			getBughouseChessArea()
					.setBugOrientation(BugChessArea.AREA1_ON_LEFT);

			if (!bugStartEvent.isBughouse()) {
				throw new IllegalArgumentException(INVALID_GAME_TYPE);
			}

			setValidating(true);

			setPlaying(bugStartEvent.getBlackName().equalsIgnoreCase(
					User.getInstance().getHandle())
					|| bugStartEvent.getWhiteName().equalsIgnoreCase(
							User.getInstance().getHandle()));

			if (isPlaying()) {
				setUserWhite(bugStartEvent.getWhiteName().equalsIgnoreCase(
						User.getInstance().getHandle()));

				getBughouseChessArea()
						.setBugOrientation(
								getPreferences().getBughousePreferences()
										.isPlayingLeftBoard() ? BugChessArea.AREA1_ON_LEFT
										: BugChessArea.AREA1_ON_RIGHT);
			} else {
				setUserWhite(false);

			}

			setGameId(bugStartEvent.getGameId());
			setPartnersGameId(bugStartEvent.getGameId());
			setObserving(!isPlaying());
			setExamining(false);
			setActive(true);
			setBughouse(true);
			setDroppable(true);
			setPartnerWhite(isPlaying() ? !isUserWhite() : false);
			resetLastMoveMadeTime();

			int intialTime = game1MoveEvent.getInitialTime() == 0 ? 10
					: game1MoveEvent.getInitialTime() * 60;

			int initialInc = game1MoveEvent.getInitialInc();

			setInitialTimeSecs(intialTime);
			setInitialIncSecs(initialInc);

			boolean isWhiteOnTop = false;
			if (!isPlaying()) {
				String following = User.getInstance().getFollowing();
				if (following != null) {
					isWhiteOnTop = game1MoveEvent.getWhiteName().equals(
							following) ? false : game1MoveEvent.getBlackName()
							.equals(following) ? true : false;
				}
			}

			getChessArea().setup(
					"" + getGameId(),
					bugStartEvent.getWhiteName(),
					bugStartEvent.getWhiteRating().equals("0P") ? "++++"
							: bugStartEvent.getWhiteRating(),
					bugStartEvent.getBlackName(),
					bugStartEvent.getBlackRating().equals("0P") ? "++++"
							: bugStartEvent.getBlackRating(), true,
					isPlaying() ? !isUserWhite() : isWhiteOnTop, intialTime,
					initialInc, game1MoveEvent.getPosition(),
					getPreferences().getBughousePreferences().isShowingLag());

			getPartnersChessArea().setup("-1", "Loading ...", "",
					"Loading ...", "", true, !getChessArea().isWhiteOnTop(),
					intialTime, initialInc, Position.getEmpty(),
					getPreferences().getBughousePreferences().isShowingLag());

			setGameId(bugStartEvent.getGameId());

			getChessArea().setWhiteTime(game1MoveEvent.getWhiteRemainingTime());
			getChessArea().setBlackTime(game1MoveEvent.getBlackRemainingTime());

			if (!game1MoveEvent.isClockTicking()) {
				getChessArea().startOrStopClocksWithoutTicking();
			}

			getChessArea().setShowTenthsWhenTimeIsLessThanSeconds(
					preferences.getBoardPreferences()
							.getShowTenthsWhenTimeIsLessThanSeconds());
			getPartnersChessArea().setShowTenthsWhenTimeIsLessThanSeconds(
					preferences.getBoardPreferences()
							.getShowTenthsWhenTimeIsLessThanSeconds());

			getChessArea()
					.setWhiteDropPieces(
							game1MoveEvent.getHoldingsChangedEvent() == null ? new int[0]
									: game1MoveEvent.getHoldingsChangedEvent()
											.getWhiteHoldings());
			getChessArea()
					.setBlackDropPieces(
							game1MoveEvent.getHoldingsChangedEvent() == null ? new int[0]
									: game1MoveEvent.getHoldingsChangedEvent()
											.getBlackHoldings());

			String frameTitle = "";

			if (isPlaying()) {
				frameTitle = "Playing " + getDescription(bugStartEvent);
				getChessArea().setSpeakingCountdown(isUserWhite());

			} else {
				frameTitle = "Observing " + getDescription(bugStartEvent);
			}

			getFrame().setTitle(frameTitle);

			if (preferences.getBughousePreferences().isShowingBugToolbar()) {
				if (getCommandToolbar() != null) {
					ChessAreaToolbar toolbar = getCommandToolbar();
					getFrame().remove(toolbar);
					toolbar.dispose();
				}

				ChessAreaToolbar toolbar = new ChessAreaToolbar(preferences,
						this);
				GUIManager.getInstance().addKeyForwarder(toolbar);
				Container container = getFrame().getContentPane();
				container.add(toolbar, BorderLayout.NORTH);
				setCommandToolbar(toolbar);
				toolbar.invalidate();
			} else {
				if (getCommandToolbar() != null) {
					ChessAreaToolbar toolbar = getCommandToolbar();
					getFrame().remove(toolbar);
					toolbar.dispose();
				}
			}

			if ((isObserving() && preferences.getBughousePreferences()
					.isShowingMoveListOnObsGame())
					|| (isPlaying() && preferences.getBughousePreferences()
							.isShowingMoveListsOnPlayingGame())) {
				showMoveList();
			} else {
				hideMoveList();

			}

			getChessArea().getMoveList().removeAllMoveListListeners();
			getChessArea().getMoveList().setRealtimeUpdateEnabled(!isPlaying());
			getChessArea().getMoveList().addMoveListListener(
					new BugChessAreaMoveListListener());

			getPartnersChessArea().getMoveList().removeAllMoveListListeners();
			getPartnersChessArea().getMoveList().setRealtimeUpdateEnabled(
					!isPlaying());
			getPartnersChessArea().getMoveList().addMoveListListener(
					new BugChessAreaMoveListListener());

			getBughouseChessArea().setBugChessAreaPrefsOnly(preferences);

			handleGameStarted();

			subscribe();

			clearPremove();

			if ((isObserving() || isExamining())) {
				EventService.getInstance().publish(
						new OutboundEvent("moves " + getGameId(), true,
								MoveListEvent.class));
			}

			if (isPlaying()
					&& getPreferences().getBughousePreferences()
							.getAutoFirstWhiteMove() != null
					&& !getPreferences().getBughousePreferences()
							.getAutoFirstWhiteMove().trim().equals("")) {
				EventService.getInstance().publish(
						new OutboundEvent(getPreferences()
								.getBughousePreferences()
								.getAutoFirstWhiteMove()));
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setup bughouse game. elapsedTime="
						+ (System.currentTimeMillis() - startTime)
						+ " isUserPlayingGame=" + isPlaying());
			}
			getFrame().invalidate();
		}
	}

	public void setObservingGameStartEvent(GameStartEvent bugStartEvent) {

		if (isActive()) {
			MoveEvent moveEvent = bugStartEvent
					.getInitialInboundChessMoveEvent();
			ChessArea partnersArea = getPartnersChessArea();
			setPartnersGameId(bugStartEvent.getGameId());
			partnersArea.setBoardId("" + bugStartEvent.getGameId());
			partnersArea.setWhiteInfo(bugStartEvent.getWhiteName(),
					bugStartEvent.getWhiteRating());
			partnersArea.setBlackInfo(bugStartEvent.getBlackName(),
					bugStartEvent.getBlackRating());
			partnersArea.setPosition(moveEvent.getPosition());
			partnersArea.setWhiteTime(moveEvent.getWhiteRemainingTime());
			partnersArea.setBlackTime(moveEvent.getBlackRemainingTime());
			partnersArea.setWhiteDropPieces(moveEvent.getHoldingsChangedEvent()
					.getWhiteHoldings());
			partnersArea.setBlackDropPieces(moveEvent.getHoldingsChangedEvent()
					.getBlackHoldings());

			if (!moveEvent.isClockTicking()) {
				partnersArea.startOrStopClocksWithoutTicking();
			}

			subscribePartner();

			if ((isObserving() || isExamining())) {
				EventService.getInstance().publish(
						new OutboundEvent("moves " + getPartnersGameId(), true,
								MoveListEvent.class));
			}
		}
	}
}