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
package decaf.gui.widgets;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import decaf.com.outboundevent.OutboundEvent;
import decaf.event.EventService;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.PropertiesManager;
import decaf.gui.util.TextProperties;

public class BugEarPanel extends JPanel implements Preferenceable {

	private static Action[] BUTTON_ACTIONS = null;

	private final static int ROWS = PropertiesManager.getInstance().getInt(
			"BugButtons", "grid.rows");

	private final static int COLUMNS = PropertiesManager.getInstance().getInt(
			"BugButtons", "grid.columns");

	static {
		try {
			int currentButton = 1;
			PropertiesManager manager = PropertiesManager.getInstance();
			List<AbstractAction> actions = new LinkedList<AbstractAction>();

			String currentText = manager.getString("BugButtons", currentButton
					+ ".text");
			String currentPtell = manager.getString("BugButtons", currentButton
					+ ".ptell");

			while ((currentText != null)) {
				final String finalPtell = currentPtell;

				actions.add(new AbstractAction(currentText) {
					public void actionPerformed(ActionEvent e) {
						EventService.getInstance()
								.publish(
										new OutboundEvent(
												"ptell " + finalPtell, false));
					}
				});
				currentButton++;
				currentText = manager.getString("BugButtons", currentButton
						+ ".text");
				currentPtell = manager.getString("BugButtons", currentButton
						+ ".ptell");
			}

			BUTTON_ACTIONS = actions.toArray(new AbstractAction[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private List<JButton> buttons = new LinkedList<JButton>();

	private Preferences preferences;

	public BugEarPanel(Preferences preferences) {
		this.preferences = preferences;
		setBackground(preferences.getBughousePreferences()
				.getBughouseButtonBackground());
		setupButtons();
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		setBackground(preferences.getBughousePreferences()
				.getBughouseButtonBackground());

		TextProperties buttonProperties = preferences.getBughousePreferences()
				.getBughouseButtonTextProperties();

		for (JButton button : buttons) {
			button.setFont(buttonProperties.getFont());
			button.setForeground(buttonProperties.getForeground());
			button.setBackground(buttonProperties.getBackground());

		}

	}

	private void setupButtons() {
		setLayout(new GridLayout(ROWS, COLUMNS));
		for (int i = 0; i < BUTTON_ACTIONS.length; i++) {
			JButton button = new JButton(BUTTON_ACTIONS[i]);
			add(button);
			buttons.add(button);

			TextProperties buttonProperties = preferences
					.getBughousePreferences().getBughouseButtonTextProperties();
			button.setFont(buttonProperties.getFont());
			button.setForeground(buttonProperties.getForeground());
			button.setBackground(buttonProperties.getBackground());
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

}
