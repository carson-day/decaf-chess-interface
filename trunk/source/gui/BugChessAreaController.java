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
package decaf.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import decaf.com.inboundevent.game.GameStartEvent;
import decaf.com.inboundevent.game.MoveEvent;
import decaf.gui.util.User;
import decaf.gui.widgets.BugChessArea;
import decaf.gui.widgets.ChessArea;
import decaf.moveengine.Position;

public class BugChessAreaController extends ChessAreaControllerBase implements
		Disposable {

	private static final Logger LOGGER = Logger
			.getLogger(BugChessAreaController.class);

	private static User user = User.getInstance();

	private static final String INVALID_GAME_TYPE = "BughouseChessAreaController does not handle "
			+ "anything besides bughouse. Try ChessAreaController instead.";

	public void dispose() {
		super.dispose();
	}

	public void prepareForRecyling() {
		synchronized(this)
		{
			unsubscribe();
			if (this.getChessArea() != null) {
				getChessArea().prepareForRecycling();
			}
			if (this.getBughouseChessArea() != null) {
				getChessArea().prepareForRecycling();
			}
	
			if (getCommandToolbar() != null) {
				getFrame().getContentPane().remove(getCommandToolbar());
				getCommandToolbar().dispose();
			}
			
			WindowListener[] listeners = getFrame().getWindowListeners();
			for (int i = 0; i < listeners.length; i++)
			{
				getFrame().removeWindowListener(listeners[i]);
			}
		}
	}

	public void setup(GameStartEvent bugStartEvent) {
		long startTime = System.currentTimeMillis();

		this.preferences = preferences;
		
		synchronized(this)
		{

			MoveEvent game1MoveEvent = bugStartEvent
					.getInitialInboundChessMoveEvent();
	
			if (bugStartEvent.getGameType() != GameStartEvent.BUGHOUSE) {
				throw new IllegalArgumentException(INVALID_GAME_TYPE);
			}
	
			setValidating(true);
	
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("WhiteName=" + bugStartEvent.getWhiteName()
						+ " BlackName=" + bugStartEvent.getBlackName()
						+ " playerName=" + User.getInstance().getHandle());
			}
			setPlaying(bugStartEvent.getBlackName().equalsIgnoreCase(
					user.getHandle())
					|| bugStartEvent.getWhiteName().equalsIgnoreCase(
							user.getHandle()));
	
			if (isPlaying()) {
				setUserWhite(bugStartEvent.getWhiteName().equalsIgnoreCase(
						user.getHandle()));
	
				getBughouseChessArea().setBugOrientation(
						getPreferences().getBughousePreferences()
								.isPlayingLeftBoard() ? BugChessArea.AREA1_ON_LEFT
								: BugChessArea.AREA1_ON_RIGHT);
			} else {
				setUserWhite(false);
			}
	
			setObserving(!isPlaying());
			setExamining(false);
			setActive(true);
			setBughouse(true);
			setDroppable(true);
			setPartnerWhite(isPlaying() ? !isUserWhite() : false);
	
			int intialTime = game1MoveEvent.getInitialTime() == 0 ? 10
					: game1MoveEvent.getInitialTime() * 60;
	
			int initialInc = game1MoveEvent.getInitialInc();
	
			LOGGER.debug("getChessArea=" + getChessArea() + " "
					+ bugStartEvent.getWhiteRating() + " "
					+ bugStartEvent.getBlackRating());
	
			getChessArea().setup(
					"" + getGameId(),
					bugStartEvent.getWhiteName(),
					bugStartEvent.getWhiteRating().equals("0") ? "++++"
							: bugStartEvent.getWhiteRating(),
					bugStartEvent.getBlackName(),
					bugStartEvent.getBlackRating().equals("0") ? "++++"
							: bugStartEvent.getBlackRating(), true,
					isPlaying() ? !isUserWhite() : false, intialTime, initialInc,
					game1MoveEvent.getPosition());
	
			getPartnersChessArea().setup("-1", "Loading ...", "", "Loading ...",
					"", true, isPlaying() ? isUserWhite() : true, intialTime,
					initialInc, Position.getEmpty());
	
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
	
			getChessArea().setWhiteDropPieces(
					game1MoveEvent.getHoldingsChangedEvent().getWhiteHoldings());
			getChessArea().setBlackDropPieces(
					game1MoveEvent.getHoldingsChangedEvent().getBlackHoldings());
	
			String frameTitle = "";
	
			if (isPlaying()) {
				frameTitle = "Playing Bughouse " + game1MoveEvent.getInitialTime()
						+ " " + game1MoveEvent.getInitialInc()
						+ (bugStartEvent.isRated() ? " rated " : " unrated");
	
			} else {
				frameTitle = "Observing Bughouse "
						+ +game1MoveEvent.getInitialTime() + " "
						+ game1MoveEvent.getInitialInc()
						+ (bugStartEvent.isRated() ? " rated" : " unrated");
			}
	
			getFrame().setTitle(frameTitle);
	
			if (preferences.getBughousePreferences().isShowingBugToolbar()) {
				setCommandToolbar(new ChessAreaToolbar(preferences, this));
				Container container = getFrame().getContentPane();
				container.add(getCommandToolbar(), BorderLayout.NORTH);
			}
	
			getBughouseChessArea().setBugChessAreaPrefsOnly(preferences);
	
			handleGameStarted();
	
			subscribe();
	
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setup bughouse game. elapsedTime="
						+ (System.currentTimeMillis() - startTime)
						+ " isUserPlayingGame=" + isPlaying());
			}
		}
	}

	public BugChessAreaController() {
		setFrame(new JFrame("Not Initialized this is a bug message cday"));
		getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setChessArea(new ChessArea());
		setPartnersChessArea(new ChessArea());

		setBughouseChessArea(new BugChessArea(getChessArea(),
				getPartnersChessArea()));
		setBughouse(true);

		Container contentPane = getFrame().getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(getBughouseChessArea(), BorderLayout.CENTER);
	}

	public void setObservingGameStartEvent(GameStartEvent bugStartEvent) {

		MoveEvent moveEvent = bugStartEvent.getInitialInboundChessMoveEvent();
		ChessArea partnersArea = getPartnersChessArea();
		setPartnersGameId(bugStartEvent.getGameId());
		partnersArea.setBoardId("" + bugStartEvent.getGameId());
		partnersArea.setWhiteInfo(bugStartEvent.getWhiteName(), bugStartEvent
				.getWhiteRating());
		partnersArea.setBlackInfo(bugStartEvent.getBlackName(), bugStartEvent
				.getBlackRating());
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
	}
}