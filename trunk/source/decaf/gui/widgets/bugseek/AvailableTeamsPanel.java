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
package decaf.gui.widgets.bugseek;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import decaf.gui.GUIManager;
import decaf.gui.User;
import decaf.messaging.ics.nongameparser.ParserUtil;
import decaf.util.StringUtility;

public class AvailableTeamsPanel extends JPanel {

	private static final Logger LOGGER = Logger
			.getLogger(AvailableTeamsPanel.class);

	private JPanel innerPanel = new JPanel();

	private JScrollPane scrollPane = new JScrollPane(innerPanel);

	private BugWhoPEventAdapter adapter;

	private List<BugWhoPTeam> teams = new LinkedList<BugWhoPTeam>();

	private List<JPanel> panels = new LinkedList<JPanel>();

	public AvailableTeamsPanel(final BugWhoPEventAdapter adapter) {
		this.adapter = adapter;
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(new AbstractAction("Match All 1 0") {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					for (BugWhoPTeam team : teams) {
						adapter.matchPlayer(team.getPlayer1Handle(), "1 0");
						adapter.matchPlayer(team.getPlayer2Handle(), "1 0");
					}
				}
			}
		}));
		buttonPanel.add(new JButton(new AbstractAction("Match All 2 0") {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					for (BugWhoPTeam team : teams) {
						/*
						 * String myUsername = User.getInstance().getHandle();
						 * if (team.getPlayer1Handle().equals(myUsername) ||
						 * team.getPlayer2Handle().equals(myUsername)) {
						 * continue; }
						 */
						adapter.matchPlayer(team.getPlayer1Handle(), "2 0");
						adapter.matchPlayer(team.getPlayer2Handle(), "2 0");
					}
				}
			}
		}));
		buttonPanel.add(new JButton(new AbstractAction("Match All 3 0") {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					for (BugWhoPTeam team : teams) {
						adapter.matchPlayer(team.getPlayer1Handle(), "3 0");
						adapter.matchPlayer(team.getPlayer2Handle(), "3 0");
					}
				}
			}
		}));
		add(buttonPanel, BorderLayout.SOUTH);

		innerPanel.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		scrollPane.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
	}

	private JPanel buildButtonPanel(final boolean isTeam1,
			final BugWhoPTeam team) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(isTeam1 ? FlowLayout.RIGHT
				: FlowLayout.LEFT));

		if (isTeam1) {
			panel.add(new JButton(new AbstractAction("3 0") {
				public void actionPerformed(ActionEvent e) {
					adapter.matchPlayer(team.getPlayer1Handle(), "3 0");
				}
			}));
			panel.add(new JButton(new AbstractAction("2 0") {
				public void actionPerformed(ActionEvent e) {
					adapter.matchPlayer(team.getPlayer1Handle(), "2 0");
				}
			}));

			panel.add(new JButton(new AbstractAction("1 0") {
				public void actionPerformed(ActionEvent e) {
					adapter.matchPlayer(team.getPlayer1Handle(), "1 0");
				}
			}));
		} else {
			panel.add(new JButton(new AbstractAction("1 0") {
				public void actionPerformed(ActionEvent e) {
					adapter.matchPlayer(team.getPlayer2Handle(), "1 0");
				}
			}));
			panel.add(new JButton(new AbstractAction("2 0") {
				public void actionPerformed(ActionEvent e) {
					adapter.matchPlayer(team.getPlayer2Handle(), "2 0");
				}
			}));
			panel.add(new JButton(new AbstractAction("3 0") {
				public void actionPerformed(ActionEvent e) {
					adapter.matchPlayer(team.getPlayer2Handle(), "3 0");
				}
			}));
		}

		if (!isTeamActive(team)) {
			Component[] buttons = panel.getComponents();
			for (int i = 0; i < buttons.length; i++) {
				buttons[i].setEnabled(false);
			}
		}
		panels.add(panel);
		panel.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		return panel;
	}

	private JLabel buildLabel(String text, int chars) {
		JLabel result = new JLabel(StringUtility.trimOrRightPad(text, chars),
				SwingConstants.LEFT);
		result.setFont(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getControlLabelTextProperties()
				.getFont());
		result.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		result.setForeground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getControlLabelTextProperties()
				.getForeground());
		return result;
	}

	private JPanel buildPlayerLabelPanel(BugWhoPTeam team) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(buildLabel(team.getPlayer1Rating(), 4));
		panel.add(buildLabel(team.getPlayer1Modifier()
				+ team.getPlayer1Handle(), 10));
		panel.add(buildLabel(team.getPlayer2Rating(), 4));
		panel.add(buildLabel(team.getPlayer2Modifier()
				+ team.getPlayer2Handle(), 10));
		panels.add(panel);
		panel.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		return panel;
	}

	private boolean isTeamActive(BugWhoPTeam team) {

		return (team.getPlayer1Modifier() == 0 || team.getPlayer1Modifier() == '.')
				&& (team.getPlayer2Modifier() == 0 || team.getPlayer2Modifier() == '.')
				&& !ParserUtil.removeTitles(team.getPlayer1Handle()).equals(
						User.getInstance().getHandle())
				&& !ParserUtil.removeTitles(team.getPlayer2Handle()).equals(
						User.getInstance().getHandle());

	}

	public void setTeams(List<BugWhoPTeam> teams) {
		synchronized (this) {
			this.teams = teams;
			innerPanel.removeAll();
			for (JPanel panel : panels) {
				panel.removeAll();
			}
			panels.clear();

			if (teams.size() > 0) {
				innerPanel.setLayout(new GridBagLayout());
				GridBagConstraints constraints = new GridBagConstraints();
				int y = 0;

				for (final BugWhoPTeam team : teams) {
					constraints.gridx = 0;
					constraints.gridy = y;
					constraints.weightx = 0.0;
					constraints.weighty = 0.0;
					innerPanel.add(buildButtonPanel(true, team), constraints);

					constraints.gridx = 1;
					constraints.gridy = y;
					constraints.weightx = 1.0;
					constraints.weighty = 0.0;
					innerPanel.add(buildPlayerLabelPanel(team), constraints);

					constraints.gridx = 2;
					constraints.gridy = y;
					constraints.weightx = 0.0;
					constraints.weighty = 0.0;
					innerPanel.add(buildButtonPanel(false, team), constraints);

					y++;

				}
			} else {
				innerPanel.setLayout(new BorderLayout());
				JLabel label = new JLabel("No available teams.",
						SwingConstants.CENTER);
				label.setFont(GUIManager.getInstance().getPreferences()
						.getBoardPreferences().getControlLabelTextProperties()
						.getFont());
				label.setBackground(GUIManager.getInstance().getPreferences()
						.getBoardPreferences().getBackgroundControlsColor());
				label.setForeground(GUIManager.getInstance().getPreferences()
						.getBoardPreferences().getControlLabelTextProperties()
						.getForeground());
				innerPanel.add(label, BorderLayout.CENTER);
			}
			innerPanel.invalidate();
			scrollPane.invalidate();
			validate();
			scrollPane.repaint();
		}
	}
}
