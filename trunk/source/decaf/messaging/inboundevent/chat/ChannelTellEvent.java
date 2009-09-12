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

/**
 * An channel tell event.
 */
public class ChannelTellEvent extends IcsNonGameEvent {

	private int channel;

	private String user;

	private String message;

	public ChannelTellEvent(int icsId, String text, int channel, String user,
			String message) {
		super(icsId, text.trim());
		this.channel = channel;
		this.user = user;
		this.message = message;
	}

	public int getChannel() {
		return channel;
	}

	public String getMessage() {
		return message;
	}

	public String getUser() {
		return user;
	}

	@Override
	public String toString() {
		return "<ChannelTellEvent>" + super.toString() + "<user>" + user
				+ "</user>" + "<channel>" + channel + "</channel>"
				+ "<message>" + message + "</message>" + "</ChannelTellEvent>";
	}
}