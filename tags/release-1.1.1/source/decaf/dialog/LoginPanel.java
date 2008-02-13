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
package decaf.dialog;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

import decaf.gui.pref.Preferences;
import decaf.gui.widgets.PositiveIntegerTextField;
import decaf.resources.ResourceManagerFactory;

public class LoginPanel extends JPanel implements ActionListener {
	private static final Logger LOGGER = Logger.getLogger(LoginPanel.class);

	private JLabel handleLbl = new JLabel("Handle:");

	private JLabel passwordLbl = new JLabel("Password:");

	private JLabel serverLbl = new JLabel("Server:");

	private JLabel portLbl = new JLabel("Port");

	private JTextField handleField = new JTextField();

	private JTextField serverField = new JTextField("freechess.org");

	private PositiveIntegerTextField portField = new PositiveIntegerTextField();

	private JCheckBox autoLoginCheckBox = new JCheckBox(
			"Auto log me in next time.");

	private JCheckBox guestLoginCheckBox = new JCheckBox("Login as guest.");

	private JCheckBox timesealEnabledCheckBox = new JCheckBox("Timeseal");

	private JPasswordField passwordField = new JPasswordField();

	private JButton logonButton = new JButton("Login");

	private Preferences preferences;

	private ActionListener listener;

	private JPanel handlePanel = new JPanel(new GridLayout(1, 2));

	private JPanel passwordPanel = new JPanel(new GridLayout(1, 2));

	private JPanel serverPanel = new JPanel(new GridLayout(1, 2));

	private JPanel portPanel = new JPanel(new GridLayout(1, 2));

	public LoginPanel(Preferences preferences) {
		this.preferences = preferences;
		Container contentPane = this;

		handlePanel.add(handleLbl);
		handlePanel.add(handleField);

		passwordPanel.add(passwordLbl);
		passwordPanel.add(passwordField);

		serverPanel.add(serverLbl);
		serverPanel.add(serverField);

		portPanel.add(portLbl);
		portPanel.add(portField);

		guestLoginCheckBox
				.setToolTipText("Dont enter a name to login as an unamed guest");

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
		contentPane.add(timesealEnabledCheckBox, constraints);

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = insets;
		contentPane.add(autoLoginCheckBox, constraints);

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
		guestLoginCheckBox.setSelected(preferences.getLoginPreferences()
				.isDefaultGuestEnabled());
		timesealEnabledCheckBox.setSelected(preferences.getLoginPreferences()
				.isDefaultTimesealEnabled());

		autoLoginCheckBox.setSelected(preferences.getLoginPreferences()
				.isAutoLogin());

		timesealEnabledCheckBox.setSelected(preferences.getLoginPreferences()
				.isDefaultTimesealEnabled());

		serverField.setText(preferences.getLoginPreferences().getServer());
		portField.setText(""
				+ preferences.getLoginPreferences().getServerPort());

		logonButton.addActionListener(this);
		guestLoginCheckBox.addActionListener(this);
		timesealEnabledCheckBox.addActionListener(this);

		adjustToCheckBoxControls();

		passwordField.addActionListener(this);
		handleField.addActionListener(this);
		serverField.addActionListener(this);
		portField.addActionListener(this);

	}

	public void addActionListener(ActionListener listener) {
		this.listener = listener;
	}

	public void disableTimeseal() {
		timesealEnabledCheckBox.setEnabled(false);
		LOGGER.error("Timeseal");
	}

	public void disableServer() {
		serverField.setEnabled(false);
		serverLbl.setEnabled(false);
		LOGGER.error("Disabled server");
	}

	public void disablePort() {
		portField.setEnabled(false);
		portLbl.setEnabled(false);
		LOGGER.error("Disabled port");
	}

	public void disableAutoLogin() {
		autoLoginCheckBox.setEnabled(false);
		LOGGER.error("Disabled auto login");
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
		preferences.getLoginPreferences().setServer(serverField.getText());
		preferences.getLoginPreferences().setServerPort(
				Integer.parseInt(portField.getText()));
		preferences.getLoginPreferences().setAutoLogin(
				autoLoginCheckBox.isSelected());
		ResourceManagerFactory.getManager().savePerferences(preferences);
	}

	private void adjustToCheckBoxControls() {
		if (guestLoginCheckBox.isSelected()) {
			passwordField.setText("");
			passwordField.setEnabled(false);
			handleField.setEnabled(true);
			passwordLbl.setEnabled(false);
		} else {
			passwordField.setEnabled(true);
			passwordLbl.setEnabled(true);
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

	public boolean isLoggingInAsGuest() {
		return guestLoginCheckBox.isSelected();
	}

	public boolean isTimesealEnabled() {
		return timesealEnabledCheckBox.isSelected();
	}

	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == guestLoginCheckBox || source == timesealEnabledCheckBox) {
			adjustToCheckBoxControls();
		} else if (source == logonButton || source == passwordField) {
			String handleText = handleField.getText();
			String passwordText = new String(passwordField.getPassword());

			if (handleText.length() != 0
					&& (handleText.length() < 3 || handleText.length() > 17)) {
				JOptionPane.showMessageDialog(this,
						"Handle must be between 3 and 17 chracters.");
			} else if (handleText.length() != 0
					&& !handleText.matches("[a-zA-Z]*")) {
				JOptionPane.showMessageDialog(this,
						"Handle must contain only letters.");
			} else if (!isLoggingInAsGuest()
					&& (handleText == null || handleText.equals(""))) {
				JOptionPane.showMessageDialog(this,
						"You must enter a handle before logging in.");
			} else if (!isLoggingInAsGuest()
					&& (passwordText == null || passwordText.equals(""))) {
				JOptionPane.showMessageDialog(this,
						"You must enter a password before logging in.");
			} else {
				saveOptions();
				listener.actionPerformed(event);
			}
		}
	}
}
