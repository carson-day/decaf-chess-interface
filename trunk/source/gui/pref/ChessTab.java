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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import decaf.gui.widgets.ComboBoxItem;
import decaf.gui.widgets.ComboBoxItems;
import decaf.gui.widgets.LabeledComponent;

public class ChessTab extends PreferencesTab {

	private static final ComboBoxItems AUTO_FIRST_MOVE = new ComboBoxItems(
			new ComboBoxItem[] { new ComboBoxItem("None", null),
					new ComboBoxItem("a3", "a3"), new ComboBoxItem("a4", "a4"),
					new ComboBoxItem("b3", "b3"), new ComboBoxItem("b4", "b4"),
					new ComboBoxItem("c3", "c3"), new ComboBoxItem("c4", "c4"),
					new ComboBoxItem("e3", "e3"), new ComboBoxItem("e4", "e4"),
					new ComboBoxItem("d3", "d3"), new ComboBoxItem("d4", "d4"),
					new ComboBoxItem("f3", "f3"), new ComboBoxItem("f4", "f4"),
					new ComboBoxItem("g3", "g3"), new ComboBoxItem("g4", "g4"),
					new ComboBoxItem("h3", "h3"), new ComboBoxItem("h4", "h4"),
					new ComboBoxItem("nf3", "nf3"),
					new ComboBoxItem("nc3", "nc3"),
					new ComboBoxItem("nh3", "nh3"),
					new ComboBoxItem("na3", "na3") });

