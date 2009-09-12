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
import decaf.gui.widgets.Disposable;
import decaf.messaging.inboundevent.inform.BugWhoGEvent;
import decaf.messaging.outboundevent.OutboundEvent;

public class BugWhoGEventAdapter implements Subscriber, Disposable {

	private static final Logger LOGGER = Logger
			.getLogger(BugWhoGEventAdapter.class);

	private GamesInProgressPanel panel;

	private Subscription subscription;

	private Timer refresher;

	private boolean running = false;

	public BugWhoGEventAdapter() {
		refresher = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		refresher.setInitialDelay(10);

	}

	public void dispose() {
		stop();
	}

	public GamesInProgressPanel getPanel() {
		return panel;
	}

	public void inform(BugWhoGEvent event) {
		panel.setGames(event.getGames());
	}

	public void obsGame(int gameId) {
		EventService.getInstance().publish(
				new OutboundEvent("obs " + gameId, false));
	}

	protected void refresh() {
		EventService.getInstance().publish(
				new OutboundEvent("bugwho g", true, BugWhoGEvent.class));
	}

	public void setPanel(GamesInProgressPanel panel) {
		this.panel = panel;
	}

	public void start() {
		if (!running) {
			subscription = new Subscription(BugWhoGEvent.class, this);
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

}