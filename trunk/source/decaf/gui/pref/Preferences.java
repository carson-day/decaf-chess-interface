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

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

import decaf.resources.ResourceManagerFactory;

public class Preferences implements Serializable, Cloneable {

	private static final long serialVersionUID = 11;

	private static final Logger LOGGER = Logger.getLogger(Preferences.class);

	public static final int SNAP_TO_LAYOUT_EVERY_GAME_STRATEGY = 0;

	public static final int REMEMBER_LAST_WINDOW_POSITION_STRATEGY = 1;

	private String lookAndFeelClassName = getLookAndFeelDefault();

	private int windowLayoutStrategy = REMEMBER_LAST_WINDOW_POSITION_STRATEGY;

	private Point rememberChatLocation = null;

	private Dimension rememberChatDimension = null;

	private Point rememberChessLocation = null;

	private Dimension rememberChessDimension = null;

	private Point rememberBugLocation = null;

	private Dimension rememberBugDimension = null;

	private Point rememberBugEarLocation = null;

	private Dimension rememberBugEarDimension = null;
	
	private Point rememberBugSeekLocation = null;

	private Dimension rememberBugSeekDimension = null;
	
	private Point rememberSeekLocation = null;

	private Dimension rememberSeekDimension = null;

	private int rememberBugSliderPosition = 0;
	
	private int rememberAvailablePartnersFilter = 0;

	BughousePreferences bughousePreferences;

	BoardPreferences boardPreferences;

	LoginPreferences loginPreferences;

	ChatPreferences chatPreferences;

	SpeechPreferences speechPreferences;

	LoggingPreferences loggingPreferences;
	
	SeekGraphPreferences seekGraphPreferences;

	Object growingRoom;

	boolean isSoundOn = true;

	public Preferences() {

	}

	private static String getLookAndFeelDefault() {
		String os = ResourceManagerFactory.getManager().getString("os", "os");
		String result = null;

		if (os.equals("osx")) {
			result = UIManager.getSystemLookAndFeelClassName();
		} else {
			result = UIManager.getCrossPlatformLookAndFeelClassName();
		}

		return result;
	}

