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

public class DisconnectedEvent extends InboundEvent {

	public DisconnectedEvent(Object source, String messageId, String text,
			String user, String url, int port) {
		super(source, messageId, text);
		this.user = user;
		this.url = url;
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public String getURL() {
		return url;
	}

	public int getPort() {
		return port;
	}

	public String toString() {
		return "<DisconnectedEvent>" + super.toString() + "<user>" + user
				+ "</user>" + "<url>" + url + "</url>" + "<port>" + port
				+ "</port>" + "</DisconnectedEvent>";
	}

	String user;

	String url;

	int port;
}