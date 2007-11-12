/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2007  Carson Day (carsonday@gmail.com)
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
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;

import decaf.com.inboundevent.inform.PartnershipCreatedEvent;
import decaf.com.inboundevent.inform.PartnershipEndedEvent;
import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.util.User;

public class PartnerWidget extends JComponent {
	private static final User user = User.getInstance();

	private static final EventService eventService = EventService.getInstance();

	private static final long SLEEP_TIME = 1000L;

	private String currentPartner = null;

	private PartnershipSubscriber partnershipSubscriber = new PartnershipSubscriber();

	public PartnerWidget() {
		eventService.subscribe(new Subscription(PartnershipCreatedEvent.class,
				null, partnershipSubscriber));
		eventService.subscribe(new Subscription(PartnershipEndedEvent.class,
				null, partnershipSubscriber));
	}

	/*
	 * public Dimension getPreferredSize() { return new Dimension(140,20); }
	 * 
	 * public Dimension getMinimumSize() { return new Dimension(140,20); }
	 * 
	 * public Dimension getMaximumSize() { return new Dimension(140,20); }
	 */

	protected void paintComponent(Graphics g) {

		int height = getSize().height;
		int width = getSize().width;

		g.setColor(Color.gray);
		g.drawRect(0, 0, width - 1, height - 1);

		g.setColor(Color.black);
		g.setFont(getFont());

		FontMetrics fontMetrics = g.getFontMetrics();

		String text = currentPartner == null ? "" : currentPartner;

		setToolTipText(currentPartner == null ? "You currently do not have a bughouse partner."
				: "Your bughouse partner is " + currentPartner);
		int textWidth = fontMetrics.stringWidth(text);
		int ascent = g.getFontMetrics().getAscent();

		g.drawString(text, (width - textWidth) / 2, (height - ascent) / 2
				+ ascent);
	}

	public class PartnershipSubscriber implements Subscriber {
		public void inform(PartnershipCreatedEvent event) {
			currentPartner = event.getPartnersName();
			repaint();
		}

		public void inform(PartnershipEndedEvent event) {
			currentPartner = null;
			repaint();
		}
	}
}