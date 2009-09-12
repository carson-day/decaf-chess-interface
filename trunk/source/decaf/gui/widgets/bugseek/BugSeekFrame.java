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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import decaf.gui.BugChessAreaController;
import decaf.gui.ChessAreaController;
import decaf.gui.GUIManager;
import decaf.gui.GameNotificationListener;
import decaf.gui.User;
import decaf.resources.ResourceManagerFactory;

public class BugSeekFrame extends JFrame implements GameNotificationListener {
	private class RememberPositionFrameListener extends ComponentAdapter {

		@Override
		public void componentMoved(ComponentEvent arg0) {
			saveSettings();
		}

		@Override
		public void componentResized(ComponentEvent arg0) {

			saveSettings();
		}

		private void saveSettings() {
			Point location = thisFrame.getLocation();
			Dimension dimension = thisFrame.getSize();

			GUIManager.getInstance().getPreferences()
					.setRememberBugSeekDimension(dimension);
			GUIManager.getInstance().getPreferences()
					.setRememberBugSeekLocation(location);

			ResourceManagerFactory.getManager().savePerferences(
					GUIManager.getInstance().getPreferences());
		}

	}

	private AvailablePartnersPanel availablePartnersPanel;

	private AvailableTeamsPanel teamsPanel;

	private GamesInProgressPanel gamesInProgress;

	private BugWhoUEventAdapter bugWhoUEventAdapter;

	private BugWhoPEventAdapter bugWhoPEventAdapter;

	private BugWhoGEventAdapter bugWhoGEventAdapter;

	private BugSeekFrame thisFrame = this;

	private JTabbedPane tabbedPane = null;

	public BugSeekFrame() {
		setTitle("Bughouse Seek");
		setSize(GUIManager.getInstance().getPreferences()
				.getRememberBugSeekDimension());
		setLocation(GUIManager.getInstance().getPreferences()
				.getRememberBugSeekLocation());
		setSize(750, 400);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(1, 1));

		bugWhoGEventAdapter = new BugWhoGEventAdapter();
		gamesInProgress = new GamesInProgressPanel(bugWhoGEventAdapter);
		bugWhoGEventAdapter.setPanel(gamesInProgress);

		bugWhoPEventAdapter = new BugWhoPEventAdapter();
		teamsPanel = new AvailableTeamsPanel(bugWhoPEventAdapter);
		bugWhoPEventAdapter.setPanel(teamsPanel);

		bugWhoUEventAdapter = new BugWhoUEventAdapter();
		availablePartnersPanel = new AvailablePartnersPanel(bugWhoUEventAdapter);
		bugWhoUEventAdapter.setPanel(availablePartnersPanel);

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Games In Progress", gamesInProgress);
		tabbedPane.addTab("Available Teams", teamsPanel);
		tabbedPane.addTab("Available Partners", availablePartnersPanel);

		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (tabbedPane.getSelectedComponent() == teamsPanel) {
					bugWhoUEventAdapter.stop();
					bugWhoPEventAdapter.start();
					bugWhoGEventAdapter.stop();
				} else if (tabbedPane.getSelectedComponent() == availablePartnersPanel) {
					bugWhoPEventAdapter.stop();
					bugWhoUEventAdapter.start();
					bugWhoGEventAdapter.stop();
				} else {
					bugWhoPEventAdapter.stop();
					bugWhoUEventAdapter.stop();
					bugWhoGEventAdapter.start();
				}
			}
		});

		GUIManager.getInstance().addGameNotificationListener(this);

		add(tabbedPane);

		addComponentListener(new RememberPositionFrameListener());

		if (User.getInstance().getBughousePartner() == null
				|| "".equals(User.getInstance().getBughousePartner())) {
			tabbedPane.setSelectedIndex(2);
		} else {
			tabbedPane.setSelectedIndex(0);
			tabbedPane.setSelectedIndex(1);
		}
	}

	public void bugGameEnded(BugChessAreaController controller) {
		if (controller.isPlaying()) {
			if (tabbedPane.getSelectedComponent() == teamsPanel) {
				bugWhoPEventAdapter.start();
			} else if (tabbedPane.getSelectedComponent() == availablePartnersPanel) {
				bugWhoUEventAdapter.start();
			} else {
				bugWhoGEventAdapter.start();
			}
		}
	}

	public void bugGameStarted(BugChessAreaController controller) {
		if (controller.isPlaying()) {
			bugWhoUEventAdapter.stop();
			bugWhoPEventAdapter.stop();
			bugWhoGEventAdapter.stop();
		}
	}

	@Override
	public void dispose() {
		bugWhoUEventAdapter.dispose();
		bugWhoPEventAdapter.dispose();
		bugWhoGEventAdapter.dispose();
		GUIManager.getInstance().removeGameNotificationListener(this);
		super.dispose();
	}

	public void gameEnded(ChessAreaController controller) {
		if (controller.isPlaying()) {
			if (tabbedPane.getSelectedComponent() == teamsPanel) {
				bugWhoPEventAdapter.start();
			} else if (tabbedPane.getSelectedComponent() == availablePartnersPanel) {
				bugWhoUEventAdapter.start();
			} else {
				bugWhoGEventAdapter.start();
			}
		}
	}

	public void gameStarted(ChessAreaController controller) {
		if (controller.isPlaying()) {
			bugWhoUEventAdapter.stop();
			bugWhoPEventAdapter.stop();
			bugWhoGEventAdapter.stop();
		}
	}
}