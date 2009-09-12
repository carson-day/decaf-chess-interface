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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.ChessAreaToolbar;
import decaf.gui.widgets.Disposable;
import decaf.gui.widgets.movelist.MoveList;
import decaf.messaging.inboundevent.game.GameStartEvent;
import decaf.messaging.inboundevent.game.MoveEvent;
import decaf.messaging.inboundevent.inform.MoveListEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.resources.ResourceManagerFactory;
import decaf.util.StorePGN;

/**
 * Delegates commands to an underlying chess area based on events received from
 * EventService and offers methods as well. This class is thread safe.
 */
public class ChessAreaController extends ChessAreaControllerBase implements
		Disposable, PropertyChangeListener {
	private static final Logger LOGGER = Logger
			.getLogger(ChessAreaController.class);

	private static final String INVALID_GAME_TYPE = "ChessAreaController does not handle bughouse. "
			+ "Try BughouseChessAreaController instead.";

	private static String[] DO_NOT_VALIDATE_KEYWORDS = null;

	static {
		String dontValidate = ResourceManagerFactory.getManager().getString(
				"Decaf", "dontValidateGameTypes");
		if (dontValidate == null) {
			DO_NOT_VALIDATE_KEYWORDS = new String[0];
		} else {
			DO_NOT_VALIDATE_KEYWORDS = dontValidate.trim().split(",");
		}
	}

	public ChessAreaController() {
		setBughouse(false);
		setChessArea(new ChessArea());

		setFrame(new ChessAreaFrame());
		getFrame().setIconImage(GUIManager.DECAF_ICON);
		getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Container contentPane = getFrame().getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(getChessArea(), BorderLayout.CENTER);
	}

	public void recycle() {
		synchronized (this) {
			unsubscribe();
			if (this.getChessArea() != null) {
				getChessArea().recycle();
			}
			if (this.getPartnersChessArea() != null) {
				getPartnersChessArea().recycle();
			}

			if (getCommandToolbar() != null) {
				getFrame().getContentPane().remove(getCommandToolbar());
				getCommandToolbar().dispose();
				setCommandToolbar(null);
			}

			WindowListener[] listeners = getFrame().getWindowListeners();
			for (int i = 0; i < listeners.length; i++) {
				getFrame().removeWindowListener(listeners[i]);
			}
		}

	}

	private boolean isValidatingGameType(String gameDescription) {
		boolean result = true;

		for (int i = 0; result && i < DO_NOT_VALIDATE_KEYWORDS.length; i++) {
			if (gameDescription.indexOf(DO_NOT_VALIDATE_KEYWORDS[i]) != -1) {
				result = false;
			}
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("isValidatingGameType=" + result + " gameDescription="
					+ gameDescription);
		}
		return result;

	}

	private void setup(GameStartEvent event, MoveEvent moveEvent) {
		long startTime = System.currentTimeMillis();

		int gameId = event == null ? moveEvent.getGameId() : event.getGameId();
		String whiteName = event == null ? moveEvent.getWhiteName() : event
				.getWhiteName();
		String blackName = event == null ? moveEvent.getBlackName() : event
				.getBlackName();

		if (event != null && event.isBughouse()) {
			IllegalArgumentException iae = new IllegalArgumentException(
					INVALID_GAME_TYPE);
			LOGGER.error(iae);
			throw iae;
		}

		synchronized (this) {
			setPlaying(blackName.equalsIgnoreCase(User.getInstance()
					.getHandle())
					|| whiteName.equalsIgnoreCase(User.getInstance()
							.getHandle()));

			if (isPlaying() && moveEvent != null) {

				if (moveEvent.getRelation() == -2) {
					setPlaying(false);
				}
			}

			if (isPlaying()) {
				setUserWhite(whiteName.equalsIgnoreCase(User.getInstance()
						.getHandle()));
			} else {
				setUserWhite(false);
			}

			setActive(true);
			setObserving(!isPlaying());
			setBughouse(false);

			setGameId(gameId);
			resetLastMoveMadeTime();
			String frameTitle = null;

			boolean isWhiteOnTop = false;
			if (!isPlaying()) {
				String following = User.getInstance().getFollowing();
				if (following != null) {
					isWhiteOnTop = moveEvent.getWhiteName().equals(following) ? false
							: moveEvent.getBlackName().equals(following) ? true
									: false;
				}
			}

			if (event == null) {
				// IF you mess with this code be sure and update the
				// Style12Subscriber who changes
				// from obs to ex mode when the relation changes.
				setPlaying(true);
				setExamining(true);
				setObserving(false);
				setValidating(false);
				setDroppable(moveEvent.getHoldingsChangedEvent() != null);

				getChessArea().setup("" + getGameId(),
						moveEvent.getWhiteName(), "", moveEvent.getBlackName(),
						"", isDroppable(), false, moveEvent.getPosition(),
						getPreferences().getBoardPreferences().isShowingLag());

				setInitialTimeSecs(0);
				setInitialIncSecs(0);

				frameTitle = "Examining";
			} else {
				setExamining(false);
				setDroppable(event.isCrazyhouse());
				setValidating(isValidatingGameType(event.getGameDescription()));

				getChessArea().setup(
						"" + getGameId(),
						event.getWhiteName(),
						event.getWhiteRating().equals("0P") ? "++++" : event
								.getWhiteRating(),
						event.getBlackName(),
						event.getBlackRating().equals("0P") ? "++++" : event
								.getBlackRating(),
						isDroppable(),
						isPlaying() ? !isUserWhite() : isWhiteOnTop,
						moveEvent.getInitialTime() == 0 ? 10 : moveEvent
								.getInitialTime() * 60,
						moveEvent.getInitialInc(), moveEvent.getPosition(),
						getPreferences().getBoardPreferences().isShowingLag());

				setInitialTimeSecs(moveEvent.getInitialTime() == 0 ? 10
						: moveEvent.getInitialTime() * 60);
				setInitialIncSecs(moveEvent.getInitialInc());

				if (isPlaying()) {
					frameTitle = "Playing " + getDescription(event);
					getChessArea().setSpeakingCountdown(isUserWhite());

				} else {
					frameTitle = "Observing " + getDescription(event);
				}
			}

			getFrame().setTitle(frameTitle);

			if (preferences.getBoardPreferences().isShowingToolbar()) {
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
			} else {
				if (getCommandToolbar() != null) {
					ChessAreaToolbar toolbar = getCommandToolbar();
					getFrame().remove(toolbar);
					toolbar.dispose();
				}
			}

			if ((isObserving() && preferences.getBoardPreferences()
					.isShowingMoveListOnObsGames())
					|| (isPlaying() && !isExamining() && preferences
							.getBoardPreferences()
							.isShowingMoveListOnPlayingGames())) {
				showMoveList();

			} else {
				hideMoveList();
			}

			getChessArea().getMoveList().removeAllMoveListListeners();
			getChessArea().getMoveList().setRealtimeUpdateEnabled(!isPlaying());
			getChessArea().getMoveList().addMoveListListener(
					new ChessAreaMoveListListener());
			getChessArea().getMoveList().setUpPgnListener(this);

			getChessArea().setWhiteTime(moveEvent.getWhiteRemainingTime());
			getChessArea().setBlackTime(moveEvent.getBlackRemainingTime());

			if (!moveEvent.isClockTicking()) {
				getChessArea().startOrStopClocksWithoutTicking();
			}

			getChessArea().setShowTenthsWhenTimeIsLessThanSeconds(
					preferences.getBoardPreferences()
							.getShowTenthsWhenTimeIsLessThanSeconds());

			if (isDroppable()) {
				getChessArea().setWhiteDropPieces(
						moveEvent.getHoldingsChangedEvent().getWhiteHoldings());
				getChessArea().setBlackDropPieces(
						moveEvent.getHoldingsChangedEvent().getBlackHoldings());
			}

			this.handleGameStarted();
			subscribe();

			if ((isObserving() || isExamining())) {
				EventService.getInstance().publish(
						new OutboundEvent("moves " + getGameId(), true,
								MoveListEvent.class));
			}

			clearPremove();

			if (isPlaying()
					&& !isExamining()
					&& getPreferences().getBoardPreferences()
							.getAutoFirstWhiteMove() != null
					&& !getPreferences().getBoardPreferences()
							.getAutoFirstWhiteMove().trim().equals("")) {
				EventService.getInstance()
						.publish(
								new OutboundEvent(getPreferences()
										.getBoardPreferences()
										.getAutoFirstWhiteMove()));
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setup non-bughouse game. Elapsed Time="
						+ (System.currentTimeMillis() - startTime)
						+ " + isUserPlaying=" + isPlaying());
			}
		}
	}

	public void setupExamine(MoveEvent event) {
		setup(null, event);
	}

	public void setup(GameStartEvent event) {
		setup(event, event.getInitialInboundChessMoveEvent());
	}

	public void dispose() {
		System.out.println("Disposing Chess Area Controller.");
		if (getChessArea() != null && getChessArea().getMoveList() != null) {
			getChessArea().getMoveList().removePropertyChangeListener(
					MoveList.SAVE_TO_PGN, this);
		}
		super.dispose();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getPropertyName();
		if (MoveList.SAVE_TO_PGN.equals(property)) {
			Date date = new Date();
			final JFileChooser fc = new JFileChooser();
			int returnVal = fc.showSaveDialog(getChessArea());

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				StorePGN.writeOutPGN(this, file, date);
			}
		}
	}

}