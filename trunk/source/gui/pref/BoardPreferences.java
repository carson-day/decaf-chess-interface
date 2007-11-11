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
package decaf.gui.pref;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.Serializable;

import decaf.gui.util.BorderUtil;
import decaf.gui.util.TextProperties;
import decaf.gui.widgets.ChessSet;
import decaf.gui.widgets.ImageChessSet;

public class BoardPreferences implements Cloneable, Serializable {

	public static final int NO_PREMOVE = 0;

	public static final int TRUE_PREMOVE = 1;

	public static final int QUEUED_PREMOVE = 2;

	public static final int AUTO_PROMOTE_DISABLED = 0;

	public static final int AUTO_BISHOP = 1;

	public static final int AUTO_KNIGHT = 2;

	public static final int AUTO_ROOK = 3;

	public static final int AUTO_QUEEN = 4;

	public static final int NO_HIGHLIGHT = 0;

	public static final int HIGHLIGHT_FADE = 1;

	public static final int HIGHLIGHT_UNTIL_NEXT_MOVE = 2;

	public static final int DROP_PIECES_ON_TOP_BOTTOM = 1;

	public static final int DROP_PIECES_ON_RIGHT = 2;
	
	public static final int DROP_PIECES_ON_LEFT_RIGHT = 3;	

	private int dropPiecesLocation = 2;

	private int highlightMode = 1;

	private int autoPromotionMode = 4;

	private int premoveType = TRUE_PREMOVE;

	private int showTenthsWhenTimeIsLessThanSeconds = 15;

	private boolean isClosingAllWindowsOnGameStart = true;

	private boolean isShowingLag = false;

	private boolean isCLosingInactiveGamesOnNewObservedGame = true;

	private boolean isPlayingMoveSoundOnObserving = true;

	private boolean snapToChatIfNoGames = false;

	private Dimension getGameWindowSize = new Dimension(600, 450);

	private Point gameWindowPoint = new Point(275, 25);

	private Dimension chatWindowDimension = new Dimension(900, 275);

	private Point chatWindowPoint = new Point(25, 500);

	private Color dropSquareColor = new Color(128, 128, 128);

	private Color backgroundControlsColor = Color.BLACK;

	private Color lightSquareBackgroundColor = new Color(204, 204, 204);

	private Color darkSquareBackgroundColor = new Color(128, 128, 128);

	private Color moveHighlightColor = Color.GREEN.brighter().brighter();

	private TextProperties clockActiveTextProperties = new TextProperties(
			new Font("Monospaced", Font.PLAIN, 14), Color.green, Color.black);

	private TextProperties clockInactiveTextProperties = new TextProperties(
			new Font("Monospaced", Font.PLAIN, 14), Color.red, Color.black);

	private TextProperties controlLabelTextProperties = new TextProperties(
			new Font("Monospaced", Font.PLAIN, 14), new Color(128, 128, 128),
			Color.black);

	private TextProperties statusBarTextProperties = new TextProperties(
			new Font("Monospaced", Font.PLAIN, 14), new Color(128, 128, 128),
			Color.black);

	private TextProperties dropSquareLabelTextProperties = new TextProperties(
			new Font("Monospaced", Font.BOLD, 14), Color.green, Color.black);

	private ChessSet set = new ImageChessSet("WINBOARD");

	private int dropSquareBorder = BorderUtil.RAISED_ETCHED_BORDER;

	private int squareBorder = BorderUtil.EMPTY_BORDER;

	private int chessBoardBorder = BorderUtil.EMPTY_BORDER;

	private String autoFirstWhiteMove;

	private boolean isShowingToolbar = true;

	private boolean isShowingStatusBar = true;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static BoardPreferences getDefault() {
		BoardPreferences result = new BoardPreferences();
		return result;
	}

	public int getAutoPromotionMode() {
		return autoPromotionMode;
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
		return getGameWindowSize;
	}

	public void setGameWindowDimension(Dimension gameWindowSize) {
		this.getGameWindowSize = gameWindowSize;
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

	public int getHighlightMode() {
		return highlightMode;
	}

	public void setHighlightMode(int highlightMode) {
		this.highlightMode = highlightMode;
	}

	public boolean isShowingToolbar() {
		return isShowingToolbar;
	}

	public void setShowingToolbar(boolean isShowingToolbar) {
		this.isShowingToolbar = isShowingToolbar;
	}

	public int dropPiecesLocation() {
		return dropPiecesLocation;
	}

	public void dropPiecesLocation(int dropPiecesMode) {
		this.dropPiecesLocation = dropPiecesMode;
	}

	public boolean isShowingStatusBar() {
		return isShowingStatusBar;
	}

	public void setShowingStatusBar(boolean isShowingStatusBar) {
		this.isShowingStatusBar = isShowingStatusBar;
	}

}
