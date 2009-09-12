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

public class KibitzEvent extends IcsNonGameEvent {

	private String message;

	private String kibitzer;

	private int gameNumber;

	public KibitzEvent(int icsId, String text, String message, String kibitzer,
			int gameId) {
		super(icsId, text.trim());
		this.message = message;
		this.kibitzer = kibitzer;
		this.gameNumber = gameId;
	}

	public int getGameId() {
		return gameNumber;
	}

	public String getKibitzer() {
		return kibitzer;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "<KibitzEvent>" + super.toString() + "<kibitzer>" + kibitzer
				+ "</kibitzer>" + "<gameNumber>" + gameNumber + "</gameNumber>"
				+ "<message>" + message + "</message>" + "</KibitzEvent>";
	}
}