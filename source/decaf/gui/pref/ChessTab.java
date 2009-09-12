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

import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.selectioncontrol.ComboBoxItem;
import decaf.gui.widgets.selectioncontrol.ComboBoxItems;

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

	private static final ComboBoxItems SHOW_TENTHS = new ComboBoxItems(
			new ComboBoxItem[] { new ComboBoxItem("Never", new Integer(0)),
					new ComboBoxItem("At 1 seconds", new Integer(1)),
					new ComboBoxItem("At 3 seconds", new Integer(3)),
					new ComboBoxItem("At 5 seconds", new Integer(5)),
					new ComboBoxItem("At 10 seconds", new Integer(10)),
					new ComboBoxItem("At 15 seconds", new Integer(15)),
					new ComboBoxItem("At 30 seconds", new Integer(30)),
					new ComboBoxItem("At 45 seconds", new Integer(45)),
					new ComboBoxItem("At 60 seconds", new Integer(60)),
					new ComboBoxItem("Always", new Integer(Integer.MAX_VALUE)) });

	private static final ComboBoxItems DRAG_N_DROP = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Standard Drag And Drop", new Integer(
							BoardPreferences.STANDARD_DRAG_AND_DROP)),
					new ComboBoxItem("Click Move Mode", new Integer(
							BoardPreferences.CLICK_CLICK_DRAG_AND_DROP)),
					new ComboBoxItem("Invisible Move Mode", new Integer(
							BoardPreferences.INVISIBLE_MOVE)) });

	private JCheckBox isShowingLag = new JCheckBox("");

	private JCheckBox isPlayingMoveSoundOnObserving = new JCheckBox("");

	private JCheckBox isShowingToolbar = new JCheckBox("");

	private JCheckBox isShowingStatusBar = new JCheckBox("");

	private JCheckBox isShowingCaptions = new JCheckBox("");

	private JCheckBox isSmartMoveEnabled = new JCheckBox("");

	private JCheckBox isUnfollowingOnPlayingGame = new JCheckBox("");

	private JCheckBox isShowingMoveListOnPlayingGame = new JCheckBox("");

	private JCheckBox isShowingMoveListOnObsGame = new JCheckBox("");

	private JCheckBox isShowingSideUpTime = new JCheckBox("");

	private JCheckBox isShowingPieceJail = new JCheckBox("");

	private JCheckBox isShowingMyMovesAsSelected = new JCheckBox("");

	private JCheckBox isSelectingSquareOnHover = new JCheckBox("");

	private JComboBox premoveType = new JComboBox(PREMOVE_TYPE.getItems());

	private JComboBox autoPromoteType = new JComboBox(AUTO_PROMOTE.getItems());

	private JComboBox autoFirstMove = new JComboBox(AUTO_FIRST_MOVE.getItems());

	private JComboBox showTenthsAt = new JComboBox(SHOW_TENTHS.getItems());

	private JComboBox dragAndDrop = new JComboBox(DRAG_N_DROP.getItems());

	private JPanel checkboxPanel;

	public ChessTab() {
		super("Chess");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		checkboxPanel = new JPanel();
		checkboxPanel.setLayout(new GridLayout(6, 2));
		checkboxPanel.add(new LabeledComponent("Show Lag Per Move",
				isShowingLag));
		checkboxPanel
				.add(new LabeledComponent("Show Toolbar", isShowingToolbar));
		checkboxPanel.add(new LabeledComponent("Show Status Bar",
				isShowingStatusBar));
		checkboxPanel.add(new LabeledComponent("Show Kib/Says Captions",
				isShowingCaptions));
		checkboxPanel.add(new LabeledComponent(
				"Issuing Unfollow When Playing Game",
				isUnfollowingOnPlayingGame));
		checkboxPanel
				.add(new LabeledComponent("Play Move Sound When Observing",
						isPlayingMoveSoundOnObserving));
		checkboxPanel
				.add(new LabeledComponent("Smartmove", isSmartMoveEnabled));
		checkboxPanel.add(new LabeledComponent("Show Move List When Playing",
				isShowingMoveListOnPlayingGame));
		checkboxPanel.add(new LabeledComponent("Show Move List When Observing",
				isShowingMoveListOnObsGame));
		checkboxPanel.add(new LabeledComponent("Show Side Up Time",
				isShowingSideUpTime));
		checkboxPanel.add(new LabeledComponent("Show Captured Pieces",
				isShowingPieceJail));
		checkboxPanel.add(new LabeledComponent("Mark My Moves",
				isShowingMyMovesAsSelected));
		checkboxPanel.add(new LabeledComponent("Select Squares When Hovering",
				isSelectingSquareOnHover));

		add(checkboxPanel);
		add(new LabeledComponent("Show Tenths Of A Second", showTenthsAt));
		add(new LabeledComponent("Auto Promote", autoPromoteType));
		add(new LabeledComponent("Premove Type", premoveType));
		add(new LabeledComponent("Drag And Drop Mode", dragAndDrop));
		add(new LabeledComponent("Auto First Move As White", autoFirstMove));
		add(Box.createVerticalGlue());
	}

	@Override
	public void dispose() {
		checkboxPanel.removeAll();
		removeAll();
	}

	@Override
	public void load(Preferences preferences) {

		isShowingLag.setSelected(preferences.getBoardPreferences()
				.isShowingLag());
		isShowingToolbar.setSelected(preferences.getBoardPreferences()
				.isShowingToolbar());
		isShowingStatusBar.setSelected(preferences.getBoardPreferences()
				.isShowingStatusBar());
		isPlayingMoveSoundOnObserving.setSelected(preferences
				.getBoardPreferences().isPlayingMoveSoundOnObserving());
		isShowingCaptions.setSelected(preferences.getBoardPreferences()
				.isShowingGameCaptions());
		isSmartMoveEnabled.setSelected(preferences.getBoardPreferences()
				.isSmartMoveEnabled());
		isUnfollowingOnPlayingGame.setSelected(preferences
				.getBoardPreferences().isUnfollowingOnPlayingGame());
		isShowingMoveListOnPlayingGame.setSelected(preferences
				.getBoardPreferences().isShowingMoveListOnPlayingGames());
		isShowingSideUpTime.setSelected(preferences.getBoardPreferences()
				.isShowingSideUpTime());
		isShowingPieceJail.setSelected(preferences.getBoardPreferences()
				.isShowingPieceJail());
		isShowingMoveListOnObsGame.setSelected(preferences
				.getBoardPreferences().isShowingMoveListOnObsGames());
		isShowingMyMovesAsSelected.setSelected(preferences
				.getBoardPreferences().isShowingMyMovesAsSelected());
		isSelectingSquareOnHover.setSelected(preferences.getBoardPreferences()
				.isSelectingHoverOverSquares());

		showTenthsAt.setSelectedIndex(SHOW_TENTHS
				.getIndexWithValue(preferences.getBoardPreferences()
						.getShowTenthsWhenTimeIsLessThanSeconds()));
		premoveType.setSelectedIndex(PREMOVE_TYPE
				.getIndexWithValue(new Integer(preferences
						.getBoardPreferences().getPremoveType())));
		autoPromoteType.setSelectedIndex(AUTO_PROMOTE
				.getIndexWithValue(new Integer(preferences
						.getBoardPreferences().getAutoPromotionMode())));
		autoFirstMove.setSelectedIndex(AUTO_FIRST_MOVE
				.getIndexWithValue(preferences.getBoardPreferences()
						.getAutoFirstWhiteMove()));

		dragAndDrop.setSelectedIndex(DRAG_N_DROP.getIndexWithValue(preferences
				.getBoardPreferences().getDragAndDropMode()));

	}

	@Override
	public void save(Preferences preferences) {
		preferences.getBoardPreferences().setShowingLag(
				isShowingLag.isSelected());
		preferences.getBoardPreferences().setShowingToolbar(
				isShowingToolbar.isSelected());
		preferences.getBoardPreferences().setShowingStatusBar(
				isShowingStatusBar.isSelected());
		preferences.getBoardPreferences().setShowingGameCaptions(
				isShowingCaptions.isSelected());
		preferences.getBoardPreferences().setPlayingMoveSoundOnObserving(
				isPlayingMoveSoundOnObserving.isSelected());
		preferences.getBoardPreferences().setSmartMoveEnabled(
				isSmartMoveEnabled.isSelected());
		preferences.getBoardPreferences().setUnfollowingOnPlayingGame(
				isUnfollowingOnPlayingGame.isSelected());
		preferences.getBoardPreferences().setShowingMoveListOnPlayingGames(
				isShowingMoveListOnPlayingGame.isSelected());
		preferences.getBoardPreferences().setShowingSideUpTime(
				isShowingSideUpTime.isSelected());
		preferences.getBoardPreferences().setShowingPieceJail(
				isShowingPieceJail.isSelected());
		preferences.getBoardPreferences().setShowingMoveListOnObsGames(
				isShowingMoveListOnObsGame.isSelected());
		preferences.getBoardPreferences().setShowingMyMovesAsSelected(
				isShowingMyMovesAsSelected.isSelected());
		preferences.getBoardPreferences().setSelectingHoverOverSquares(
				isSelectingSquareOnHover.isSelected());

		preferences
				.getBoardPreferences()
				.setShowTenthsWhenTimeIsLessThanSeconds(
						((ComboBoxItem) showTenthsAt.getSelectedItem()).toInt());
		preferences.getBoardPreferences().setAutoPromotionMode(
				((ComboBoxItem) autoPromoteType.getSelectedItem()).toInt());
		preferences.getBoardPreferences().setPremoveType(
				((ComboBoxItem) premoveType.getSelectedItem()).toInt());
		preferences.getBoardPreferences().setAutoFirstWhiteMove(
				(String) ((ComboBoxItem) autoFirstMove.getSelectedItem())
						.getValue());
		preferences.getBoardPreferences().setDragAndDropMode(
				((ComboBoxItem) dragAndDrop.getSelectedItem()).toInt());
	}
}
