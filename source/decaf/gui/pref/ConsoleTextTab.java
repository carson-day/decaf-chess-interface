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

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import decaf.gui.widgets.selectioncontrol.ColorSelectionControl;
import decaf.gui.widgets.selectioncontrol.TextPropertiesSelectionControl;
import decaf.util.TextProperties;

public class ConsoleTextTab extends PreferencesTab {

	private class ChannelControl extends JPanel {
		private int channel;

		private TextPropertiesSelectionControl control;

		public ChannelControl(int channel) {
			this.channel = channel;
			this.control = new TextPropertiesSelectionControl("Channel "
					+ channel + " Text", null, null);
			setLayout(new FlowLayout(FlowLayout.LEFT));

			add(new JButton(new AbstractAction("Remove") {
				public void actionPerformed(ActionEvent e) {
					removeChannelControl(ChannelControl.this);
				}
			}));
			add(control);
		}

		public int getChannel() {
			return channel;
		}

		public TextPropertiesSelectionControl getControl() {
			return control;
		}

		public TextProperties getProperties() {
			return (TextProperties) control.getValue();
		}

		public void setChannel(int channel) {
			this.channel = channel;
		}

		public void setProperties(TextProperties properties) {
			control.setValue(properties);
		}
	}

	private boolean ignoreUpdates = true;

	private ColorSelectionControl chatPanelBackground = new ColorSelectionControl(
			"Background", null, null) {
		@Override
		public void setValue(Object value) {
			super.setValue(value);
			if (value != null) {

				chatPanelFont.setOverrideBackground((Color) chatPanelBackground
						.getValue());
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

	private TextPropertiesSelectionControl chatPanelFont = new TextPropertiesSelectionControl(
			"All Fonts", null, null) {
		@Override
		public void setValue(Object value) {
			super.setValue(value);
			if (!ignoreUpdates) {
				TextProperties properties = (TextProperties) value;
				Font newFont = properties.getFont();
				if (value != null) {
					shoutTextProperties.setValue(getNewTextProperties(
							shoutTextProperties, newFont));
					cshoutTextProperties.setValue(getNewTextProperties(
							cshoutTextProperties, newFont));
					kibitzTextProperties.setValue(getNewTextProperties(
							kibitzTextProperties, newFont));
					whisperTextProperties.setValue(getNewTextProperties(
							whisperTextProperties, newFont));
					ptellTextProperties.setValue(getNewTextProperties(
							ptellTextProperties, newFont));
					tellTextProperties.setValue(getNewTextProperties(
							tellTextProperties, newFont));
					notificationTextProperties.setValue(getNewTextProperties(
							notificationTextProperties, newFont));
					matchTextProperties.setValue(getNewTextProperties(
							matchTextProperties, newFont));
					alertTextProperties.setValue(getNewTextProperties(
							alertTextProperties, newFont));
					defaultTextProperties.setValue(getNewTextProperties(
							defaultTextProperties, newFont));

					for (ChannelControl control : channelControls) {
						control.getControl().setValue(
								getNewTextProperties(control.getControl(),
										newFont));
					}
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
			"Notif. Text", null, null);

	private TextPropertiesSelectionControl matchTextProperties = new TextPropertiesSelectionControl(
			"Match Text", null, null);

	private TextPropertiesSelectionControl alertTextProperties = new TextPropertiesSelectionControl(
			"Alert Text", null, null);

	private TextPropertiesSelectionControl defaultTextProperties = new TextPropertiesSelectionControl(
			"Default Text", null, null);

	private List<ChannelControl> channelControls = new LinkedList<ChannelControl>();

	private JPanel channelControlPanel = new JPanel();

	private JComboBox channelsCombo = buildChannelsCombo();

	private Preferences preferences;

	private JPanel panel1;
	private JPanel addChannelPanel;

	public ConsoleTextTab() {
		super("Console Text");

		chatPanelFont.setAllowBackgroundColorChange(false);
		chatPanelFont.setAllowForegroundColorChange(false);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		panel1 = new JPanel();
		panel1.setLayout(new GridLayout(6, 2));
		panel1.add(chatPanelBackground);
		panel1.add(chatPanelFont);
		panel1.add(shoutTextProperties);
		panel1.add(cshoutTextProperties);
		panel1.add(kibitzTextProperties);
		panel1.add(whisperTextProperties);
		panel1.add(ptellTextProperties);
		panel1.add(tellTextProperties);
		panel1.add(notificationTextProperties);
		panel1.add(matchTextProperties);
		panel1.add(alertTextProperties);
		panel1.add(defaultTextProperties);
		add(panel1);

		addChannelPanel = new JPanel();
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
		add(new JScrollPane(channelControlPanel));
	}

	private void addChannelControl(final ChannelControl control) {
		channelControls.add(control);
		channelControlPanel.add(control);
		redoLayout();
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
		channelControlPanel.removeAll();
		panel1.removeAll();
		addChannelPanel.removeAll();
		removeAll();
	}

	private TextProperties getNewTextProperties(
			TextPropertiesSelectionControl control, Font newFont) {
		TextProperties oldProperties = (TextProperties) control.getValue();
		return new TextProperties(newFont, oldProperties.getForeground(),
				oldProperties.getBackground());
	}

	public boolean hasChannelProperties(int channel) {

		for (ChannelControl control : channelControls) {
			if (control.getChannel() == channel) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void load(Preferences preferences) {
		this.preferences = preferences;
		channelControlPanel.removeAll();
		channelControls.clear();

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

		ignoreUpdates = true;
		chatPanelFont.setValue(preferences.getChatPreferences()
				.getDefaultTextProperties());
		ignoreUpdates = false;

		redoLayout();

	}

	private void redoLayout() {
		channelControlPanel.invalidate();
		ConsoleTextTab.this.validate();
	}

	private void removeChannelControl(final ChannelControl control) {
		channelControls.remove(control);
		channelControlPanel.remove(control);
		redoLayout();
	}

	@Override
	public void save(Preferences preferences) {

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
