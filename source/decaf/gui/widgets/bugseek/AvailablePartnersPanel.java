package decaf.gui.widgets.bugseek;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import decaf.gui.GUIManager;
import decaf.resources.ResourceManagerFactory;
import decaf.util.StringUtility;

public class AvailablePartnersPanel extends JPanel {

	private static final Logger LOGGER = Logger
			.getLogger(AvailableTeamsPanel.class);

	private JPanel innerPanel = new JPanel();

	private JScrollPane scrollPane = new JScrollPane(innerPanel);

	private BugWhoUEventAdapter adapter;

	private JComboBox ratingFilter;

	private List<UnpartneredBugger> buggers = new LinkedList<UnpartneredBugger>();

	public AvailablePartnersPanel(final BugWhoUEventAdapter adapter) {
		this.adapter = adapter;
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(new JButton(new AbstractAction("Partner All") {
			public void actionPerformed(ActionEvent e) {
				synchronized (this) {
					for (UnpartneredBugger bugger : buggers) {
						adapter.partnerPlayer(bugger.getHandle());
					}
				}
			}
		}));

		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		ratingFilter = new JComboBox(new Object[] { new Integer(0),
				new Integer(1), new Integer(1000), new Integer(1200),
				new Integer(1400), new Integer(1500), new Integer(1600),
				new Integer(1700), new Integer(1800), new Integer(1900),
				new Integer(2000), new Integer(2100), new Integer(2200) });
		ratingFilter.setSelectedItem(new Integer(GUIManager.getInstance()
				.getPreferences().getRememberAvailablePartnersFilter()));
		ratingFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIManager.getInstance().getPreferences()
						.setRememberAvailablePartnersFilter(
								((Integer) ratingFilter.getSelectedItem())
										.intValue());
				ResourceManagerFactory.getManager().savePerferences(
						GUIManager.getInstance().getPreferences());
			}
		});
		topPanel.add(new JLabel("Rating >= "));
		topPanel.add(ratingFilter);

		add(topPanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.SOUTH);

		innerPanel.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		scrollPane.setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
		setBackground(GUIManager.getInstance().getPreferences()
				.getBoardPreferences().getBackgroundControlsColor());
	}

	private void filterBuggers() {
		int filter = ((Integer) ratingFilter.getSelectedItem()).intValue();

		for (int i = 0; i < buggers.size(); i++) {
			int buggerRating = 0;

			try {
				buggerRating = Integer.parseInt(buggers.get(i).getRating());
			} catch (NumberFormatException e) {
			}

			if (buggerRating < filter) {
				buggers.remove(i);
				i--;
			}
		}
		Collections.sort(buggers);
	}

	public void setAvailablePartners(List<UnpartneredBugger> buggers) {
		synchronized (this) {
			this.buggers = buggers;

			filterBuggers();

			innerPanel.removeAll();

			innerPanel.setLayout(new GridLayout(buggers.size() / 3 + 1, 3));

			for (final UnpartneredBugger bugger : buggers) {
				innerPanel.add(new JButton(new AbstractAction(
						buildButtonText(bugger)) {
					public void actionPerformed(ActionEvent e) {
						adapter.partnerPlayer(bugger.getHandle());
					}
				}));
			}

			Component[] components = innerPanel.getComponents();

			for (int i = 0; i < components.length; i++) {
				components[i].setFont(GUIManager.getInstance().getPreferences()
						.getBoardPreferences().getControlLabelTextProperties()
						.getFont());
			}

			innerPanel.invalidate();
			scrollPane.invalidate();
			validate();
			scrollPane.repaint();
		}
	}

	private String buildButtonText(UnpartneredBugger bugger) {
		String result = bugger.getRating();
		if (bugger.getRatingModifier() != 0) {
			result += bugger.getRatingModifier();
		}
		result += " ";

		if (bugger.getHandleModifier() != 0) {
			result += bugger.getHandleModifier();
		}
		result += bugger.getHandle();

		result = StringUtility.trimOrRightPad(result, 20);
		return result;
	}
}
