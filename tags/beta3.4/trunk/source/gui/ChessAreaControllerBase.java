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
import java.awt.Cursor;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import decaf.com.outboundevent.MoveRequestEvent;
import decaf.com.outboundevent.PartnerTellRequestEvent;
import decaf.encoder.LongAlgebraicEncoder;
import decaf.event.EventService;
import decaf.event.Subscription;
import decaf.gui.event.ClockCountdownListener;
import decaf.gui.event.DroppableHoldingsChangedEventFilter;
import decaf.gui.event.DroppableHoldingsChangedSubscriber;
import decaf.gui.event.GameEndEventFilter;
import decaf.gui.event.GameEndSubscriber;
import decaf.gui.event.IllegalMoveSubscriber;
import decaf.gui.event.Style12EventFilter;
import decaf.gui.event.Style12Subscriber;
import decaf.gui.event.SuggestBugMoveGUIMoveListener;
import decaf.gui.event.UserActionListener;
import decaf.gui.event.ValidatingPlayerMoveListener;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.CoordinatesUtil;
import decaf.gui.widgets.BugChessArea;
import decaf.gui.widgets.ChessArea;
import decaf.moveengine.Move;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;

/**
 * Delegates commands to an underlying chess area based on events received from
 * EventService and offers methods as well. This class is thread safe.
 */
