package decaf.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import decaf.gui.GUIManager;
import decaf.resources.ResourceManagerFactory;
import decaf.util.LaunchBrowser;

public class AboutDialog extends JDialog {
	public AboutDialog() {
		setTitle("About");
		getContentPane().setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(
				new JLabel(ResourceManagerFactory.getManager().getString(
						"Decaf", "version")), constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(new JButton(new ImageIcon(GUIManager.DECAF_ICON)),
				constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane()
				.add(
						new JLabel(
								"Current Developers:  Carson Day (cday) , Sergei Kozyrenko(kozyr)"),
						constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.weightx = 1.0;
		constraints.anchor = GridBagConstraints.EAST;
		getContentPane().add(new JLabel("GPLv3+: "), constraints);

		constraints.gridx = 1;
		constraints.gridy = 3;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		getContentPane().add(
				new JButton(new AbstractAction(ResourceManagerFactory
						.getManager().getString("Decaf", "license")) {
					public void actionPerformed(ActionEvent e) {
						LaunchBrowser.openURL(ResourceManagerFactory
								.getManager().getString("Decaf", "license"));
					}
				}), constraints);

		constraints.gridx = 0;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		getContentPane().add(new JLabel("Project url: "), constraints);

		constraints.gridx = 1;
		constraints.gridy = 4;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		getContentPane().add(
				new JButton(new AbstractAction(ResourceManagerFactory
						.getManager().getString("Decaf", "website")) {
					public void actionPerformed(ActionEvent e) {
						LaunchBrowser.openURL(ResourceManagerFactory
								.getManager().getString("Decaf", "website"));
					}
				}), constraints);

		constraints.gridx = 0;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.EAST;
		getContentPane().add(new JLabel("Report A Bug: "), constraints);

		constraints.gridx = 1;
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.WEST;
		getContentPane().add(
				new JButton(new AbstractAction(ResourceManagerFactory
						.getManager().getString("Decaf", "bugs")) {
					public void actionPerformed(ActionEvent e) {
						LaunchBrowser.openURL(ResourceManagerFactory
								.getManager().getString("Decaf", "bugs"));
					}
				}), constraints);

		constraints.gridx = 0;
		constraints.gridy = 6;
		constraints.gridwidth = 2;
		constraints.weightx = 0;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(new JLabel("     "), constraints);

		constraints.gridx = 0;
		constraints.gridy = 7;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(
				new JLabel(ResourceManagerFactory.getManager().getString(
						"Decaf", "thanks1")), constraints);

		constraints.gridx = 0;
		constraints.gridy = 8;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(
				new JLabel(" "
						+ ResourceManagerFactory.getManager().getString(
								"Decaf", "thanks2") + " "), constraints);

		constraints.gridx = 0;
		constraints.gridy = 9;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(new JLabel("     "), constraints);

		constraints.gridx = 0;
		constraints.gridy = 10;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(
				new JLabel(ResourceManagerFactory.getManager().getString(
						"Decaf", "thanks3")), constraints);

		constraints.gridx = 0;
		constraints.gridy = 11;
		constraints.gridwidth = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		getContentPane().add(new JLabel("     "), constraints);

		pack();
	}
}
