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
package decaf.gui;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import decaf.gui.pref.Preferences;
import decaf.gui.widgets.PositiveIntegerTextField;

public class LoginDialog extends JDialog implements ActionListener {
	private JLabel handleLbl = new JLabel("Handle:");

	private JLabel passwordLbl = new JLabel("Password:");

	private JLabel serverLbl = new JLabel("Server:");

	private JLabel portLbl = new JLabel("Port");

	private JComboBox serverChoices = new JComboBox(
			new Object[] { "freechess.org port: 5000" });

	private JTextField handleField = new JTextField();

	private JTextField serverField = new JTextField("freechess.org");

	private PositiveIntegerTextField portField = new PositiveIntegerTextField();

	private JCheckBox guestLoginCheckBox = new JCheckBox(
			"Login as a guest with handle.");

	private JCheckBox guestLoginWithoutHanndleCheckBox = new JCheckBox(
			"Login as a guest without handle.");

	private JCheckBox timesealEnabledCheckBox = new JCheckBox("Timeseal");

	private JPasswordField passwordField = new JPasswordField();

	private JButton logonButton = new JButton("Login");

	private Preferences preferences;

	private JPanel handlePanel = new JPanel(new GridLayout(1, 2));

	private JPanel passwordPanel = new JPanel(new GridLayout(1, 2));

	private JPanel serverPanel = new JPanel(new GridLayout(1, 2));

	private JPanel portPanel = new JPanel(new GridLayout(1, 2));

	public LoginDialog(Preferences preferences) {
		super((JFrame) null, "Decaffeinate Login", true);
		this.preferences = preferences;
		Container contentPane = getContentPane();

		handlePanel.add(handleLbl);
		handlePanel.add(handleField);

		passwordPanel.add(passwordLbl);
		passwordPanel.add(passwordField);

		serverPanel.add(serverLbl);
		serverPanel.add(serverField);

		portPanel.add(portLbl);
		portPanel.add(portField);

		contentPane.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		Insets insets = new Insets(10, 10, 10, 10);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(handlePanel, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(passwordPanel, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(serverPanel, constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(portPanel, constraints);

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(guestLoginCheckBox, constraints);

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(guestLoginWithoutHanndleCheckBox, constraints);

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(timesealEnabledCheckBox, constraints);

		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		constraints.anchor = GridBagConstraints.CENTER;
		contentPane.add(logonButton, constraints);

		if (preferences.getLoginPreferences().getDefaultUserName() != null) {
			handleField.setText(preferences.getLoginPreferences()
					.getDefaultUserName());
		}

		if (preferences.getLoginPreferences().getDefaultPassword() != null) {
			passwordField.setText(preferences.getLoginPreferences()
					.getDefaultPassword());
		}
		guestLoginWithoutHanndleCheckBox.setSelected(preferences
				.getLoginPreferences().isDefaultGuestWithoutHandle());
		guestLoginCheckBox.setSelected(preferences.getLoginPreferences()
				.isDefaultGuestEnabled());
		timesealEnabledCheckBox.setSelected(preferences.getLoginPreferences()
				.isDefaultTimesealEnabled());
		timesealEnabledCheckBox.setEnabled(false);

		serverField.setText(preferences.getLoginPreferences().getServer());
		portField.setText(""
				+ preferences.getLoginPreferences().getServerPort());

		logonButton.addActionListener(this);
		guestLoginCheckBox.addActionListener(this);
		timesealEnabledCheckBox.addActionListener(this);
		guestLoginWithoutHanndleCheckBox.addActionListener(this);

		adjustToCheckBoxControls();
		pack();

		passwordField.addActionListener(this);

		setLocation(new Point(250, 150));
		setVisible(true);
	}

	private void saveOptions() {
		preferences.getLoginPreferences().setDefaultUserName(
				handleField.getText());
		preferences.getLoginPreferences().setDefaultPassword(
				new String(passwordField.getPassword()));
		preferences.getLoginPreferences().setDefaultGuestEnabled(
				guestLoginCheckBox.isSelected());
		preferences.getLoginPreferences().setDefaultTimesealEnabled(
				timesealEnabledCheckBox.isSelected());
		preferences.getLoginPreferences().setDefaultGuestWithoutHandle(
				guestLoginWithoutHanndleCheckBox.isSelected());
		preferences.getLoginPreferences().setServer(serverField.getText());
		preferences.getLoginPreferences().setServerPort(
				Integer.parseInt(portField.getText()));
		preferences.save();
	}

	private void adjustToCheckBoxControls() {
		if (guestLoginCheckBox.isSelected()) {
			passwordField.setText("");
			passwordField.setEnabled(false);
			handleField.setEnabled(true);
		} else if (guestLoginWithoutHanndleCheckBox.isSelected()) {
			passwordField.setText("");
			passwordField.setEnabled(false);
			handleField.setText("");
			handleField.setEnabled(false);
			guestLoginCheckBox.setSelected(false);
		} else {
			passwordField.setEnabled(true);
			handleField.setEnabled(true);
		}
	}

	public String getServer() {
		return serverField.getText();
	}

	public int getPort() {
		return Integer.parseInt(portField.getText());
	}

	public String getUserName() {
		return handleField.getText();
	}

	public String getPassword() {
		return new String(passwordField.getPassword());
	}

	public boolean isLoggingInAsGuestWithoutHandle() {
		return guestLoginWithoutHanndleCheckBox.isSelected();
	}

	public boolean isLoggingInAsGuest() {
		return guestLoginCheckBox.isSelected();
	}

	public boolean isTimesealEnabled() {
		return timesealEnabledCheckBox.isSelected();
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == guestLoginCheckBox || source == timesealEnabledCheckBox
				|| source == guestLoginWithoutHanndleCheckBox) {
			adjustToCheckBoxControls();
		} else if (source == logonButton || source == passwordField) {
			String handleText = handleField.getText();
			String passwordText = new String(passwordField.getPassword());

			if (!isLoggingInAsGuestWithoutHandle()
					&& (handleText == null || handleText.equals(""))) {
				JOptionPane.showMessageDialog(logonButton,
						"You must enter a handle before logging in.");
			} else if (!isLoggingInAsGuest()
					&& !isLoggingInAsGuestWithoutHandle()
					&& (passwordText == null || passwordText.equals(""))) {
				JOptionPane.showMessageDialog(logonButton,
						"You must enter a password before logging in.");
			} else {
				saveOptions();
				setVisible(false);
			}
		}
	}
}
