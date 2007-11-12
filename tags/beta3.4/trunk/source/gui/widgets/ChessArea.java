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
package decaf.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentListener;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import decaf.gui.event.ChessClockListener;
import decaf.gui.event.UserActionListener;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.StringUtility;
import decaf.gui.util.TextProperties;
import decaf.moveengine.Position;

/**
 * This class contains no thread synchronization, its up to the caller to ensure
 * thread saftey.
 */
public class ChessArea extends JPanel implements Preferenceable {

	private static final Logger LOGGER = Logger.getLogger(ChessArea.class);

	private static final int MAX_NAME_LENGTH = 24;

	private boolean isWhiteOnTop;

	private boolean isDroppable;

	private boolean isTimed;

	private boolean isActive;

	private ChessBoard board;

	private JPanel boardAndDropPiecesPanel;

	private JPanel whiteControlsPanel;

	private JPanel blackControlsPanel;

	private JPanel mainPanel;

	private JPanel northBoardFillerPanel;

	private JPanel southBoardFillerPanel;

	private JPanel dropPiecePanels;

	private JPanel eastBoardFillerPanel;

	private JPanel westBoardFillerPanel;

	private JLabel whiteNameLbl;

	private JLabel blackNameLbl;

	private JLabel whiteLagLbl;

	private JLabel blackLagLbl;

	private JTextField statusField;

	private PanelChessClock whitesClock;

	private PanelChessClock blacksClock;

	private Preferences preferences;

	private Position position;

	private int initialTimeInSeconds;

	private int increment;

	private int totalWhiteLag;

	private int totalBlackLag;

	private String boardId;

	private String whitesName;

	private String blacksName;


	private HorizontalHoldingsPanel horizontalWhiteHoldings;

	private HorizontalHoldingsPanel horizontalBlackHoldings;

	private VerticalHoldingsPanel verticalWhiteHoldings;

	private VerticalHoldingsPanel verticalBlackHoldings;
	
	private LeftRightHoldingsPanel whiteLeftRightHoldings;
	
	private LeftRightHoldingsPanel blackLeftRightHoldings;

	private SquareSandwichLayout boardDropPieceLayout;
	
	private List<UserActionListener> userActionListeners = new LinkedList<UserActionListener>();
	private List <ChessClockListener> chessClockListeners = new LinkedList<ChessClockListener>();

	public void dispose() {
		synchronized(this)
		{
			removeAllListeners();
			if (board != null) {
				board.dispose();
			}
			if (whiteLeftRightHoldings != null)
			{
				whiteLeftRightHoldings.dispose();
			}
			if (blackLeftRightHoldings != null)
			{
				blackLeftRightHoldings.dispose();
			}
			if (horizontalWhiteHoldings != null) {
				horizontalWhiteHoldings.dispose();
			}
			if (horizontalBlackHoldings != null) {
				horizontalBlackHoldings.dispose();
			}
	
			if (verticalWhiteHoldings != null) {
				verticalWhiteHoldings.dispose();
			}
			if (verticalBlackHoldings != null) {
				verticalBlackHoldings.dispose();
			}
	
			if (whitesClock != null) {
				whitesClock.dispose();
			}
			if (blacksClock != null) {
				blacksClock.dispose();
			}
			boardAndDropPiecesPanel = null;
			whiteControlsPanel = null;
			blackControlsPanel = null;
			mainPanel = null;
			northBoardFillerPanel = null;
			southBoardFillerPanel = null;
			dropPiecePanels = null;
			westBoardFillerPanel = null;
			whiteNameLbl = null;
			blackNameLbl = null;
			statusField = null;
			boardId = null;
			whitesName = null;
			blacksName = null;
			position = null;
		}
	}

