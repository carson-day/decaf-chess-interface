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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import decaf.gui.util.TextProperties;

public class ChatPreferences implements Cloneable, Serializable {
	private int telnetBufferSize = 5000000;

	private Color telnetPanelBackground = Color.BLACK;

	private Dimension chatWindowDimension = new Dimension(900, 650);

	private Point chatWindowPoint = new Point(25, 25);

	private HashMap<Integer, TextProperties> channelToProperties = new HashMap<Integer, TextProperties>();

	private TextProperties shoutTextProperties = new TextProperties(new Font(
			"Monospaced", Font.BOLD, 14), Color.green, Color.black);

	private TextProperties cshoutTextProperties = new TextProperties(new Font(
			"Monospaced", Font.BOLD, 14), Color.green, Color.black);

	private TextProperties kibitzTextProperties = new TextProperties(new Font(
			"Monospaced", Font.BOLD, 16), Color.white, Color.black);

	private TextProperties whisperTextProperties = new TextProperties(new Font(
			"Monospaced", Font.BOLD, 16), Color.white, Color.black);

	private TextProperties ptellTextProperties = new TextProperties(new Font(
			"Monospaced", Font.BOLD, 16), Color.yellow, Color.black);

	private TextProperties tellTextProperties = new TextProperties(new Font(
			"Monospaced", Font.PLAIN, 16), Color.yellow, Color.black);

	private TextProperties notificationTextProperties = new TextProperties(
			new Font("Monospaced", Font.PLAIN, 16), Color.red, Color.black);

	private TextProperties matchTextProperties = new TextProperties(new Font(
			"Monospaced", Font.PLAIN, 16), Color.red, Color.black);

	private TextProperties defaultTextProperties = new TextProperties(new Font(
			"Monospaced", 0, 15), Color.gray, Color.black);

	private TextProperties alertTextProperties = new TextProperties(new Font(
			"Monospaced", 0, 17), Color.red, Color.black);

	private boolean isSmartScrollEnabled = true;

	private boolean isPreventingIdleLogout = false;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static ChatPreferences getDefault() {
		ChatPreferences result = new ChatPreferences();
		result.channelToProperties
				.put(new Integer(1), new TextProperties(new Font("Monospaced",
						Font.BOLD, 15), Color.blue, Color.black));
		result.channelToProperties.put(new Integer(24), new TextProperties(
				new Font("Monospaced", Font.BOLD, 15), Color.DARK_GRAY,
				Color.black));
		result.channelToProperties
				.put(new Integer(50), new TextProperties(new Font("Monospaced",
						Font.BOLD, 15), Color.PINK, Color.black));
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

	public TextProperties getChannelProperties(int channel) {
		TextProperties result = channelToProperties.get(new Integer(channel));
		if (result == null) {
			result = getDefaultTextProperties();
		}
		return result;
	}

	public void setChannelProperties(int channel, TextProperties properties) {
		if (properties == null) {
			throw new IllegalArgumentException("properties cant be null");
		}
		channelToProperties.put(new Integer(channel), properties);
	}

	public TextProperties getAlertTextProperties() {
		return alertTextProperties;
	}

	public void setAlertTextProperties(TextProperties alertTextProperties) {
		this.alertTextProperties = alertTextProperties;
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

	public TextProperties getCshoutTextProperties() {
		return cshoutTextProperties;
	}

	public void setCshoutTextProperties(TextProperties cshoutTextProperties) {
		this.cshoutTextProperties = cshoutTextProperties;
	}

	public TextProperties getDefaultTextProperties() {
		return defaultTextProperties;
	}

	public void setDefaultTextProperties(TextProperties defaultTextProperties) {
		this.defaultTextProperties = defaultTextProperties;
	}

	public boolean isPreventingIdleLogout() {
		return isPreventingIdleLogout;
	}

	public void setPreventingIdleLogout(boolean isPreventingIdleLogout) {
		this.isPreventingIdleLogout = isPreventingIdleLogout;
	}

	public boolean isSmartScrollEnabled() {
		return isSmartScrollEnabled;
	}

	public void setSmartScrollEnabled(boolean isSmartScrollEnabled) {
		this.isSmartScrollEnabled = isSmartScrollEnabled;
	}

	public TextProperties getKibitzTextProperties() {
		return kibitzTextProperties;
	}

	public void setKibitzTextProperties(TextProperties kibitzTextProperties) {
		this.kibitzTextProperties = kibitzTextProperties;
	}

	public TextProperties getMatchTextProperties() {
		return matchTextProperties;
	}

	public void setMatchTextProperties(TextProperties matchTextProperties) {
		this.matchTextProperties = matchTextProperties;
	}

	public TextProperties getNotificationTextProperties() {
		return notificationTextProperties;
	}

	public void setNotificationTextProperties(
			TextProperties notificationTextProperties) {
		this.notificationTextProperties = notificationTextProperties;
	}

	public TextProperties getPtellTextProperties() {
		return ptellTextProperties;
	}

	public void setPtellTextProperties(TextProperties ptellTextProperties) {
		this.ptellTextProperties = ptellTextProperties;
	}

	public TextProperties getShoutTextProperties() {
		return shoutTextProperties;
	}

	public void setShoutTextProperties(TextProperties shoutTextProperties) {
		this.shoutTextProperties = shoutTextProperties;
	}

	public TextProperties getTellTextProperties() {
		return tellTextProperties;
	}

	public void setTellTextProperties(TextProperties tellTextProperties) {
		this.tellTextProperties = tellTextProperties;
	}

	public int getTelnetBufferSize() {
		return telnetBufferSize;
	}

	public void setTelnetBufferSize(int telnetBufferSize) {
		this.telnetBufferSize = telnetBufferSize;
	}

	public Color getTelnetPanelBackground() {
		return telnetPanelBackground;
	}

	public void setTelnetPanelBackground(Color telnetPanelBackground) {
		this.telnetPanelBackground = telnetPanelBackground;
	}

	public TextProperties getWhisperTextProperties() {
		return whisperTextProperties;
	}

	public void setWhisperTextProperties(TextProperties whisperTextProperties) {
		this.whisperTextProperties = whisperTextProperties;
	}

}
