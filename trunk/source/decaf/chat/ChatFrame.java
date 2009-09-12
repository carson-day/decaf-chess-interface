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
package decaf.chat;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;

import decaf.gui.GUIManager;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;

public class ChatFrame extends JFrame implements Preferenceable {
	private ChatPanel chatPanel;

	private Preferences preferences;

	public ChatFrame(Preferences preferences, String title) {
		super(title);
		this.preferences = preferences;
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		container.add(chatPanel = new ChatPanel(preferences),
				BorderLayout.CENTER);
		setIconImage(GUIManager.DECAF_ICON);
	}

	public ChatPanel getChatPanel() {
		return chatPanel;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	@Override
	public void requestFocus() {
		chatPanel.getInputField().requestFocusInWindow();
	}

	public void setChatPanel(ChatPanel chatPanel) {
		this.chatPanel = chatPanel;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		chatPanel.setPreferences(preferences);

	}

}