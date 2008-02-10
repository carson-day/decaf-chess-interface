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
package decaf.gui.widgets;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.JWindow;

import org.apache.log4j.Logger;

import decaf.gui.ChessAreaControllerBase;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.thread.ThreadManager;
import decaf.util.ToolbarUtil;

/**
 * A ToolBar containing buttons for chess areas. Pass into the constructor the
 * Mode of the toolbar to get the desired set of buttons.
 */
public class ChessAreaToolbar extends JToolBar implements Preferenceable,
		Disposable {

	private static final Logger LOGGER = Logger
			.getLogger(ChessAreaToolbar.class);

	private JComboBox autoPromotionCombo = new JComboBox(new Object[] { "Off",
			"Queen", "Knight", "Bishop", "Rook" });

	public static final int EXAMINING_MODE = 0;

	public static final int PLAYING_CHESS_MODE = 1;

	public static final int PLAYING_BUGHOUSE_MODE = 2;

	public static final int OBSERVING_MODE = 3;

	public static final int OBS_BUG_MODE = 4;

	private Preferences preferences;

	private List<ToolbarUtil.ToolbarButton> buttons;

	private ChessAreaToolbar thisToolbar = this;

	private Container currentContentPane;

	// DONT USE ME I CRASH WHEN YOU CHANGE THE MOVE LIST SPLIT PLANE
	private JButton fullScreenButton = new JButton(new AbstractAction(
			"Full Screen Mode") {
		public void actionPerformed(ActionEvent e) {
			ThreadManager.execute(new Runnable() {
				public void run() {
					try {
						thisToolbar.remove(fullScreenButton);
						thisToolbar.add(exitFullScreenButton);
						thisToolbar.invalidate();
						JWindow window = new JWindow(controller.getFrame());
						currentContentPane = controller.getFrame()
								.getContentPane();
						controller.getFrame().remove(currentContentPane);
						window.setContentPane(currentContentPane);
						GraphicsEnvironment.getLocalGraphicsEnvironment()
								.getDefaultScreenDevice().setFullScreenWindow(
										window);
						window.validate();
					} catch (Exception ex) {
						LOGGER.error(ex);
						GraphicsEnvironment.getLocalGraphicsEnvironment()
								.getDefaultScreenDevice().setFullScreenWindow(
										null);
						controller.getFrame()
								.setContentPane(currentContentPane);
						controller.getFrame().validate();
					}
				}
			});
		}
	});

	// DONT USE ME I CRASH WHEN YOU CHANGE THE MOVE LIST SPLIT PLANE
	private JButton exitFullScreenButton = new JButton(new AbstractAction(
			"Exit Full Screen Mode") {
		public void actionPerformed(ActionEvent e) {
			ThreadManager.execute(new Runnable() {
				public void run() {
					GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getDefaultScreenDevice().setFullScreenWindow(null);
					controller.getFrame().setContentPane(currentContentPane);
					thisToolbar.remove(exitFullScreenButton);
					thisToolbar.add(fullScreenButton);
					thisToolbar.invalidate();
					controller.getFrame().validate();
				}
			});
		}
	});

	private JButton moveListButton = new JButton(new AbstractAction(
			"Show Move List") {
		public void actionPerformed(ActionEvent e) {

			if (controller.getChessArea().getMoveList().isVisible()) {
				controller.hideMoveList();
			} else {
				controller.showMoveList();
			}
		}
	});

	private ChessAreaControllerBase controller;

	public void dispose() {
		removeAll();
		preferences = null;
	}

	/**
	 * @param controller
	 *            the controller using this toolbar.
	 * @param mode
	 *            One of the *_MODE constants in this class. ClearPremove Button
	 *            is disabled by default.
	 */
	public ChessAreaToolbar(final Preferences preferences,
			ChessAreaControllerBase controller) {
		super("Speed Button Toolbar", JToolBar.HORIZONTAL);
		this.controller = controller;
		long startTime = System.currentTimeMillis();
		setFloatable(false);
		setPreferences(preferences);
		add(moveListButton);
		addSeparator(new Dimension(3, 1));

		String propFile = null;

		if (!controller.isActive() && controller.isPlaying()) {
			propFile = "AfterPlayingGameToolbar";
		} else if (controller.isExamining()) {
			propFile = "ExamineGameToolbar";
		} else if (controller.isPlaying() && !controller.isBughouse()) {
			propFile = "PlayingGameToolbar";
		} else if (controller.isPlaying() && controller.isBughouse()) {
			propFile = "PlayingBugToolbar";
		} else if (!controller.isBughouse()) {
			propFile = "ObsGameToolbar";
		} else {
			propFile = "ObsBugToolbar";
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initializing toolbar with properties: " + propFile);
		}

		buttons = ToolbarUtil.addGameToolbarButtonsFromProperties(propFile,
				this, 12, 3, controller);
		setClearPremoveEnabled(false);

		if (controller.isPlaying() && controller.isActive()) {
			autoPromotionCombo.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent arg0) {
					String item = (String) autoPromotionCombo.getSelectedItem();

					if (item.equals("Off")) {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_PROMOTE_DISABLED);
					} else if (item.equals("Queen")) {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_QUEEN);
					} else if (item.equals("Knight")) {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_KNIGHT);
					} else if (item.equals("Bishop")) {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_BISHOP);
					} else if (item.equals("Rook")) {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_ROOK);
					} else {
						throw new IllegalStateException(
								"Invalid item selected: " + item);
					}
				}
			});

			autoPromotionCombo.requestFocusInWindow();

			if (preferences.getBoardPreferences().getAutoPromotionMode() != BoardPreferences.AUTO_PROMOTE_DISABLED) {
				autoPromotionCombo.setSelectedIndex(1);
			} else {
				autoPromotionCombo.setSelectedIndex(0);
			}
			addSeparator(new Dimension(12, 1));
			add(new JLabel("Promote:"));
			add(autoPromotionCombo);
			autoPromotionCombo.setMaximumSize(new Dimension(100, 300));
		}

		addSeparator(new Dimension(12, 1));

		// DONT USE ME I CRASH WHEN YOU CHANGE THE MOVE LIST SPLIT PLANE

		/*
		 * if (GraphicsEnvironment.getLocalGraphicsEnvironment()
		 * .getDefaultScreenDevice().isFullScreenSupported()) {
		 * add(fullScreenButton); }
		 */

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created ChessAreaToolbar in: "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	public void setButtonToHideMoveList() {
		moveListButton.setText("Hide Move List");
		moveListButton.invalidate();
		thisToolbar.validate();
	}

	public void setButtonToShowMoveList() {
		moveListButton.setText("Show Move List");
		moveListButton.invalidate();
		thisToolbar.validate();
	}

	public void requestComboBoxFocus() {
		setRequestFocusEnabled(true);
		autoPromotionCombo.requestFocus();
	}

	public void setClearPremoveEnabled(boolean isClearPremoveEnabled) {

		for (ToolbarUtil.ToolbarButton button : buttons) {
			if (button.getCommand().equalsIgnoreCase("$CLEAR_PREMOVE")) {
				button.setEnabled(isClearPremoveEnabled);
			}
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences options) {
		this.preferences = options;
	}
}