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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingConstants;

import decaf.util.GUIUtil;
import decaf.util.TextProperties;

public class ChatPreferences implements Cloneable, Serializable {
	private static final long serialVersionUID = 11;

	public static final int TABS_ON_LEFT = SwingConstants.LEFT;

	public static final int TABS_ON_RIGHT = SwingConstants.RIGHT;

	public static final int TABS_ON_TOP = SwingConstants.TOP;

	public static final int TABS_ON_BOTTOM = SwingConstants.BOTTOM;

	public static final int LEFT_CLICK_POPUP = 1;

	public static final int RIGHT_CLICK_POPUP = 2;

	public static final int MIDDLE_CLICK_POPUP = 3;

	public static ChatPreferences getDefault() {
		ChatPreferences result = new ChatPreferences();
		result.channelToProperties.put(new Integer(1), new TextProperties(
				new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 15),
				Color.ORANGE, Color.black));
		result.channelToProperties.put(new Integer(4), new TextProperties(
				new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 15),
				Color.GREEN, Color.black));
		result.channelToProperties.put(new Integer(50), new TextProperties(
				new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 15), Color.PINK,
				Color.black));
		result.channelToProperties.put(new Integer(53), new TextProperties(
				new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 15),
				Color.MAGENTA, Color.black));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getDefaultScreenDevice().getDefaultConfiguration());

		int totalWidth = screenSize.width - insets.left - insets.right;
		int totalHeight = screenSize.height - insets.top - insets.bottom;

		result.chatWindowPoint = new Point(insets.left, insets.top
				+ (int) (totalHeight * .7) + 1);
		result.chatWindowDimension = new Dimension(totalWidth,
				(int) (totalHeight * .3));

		return result;
	}

	private int popupMenuClick = RIGHT_CLICK_POPUP;

	private int consoleTabBufferSize = 1000000;

	private int chatTabBufferSize = 1000000;

	private Color telnetPanelBackground = Color.BLACK;

	private Dimension chatWindowDimension = null;

	private Point chatWindowPoint = null;

	private HashMap<Integer, TextProperties> channelToProperties = new HashMap<Integer, TextProperties>();

	private TextProperties shoutTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 14), Color.gray, Color.black);

	private TextProperties cshoutTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 14), Color.gray, Color.black);

	private TextProperties kibitzTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 16), Color.white, Color.black);

	private TextProperties whisperTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 16), Color.white, Color.black);

	private TextProperties ptellTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 16), Color.yellow,
			Color.black);

	private TextProperties tellTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 16), Color.yellow,
			Color.black);

	private TextProperties notificationTextProperties = new TextProperties(
			new Font(GUIUtil.getDefaultFont(), Font.PLAIN, 16), Color.red,
			Color.black);

	private TextProperties matchTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 16), Color.red, Color.black);

	private TextProperties defaultTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 15), Color.gray, Color.black);

	private TextProperties alertTextProperties = new TextProperties(new Font(
			GUIUtil.getDefaultFont(), Font.PLAIN, 17), Color.red, Color.black);

	private boolean isPreventingIdleLogout = false;

	private boolean disableTabs = false;

	private boolean preprendTellToTabs = true;

	private boolean isShowingSeekGraphButton = true;

	private boolean isShowingBugSeek = true;

	private boolean isShowingBugOpenCheckbox = true;

	private int tabOrientation = TABS_ON_LEFT;

	private List<Integer> channelTabs = new LinkedList<Integer>();

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public TextProperties getAlertTextProperties() {
		return alertTextProperties;
	}

	public TextProperties getChannelProperties(int channel) {
		TextProperties result = channelToProperties.get(new Integer(channel));
		if (result == null) {
			result = getDefaultTextProperties();
		}
		return result;
	}

	public int[] getChannelsThatHaveProperties() {
		List<Integer> result = new LinkedList<Integer>(channelToProperties
				.keySet());
		Collections.sort(result);

		int[] resultInt = new int[result.size()];

		for (int i = 0; i < result.size(); i++) {
			resultInt[i] = result.get(i).intValue();
		}
		return resultInt;
	}

	public List<Integer> getChannelTabs() {
		return channelTabs;
	}

	public int getChatTabBufferSize() {
		return chatTabBufferSize;
	}

	public Dimension getChatWindowDimension() {
		return chatWindowDimension;
	}

	public Point getChatWindowPoint() {
		return chatWindowPoint;
	}

	public int getConsoleTabBufferSize() {
		return consoleTabBufferSize;
	}

	public void getConsoleTabBufferSize(int consoleTabBufferSize) {
		this.consoleTabBufferSize = consoleTabBufferSize;
	}

	public TextProperties getCshoutTextProperties() {
		return cshoutTextProperties;
	}

	public TextProperties getDefaultTextProperties() {
		return defaultTextProperties;
	}

	public TextProperties getKibitzTextProperties() {
		return kibitzTextProperties;
	}

	public TextProperties getMatchTextProperties() {
		return matchTextProperties;
	}

	public TextProperties getNotificationTextProperties() {
		return notificationTextProperties;
	}

	public int getPopupMenuClick() {
		return popupMenuClick;
	}

	public TextProperties getPtellTextProperties() {
		return ptellTextProperties;
	}

	public TextProperties getShoutTextProperties() {
		return shoutTextProperties;
	}

	public int getTabOrientation() {
		return tabOrientation;
	}

	public TextProperties getTellTextProperties() {
		return tellTextProperties;
	}

	public Color getTelnetPanelBackground() {
		return telnetPanelBackground;
	}

	public TextProperties getWhisperTextProperties() {
		return whisperTextProperties;
	}

	public boolean isDisableTabs() {
		return disableTabs;
	}

	public boolean isPreprendTellToTabs() {
		return preprendTellToTabs;
	}

	public boolean isPreventingIdleLogout() {
		return isPreventingIdleLogout;
	}

	public boolean isShowingBugOpenCheckbox() {
		return isShowingBugOpenCheckbox;
	}

	public boolean isShowingBugSeekButton() {
		return isShowingBugSeek;
	}

	public boolean isShowingSeekGraphButton() {
		return isShowingSeekGraphButton;
	}

	public void setAlertTextProperties(TextProperties alertTextProperties) {
		this.alertTextProperties = alertTextProperties;
	}

	public void setChannelProperties(int channel, TextProperties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("properties cant be null");
		}
		channelToProperties.put(new Integer(channel), properties);
	}

	public void setChannelTabs(List<Integer> channelTabs) {
		this.channelTabs = channelTabs;
	}

	public void setChatTabBufferSize(int chatTabBufferSize) {
		this.chatTabBufferSize = chatTabBufferSize;
	}

	public void setChatWindowDimension(Dimension chatWindowDimension) {
		this.chatWindowDimension = chatWindowDimension;
	}

	public void setChatWindowPoint(Point chatWindowPoint) {
		this.chatWindowPoint = chatWindowPoint;
	}

	public void setCshoutTextProperties(TextProperties cshoutTextProperties) {
		this.cshoutTextProperties = cshoutTextProperties;
	}

	public void setDefaultTextProperties(TextProperties defaultTextProperties) {
		this.defaultTextProperties = defaultTextProperties;
	}

	public void setDisableTabs(boolean disableTabs) {
		this.disableTabs = disableTabs;
	}

	public void setKibitzTextProperties(TextProperties kibitzTextProperties) {
		this.kibitzTextProperties = kibitzTextProperties;
	}

	public void setMatchTextProperties(TextProperties matchTextProperties) {
		this.matchTextProperties = matchTextProperties;
	}

	public void setNotificationTextProperties(
			TextProperties notificationTextProperties) {
		this.notificationTextProperties = notificationTextProperties;
	}

	public void setPopupMenuClick(int popupMenuClick) {
		this.popupMenuClick = popupMenuClick;
	}

	public void setPreprendTellToTabs(boolean preprendTellToTabs) {
		this.preprendTellToTabs = preprendTellToTabs;
	}

	public void setPreventingIdleLogout(boolean isPreventingIdleLogout) {
		this.isPreventingIdleLogout = isPreventingIdleLogout;
	}

	public void setPtellTextProperties(TextProperties ptellTextProperties) {
		this.ptellTextProperties = ptellTextProperties;
	}

	public void setShoutTextProperties(TextProperties shoutTextProperties) {
		this.shoutTextProperties = shoutTextProperties;
	}

	public void setShowingBugOpenCheckbox(boolean isShowingBugOpenCheckbox) {
		this.isShowingBugOpenCheckbox = isShowingBugOpenCheckbox;
	}

	public void setShowingBugSeekButton(boolean isShowingBugSeek) {
		this.isShowingBugSeek = isShowingBugSeek;
	}

	public void setShowingSeekGraphButton(boolean isShowingSeekGraphButton) {
		this.isShowingSeekGraphButton = isShowingSeekGraphButton;
	}

	public void setTabOrientation(int tabLocation) {
		this.tabOrientation = tabLocation;
	}

	public void setTellTextProperties(TextProperties tellTextProperties) {
		this.tellTextProperties = tellTextProperties;
	}

	public void setTelnetPanelBackground(Color telnetPanelBackground) {
		this.telnetPanelBackground = telnetPanelBackground;
	}

	public void setWhisperTextProperties(TextProperties whisperTextProperties) {
		this.whisperTextProperties = whisperTextProperties;
	}

}
