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
package decaf.messaging.inboundevent.chat;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class PartnerTellEvent extends IcsNonGameEvent {

	private String partnersName;

	private String message;

	public PartnerTellEvent(int icsId, String text, String partnersName,
			String message) {
		super(icsId, text.trim());
		this.partnersName = partnersName;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "<PartnerTellRequestEvent>" + super.toString()
				+ "<partnersName>" + partnersName + "<partnersName>"
				+ "<message>" + message + "<message>"
				+ "</PartnerTellRequestEvent>";
	}
}