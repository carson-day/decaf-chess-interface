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

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import decaf.gui.widgets.Disposable;
import decaf.util.TextProperties;

/**
 * A SelectionControl for chageing colors. getValue and setValue will only
 * handle type TextProperties.
 */
public class TextPropertiesSelectionControl extends SelectionControl implements
		Disposable {
	private static final Logger LOGGER = Logger
			.getLogger(TextPropertiesSelectionControl.class);

	private JPanel swatch;

	private JLabel swatchLabel;

	private JLabel label;

	private JButton changeButton;

	private boolean allowBackgroundColorChange = true;

	private boolean allowForegroundColorChange = true;

	private Color overrideBackground = null;

	public void dispose() {
		super.dispose();
		removeAll();
		label = null;
		if (swatch != null) {
			swatch.removeAll();
			swatch = null;
		}
		swatchLabel = null;
		changeButton = null;
	}

	public TextPropertiesSelectionControl(String labelText, String helpText,
			TextProperties properties) {
		this(labelText, helpText, properties, true, null);

	}

	public TextPropertiesSelectionControl(String labelText, String helpText,
			TextProperties properties, boolean allowBackgroundColorChange,
			Color overrideBackground) {
		super();
		this.allowBackgroundColorChange = allowBackgroundColorChange;
		this.overrideBackground = overrideBackground;
		label = new JLabel(labelText);
		swatch = new JPanel();
		swatchLabel = new JLabel("Test");
		swatch.add(swatchLabel);
		changeButton = new JButton("Change");

		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(changeButton);
		add(swatch);
		add(label);

		changeButton.addActionListener(new ChangeButtonActionListener(this));

		changeButton.setToolTipText(helpText);
		swatch.setToolTipText(helpText);
		label.setToolTipText(helpText);

		setLabel(labelText);
		setValue(properties);
		setHelpText(helpText);
	}

	public void setEnabled(boolean isEnabled) {
		changeButton.setEnabled(isEnabled);
		super.setEnabled(isEnabled);
	}

	/**
	 * Value will always be of type Boolean
	 */
	public void setValue(Object value) {
		if (value != null && !(value instanceof TextProperties)) {
			throw new IllegalArgumentException(
					"value must be of type TextProperties");
		}

		TextProperties properties = (TextProperties) value;

		if (properties != null && !properties.equals(getValue())) {
			swatchLabel.setFont(properties.getFont());
			swatchLabel.setForeground(properties.getForeground());

			if (overrideBackground == null) {
				swatchLabel.setBackground(properties.getBackground());
				swatch.setBackground(properties.getBackground());
			} else {
				swatchLabel.setBackground(overrideBackground);
				swatch.setBackground(overrideBackground);
			}

			super.setValue(value);
			fireValueChanged();
		}
	}

	private class ChangeButtonActionListener implements ActionListener {
		Component parent;

		public ChangeButtonActionListener(Component parent) {
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent e) {

			TextProperties properties = TextPropertiesSelectionDialog
					.showTextPropertiesDialog(parent, getLabel(),
							(TextProperties) getValue(),
							!allowForegroundColorChange,
							!allowBackgroundColorChange, overrideBackground);
			if (properties != null) {
				setValue(properties);
			}
		}
	}

	public boolean isAllowForegroundColorChange() {
		return allowForegroundColorChange;
	}

	public void setAllowForegroundColorChange(boolean allowForegroundColorChange) {
		this.allowForegroundColorChange = allowForegroundColorChange;
	}

	public boolean isAllowBackgroundColorChange() {
		return allowBackgroundColorChange;
	}

	public void setAllowBackgroundColorChange(boolean allowBackgroundColorChange) {
		this.allowBackgroundColorChange = allowBackgroundColorChange;
	}

	public Color getOverrideBackground() {
		return overrideBackground;
	}

	public void setOverrideBackground(Color overrideBackground) {
		this.overrideBackground = overrideBackground;

		if (overrideBackground != null) {
			swatchLabel.setBackground(overrideBackground);
			swatch.setBackground(overrideBackground);
		}

	}

}