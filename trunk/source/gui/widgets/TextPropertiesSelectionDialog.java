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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import decaf.gui.util.TextProperties;

public class TextPropertiesSelectionDialog extends JDialog {
	private static final GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
			.getLocalGraphicsEnvironment();

	private TextProperties textProperties;

	private JLabel fontFamilyLabel;

	private JComboBox fontFamilyComboBox;

	private JLabel fontSizeLabel;

	private JLabel fontSizeValueLabel;

	private JSlider fontSizeSlider;

	private JLabel swatch;

	private JCheckBox boldCheckBox;

	private JCheckBox italicCheckBox;

	private JButton changeForegroundButton;

	private JButton changeBackgroundButton;

	private JButton okButton;

	private JButton cancelButton;

	private JPanel swatchPanel;

	private JPanel checkboxPanel;

	private JPanel changeColorPanel;

	private JPanel changeFontFamilyPanel;

	private JPanel changeFontSizePanel;

	private JPanel okCancelPanel;

	private Color overrideBackgroundColor;

	private class FontFamilyActionListener implements ActionListener {
		public void actionPerformed(ActionEvent actionEvent) {
			TextProperties textProperties = getTextProperties();
			String fontFamilySelected = (String) fontFamilyComboBox
					.getSelectedItem();

			if (!textProperties.getFont().getFamily()
					.equals(fontFamilySelected)) {
				setTextProperties(new TextProperties(new Font(
						fontFamilySelected,
						textProperties.getFont().getStyle(), textProperties
								.getFont().getSize()), textProperties
						.getForeground(), textProperties.getBackground()));
			}
		}
	}

	private class FontSizeChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			TextProperties textProperties = getTextProperties();
			int newSize = fontSizeSlider.getValue();

