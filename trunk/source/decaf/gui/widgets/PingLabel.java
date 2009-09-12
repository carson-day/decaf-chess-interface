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
package decaf.gui.widgets;

import java.awt.Color;

import javax.swing.JLabel;

import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.messaging.inboundevent.inform.ResponseTimeEvent;
import decaf.util.StringUtility;

public class PingLabel extends JLabel implements Preferenceable {

	public class PingHandler implements Subscriber {
		public PingHandler() {
		}

		public void inform(ResponseTimeEvent event) {
			if (event.getResponseTimeMillis() < 400) {
				setForeground(Color.GREEN.darker());
			} else {
				setForeground(Color.RED.darker());
			}

			String responseTime = StringUtility.padCharsToLeft(""
					+ event.getResponseTimeMillis(), ' ', 4);
			setText("  " + responseTime + "ms   ");
			setToolTipText("<html>A rough estimation of ping time <br/>based on the difference <br/>between the last  <br/>message sent and the  <br/>last message received.</html>");
			repaint();
		}
	}

	private PingHandler pingHandler;

	private Preferences preferences;

	public PingLabel(Preferences preferences) {
		pingHandler = new PingHandler();
		setPreferences(preferences);
		EventService.getInstance().subscribe(
				new Subscription(ResponseTimeEvent.class, null, pingHandler));
		setText("           ");
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences skin) {
		this.preferences = skin;
		// setFont(skin.getInputTextProperties().getFont());
		// setBackground(skin.getTelnetPanelBackground());
		// setForeground(Color.GREEN.darker());
	}

}