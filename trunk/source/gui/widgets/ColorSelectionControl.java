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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import decaf.gui.Disposable;

/**
 * A SelectionControl for chageing colors. getValue and setValue will only
 * handle type Color.
 */
public class ColorSelectionControl extends SelectionControl implements
		Disposable {
	private JPanel swatch;

	private JLabel label;

	private JButton changeButton;

	public void dispose() {
		super.dispose();
		removeAll();
		label = null;
		if (swatch != null) {
			swatch.removeAll();
			swatch = null;
		}
		changeButton = null;
	}

	public ColorSelectionControl(String labelText, String helpText, Color value) {
		super();

		label = new JLabel(labelText);
		swatch = new JPanel();
		swatch.setBackground(value);

		changeButton = new JButton("Change");

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(changeButton);
		add(swatch);
		add(label);

		changeButton.addActionListener(new ChangeButtonActionListener(this));

		// changeButton.setToolTipText(helpText);
		// swatch.setToolTipText(helpText);
		// label.setToolTipText(helpText);

		setLabel(labelText);
		setValue(value);
		setHelpText(helpText);
		label.setToolTipText(helpText);
	}

	public void setEnabled(boolean isEnabled) {
		changeButton.setEnabled(isEnabled);
		super.setEnabled(isEnabled);
	}

	/**
	 * Value will always be of type Boolean
	 */
	public void setValue(Object value) {
		if (value != null && !(value instanceof Color)) {
			throw new IllegalArgumentException("value must be of type Color");
		}

		Color color = (Color) value;

		if (value != null && !color.equals(getValue()))
			;
		{
			swatch.setBackground(color);
			super.setValue(color);
			fireValueChanged();
		}
	}

	private class ChangeButtonActionListener implements ActionListener {
		Component parent;

		public ChangeButtonActionListener(Component parent) {
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent e) {
			Color newColor = JColorChooser.showDialog(parent, getLabel(),
					(Color) getValue());
			if (newColor != null) {
				setValue(newColor);
			}
		}
	}
}