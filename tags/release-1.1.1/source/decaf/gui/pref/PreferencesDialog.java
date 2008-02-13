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
package decaf.gui.pref;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import decaf.gui.GUIManager;
import decaf.resources.ResourceManagerFactory;

public class PreferencesDialog extends JDialog {
	private JTabbedPane tabbedPane = new JTabbedPane();

	Preferences preferences = ResourceManagerFactory.getManager()
			.loadPreferences();

	private PreferencesTab[] preferenceTabs = new PreferencesTab[] {
			new GeneralTab(), new ChessGuiTab(preferences), new LayoutTab(),
			new ChessTab(), new BughouseTab(), new ConsoleTextTab(),
			new ConsoleTab(), new SpeechTab() , new SeekGraphTab() };

	private Action saveAction = new AbstractAction("Save") {
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < preferenceTabs.length; i++) {
				preferenceTabs[i].save(preferences);
			}
			ResourceManagerFactory.getManager().savePerferences(preferences);
			GUIManager.getInstance().setPreferences(preferences);
		}
	};

	private Action loadDefaultsAction = new AbstractAction("Load Defaults") {
		public void actionPerformed(ActionEvent e) {
			preferences = ResourceManagerFactory.getManager()
					.loadDefaultPreferences();
			for (int i = 0; i < preferenceTabs.length; i++) {
				preferenceTabs[i].load(preferences);
			}

		}
	};

	private Action loadLastSavedAction = new AbstractAction("Load Last Saved") {
		public void actionPerformed(ActionEvent e) {
			preferences = ResourceManagerFactory.getManager().loadPreferences();
			for (int i = 0; i < preferenceTabs.length; i++) {
				preferenceTabs[i].load(preferences);
			}
		}
	};

	private Action closeAction = new AbstractAction("Close") {
		public void actionPerformed(ActionEvent e) {
			setVisible(false);
		}
	};

	public PreferencesDialog(JFrame frame) {
		super(frame);
		setTitle("Preferences");
		setLayout(new BorderLayout());

		for (int i = 0; i < preferenceTabs.length; i++) {
			tabbedPane.add(preferenceTabs[i].getTitle(), preferenceTabs[i]);
			preferenceTabs[i].load(preferences);
		}

		add(tabbedPane, BorderLayout.CENTER);
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonsPanel.add(new JButton(saveAction));
		buttonsPanel.add(new JButton(loadDefaultsAction));
		buttonsPanel.add(new JButton(loadLastSavedAction));
		buttonsPanel.add(new JButton(closeAction));
		add(buttonsPanel, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent arg0) {
				for (int i = 0; i < preferenceTabs.length; i++)
				{
					preferenceTabs[i].dispose();
				}
				dispose();
			}
			
		});

		pack();
	}

}
