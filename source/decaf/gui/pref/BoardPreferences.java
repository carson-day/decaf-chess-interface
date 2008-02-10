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
package decaf.gui.pref;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.Serializable;

import decaf.gui.widgets.ChessSet;
import decaf.gui.widgets.SquareImageBackground;
import decaf.util.BorderUtil;
import decaf.util.GUIUtil;
import decaf.util.TextProperties;

public class BoardPreferences implements Cloneable, Serializable {
	private static final long serialVersionUID = 11;

	public static final int DEFAULT_LAYOUT = 0;

	public static final int WINBOARD_LAYOUT = 1;

	public static final int BLITZEN_LAYOUT = 2;

	public static final int NO_PREMOVE = 0;

	public static final int TRUE_PREMOVE = 1;

	public static final int QUEUED_PREMOVE = 2;

	public static final int AUTO_PROMOTE_DISABLED = 0;

	public static final int AUTO_BISHOP = 1;

	public static final int AUTO_KNIGHT = 2;

	public static final int AUTO_ROOK = 3;

	public static final int AUTO_QUEEN = 4;

	public static final int NONE_SQUARE_SELECTION_MODE = 0;

	public static final int FADE_SQUARE_SELECTION_MODE = 1;

	public static final int BORDER_SQUARE_SELECTION_MODE = 2;

	public static final int FILL_BACKGROUND_SQUARE_SELECTION_MODE = 3;
	
	public static final int DIAGONAL_LINE_BACKGROUND_SQUARE_SELECTION_MODE = 4;

	public static final int DROP_PIECES_ON_TOP_BOTTOM = 1;

	public static final int DROP_PIECES_ON_RIGHT_2x4 = 2;

	public static final int DROP_PIECES_ON_RIGHT_3x2 = 4;

	public static final int DROP_PIECES_ON_LEFT_RIGHT = 3;

	public static final int ONLY_GAMES_I_PLAY_TO_FRONT = 0;

	public static final int ALL_GAMES_TO_FRONT = 1;

	public static final int STANDARD_DRAG_AND_DROP = 0;

	public static final int INVISIBLE_MOVE = 1;

	public static final int CLICK_CLICK_DRAG_AND_DROP = 2;

	/**
	 * This should always be an even number. Its the piece size is reduced from
	 * the square size.
	 */
	private int pieceSizeDelta = 4;

	private int dropPiecesLocation = 2;

	private int dragAndDropMode = STANDARD_DRAG_AND_DROP;

	private int squareSelectionMode = DIAGONAL_LINE_BACKGROUND_SQUARE_SELECTION_MODE;

	private int autoPromotionMode = AUTO_QUEEN;

	private int premoveType = TRUE_PREMOVE;

	private int showTenthsWhenTimeIsLessThanSeconds = 10;

	private boolean isClosingAllWindowsOnGameStart = true;

	private boolean isShowingLag = false;

	private boolean isCLosingInactiveGamesOnNewObservedGame = true;

	private boolean isPlayingMoveSoundOnObserving = true;

	private boolean snapToChatIfNoGames = false;

	private boolean isUnfollowingOnPlayingGame = true;

	private boolean isSmartMoveEnabled = false;

	private Dimension gameWindowSize = null;

	private Point gameWindowPoint = null;

	private Dimension chatWindowDimension = null;

	private Point chatWindowPoint = null;

	private Color dropSquareColor = Color.BLACK;

	private Color backgroundControlsColor = Color.BLACK;

	private Color lightSquareBackgroundColor = new Color(204, 204, 204);

	private Color darkSquareBackgroundColor = new Color(128, 128, 128);

	private Color moveHighlightColor = new Color(255,0,153);

	private String layoutClassName = "decaf.gui.widgets.chessarealayout.ThiefChessAreaLayout";

	private TextProperties clockActiveTextProperties = new TextProperties(
			new Font(GUIUtil.getDefaultFont(), Font.BOLD, 16), Color.red,
			Color.black);

	private TextProperties clockInactiveTextProperties = new TextProperties(
			new Font(GUIUtil.getDefaultFont(), Font.BOLD, 16), new Color(128,
					128, 128), Color.black);