public abstract class ChessAreaControllerBase implements Preferenceable,
		Disposable {
	private static final Logger LOGGER = Logger
	.getLogger(ChessAreaControllerBase.class);	
	
	private boolean isDroppable;

	private boolean isObserving;

	private boolean isExamining;

	private boolean isActive;

	private boolean isPlaying;

	private boolean isUserWhite;

	private boolean isBughouse;

	private boolean isPartnerWhite;

	private boolean isValidating;

	private boolean lastMoveWasPremove;

	private ChessArea chessArea;

	private ChessArea partnersChessArea;

	private BugChessArea bughouseChessArea;

	private JFrame frame;

	private int gameId;

	private int partnersGameId;

	protected Preferences preferences;

	private ChessAreaToolbar commandToolbar;

	private SuggestBugMoveGUIMoveListener suggestMoveBugListener;

	private UserActionListener userMoveListener;

	private UserActionListener examineGUIMoveListener;

	private GameEndSubscriber gameEndSubscriber;

	private DroppableHoldingsChangedSubscriber droppableHoldingsChangedSubscriber;

	private DroppableHoldingsChangedEventFilter droppableHoldingsChangedEventFilter;

	private IllegalMoveSubscriber illegalMoveSubscriber;

	private ClockCountdownListener soundPlayingChessClockListener;

	private GameEndSubscriber partnersGameEndSubscriber;

	private DroppableHoldingsChangedSubscriber partnersDroppableHoldingsChangedSubscriber;

	private DroppableHoldingsChangedEventFilter partnersDroppableHoldingsChangedEventFilter;

	private GameEndEventFilter partnersGameEndEventFilter;

	private Style12EventFilter partnersStyle12EventFilter;

	private Style12Subscriber partnersStyle12Subsriber;

	private GameEndEventFilter gameEndEventFilter;

	private Style12EventFilter style12EventFilter;

	private Style12Subscriber style12Subsriber;

	private List<PremoveInfo> queuedPremoves = new LinkedList<PremoveInfo>();

	private boolean isDisposed = false;
	
	private List<Subscription> subscriptions = new LinkedList<Subscription>();


	private static final LongAlgebraicEncoder MOVE_ENCODER = new LongAlgebraicEncoder();

	private class PremoveInfo {
		public int[] endCoordinates;

		public String move;
	}

	public int getBughouserDividerLocation() {
		return bughouseChessArea.getSplitPaneLocation();
	}

	public Style12Subscriber getPartnersStyle12Subsriber() {
		return partnersStyle12Subsriber;
	}

	public void setPartnersStyle12Subsriber(
			Style12Subscriber partnersStyle12Subsriber) {
		this.partnersStyle12Subsriber = partnersStyle12Subsriber;
	}

	public Style12Subscriber getStyle12Subsriber() {
		return style12Subsriber;
	}

	public void setStyle12Subsriber(Style12Subscriber style12Subsriber) {
		this.style12Subsriber = style12Subsriber;
	}

	public void rotate() {
		if (isBughouse()) {
			getBughouseChessArea().rotate();
		}
	}

	public void setBugOrientation(int orientation) {
		if (isBughouse()) {
			getBughouseChessArea().setBugOrientation(orientation);
		}
	}

	public void dispose() {
		
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("A chess area controller is being disposed");
		}
		
		synchronized (this) {
			if (!isDisposed) {
				unsubscribe();
				if (frame != null) {
					frame.setVisible(false);
					frame.dispose();
					frame = null;
				}

				if (chessArea != null) {
					chessArea.dispose();
					chessArea = null;
				}
				if (partnersChessArea != null) {
					partnersChessArea.dispose();
					partnersChessArea = null;
				}
				if (bughouseChessArea != null) {
					bughouseChessArea.dispose();
					bughouseChessArea = null;
				}
				if (preferences != null) {
					preferences = null;
				}
				if (commandToolbar != null) {
					commandToolbar.dispose();
					commandToolbar = null;
				}
				if (suggestMoveBugListener != null) {
					suggestMoveBugListener.dispose();
					suggestMoveBugListener = null;
				}
				if (userMoveListener != null) {
					userMoveListener.dispose();
					userMoveListener = null;
				}
				if (examineGUIMoveListener != null) {
					examineGUIMoveListener.dispose();
					examineGUIMoveListener = null;
				}
				if (gameEndSubscriber != null) {
					gameEndSubscriber.dispose();
					gameEndSubscriber = null;
				}
				if (droppableHoldingsChangedSubscriber != null) {
					droppableHoldingsChangedSubscriber.dispose();
					droppableHoldingsChangedSubscriber = null;
				}
				if (illegalMoveSubscriber != null) {
					illegalMoveSubscriber.dispose();
					illegalMoveSubscriber = null;
				}
				if (soundPlayingChessClockListener != null) {
					soundPlayingChessClockListener.dispose();
					soundPlayingChessClockListener = null;
				}
				if (partnersGameEndSubscriber != null) {
					partnersGameEndSubscriber.dispose();
					partnersGameEndSubscriber = null;
				}
				if (partnersDroppableHoldingsChangedSubscriber != null) {
					partnersDroppableHoldingsChangedSubscriber.dispose();
					partnersDroppableHoldingsChangedSubscriber = null;
				}
				if (partnersStyle12Subsriber != null) {
					partnersStyle12Subsriber.dispose();
					partnersStyle12Subsriber = null;
				}
				if (style12Subsriber != null) {
					style12Subsriber.dispose();
					style12Subsriber = null;
				}

				partnersDroppableHoldingsChangedEventFilter = null;
				partnersGameEndEventFilter = null;
				partnersStyle12EventFilter = null;

				gameEndEventFilter = null;
				style12EventFilter = null;


				isDisposed = true;
			}
		}

	}

	/**
	 * @param isValidating
	 *            The isValidating to set.
	 */
	protected void setValidating(boolean isValidating) {
		this.isValidating = isValidating;
	}

	/**
	 * @return Returns the isValidating.
	 */
	public boolean isValidating() {
		return isValidating;
	}

	public final boolean isUserWhite() {
		return isUserWhite;
	}

	public final boolean isPartnerWhite() {
		return isPartnerWhite;
	}

	public final void setPremove(String premove, int[] startCoordinates,
			int[] endCoordinates) {
		synchronized (this) {
			switch (preferences.getBoardPreferences().getPremoveType()) {
			case BoardPreferences.NO_PREMOVE: {
				break;
			}
			case BoardPreferences.TRUE_PREMOVE: {
				setTruePremove(premove, startCoordinates, endCoordinates);
				break;
			}
			case BoardPreferences.QUEUED_PREMOVE: {
				setQeueuedPremove(premove, startCoordinates, endCoordinates);
				break;
			}
			default: {
				throw new IllegalStateException("Invalid premove type: "
						+ preferences.getBoardPreferences().getPremoveType());
			}
			}
		}

	}

	private void setTruePremove(String premove, int[] startCoordinates,
			int[] endCoordinates) {
		if (premove != null
				&& preferences.getBoardPreferences().getPremoveType() == BoardPreferences.TRUE_PREMOVE) {
			queuedPremoves.clear();
			PremoveInfo premoveInfo = new PremoveInfo();
			premoveInfo.move = premove;
			premoveInfo.endCoordinates = endCoordinates;
			queuedPremoves.add(premoveInfo);

			getChessArea().setStatusText(
					"Current premove: " + getPremovesText());
			getCommandToolbar().setClearPremoveEnabled(true);

			unselectAllSquares();
			selectSquare(endCoordinates);

		} else {
			// LOGGER.warn("Null premove was encountered");
		}
	}

	private void setQeueuedPremove(String premove, int[] startCoordinates,
			int[] endCoordinates) {
		if (premove != null
				&& preferences.getBoardPreferences().getPremoveType() == BoardPreferences.QUEUED_PREMOVE) {
			PremoveInfo premoveInfo = new PremoveInfo();
			premoveInfo.move = premove;
			premoveInfo.endCoordinates = endCoordinates;
			queuedPremoves.add(premoveInfo);

			getChessArea().setStatusText("Premove queue: " + getPremovesText());
			getCommandToolbar().setClearPremoveEnabled(true);

			unselectAllSquares();
			selectSquare(endCoordinates);
		} else {
			// LOGGER.warn("Null premove was encountered");
		}

	}

	public final void clearPremove() {
		synchronized (this) {
			if (isPremoveSet()) {
				getChessArea()
						.setStatusText(
								preferences.getBoardPreferences()
										.getPremoveType() == BoardPreferences.TRUE_PREMOVE ? " Cleared premove"
										: "Cleared premove queue: "
												+ getPremovesText());
			}
			getCommandToolbar().setClearPremoveEnabled(false);
			queuedPremoves.clear();
			unselectAllSquares();
		}
	}

	private String getPremovesText() {
		StringBuffer result = new StringBuffer(20);

		for (PremoveInfo info : getPremoves()) {
			result.append(info.move + " ");
		}
		return result.toString();
	}

	private List<PremoveInfo> getPremoves() {
		return queuedPremoves;
	}

	private final void selectSquare(int[] coordinates) {
		getChessArea().getBoard().selectSquare(coordinates);
	}

	private final void unselectAllSquares() {
		getChessArea().getBoard().unselectAllSquares();
	}

	private final void selectPartnersBoardSquare(int[] coordinates) {
		getPartnersChessArea().getBoard().selectSquare(coordinates);
	}

	private final void unselectAllPartnersBoardSquares() {
		getPartnersChessArea().getBoard().unselectAllSquares();
	}

	public boolean makePremove() {
		synchronized (this) {
			if (isPremoveSet()) {
				PremoveInfo premove = queuedPremoves.remove(0);
				chessArea.setStatusText("Sending premove: " + premove.move
						+ ". Moves left in queued " + queuedPremoves.size());
				EventService.getInstance().publish(new MoveRequestEvent(getGameId(),
						premove.move, true));
				return true;
			}
			return false;
		}

	}

	public void handleUserPositionUpdate(String move, Position position,
			int timeTaken, int whiteTime, int blackTime,
			int lagLastMoveInMillis, boolean areClocksTicking) {
		synchronized (this) {
			handleUserPositionUpdate(move, position, timeTaken, whiteTime,
					blackTime, null, null, lagLastMoveInMillis,
					areClocksTicking);
		}
	}

	public void handleUserPositionUpdate(String move, Position position,
			int timeTaken, int whiteTime, int blackTime, int[] whiteDropPieces,
			int[] blackDropPieces, int lagLastMoveInMillis,
			boolean areClocksTicking) {
		synchronized (this) {
			String queuedPremovesString = null;
			boolean wasPremoveMade = false;
			// make premove if its set.
			if (isPlaying() && isPremoveSet() && isUsersMove()) {
				// These flags handle selecting the last move if it was a
				// premove on the board.
				// It is a bit hacky.
				makePremove();
				wasPremoveMade = true;
				lastMoveWasPremove = true;

				if (isPremoveSet()) {
					queuedPremovesString = getPremovesText();
				}
			}

			unselectAllSquares();

			if ((isBughouse() && preferences.getBughousePreferences()
					.isShowingLag())
					|| (!isBughouse() && preferences.getBoardPreferences()
							.isShowingLag())) {
				if (isWhitesMove()) {
					getChessArea().setWhiteLag(lagLastMoveInMillis);
				} else {
					getChessArea().setBlackLag(lagLastMoveInMillis);
				}
			}
			getChessArea().setWhiteTime(whiteTime);
			getChessArea().setBlackTime(blackTime);
			getChessArea().setPosition(position, areClocksTicking);

			if (chessArea.isDroppable() && whiteDropPieces != null) {
				getChessArea().setWhiteDropPieces(whiteDropPieces);
			}

			if (chessArea.isDroppable() && blackDropPieces != null) {
				getChessArea().setBlackDropPieces(blackDropPieces);
			}

			if (move.equals("none")) // fics sends out a none for move if a
			{
				chessArea.setStatusText("Refreshed position from server. "
						+ (queuedPremovesString == null ? ""
								: " Queued premoves: " + queuedPremovesString));
			} else if (move != null && !move.equals("")) {
				chessArea.setStatusText("Last Move: "
						+ move
						+ "("
						+ timeTaken
						+ ")"
						+ (queuedPremovesString == null ? ""
								: " Queued premoves: " + queuedPremovesString));

				// Dont select if its the users move that was made.
				if (!isPlaying || lastMoveWasPremove || !isUsersMove()) {
					int[] coordinates = getToSquareCoordinates(move);

					if (coordinates != null) {
						selectSquare(coordinates);
					}
				}
			}

			if (isPlaying() || isExamining()) {
				SoundManager.getInstance().playSound(SoundKeys.MOVE_KEY);
			} else if (isObserving()
					&& preferences.getBoardPreferences()
							.isPlayingMoveSoundOnObserving()) {
				SoundManager.getInstance().playSound(SoundKeys.OBS_MOVE_KEY);
			}

			if (!wasPremoveMade) {
				lastMoveWasPremove = false;
			}
		}
	}

	public boolean isUsersMove() {
		return !((isPlaying && isWhitesMove() && isUserWhite()) || (isPlaying
				&& !isWhitesMove() && !isUserWhite()));
	}

	public boolean isUsersPartersMove() {
		return isPlaying
				&& (isPartnerWhite() && getPartnersPosition().isWhitesMove())
				|| (!isPartnerWhite() && !getPartnersPosition().isWhitesMove());
	}

	private int[] getToSquareCoordinates(String move) {
		move = move.replaceAll("\\+", "");
		move = move.replaceAll("\\#", "");
		int atIndex = move.indexOf("@");
		int takesIndex = move.indexOf("x");
		String lastSquare = null;
		int[] result = null;

		if (takesIndex != -1) {
			lastSquare = move.substring(takesIndex + 1, move.length()).trim();
		} else if (atIndex != -1) {
			lastSquare = move.substring(atIndex + 1, move.length()).trim();
		} else if (move.length() >= 2) {
			lastSquare = move.substring(move.length() - 2, move.length());
		}

		if (lastSquare != null) {
			result = CoordinatesUtil.algebraicToCoordinates(lastSquare);
		}
		return result;
	}

	public void updateUserDropPieces(int[] whiteDropPieces,
			int[] blackDropPieces) {
		synchronized (this) {
			if (isDroppable() && whiteDropPieces != null) {
				getChessArea().setWhiteDropPieces(whiteDropPieces);
				getChessArea().getPosition().setWhiteHoldings(whiteDropPieces);
			}
			if (isDroppable() && blackDropPieces != null) {
				getChessArea().setBlackDropPieces(blackDropPieces);
				getChessArea().getPosition().setBlackHoldings(blackDropPieces);
			}
		}
	}

	public void updatePartnerDropPieces(int[] whiteDropPieces,
			int[] blackDropPieces) {
		synchronized (this) {
			if (isDroppable() && whiteDropPieces != null) {
				getPartnersChessArea().setWhiteDropPieces(whiteDropPieces);
				getPartnersChessArea().getPosition().setWhiteHoldings(
						whiteDropPieces);
			}
			if (isDroppable() && blackDropPieces != null) {
				getPartnersChessArea().setBlackDropPieces(blackDropPieces);
				getPartnersChessArea().getPosition().setBlackHoldings(
						blackDropPieces);
			}
		}
	}

	public void handlePartnerPositionUpdate(String move, Position position,
			int timeTaken, int whiteTime, int blackTime,
			int lagLastMoveInMillis, boolean areClocksTicking) {
		synchronized (this) {
			handlePartnerPositionUpdate(move, position, timeTaken, whiteTime,
					blackTime, null, null, lagLastMoveInMillis,
					areClocksTicking);
		}
	}

	public void handlePartnerPositionUpdate(String move, Position position,
			int timeTaken, int whiteTime, int blackTime, int[] whiteDropPieces,
			int[] blackDropPieces, int lagLastMoveInMillis,
			boolean areClocksTicking) {
		synchronized (this) {
			if (isBughouse()
					&& preferences.getBughousePreferences().isShowingLag()) {
				if (isWhitesMoveOnPartnersBoard()) {
					getPartnersChessArea().setWhiteLag(lagLastMoveInMillis);
				} else {
					getPartnersChessArea().setBlackLag(lagLastMoveInMillis);
				}
			}

			unselectAllPartnersBoardSquares();
			getPartnersChessArea().setWhiteTime(whiteTime);
			getPartnersChessArea().setBlackTime(blackTime);
			getPartnersChessArea().setPosition(position, areClocksTicking);

			if (isDroppable() && whiteDropPieces != null) {
				getPartnersChessArea().setWhiteDropPieces(whiteDropPieces);
			}

			if (isDroppable() && blackDropPieces != null) {
				getPartnersChessArea().setBlackDropPieces(blackDropPieces);
			}

			if (move != null && !move.equals("")) {
				getPartnersChessArea().setStatusText(
						"Last Move: " + move + "(" + timeTaken + ")");

				int[] coordinates = getToSquareCoordinates(move);
				if (coordinates != null) {
					selectPartnersBoardSquare(coordinates);
				}
			}

			if ((isBughouse() && !isObserving() && preferences
					.getBughousePreferences()
					.isPlayingMoveSoundOnPartnersBoard())
					|| (isBughouse() && isObserving() && preferences
							.getBoardPreferences()
							.isPlayingMoveSoundOnObserving())) {
				SoundManager.getInstance().playSound(SoundKeys.OBS_MOVE_KEY);
			}
		}
	}

	public final boolean isPremoveSet() {
		return queuedPremoves.size() > 0;
	}

	public final boolean isPlaying() {
		return isPlaying;
	}

	public void givePartnerAdvice(String advice) {
		EventService.getInstance().publish(new PartnerTellRequestEvent(advice));
	}

	public void handleIllegalPartnerSuggestion(String move) {
		// Currently not implemented. Suggest to partner anyway.
		givePartnerAdvice(move);
	}

	public boolean isWhitesMove() {
		return getChessArea().isWhitesMove();
	}

	public boolean isWhitesMoveOnPartnersBoard() {
		return getPartnersChessArea().isWhitesMove();
	}

	public Position getPosition() {
		return getChessArea().getPosition();
	}

	public Position getPartnersPosition() {
		return getPartnersChessArea().getPosition();
	}

	private String encodeMove(Move move) {
		return MOVE_ENCODER.encode(move, getPosition());
	}

	public void makeUnvalidatedMove(String moveString) {
		synchronized (this) {
			getChessArea().setStatusText("Sending move: " + moveString);
			EventService.getInstance().publish(new MoveRequestEvent(getGameId(), moveString,
					true));
		}
	}

	public void makeMove(Move move, Position newPosition) {
		synchronized (this) {
			getChessArea().setPosition(newPosition);
			String moveString = encodeMove(move);
			getChessArea().setStatusText("Sending move: " + moveString);
			EventService.getInstance().publish(new MoveRequestEvent(getGameId(), moveString,
					true));
			// No need to play sound since a style 12 move is sent and it is
			// played
			// then
			// soundManager.playSound(Skin.MOVE_ALERT_SOUND_KEY);
		}
	}

	/**
	 * The user made an invalid move on the input device this ControllerBase is
	 * connected to.
	 */
	public void handleInvalidMove(Move move) {
		synchronized (this) {
			getChessArea().setStatusText("Invalid move: " + encodeMove(move));
			playIllegalMoveSound();
		}
	}

	public void playIllegalMoveSound() {
		SoundManager.getInstance().playSound(SoundKeys.ILLEGAL_MOVE_KEY);
	}

	/**
	 * The user made an invalid move on the input device this ControllerBase is
	 * connected to.
	 */
	public void handleInvalidPremove(String move) {
		synchronized (this) {
			// setPremove(move);
			getChessArea().setStatusText(
					"Invalid premove: " + move + ". Premove has been cleared.");
			clearPremove();
			playIllegalMoveSound();
		}
	}

	/**
	 * For some reason this ControllerBase is contacted to vetoed the move.
	 */
	public void handleMoveVetoedEvent(String move) {
		synchronized (this) {
			clearPremove();
			chessArea.setStatusText("Server vetoed move: " + move
					+ ". Refreshing position");

			// No need to refresh a style 12 should be sent on an illegal move.
			// eventService.publish(new RefreshRequestEvent(getGameId(), true));
			playIllegalMoveSound();
		}
	}

	public void handleGameStarted() {
		SoundManager.getInstance().playSound(SoundKeys.GAME_START_KEY);
	}

	public void handleUserWon(String description) {
		synchronized (this) {
			if (isPlaying()) {
				SoundManager.getInstance().playSound(SoundKeys.WIN_KEY);
			} else {
				SoundManager.getInstance().playSound(SoundKeys.OBS_GAME_END_KEY);
			}

			setActive(false);
			setPlaying(false);
			chessArea.setStatusText(description);
		}
	}

	public void handleUserLost(String description) {
		synchronized (this) {
			setActive(false);
			setPlaying(false);
			chessArea.setStatusText(description);

			if (isPlaying()) {
				SoundManager.getInstance().playSound(SoundKeys.LOSE_KEY);
			} else {
				SoundManager.getInstance().playSound(SoundKeys.OBS_GAME_END_KEY);
			}
		}
	}

	public void handleUserDrew(String description) {
		synchronized (this) {
			setActive(false);
			setPlaying(false);
			chessArea.setStatusText(description);
			SoundManager.getInstance().playSound(SoundKeys.OBS_GAME_END_KEY);
		}

	}

	public void handleGameStopped(String description) {
		// if (isPlaying())
		// {
		// soundManager.playSound(SoundKeys.GAME_WIN_SOUND_KEY);
		// }
		synchronized (this) {
			setActive(false);
			setPlaying(false);
			chessArea.setStatusText(description);
		}
	}

	public final boolean isActive() {
		return isActive;
	}

	public final void setActive(boolean param) {
		synchronized(this)
		{
			if (!param && isActive) {
				isActive = false;
				unsubscribe();
	
				if (getCommandToolbar() != null && isPlaying()) {
					Container container = getFrame().getContentPane();
					ChessAreaToolbar toolbar = getCommandToolbar();
	
					ChessAreaToolbar replacement = new ChessAreaToolbar(
							getPreferences(), this);
	
					container.remove(toolbar);
					toolbar.dispose();
					setCommandToolbar(replacement);
	
					container.add(replacement, BorderLayout.NORTH);
				}
				if (getChessArea() != null) {
					getChessArea().setActive(false);
				}
				if (getPartnersChessArea() != null) {
					getPartnersChessArea().setActive(false);
				}
			} else {
				isActive = param;
			}
		}
	}

	public final JFrame getFrame() {
		return frame;
	}

	public int showPromotionDialog(boolean isWhitePromotion) {
		synchronized (this) {
			if (isPlaying()
					&& preferences.getBoardPreferences().getAutoPromotionMode() != BoardPreferences.AUTO_PROMOTE_DISABLED) {

				switch (preferences.getBoardPreferences()
						.getAutoPromotionMode()) {
				case BoardPreferences.AUTO_BISHOP: {
					return isWhitePromotion ? Piece.WB : Piece.BB;
				}
				case BoardPreferences.AUTO_KNIGHT: {
					return isWhitePromotion ? Piece.WN : Piece.BN;
				}
				case BoardPreferences.AUTO_ROOK: {
					return isWhitePromotion ? Piece.WR : Piece.BR;
				}
				case BoardPreferences.AUTO_QUEEN: {
					return isWhitePromotion ? Piece.WQ : Piece.BQ;
				}
				default: {
					throw new IllegalStateException(
							"Unknown Auto Promotion Mode: "
									+ preferences.getBoardPreferences()
											.getAutoPromotionMode());
				}
				}
			} else if (isPlaying()) {
				return PromotionDialog.showPromotionDialog(getFrame(),
						isWhitePromotion, preferences.getBoardPreferences()
								.getBackgroundControlsColor());
			} else {
				return Piece.EMPTY;
			}
		}
	}

	public final void flip() {
		synchronized (this) {
			getChessArea().flip();
			if (isBughouse()) {
				getPartnersChessArea().flip();
			}
		}
	}

	public final Cursor getCursor() {
		return getFrame().getCursor();
	}

	public void setCursor(Cursor cursor) {
		getFrame().setCursor(cursor);
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		synchronized (this) {
			this.preferences = preferences;
			if (isBughouse()) {
				if (getBughouseChessArea() != null) {

					getBughouseChessArea().setPreferences(preferences);

					if (preferences.getBughousePreferences()
							.isShowingStatusBar()) {
						getChessArea().addStatusBar();
						getPartnersChessArea().addStatusBar();
					} else if (!preferences.getBughousePreferences()
							.isShowingStatusBar()) {
						getChessArea().removeStatusBar();
						getPartnersChessArea().removeStatusBar();
					}
				}
			} else {
				if (getChessArea() != null) {
					getChessArea().setPreferences(preferences);

					if (preferences.getBoardPreferences().isShowingStatusBar()) {
						getChessArea().addStatusBar();
					} else if (!preferences.getBoardPreferences()
							.isShowingStatusBar()) {
						getChessArea().removeStatusBar();
					}
				}
			}
			if (getCommandToolbar() != null) {
				getCommandToolbar().setPreferences(preferences);
			}
		}
	}

	public final boolean isDroppable() {
		return isDroppable;
	}

	public final boolean isObserving() {
		return isObserving;
	}

	public final boolean isExamining() {
		return isExamining;
	}

	public final int getGameId() {
		return gameId;
	}

	public final int getPartnersGameId() {
		return partnersGameId;
	}

	public final boolean isBughouse() {
		return isBughouse;
	}

	/*
	 * protected final void setFrameBounds() { synchronized (this) { Point
	 * location = preferences.getChess(); Dimension dimension = isBughouse() ?
	 * preferences .getBughouseFrameDimension() : isDroppable() ? preferences
	 * .getCrazyhouseFrameDimension() : preferences .getChessFrameDimension();
	 * getFrame().setBounds(location.x, location.y, dimension.width,
	 * dimension.height); } }
	 * 
	 * protected final void setFrameBounds(Point initialPoint) { Dimension
	 * dimension = isBughouse() ? preferences .getBughouseFrameDimension() :
	 * isDroppable() ? preferences .getCrazyhouseFrameDimension() : preferences
	 * .getChessFrameDimension(); getFrame().setBounds(initialPoint.x,
	 * initialPoint.y, dimension.width, dimension.height); }
	 */

	protected void setUserWhite(boolean isUserWhite) {
		this.isUserWhite = isUserWhite;
	}

	protected void setPartnerWhite(boolean isPartnerWhite) {
		this.isPartnerWhite = isPartnerWhite;
	}

	protected void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	protected void setFrame(JFrame frame) {
		this.frame = frame;
	}

	protected void setDroppable(boolean isDroppable) {
		this.isDroppable = isDroppable;
	}

	protected void setObserving(boolean isObserving) {
		this.isObserving = isObserving;
	}

	protected void setExamining(boolean isExamining) {
		this.isExamining = isExamining;
	}

	protected void setGameId(int gameId) {
		this.gameId = gameId;
		getChessArea().setBoardId("" + gameId);
	}

	protected void setPartnersGameId(int partnersGameId) {
		this.partnersGameId = partnersGameId;
		getPartnersChessArea().setBoardId("" + gameId);
	}

	protected void setBughouse(boolean isBughouse) {
		this.isBughouse = isBughouse;
	}

	protected final BugChessArea getBughouseChessArea() {
		return bughouseChessArea;
	}

	protected final void setBughouseChessArea(BugChessArea bughouseChessArea) {
		this.bughouseChessArea = bughouseChessArea;

	}

	protected final ChessArea getChessArea() {
		return chessArea;
	}

	protected final void setChessArea(ChessArea chessArea) {
		this.chessArea = chessArea;
	}

	protected final ChessArea getPartnersChessArea() {
		return partnersChessArea;
	}

	protected final void setPartnersChessArea(ChessArea partnersChessArea) {
		this.partnersChessArea = partnersChessArea;
	}

	protected ChessAreaToolbar getCommandToolbar() {
		return commandToolbar;
	}

	protected void setCommandToolbar(ChessAreaToolbar commandToolbar) {
		this.commandToolbar = commandToolbar;
	}

	/**
	 * Over-ride to provide an implementation. Currently just unsubscribes to
	 * all events and removes all GUIMoveListeners.
	 */
	protected void unsubscribe() {
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("A ChessAreaController is unsubscribing.");
		}
		synchronized(this)
		{

			for (Subscription subscription : subscriptions)
			{
				EventService.getInstance().unsubscribe(subscription);
			}
			subscriptions.clear();
			
			if (getChessArea() != null)
			{
			   getChessArea().removeAllListeners();
			}
			if (getPartnersChessArea() != null)
			{
				getPartnersChessArea().removeAllListeners();
			}
		}
	}
	
	/**
	 * All subscriptions should go through this method so unsubscribe will work properly.
	 * @param subscription
	 */
	private void subscribe(Subscription subscription)
	{
		subscriptions.add(subscription);
		EventService.getInstance().subscribe(subscription);
	}

	/**
	 * Sets up all event handling.
	 */
	protected void subscribe() {
		
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("A ChessAreaController is setting up subscriptions.");
		}
		synchronized(this)
		{
			if (isPlaying()) {
				userMoveListener = new ValidatingPlayerMoveListener(this,
						isValidating());
				getChessArea().addUserActionListener(userMoveListener);
				
				
				illegalMoveSubscriber = new IllegalMoveSubscriber(this);
				subscribe(new Subscription(
						decaf.com.inboundevent.game.IllegalMoveEvent.class, null,
						illegalMoveSubscriber));
	
				if (preferences.getSpeechPreferences()
						.isSpeaking10SecondCountdown()) {
					soundPlayingChessClockListener = new ClockCountdownListener();
					if (isUserWhite()) {
						getChessArea().addWhiteChessClockListener(
								soundPlayingChessClockListener);
					} else {
						getChessArea().addBlackChessClockListener(
								soundPlayingChessClockListener);
					}
				}
	
			} else if (isExamining()) {
				examineGUIMoveListener = new ValidatingPlayerMoveListener(this,
						false, true);
				illegalMoveSubscriber = new IllegalMoveSubscriber(this);
				subscribe(new Subscription(
						decaf.com.inboundevent.game.IllegalMoveEvent.class, null,
						illegalMoveSubscriber));
				getChessArea().addUserActionListener(examineGUIMoveListener);
	
			}
			gameEndEventFilter = new GameEndEventFilter(getGameId());
			gameEndSubscriber = new GameEndSubscriber(this);
	
			style12EventFilter = new Style12EventFilter(getGameId());
			style12Subsriber = new Style12Subscriber(this);
	
			subscribe(new Subscription(
					decaf.com.inboundevent.game.GameEndEvent.class,
					gameEndEventFilter, gameEndSubscriber));
			subscribe(new Subscription(
					decaf.com.inboundevent.game.RemovingObservedGameEvent.class,
					gameEndEventFilter, gameEndSubscriber));
			subscribe(new Subscription(
					decaf.com.inboundevent.game.MoveEvent.class,
					style12EventFilter, style12Subsriber));
	
			if (isDroppable()) {
				droppableHoldingsChangedSubscriber = new DroppableHoldingsChangedSubscriber(
						this);
				droppableHoldingsChangedEventFilter = new DroppableHoldingsChangedEventFilter(
						getGameId());
				subscribe(new Subscription(
								decaf.com.inboundevent.game.DroppableHoldingsChangedEvent.class,
								droppableHoldingsChangedEventFilter,
								droppableHoldingsChangedSubscriber));
			}
		}
	}

	public void subscribePartner() {
		
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("A ChessAreaController is setting up partner subscriptions.");
		}
		
		synchronized(this)
		{
			if (isBughouse()) {
				partnersGameEndEventFilter = new GameEndEventFilter(
						getPartnersGameId());
				partnersGameEndSubscriber = new GameEndSubscriber(this);
	
				partnersStyle12EventFilter = new Style12EventFilter(
						getPartnersGameId());
				partnersStyle12Subsriber = new Style12Subscriber(this, true);
	
				partnersDroppableHoldingsChangedEventFilter = new DroppableHoldingsChangedEventFilter(
						getPartnersGameId());
				partnersDroppableHoldingsChangedSubscriber = new DroppableHoldingsChangedSubscriber(
						this, true);
	
				subscribe(new Subscription(
								decaf.com.inboundevent.game.GameEndEvent.class,
								partnersGameEndEventFilter,
								partnersGameEndSubscriber));
				subscribe(new Subscription(
								decaf.com.inboundevent.game.MoveEvent.class,
								partnersStyle12EventFilter,
								partnersStyle12Subsriber));
				subscribe(new Subscription(
								decaf.com.inboundevent.game.DroppableHoldingsChangedEvent.class,
								partnersDroppableHoldingsChangedEventFilter,
								partnersDroppableHoldingsChangedSubscriber));
	
				if (isPlaying()) {
					suggestMoveBugListener = new SuggestBugMoveGUIMoveListener(this);
					partnersChessArea.addUserActionListener(suggestMoveBugListener);
				}
			}
		}
	}

	public Style12EventFilter getPartnersStyle12EventFilter() {
		return partnersStyle12EventFilter;
	}

	public void setPartnersStyle12EventFilter(
			Style12EventFilter partnersStyle12EventFilter) {
		this.partnersStyle12EventFilter = partnersStyle12EventFilter;
	}
	
	

}