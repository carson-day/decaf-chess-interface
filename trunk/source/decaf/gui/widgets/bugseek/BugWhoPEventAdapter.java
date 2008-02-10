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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.BugChessAreaController;
import decaf.gui.ChessAreaController;
import decaf.gui.GUIManager;
import decaf.gui.GameNotificationListener;
import decaf.gui.widgets.Disposable;
import decaf.messaging.ics.nongameparser.ParserUtil;
import decaf.messaging.inboundevent.inform.BugWhoGEvent;
import decaf.messaging.inboundevent.inform.BugWhoPEvent;
import decaf.messaging.outboundevent.OutboundEvent;

public class BugWhoPEventAdapter  implements Subscriber,Disposable {

	private static final Logger LOGGER = Logger
	.getLogger(BugWhoPEventAdapter.class);
	
	private AvailableTeamsPanel panel;

	private Subscription subscription;

	private Timer refresher;

	private boolean running = false;

	public BugWhoPEventAdapter() {
		refresher = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		refresher.setInitialDelay(10);

	}
	
	
	
	public AvailableTeamsPanel getPanel() {
		return panel;
	}



	public void setPanel(AvailableTeamsPanel panel) {
		this.panel = panel;
	}



	public void dispose()
	{
		stop();
	}

	protected void refresh() {
		EventService.getInstance().publish(new OutboundEvent("bugwho p", true,BugWhoPEvent.class));
	}

	public void start() {
		if (!running) {
			subscription = new Subscription(BugWhoPEvent.class, this);
			EventService.getInstance().subscribe(subscription);

			refresher.start();
			running = true;
		}
	}

	public void stop() {
		if (running) {
			refresher.stop();
			EventService.getInstance().unsubscribe(subscription);
			running = false;
		}
	}

	public void inform(BugWhoPEvent event) {
		panel.setTeams(event.getTeams());
	}

	public void matchPlayer(String handle,String timeControl) {
		EventService.getInstance().publish(
				new OutboundEvent("match " + ParserUtil.removeTitles(handle) + " " + timeControl + " bughouse", false));
	}


}