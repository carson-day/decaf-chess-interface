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

import decaf.gui.util.TextProperties;

public class BughousePreferences implements Cloneable, Serializable {

	private Dimension bugEarDimension = new Dimension(200, 450);

	private Point bugEarPoint = new Point(20, 25);

	private Dimension gameWindowDimension = new Dimension(765, 450);

	private Point gameWindowPoint = new Point(228, 25);

	private Dimension chatWindowDimension = new Dimension(975, 300);

	private Point chatWindowPoint = new Point(20, 475);

	private int boardSplitterLocation = 435;

	private boolean isShowingPartnerCommunicationButtons = true;

	private boolean isPlayingMoveSoundOnPartnersBoard = true;

	private boolean isShowingLag = true;

	private Color bughouseButtonBackground = Color.BLACK;

	private TextProperties bughouseButtonTextProperties = new TextProperties(
			new Font("Monospaced", Font.PLAIN, 12), Color.black, Color.white);

	private String autoFirstWhiteMove;

	private boolean isShowingBugToolbar = true;

	private boolean isShowingStatusBar = true;

	private boolean isPlayingLeftBoard = true;

	public static BughousePreferences getDefault() {
		BughousePreferences result = new BughousePreferences();
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

}
