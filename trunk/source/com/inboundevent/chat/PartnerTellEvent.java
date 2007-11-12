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
package decaf.com.inboundevent.chat;

import decaf.com.inboundevent.InboundEvent;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class PartnerTellEvent extends InboundEvent {

	public PartnerTellEvent(Object source, String messageId, String text,
			String partnersName, String message) {
		super(source, messageId, text.trim());
		this.partnersName = partnersName;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return "<PartnerTellRequestEvent>" + super.toString()
				+ "<partnersName>" + partnersName + "<partnersName>"
				+ "<message>" + message + "<message>"
				+ "</PartnerTellRequestEvent>";
	}

	private String partnersName;

	private String message;
}