	public Preferences getDeepCopy() {
		try {
			Preferences result = (Preferences) this.clone();
			result.bughousePreferences = (BughousePreferences) bughousePreferences
					.clone();
			result.chatPreferences = (ChatPreferences) chatPreferences.clone();
			result.boardPreferences = (BoardPreferences) boardPreferences
					.clone();
			result.loginPreferences = (LoginPreferences) loginPreferences
					.clone();
			result.speechPreferences = (SpeechPreferences) speechPreferences
					.clone();
			result.loggingPreferences = (LoggingPreferences) loggingPreferences
					.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			LOGGER.error(e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public BoardPreferences getBoardPreferences() {
		return boardPreferences;
	}

	public void setBoardPreferences(BoardPreferences boardPreferences) {
		this.boardPreferences = boardPreferences;
	}

	public BughousePreferences getBughousePreferences() {
		return bughousePreferences;
	}

	public void setBughousePreferences(BughousePreferences bughousePreferences) {
		this.bughousePreferences = bughousePreferences;
	}

	public ChatPreferences getChatPreferences() {
		return chatPreferences;
	}

	public void setChatPreferences(ChatPreferences chatPreferences) {
		this.chatPreferences = chatPreferences;
	}

	public LoginPreferences getLoginPreferences() {
		return loginPreferences;
	}

	public void setLoginPreferences(LoginPreferences loginPreferences) {
		this.loginPreferences = loginPreferences;
	}

	public String getLookAndFeelClassName() {
		return lookAndFeelClassName;
	}

	public void setLookAndFeelClassName(String lookAndFeelClassName) {
		this.lookAndFeelClassName = lookAndFeelClassName;
	}

	public SpeechPreferences getSpeechPreferences() {
		return speechPreferences;
	}

	public void setSpeechPreferences(SpeechPreferences speechPreferences) {
		this.speechPreferences = speechPreferences;
	}

	public int getWindowLayoutStrategy() {
		return windowLayoutStrategy;
	}

	public void setWindowLayoutStrategy(int windowLayoutStrategy) {
		this.windowLayoutStrategy = windowLayoutStrategy;
	}

	public Dimension getRememberBugDimension() {
		return rememberBugDimension;
	}

	public void setRememberBugDimension(Dimension rememberBugDimension) {
		this.rememberBugDimension = rememberBugDimension;
	}

	public Dimension getRememberBugEarDimension() {
		return rememberBugEarDimension;
	}

	public void setRememberBugEarDimension(Dimension rememberBugEarDimension) {
		this.rememberBugEarDimension = rememberBugEarDimension;
	}

	public Point getRememberBugEarLocation() {
		return rememberBugEarLocation;
	}

	public void setRememberBugEarLocation(Point rememberBugEarLocation) {
		this.rememberBugEarLocation = rememberBugEarLocation;
	}

	public Point getRememberBugLocation() {
		return rememberBugLocation;
	}

	public void setRememberBugLocation(Point rememberBugLocation) {
		this.rememberBugLocation = rememberBugLocation;
	}

	public Dimension getRememberChatDimension() {
		return rememberChatDimension;
	}

	public void setRememberChatDimension(Dimension rememberChatDimension) {
		this.rememberChatDimension = rememberChatDimension;
	}

	public Point getRememberChatLocation() {
		return rememberChatLocation;
	}

	public void setRememberChatLocation(Point rememberChatLocation) {
		this.rememberChatLocation = rememberChatLocation;
	}

	public Dimension getRememberChessDimension() {
		return rememberChessDimension;
	}

	public void setRememberChessDimension(Dimension rememberChessDimension) {
		this.rememberChessDimension = rememberChessDimension;
	}

	public Point getRememberChessLocation() {
		return rememberChessLocation;
	}

	public void setRememberChessLocation(Point rememberChessLocation) {
		this.rememberChessLocation = rememberChessLocation;
	}

	public int getRememberBugSliderPosition() {
		return rememberBugSliderPosition;
	}

	public void setRememberBugSliderPosition(int rememberBugSliderPosition) {
		this.rememberBugSliderPosition = rememberBugSliderPosition;
	}

	
	public Dimension getRememberBugSeekDimension() {
		return rememberBugSeekDimension;
	}

	public void setRememberBugSeekDimension(Dimension rememberBugSeekDimension) {
		this.rememberBugSeekDimension = rememberBugSeekDimension;
	}

	public Point getRememberBugSeekLocation() {
		return rememberBugSeekLocation;
	}

	public void setRememberBugSeekLocation(Point rememberBugSeekLocation) {
		this.rememberBugSeekLocation = rememberBugSeekLocation;
	}

	public Dimension getRememberSeekDimension() {
		return rememberSeekDimension;
	}

	public void setRememberSeekDimension(Dimension rememberSeekDimension) {
		this.rememberSeekDimension = rememberSeekDimension;
	}

	public Point getRememberSeekLocation() {
		return rememberSeekLocation;
	}

	public void setRememberSeekLocation(Point rememberSeekLocation) {
		this.rememberSeekLocation = rememberSeekLocation;
	}

	public int getRememberAvailablePartnersFilter() {
		return rememberAvailablePartnersFilter;
	}

	public void setRememberAvailablePartnersFilter(
			int rememberAvailablePartnersFilter) {
		this.rememberAvailablePartnersFilter = rememberAvailablePartnersFilter;
	}

	public LoggingPreferences getLoggingPreferences() {
		return loggingPreferences;
	}

	public void setLoggingPreferences(LoggingPreferences loggingPreferences) {
		this.loggingPreferences = loggingPreferences;
	}

	public boolean isSoundOn() {
		return isSoundOn;
	}

	public void setSoundOn(boolean isSoundOn) {
		this.isSoundOn = isSoundOn;
	}
	
	public void setSeekGraphPreferences(SeekGraphPreferences seekGraphPreferences) {
		this.seekGraphPreferences = seekGraphPreferences;
	}
	
	public SeekGraphPreferences getSeekGraphPreferences() {
		return seekGraphPreferences;
	}
	
	
}