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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.ToolbarUtil;
import decaf.gui.widgets.ImageChessSet;

/**
 * A ToolBar containing buttons for chess areas. Pass into the constructor the
 * Mode of the toolbar to get the desired set of buttons.
 */
public class ChessAreaToolbar extends JToolBar implements Preferenceable,
		Disposable {

	private static final Logger LOGGER = Logger
			.getLogger(ChessAreaToolbar.class);

	private static final int ICON_HEIGHT = 20;

	private static final int ICON_WIDTH = 20;

	private static final int ICON_HINTS = Image.SCALE_SMOOTH;

	private static final ImageIcon WHITE_KNIGHT = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.WKNIGHT.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private static final ImageIcon WHITE_BISHOP = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.WBISHOP.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private static final ImageIcon WHITE_QUEEN = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.WQUEEN.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private static final ImageIcon WHITE_ROOK = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.WROOK.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private static final ImageIcon BLACK_KNIGHT = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.BKNIGHT.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private static final ImageIcon BLACK_BISHOP = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.BBISHOP.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private static final ImageIcon BLACK_QUEEN = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.BQUEEN.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private static final ImageIcon BLACK_ROOK = new ImageIcon(ImageChessSet
			.getChessPieceImage("./resources/SET.BOOK.BROOK.BMP")
			.getScaledInstance(ICON_WIDTH, ICON_HEIGHT, ICON_HINTS));

	private JCheckBox autoQueenCheckbox;

	private JCheckBox autoKnightCheckbox;

	private JCheckBox autoRookCheckbox;

	private JCheckBox autoBishopCheckbox;

	private ItemListener checkboxActionListener = new ItemListener() {
		private boolean isIgnoring = false;

		public void itemStateChanged(ItemEvent e) {
			Object source = e.getItemSelectable();

			if (!isIgnoring) {
				isIgnoring = true;

				if (source == autoQueenCheckbox) {
					boolean isChecked = autoQueenCheckbox.isSelected();
					if (isChecked) {
						autoKnightCheckbox.setSelected(false);
						autoRookCheckbox.setSelected(false);
						autoBishopCheckbox.setSelected(false);
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_QUEEN);
					} else {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_PROMOTE_DISABLED);
					}
				} else if (source == autoKnightCheckbox) {
					boolean isChecked = autoKnightCheckbox.isSelected();
					if (isChecked) {
						autoQueenCheckbox.setSelected(false);
						autoRookCheckbox.setSelected(false);
						autoBishopCheckbox.setSelected(false);
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_KNIGHT);
					} else {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_PROMOTE_DISABLED);
					}
				} else if (source == autoRookCheckbox) {
					boolean isChecked = autoRookCheckbox.isSelected();
					if (isChecked) {
						autoKnightCheckbox.setSelected(false);
						autoQueenCheckbox.setSelected(false);
						autoBishopCheckbox.setSelected(false);
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_ROOK);
					} else {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_PROMOTE_DISABLED);
					}
				}

				else if (source == autoBishopCheckbox) {
					boolean isChecked = autoBishopCheckbox.isSelected();
					if (isChecked) {
						autoKnightCheckbox.setSelected(false);
						autoRookCheckbox.setSelected(false);
						autoQueenCheckbox.setSelected(false);
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_BISHOP);
					} else {
						preferences.getBoardPreferences().setAutoPromotionMode(
								BoardPreferences.AUTO_PROMOTE_DISABLED);
					}
				}

				isIgnoring = false;
			}
		}
	};

	public static final int EXAMINING_MODE = 0;

	public static final int PLAYING_CHESS_MODE = 1;

	public static final int PLAYING_BUGHOUSE_MODE = 2;

	public static final int OBSERVING_MODE = 3;

	public static final int OBS_BUG_MODE = 4;

	private int mode;

	private Preferences preferences;

	private static EventService eventService = EventService.getInstance();

	private ChessAreaControllerBase controller;

	private List<ToolbarUtil.ToolbarButton> buttons;

	public void dispose() {
		removeAll();
		preferences = null;
		controller = null;
	}

	/**
	 * @param controller
	 *            the controller using this toolbar.
	 * @param mode
	 *            One of the *_MODE constants in this class. ClearPremove Button
	 *            is disabled by default.
	 */
	public ChessAreaToolbar(Preferences preferences,
			ChessAreaControllerBase controller) {
		super("Speed Button Toolbar", JToolBar.HORIZONTAL);
		long startTime = System.currentTimeMillis();
		setFloatable(false);

		this.mode = controller.isExamining() ? EXAMINING_MODE : controller
				.isPlaying()
				&& controller.isBughouse() ? PLAYING_BUGHOUSE_MODE : controller
				.isPlaying() ? PLAYING_CHESS_MODE : OBSERVING_MODE;

		if (controller.isBughouse() && !controller.isPlaying()) {
			this.mode = OBS_BUG_MODE;
		}

		this.controller = controller;
		setPreferences(preferences);

		String propFile = null;

		if (!controller.isActive() && controller.isPlaying()) {
			propFile = "AfterPlayingGameToolbar";
		} else if (mode == EXAMINING_MODE) {
			propFile = "ExamineGameToolbar";
		} else if (mode == PLAYING_CHESS_MODE) {
			propFile = "PlayingGameToolbar";
		} else if (mode == PLAYING_BUGHOUSE_MODE) {
			propFile = "PlayingBugToolbar";
		} else if (mode == OBSERVING_MODE) {
			propFile = "ObsGameToolbar";
		} else if (mode == OBS_BUG_MODE) {
			propFile = "ObsBugToolbar";
		}

		buttons = ToolbarUtil.addGameToolbarButtonsFromProperties(propFile,
				this, 12, 3, controller);
		setClearPremoveEnabled(false);

		if (controller.isActive()
				&& (mode == PLAYING_BUGHOUSE_MODE || mode == PLAYING_CHESS_MODE)) {

			boolean isPlayerWhite = controller.isUserWhite();

			autoQueenCheckbox = new JCheckBox();
			autoKnightCheckbox = new JCheckBox();
			autoBishopCheckbox = new JCheckBox();
			autoRookCheckbox = new JCheckBox();

			if (preferences.getBoardPreferences().getAutoPromotionMode() != BoardPreferences.AUTO_PROMOTE_DISABLED) {
				autoQueenCheckbox.setSelected(true);
			}

			autoQueenCheckbox.addItemListener(checkboxActionListener);
			autoKnightCheckbox.addItemListener(checkboxActionListener);
			autoBishopCheckbox.addItemListener(checkboxActionListener);
			autoRookCheckbox.addItemListener(checkboxActionListener);

			autoQueenCheckbox.setToolTipText("Auto Queen");
			autoKnightCheckbox.setToolTipText("Auto Knight");
			autoBishopCheckbox.setToolTipText("Auto Bishop");
			autoRookCheckbox.setToolTipText("Auto Rook");

			JLabel queenLabel = new JLabel(isPlayerWhite ? WHITE_QUEEN
					: BLACK_QUEEN);
			queenLabel.setToolTipText("Auto Queen");

			JLabel knightLabel = new JLabel(isPlayerWhite ? WHITE_KNIGHT
					: BLACK_KNIGHT);
			knightLabel.setToolTipText("Auto Knight");

			JLabel bishopLabel = new JLabel(isPlayerWhite ? WHITE_BISHOP
					: BLACK_BISHOP);
			bishopLabel.setToolTipText("Auto Bishop");

			JLabel rookLabel = new JLabel(isPlayerWhite ? WHITE_ROOK
					: BLACK_ROOK);
			rookLabel.setToolTipText("Auto Rook");

			addSeparator(new Dimension(12, 1));
			add(autoQueenCheckbox);
			add(queenLabel);
			addSeparator(new Dimension(3, 1));
			add(autoKnightCheckbox);
			add(knightLabel);
			addSeparator(new Dimension(3, 1));
			add(autoBishopCheckbox);
			add(bishopLabel);
			addSeparator(new Dimension(3, 1));
			add(autoRookCheckbox);
			add(rookLabel);
			addSeparator(new Dimension(3, 1));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Created ChessAreaToolbar in: "
					+ (System.currentTimeMillis() - startTime));
		}
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