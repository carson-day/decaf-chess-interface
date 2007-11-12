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
package decaf.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import decaf.Decaf;
import decaf.event.EventService;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.pref.PreferencesDialog;
import decaf.gui.util.User;

public class ChatFrame extends JFrame implements Preferenceable {
	private ChatFrame thisFrame = this;

	private ChatPanel chatPanel;

	private Preferences preferences;

	private Action showPreferencesAction = new AbstractAction("Preferences") {
		public void actionPerformed(ActionEvent event) {
			PreferencesDialog.showPreferencesDialog(thisFrame);
		}
	};

	private Action closeNonPlayingGames = new AbstractAction(
			"Close all games I am not playing") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().removeAllNonPlayingControllers();
		}
	};

	private Action closeInactiveGames = new AbstractAction(
			"Close all inactive games") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().removeAllInactiveControllers();
		}
	};

	private Action reconnectAction = new AbstractAction("Reconnect") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().dispose();
			User.reset();
			EventService.getInstance().unsubscribeAll();

			Thread myThread = new Thread(new Runnable() {
				public void run() {
					new Decaf();
				}
			});
			myThread.start();
		}
	};

	private Action exitAction = new AbstractAction("Exit") {
		public void actionPerformed(ActionEvent event) {
			// if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(
			// component, "Are you sure you want to quit?")) {
			System.exit(1);
			// }
		}
	};

	private Action snapBugLayoutAction = new AbstractAction(
			"Snap to Bughouse Layout") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().snapToBug();
		}
	};

	private Action snapChessLayoutAction = new AbstractAction(
			"Snap to Chess Layout") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().snapToChess();
		}
	};

	private Action snapChatLayoutAction = new AbstractAction(
			"Snap to Chat Layout") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().snapToChatFrame();
		}
	};

	private Action saveBugLayoutAction = new AbstractAction(
			"Save Bughouse Layout") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().saveBughoueLayout();
		}
	};

	private Action saveChessLayoutAction = new AbstractAction(
			"Save Chess Layout") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().saveChessLayout();
		}
	};

	private Action saveChatLayoutAction = new AbstractAction("Save Chat Layout") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().saveChatLayout();
		}
	};

	public ChatFrame(Preferences preferences, String title) {
		super(title);
		this.preferences = preferences;
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		intializeMenus();
		container.add(chatPanel = new ChatPanel(preferences, true),
				BorderLayout.CENTER);
	}

	private void intializeMenus() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(reconnectAction);
		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		menuBar.add(fileMenu);

		JMenu prefMenu = new JMenu("Configure");
		prefMenu.add(showPreferencesAction);
		menuBar.add(prefMenu);

		JMenu layoutMenu = new JMenu("Window");
		layoutMenu.add(snapChatLayoutAction);
		layoutMenu.add(snapChessLayoutAction);
		layoutMenu.add(snapBugLayoutAction);
		layoutMenu.addSeparator();
		layoutMenu.add(saveChatLayoutAction);
		layoutMenu.add(saveChessLayoutAction);
		layoutMenu.add(saveBugLayoutAction);
		layoutMenu.addSeparator();
		layoutMenu.add(closeNonPlayingGames);
		layoutMenu.add(closeInactiveGames);
		menuBar.add(layoutMenu);

		setJMenuBar(menuBar);
	}

	public void updateMenus() {
		if (GUIManager.getInstance().isBughouseLayout()) {
			saveBugLayoutAction.setEnabled(true);
		} else {
			saveBugLayoutAction.setEnabled(false);
		}

		if (GUIManager.getInstance().isChessLayout()) {
			saveChessLayoutAction.setEnabled(true);
		} else {
			saveChessLayoutAction.setEnabled(false);
		}
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		chatPanel.setPreferences(preferences);

	}

	public Preferences getPreferences() {
		return preferences;
	}

	public ChatPanel getChatPanel() {
		return chatPanel;
	}

	public void setChatPanel(ChatPanel chatPanel) {
		this.chatPanel = chatPanel;
	}

}