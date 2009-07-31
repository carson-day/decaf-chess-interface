package decaf.gui.pref;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.log4j.Logger;

import decaf.gui.GUIManager;
import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.selectioncontrol.ComboBoxItem;
import decaf.gui.widgets.selectioncontrol.ComboBoxItems;
import decaf.resources.ResourceManagerFactory;

public class GeneralTab extends PreferencesTab {
	private static final Logger LOGGER = Logger.getLogger(GeneralTab.class);

	private static final ComboBoxItems LOOK_AND_FEELS = new ComboBoxItems(
			getLookAndFeels());

	private static final ComboBoxItems MAX_FILE_SIZE = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("1 meg", new Integer(1000000)),
					new ComboBoxItem("5 meg", new Integer(5000000)),
					new ComboBoxItem("10 meg", new Integer(10000000)),
					new ComboBoxItem("25 meg", new Integer(25000000)) });

	private static final ComboBoxItems PGN_STORAGE = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("in unique files", new Integer(
							LoggingPreferences.SEPERATE_FILE_FOR_EACH_GAME)),
					new ComboBoxItem("appended to eachother in games.pgn",
							new Integer(LoggingPreferences.APPEND_TO_GAMES_PGN)) });

	private JCheckBox isSoundOn = new JCheckBox("");

	private JCheckBox isPreventingIdleLogout = new JCheckBox("");

	private JCheckBox isAutoLogin = new JCheckBox("");

	private JComboBox lookAndFeel = new JComboBox(LOOK_AND_FEELS.getItems());

	private JCheckBox isLoggingEnabled = new JCheckBox("");

	private JCheckBox isConsoleLoggingEnabled = new JCheckBox("");

	private JCheckBox isChannelLoggingEnabled = new JCheckBox("");

	private JCheckBox isTellLoggingEnabled = new JCheckBox("");

	private JCheckBox isLoggingGames = new JCheckBox("");

	private JComboBox maxFileSize = new JComboBox(MAX_FILE_SIZE.getItems());

	private JComboBox pgnStorage = new JComboBox(PGN_STORAGE.getItems());
	
	public void dispose()
	{
		removeAll();
	}

	public GeneralTab() {
		super("General");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		boolean requiresRestart = true;
		String DEF_COMM = GUIManager.getInstance().getDriver().TIMER_DEFAULT_COMMAND;
		DEF_COMM = (DEF_COMM.charAt(0)+"").toUpperCase() + DEF_COMM.substring(1);
		add(new LabeledComponent(
				"Prevent Idle Logout With '" + DEF_COMM + "' " + (requiresRestart?"(Requires Restart)":""),
				isPreventingIdleLogout));
		add(new LabeledComponent("Auto Login Enabled", isAutoLogin));
		add(new LabeledComponent("Sound Enabled", isSoundOn));
		add(new LabeledComponent("Look And Feel", lookAndFeel));

		if (!ResourceManagerFactory.getManager().isApplet()) {
			JPanel labelPanel = new JPanel();
			String seperator = java.io.File.separator;
			JLabel label = new JLabel("Log Directory: "
					+ System.getProperty("user.home") + seperator + ".Decaf" + seperator + "logs",
					JLabel.CENTER);
			labelPanel.add(label);
			label.setForeground(Color.RED);
			add(labelPanel);
			add(new LabeledComponent("Logging Enabled", isLoggingEnabled));
			add(new LabeledComponent("Console Log Enabled",
					isConsoleLoggingEnabled));
			add(new LabeledComponent("Channel Logging Enabled",
					isChannelLoggingEnabled));
			add(new LabeledComponent("Direct Tell Logging Enabled",
					isTellLoggingEnabled));
			add(new LabeledComponent("Max Log Size", maxFileSize));

			JLabel label2 = new JLabel("Games Directory: "
					+ System.getProperty("user.home") + seperator + ".Decaf" + seperator + "games",
					JLabel.CENTER);
			JPanel labelPanel2 = new JPanel();
			labelPanel2.add(label2);
			label2.setForeground(Color.RED);
			add(labelPanel2);
			add(new LabeledComponent("Log Games In PGN", isLoggingGames));
			add(new LabeledComponent("Store games", pgnStorage));
		}
	}

	public void load(Preferences preferences) {
		isPreventingIdleLogout.setSelected(preferences.getChatPreferences()
				.isPreventingIdleLogout());
		isSoundOn.setSelected(preferences.isSoundOn());
		isAutoLogin
				.setSelected(preferences.getLoginPreferences().isAutoLogin());
		lookAndFeel.setSelectedIndex(LOOK_AND_FEELS
				.getIndexWithValue(preferences.getLookAndFeelClassName()));
		isLoggingEnabled.setSelected(preferences.getLoggingPreferences()
				.isLoggingEnabled());
		isConsoleLoggingEnabled.setSelected(preferences.getLoggingPreferences()
				.isLoggingConsole());
		isChannelLoggingEnabled.setSelected(preferences.getLoggingPreferences()
				.isLoggingChannels());
		isTellLoggingEnabled.setSelected(preferences.getLoggingPreferences()
				.isLoggingPersonalTells());
		isLoggingGames.setSelected(preferences.getLoggingPreferences()
				.isLoggingGames());
		maxFileSize.setSelectedIndex(MAX_FILE_SIZE
				.getIndexWithValue(new Integer(preferences
						.getLoggingPreferences().getMaxFileSize())));
		pgnStorage.setSelectedIndex(PGN_STORAGE.getIndexWithValue(new Integer(
				preferences.getLoggingPreferences().getGameLogMode())));

	}

	public void save(Preferences preferences) {
		preferences.getChatPreferences().setPreventingIdleLogout(
				isPreventingIdleLogout.isSelected());
		preferences.getLoginPreferences()
				.setAutoLogin(isAutoLogin.isSelected());
		preferences.setSoundOn(isSoundOn.isSelected());
		preferences
				.setLookAndFeelClassName((String) ((ComboBoxItem) lookAndFeel
						.getSelectedItem()).getValue());
		preferences.getLoggingPreferences().setLoggingEnabled(
				isLoggingEnabled.isSelected());
		preferences.getLoggingPreferences().setLoggingConsole(
				isConsoleLoggingEnabled.isSelected());
		preferences.getLoggingPreferences().setLoggingPersonalTells(
				isTellLoggingEnabled.isSelected());
		preferences.getLoggingPreferences().setLoggingChannels(
				isChannelLoggingEnabled.isSelected());
		preferences.getLoggingPreferences().setLoggingGames(
				isLoggingGames.isSelected());
		preferences.getLoggingPreferences().setMaxFileSize(
				((ComboBoxItem) maxFileSize.getSelectedItem()).toInt());
		preferences.getLoggingPreferences().setGameLogMode(
				((ComboBoxItem) pgnStorage.getSelectedItem()).toInt());
	}

	private static ComboBoxItem[] getLookAndFeels() {
		LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
		ComboBoxItem[] result = new ComboBoxItem[lookAndFeels.length];

		for (int i = 0; i < lookAndFeels.length; i++) {
			LOGGER.debug("Adding :" + lookAndFeels[i].getClassName());
			result[i] = new ComboBoxItem(lookAndFeels[i].getName(),
					lookAndFeels[i].getClassName());
		}

		return result;
	}
}
