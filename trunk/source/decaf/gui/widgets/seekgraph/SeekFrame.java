/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Sergei Kozyrenko (kozyr82@gmail.com)
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
package decaf.gui.widgets.seekgraph;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import decaf.gui.GUIManager;
import decaf.gui.pref.Preferences;
import decaf.gui.pref.SeekGraphPreferences;
import decaf.resources.ResourceManagerFactory;

public class SeekFrame extends JFrame {

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

			GUIManager.getInstance().getPreferences().setRememberSeekDimension(
					dimension);
			GUIManager.getInstance().getPreferences().setRememberSeekLocation(
					location);

			ResourceManagerFactory.getManager().savePerferences(
					GUIManager.getInstance().getPreferences());
		}

	}

	private SeekGraph seekGraph;

	private SeekGraphEventAdapter eventAdapter;

	private SeekFrame thisFrame = this;

	public SeekFrame() {
		setTitle("Seek Graph");
		setSize(GUIManager.getInstance().getPreferences()
				.getRememberSeekDimension());
		setLocation(GUIManager.getInstance().getPreferences()
				.getRememberSeekLocation());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setLayout(new GridLayout(1, 1));

		seekGraph = new SeekGraph();
		eventAdapter = new SeekGraphEventAdapter(seekGraph);
		add(seekGraph);

		setSeekGraphPreferences(GUIManager.getInstance().getPreferences()
				.getSeekGraphPreferences());

		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				// don't care
			}

			public void windowLostFocus(WindowEvent e) {
				seekGraph.hideSelectMenu();
			}
		});
		addComponentListener(new RememberPositionFrameListener());

		eventAdapter.start();
	}

	@Override
	public void dispose() {
		eventAdapter.dispose();
		super.dispose();
	}

	public void setPreferences(Preferences preferences) {
		setSeekGraphPreferences(preferences.getSeekGraphPreferences());
	}

	private void setSeekGraphPreferences(SeekGraphPreferences sgp) {
		seekGraph.setVStart(sgp.getVstart());
		seekGraph.setVScale(sgp.getVscale());

		seekGraph.setHStart(sgp.getHstart());
		seekGraph.setHScale(sgp.getHscale());

		seekGraph.setComputerColor(sgp.getComputerColor());
		seekGraph.setRatedColor(sgp.getRatedColor());
		seekGraph.setUnratedColor(sgp.getUnratedColor());
		seekGraph.setManyColor(sgp.getManyColor());

		seekGraph.setShowComputerSeeks(sgp.isShowComputer());
		seekGraph.setShowUnratedSeeks(sgp.isShowUnrated());

		seekGraph.redoLegend();

		seekGraph.repaint();
	}
}
