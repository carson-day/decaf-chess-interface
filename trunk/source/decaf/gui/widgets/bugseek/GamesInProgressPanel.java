package decaf.gui.widgets.bugseek;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import decaf.gui.GUIManager;

public class GamesInProgressPanel extends JPanel {
	private JPanel innerPanel = new JPanel();

	private JScrollPane scrollPane = new JScrollPane(innerPanel);

	private BugWhoGEventAdapter adapter;

	private List<Container> panels = new LinkedList<Container>();

	public GamesInProgressPanel(BugWhoGEventAdapter adapter) {
		this.adapter = adapter;
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		innerPanel.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		scrollPane.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
	}

	public void setGames(List<BugWhoGGame> games) {
		synchronized (this) {
			innerPanel.removeAll();

			for (Container container : panels) {
				container.removeAll();
			}
			panels.clear();

			if (games.size() > 0) {
				innerPanel
						.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
				for (final BugWhoGGame game : games) {
					JButton button = new JButton(new AbstractAction("observe") {
						public void actionPerformed(ActionEvent event) {
							adapter.obsGame(game.getGame1Id());
						}
					});
					JLabel label = new JLabel(game.getGame1Description());
					JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
					label.setFont(GUIManager.getInstance().getPreferences()
							.getBoardPreferences()
							.getControlLabelTextProperties().getFont());
					label.setBackground(GUIManager.getInstance()
							.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
					label.setForeground(GUIManager.getInstance()
							.getPreferences().getBoardPreferences()
							.getControlLabelTextProperties().getForeground());
					panel.setBackground(GUIManager.getInstance()
							.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
					panel.setAlignmentY(0);
					panel.add(button);
					panel.add(label);
					panels.add(panel);

					JButton button2 = new JButton(
							new AbstractAction("observe") {
								public void actionPerformed(ActionEvent event) {
									adapter.obsGame(game.getGame2Id());
								}
							});
					JLabel label2 = new JLabel(game.getGame2Description());
					JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
					label2.setFont(GUIManager.getInstance().getPreferences()
							.getBoardPreferences()
							.getControlLabelTextProperties().getFont());
					label2.setBackground(GUIManager.getInstance()
							.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
					label2.setForeground(GUIManager.getInstance()
							.getPreferences().getBoardPreferences()
							.getControlLabelTextProperties().getForeground());
					panel2.setBackground(GUIManager.getInstance()
							.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
					panel2.setAlignmentY(0);
					panel2.add(button2);
					panel2.add(label2);
					panels.add(panel2);

					JPanel sandwichPanel = new JPanel(new GridBagLayout());
					sandwichPanel.setBackground(GUIManager.getInstance()
							.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
					GridBagConstraints constraints = new GridBagConstraints();
					constraints.gridx = 0;
					constraints.gridy = 0;
					constraints.anchor = GridBagConstraints.SOUTHWEST;
					sandwichPanel.add(panel, constraints);

					constraints.gridx = 0;
					constraints.gridy = 1;
					constraints.anchor = GridBagConstraints.NORTHWEST;
					sandwichPanel.add(panel2, constraints);

					panels.add(sandwichPanel);
					innerPanel.add(sandwichPanel);
				}
				innerPanel.add(Box.createVerticalGlue());
			} else {
				innerPanel.setLayout(new BorderLayout());
				JLabel label = new JLabel("No games in progress.",
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
		}
		innerPanel.invalidate();
		scrollPane.invalidate();
		validate();
		scrollPane.repaint();
	}
}
