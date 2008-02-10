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
package decaf.messaging.inboundevent;

import decaf.event.Event;

public class IcsInboundEvent implements Event {
	public IcsInboundEvent(int icsId) {
	}

	private int icsId;
	private boolean hideFromUser;

	public int getIcsId() {
		return icsId;
	}

	public void setIcsId(int icsId) {
		this.icsId = icsId;
	}

	public boolean isHideFromUser() {
		return hideFromUser;
	}

	public void setHideFromUser(boolean hideFromUser) {
		this.hideFromUser = hideFromUser;
	}
	
	
}