	private TextProperties controlLabelTextProperties = new TextProperties(
			new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 14), new Color(128,
					128, 128), Color.black);

	private TextProperties statusBarTextProperties = new TextProperties(
			new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 14), new Color(128,
					128, 128), Color.black);

	private TextProperties dropSquareLabelTextProperties = new TextProperties(
			new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 14), Color.green,
			Color.black);

	private TextProperties markTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 14), Color.red, Color.black);

	private ChessSet set = new ChessSet("WCN");

	private int dropSquareBorder = BorderUtil.EMPTY_BORDER;

	private int squareBorder = BorderUtil.EMPTY_BORDER;

	private int chessBoardBorder = BorderUtil.EMPTY_BORDER;

	private String autoFirstWhiteMove;

	private boolean isShowingToolbar = true;

	private boolean isShowingStatusBar = true;

	private boolean isShowingCoordinates = true;

	private boolean isShowingGameCaptions = true;

	private boolean isShowingMoveListOnObsGames = false;

	private boolean isShowingMoveListOnExaminedGames = true;

	private boolean isShowingMoveListOnPlayingGames = false;

	private boolean isShowingSideUpTime = false;

	private boolean isShowingPieceJail = false;
	
	private boolean isShowingMyMovesAsSelected = false;

	private SquareImageBackground squareImageBackground = new SquareImageBackground(
			"Paper");

	private int gamesToFrontMode = ONLY_GAMES_I_PLAY_TO_FRONT;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static BoardPreferences getDefault() {
		BoardPreferences result = new BoardPreferences();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getDefaultScreenDevice().getDefaultConfiguration());

		int totalWidth = screenSize.width - insets.left - insets.right;
		int totalHeight = screenSize.height - insets.top - insets.bottom;

		result.gameWindowPoint = new Point(insets.left, insets.top);
		result.gameWindowSize = new Dimension(totalWidth,
				(int) (totalHeight * .7));

		result.chatWindowPoint = new Point(insets.left, insets.top
				+ (int) (totalHeight * .7) + 1);
		result.chatWindowDimension = new Dimension(totalWidth,
				(int) (totalHeight * .3));

		return result;
	}

	public int getAutoPromotionMode() {
		return autoPromotionMode;
	}

	public SquareImageBackground getSquareImageBackground() {
		return squareImageBackground;
	}

	public void setSquareImageBackground(
			SquareImageBackground imageSquareBackground) {
		this.squareImageBackground = imageSquareBackground;
	}

	public void setAutoPromotionMode(int autoPromotionMode) {
		this.autoPromotionMode = autoPromotionMode;
	}

	public Color getBackgroundControlsColor() {
		return backgroundControlsColor;
	}

	public void setBackgroundControlsColor(Color backgroundControlsColor) {
		this.backgroundControlsColor = backgroundControlsColor;
	}

	public Dimension getChatWindowDimension() {
		return chatWindowDimension;
	}

	public void setChatWindowDimension(Dimension chatWindowDimension) {
		this.chatWindowDimension = chatWindowDimension;
	}

	public Point getChatWindowPoint() {
		return chatWindowPoint;
	}

	public void setChatWindowPoint(Point chatWindowPoint) {
		this.chatWindowPoint = chatWindowPoint;
	}

	public int getChessBoardBorder() {
		return chessBoardBorder;
	}

	public void setChessBoardBorder(int chessBoardBorder) {
		this.chessBoardBorder = chessBoardBorder;
	}

	public TextProperties getClockActiveTextProperties() {
		return clockActiveTextProperties;
	}

	public void setClockActiveTextProperties(
			TextProperties clockActiveTextProperties) {
		this.clockActiveTextProperties = clockActiveTextProperties;
	}

	public TextProperties getClockInactiveTextProperties() {
		return clockInactiveTextProperties;
	}

	public void setClockInactiveTextProperties(
			TextProperties clockInactiveTextProperties) {
		this.clockInactiveTextProperties = clockInactiveTextProperties;
	}

	public TextProperties getControlLabelTextProperties() {
		return controlLabelTextProperties;
	}

	public void setControlLabelTextProperties(
			TextProperties controlLabelTextProperties) {
		this.controlLabelTextProperties = controlLabelTextProperties;
	}

	public Color getDarkSquareBackgroundColor() {
		return darkSquareBackgroundColor;
	}

	public void setDarkSquareBackgroundColor(Color darkSquareBackgroundColor) {
		this.darkSquareBackgroundColor = darkSquareBackgroundColor;
	}

	public int getDropSquareBorder() {
		return dropSquareBorder;
	}

	public void setDropSquareBorder(int dropSquareBorder) {
		this.dropSquareBorder = dropSquareBorder;
	}

	public Color getDropSquareColor() {
		return dropSquareColor;
	}

	public void setDropSquareColor(Color dropSquareColor) {
		this.dropSquareColor = dropSquareColor;
	}

	public TextProperties getDropSquareLabelTextProperties() {
		return dropSquareLabelTextProperties;
	}

	public void setDropSquareLabelTextProperties(
			TextProperties dropSquareLabelTextProperties) {
		this.dropSquareLabelTextProperties = dropSquareLabelTextProperties;
	}

	public Dimension getGameWindowSize() {
		return gameWindowSize;
	}

	public void setGameWindowDimension(Dimension gameWindowSize) {
		this.gameWindowSize = gameWindowSize;
	}

	public Point getGameWindowPoint() {
		return gameWindowPoint;
	}

	public void setGameWindowPoint(Point gameWindowPoint) {
		this.gameWindowPoint = gameWindowPoint;
	}

	public boolean isClosingAllWindowsOnGameStart() {
		return isClosingAllWindowsOnGameStart;
	}

	public void setClosingAllWindowsOnGameStart(
			boolean isClosingAllWindowsOnGameStart) {
		this.isClosingAllWindowsOnGameStart = isClosingAllWindowsOnGameStart;
	}

	public boolean isCLosingInactiveGamesOnNewObservedGame() {
		return isCLosingInactiveGamesOnNewObservedGame;
	}

	public void setClosingInactiveGamesOnNewObservedGame(
			boolean isCLosingInactiveGamesOnNewObservedGame) {
		this.isCLosingInactiveGamesOnNewObservedGame = isCLosingInactiveGamesOnNewObservedGame;
	}

	public boolean isShowingLag() {
		return isShowingLag;
	}

	public void setShowingLag(boolean isShowingLag) {
		this.isShowingLag = isShowingLag;
	}

	public Color getLightSquareBackgroundColor() {
		return lightSquareBackgroundColor;
	}

	public void setLightSquareBackgroundColor(Color lightSquareBackgroundColor) {
		this.lightSquareBackgroundColor = lightSquareBackgroundColor;
	}

	public Color getMoveHighlightColor() {
		return moveHighlightColor;
	}

	public void setMoveHighlightColor(Color moveHighlightColor) {
		this.moveHighlightColor = moveHighlightColor;
	}

	public int getPremoveType() {
		return premoveType;
	}

	public void setPremoveType(int premoveType) {
		this.premoveType = premoveType;
	}

	public ChessSet getSet() {
		return set;
	}

	public void setSet(ChessSet set) {
		this.set = set;
	}

	public int getShowTenthsWhenTimeIsLessThanSeconds() {
		return showTenthsWhenTimeIsLessThanSeconds;
	}

	public void setShowTenthsWhenTimeIsLessThanSeconds(
			int showTenthsWhenTimeIsLessThanSeconds) {
		this.showTenthsWhenTimeIsLessThanSeconds = showTenthsWhenTimeIsLessThanSeconds;
	}

	public int getSquareBorder() {
		return squareBorder;
	}

	public void setSquareBorder(int squareBorder) {
		this.squareBorder = squareBorder;
	}

	public TextProperties getStatusBarTextProperties() {
		return statusBarTextProperties;
	}

	public void setStatusBarTextProperties(
			TextProperties statusBarTextProperties) {
		this.statusBarTextProperties = statusBarTextProperties;
	}

	public boolean isPlayingMoveSoundOnObserving() {
		return isPlayingMoveSoundOnObserving;
	}

	public void setPlayingMoveSoundOnObserving(
			boolean isPlayingMoveSoundOnObserving) {
		this.isPlayingMoveSoundOnObserving = isPlayingMoveSoundOnObserving;
	}

	public boolean isSnapToChatIfNoGames() {
		return snapToChatIfNoGames;
	}

	public void setSnapToChatIfNoGames(boolean snapToChatIfNoGames) {
		this.snapToChatIfNoGames = snapToChatIfNoGames;
	}

	public String getAutoFirstWhiteMove() {
		return autoFirstWhiteMove;
	}

	public void setAutoFirstWhiteMove(String autoFirstWhiteMove) {
		this.autoFirstWhiteMove = autoFirstWhiteMove;
	}

	public int getSquareSelectionMode() {
		return squareSelectionMode;
	}

	public void setSquareSelectionMode(int highlightMode) {
		this.squareSelectionMode = highlightMode;
	}

	public boolean isShowingToolbar() {
		return isShowingToolbar;
	}

	public void setShowingToolbar(boolean isShowingToolbar) {
		this.isShowingToolbar = isShowingToolbar;
	}

	public int getDropPiecesLocation() {
		return dropPiecesLocation;
	}

	public void setDropPiecesLocation(int dropPiecesMode) {
		this.dropPiecesLocation = dropPiecesMode;
	}

	public boolean isShowingStatusBar() {
		return isShowingStatusBar;
	}

	public void setShowingStatusBar(boolean isShowingStatusBar) {
		this.isShowingStatusBar = isShowingStatusBar;
	}

	public boolean isShowingCoordinates() {
		return isShowingCoordinates;
	}

	public void setShowingCoordinates(boolean isShowingCoordinates) {
		this.isShowingCoordinates = isShowingCoordinates;
	}

	public boolean isShowingGameCaptions() {
		return isShowingGameCaptions;
	}

	public void setShowingGameCaptions(boolean isShowingGameCaptions) {
		this.isShowingGameCaptions = isShowingGameCaptions;
	}

	public int getGamesToFrontMode() {
		return gamesToFrontMode;
	}

	public void setGamesToFrontMode(int gamesToFrontMode) {
		this.gamesToFrontMode = gamesToFrontMode;
	}

	public int getDragAndDropMode() {
		return dragAndDropMode;
	}

	public void setDragAndDropMode(int dragAndDropMode) {
		this.dragAndDropMode = dragAndDropMode;
	}

	public boolean isShowingMoveListOnExaminedGames() {
		return isShowingMoveListOnExaminedGames;
	}

	public void setShowingMoveListOnExaminedGames(
			boolean isShowingMoveListOnExaminedGames) {
		this.isShowingMoveListOnExaminedGames = isShowingMoveListOnExaminedGames;
	}

	public boolean isShowingMoveListOnObsGames() {
		return isShowingMoveListOnObsGames;
	}

	public void setShowingMoveListOnObsGames(boolean isShowingMoveListOnObsGames) {
		this.isShowingMoveListOnObsGames = isShowingMoveListOnObsGames;
	}

	public boolean isShowingMoveListOnPlayingGames() {
		return isShowingMoveListOnPlayingGames;
	}

	public void setShowingMoveListOnPlayingGames(
			boolean isShowingMoveListOnPlayingGames) {
		this.isShowingMoveListOnPlayingGames = isShowingMoveListOnPlayingGames;
	}

	public int getPieceSizeDelta() {
		return pieceSizeDelta;
	}

	public void setPieceSizeDelta(int pieceSizeDelta) {
		this.pieceSizeDelta = pieceSizeDelta;
	}

	public boolean isUnfollowingOnPlayingGame() {
		return isUnfollowingOnPlayingGame;
	}

	public void setUnfollowingOnPlayingGame(boolean isUnfollowingOnPlayingGame) {
		this.isUnfollowingOnPlayingGame = isUnfollowingOnPlayingGame;
	}

	public boolean isSmartMoveEnabled() {
		return isSmartMoveEnabled;
	}

	public void setSmartMoveEnabled(boolean isSmartMoveEnabled) {
		this.isSmartMoveEnabled = isSmartMoveEnabled;
	}

	public boolean isShowingSideUpTime() {
		return isShowingSideUpTime;
	}

	public void setShowingSideUpTime(boolean isShowingSideUpTime) {
		this.isShowingSideUpTime = isShowingSideUpTime;
	}

	public boolean isShowingPieceJail() {
		return isShowingPieceJail;
	}

	public void setShowingPieceJail(boolean isShowingPieceJail) {
		this.isShowingPieceJail = isShowingPieceJail;
	}

	public String getLayoutClassName() {
		return layoutClassName;
	}

	public void setLayoutClassName(String layoutClassName) {
		this.layoutClassName = layoutClassName;
	}

	public TextProperties getMarkTextProperties() {
		return markTextProperties;
	}

	public void setMarkTextProperties(TextProperties markTextProperties) {
		this.markTextProperties = markTextProperties;
	}

	public boolean isShowingMyMovesAsSelected() {
		return isShowingMyMovesAsSelected;
	}

	public void setShowingMyMovesAsSelected(boolean isShowingMyMovesAsSelected) {
		this.isShowingMyMovesAsSelected = isShowingMyMovesAsSelected;
	}


}
