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

package decaf.gui;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.messaging.inboundevent.game.GameStartEvent;
import decaf.messaging.inboundevent.inform.PartnershipCreatedEvent;
import decaf.messaging.inboundevent.inform.PartnershipEndedEvent;
import decaf.messaging.inboundevent.inform.UserNameChangedEvent;

public class User implements Subscriber {

	private static final Logger LOGGER = Logger.getLogger(User.class);

	private boolean isGuest;

	private static User singletonInstance;

	private boolean isConnected;

	private String handle;

	private String bughousePartner;

	private User() {

		EventService
				.getInstance()
				.subscribe(
						new Subscription(
								decaf.messaging.inboundevent.inform.PartnershipCreatedEvent.class,
								null, this));
		EventService
				.getInstance()
				.subscribe(
						new Subscription(
								decaf.messaging.inboundevent.inform.PartnershipEndedEvent.class,
								null, this));
		EventService
				.getInstance()
				.subscribe(
						new Subscription(
								decaf.messaging.inboundevent.inform.UserNameChangedEvent.class,
								null, this));
	}

	public void inform(UserNameChangedEvent event) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Receibved UserNameChangedEVent userName is now: "
					+ event.getUserName());
		}
		handle = event.getUserName();
		this.isGuest = event.isGuest();
	}

	public void inform(PartnershipCreatedEvent partnershipcreatedevent) {
		bughousePartner = partnershipcreatedevent.getPartnersName();
	}

	public void inform(PartnershipEndedEvent partnershipendedevent) {
		bughousePartner = null;
	}

	public static User getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new User();
		}
		return singletonInstance;
	}

	public static void reset() {
		singletonInstance = null;
	}

	public String getHandle() {
		return handle;
	}

	public String getBughousePartner() {
		return bughousePartner;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public boolean isPlaying(GameStartEvent gameStartEvent) {
		return gameStartEvent.getWhiteName().equalsIgnoreCase(getHandle())
				|| gameStartEvent.getBlackName().equalsIgnoreCase(getHandle());
	}

	public boolean isGuest() {
		return isGuest;
	}

	public void setGuest(boolean isGuest) {
		this.isGuest = isGuest;
	}

}