	private static final ComboBoxItems HIGHLIGHT_MODE = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("None", new Integer(
							BoardPreferences.NO_HIGHLIGHT)),
					new ComboBoxItem("Highlight Fade Away", new Integer(
							BoardPreferences.HIGHLIGHT_FADE)),
					new ComboBoxItem("Highlight Stays Till Next Move",
							new Integer(
									BoardPreferences.HIGHLIGHT_UNTIL_NEXT_MOVE)) });

	private static final ComboBoxItems PREMOVE_TYPE = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("No Premove", new Integer(
							BoardPreferences.NO_PREMOVE)),
					new ComboBoxItem("True Premove", new Integer(
							BoardPreferences.TRUE_PREMOVE)),
					new ComboBoxItem("Queued Premove", new Integer(
							BoardPreferences.QUEUED_PREMOVE)) });

	private static final ComboBoxItems AUTO_PROMOTE = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Auto Promote Disabled", new Integer(
							BoardPreferences.AUTO_PROMOTE_DISABLED)),
					new ComboBoxItem("Auto Queen", new Integer(
							BoardPreferences.AUTO_QUEEN)),
					new ComboBoxItem("Auto Rook", new Integer(
							BoardPreferences.AUTO_ROOK)),
					new ComboBoxItem("Auto Bishop", new Integer(
							BoardPreferences.AUTO_KNIGHT)),
					new ComboBoxItem("Auto Knight", new Integer(
							BoardPreferences.AUTO_BISHOP)) });

	private JCheckBox isClosingAllWindowsOnGameStart = new JCheckBox("");

	private JCheckBox isShowingLag = new JCheckBox("");

	private JCheckBox isCLosingInactiveGamesOnNewObservedGame = new JCheckBox(
			"");

	private JCheckBox isPlayingMoveSoundOnObserving = new JCheckBox("");

	private JCheckBox snapToChatIfNoGames = new JCheckBox("");

	private JCheckBox isShowingToolbar = new JCheckBox("");

	private JCheckBox isShowingStatusBar = new JCheckBox("");

	private JComboBox premoveType = new JComboBox(PREMOVE_TYPE.getItems());

	private JComboBox autoPromoteType = new JComboBox(AUTO_PROMOTE.getItems());

	private JComboBox autoFirstMove = new JComboBox(AUTO_FIRST_MOVE.getItems());

	private JComboBox highlightMode = new JComboBox(HIGHLIGHT_MODE.getItems());

	public ChessTab() {
		super("Chess");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new LabeledComponent("Close All Other Windows When Playing A Game",
				isClosingAllWindowsOnGameStart));
		add(new LabeledComponent("Close Inactive Games On New Observed Game",
				isCLosingInactiveGamesOnNewObservedGame));
		add(new LabeledComponent("Show Lag Per Move", isShowingLag));
		add(new LabeledComponent("Is Showing Toolbar", isShowingToolbar));
		add(new LabeledComponent("Is Showing Status Bar", isShowingStatusBar));
		add(new LabeledComponent("Play Move Sound When Observing",
				isPlayingMoveSoundOnObserving));
		add(new LabeledComponent("Snap To Chat Window Layout If No Games",
				snapToChatIfNoGames));
		add(new LabeledComponent("Auto Promote", autoPromoteType));
		add(new LabeledComponent("Premove Type", premoveType));
		add(new LabeledComponent("Auto First Move As White", autoFirstMove));
		add(new LabeledComponent("Last Move Highlight Mode", highlightMode));
		add(Box.createVerticalGlue());
	}

	public void load(Preferences preferences) {
		isClosingAllWindowsOnGameStart.setSelected(preferences
				.getBoardPreferences().isClosingAllWindowsOnGameStart());
		isShowingLag.setSelected(preferences.getBoardPreferences()
				.isShowingLag());
		isShowingToolbar.setSelected(preferences.getBoardPreferences()
				.isShowingToolbar());
		isShowingStatusBar.setSelected(preferences.getBoardPreferences()
				.isShowingStatusBar());
		isCLosingInactiveGamesOnNewObservedGame.setSelected(preferences
				.getBoardPreferences()
				.isCLosingInactiveGamesOnNewObservedGame());
		isPlayingMoveSoundOnObserving.setSelected(preferences
				.getBoardPreferences().isPlayingMoveSoundOnObserving());
		snapToChatIfNoGames.setSelected(preferences.getBoardPreferences()
				.isSnapToChatIfNoGames());
		premoveType.setSelectedIndex(PREMOVE_TYPE
				.getIndexWithValue(new Integer(preferences
						.getBoardPreferences().getPremoveType())));
		autoPromoteType.setSelectedIndex(AUTO_PROMOTE
				.getIndexWithValue(new Integer(preferences
						.getBoardPreferences().getAutoPromotionMode())));
		autoFirstMove.setSelectedIndex(AUTO_FIRST_MOVE
				.getIndexWithValue(preferences.getBoardPreferences()
						.getAutoFirstWhiteMove()));
		highlightMode.setSelectedIndex(HIGHLIGHT_MODE
				.getIndexWithValue(preferences.getBoardPreferences()
						.getHighlightMode()));
	}

	public void save(Preferences preferences) {
		preferences.getBoardPreferences().setClosingAllWindowsOnGameStart(
				isClosingAllWindowsOnGameStart.isSelected());
		preferences.getBoardPreferences().setShowingLag(
				isShowingLag.isSelected());
		preferences.getBoardPreferences().setShowingToolbar(
				isShowingToolbar.isSelected());
		preferences.getBoardPreferences().setShowingStatusBar(
				isShowingStatusBar.isSelected());
		preferences.getBoardPreferences()
				.setClosingInactiveGamesOnNewObservedGame(
						isCLosingInactiveGamesOnNewObservedGame.isSelected());
		preferences.getBoardPreferences().setPlayingMoveSoundOnObserving(
				isPlayingMoveSoundOnObserving.isSelected());
		preferences.getBoardPreferences().setSnapToChatIfNoGames(
				snapToChatIfNoGames.isSelected());
		preferences.getBoardPreferences().setAutoPromotionMode(
				((ComboBoxItem) autoPromoteType.getSelectedItem()).toInt());
		preferences.getBoardPreferences().setPremoveType(
				((ComboBoxItem) premoveType.getSelectedItem()).toInt());
		preferences.getBoardPreferences().setAutoFirstWhiteMove(
				(String) ((ComboBoxItem) autoFirstMove.getSelectedItem())
						.getValue());
		preferences.getBoardPreferences().setHighlightMode(
				((ComboBoxItem) highlightMode.getSelectedItem()).toInt());
	}
}
