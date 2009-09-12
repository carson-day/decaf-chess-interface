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

package decaf.gui.widgets.selectioncontrol;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import decaf.gui.widgets.Disposable;

/**
 * A ChoiceSelectioNControl for a choice control. You pass in the
 * choiceDescriptions and choiceValues into the constructor. The
 * choiceDescriptions are displayed to the user, and the choiceValues are used
 * for storing value. All comparaisons are performed using the equals operator.
 * This control does not support empty values.
 */
public class ChoiceSelectionControl extends SelectionControl implements
		Disposable {
	private class ComboBoxActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setValue(descriptionToValue(comboBox.getSelectedItem()));
		}
	}

	private JLabel label;

	private JComboBox comboBox;

	private Object[] descriptions;

	private Object[] values;

	public ChoiceSelectionControl(String labelText, String helpText,
			Object value, Object[] choiceDescriptions, Object[] choiceValues) {
		super();

		if (choiceDescriptions.length != choiceValues.length) {
			throw new IllegalArgumentException(
					"choiceDescriptions must be the same length as choiceValues");
		}

		label = new JLabel(labelText);
		comboBox = new JComboBox(choiceDescriptions);
		comboBox.setEditable(false);

		comboBox.addActionListener(new ComboBoxActionListener());

		descriptions = choiceDescriptions;
		values = choiceValues;

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(comboBox);
		add(label);

		// comboBox.setToolTipText(helpText);
		// label.setToolTipText(helpText);
		setLabel(labelText);
		setValue(value);
		setHelpText(helpText);
		label.setToolTipText(helpText);

	}

	private Object descriptionToValue(Object description) {
		Object result = null;
		for (int i = 0; result == null && i < values.length; i++) {
			if (descriptions[i].equals(description)) {
				result = values[i];
			}
		}
		return result;
	}

	@Override
	public void dispose() {
		super.dispose();
		removeAll();
		label = null;
		if (comboBox != null) {
			comboBox.removeAll();
			comboBox = null;
		}
		descriptions = null;
		values = null;
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		comboBox.setEnabled(isEnabled);
		super.setEnabled(isEnabled);
	}

	@Override
	public void setValue(Object newValue) {
		if (!newValue.equals(getValue())) {
			Object description = valueToDescription(newValue);

			if (description == null) {
				throw new IllegalArgumentException(
						"New value is not in choiceVallues. newValue="
								+ newValue);
			}

			super.setValue(newValue);
			comboBox.setSelectedItem(description);
			fireValueChanged();
		}
	}

	private Object valueToDescription(Object value) {
		Object result = null;
		for (int i = 0; result == null && i < values.length; i++) {
			if (values[i].equals(value)) {
				result = descriptions[i];
			}
		}
		return result;
	}
}