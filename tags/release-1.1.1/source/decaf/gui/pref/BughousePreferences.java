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

import decaf.util.GUIUtil;
import decaf.util.TextProperties;

public class BughousePreferences implements Cloneable, Serializable {
	private static final long serialVersionUID = 11;

	private Dimension bugEarDimension = null;

	private Point bugEarPoint = null;

	private Dimension gameWindowDimension = null;

	private Point gameWindowPoint = null;

	private Dimension chatWindowDimension = null;

	private Point chatWindowPoint = null;

	private int boardSplitterLocation = 0;

	private boolean isShowingPartnerCommunicationButtons = true;

	private boolean isPlayingMoveSoundOnPartnersBoard = true;

	private boolean isShowingLag = true;

	private Color bughouseButtonBackground = Color.BLACK;

	private TextProperties bughouseButtonTextProperties = new TextProperties(
			new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 12), Color.black,
			Color.white);

	private String autoFirstWhiteMove;

	private boolean isShowingBugToolbar = true;

	private boolean isShowingStatusBar = true;

	private boolean isPlayingLeftBoard = true;

	private boolean isShowingPartnerCaptions = true;

	private boolean isShowingMoveListsOnPlayingGame = false;

	private boolean isShowingMoveListOnObsGame = false;

	private boolean isShowingUpSideUpTime = true;

	public static BughousePreferences getDefault() {
		BughousePreferences result = new BughousePreferences();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getDefaultScreenDevice().getDefaultConfiguration());

		int totalWidth = screenSize.width - insets.left - insets.right;
		int totalHeight = screenSize.height - insets.top - insets.bottom;

		result.bugEarPoint = new Point(insets.left, insets.top);
		result.bugEarDimension = new Dimension(250, (int) (totalHeight * .7));

		result.gameWindowPoint = new Point(insets.left
				+ result.bugEarDimension.width + 2, insets.top);
		result.gameWindowDimension = new Dimension(totalWidth
				- result.bugEarDimension.width, (int) (totalHeight * .7));

		result.chatWindowPoint = new Point(insets.left, insets.top
				+ (int) (totalHeight * .7) + 1);
		result.chatWindowDimension = new Dimension(totalWidth,
				(int) (totalHeight * .3));

		result.boardSplitterLocation = result.gameWindowDimension.width / 2;

		return result;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Dimension getBugEarDimension() {
		return bugEarDimension;
	}

	public void setBugEarDimension(Dimension bugEarDimension) {
		this.bugEarDimension = bugEarDimension;
	}

	public Point getBugEarPoint() {
		return bugEarPoint;
	}

	public void setBugEarPoint(Point bugEarPoint) {
		this.bugEarPoint = bugEarPoint;
	}

	public Color getBughouseButtonBackground() {
		return bughouseButtonBackground;
	}

	public void setBughouseButtonBackground(Color bughouseButtonBackground) {
		this.bughouseButtonBackground = bughouseButtonBackground;
	}

	public TextProperties getBughouseButtonTextProperties() {
		return bughouseButtonTextProperties;
	}

	public void setBughouseButtonTextProperties(
			TextProperties bughouseButtonTextProperties) {
		this.bughouseButtonTextProperties = bughouseButtonTextProperties;
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

	public Dimension getGameWindowDimension() {
		return gameWindowDimension;
	}

	public void setGameWindowDimension(Dimension gameWindowDimension) {
		this.gameWindowDimension = gameWindowDimension;
	}

	public Point getGameWindowPoint() {
		return gameWindowPoint;
	}

	public void setGameWindowPoint(Point gameWindowPoint) {
		this.gameWindowPoint = gameWindowPoint;
	}

	public boolean isPlayingMoveSoundOnPartnersBoard() {
		return isPlayingMoveSoundOnPartnersBoard;
	}

	public void setPlayingMoveSoundOnPartnersBoard(
			boolean isPlayingMoveSoundOnPartnersBoard) {
		this.isPlayingMoveSoundOnPartnersBoard = isPlayingMoveSoundOnPartnersBoard;
	}

	public boolean isShowingLag() {
		return isShowingLag;
	}

	public void setShowingLag(boolean isShowingLag) {
		this.isShowingLag = isShowingLag;
	}

	public boolean isShowingPartnerCommunicationButtons() {
		return isShowingPartnerCommunicationButtons;
	}

	public void setShowingPartnerCommunicationButtons(
			boolean isShowingPartnerCommunicationButtons) {
		this.isShowingPartnerCommunicationButtons = isShowingPartnerCommunicationButtons;
	}

	public int getBoardSplitterLocation() {
		return boardSplitterLocation;
	}

	public void setBoardSplitterLocation(int boardSplitterLocation) {
		this.boardSplitterLocation = boardSplitterLocation;
	}

	public String getAutoFirstWhiteMove() {
		return autoFirstWhiteMove;
	}

	public void setAutoFirstWhiteMove(String autoFirstWhiteMove) {
		this.autoFirstWhiteMove = autoFirstWhiteMove;
	}

	public boolean isShowingBugToolbar() {
		return isShowingBugToolbar;
	}

	public void setShowingBugToolbar(boolean isShowingBugToolbar) {
		this.isShowingBugToolbar = isShowingBugToolbar;
	}

	public boolean isShowingStatusBar() {
		return isShowingStatusBar;
	}

	public void setShowingStatusBar(boolean isShowingStatusBar) {
		this.isShowingStatusBar = isShowingStatusBar;
	}

	public boolean isPlayingLeftBoard() {
		return isPlayingLeftBoard;
	}

	public void setPlayingLeftBoard(boolean isPlayingLeftBoard) {
		this.isPlayingLeftBoard = isPlayingLeftBoard;
	}

	public boolean isShowingPartnerCaptions() {
		return isShowingPartnerCaptions;
	}

	public void setShowingPartnerCaptions(boolean isShowingPartnerCaptions) {
		this.isShowingPartnerCaptions = isShowingPartnerCaptions;
	}

	public boolean isShowingMoveListsOnPlayingGame() {
		return isShowingMoveListsOnPlayingGame;
	}

	public void setShowingMoveListsOnPlayingGame(
			boolean isShowingMoveListsOnPlayingGame) {
		this.isShowingMoveListsOnPlayingGame = isShowingMoveListsOnPlayingGame;
	}

	public boolean isShowingUpSideUpTime() {
		return isShowingUpSideUpTime;
	}

	public void setShowingUpSideUpTime(boolean isShowingUpSideUpTime) {
		this.isShowingUpSideUpTime = isShowingUpSideUpTime;
	}

	public boolean isShowingMoveListOnObsGame() {
		return isShowingMoveListOnObsGame;
	}

	public void setShowingMoveListOnObsGame(boolean isShowingMoveListOnObsGame) {
		this.isShowingMoveListOnObsGame = isShowingMoveListOnObsGame;
	}

}
