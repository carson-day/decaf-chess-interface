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

public class WhisperEvent extends IcsNonGameEvent {

	private String message;

	private String whisperer;

	private int gameNumber;

	public WhisperEvent(int icsId, String text, String message,
			String whisperer, int gameId) {
		super(icsId, text.trim());
		this.message = message;
		this.whisperer = whisperer;
		this.gameNumber = gameId;
	}

	public int getGameId() {
		return gameNumber;
	}

	public String getMessage() {
		return message;
	}

	public String getWhisperer() {
		return whisperer;
	}

	@Override
	public String toString() {
		return "<WhisperEvent>" + super.toString() + "<whisperer>" + whisperer
				+ "</whisperer>" + "<message>" + message + "</message>"
				+ "<gameId>" + gameNumber + "</gameId>" + "</WhisperEvent>";
	}
}