	/**
	 * Creates a board. The board will not be useful until setup is invoked.
	 * Creating a board this way allows for ChessArea recycling.
	 */
	public ChessArea() {
		long startTime = System.currentTimeMillis();
		board = new ChessBoard();

		horizontalWhiteHoldings = new HorizontalHoldingsPanel(true);
		horizontalBlackHoldings = new HorizontalHoldingsPanel(false);

		verticalWhiteHoldings = new VerticalHoldingsPanel(true);
		verticalBlackHoldings = new VerticalHoldingsPanel(false);
		
		whiteLeftRightHoldings = new LeftRightHoldingsPanel(true);
		blackLeftRightHoldings = new LeftRightHoldingsPanel(false);

		blackControlsPanel = new JPanel();
		whiteControlsPanel = new JPanel();
		boardAndDropPiecesPanel = new JPanel();
		mainPanel = new JPanel();
		northBoardFillerPanel = new JPanel();
		southBoardFillerPanel = new JPanel();
		dropPiecePanels = new JPanel();
		dropPiecePanels.setLayout(new GridLayout(2, 1));
		dropPiecePanels.add(horizontalBlackHoldings);
		dropPiecePanels.add(horizontalWhiteHoldings);

		westBoardFillerPanel = new JPanel();
		eastBoardFillerPanel = new JPanel();

		whiteNameLbl = new JLabel("");
		blackNameLbl = new JLabel("");
		whiteLagLbl = new JLabel("");
		blackLagLbl = new JLabel("");

		whitesClock = new PanelChessClock(initialTimeInSeconds, increment);
		blacksClock = new PanelChessClock(initialTimeInSeconds, increment);

		statusField = new JTextField();
		statusField.setEditable(false);

		boardAndDropPiecesPanel
				.setLayout(boardDropPieceLayout = new SquareSandwichLayout(
						SquareSandwichLayout.NONE_FIXED));
		boardAndDropPiecesPanel.add(northBoardFillerPanel,
				SquareSandwichLayout.NORTH);
		boardAndDropPiecesPanel.add(westBoardFillerPanel,
				SquareSandwichLayout.WEST);
		boardAndDropPiecesPanel.add(board, SquareSandwichLayout.CENTER);
		boardAndDropPiecesPanel.add(eastBoardFillerPanel,
				SquareSandwichLayout.EAST);
		boardAndDropPiecesPanel.add(southBoardFillerPanel,
				SquareSandwichLayout.SOUTH);

		whiteControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		whiteControlsPanel.add(whiteNameLbl);
		whiteControlsPanel.add(whitesClock);
		whiteControlsPanel.add(whiteLagLbl);

		blackControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		blackControlsPanel.add(blackNameLbl);
		blackControlsPanel.add(blacksClock);
		blackControlsPanel.add(blackLagLbl);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(whiteControlsPanel, isWhiteOnTop ? BorderLayout.NORTH
				: BorderLayout.SOUTH);
		mainPanel.add(blackControlsPanel, isWhiteOnTop ? BorderLayout.SOUTH
				: BorderLayout.NORTH);
		mainPanel.add(boardAndDropPiecesPanel, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(statusField, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Time to create ChessArea: "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	public void removeStatusBar() {
		remove(statusField);
	}

	public void addStatusBar() {
		remove(statusField);
		add(statusField, BorderLayout.SOUTH);
	}

	public void prepareForRecycling() {
		removeAllListeners();
	}

	public void setup(String boardId, String whitesName, String whitesRating,
			String blacksName, String blacksRating, boolean isDroppable,
			boolean isWhiteOnTop, Position position) {

		setup(boardId, whitesName, whitesRating, blacksName, blacksRating,
				isDroppable, isWhiteOnTop, -1, -1, position);
	}

	public void setup(String boardId, String whitesName, String whitesRating,
			String blacksName, String blacksRating, boolean isDroppable,
			boolean isWhiteOnTop, int initialTimeInSeconds,
			int initialIncInSeconds, Position position) {
		synchronized(this)
		{
			long startTime = System.currentTimeMillis();
			
			removeAllListeners();
			statusField.setText("");
	
			this.isWhiteOnTop = isWhiteOnTop;
			this.position = position;
			this.initialTimeInSeconds = initialTimeInSeconds;
			this.increment = initialIncInSeconds;
			this.isDroppable = isDroppable;
			this.isTimed = initialTimeInSeconds > 0;
			this.isActive = true;
			this.totalWhiteLag = 0;
			this.totalBlackLag = 0;
	
			setWhiteInfo(whitesName, whitesRating);
			setBlackInfo(blacksName, blacksRating);
	
			setBoardId(boardId);
	
			board.setWhiteOnTop(isWhiteOnTop);
	
			layoutBoardAndDropPiecePanel();
	
			layoutMainPanel();
	
			board.setPosition(position);
	
			startOrStopClocks();
	
			if (isDroppable) {
				layoutBoardAndDropPiecePanel();
			}
	
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Time to setup ChessArea: "
						+ (System.currentTimeMillis() - startTime));
			}
		}

	}

