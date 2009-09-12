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
package decaf.messaging.ics;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class NoTimesealStrategy implements TimesealStrategy {
	private Socket socket;

	private InputStream inputStream;

	private OutputStream outputStream;

	public void connect(String server, int port) throws IOException {
		socket = new Socket(server, port);
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		outputStream.flush();
	}

	public void disconnect() {
		try {
			socket.close();
		} catch (IOException ioe) {
		}
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void sendAck() throws IOException {
	}

	public void sendMsg(String message) throws IOException {
		outputStream.write(message.getBytes(), 0, message.length());
		outputStream.flush();
	}

}
