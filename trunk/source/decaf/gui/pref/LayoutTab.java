package decaf.gui.pref;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.selectioncontrol.ComboBoxItem;
import decaf.gui.widgets.selectioncontrol.ComboBoxItems;

public class LayoutTab extends PreferencesTab {

	private static final ComboBoxItems TO_FRONT = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Every Game", new Integer(
							BoardPreferences.ALL_GAMES_TO_FRONT)),
					new ComboBoxItem("Only Games I Am Playing", new Integer(
							BoardPreferences.ONLY_GAMES_I_PLAY_TO_FRONT)) });

	private static final ComboBoxItems LAYOUT_STRATEGY = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem(
							"Remember last window position",
							new Integer(
									Preferences.REMEMBER_LAST_WINDOW_POSITION_STRATEGY)),
					new ComboBoxItem(
							"Snap to saved layout every game",
							new Integer(
									Preferences.SNAP_TO_LAYOUT_EVERY_GAME_STRATEGY)) });

	private JCheckBox isClosingAllWindowsOnGameStart = new JCheckBox("");

	private JCheckBox isCLosingInactiveGamesOnNewObservedGame = new JCheckBox(
			"");

	private JCheckBox snapToChatIfNoGames = new JCheckBox("");

	private JComboBox toFront = new JComboBox(TO_FRONT.getItems());

	private JComboBox adjustmentStrategy = new JComboBox(LAYOUT_STRATEGY
			.getItems());

	public LayoutTab() {
		super("Window Layouts");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new LabeledComponent("Close All Other Windows When Playing A Game",
				isClosingAllWindowsOnGameStart));
		add(new LabeledComponent("Close Inactive Games On New Observed Game",
				isCLosingInactiveGamesOnNewObservedGame));
		add(new LabeledComponent("Snap To Chat Window Layout If No Games",
				snapToChatIfNoGames));
		add(new LabeledComponent("Move Chess Board Window To Front On", toFront));
		add(new LabeledComponent("Window size/position strategy",
				adjustmentStrategy));
	}

	@Override
	public void dispose() {
		removeAll();
	}

	@Override
	public void load(Preferences preferences) {
		isClosingAllWindowsOnGameStart.setSelected(preferences
				.getBoardPreferences().isClosingAllWindowsOnGameStart());
		isCLosingInactiveGamesOnNewObservedGame.setSelected(preferences
				.getBoardPreferences()
				.isCLosingInactiveGamesOnNewObservedGame());
		snapToChatIfNoGames.setSelected(preferences.getBoardPreferences()
				.isSnapToChatIfNoGames());
		toFront.setSelectedIndex(TO_FRONT.getIndexWithValue(preferences
				.getBoardPreferences().getGamesToFrontMode()));
		adjustmentStrategy.setSelectedIndex(LAYOUT_STRATEGY
				.getIndexWithValue(preferences.getWindowLayoutStrategy()));

	}

	@Override
	public void save(Preferences preferences) {
		preferences.getBoardPreferences().setClosingAllWindowsOnGameStart(
				isClosingAllWindowsOnGameStart.isSelected());
		preferences.getBoardPreferences()
				.setClosingInactiveGamesOnNewObservedGame(
						isCLosingInactiveGamesOnNewObservedGame.isSelected());
		preferences.getBoardPreferences().setSnapToChatIfNoGames(
				snapToChatIfNoGames.isSelected());
		preferences.getBoardPreferences().setGamesToFrontMode(
				((ComboBoxItem) toFront.getSelectedItem()).toInt());
		preferences.setWindowLayoutStrategy(((ComboBoxItem) adjustmentStrategy
				.getSelectedItem()).toInt());
	}
}