	public void setBoardId(String boardId) {
		synchronized(this)
		{
			this.boardId = boardId;
			horizontalWhiteHoldings.setBoardId(boardId);
			horizontalBlackHoldings.setBoardId(boardId);
			verticalWhiteHoldings.setBoardId(boardId);
			verticalBlackHoldings.setBoardId(boardId);
			board.setBoardId(boardId);
		}
	}

	public String getBoardId() {
		return boardId;
	}

	public void addUserActionListener(UserActionListener userActionListener) {
		synchronized(this)
		{
			userActionListeners.add(userActionListener);
			board.addUserActionListener(userActionListener);
			if (isDroppable) {
				horizontalWhiteHoldings.addUserActionListener(userActionListener);
				horizontalBlackHoldings.addUserActionListener(userActionListener);
				verticalWhiteHoldings.addUserActionListener(userActionListener);
				verticalBlackHoldings.addUserActionListener(userActionListener);
				whiteLeftRightHoldings.addUserActionListener(userActionListener);
				blackLeftRightHoldings.addUserActionListener(userActionListener);
	
			}
		}
	}

	public void removeUserActionListener(UserActionListener userActionListener) {
		synchronized(this)
		{
			userActionListeners.remove(userActionListener);
			board.removeUserActionListener(userActionListener);
			if (isDroppable) {
				horizontalWhiteHoldings.removeUserActionListener(userActionListener);
				horizontalBlackHoldings.removeUserActionListener(userActionListener);
				verticalWhiteHoldings.removeUserActionListener(userActionListener);
				verticalBlackHoldings.removeUserActionListener(userActionListener);
				whiteLeftRightHoldings.removeUserActionListener(userActionListener);
				blackLeftRightHoldings.removeUserActionListener(userActionListener);			
			}
		}
	}
	
