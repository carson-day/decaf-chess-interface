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
import decaf.gui.User;
import decaf.resources.ResourceManagerFactory;
import decaf.util.StringUtility;

public class AvailablePartnersPanel extends JPanel {

	private static final Logger LOGGER = Logger
			.getLogger(AvailableTeamsPanel.class);

	private boolean enableMaxRatingFilter = false;
	
	private JPanel innerPanel = new JPanel();

	private JScrollPane scrollPane = new JScrollPane(innerPanel);

	private BugWhoUEventAdapter adapter;

	private JComboBox ratingFilterMINIMUM;
	
	private JComboBox ratingFilterMAXIMUM;

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
		Integer[] MIN_RATINGS = {0,1,1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,2100,2200};
			
			/* new Object[] { new Integer(0),
				new Integer(1), new Integer(1000), new Integer(1200),
				new Integer(1400), new Integer(1500), new Integer(1600),
				new Integer(1700), new Integer(1800), new Integer(1900),
				new Integer(2000), new Integer(2100), new Integer(2200) };
			 * */
		Integer[] MAX_RATINGS = {0,1,1000,1100,1200,1300,1400,1500,1600,1700,1800,1900,2000,2100,2200,2300,2400,2500,9999};
			/*new Object[] { new Integer(0),
				new Integer(1), new Integer(1000), new Integer(1200),
				new Integer(1400), new Integer(1500), new Integer(1600),
				new Integer(1700), new Integer(1800), new Integer(1900),
				new Integer(2000), new Integer(2100), new Integer(2200),
				new Integer(2500), new Integer(9999)  };*/
		
		ratingFilterMINIMUM = new JComboBox(MIN_RATINGS);
		if (enableMaxRatingFilter) { ratingFilterMAXIMUM = new JComboBox(MAX_RATINGS); }
		
		ratingFilterMINIMUM.setSelectedItem(new Integer(GUIManager.getInstance()
				.getPreferences().getRememberAvailablePartnersFilter()));
		if (enableMaxRatingFilter) { ratingFilterMAXIMUM.setSelectedIndex(MAX_RATINGS.length-1); }
		
		ratingFilterMINIMUM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GUIManager.getInstance().getPreferences()
						.setRememberAvailablePartnersFilter(
								((Integer) ratingFilterMINIMUM.getSelectedItem())
										.intValue());
				ResourceManagerFactory.getManager().savePerferences(
						GUIManager.getInstance().getPreferences());
			}
		});
		
		
		topPanel.add(new JLabel("Rating >= "));
		topPanel.add(ratingFilterMINIMUM);
		
		if (enableMaxRatingFilter) {
		topPanel.add(new JLabel(" && Rating <= "));
		topPanel.add(ratingFilterMAXIMUM);
		}


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
		int MIN_filter = ((Integer) ratingFilterMINIMUM.getSelectedItem()).intValue();
		int MAX_filter = 0;
		if (enableMaxRatingFilter) MAX_filter = ((Integer) ratingFilterMAXIMUM.getSelectedItem()).intValue();

		for (int i = 0; i < buggers.size(); i++) {
			int buggerRating = 0;

			try {
				buggerRating = Integer.parseInt(buggers.get(i).getRating());
			} catch (NumberFormatException e) {
			}

			//if ()
			if (buggerRating < MIN_filter) { buggers.remove(i); i--; } else
			if (enableMaxRatingFilter && buggerRating > MAX_filter) { buggers.remove(i); i--; }
		}
		Collections.sort(buggers);
	}

	public void setAvailablePartners(List<UnpartneredBugger> buggers) {
		synchronized (this) {
			this.buggers = buggers;

			filterBuggers();

			innerPanel.removeAll();

			int COLS = 3;
			innerPanel.setLayout(new GridLayout(buggers.size() / 3 + 1, COLS));

			for (final UnpartneredBugger bugger : buggers) {
			//for(int i=0;i<buggers.size();i++) {
				//final UnpartneredBugger bugger = buggers.get(i);
				if (bugger.getHandle().equals(User.getInstance().getHandle())) { continue; }
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
