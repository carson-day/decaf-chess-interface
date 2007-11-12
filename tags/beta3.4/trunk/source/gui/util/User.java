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

package decaf.gui.util;

import org.apache.log4j.Logger;

import decaf.com.inboundevent.game.GameStartEvent;
import decaf.com.inboundevent.inform.DisconnectedEvent;
import decaf.com.inboundevent.inform.PartnershipCreatedEvent;
import decaf.com.inboundevent.inform.PartnershipEndedEvent;
import decaf.com.inboundevent.inform.UserNameChangedEvent;
import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;

// Referenced classes of package caffeine.util:
// Subscriber, EventService

public class User implements Subscriber {
	private static final PropertiesManager resourceRepository = PropertiesManager
			.getInstance();

	private static final Logger LOGGER = Logger.getLogger(User.class);

	public void inform(UserNameChangedEvent event) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Receibved UserNameChangedEVent userName is now: "
					+ event.getUserName());
		}
		handle = event.getUserName();
	}

	public void inform(DisconnectedEvent disconnectedevent) {
		isConnected = false;
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

	private User() {
		eventService = EventService.getInstance();
		// eventService.subscribe(new Subscription(
		// caffeine.com.inboundevent.inform.ConnectedEvent.class, null,
		// this));
		eventService.subscribe(new Subscription(
				decaf.com.inboundevent.inform.DisconnectedEvent.class, null,
				this));
		eventService.subscribe(new Subscription(
				decaf.com.inboundevent.inform.PartnershipCreatedEvent.class,
				null, this));
		eventService.subscribe(new Subscription(
				decaf.com.inboundevent.inform.PartnershipEndedEvent.class,
				null, this));
		eventService.subscribe(new Subscription(
				decaf.com.inboundevent.inform.UserNameChangedEvent.class, null,
				this));
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

	private static User singletonInstance;

	private boolean isConnected;

	private String handle;

	private String bughousePartner;

	private EventService eventService;

}