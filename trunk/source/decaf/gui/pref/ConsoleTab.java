package decaf.gui.pref;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.selectioncontrol.ComboBoxItem;
import decaf.gui.widgets.selectioncontrol.ComboBoxItems;

public class ConsoleTab extends PreferencesTab {
	private static final ComboBoxItems CHAT_BUFFER = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("1 Meg", new Integer(1000000)),
					new ComboBoxItem("5 Megs", new Integer(5000000)),
					new ComboBoxItem("10 Megs", new Integer(10000000)),
					new ComboBoxItem("25 Megs", new Integer(25000000)) });

	private static final ComboBoxItems TAB_ORIENTATION = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Tabs on Left",
							ChatPreferences.TABS_ON_LEFT),
					new ComboBoxItem("Tabs on Right",
							ChatPreferences.TABS_ON_RIGHT),
					new ComboBoxItem("Tabs on Top", ChatPreferences.TABS_ON_TOP),
					new ComboBoxItem("Tabs on Bottom",
							ChatPreferences.TABS_ON_BOTTOM) });

	private static final ComboBoxItems POPUP_CLICK = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Right Click",
							ChatPreferences.RIGHT_CLICK_POPUP),
					new ComboBoxItem("Left Click",
							ChatPreferences.LEFT_CLICK_POPUP),
					new ComboBoxItem("Middle Click",
							ChatPreferences.MIDDLE_CLICK_POPUP) });

	private JCheckBox isDisabled = new JCheckBox("");

	private JCheckBox prependTellToTabs = new JCheckBox("");

	private JComboBox tabOrientation = new JComboBox(TAB_ORIENTATION.getItems());

	private JComboBox consoleBuffer = new JComboBox(CHAT_BUFFER.getItems());

	private JComboBox nonConsoleBuffer = new JComboBox(CHAT_BUFFER.getItems());

	private JComboBox popupClick = new JComboBox(POPUP_CLICK.getItems());

	private JComboBox channelsCombo = buildChannelsCombo();

	private JCheckBox isShowingSeekGraph = new JCheckBox("");

	private JCheckBox isShowingBugSeek = new JCheckBox("");

	private JCheckBox isShowingBugOpenCheckbox = new JCheckBox("");

	private JLabel currentChannelsLabel = new JLabel();

	private List<Integer> currentChannels = new LinkedList<Integer>();

	private JPanel channelPanel = null;

	public ConsoleTab() {
		super("Console");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new LabeledComponent("Tabbing Disabled (Requires Restart)",
				isDisabled));
		add(new LabeledComponent("Prepend Tell X To Current Tab",
				prependTellToTabs));
		add(new LabeledComponent("Show Bug Open Checkbox (Requires Restart)",
				isShowingBugOpenCheckbox));
		add(new LabeledComponent("Show Seek Graph Button (Requires Restart)",
				isShowingSeekGraph));
		add(new LabeledComponent("Show Bug Seek Button (Requires Restart)",
				isShowingBugSeek));
		add(new LabeledComponent("Tab Orientation", tabOrientation));
		add(new LabeledComponent("Popup Click:", popupClick));
		add(new LabeledComponent("Main Console Tab Buffer", consoleBuffer));
		add(new LabeledComponent("Non Console Tab Buffer", nonConsoleBuffer));
		add(new LabeledComponent("Permanent Channel Tabs:",
				currentChannelsLabel));

		channelPanel = new JPanel();
		channelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		channelPanel.add(channelsCombo);
		channelPanel.add(new JButton(new AbstractAction("Add Channel Tab") {
			public void actionPerformed(ActionEvent e) {
				int channelSelected = ((Integer) channelsCombo
						.getSelectedItem()).intValue();

				if (!currentChannels.contains(new Integer(channelSelected))) {
					currentChannels.add(channelSelected);
				}
				currentChannelsLabel.setText(getChannels());
			}
		}));
		channelPanel.add(new JButton(new AbstractAction("Remove Channel Tab") {
			public void actionPerformed(ActionEvent e) {
				int channelSelected = ((Integer) channelsCombo
						.getSelectedItem()).intValue();
				currentChannels.remove(channelSelected);
				currentChannelsLabel.setText(getChannels());
			}
		}));
		add(channelPanel);

	}

	private JComboBox buildChannelsCombo() {
		Integer[] channels = new Integer[256];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = new Integer(i);
		}
		return new JComboBox(channels);
	}

	@Override
	public void dispose() {
		channelPanel.removeAll();
		removeAll();
	}

	private String getChannels() {
		Collections.sort(currentChannels);
		String result = "";
		for (int i = 0; i < currentChannels.size(); i++) {
			result += currentChannels.get(i)
					+ (currentChannels.size() != 1
							&& i < currentChannels.size() - 1 ? "," : "");
		}
		return result;
	}

	@Override
	public void load(Preferences preferences) {
		isDisabled
				.setSelected(preferences.getChatPreferences().isDisableTabs());
		tabOrientation.setSelectedIndex(TAB_ORIENTATION
				.getIndexWithValue(new Integer(preferences.getChatPreferences()
						.getTabOrientation())));

		prependTellToTabs.setSelected(preferences.getChatPreferences()
				.isPreprendTellToTabs());
		consoleBuffer.setSelectedIndex(CHAT_BUFFER
				.getIndexWithValue(new Integer(preferences.getChatPreferences()
						.getConsoleTabBufferSize())));
		nonConsoleBuffer.setSelectedIndex(CHAT_BUFFER
				.getIndexWithValue(new Integer(preferences.getChatPreferences()
						.getChatTabBufferSize())));
		popupClick.setSelectedIndex(POPUP_CLICK.getIndexWithValue(new Integer(
				preferences.getChatPreferences().getPopupMenuClick())));
		currentChannels = new LinkedList<Integer>(preferences
				.getChatPreferences().getChannelTabs());
		currentChannelsLabel.setText(getChannels());

		isShowingBugSeek.setSelected(preferences.getChatPreferences()
				.isShowingBugSeekButton());
		isShowingSeekGraph.setSelected(preferences.getChatPreferences()
				.isShowingSeekGraphButton());
		isShowingBugOpenCheckbox.setSelected(preferences.getChatPreferences()
				.isShowingBugOpenCheckbox());
	}

	@Override
	public void save(Preferences preferences) {
		preferences.getChatPreferences()
				.setDisableTabs(isDisabled.isSelected());
		preferences.getChatPreferences().setPreprendTellToTabs(
				prependTellToTabs.isSelected());
		preferences.getChatPreferences().setTabOrientation(
				((ComboBoxItem) tabOrientation.getSelectedItem()).toInt());
		preferences.getChatPreferences().getConsoleTabBufferSize(
				((ComboBoxItem) consoleBuffer.getSelectedItem()).toInt());
		preferences.getChatPreferences().setChatTabBufferSize(
				((ComboBoxItem) nonConsoleBuffer.getSelectedItem()).toInt());
		preferences.getChatPreferences().setPopupMenuClick(
				((ComboBoxItem) popupClick.getSelectedItem()).toInt());
		preferences.getChatPreferences().setChannelTabs(currentChannels);

		preferences.getChatPreferences().setShowingBugSeekButton(
				isShowingBugSeek.isSelected());
		preferences.getChatPreferences().setShowingSeekGraphButton(
				isShowingSeekGraph.isSelected());
		preferences.getChatPreferences().setShowingBugOpenCheckbox(
				isShowingBugOpenCheckbox.isSelected());
	}

}
