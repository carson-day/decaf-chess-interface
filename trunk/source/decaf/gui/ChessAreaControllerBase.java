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
import java.awt.Cursor;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import decaf.dialog.PromotionDialog;
import decaf.event.Event;
import decaf.event.EventService;
import decaf.event.Filter;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.event.BugSuggestUserMoveListener;
import decaf.gui.event.DroppableHoldingsChangedEventFilter;
import decaf.gui.event.DroppableHoldingsChangedSubscriber;
import decaf.gui.event.GameEndEventFilter;
import decaf.gui.event.GameEndSubscriber;
import decaf.gui.event.IllegalMoveSubscriber;
import decaf.gui.event.Style12EventFilter;
import decaf.gui.event.Style12Subscriber;
import decaf.gui.event.ValidatingUserMoveListener;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.widgets.BugChessArea;
import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.ChessAreaToolbar;
import decaf.gui.widgets.Disposable;
import decaf.gui.widgets.movelist.MoveList;
import decaf.gui.widgets.movelist.MoveListListener;
import decaf.gui.widgets.movelist.MoveListModelMove;
import decaf.messaging.inboundevent.game.GameEndEvent;
import decaf.messaging.inboundevent.game.GameStartEvent;
import decaf.messaging.inboundevent.inform.MoveListEvent;
import decaf.messaging.outboundevent.MoveRequestEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.messaging.outboundevent.PartnerTellRequestEvent;
import decaf.moveengine.Coordinates;
import decaf.moveengine.LongAlgebraicEncoder;
import decaf.moveengine.Move;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;
import decaf.sound.SoundKeys;
import decaf.sound.SoundManagerFactory;
import decaf.speech.SpeechManager;
import decaf.util.CoordinatesUtil;
import decaf.util.StorePGN;
import decaf.util.StringUtility;

/**
 * Delegates commands to an underlying chess area based on events received from
 * EventService and offers methods as well. This class is thread safe.
 */
