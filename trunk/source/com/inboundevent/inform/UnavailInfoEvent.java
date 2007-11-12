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

package decaf.com.inboundevent.inform;

import decaf.com.inboundevent.InboundEvent;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class UnavailInfoEvent extends InboundEvent {

	public UnavailInfoEvent(Object source, String messageId, String text,
			String user) {
		super(source, messageId, text);
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public String toString() {
		return "<UnavailInfoEvent>" + super.toString() + "</UnavailInfoEvent>";
	}

	private String user;
}