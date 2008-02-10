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
package decaf.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import org.apache.log4j.Logger;

import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.widgets.chessarealayout.ChessAreaLayout;
import decaf.gui.widgets.holdings.HoldingsPanelBase;
import decaf.gui.widgets.movelist.MoveList;
import decaf.moveengine.Position;
import decaf.util.StringUtility;
import decaf.util.TextProperties;

/**
 * This class contains no thread synchronization, its up to the caller to ensure
 * thread saftey.
 */
public class ChessArea extends JPanel implements Preferenceable, Disposable {

	public static final int MARK_NEITHER_SIDE = 0;

	public static final int MARK_WHITE_SIDE = 1;

	public static final int MARK_BLACK_SIDE = 2;

	private static final Logger LOGGER = Logger.getLogger(ChessArea.class);

	private static final int RED_LAG = 15000;

	private boolean isWhiteOnTop = true;

	private boolean isDroppable;

	private boolean isTimed;

	private boolean isActive;

	private ChessBoard board;

	private JLabel markWhiteLabel;

	private JLabel markBlackLabel;

	private JLabel whiteNameLbl;

	private JLabel blackNameLbl;

	private JLabel whiteLagLbl;

	private JLabel blackLagLbl;

	private JTextField statusField;

	private ChessClock whitesClock;

	private ChessClock blacksClock;

	private Preferences preferences;

	private Position position;

	private int initialTimeInSeconds;

	private int increment;

	private int totalWhiteLag;

	private int totalBlackLag;

	private String boardId;

	private String whitesName;

	private String whitesRating;

	private String blacksName;

	private String blacksRating;

	private HoldingsPanelBase whiteHoldings;

	private HoldingsPanelBase blackHoldings;

	private MoveList moveList;

	private boolean isShowingLag = true;

	private ChessAreaLayout layout;

	/**
	 * Creates a board. The board will not be useful until setup is invoked.
	 * Creating a board this way allows for ChessArea recycling.
	 */
	public ChessArea() {

		board = new ChessBoard();
		moveList = new MoveList();

		markWhiteLabel = new JLabel("");
		markBlackLabel = new JLabel("");

		whiteNameLbl = new JLabel("");
		blackNameLbl = new JLabel("");
		whiteLagLbl = new JLabel("");
		blackLagLbl = new JLabel("");

		whitesClock = new ChessClock(initialTimeInSeconds, increment);
		whitesClock.setWhiteClock(true);

		blacksClock = new ChessClock(initialTimeInSeconds, increment);
		blacksClock.setWhiteClock(false);

		statusField = new JTextField();
		statusField.setEditable(false);
	}

	public boolean isWhiteOnTop() {
		return isWhiteOnTop;
	}

	public JLabel getBlackLagLbl() {
		return blackLagLbl;
	}

	public JLabel getBlackNameLbl() {
		return blackNameLbl;
	}

	public ChessClock getBlacksClock() {
		return blacksClock;
	}

	public String getBlacksName() {
		return blacksName;
	}

	public JLabel getMarkBlackLabel() {
		return markBlackLabel;
	}

	public JLabel getMarkWhiteLabel() {
		return markWhiteLabel;
	}

	public JTextField getStatusField() {
		return statusField;
	}

	public JLabel getWhiteLagLbl() {
		return whiteLagLbl;
	}

	public JLabel getWhiteNameLbl() {
		return whiteNameLbl;
	}

	public ChessClock getWhitesClock() {
		return whitesClock;
	}

	public String getWhitesName() {
		return whitesName;
	}

	public void setWhiteMarkText(String text) {
		markWhiteLabel.setText(text);
		/*
		 * String blackText = new String(); for (int i = 0; i < text.length();
		 * i++) { blackText = " "; }
		 */
		markBlackLabel.setText("");
		markBlackLabel.invalidate();
		markWhiteLabel.invalidate();
		validate();

	}

	public void setBlackMarkText(String text) {
		markBlackLabel.setText(text);
		/*
		 * String whiteText = new String(); for (int i = 0; i < text.length();
		 * i++) { whiteText = " "; }
		 */
		markWhiteLabel.setText("");

		markBlackLabel.invalidate();
		markWhiteLabel.invalidate();
		validate();
	}

	public void clearMarkText() {
		markBlackLabel.setText("");
		markWhiteLabel.setText("");
	}