	public void removeAllListeners()
	{
		synchronized(this)
		{
			//sucks duplicating this code but better than any other alternative since the fail fast iterators will choke if the remove methods are called.
			
			for (UserActionListener userActionListener : userActionListeners)
			{
				board.removeUserActionListener(userActionListener);
				if (isDroppable) {
					horizontalWhiteHoldings.removeUserActionListener(userActionListener);
					horizontalBlackHoldings.removeUserActionListener(userActionListener);
					verticalWhiteHoldings.removeUserActionListener(userActionListener);
					verticalBlackHoldings.removeUserActionListener(userActionListener);
					whiteLeftRightHoldings.removeUserActionListener(userActionListener);
					blackLeftRightHoldings.removeUserActionListener(userActionListener);			
				}
			}
			userActionListeners.clear();
			
			for (ChessClockListener clockListener : chessClockListeners)
			{
				blacksClock.removeChessClockListener(clockListener);
				whitesClock.removeChessClockListener(clockListener);		
			}		
			chessClockListeners.clear();
		}
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

	public void addWhiteChessClockListener(ChessClockListener listener) {
		if (isTimed) {
			synchronized(this)
			{
				chessClockListeners.add(listener);
				whitesClock.addChessClockListener(listener);
			}
		}
	}

	public void addBlackChessClockListener(ChessClockListener listener) {
		if (isTimed) {
			synchronized(this)
			{
				chessClockListeners.remove(listener);
				blacksClock.addChessClockListener(listener);
			}
		}
	}

	public void removeWhiteChessClockListener(ChessClockListener listener) {
		if (isTimed) {
			synchronized(this)
			{
				chessClockListeners.add(listener);
				whitesClock.removeChessClockListener(listener);
			}
		}
	}

	public void removeBlackChessClockListener(ChessClockListener listener) {
		if (isTimed) {
			synchronized(this)
			{
				chessClockListeners.remove(listener);
				blacksClock.removeChessClockListener(listener);
			}
		}
	}

	public void setShowTenthsWhenTimeIsLessThanSeconds(int value) {
		if (isTimed) {
			synchronized(this)
			{
				whitesClock.setShowTenthsWhenTimeIsLessThanSeconds(value);
				blacksClock.setShowTenthsWhenTimeIsLessThanSeconds(value);
			}
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		synchronized(this)
		{
			long startTime = System.currentTimeMillis();
			this.preferences = preferences;
			TextProperties controlLabelTextProperties = preferences
					.getBoardPreferences().getControlLabelTextProperties();
			TextProperties statusTextProperties = preferences.getBoardPreferences()
					.getStatusBarTextProperties();
			Color controlBackground = preferences.getBoardPreferences()
					.getBackgroundControlsColor();
	
			setBackground(controlBackground);
			boardAndDropPiecesPanel.setBackground(controlBackground);
			blackControlsPanel.setBackground(controlBackground);
			whiteControlsPanel.setBackground(controlBackground);
			mainPanel.setBackground(controlBackground);
			northBoardFillerPanel.setBackground(controlBackground);
			southBoardFillerPanel.setBackground(controlBackground);
			dropPiecePanels.setBackground(controlBackground);
			westBoardFillerPanel.setBackground(controlBackground);
			eastBoardFillerPanel.setBackground(controlBackground);
			board.setBackground(controlBackground);
	
			statusField.setBackground(controlLabelTextProperties.getBackground());
			blackNameLbl.setBackground(controlLabelTextProperties.getBackground());
			whiteNameLbl.setBackground(controlLabelTextProperties.getBackground());
			blackLagLbl.setBackground(controlLabelTextProperties.getBackground());
			whiteLagLbl.setBackground(controlLabelTextProperties.getBackground());
			statusField.setBackground(statusTextProperties.getBackground());
	
			blackNameLbl.setForeground(controlLabelTextProperties.getForeground());
			whiteNameLbl.setForeground(controlLabelTextProperties.getForeground());
			blackLagLbl.setForeground(controlLabelTextProperties.getForeground());
			whiteLagLbl.setForeground(controlLabelTextProperties.getForeground());
			statusField.setForeground(statusTextProperties.getForeground());
	
			blackNameLbl.setFont(controlLabelTextProperties.getFont());
			whiteNameLbl.setFont(controlLabelTextProperties.getFont());
			blackLagLbl.setFont(controlLabelTextProperties.getFont());
			whiteLagLbl.setFont(controlLabelTextProperties.getFont());
			statusField.setFont(statusTextProperties.getFont());
	
			board.setPreferences(preferences);
	
			whitesClock.setPreferences(preferences);
			blacksClock.setPreferences(preferences);
	
			horizontalWhiteHoldings.setPreferences(preferences);
			horizontalBlackHoldings.setPreferences(preferences);
	
			verticalWhiteHoldings.setPreferences(preferences);
			verticalBlackHoldings.setPreferences(preferences);
			
			whiteLeftRightHoldings.setPreferences(preferences);
			blackLeftRightHoldings.setPreferences(preferences);
	
			layoutBoardAndDropPiecePanel();
	
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Set prefs on chess area in "
						+ (System.currentTimeMillis() - startTime));
			}
		}
	}

	public String getWhiteName() {
		return whitesName;
	}