			if (newSize != textProperties.getFont().getSize()) {
				fontSizeValueLabel.setText("" + newSize);
				setTextProperties(new TextProperties(new Font(textProperties
						.getFont().getFamily(), textProperties.getFont()
						.getStyle(), newSize), textProperties.getForeground(),
						textProperties.getBackground()));
			}
		}
	}

	private class ButtonActionListener implements ActionListener {
		private Component parent;

		public ButtonActionListener(Component parent) {
			this.parent = parent;
		}

		public void actionPerformed(ActionEvent actionEvent) {
			Object source = actionEvent.getSource();
			if (source == changeForegroundButton) {
				Color newForeground = JColorChooser.showDialog(parent,
						"Change Text Color", getTextProperties()
								.getForeground());
				if (newForeground != null
						&& !textProperties.getForeground()
								.equals(newForeground)) {
					setTextProperties(new TextProperties(textProperties
							.getFont(), newForeground, textProperties
							.getBackground()));
				}
			} else if (source == changeBackgroundButton) {
				Color newBackground = JColorChooser.showDialog(parent,
						"Change Background Color", getTextProperties()
								.getBackground());
				if (newBackground != null
						&& !textProperties.getBackground()
								.equals(newBackground)) {
					setTextProperties(new TextProperties(textProperties
							.getFont(), textProperties.getForeground(),
							newBackground));
				}
			} else if (source == boldCheckBox || source == italicCheckBox) {
				boolean isBoldChecked = boldCheckBox.isSelected();
				boolean isItalicChecked = italicCheckBox.isSelected();
				int style = !isBoldChecked && !isItalicChecked ? Font.PLAIN
						: isBoldChecked && !isItalicChecked ? Font.BOLD
								: !isBoldChecked && isItalicChecked ? Font.ITALIC
										: Font.ITALIC | Font.BOLD;
				setTextProperties(new TextProperties(new Font(textProperties
						.getFont().getFamily(), style, textProperties.getFont()
						.getSize()), textProperties.getForeground(),
						textProperties.getBackground()));
			} else if (source == okButton) {
				parent.setVisible(false);
			} else if (source == cancelButton) {
				setTextProperties(null);
				parent.setVisible(false);
			}
		}
	}

	public TextPropertiesSelectionDialog(JDialog owner, String title,
			boolean isModal, TextProperties properties) {
		super(owner, title, isModal);
		init(properties);
	}

	public TextPropertiesSelectionDialog(JFrame owner, String title,
			boolean isModal, TextProperties properties) {
		super(owner, title, isModal);
		init(properties);
	}

	private void init(TextProperties properties) {
		fontFamilyLabel = new JLabel("Font Family:");
		fontSizeLabel = new JLabel("Font Size:");

		fontFamilyComboBox = new JComboBox(graphicsEnvironment
				.getAvailableFontFamilyNames());
		fontFamilyComboBox.setSelectedItem(properties.getFont().getFamily());
		fontFamilyComboBox.addActionListener(new FontFamilyActionListener());

		fontSizeSlider = new JSlider(JSlider.HORIZONTAL, 5, 60, properties
				.getFont().getSize());
		fontSizeValueLabel = new JLabel("" + properties.getFont().getSize());
		fontSizeSlider.addChangeListener(new FontSizeChangeListener());

		boldCheckBox = new JCheckBox("Bold", properties.getFont().isBold());
		italicCheckBox = new JCheckBox("Italic", properties.getFont()
				.isItalic());

		swatch = new JLabel("Swatch", JLabel.CENTER);

		changeForegroundButton = new JButton("Change Text Color");
		changeBackgroundButton = new JButton("Change Background Color");

		okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");

		ButtonActionListener buttonActionListener = new ButtonActionListener(
				this);
		changeForegroundButton.addActionListener(buttonActionListener);
		changeBackgroundButton.addActionListener(buttonActionListener);
		boldCheckBox.addActionListener(buttonActionListener);
		italicCheckBox.addActionListener(buttonActionListener);
		okButton.addActionListener(buttonActionListener);
		cancelButton.addActionListener(buttonActionListener);

		checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		checkboxPanel.add(boldCheckBox);
		checkboxPanel.add(italicCheckBox);

		swatchPanel = new JPanel(new BorderLayout());
		swatchPanel.add(swatch, BorderLayout.CENTER);

		changeColorPanel = new JPanel(new GridLayout(2, 1));
		changeColorPanel.add(changeForegroundButton);
		changeColorPanel.add(changeBackgroundButton);

		changeFontFamilyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		changeFontFamilyPanel.add(fontFamilyLabel);
		changeFontFamilyPanel.add(fontFamilyComboBox);

		changeFontSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		changeFontSizePanel.add(fontSizeLabel);
		changeFontSizePanel.add(fontSizeSlider);
		changeFontSizePanel.add(fontSizeValueLabel);

		okCancelPanel = new JPanel(new GridLayout(1, 2));
		okCancelPanel.add(okButton);
		okCancelPanel.add(cancelButton);

		Container contentPane = getContentPane();

		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 1.0F;
		gbc.weighty = 0.0F;
		contentPane.add(changeFontFamilyPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = 1.0F;
		gbc.weighty = 0.0F;
		contentPane.add(changeFontSizePanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.weightx = 1.0F;
		gbc.weighty = 0.0F;
		contentPane.add(checkboxPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.weightx = .75F;
		gbc.weighty = 1.0F;
		contentPane.add(swatchPanel, gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = .25F;
		gbc.weighty = 1.0F;
		contentPane.add(changeColorPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(20, 5, 5, 5);
		gbc.weightx = 1.0F;
		gbc.weighty = 0.0F;
		contentPane.add(okCancelPanel, gbc);

		pack();
		setTextProperties(properties);

	}

	public TextProperties getTextProperties() {
		return textProperties;
	}

	public void setTextProperties(TextProperties textProperties) {
		this.textProperties = textProperties;
		if (textProperties != null) {
			swatch.setForeground(textProperties.getForeground());
			swatch.setFont(textProperties.getFont());

			if (overrideBackgroundColor != null) {
				swatch.setBackground(textProperties.getBackground());
				swatchPanel.setBackground(textProperties.getBackground());
			} else {
				swatch.setBackground(textProperties.getBackground());
				swatchPanel.setBackground(textProperties.getBackground());
			}
		}
	}

	public void setOverrideBackgroundColor(Color color) {
		overrideBackgroundColor = color;

		if (color != null) {
			swatch.setBackground(textProperties.getBackground());
			swatchPanel.setBackground(textProperties.getBackground());
		}

	}

	public void disableSettingBackgroundColor() {
		changeBackgroundButton.setEnabled(false);
	}

	public static TextProperties showTextPropertiesDialog(Component component,
			String title, TextProperties properties,
			boolean isDisablingBackgroundColorChange, Color defaultColor) {
		Component root = SwingUtilities.getRoot(component);
		TextPropertiesSelectionDialog dialog = null;
		if (root instanceof JFrame) {
			dialog = new TextPropertiesSelectionDialog((JFrame) root, title,
					true, properties);
		} else if (root instanceof JDialog) {
			dialog = new TextPropertiesSelectionDialog((JDialog) root, title,
					true, properties);
		} else {
			throw new IllegalArgumentException(
					"Component must have a JDialg or JFrame as its root.");
		}

		dialog.setOverrideBackgroundColor(defaultColor);
		if (isDisablingBackgroundColorChange) {
			dialog.disableSettingBackgroundColor();
		}
		dialog.setVisible(true);
		dialog.dispose();
		return dialog.getTextProperties();

	}

	public static TextProperties showTextPropertiesDialog(Component component,
			String title, TextProperties properties) {

		return showTextPropertiesDialog(component, title, properties, false,
				null);
	}

	public static void main(String args[]) {
		JFrame testFrame = new JFrame("Test Frame");
		testFrame.setBounds(100, 100, 300, 300);
		testFrame.setVisible(true);

		showTextPropertiesDialog(testFrame, "Test TextProperties Dialog",
				new TextProperties(new Font("Monospaced", Font.PLAIN, 12),
						Color.black, Color.white));
	}
}