	public HoldingsPanelBase getBlackHoldings() {
		return blackHoldings;
	}

	public void setBlackHoldings(HoldingsPanelBase blackHoldings) {
		this.blackHoldings = blackHoldings;
		blackHoldings.setPreferences(getPreferences());
	}

	public HoldingsPanelBase getWhiteHoldings() {
		return whiteHoldings;
	}

	public void setWhiteHoldings(HoldingsPanelBase whiteHoldings) {
		this.whiteHoldings = whiteHoldings;
		whiteHoldings.setPreferences(getPreferences());
	}

	public void dispose() {
		synchronized (this) {
			if (board != null) {
				board.dispose();
			}
			if (whitesClock != null) {
				whitesClock.dispose();
			}
			if (blacksClock != null) {
				blacksClock.dispose();
			}
			if (layout != null) {
				layout.dispose();
			}
			removeAll();

			moveList.removeAllMoveListListeners();
		}
	}

	public boolean isMoveable() {
		return board.isMoveable();
	}

	public void setMoveable(boolean isMoveable) {
		synchronized (this) {
			whiteHoldings.setMoveable(isMoveable);
			blackHoldings.setMoveable(isMoveable);
			board.setMoveable(isMoveable);
		}
	}

	public void setSpeakingCountdown(boolean isUserWhite) {
		if (isUserWhite) {
			whitesClock.setSpeakingCountdown(true);
		} else {
			blacksClock.setSpeakingCountdown(true);
		}
	}

	public void removeStatusBar() {
		remove(statusField);
	}

	public void addStatusBar() {
		remove(statusField);
		add(statusField, BorderLayout.SOUTH);
	}

	public void recycle() {
		whitesClock.stop();
		blacksClock.stop();
		moveList.clear();
	}

	public void setShowingLag(boolean isShowingLag) {
		this.isShowingLag = isShowingLag;
	}

	public boolean isShowingLag() {
		return isShowingLag;
	}

	public void setup(String boardId, String whitesName, String whitesRating,
			String blacksName, String blacksRating, boolean isDroppable,
			boolean isWhiteOnTop, Position position, boolean isShowingLag) {

		setup(boardId, whitesName, whitesRating, blacksName, blacksRating,
				isDroppable, isWhiteOnTop, -1, -1, position, isShowingLag);
	}

