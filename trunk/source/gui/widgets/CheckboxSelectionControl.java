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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import decaf.gui.Disposable;

/**
 * A Checkbox selection control. Boolean is the Object type used for get and set
 * value.
 */
public class CheckboxSelectionControl extends SelectionControl implements
		Disposable {
	private JCheckBox checkBox;

	public void dispose() {
		super.dispose();
		removeAll();
		checkBox = null;
	}

	public CheckboxSelectionControl(String labelText, String helpText,
			boolean value) {
		super();

		checkBox = new JCheckBox(labelText);

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(checkBox);

		checkBox.addActionListener(new CheckBoxActionListener());

		// checkBox.setToolTipText(helpText);
		setLabel(labelText);
		setValue(new Boolean(value));
		checkBox.setToolTipText(helpText);
		setHelpText(helpText);
	}

	public void setEnabled(boolean isEnabled) {
		checkBox.setEnabled(isEnabled);
		super.setEnabled(isEnabled);
	}

	/**
	 * Value will always be of type Boolean
	 */
	public void setValue(Object value) {
		if (!(value instanceof Boolean)) {
			throw new IllegalArgumentException("value must be of type Boolean");
		}

		Boolean booleanValue = (Boolean) value;

		if (!booleanValue.equals(getValue())) {
			checkBox.setSelected(booleanValue.booleanValue());
			super.setValue(booleanValue);
			fireValueChanged();
		}
	}

	private class CheckBoxActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setValue(new Boolean(checkBox.isSelected()));
		}
	}
}