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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.BugChessAreaController;
import decaf.gui.ChessAreaController;
import decaf.gui.GUIManager;
import decaf.gui.GameNotificationListener;
import decaf.gui.widgets.Disposable;
import decaf.messaging.inboundevent.inform.SoughtEvent;
import decaf.messaging.outboundevent.OutboundEvent;

public class SeekGraphEventAdapter implements Subscriber,
		GameNotificationListener, AcceptSeekListener, Disposable {

	private SeekGraph graph;

	private Subscription subscription;

	private Timer refresher;

	private boolean running = false;

	public SeekGraphEventAdapter(SeekGraph graph) {
		this.graph = graph;

		refresher = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		refresher.setInitialDelay(10);
		GUIManager.getInstance().addGameNotificationListener(this);
	}

	public void acceptedSeek(int adNumber) {
		EventService.getInstance().publish(
				new OutboundEvent("play " + adNumber, false));
	}

	public void bugGameEnded(BugChessAreaController controller) {
		if (controller.isPlaying()) {
			start();
		}
	}

	public void bugGameStarted(BugChessAreaController controller) {
		if (controller.isPlaying()) {
			stop();
		}
	}

	public void dispose() {
		GUIManager.getInstance().removeGameNotificationListener(this);
		stop();
	}

	public void gameEnded(ChessAreaController controller) {
		if (controller.isPlaying()) {
			start();
		}
	}

	public void gameStarted(ChessAreaController controller) {
		if (controller.isPlaying()) {
			stop();
		}
	}

	public void inform(SoughtEvent event) {
		// System.err.println("inform called");
		graph.replaceBy(event.getSeeks());
	}

	protected void refresh() {
		EventService.getInstance().publish(
				new OutboundEvent("sought", true, SoughtEvent.class));
	}

	public void start() {
		if (!running) {
			this.graph.addAcceptSeekListener(this);

			subscription = new Subscription(SoughtEvent.class, this);
			EventService.getInstance().subscribe(subscription);

			refresher.start();
			running = true;
		}
	}

	public void stop() {
		if (running) {
			refresher.stop();
			EventService.getInstance().unsubscribe(subscription);
			graph.removeAcceptSeekListener(this);
			running = false;
		}
	}
}