public abstract class ChessAreaControllerBase implements Preferenceable,
		Disposable {
	private static final Logger LOGGER = Logger
			.getLogger(ChessAreaControllerBase.class);

	public static final int WHITE_WON = GameEndEvent.WHITE_WON;

	public static final int BLACK_WON = GameEndEvent.BLACK_WON;

	public static final int DRAW = GameEndEvent.DRAW;

	public static final int ADJOURNED = GameEndEvent.ADJOURNED;

	public static final int ABORTED = GameEndEvent.ABORTED;

	public static final int UNDETERMINED = GameEndEvent.UNDETERMINED;

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

	private int initialTimeSecs;

	private int initialIncSecs;

	private ChessArea chessArea;

	private ChessArea partnersChessArea;

	private BugChessArea bughouseChessArea;

	private boolean hasSubscribed;

	private boolean hasPartnerSubscribed;

	private ChessAreaFrame frame;

	private int gameId;

	private int partnersGameId;

	private int premoveDropPiece = Piece.EMPTY;

	protected Preferences preferences;

	private ChessAreaToolbar commandToolbar;

	private long lastMoveMadeTime = -1;

	private long lastMoveResponseTime;

	private List<PremoveInfo> queuedPremoves = new LinkedList<PremoveInfo>();

	private boolean isDisposed = false;

	private List<Subscription> subscriptions = new LinkedList<Subscription>();

	private List<Subscription> partnersSubscriptions = new LinkedList<Subscription>();

	private static final LongAlgebraicEncoder MOVE_ENCODER = new LongAlgebraicEncoder();

	private Style12Subscriber style12Subscriber;

	private Style12Subscriber partnersStyle12Subscriber;

	private Position lastPosition;

	private Position lastPartnersPosition;

	private long lastWhiteTime;

	private long lastPartnersWhiteTime;

	private long lastBlackTime;

	private long lastPartnersBlackTime;

	private boolean isIgnoringBugMoveListEvents = false;

	private ChessAreaControllerBase thisController = this;

	private int gameEndState;
	
	private int [] lastOppponentMoveStart;
	
	private int [] lastOpponentMoveEnd;

	public int getGameEndState() {
		return gameEndState;
	}

	public void setSideUpTime() {
		if (isBughouse()
				&& getPreferences().getBughousePreferences()
						.isShowingUpSideUpTime()) {
			long whiteUpTime = getChessArea().getWhiteTime()
					- getPartnersChessArea().getWhiteTime();

			if (whiteUpTime > 0) {
				getChessArea().setWhiteMarkText(
						"("
								+ StringUtility
										.millisToDurationInTenths(whiteUpTime)
								+ ")");
			} else {
				getPartnersChessArea().setWhiteMarkText(
						"("
								+ StringUtility
										.millisToDurationInTenths(whiteUpTime
												* -1) + ")");
			}

			long blackUpTime = getChessArea().getBlackTime()
					- getPartnersChessArea().getBlackTime();
			if (blackUpTime > 0) {
				getChessArea().setBlackMarkText(
						"("
								+ StringUtility
										.millisToDurationInTenths(blackUpTime)
								+ ")");
			} else {
				getPartnersChessArea().setBlackMarkText(
						"("
								+ StringUtility
										.millisToDurationInTenths(blackUpTime
												* -1) + ")");
			}

		} else if (!isBughouse()
				&& getPreferences().getBoardPreferences().isShowingSideUpTime()) {
			long timeUp = getChessArea().getWhiteTime()
					- getChessArea().getBlackTime();
			if (timeUp > 0) {
				getChessArea().setWhiteMarkText(
						"(" + StringUtility.millisToDurationInTenths(timeUp)
								+ ")");
			} else {
				getChessArea().setBlackMarkText(
						"("
								+ StringUtility.millisToDurationInTenths(timeUp
										* -1) + ")");
			}
		}
	}

	public int getBughouserDividerLocation() {
		return bughouseChessArea.getSplitPaneLocation();
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

	public static String getDescription(GameStartEvent event) {
		String result = "";
		String it = event.getG1Param("it");
		String r = event.getG1Param("r");

		if (it != null) {
			result += Integer.parseInt(it.split(",")[0]) / 60 + " "
					+ Integer.parseInt(it.split(",")[1]) + " "
					+ event.getGameDescription() + " "
					+ (r.equals("1") ? "r" : "u");
		} else {
			result = event.getGameDescription();
		}
		return result;
	}

	public void dispose() {

		if (LOGGER.isDebugEnabled()) {
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

				isDisposed = true;
			}
		}

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

	public void setDropPremove(String premove, int piece,
			int[] startCoordinates, int[] endCoordinates) {
		setPremove(premove, startCoordinates, endCoordinates);
		premoveDropPiece = piece;
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
				if (isBughouse()) {
					setTruePremove(premove, startCoordinates, endCoordinates);
				} else {
					setQeueuedPremove(premove, startCoordinates, endCoordinates);
				}
				break;
			}
			default: {
				throw new IllegalStateException("Invalid premove type: "
						+ preferences.getBoardPreferences().getPremoveType());
			}
			}
		}

	}

	public final void clearPremove() {
		synchronized (this) {

			try {
				if (isPremoveSet()) {
					getChessArea()
							.setStatusText(
									preferences.getBoardPreferences()
											.getPremoveType() == BoardPreferences.TRUE_PREMOVE ? " Cleared premove"
											: "Cleared premove queue: "
													+ getPremovesText());
					premoveDropPiece = Piece.EMPTY;
				}
				if (getCommandToolbar() != null) {
					getCommandToolbar().setClearPremoveEnabled(false);
				}
				queuedPremoves.clear();
				unselectAllSquares();
			} catch (Throwable t) {
				LOGGER.warn(t);
			}
		}
	}

	public boolean makePremove() {
		synchronized (this) {
			if (isPremoveSet()) {
				if (premoveDropPiece == Piece.EMPTY) {
					PremoveInfo premove = queuedPremoves.remove(0);
					chessArea.setStatusText("Sending premove: " + premove.move
							+ ". Moves queued " + queuedPremoves.size());
					EventService.getInstance().publish(
							new MoveRequestEvent(getGameId(), premove.move,
									true));
					return true;
				} else {
					// only make the premove if the user has the piece.
					int[] usersPieces = isUserWhite() ? getChessArea()
							.getWhiteDropPieces() : getChessArea()
							.getBlackDropPieces();

					for (int i = 0; i < usersPieces.length; i++) {
						if (usersPieces[i] == premoveDropPiece) {
							PremoveInfo premove = queuedPremoves.remove(0);
							chessArea.setStatusText("Sending premove: "
									+ premove.move + ". Moves queued "
									+ queuedPremoves.size());
							EventService.getInstance().publish(
									new MoveRequestEvent(getGameId(),
											premove.move, true));
							premoveDropPiece = Piece.EMPTY;
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	public void handleUserPositionUpdate(String move, Position position,
			long timeTaken, long whiteTime, long blackTime,
			int lagLastMoveInMillis, boolean areClocksTicking,
			String verboseNotation) {
		synchronized (this) {
			handleUserPositionUpdate(move, position, timeTaken, whiteTime,
					blackTime, null, null, lagLastMoveInMillis,
					areClocksTicking, verboseNotation);
		}
	}

	public void handleUserPositionUpdate(String move, Position position,
			long timeTaken, long whiteTime, long blackTime,
			int[] whiteDropPieces, int[] blackDropPieces,
			int lagLastMoveInMillis, boolean areClocksTicking,
			String verboseNotation) {
		synchronized (this) {
			LOGGER.debug("Handling user position update.");

			if (lastMoveMadeTime != -1) {
				lastMoveResponseTime = System.currentTimeMillis()
						- lastMoveMadeTime;
				lastMoveMadeTime = -1;
			}

			String queuedPremovesString = null;
			boolean wasPremoveMade = false;

			// A check to ignore refreshes/illegal moves
			MoveListModelMove lastMove = getChessArea().getMoveList()
					.getLastMove();
			
			boolean lastMoveWhite = getChessArea().getMoveList().isLastMoveWhite();
//			System.err.println("=================================");
//			System.err.println("lastMove: " + (lastMove == null ? null : lastMove.getAlgebraicDescription()));
//			System.err.println("currentMove: " + move);
//			System.err.println("lastMoveWhite: " + lastMoveWhite);
//			System.err.println("Position last move white: " + position.isWhitesMove());
//			System.err.println("=================================");
			
			if (	lastMove == null 
					|| 
					(!lastMove.equals("none") 
					&& 
					!(lastMove.getAlgebraicDescription().equals(move) && 
					  (lastMoveWhite != position.isWhitesMove())))) {
				getChessArea().getMoveList().appendMove(move, timeTaken,
						position);
			}

			lastPosition = position;
			lastWhiteTime = whiteTime;
			lastBlackTime = blackTime;

			if (getChessArea().getMoveList().isVisible()
					&& !getChessArea().getMoveList().isRealtimeUpdate()
					&& (isObserving() || isExamining())) {
			} else {
				// System.err.println("Premove list: " + queuedPremoves);
				if (isPlaying() && isPremoveSet()) {
					unselectLastCoordinates();
				} else {
					unselectAllSquares();
				}
				getChessArea().setWhiteTime(whiteTime);
				getChessArea().setBlackTime(blackTime);
				getChessArea().setPosition(position, areClocksTicking);
				setSideUpTime();

				if (isPlaying() && isUsersMove()) {
					// handle premove
					if (isPremoveSet()) {
						// These flags handle selecting the last move if it was
						// a premove on the board.
						// It is a bit hacky.
						wasPremoveMade = makePremove();
						lastMoveWasPremove = wasPremoveMade;

						if (isPremoveSet()) {
							queuedPremovesString = getPremovesText();
						}
					}
				}

				// If partners position move list is not on the last move update
				// its position as well.
				if (isBughouse()
						&& lastPartnersPosition != null
						&& getPartnersChessArea().getMoveList().getHalfMoves() != 0
						&& getPartnersChessArea().getMoveList()
								.getSelectedHalfMove() + 1 != getPartnersChessArea()
								.getMoveList().getHalfMoves()) {
					getPartnersChessArea().setWhiteTime(lastPartnersWhiteTime);
					getPartnersChessArea().setBlackTime(lastPartnersBlackTime);
					getPartnersChessArea().setPosition(lastPartnersPosition,
							areClocksTicking);
					isIgnoringBugMoveListEvents = true;
					getPartnersChessArea().getMoveList().selectLastMove();
					isIgnoringBugMoveListEvents = false;
				}

				// Dont select if its the users move that was made.
				if ((!isPlaying || lastMoveWasPremove || isUsersMove() || getPreferences().getBoardPreferences().isShowingMyMovesAsSelected())
						&& verboseNotation != null) {

					int[] startCoordinates = verboseNotationToStartCoordinates(
							verboseNotation, !isWhitesMove());
					int[] endCoordinates = verboseNotationToEndCoordinates(
							verboseNotation, !isWhitesMove());

					if (startCoordinates != null) {
						selectSquare(startCoordinates);
					}
					if (endCoordinates != null) {
						selectSquare(endCoordinates);
					}
					if (isPremoveSet()) {
						rememberLastCoordinates(startCoordinates, endCoordinates);
					} else {
						rememberLastCoordinates(null, null);
					}
				}
			}

			if (isShowingLag()) {
				if (isUsersMove() && lagLastMoveInMillis == 0
						&& lastMoveResponseTime > 0) {
					lagLastMoveInMillis = (int) lastMoveResponseTime;
				}

				if (isWhitesMove()) {
					getChessArea().addBlackLag(lagLastMoveInMillis);
				} else {
					getChessArea().addWhiteLag(lagLastMoveInMillis);
				}

			}

			if (move.equals("none")) // fics sends out a none for move if a
			{
				chessArea.setStatusText("Refreshed position from server. "
						+ (queuedPremovesString == null ? ""
								: " Queued premoves: " + queuedPremovesString));
			} else if (move != null && !move.equals("")) {
				chessArea.setStatusText("Last Move: "
						+ move
						+ " ("
						+ StringUtility.millisToDuration(timeTaken)
						+ ")"
						+ (queuedPremovesString == null ? ""
								: " Queued premoves: " + queuedPremovesString));

			}

			if (isPlaying() || isExamining()) {
				SoundManagerFactory.getInstance().playSound(SoundKeys.MOVE_KEY);
			} else if (isObserving()
					&& preferences.getBoardPreferences()
							.isPlayingMoveSoundOnObserving()) {
				SoundManagerFactory.getInstance().playSound(
						SoundKeys.OBS_MOVE_KEY);
			}
			
			if (isPlaying() && isUsersMove() && getPreferences().getSpeechPreferences().isSpeechEnabled() && getPreferences().getSpeechPreferences().isAnnouncingCheck())
			{
				 if (move.contains("+"))
				 {
				     SpeechManager.getInstance().getSpeech().speak("check");
				 }
				 else if (move.contains("#"))
				 {
					 SpeechManager.getInstance().getSpeech().speak("check mate");					 
				 }
			}

			if (!wasPremoveMade) {
				lastMoveWasPremove = false;
			}
		}
	}
	
	private void rememberLastCoordinates(int [] startCoordinates, int [] endCoordinates) {
		lastOppponentMoveStart = startCoordinates;
		lastOpponentMoveEnd = endCoordinates;
	}
	
	private void unselectLastCoordinates() {
		if (lastOppponentMoveStart != null) {
			unselectSquare(lastOppponentMoveStart);
		} 
		if (lastOpponentMoveEnd != null) {
			unselectSquare(lastOpponentMoveEnd);
		}
	}

	public void resetLastMoveMadeTime() {
		lastMoveMadeTime = -1;
	}

	public boolean isShowingLag() {
		return (isBughouse() && preferences.getBughousePreferences()
				.isShowingLag())
				|| (!isBughouse() && preferences.getBoardPreferences()
						.isShowingLag());
	}

	public boolean isUsersMove() {
		return (isPlaying && isWhitesMove() && isUserWhite())
				|| (isPlaying && !isWhitesMove() && !isUserWhite());
	}

	public boolean isUsersPartersMove() {
		return isPlaying
				&& (isPartnerWhite() && getPartnersPosition().isWhitesMove())
				|| (!isPartnerWhite() && !getPartnersPosition().isWhitesMove());
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

			if (isUsersMove() && isPremoveSet()
					&& premoveDropPiece != Piece.EMPTY) {
				makePremove();
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
			long timeTaken, long whiteTime, long blackTime,
			int lagLastMoveInMillis, boolean areClocksTicking,
			String verboseNotation) {
		synchronized (this) {
			handlePartnerPositionUpdate(move, position, timeTaken, whiteTime,
					blackTime, null, null, lagLastMoveInMillis,
					areClocksTicking, verboseNotation);
		}
	}

	public void handlePartnerPositionUpdate(String move, Position position,
			long timeTaken, long whiteTime, long blackTime,
			int[] whiteDropPieces, int[] blackDropPieces,
			int lagLastMoveInMillis, boolean areClocksTicking,
			String verboseNotation) {
		synchronized (this) {

			// A check to ignore refreshes/illegal moves
			MoveListModelMove lastMove = getPartnersChessArea().getMoveList()
					.getLastMove();
			if (lastMove == null
					|| (!lastMove.equals("none") && !lastMove
							.getAlgebraicDescription().equals(move))) {
				getPartnersChessArea().getMoveList().appendMove(move,
						timeTaken, position);
			}

			lastPartnersPosition = position;
			lastPartnersWhiteTime = whiteTime;
			lastPartnersBlackTime = blackTime;

			if (getChessArea().getMoveList().isVisible()
					&& !getChessArea().getMoveList().isRealtimeUpdate()
					&& (isObserving() || isExamining())) {
			} else {
				unselectAllPartnersBoardSquares();
				getPartnersChessArea().setWhiteTime(whiteTime);
				getPartnersChessArea().setBlackTime(blackTime);
				getPartnersChessArea().setPosition(position, areClocksTicking);
				setSideUpTime();

				// If partners position move list is not on the last move update
				// its position as well.
				if (isBughouse()
						&& lastPosition != null
						&& getChessArea().getMoveList().getHalfMoves() != 0
						&& getChessArea().getMoveList().getSelectedHalfMove() + 1 != getChessArea()
								.getMoveList().getHalfMoves()
						&& lastPosition != null) {
					getChessArea().setWhiteTime(lastWhiteTime);
					getChessArea().setBlackTime(lastBlackTime);
					getChessArea().setPosition(lastPosition, areClocksTicking);

					isIgnoringBugMoveListEvents = true;
					getChessArea().getMoveList().selectLastMove();
					isIgnoringBugMoveListEvents = false;
				}

				if (isShowingLag()) {
					if (isWhitesMoveOnPartnersBoard()) {
						getPartnersChessArea().addBlackLag(lagLastMoveInMillis);
					} else {
						getPartnersChessArea().addWhiteLag(lagLastMoveInMillis);
					}
				}
			}

			if (verboseNotation != null && !verboseNotation.equals("")) {
				getPartnersChessArea().setStatusText(
						"Last Move: " + move + " ("
								+ StringUtility.millisToDuration(timeTaken)
								+ ")");

				int[] startCoordinates = verboseNotationToStartCoordinates(
						verboseNotation, !isWhitesMove());
				int[] endCoordinates = verboseNotationToEndCoordinates(
						verboseNotation, !isWhitesMove());

				if (startCoordinates != null) {
					selectPartnersBoardSquare(startCoordinates);
				}
				if (endCoordinates != null) {
					selectPartnersBoardSquare(endCoordinates);
				}
			}

			if ((isBughouse() && !isObserving() && preferences
					.getBughousePreferences()
					.isPlayingMoveSoundOnPartnersBoard())
					|| (isBughouse() && isObserving() && preferences
							.getBoardPreferences()
							.isPlayingMoveSoundOnObserving())) {
				SoundManagerFactory.getInstance().playSound(
						SoundKeys.OBS_MOVE_KEY);
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
			lastMoveMadeTime = System.currentTimeMillis();
			EventService.getInstance().publish(
					new MoveRequestEvent(getGameId(), moveString, true));

			if (isPremoveSet() && isBughouse()) {
				clearPremove();
			}
		}
	}

	public void makeMove(Move move, Position newPosition) {
		synchronized (this) {
			getChessArea().setPosition(newPosition);
			String moveString = encodeMove(move);
			lastMoveMadeTime = System.currentTimeMillis();
			getChessArea().setStatusText("Sending move: " + moveString);
			EventService.getInstance().publish(
					new MoveRequestEvent(getGameId(), moveString, true));
			// No need to play sound since a style 12 move is sent and it is
			// played then.
			if (isPremoveSet() && isBughouse()) {
				clearPremove();
			}
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
		SoundManagerFactory.getInstance().playSound(SoundKeys.ILLEGAL_MOVE_KEY);
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
		SoundManagerFactory.getInstance().playSound(SoundKeys.GAME_START_KEY);
	}

	public void handleUserWon(String description) {
		synchronized (this) {
			if (isPlaying()) {
				SoundManagerFactory.getInstance().playSound(SoundKeys.WIN_KEY);
			} else {
				SoundManagerFactory.getInstance().playSound(
						SoundKeys.OBS_GAME_END_KEY);
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
				SoundManagerFactory.getInstance().playSound(SoundKeys.LOSE_KEY);
			} else {
				SoundManagerFactory.getInstance().playSound(
						SoundKeys.OBS_GAME_END_KEY);
			}
		}
	}

	public void handleUserDrew(String description) {
		synchronized (this) {
			setActive(false);
			setPlaying(false);
			chessArea.setStatusText(description);
			SoundManagerFactory.getInstance().playSound(
					SoundKeys.OBS_GAME_END_KEY);
		}

	}

	public void handleGameStopped(String description) {
		synchronized (this) {
			setActive(false);
			setPlaying(false);
			chessArea.setStatusText(description);
		}
	}

	public void showMoveList() {
		if (isBughouse()) {
			getChessArea().getMoveList().setVisible(true);
			getPartnersChessArea().getMoveList().setVisible(true);
			getChessArea().getMoveList().invalidate();
			getChessArea().getWhiteHoldings().invalidate();
			getChessArea().getBlackHoldings().invalidate();
			getPartnersChessArea().getMoveList().invalidate();
			getPartnersChessArea().getWhiteHoldings().invalidate();
			getPartnersChessArea().getBlackHoldings().invalidate();
			getChessArea().validate();
			getPartnersChessArea().validate();
		} else {
			getChessArea().getMoveList().setVisible(true);
			getChessArea().getMoveList().invalidate();
			getChessArea().getWhiteHoldings().invalidate();
			getChessArea().getBlackHoldings().invalidate();
			getChessArea().validate();
		}

		if (getCommandToolbar() != null) {
			getCommandToolbar().setButtonToHideMoveList();
		}
	}

	public void hideMoveList() {
		if (isBughouse()) {
			getChessArea().getMoveList().setVisible(false);
			getPartnersChessArea().getMoveList().setVisible(false);
			getChessArea().getWhiteHoldings().invalidate();
			getChessArea().getBlackHoldings().invalidate();
			getPartnersChessArea().getMoveList().invalidate();
			getPartnersChessArea().getWhiteHoldings().invalidate();
			getPartnersChessArea().getBlackHoldings().invalidate();
			getChessArea().validate();
			getPartnersChessArea().validate();

		} else {
			getChessArea().getMoveList().setVisible(false);
			getChessArea().getMoveList().invalidate();
			getChessArea().getWhiteHoldings().invalidate();
			getChessArea().getBlackHoldings().invalidate();
			getChessArea().validate();

		}

		if (getCommandToolbar() != null) {
			getCommandToolbar().setButtonToShowMoveList();
		}
	}

	public void processGameEnd(int gameEndState) {
		this.gameEndState = gameEndState;
		if (!isBughouse()) {
			getChessArea().getMoveList().setGameEnd(
					StorePGN.gameEndStateToResult(gameEndState));

			if (preferences.getLoggingPreferences().isLoggingGames()) {
				StorePGN.storePGN(this);
			}
		}
		// TO DO add in recording bgpn
		if (isBughouse
				&& !preferences.getBughousePreferences()
						.isShowingMoveListsOnPlayingGame() && isPlaying()) {
			showMoveList();
		}
	}

	public final boolean isActive() {
		return isActive;
	}

	public final void setActive(boolean param) {
		synchronized (this) {
			if (!param && isActive) {
				isActive = false;
				unsubscribe();

				if (isPlaying() && getCommandToolbar() != null) {
					Container container = getFrame().getContentPane();
					ChessAreaToolbar toolbar = getCommandToolbar();
					ChessAreaToolbar replacement = new ChessAreaToolbar(
							getPreferences(), this);
					container.remove(toolbar);
					toolbar.dispose();
					setCommandToolbar(replacement);
					GUIManager.getInstance().addKeyForwarder(replacement);
					container.add(replacement, BorderLayout.NORTH);
					replacement.invalidate();
					replacement.validate();
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

	public final ChessAreaFrame getFrame() {
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

	public int getInitialIncSecs() {
		return initialIncSecs;
	}

	public void setInitialIncSecs(int initialIncSecs) {
		this.initialIncSecs = initialIncSecs;
	}

	public int getInitialTimeSecs() {
		return initialTimeSecs;
	}

	public void setInitialTimeSecs(int initialTimeSecs) {
		this.initialTimeSecs = initialTimeSecs;
	}

	public void subscribePartner() {

		synchronized (this) {
			if (hasPartnerSubscribed) {
				LOGGER.error("Partner was already subscribed! unsubscribing.");
				unsubscribePartner();
			}

			hasPartnerSubscribed = true;

			if (LOGGER.isDebugEnabled()) {
				LOGGER
						.debug("A ChessAreaController is setting up partner subscriptions.");
			}

			if (isBughouse()) {
				Style12EventFilter partnersStyle12EventFilter = new Style12EventFilter(
						getPartnersGameId());
				partnersStyle12Subscriber = new Style12Subscriber(this, true);

				DroppableHoldingsChangedEventFilter partnersDroppableHoldingsChangedEventFilter = new DroppableHoldingsChangedEventFilter(
						getPartnersGameId());
				DroppableHoldingsChangedSubscriber partnersDroppableHoldingsChangedSubscriber = new DroppableHoldingsChangedSubscriber(
						this, true);

				subscribePartner(new Subscription(
						decaf.messaging.inboundevent.game.MoveEvent.class,
						partnersStyle12EventFilter, partnersStyle12Subscriber));
				subscribePartner(new Subscription(
						decaf.messaging.inboundevent.game.HoldingsChangedEvent.class,
						partnersDroppableHoldingsChangedEventFilter,
						partnersDroppableHoldingsChangedSubscriber));

				if (isPlaying()) {
					getPartnersChessArea().getBoard().setUserMoveInputListener(
							new BugSuggestUserMoveListener(this));
				}
				if (isObserving() || isExamining()) {
					subscribePartner(new Subscription(
							decaf.messaging.inboundevent.inform.MoveListEvent.class,
							new Filter() {
								public boolean apply(Event event) {
									MoveListEvent moveListEvent = (MoveListEvent) event;
									return moveListEvent.getGameId() == getPartnersGameId();
								}
							}, new PartnerMoveListSubscriber()));
				}
			}
		}
	}

	public Style12Subscriber getPartnersStyle12Subscriber() {
		return partnersStyle12Subscriber;
	}

	public void setPartnersStyle12Subscriber(
			Style12Subscriber partnersStyle12Subscriber) {
		this.partnersStyle12Subscriber = partnersStyle12Subscriber;
	}

	public Style12Subscriber getStyle12Subscriber() {
		return style12Subscriber;
	}

	public void setStyle12Subscriber(Style12Subscriber style12Subscriber) {
		this.style12Subscriber = style12Subscriber;
	}

	public void setObserving(boolean isObserving) {
		this.isObserving = isObserving;
	}

	public void setExamining(boolean isExamining) {
		this.isExamining = isExamining;
	}

	/**
	 * @param isValidating
	 *            The isValidating to set.
	 */
	public void setValidating(boolean isValidating) {
		this.isValidating = isValidating;
	}

	public void setUserWhite(boolean isUserWhite) {
		this.isUserWhite = isUserWhite;
	}

	public void setPartnerWhite(boolean isPartnerWhite) {
		this.isPartnerWhite = isPartnerWhite;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
		if (isPlaying) {
			getChessArea().setMoveable(true);
			if (isBughouse()) {
				getPartnersChessArea().setMoveable(true);
			}
		}
	}

	public void setFrame(ChessAreaFrame frame) {
		this.frame = frame;
	}

	public void setDroppable(boolean isDroppable) {
		this.isDroppable = isDroppable;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
		getChessArea().setBoardId("" + gameId);
	}

	public void setPartnersGameId(int partnersGameId) {
		this.partnersGameId = partnersGameId;
		getPartnersChessArea().setBoardId("" + gameId);
	}

	public void setBughouse(boolean isBughouse) {
		this.isBughouse = isBughouse;
	}

	public final BugChessArea getBughouseChessArea() {
		return bughouseChessArea;
	}

	public final void setBughouseChessArea(BugChessArea bughouseChessArea) {
		this.bughouseChessArea = bughouseChessArea;

	}

	public final ChessArea getChessArea() {
		return chessArea;
	}

	public final void setChessArea(ChessArea chessArea) {
		this.chessArea = chessArea;
	}

	public final ChessArea getPartnersChessArea() {
		return partnersChessArea;
	}

	public final void setPartnersChessArea(ChessArea partnersChessArea) {
		this.partnersChessArea = partnersChessArea;
	}

	public ChessAreaToolbar getCommandToolbar() {
		return commandToolbar;
	}

	public void setCommandToolbar(ChessAreaToolbar commandToolbar) {
		this.commandToolbar = commandToolbar;
	}

	/**
	 * Override to provide an implementation. Currently just unsubscribes to all
	 * events.
	 */
	public void unsubscribe() {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("A ChessAreaController is unsubscribing. "
					+ hasSubscribed + " " + hasPartnerSubscribed);
		}
		synchronized (this) {
			hasSubscribed = false;

			for (Subscription subscription : subscriptions) {
				EventService.getInstance().unsubscribe(subscription);
			}
			subscriptions.clear();

			lastPosition = null;
			lastWhiteTime = 0;
			lastBlackTime = 0;

			if (isBughouse()) {
				unsubscribePartner();
			}

		}
	}

	public void unsubscribePartner() {
		synchronized (this) {
			hasPartnerSubscribed = false;

			for (Subscription subscription : partnersSubscriptions) {
				EventService.getInstance().unsubscribe(subscription);
			}

			lastPartnersPosition = null;
			lastPartnersWhiteTime = 0;
			lastPartnersBlackTime = 0;
		}
	}

	/**
	 * All subscriptions should go through this method so unsubscribe will work
	 * properly.
	 * 
	 * @param subscription
	 */
	private void subscribe(Subscription subscription) {
		subscriptions.add(subscription);
		EventService.getInstance().subscribe(subscription);
	}

	private void subscribePartner(Subscription subscription) {
		partnersSubscriptions.add(subscription);
		EventService.getInstance().subscribe(subscription);
	}

	/**
	 * Sets up all event handling.
	 */
	public void subscribe() {
		synchronized (this) {
			if (hasSubscribed) {
				throw new IllegalStateException("Already subscribed");
			}
			hasSubscribed = true;
			if (LOGGER.isDebugEnabled()) {
				LOGGER
						.debug("A ChessAreaController is setting up subscriptions.");
			}

			if (isPlaying() && !isExamining()) {
				getChessArea().getBoard().setUserMoveInputListener(
						new ValidatingUserMoveListener(this, isValidating()));

				IllegalMoveSubscriber illegalMoveSubscriber = new IllegalMoveSubscriber(
						this);
				subscribe(new Subscription(
						decaf.messaging.inboundevent.game.IllegalMoveEvent.class,
						null, illegalMoveSubscriber));

			} else if (isExamining()) {
				LOGGER.debug("Adding examine game subscriber");
				getChessArea().getBoard().setUserMoveInputListener(
						new ValidatingUserMoveListener(this, isValidating(),
								true));

				IllegalMoveSubscriber illegalMoveSubscriber = new IllegalMoveSubscriber(
						this);
				subscribe(new Subscription(
						decaf.messaging.inboundevent.game.IllegalMoveEvent.class,
						null, illegalMoveSubscriber));
			}

			if (isObserving() || isExamining()) {
				subscribe(new Subscription(
						decaf.messaging.inboundevent.inform.MoveListEvent.class,
						new Filter() {
							public boolean apply(Event event) {
								MoveListEvent moveListEvent = (MoveListEvent) event;
								return moveListEvent.getGameId() == getGameId();
							}
						}, new MoveListSubscriber()));
			}

			GameEndEventFilter gameEndEventFilter = new GameEndEventFilter(
					getGameId());
			GameEndSubscriber gameEndSubscriber = new GameEndSubscriber(this);

			Style12EventFilter style12EventFilter = new Style12EventFilter(
					getGameId());
			style12Subscriber = new Style12Subscriber(this);

			subscribe(new Subscription(
					decaf.messaging.inboundevent.game.GameEndEvent.class,
					gameEndEventFilter, gameEndSubscriber));
			subscribe(new Subscription(
					decaf.messaging.inboundevent.game.MoveEvent.class,
					style12EventFilter, style12Subscriber));

			if (isDroppable()) {
				DroppableHoldingsChangedSubscriber droppableHoldingsChangedSubscriber = new DroppableHoldingsChangedSubscriber(
						this);
				DroppableHoldingsChangedEventFilter droppableHoldingsChangedEventFilter = new DroppableHoldingsChangedEventFilter(
						getGameId());
				subscribe(new Subscription(
						decaf.messaging.inboundevent.game.HoldingsChangedEvent.class,
						droppableHoldingsChangedEventFilter,
						droppableHoldingsChangedSubscriber));
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

			if (getCommandToolbar() != null) {
				getCommandToolbar().setClearPremoveEnabled(true);
			}

			unselectAllSquares();
			selectSquare(startCoordinates);
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
			if (getCommandToolbar() != null) {
				getCommandToolbar().setClearPremoveEnabled(true);
			}

			if (queuedPremoves.size() == 1) {
				unselectAllSquares();
				selectSquare(startCoordinates);
			} 
			
			preSelectSquare(endCoordinates, queuedPremoves.size());	
		} else {
			// LOGGER.warn("Null premove was encountered");
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
	
	private final void unselectSquare(int[] coordinates) {
		getChessArea().getBoard().unselectSquare(coordinates);
	}
	
	private final void preSelectSquare(int[] coordinates, int index) {
		getChessArea().getBoard().preSelectSquare(coordinates, index);
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

	private int[] verboseNotationToStartCoordinates(String move,
			boolean isWhitesMove) {
		if (move.equalsIgnoreCase("o-o") || move.equalsIgnoreCase("o-o-o")) {
			if (isWhitesMove) {
				return Coordinates.E1;
			} else {
				return Coordinates.E8;
			}
		} else {
			int atIndex = move.indexOf("@");

			if (atIndex == -1) {
				return CoordinatesUtil.algebraicToCoordinates(move.substring(2,
						4));
			} else {
				return null;
			}
		}
	}

	public int[] verboseNotationToEndCoordinates(String move,
			boolean isWhitesMove) {
		if (move.equalsIgnoreCase("o-o")) {
			if (isWhitesMove) {
				return Coordinates.G1;
			} else {
				return Coordinates.G8;
			}
		} else if (move.equalsIgnoreCase("o-o-o")) {
			if (isWhitesMove) {
				return Coordinates.C1;
			} else {
				return Coordinates.C8;
			}
		} else {
			int equalsIndex = move.indexOf("=");

			if (equalsIndex == -1) {
				return CoordinatesUtil.algebraicToCoordinates(move.substring(
						move.length() - 2, move.length()));
			} else {
				return CoordinatesUtil.algebraicToCoordinates(move.substring(
						equalsIndex - 2, equalsIndex));
			}
		}

	}

	public class MoveListSubscriber implements Subscriber {
		public void inform(MoveListEvent event) {
			getChessArea().getMoveList().setMoveList(event.getMoveList());
		}
	}

	public class PartnerMoveListSubscriber implements Subscriber {
		public void inform(MoveListEvent event) {
			getPartnersChessArea().getMoveList().setMoveList(
					event.getMoveList());
		}
	}

	protected class BugChessAreaMoveListListener implements MoveListListener {

		public void realtimeUpdateChanged(MoveList moveList,
				boolean isRealtimeUpdate) {
			synchronized (thisController) {
				if (isIgnoringBugMoveListEvents) {
					return;
				}

				// Ignore if playing.
				if (isRealtimeUpdate && isActive
						&& (isObserving || isExamining)) {
					getChessArea().setWhiteTime(lastWhiteTime);
					getChessArea().setBlackTime(lastBlackTime);
					getChessArea().setPosition(lastPosition, true);
					getChessArea().getMoveList().selectLastMove();

					getPartnersChessArea().setWhiteTime(lastPartnersWhiteTime);
					getPartnersChessArea().setWhiteTime(lastPartnersBlackTime);
					getPartnersChessArea().setPosition(lastPartnersPosition);
					getPartnersChessArea().getMoveList().selectLastMove();

					EventService.getInstance().publish(
							new OutboundEvent("refresh " + getGameId()));

					EventService.getInstance()
							.publish(
									new OutboundEvent("refresh "
											+ getPartnersGameId()));

				}

				// update the other movelists setting.
				if (moveList == getChessArea().getMoveList()) {
					isIgnoringBugMoveListEvents = true;
					getPartnersChessArea().getMoveList().setRealtimeUpdate(
							isRealtimeUpdate);
					isIgnoringBugMoveListEvents = false;
				} else {
					isIgnoringBugMoveListEvents = true;
					getChessArea().getMoveList().setRealtimeUpdate(
							isRealtimeUpdate);
					isIgnoringBugMoveListEvents = false;
				}
			}
		}

		public void moveClicked(MoveList moveList, int halfMoveNumber) {
			synchronized (thisController) {
				if (isIgnoringBugMoveListEvents) {
					return;
				}

				MoveList partnersMoveList = getPartnersChessArea()
						.getMoveList();
				MoveList userMoveList = getChessArea().getMoveList();
				boolean wasUserMoveListClicked = userMoveList == moveList;

				if (isPlaying() && !isExamining() && isActive()) {
					// tick the clocks
					if (wasUserMoveListClicked) {
						long elapsedTime = userMoveList.getBlackElapsedTime(
								halfMoveNumber, getInitialIncSecs())
								+ userMoveList.getWhiteElapsedTime(
										halfMoveNumber, getInitialIncSecs());

						int halfMove = partnersMoveList
								.getHalfMoveWithElapsedTime(elapsedTime,
										getInitialIncSecs());

						if (halfMove != -1) {
							getPartnersChessArea().setPosition(
									partnersMoveList.getMove(halfMove)
											.getPosition(), true);
							getChessArea().setPosition(
									userMoveList.getMove(halfMoveNumber)
											.getPosition(), true);
						}

					} else {
						long elapsedTime = partnersMoveList
								.getBlackElapsedTime(halfMoveNumber,
										getInitialIncSecs())
								+ partnersMoveList.getWhiteElapsedTime(
										halfMoveNumber, getInitialIncSecs());

						int halfMove = userMoveList.getHalfMoveWithElapsedTime(
								elapsedTime, getInitialIncSecs());

						if (halfMove != -1) {
							getChessArea().setPosition(
									userMoveList.getMove(halfMove)
											.getPosition(), true);
							getPartnersChessArea().setPosition(
									partnersMoveList.getMove(halfMoveNumber)
											.getPosition(), true);
						}

					}
				} else {

					if (getInitialTimeSecs() != 0) {
						if (wasUserMoveListClicked) {
							long whiteTimeBoard1 = moveList
									.getWhiteElapsedTime(halfMoveNumber,
											getInitialIncSecs());
							long elapsedTime = moveList.getBlackElapsedTime(
									halfMoveNumber, getInitialIncSecs())
									+ moveList
											.getWhiteElapsedTime(
													halfMoveNumber,
													getInitialIncSecs());

							int halfMove = partnersMoveList
									.getHalfMoveWithElapsedTime(elapsedTime,
											getInitialIncSecs());

							long whiteTimeBoard2 = partnersMoveList
									.getWhiteElapsedTime(halfMove,
											getInitialIncSecs());

							if (whiteTimeBoard1 < 0) {
								whiteTimeBoard1 = initialTimeSecs * 1000
										+ whiteTimeBoard1;
							} else {
								whiteTimeBoard1 = initialTimeSecs * 1000
										- whiteTimeBoard1;
							}
							if (whiteTimeBoard2 < 0) {
								whiteTimeBoard2 = initialTimeSecs * 1000
										+ whiteTimeBoard2;
							} else {
								whiteTimeBoard2 = initialTimeSecs * 1000
										- whiteTimeBoard2;
							}
							getChessArea().setWhiteTime(whiteTimeBoard1);
							getPartnersChessArea()
									.setWhiteTime(whiteTimeBoard2);

							long blackTimeBoard1 = userMoveList
									.getBlackElapsedTime(halfMoveNumber,
											getInitialIncSecs());
							long blackTimeBoard2 = partnersMoveList
									.getBlackElapsedTime(halfMove,
											getInitialIncSecs());

							if (blackTimeBoard1 < 0) {
								blackTimeBoard1 = initialTimeSecs * 1000
										+ blackTimeBoard1;
							} else {
								blackTimeBoard1 = initialTimeSecs * 1000
										- blackTimeBoard1;
							}
							if (blackTimeBoard2 < 0) {
								blackTimeBoard2 = initialTimeSecs * 1000
										+ blackTimeBoard2;
							} else {
								blackTimeBoard2 = initialTimeSecs * 1000
										- blackTimeBoard2;
							}
							getChessArea().setBlackTime(blackTimeBoard1);
							getPartnersChessArea()
									.setBlackTime(blackTimeBoard2);
						} else {
							long whiteTimeBoard2 = partnersMoveList
									.getWhiteElapsedTime(halfMoveNumber,
											getInitialIncSecs());
							long elapsedTime = partnersMoveList
									.getBlackElapsedTime(halfMoveNumber,
											getInitialIncSecs())
									+ partnersMoveList
											.getWhiteElapsedTime(
													halfMoveNumber,
													getInitialIncSecs());

							int halfMove = userMoveList
									.getHalfMoveWithElapsedTime(elapsedTime,
											getInitialIncSecs());

							long whiteTimeBoard1 = userMoveList
									.getWhiteElapsedTime(halfMove,
											getInitialIncSecs());

							if (whiteTimeBoard1 < 0) {
								whiteTimeBoard1 = initialTimeSecs * 1000
										+ whiteTimeBoard1;
							} else {
								whiteTimeBoard1 = initialTimeSecs * 1000
										- whiteTimeBoard1;
							}
							if (whiteTimeBoard2 < 0) {
								whiteTimeBoard2 = initialTimeSecs * 1000
										+ whiteTimeBoard2;
							} else {
								whiteTimeBoard2 = initialTimeSecs * 1000
										- whiteTimeBoard2;
							}
							getChessArea().setWhiteTime(whiteTimeBoard1);
							getPartnersChessArea()
									.setWhiteTime(whiteTimeBoard2);

							long blackTimeBoard1 = userMoveList
									.getBlackElapsedTime(halfMoveNumber,
											getInitialIncSecs());
							long blackTimeBoard2 = partnersMoveList
									.getBlackElapsedTime(halfMove,
											getInitialIncSecs());

							if (blackTimeBoard1 < 0) {
								blackTimeBoard1 = initialTimeSecs * 1000
										+ blackTimeBoard1;
							} else {
								blackTimeBoard1 = initialTimeSecs * 1000
										- blackTimeBoard1;
							}
							if (blackTimeBoard2 < 0) {
								blackTimeBoard2 = initialTimeSecs * 1000
										+ blackTimeBoard2;
							} else {
								blackTimeBoard2 = initialTimeSecs * 1000
										- blackTimeBoard2;
							}
							getChessArea().setBlackTime(blackTimeBoard1);
							getPartnersChessArea()
									.setBlackTime(whiteTimeBoard2);
						}
					}

					if (wasUserMoveListClicked) {
						long elapsedTime = userMoveList.getBlackElapsedTime(
								halfMoveNumber, getInitialIncSecs())
								+ userMoveList.getWhiteElapsedTime(
										halfMoveNumber, getInitialIncSecs());

						int halfMove = partnersMoveList
								.getHalfMoveWithElapsedTime(elapsedTime,
										getInitialIncSecs());
						if (halfMove != -1) {
							getPartnersChessArea().setPosition(
									partnersMoveList.getMove(halfMove)
											.getPosition(), false);
							getChessArea().setPosition(
									userMoveList.getMove(halfMoveNumber)
											.getPosition(), false);
							isIgnoringBugMoveListEvents = true;
							getPartnersChessArea().getMoveList().selectMove(
									halfMove);
							isIgnoringBugMoveListEvents = false;
						}

					} else {
						long elapsedTime = partnersMoveList
								.getBlackElapsedTime(halfMoveNumber,
										getInitialIncSecs())
								+ partnersMoveList.getWhiteElapsedTime(
										halfMoveNumber, getInitialIncSecs());

						int halfMove = userMoveList.getHalfMoveWithElapsedTime(
								elapsedTime, getInitialIncSecs());

						if (halfMove != -1) {
							getChessArea().setPosition(
									userMoveList.getMove(halfMove)
											.getPosition(), false);
							getPartnersChessArea().setPosition(
									partnersMoveList.getMove(halfMoveNumber)
											.getPosition(), false);
							isIgnoringBugMoveListEvents = true;
							getChessArea().getMoveList().selectMove(halfMove);
							isIgnoringBugMoveListEvents = false;
						}
					}
					setSideUpTime();
				}
			}
			unselectAllSquares();
			unselectAllPartnersBoardSquares();
		}
	}

	protected class ChessAreaMoveListListener implements MoveListListener {

		public void realtimeUpdateChanged(MoveList moveList,
				boolean isAutoScrolling) {
			synchronized (thisController) {
				if (moveList == getChessArea().getMoveList()) {
					// Ignore if playing.
					if (isAutoScrolling && isActive
							&& (isObserving || isExamining)) {
						getChessArea().setWhiteTime(lastWhiteTime);
						getChessArea().setBlackTime(lastBlackTime);
						getChessArea().setPosition(lastPosition, true);

						getChessArea().getMoveList().selectLastMove();
						EventService.getInstance().publish(
								new OutboundEvent("refresh " + getGameId()));
					}
				}
			}
		}

		public void moveClicked(MoveList moveList, int halfMoveNumber) {
			synchronized (thisController) {
				if (moveList == getChessArea().getMoveList()) {
					if (isPlaying() && !isExamining() && isActive()) {
						// tick the clocks
						getChessArea().setPosition(
								moveList.getMove(halfMoveNumber).getPosition(),
								true);
					} else {
						if (getInitialTimeSecs() != 0) {
							long whiteTime = moveList.getWhiteElapsedTime(
									halfMoveNumber, getInitialIncSecs());

							if (whiteTime < 0) {
								whiteTime = initialTimeSecs * 1000 + whiteTime;
							} else {
								whiteTime = initialTimeSecs * 1000 - whiteTime;
							}
							getChessArea().setWhiteTime(whiteTime);

							long blackTime = moveList.getBlackElapsedTime(
									halfMoveNumber, getInitialIncSecs());

							if (blackTime < 0) {
								blackTime = initialTimeSecs * 1000 + blackTime;
							} else {
								blackTime = initialTimeSecs * 1000 - blackTime;
							}
							getChessArea().setBlackTime(blackTime);
						}
						getChessArea().setPosition(
								moveList.getMove(halfMoveNumber).getPosition(),
								false);
						unselectAllSquares();
						setSideUpTime();
					}
				}
			}
		}
	}

	private class PremoveInfo {
		public int[] endCoordinates;

		public String move;
	}
}