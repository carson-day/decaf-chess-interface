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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import decaf.gui.util.TextProperties;
import decaf.gui.widgets.ColorSelectionControl;
import decaf.gui.widgets.ComboBoxItem;
import decaf.gui.widgets.ComboBoxItems;
import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.TextPropertiesSelectionControl;

public class BughouseTab extends PreferencesTab {
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

	private JCheckBox isShowingPartnerCommunicationButtons = new JCheckBox("");

	private JCheckBox isPlayingMoveSoundOnPartnersBoard = new JCheckBox("");

	private JCheckBox isShowingLag = new JCheckBox("");

	private JCheckBox isShowingToolbar = new JCheckBox("");

	private JCheckBox isShowingStatusBar = new JCheckBox("");

	private JCheckBox isPlayingLeftBoard = new JCheckBox("");

	private ColorSelectionControl bughouseButtonBackground = new ColorSelectionControl(
			"BugEar Background", null, null);

	private TextPropertiesSelectionControl buttonText = new TextPropertiesSelectionControl(
			"BugEar Button Text", null, null);

	private JComboBox autoFirstMove = new JComboBox(AUTO_FIRST_MOVE.getItems());

	public BughouseTab() {
		super("Bughouse");
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(new LabeledComponent("BugEar Enabled",
				isShowingPartnerCommunicationButtons));
		add(new LabeledComponent("Play Move Sound On Partners Board",
				isPlayingMoveSoundOnPartnersBoard));
		add(new LabeledComponent("Is Showing Lag", isShowingLag));
		add(new LabeledComponent("Is Showing Toolbar", isShowingToolbar));
		add(new LabeledComponent("Is Showing Status Bar", isShowingStatusBar));
		add(new LabeledComponent("Is Playing On Left Board", isPlayingLeftBoard));
		add(bughouseButtonBackground);
		add(buttonText);
		add(new LabeledComponent("Auto First Move As White", autoFirstMove));
		add(Box.createVerticalGlue());
	}

	public void load(Preferences preferences) {
		isShowingPartnerCommunicationButtons.setSelected(preferences
				.getBughousePreferences()
				.isShowingPartnerCommunicationButtons());
		isPlayingMoveSoundOnPartnersBoard.setSelected(preferences
				.getBughousePreferences().isPlayingMoveSoundOnPartnersBoard());
		isShowingLag.setSelected(preferences.getBughousePreferences()
				.isShowingLag());
		isShowingToolbar.setSelected(preferences.getBughousePreferences()
				.isShowingBugToolbar());
		isShowingStatusBar.setSelected(preferences.getBughousePreferences()
				.isShowingStatusBar());
		isPlayingLeftBoard.setSelected(preferences.getBughousePreferences()
				.isPlayingLeftBoard());
		bughouseButtonBackground.setValue(preferences.getBughousePreferences()
				.getBughouseButtonBackground());
		buttonText.setValue(preferences.getBughousePreferences()
				.getBughouseButtonTextProperties());
		autoFirstMove.setSelectedIndex(AUTO_FIRST_MOVE
				.getIndexWithValue(preferences.getBughousePreferences()
						.getAutoFirstWhiteMove()));
	}

	public void save(Preferences preferences) {
		preferences.getBughousePreferences()
				.setShowingPartnerCommunicationButtons(
						isShowingPartnerCommunicationButtons.isSelected());
		preferences.getBughousePreferences()
				.setPlayingMoveSoundOnPartnersBoard(
						isPlayingMoveSoundOnPartnersBoard.isSelected());
		preferences.getBughousePreferences().setShowingLag(
				isShowingLag.isSelected());
		preferences.getBughousePreferences().setShowingBugToolbar(
				isShowingToolbar.isSelected());
		preferences.getBughousePreferences().setShowingStatusBar(
				isShowingStatusBar.isSelected());
		preferences.getBughousePreferences().setPlayingLeftBoard(
				isPlayingLeftBoard.isSelected());
		preferences.getBughousePreferences().setBughouseButtonBackground(
				((Color) bughouseButtonBackground.getValue()));
		preferences.getBughousePreferences().setBughouseButtonTextProperties(
				(TextProperties) buttonText.getValue());
		preferences.getBughousePreferences().setAutoFirstWhiteMove(
				(String) ((ComboBoxItem) autoFirstMove.getSelectedItem())
						.getValue());
	}
}
