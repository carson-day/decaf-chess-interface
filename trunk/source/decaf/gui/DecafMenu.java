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
package decaf.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import decaf.Decaf;
import decaf.dialog.AboutDialog;
import decaf.dialog.ProfileDialog;
import decaf.dialog.QuestionDialog;
import decaf.event.EventService;
import decaf.gui.pref.PreferencesDialog;

public class DecafMenu extends JMenuBar {
	private Action showPreferencesAction = new AbstractAction("Preferences") {
		public void actionPerformed(ActionEvent event) {

			if (dialog == null) {
				dialog = new PreferencesDialog(null);
				dialog.setModal(true);
				dialog.setVisible(true);
				// It takes up to much memory so just null it out after it
				// closed
				dialog.setEnabled(false);
				dialog.dispose();
				dialog.removeAll();
				dialog = null;
			} else if (!dialog.isVisible()) {
				dialog.setVisible(true);
				dialog.requestFocus();
				dialog.toFront();
			} else {
				dialog.requestFocus();
				dialog.toFront();
			}
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

			try {
				GUIManager.getInstance().dispose();
			} catch (Exception e) {
			}

			// Needed so the user catches the new name (if it changes) on the
			// reconnect since the event service
			// will become stale.
			User.reset();

			try {
				EventService.getInstance().dispose();
			} catch (Exception e) {
			}

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					new Decaf();
				}
			});

		}
	};

	private Action aboutAction = new AbstractAction("About") {
		public void actionPerformed(ActionEvent event) {
			if (aboutDialog == null) {
				aboutDialog = new AboutDialog();
				aboutDialog.setModal(true);
				aboutDialog.setVisible(true);
				// It takes up to much memory so just null it out after it
				// closed.
				aboutDialog = null;
			} else if (!aboutDialog.isVisible()) {
				aboutDialog.setVisible(true);
				aboutDialog.requestFocus();
				aboutDialog.toFront();
			} else {
				aboutDialog.requestFocus();
				aboutDialog.toFront();
			}
		}
	};

	private Action exitAction = new AbstractAction("Exit") {
		public void actionPerformed(ActionEvent event) {
			System.exit(1);
		}
	};

	private Action profileAction = new AbstractAction("Resources Profile") {
		public void actionPerformed(ActionEvent event) {
			new ProfileDialog();
		}
	};

	private Action fillScreenAction = new AbstractAction("Fill Screen") {
		public void actionPerformed(ActionEvent event) {
			GUIManager.getInstance().fillScreen();
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

	private Action askQuestionAction = new AbstractAction("Ask Question") {
		public void actionPerformed(ActionEvent event) {
			QuestionDialog dialog = new QuestionDialog();
			dialog.setModal(true);
			dialog.setVisible(true);
		}
	};

	public DecafMenu() {
		super();
		boolean isApplet = GUIManager.getInstance().isApplet();
		JMenu fileMenu = new JMenu("File");
		if (!isApplet) {
			fileMenu.add(reconnectAction);
			fileMenu.add(profileAction);
			fileMenu.addSeparator();
			fileMenu.add(exitAction);
		}
		add(fileMenu);

		JMenu prefMenu = new JMenu("Configure");
		prefMenu.add(showPreferencesAction);
		add(prefMenu);

		JMenu layoutMenu = new JMenu("Layout");
		layoutMenu.add(fillScreenAction);
		if (!isApplet) {
			layoutMenu.addSeparator();
			layoutMenu.add(saveChatLayoutAction);
			layoutMenu.add(saveChessLayoutAction);
			layoutMenu.add(saveBugLayoutAction);
			layoutMenu.addSeparator();
		}
		layoutMenu.add(snapChatLayoutAction);
		layoutMenu.add(snapChessLayoutAction);
		layoutMenu.add(snapBugLayoutAction);
		add(layoutMenu);

		JMenu windowMenu = new JMenu("Window");
		windowMenu.add(closeNonPlayingGames);
		windowMenu.add(closeInactiveGames);
		add(windowMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(askQuestionAction);
		helpMenu.add(aboutAction);
		add(helpMenu);
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

	private static PreferencesDialog dialog;

	private static AboutDialog aboutDialog;
}