	public String getBlackName() {
		return blacksName;
	}

	public void setWhiteInfo(String whitesName, String whitesRating) {

		String infoString = whitesName + "(" + whitesRating + ")";

		if (infoString.length() > MAX_NAME_LENGTH) {
			infoString = infoString.substring(0, MAX_NAME_LENGTH);
		}
		this.whitesName = StringUtility.padCharsToRight(infoString, ' ',
				MAX_NAME_LENGTH);

		whiteNameLbl.setText(this.whitesName);
	}

	public void setBlackInfo(String blacksName, String blacksRating) {
		String infoString = blacksName + "(" + blacksRating + ")";

		if (infoString.length() > MAX_NAME_LENGTH) {
			infoString = infoString.substring(0, MAX_NAME_LENGTH);
		}
		this.blacksName = StringUtility.padCharsToRight(infoString, ' ',
				MAX_NAME_LENGTH);

		blackNameLbl.setText(this.blacksName);
	}

	public void setPosition(Position position) {
		setPosition(position, true);
	}

	public void setPosition(Position position, boolean areClocksTicking) {
		synchronized(this)
		{
			this.position = position;
			board.setPosition(position);
			if (areClocksTicking) {
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
		synchronized(this)
		{
			this.isActive = isActive;
			if (!isActive)
				stopClocks();
		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void startOrStopClocksWithoutTicking() {
		if (isTimed) {
			synchronized(this)
			{
				blacksClock.stop();
				whitesClock.stop();
				if (isWhitesMove()) {
					whitesClock.startWithoutTicking();
				} else {
					blacksClock.startWithoutTicking();
				}
			}
		}
	}

	public void setWhiteTime(int timeInSeconds) {
		if (isTimed)
			whitesClock.set(timeInSeconds);
	}

	public void setBlackTime(int timeInSeconds) {
		if (isTimed)
			blacksClock.set(timeInSeconds);
	}

	public void setWhiteDropPieces(int dropPieces[]) {
		if (isDroppable) {
			synchronized(this)
			{
				horizontalWhiteHoldings.setFromPieceArray(dropPieces);
				verticalWhiteHoldings.setFromPieceArray(dropPieces);
				whiteLeftRightHoldings.setFromPieceArray(dropPieces);
				getPosition().setWhiteHoldings(dropPieces);
			}
		}
	}

	public void setBlackLag(int lagInMillis) {
		if (isTimed) {
			totalBlackLag += lagInMillis;
			BigDecimal bigDecimal = new BigDecimal(
					(double) totalBlackLag / 1000.0);
			bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_DOWN);

			String blackLabel = StringUtility.padCharsToLeft(bigDecimal
					.toString(), ' ', 3);

			blackLagLbl.setText(blackLabel);

			blackLagLbl.setToolTipText("Total lag: " + bigDecimal.toString()
					+ "seconds . Lag last move: " + lagInMillis
					+ " milliseconds.");
		}
	}

	public void setWhiteLag(int lagInMillis) {
		if (isTimed) {
			totalWhiteLag += lagInMillis;
			BigDecimal bigDecimal = new BigDecimal(
					(double) totalWhiteLag / 1000.0);
			bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_DOWN);

			String whiteLabel = StringUtility.padCharsToLeft(bigDecimal
					.toString(), ' ', 3);

			whiteLagLbl.setText(whiteLabel);

			whiteLagLbl.setToolTipText("Total lag: " + bigDecimal.toString()
					+ "seconds . Lag last move: " + lagInMillis
					+ " milliseconds.");

		}
	}

	public int[] getWhiteDropPieces() {
		if (isDroppable)
			return horizontalWhiteHoldings.getPieceArray();
		else
			return new int[0];
	}

	public void setBlackDropPieces(int dropPieces[]) {
		if (isDroppable) {
			synchronized(this)
			{
				horizontalBlackHoldings.setFromPieceArray(dropPieces);
				verticalBlackHoldings.setFromPieceArray(dropPieces);
				blackLeftRightHoldings.setFromPieceArray(dropPieces);
				getPosition().setBlackHoldings(dropPieces);
			}
		}
	}

	public int[] getBlackDropPieces() {
		if (isDroppable)
			return horizontalBlackHoldings.getPieceArray();
		else
			return new int[0];
	}

	public void flip() {
		
		synchronized(this)
		{
			isWhiteOnTop = !isWhiteOnTop;
			layoutMainPanel();
			layoutBoardAndDropPiecePanel();
			board.flip();
		}

		// force a layout.
		boardAndDropPiecesPanel.getLayout().layoutContainer(
				boardAndDropPiecesPanel);
		dropPiecePanels.getLayout().layoutContainer(dropPiecePanels);
		mainPanel.getLayout().layoutContainer(mainPanel);
		board.getLayout().layoutContainer(board);
	    whiteLeftRightHoldings.getLayout().layoutContainer(whiteLeftRightHoldings);
	    blackLeftRightHoldings.getLayout().layoutContainer(blackLeftRightHoldings);
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

	private void layoutMainPanel() {
		BorderLayout borderLayout = (BorderLayout) mainPanel.getLayout();
		if ((borderLayout.getLayoutComponent(BorderLayout.NORTH) == blackControlsPanel && isWhiteOnTop)
				|| (borderLayout.getLayoutComponent(BorderLayout.NORTH) == whiteControlsPanel && !isWhiteOnTop)) {
			mainPanel.remove(whiteControlsPanel);
			mainPanel.remove(blackControlsPanel);

			mainPanel.add(whiteControlsPanel, isWhiteOnTop ? BorderLayout.NORTH
					: BorderLayout.SOUTH);
			mainPanel.add(blackControlsPanel, isWhiteOnTop ? BorderLayout.SOUTH
					: BorderLayout.NORTH);
		}
	}

	private void layoutBoardAndDropPiecePanel() {
		if (isDroppable()) {
			int dropPieceMode= getPreferences().getBoardPreferences()
					.dropPiecesLocation();

			boardAndDropPiecesPanel.remove(dropPiecePanels);
			boardAndDropPiecesPanel.remove(verticalWhiteHoldings);
			boardAndDropPiecesPanel.remove(verticalBlackHoldings);
			boardAndDropPiecesPanel.remove(whiteLeftRightHoldings);
			boardAndDropPiecesPanel.remove(blackLeftRightHoldings);		
			boardAndDropPiecesPanel.remove(eastBoardFillerPanel);
			boardAndDropPiecesPanel.remove(westBoardFillerPanel);
			boardAndDropPiecesPanel.remove(northBoardFillerPanel);
			boardAndDropPiecesPanel.remove(southBoardFillerPanel);

			if (dropPieceMode == BoardPreferences.DROP_PIECES_ON_RIGHT) {
				boardDropPieceLayout.setType(SquareSandwichLayout.WEST_FIXED);

				dropPiecePanels.removeAll();
				dropPiecePanels.add(isWhiteOnTop ? horizontalWhiteHoldings
						: horizontalBlackHoldings);
				dropPiecePanels.add(isWhiteOnTop ? horizontalBlackHoldings
						: horizontalWhiteHoldings);

				boardAndDropPiecesPanel.add(dropPiecePanels,
						SquareSandwichLayout.EAST);
				boardAndDropPiecesPanel.add(westBoardFillerPanel,
						SquareSandwichLayout.WEST);				
				boardAndDropPiecesPanel.add(northBoardFillerPanel,
						SquareSandwichLayout.NORTH);
				boardAndDropPiecesPanel.add(southBoardFillerPanel,
						SquareSandwichLayout.SOUTH);
			} 
			else if (dropPieceMode == BoardPreferences.DROP_PIECES_ON_TOP_BOTTOM) {
				boardDropPieceLayout
						.setType(SquareSandwichLayout.NORTH_SOUTH_FIXED);
				boardAndDropPiecesPanel.add(eastBoardFillerPanel,
						SquareSandwichLayout.EAST);
				boardAndDropPiecesPanel.add(westBoardFillerPanel,
						SquareSandwichLayout.WEST);				

				verticalWhiteHoldings
						.setOrientation(isWhiteOnTop ? VerticalHoldingsPanel.NORTH_ORIENTATION
								: VerticalHoldingsPanel.SOUTH_ORIENTATION);
				verticalBlackHoldings
						.setOrientation(!isWhiteOnTop ? VerticalHoldingsPanel.NORTH_ORIENTATION
								: VerticalHoldingsPanel.SOUTH_ORIENTATION);
				boardAndDropPiecesPanel.add(
						isWhiteOnTop ? verticalWhiteHoldings
								: verticalBlackHoldings,
						SquareSandwichLayout.NORTH);
				boardAndDropPiecesPanel.add(
						isWhiteOnTop ? verticalBlackHoldings
								: verticalWhiteHoldings,
						SquareSandwichLayout.SOUTH);
			}
			else
			{
				boardDropPieceLayout
				.setType(SquareSandwichLayout.EAST_WEST_FIXED);
				boardAndDropPiecesPanel.add(northBoardFillerPanel,
						SquareSandwichLayout.NORTH);
				boardAndDropPiecesPanel.add(southBoardFillerPanel,
						SquareSandwichLayout.SOUTH);
				
                 if (isWhiteOnTop)
                 {
                	    whiteLeftRightHoldings.setOrientation(LeftRightHoldingsPanel.LEFT_ORIENTATION);
                	    blackLeftRightHoldings.setOrientation(LeftRightHoldingsPanel.RIGHT_ORIENTATION);
                	    boardAndDropPiecesPanel.add(whiteLeftRightHoldings,SquareSandwichLayout.WEST);
                	    boardAndDropPiecesPanel.add(blackLeftRightHoldings,SquareSandwichLayout.EAST);
                 }
                 else
                 {
                  	blackLeftRightHoldings.setOrientation(LeftRightHoldingsPanel.LEFT_ORIENTATION);
                	    whiteLeftRightHoldings.setOrientation(LeftRightHoldingsPanel.RIGHT_ORIENTATION);
                	    boardAndDropPiecesPanel.add(whiteLeftRightHoldings,SquareSandwichLayout.EAST);
                	    boardAndDropPiecesPanel.add(blackLeftRightHoldings,SquareSandwichLayout.WEST);            	                      	 
                 }           		
			}
		} else {
			boardDropPieceLayout.setType(SquareSandwichLayout.NONE_FIXED);
			boardAndDropPiecesPanel.remove(dropPiecePanels);
			boardAndDropPiecesPanel.remove(verticalWhiteHoldings);
			boardAndDropPiecesPanel.remove(verticalBlackHoldings);
			boardAndDropPiecesPanel.remove(whiteLeftRightHoldings);
			boardAndDropPiecesPanel.remove(blackLeftRightHoldings);
			boardAndDropPiecesPanel.remove(northBoardFillerPanel);
			boardAndDropPiecesPanel.remove(southBoardFillerPanel);
			boardAndDropPiecesPanel.remove(eastBoardFillerPanel);
			boardAndDropPiecesPanel.remove(westBoardFillerPanel);
			
			boardAndDropPiecesPanel.add(eastBoardFillerPanel,
					SquareSandwichLayout.EAST);
			boardAndDropPiecesPanel.add(northBoardFillerPanel,
					SquareSandwichLayout.NORTH);
			boardAndDropPiecesPanel.add(southBoardFillerPanel,
					SquareSandwichLayout.SOUTH);
			boardAndDropPiecesPanel.add(westBoardFillerPanel,
					SquareSandwichLayout.WEST);				
			
		}
	}

	private void stopClocks() {
		if (isTimed) {
			blacksClock.stop();
			whitesClock.stop();
		}
	}

	private void startOrStopClocks() {
		if (isTimed) {
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