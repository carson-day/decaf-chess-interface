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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import decaf.gui.util.TextProperties;
import decaf.gui.widgets.ColorSelectionControl;
import decaf.gui.widgets.ComboBoxItem;
import decaf.gui.widgets.ComboBoxItems;
import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.TextPropertiesSelectionControl;

public class ChatTab extends PreferencesTab {

	private class ChannelControl extends JPanel {
		private int channel;

		private TextPropertiesSelectionControl control;

		private ChannelControl thisControl = this;

		public ChannelControl(int channel) {
			this.channel = channel;
			this.control = new TextPropertiesSelectionControl("Channel "
					+ channel + " Text", null, null);
			setLayout(new FlowLayout(FlowLayout.LEFT));

			add(new JButton(new AbstractAction("Remove") {
				public void actionPerformed(ActionEvent e) {
					removeChannelControl(thisControl);
				}
			}));
			add(control);
		}

		public int getChannel() {
			return channel;
		}

		public void setChannel(int channel) {
			this.channel = channel;
		}

		public TextPropertiesSelectionControl getControl() {
			return control;
		}

		public void setProperties(TextProperties properties) {
			control.setValue(properties);
		}

		public TextProperties getProperties() {
			return (TextProperties) control.getValue();
		}
	}

	private static final ComboBoxItems CHAT_BUFFER = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("1 Meg", new Integer(1000000)),
					new ComboBoxItem("5 Megs", new Integer(5000000)),
					new ComboBoxItem("10 Megs", new Integer(10000000)),
					new ComboBoxItem("25 Megs", new Integer(25000000)) });

	private ChatTab thisTab = this;

	private JComboBox chatBuffer = new JComboBox(CHAT_BUFFER.getItems());

	private JCheckBox isSmartScrolling = new JCheckBox("");

	private JCheckBox isPreventingIdleLogout = new JCheckBox("");

	private ColorSelectionControl chatPanelBackground = new ColorSelectionControl(
			"Chat Panel Background Color", null, null) {
		public void setValue(Object value) {
			super.setValue(value);
			if (value != null) {
				shoutTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				cshoutTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				kibitzTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				whisperTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				ptellTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				tellTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				notificationTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				matchTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				alertTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());
				defaultTextProperties
						.setOverrideBackground((Color) chatPanelBackground
								.getValue());

				for (ChannelControl control : channelControls) {
					control.getControl().setOverrideBackground(
							(Color) chatPanelBackground.getValue());
				}
			}
		}
	};

	private TextPropertiesSelectionControl shoutTextProperties = new TextPropertiesSelectionControl(
			"Shout Text", null, null);

	private TextPropertiesSelectionControl cshoutTextProperties = new TextPropertiesSelectionControl(
			"CSshout Text", null, null);

	private TextPropertiesSelectionControl kibitzTextProperties = new TextPropertiesSelectionControl(
			"Kibitz Text", null, null);

	private TextPropertiesSelectionControl whisperTextProperties = new TextPropertiesSelectionControl(
			"Whisper Text", null, null);

	private TextPropertiesSelectionControl ptellTextProperties = new TextPropertiesSelectionControl(
			"PTell Text", null, null);

	private TextPropertiesSelectionControl tellTextProperties = new TextPropertiesSelectionControl(
			"Tell Text", null, null);

	private TextPropertiesSelectionControl notificationTextProperties = new TextPropertiesSelectionControl(
			"Notification Text", null, null);

	private TextPropertiesSelectionControl matchTextProperties = new TextPropertiesSelectionControl(
			"Match Text", null, null);

	private TextPropertiesSelectionControl alertTextProperties = new TextPropertiesSelectionControl(
			"Alert Text", null, null);

	private TextPropertiesSelectionControl defaultTextProperties = new TextPropertiesSelectionControl(
			"Default Text", null, null);

	private List<ChannelControl> channelControls = new LinkedList<ChannelControl>();

	private JPanel channelControlPanel = new JPanel();

	private JComboBox channelsCombo = buildChannelsCombo();

	private JScrollPane scrollPane;

	private Preferences preferences;

	public ChatTab() {
		super("Chat");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new LabeledComponent("Chat Buffer", chatBuffer));
		add(new LabeledComponent("Is Smart Scroll Enabled", isSmartScrolling));
		add(new LabeledComponent(
				"Is Preventing Idle Logout (issues date command)",
				isPreventingIdleLogout));
		add(chatPanelBackground);
		add(shoutTextProperties);
		add(cshoutTextProperties);
		add(kibitzTextProperties);
		add(whisperTextProperties);
		add(ptellTextProperties);
		add(tellTextProperties);
		add(notificationTextProperties);
		add(matchTextProperties);
		add(alertTextProperties);
		add(defaultTextProperties);

		JPanel addChannelPanel = new JPanel();
		addChannelPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		addChannelPanel.add(new JButton(new AbstractAction(
				"Add Channel Properties") {
			public void actionPerformed(ActionEvent e) {
				int channelSelected = ((Integer) channelsCombo
						.getSelectedItem()).intValue();
				if (!hasChannelProperties(channelSelected)) {
					ChannelControl control = new ChannelControl(channelSelected);
					control.setProperties(preferences.getChatPreferences()
							.getChannelProperties(channelSelected));
					addChannelControl(control);
				}
			}
		}));
		addChannelPanel.add(channelsCombo);
		add(addChannelPanel);

		channelControlPanel = new JPanel();
		channelControlPanel.setLayout(new BoxLayout(channelControlPanel,
				BoxLayout.Y_AXIS));
		channelControlPanel.setMinimumSize(new Dimension(200, 200));
		channelControlPanel.setMaximumSize(new Dimension(2000, 600));
		add(scrollPane = new JScrollPane(channelControlPanel));
	}

	private void addChannelControl(final ChannelControl control) {
		/*
		 * SwingUtilities.invokeLater(new Runnable() { public void run() {
		 */

		channelControls.add(control);
		channelControlPanel.add(control);
		thisTab.getLayout().layoutContainer(thisTab);
		scrollPane.getLayout().layoutContainer(scrollPane);
		thisTab.repaint();
		// }
		// });
	}

	private void removeChannelControl(final ChannelControl control) {

		// SwingUtilities.invokeLater(new Runnable() {
		// public void run() {

		channelControls.remove(control);
		channelControlPanel.remove(control);
		thisTab.getLayout().layoutContainer(thisTab);
		scrollPane.getLayout().layoutContainer(scrollPane);
		thisTab.repaint();
		// }
		// });

	}

	private JComboBox buildChannelsCombo() {
		Integer[] channels = new Integer[256];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = new Integer(i);
		}
		return new JComboBox(channels);
	}

	public boolean hasChannelProperties(int channel) {

		for (ChannelControl control : channelControls) {
			if (control.getChannel() == channel) {
				return true;
			}
		}
		return false;
	}

	public void load(Preferences preferences) {
		this.preferences = preferences;
		channelControlPanel.removeAll();
		channelControls.clear();

		isSmartScrolling.setSelected(preferences.getChatPreferences()
				.isSmartScrollEnabled());
		isPreventingIdleLogout.setSelected(preferences.getChatPreferences()
				.isPreventingIdleLogout());
		chatBuffer.setSelectedIndex(CHAT_BUFFER.getIndexWithValue(new Integer(
				preferences.getChatPreferences().getTelnetBufferSize())));

		shoutTextProperties.setAllowBackgroundColorChange(false);
		cshoutTextProperties.setAllowBackgroundColorChange(false);
		kibitzTextProperties.setAllowBackgroundColorChange(false);
		whisperTextProperties.setAllowBackgroundColorChange(false);
		ptellTextProperties.setAllowBackgroundColorChange(false);
		tellTextProperties.setAllowBackgroundColorChange(false);
		notificationTextProperties.setAllowBackgroundColorChange(false);
		matchTextProperties.setAllowBackgroundColorChange(false);
		alertTextProperties.setAllowBackgroundColorChange(false);
		defaultTextProperties.setAllowBackgroundColorChange(false);

		shoutTextProperties.setValue(preferences.getChatPreferences()
				.getShoutTextProperties());
		cshoutTextProperties.setValue(preferences.getChatPreferences()
				.getCshoutTextProperties());
		kibitzTextProperties.setValue(preferences.getChatPreferences()
				.getKibitzTextProperties());
		whisperTextProperties.setValue(preferences.getChatPreferences()
				.getWhisperTextProperties());
		ptellTextProperties.setValue(preferences.getChatPreferences()
				.getPtellTextProperties());
		tellTextProperties.setValue(preferences.getChatPreferences()
				.getTellTextProperties());
		notificationTextProperties.setValue(preferences.getChatPreferences()
				.getNotificationTextProperties());
		matchTextProperties.setValue(preferences.getChatPreferences()
				.getMatchTextProperties());
		alertTextProperties.setValue(preferences.getChatPreferences()
				.getAlertTextProperties());
		defaultTextProperties.setValue(preferences.getChatPreferences()
				.getDefaultTextProperties());

		int[] channels = preferences.getChatPreferences()
				.getChannelsThatHaveProperties();

		for (int i = 0; i < channels.length; i++) {
			ChannelControl control = new ChannelControl(channels[i]);
			control.setProperties(preferences.getChatPreferences()
					.getChannelProperties(channels[i]));

			control.getControl().setAllowBackgroundColorChange(false);
			channelControls.add(control);
			channelControlPanel.add(control);
		}

		// do last so all colors get set.
		chatPanelBackground.setValue(preferences.getChatPreferences()
				.getTelnetPanelBackground());

		thisTab.getLayout().layoutContainer(thisTab);
		scrollPane.getLayout().layoutContainer(scrollPane);
		repaint();

	}

	public void save(Preferences preferences) {

		preferences.getChatPreferences().setSmartScrollEnabled(
				isSmartScrolling.isSelected());
		preferences.getChatPreferences().setPreventingIdleLogout(
				isPreventingIdleLogout.isSelected());
		preferences.getChatPreferences().setTelnetBufferSize(
				((ComboBoxItem) chatBuffer.getSelectedItem()).toInt());
		preferences.getChatPreferences().setTelnetPanelBackground(
				(Color) chatPanelBackground.getValue());
		preferences.getChatPreferences().setShoutTextProperties(
				(TextProperties) shoutTextProperties.getValue());
		preferences.getChatPreferences().setCshoutTextProperties(
				(TextProperties) cshoutTextProperties.getValue());
		preferences.getChatPreferences().setKibitzTextProperties(
				(TextProperties) kibitzTextProperties.getValue());
		preferences.getChatPreferences().setWhisperTextProperties(
				(TextProperties) whisperTextProperties.getValue());
		preferences.getChatPreferences().setPtellTextProperties(
				(TextProperties) ptellTextProperties.getValue());
		preferences.getChatPreferences().setTellTextProperties(
				(TextProperties) tellTextProperties.getValue());
		preferences.getChatPreferences().setNotificationTextProperties(
				(TextProperties) notificationTextProperties.getValue());
		preferences.getChatPreferences().setMatchTextProperties(
				(TextProperties) matchTextProperties.getValue());
		preferences.getChatPreferences().setAlertTextProperties(
				(TextProperties) alertTextProperties.getValue());
		preferences.getChatPreferences().setDefaultTextProperties(
				(TextProperties) defaultTextProperties.getValue());

		for (ChannelControl control : channelControls) {
			preferences.getChatPreferences().setChannelProperties(
					control.getChannel(), control.getProperties());
		}
	}

}
