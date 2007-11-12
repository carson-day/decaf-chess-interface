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
import javax.swing.JLabel;

import decaf.gui.widgets.ComboBoxItem;
import decaf.gui.widgets.ComboBoxItems;
import decaf.gui.widgets.LabeledComponent;
import decaf.speech.SpeechManager;

public class SpeechTab extends PreferencesTab {
	private static final ComboBoxItems WORDS_PER_MINUTE = new ComboBoxItems(
			new ComboBoxItem[] { new ComboBoxItem("100", new Integer(100)),
					new ComboBoxItem("125", new Integer(125)),
					new ComboBoxItem("150", new Integer(150)),
					new ComboBoxItem("175", new Integer(175)),
					new ComboBoxItem("200", new Integer(200)),
					new ComboBoxItem("225", new Integer(225)),
					new ComboBoxItem("250", new Integer(250)) });

	private JCheckBox speechEnabled = new JCheckBox("");

	private JCheckBox isSpeakingNotifications = new JCheckBox("");

	private JCheckBox isSpeakingTells = new JCheckBox("");

	private JCheckBox isSpeakingPtells = new JCheckBox("");

	private JCheckBox isSpeakingName = new JCheckBox("");

	private JCheckBox isSpeaking10SecondCountdown = new JCheckBox("");

	private JComboBox wordsPerMinute = new JComboBox(WORDS_PER_MINUTE
			.getItems());

	public SpeechTab() {
		super("Speech");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new JLabel("Description: "
				+ SpeechManager.getInstance().getSpeech().getDescription()));
		add(new LabeledComponent("Speech Enabled", speechEnabled));
		add(new LabeledComponent("Speak Notifications", isSpeakingNotifications));
		add(new LabeledComponent("Speak Tells", isSpeakingTells));
		add(new LabeledComponent("Speak Partner Tells", isSpeakingPtells));
		add(new LabeledComponent("Speak Name", isSpeakingName));
		add(new LabeledComponent("Speak 10 Second Countdown",
				isSpeaking10SecondCountdown));
		add(new LabeledComponent("Speak rate (words per minute)",
				wordsPerMinute));

		if (!SpeechManager.getInstance().getSpeech().supportsWordsPerMinute()) {
			wordsPerMinute.setEnabled(false);
		}
		add(Box.createVerticalGlue());
	}

	public void load(Preferences preferences) {
		speechEnabled.setSelected(preferences.getSpeechPreferences()
				.isSpeechEnabled());
		isSpeakingNotifications.setSelected(preferences.getSpeechPreferences()
				.isSpeakingNotifications());
		isSpeakingTells.setSelected(preferences.getSpeechPreferences()
				.isSpeakingTells());
		isSpeakingPtells.setSelected(preferences.getSpeechPreferences()
				.isSpeakingPtells());
		isSpeakingName.setSelected(preferences.getSpeechPreferences()
				.isSpeakingName());
		isSpeaking10SecondCountdown.setSelected(preferences
				.getSpeechPreferences().isSpeaking10SecondCountdown());
		wordsPerMinute.setSelectedIndex(WORDS_PER_MINUTE
				.getIndexWithValue(new Integer(preferences
						.getSpeechPreferences().getSpokenWordsPerMinuite())));
	}

	public void save(Preferences preferences) {
		preferences.getSpeechPreferences().setSpeechEnabled(
				speechEnabled.isSelected());
		preferences.getSpeechPreferences().setSpeakingNotifications(
				isSpeakingNotifications.isSelected());
		preferences.getSpeechPreferences().setSpeakingTells(
				isSpeakingTells.isSelected());
		preferences.getSpeechPreferences().setSpeakingPtells(
				isSpeakingPtells.isSelected());
		preferences.getSpeechPreferences().setSpeakingName(
				isSpeakingName.isSelected());
		preferences.getSpeechPreferences().setSpeaking10SecondCountdown(
				isSpeaking10SecondCountdown.isSelected());
		preferences.getSpeechPreferences().setSpokenWordsPerMinuite(
				((ComboBoxItem) wordsPerMinute.getSelectedItem()).toInt());
	}
}