	public void setup(String boardId, String whitesName, String whitesRating,
			String blacksName, String blacksRating, boolean isDroppable,
			boolean isWhiteOnTop, int initialTimeInSeconds,
			int initialIncInSeconds, Position position, boolean isShowingLag) {
		synchronized (this) {
			long startTime = System.currentTimeMillis();

			statusField.setText("");

			this.position = position;
			this.initialTimeInSeconds = initialTimeInSeconds;
			this.increment = initialIncInSeconds;
			this.isDroppable = isDroppable;
			this.isTimed = initialTimeInSeconds > 0 || initialIncInSeconds > 0;
			this.isActive = true;
			this.totalWhiteLag = 0;
			this.totalBlackLag = 0;
			this.isWhiteOnTop = isWhiteOnTop;

			whiteLagLbl.setForeground(getPreferences().getBoardPreferences()
					.getControlLabelTextProperties().getForeground());
			blackLagLbl.setForeground(getPreferences().getBoardPreferences()
					.getControlLabelTextProperties().getForeground());
			whiteLagLbl.setText("");
			blackLagLbl.setText("");
			clearMarkText();

			setWhiteInfo(whitesName, whitesRating);
			setBlackInfo(blacksName, blacksRating);

			whitesClock.setSpeakingCountdown(false);
			blacksClock.setSpeakingCountdown(false);

			setShowingLag(isShowingLag);
			updateWhiteLag();
			updateBlackLag();

			setBoardId(boardId);
			board.unselectAllSquares();
			board.setWhiteOnTop(isWhiteOnTop);

			if (getWhiteHoldings() != null)
			{
				//clear out the holdings if they previously contained pieces.
				getWhiteHoldings().setFromPieceArray(new int[0]);
				getBlackHoldings().setFromPieceArray(new int[0]);
			}

			setupLayout();


			// if game is droppable white pieces show up in white drop panel if
			// its not its the black piece jail.
			// This code sets that up without having to lay everything out
			// again.
			if (isDroppable()) {
				getBlackHoldings().setVisible(true);
				getWhiteHoldings().setVisible(true);
				getWhiteHoldings().invalidate();
				getBlackHoldings().invalidate();

				getWhiteHoldings().setRepresentsLightPieces(true);
				getBlackHoldings().setRepresentsLightPieces(false);
			} else {
				getWhiteHoldings().setRepresentsLightPieces(false);
				getBlackHoldings().setRepresentsLightPieces(true);

				if (!getPreferences().getBoardPreferences()
						.isShowingPieceJail()) {
					getBlackHoldings().setVisible(false);
					getWhiteHoldings().setVisible(false);
				} else {
					getBlackHoldings().setVisible(true);
					getWhiteHoldings().setVisible(true);

					getBlackHoldings().invalidate();
					getWhiteHoldings().invalidate();
				}
			}

			moveList.clear();
			moveList.setRealtimeUpdate(true);

			board.invalidate();
			whiteLagLbl.invalidate();
			blackLagLbl.invalidate();
			whiteNameLbl.invalidate();
			blackNameLbl.invalidate();
			whitesClock.invalidate();
			blacksClock.invalidate();

			setPosition(position, true);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Time to setup ChessArea: "
						+ (System.currentTimeMillis() - startTime));
			}
		}
	}

	public MoveList getMoveList() {
		return moveList;
	}

	public void setBoardId(String boardId) {
		synchronized (this) {
			this.boardId = boardId;
			whiteHoldings.setBoardId(boardId);
			blackHoldings.setBoardId(boardId);
			whitesClock.setBoardId(boardId);
			blacksClock.setBoardId(boardId);
			board.setBoardId(boardId);
		}
	}

	private void showTooltip(JComponent compoennt, String text) {
		PopupFactory popupFactory = PopupFactory.getSharedInstance();
		JLabel label = new JLabel(text);

		final Popup popup = popupFactory.getPopup(compoennt, label, compoennt
				.getLocationOnScreen().x + 2,
				compoennt.getLocationOnScreen().y + 2);
		label.setForeground(Color.BLACK);
		label.setBackground(Color.YELLOW);

		popup.show();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				popup.hide();
			}
		}, 2000);
	}

	public void showWhiteCaption(String caption) {

		showTooltip(whiteNameLbl, caption);

	}

	public void showBlackCaption(String caption) {

		showTooltip(blackNameLbl, caption);

	}

	public String getBoardId() {
		return boardId;
	}

	public Preferences getpreferences() {
		return preferences;
	}

	public void setStatusText(String text) {
		statusField.setText(text);
	}

	public String getStatusText() {
		return statusField.getText();
	}

	public void setShowTenthsWhenTimeIsLessThanSeconds(int value) {
		if (isTimed) {
			synchronized (this) {
				whitesClock.setShowTenthsWhenTimeIsLessThanSeconds(value);
				blacksClock.setShowTenthsWhenTimeIsLessThanSeconds(value);
			}
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		synchronized (this) {
			long startTime = System.currentTimeMillis();
			this.preferences = preferences;
			TextProperties controlLabelTextProperties = preferences
					.getBoardPreferences().getControlLabelTextProperties();
			TextProperties statusTextProperties = preferences
					.getBoardPreferences().getStatusBarTextProperties();
			Color controlBackground = preferences.getBoardPreferences()
					.getBackgroundControlsColor();

			setBackground(controlBackground);
			board.setBackground(controlBackground);

			statusField.setBackground(controlLabelTextProperties
					.getBackground());
			blackNameLbl.setBackground(controlLabelTextProperties
					.getBackground());
			whiteNameLbl.setBackground(controlLabelTextProperties
					.getBackground());
			blackLagLbl.setBackground(controlLabelTextProperties
					.getBackground());
			whiteLagLbl.setBackground(controlLabelTextProperties
					.getBackground());
			statusField.setBackground(statusTextProperties.getBackground());
			markBlackLabel.setBackground(controlLabelTextProperties
					.getBackground());
			markWhiteLabel.setBackground(controlLabelTextProperties
					.getBackground());

			blackNameLbl.setForeground(controlLabelTextProperties
					.getForeground());
			whiteNameLbl.setForeground(controlLabelTextProperties
					.getForeground());
			blackLagLbl.setForeground(controlLabelTextProperties
					.getForeground());
			whiteLagLbl.setForeground(controlLabelTextProperties
					.getForeground());
			statusField.setForeground(statusTextProperties.getForeground());
			markBlackLabel.setForeground(preferences.getBoardPreferences()
					.getMarkTextProperties().getForeground());
			markWhiteLabel.setForeground(preferences.getBoardPreferences()
					.getMarkTextProperties().getForeground());

			blackNameLbl.setFont(controlLabelTextProperties.getFont());
			whiteNameLbl.setFont(controlLabelTextProperties.getFont());
			blackLagLbl.setFont(controlLabelTextProperties.getFont());
			whiteLagLbl.setFont(controlLabelTextProperties.getFont());
			statusField.setFont(statusTextProperties.getFont());
			markWhiteLabel.setFont(preferences.getBoardPreferences()
					.getMarkTextProperties().getFont());
			markBlackLabel.setFont(preferences.getBoardPreferences()
					.getMarkTextProperties().getFont());

			board.setPreferences(preferences);

			whitesClock.setPreferences(preferences);
			blacksClock.setPreferences(preferences);

			setupLayout();

			whiteHoldings.setPreferences(preferences);
			blackHoldings.setPreferences(preferences);

			if (!isDroppable()) {
				if (!getPreferences().getBoardPreferences()
						.isShowingPieceJail()) {
					getBlackHoldings().setVisible(false);
					getWhiteHoldings().setVisible(false);
					getBlackHoldings().invalidate();
					getWhiteHoldings().invalidate();
				} else {
					getBlackHoldings().setVisible(true);
					getWhiteHoldings().setVisible(true);
					getBlackHoldings().invalidate();
					getWhiteHoldings().invalidate();
				}
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Set prefs on chess area in "
						+ (System.currentTimeMillis() - startTime));
			}

			moveList.setPreferences(preferences);
			validate();
		}
	}

	public String getWhiteName() {
		return whitesName;
	}

	public String getBlackName() {
		return blacksName;
	}

	public String getWhitesRating() {
		return whitesRating;
	}

	public String getBlacksRating() {
		return blacksRating;
	}

	public void setWhiteInfo(String whitesName, String whitesRating) {

		this.whitesName = whitesName;
		this.whitesRating = whitesRating;
		String infoString = whitesName + "(" + whitesRating + ")";
		whiteNameLbl.setText(infoString);

		if (layout != null) {
			layout.adjustForLabelChanges();
		}

	}

	public void setBlackInfo(String blacksName, String blacksRating) {

		this.blacksName = blacksName;
		this.blacksRating = blacksRating;
		String infoString = blacksName + "(" + blacksRating + ")";
		blackNameLbl.setText(infoString);

		if (layout != null) {
			layout.adjustForLabelChanges();
		}
	}

	public void setPosition(Position position) {
		setPosition(position, true);
	}

	public void setPosition(Position position, boolean areClocksTicking) {
		synchronized (this) {
			this.position = position;

			if (position == null) {
				throw new IllegalArgumentException("Position cant be null");
			}
			board.setPosition(position);
			if (isDroppable()) {
				if (position.getWhiteHoldings() == null) {
					setWhiteDropPieces(new int[0]);
				} else {
					setWhiteDropPieces(position.getWhiteHoldings());
				}

				if (position.getBlackHoldings() == null) {
					setBlackDropPieces(new int[0]);
				} else {
					setBlackDropPieces(position.getBlackHoldings());
				}

			} else if (preferences.getBoardPreferences().isShowingPieceJail()) {
				setWhiteDropPieces(position.getBlackCapturedPieces());
				setBlackDropPieces(position.getWhiteCapturedPieces());

			}
			if (areClocksTicking && isActive) {
				startOrStopClocks();
			} else {
				startOrStopClocksWithoutTicking();
			}
		}
	}

	public Position getPosition() {
		return position;
	}

	public void setActive(boolean isActive) {
		synchronized (this) {
			this.isActive = isActive;
			if (!isActive) {
				setMoveable(false);
				stopClocks();
			}
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void startOrStopClocksWithoutTicking() {
		synchronized (this) {
			blacksClock.stop();
			whitesClock.stop();
			if (isWhitesMove()) {
				whitesClock.startWithoutTicking();
			} else {
				blacksClock.startWithoutTicking();
			}
		}
	}

	public void setWhiteTime(long timeInSeconds) {
		whitesClock.set(timeInSeconds);
	}

	public void setBlackTime(long timeInSeconds) {
		blacksClock.set(timeInSeconds);
	}

	public void setWhiteDropPieces(int dropPieces[]) {
		if (dropPieces != null) {
			synchronized (this) {
				whiteHoldings.setFromPieceArray(dropPieces);
				whiteHoldings.invalidate();
				whiteHoldings.validate();

				if (isDroppable()) {
					getPosition().setWhiteHoldings(dropPieces);
				}
			}
		}
	}

	public void addBlackLag(int lagInMillis) {
		totalBlackLag += lagInMillis;
		updateBlackLag();
	}

	public void addWhiteLag(int lagInMillis) {
		totalWhiteLag += lagInMillis;
		updateWhiteLag();
	}

	public void substractWhiteLag(int lagInMillis) {
		totalWhiteLag -= lagInMillis;
		if (totalWhiteLag < 0) {
			totalBlackLag = 0;
		}

		updateWhiteLag();
	}

	public void substractBlackLag(int lagInMillis) {
		totalBlackLag -= lagInMillis;

		if (totalBlackLag < 0) {
			totalBlackLag = 0;
		}
		updateBlackLag();
	}

	public int[] getWhiteDropPieces() {
		if (isDroppable)
			return whiteHoldings.getPieceArray();
		else
			return new int[0];
	}

	public void setBlackDropPieces(int dropPieces[]) {
		if (dropPieces != null) {
			synchronized (this) {
				blackHoldings.setFromPieceArray(dropPieces);
				blackHoldings.invalidate();
				blackHoldings.validate();

				if (isDroppable()) {
					getPosition().setBlackHoldings(dropPieces);
				}
			}
		}
	}

	public int[] getBlackDropPieces() {
		if (isDroppable)
			return blackHoldings.getPieceArray();
		else
			return new int[0];
	}

	public void flip() {

		synchronized (this) {
			isWhiteOnTop = !isWhiteOnTop;
			board.flip();
			board.invalidate();
			setupLayout();
			validate();
		}
	}

	public boolean isDroppable() {
		return isDroppable;
	}

	public boolean isWhitesMove() {
		return position.isWhitesMove();
	}

	/**
	 * @return Returns the board.
	 */
	public ChessBoard getBoard() {
		return board;
	}

	public long getWhiteTime() {
		return whitesClock.getCurrentMilliseconds();
	}

	public long getBlackTime() {
		return blacksClock.getCurrentMilliseconds();
	}

	private void setupLayout() {
		if (layout != null) {
			layout.dispose();
			removeAll();
		}
		try {
			layout = (ChessAreaLayout) Class.forName(
					preferences.getBoardPreferences().getLayoutClassName())
					.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		layout.init(this);
	}

	private void updateBlackLag() {
		if (isShowingLag()) {
			BigDecimal bigDecimal = new BigDecimal(
					(double) totalBlackLag / 1000.0);
			bigDecimal = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_DOWN);

			LOGGER.debug("Setting black lag to " + bigDecimal);

			String blackLabel = StringUtility.padCharsToLeft(bigDecimal
					.toString(), ' ', 4);

			if (totalBlackLag > RED_LAG) {
				blackLagLbl.setForeground(Color.RED.brighter());
			}

			blackLagLbl.setText(blackLabel);
			blackLagLbl.repaint();
		} else {
			blackLagLbl.setText("");
			blackLagLbl.repaint();
		}
	}

	private void updateWhiteLag() {
		if (isShowingLag()) {
			BigDecimal bigDecimal = new BigDecimal(
					(double) totalWhiteLag / 1000.0);
			bigDecimal = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_DOWN);

			LOGGER.debug("Setting white lag to " + bigDecimal);

			String whiteLabel = StringUtility.padCharsToLeft(bigDecimal
					.toString(), ' ', 4);

			if (totalWhiteLag > RED_LAG) {
				whiteLagLbl.setForeground(Color.RED.brighter());
			}

			whiteLagLbl.setText(whiteLabel);
			whiteLagLbl.repaint();
		} else {
			whiteLagLbl.setText("");
			whiteLagLbl.repaint();
		}
	}

	private void stopClocks() {
		synchronized (this) {
			blacksClock.stop();
			whitesClock.stop();
		}
	}

	private void startOrStopClocks() {
		synchronized (this) {
			if (isWhitesMove()) {
				blacksClock.stop();
				whitesClock.start();
			} else {
				whitesClock.stop();
				blacksClock.start();
			}
		}
	}

}