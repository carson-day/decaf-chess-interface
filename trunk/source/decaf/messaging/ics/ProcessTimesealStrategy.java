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
import java.net.InetAddress;

public class ProcessTimesealStrategy implements TimesealStrategy {
	private InputStream inputStream;

	private OutputStream outputStream;

	private String processName;

	private Process process;

	public ProcessTimesealStrategy(String processName) {
		this.processName = processName;
	}

	public void connect(String server, int port) throws IOException {
		if (!Character.isDigit(server.charAt(0))) {
			server = InetAddress.getByName(server).getHostAddress();
		}

		Runtime runtime = Runtime.getRuntime();
		process = runtime.exec(new String[] { processName, server, "" + port });

		outputStream = process.getOutputStream();
		inputStream = process.getInputStream();

		outputStream.flush();

	}

	public void disconnect() {
		try {
			outputStream.close();
		} catch (IOException ioe) {
		}

		try {
			inputStream.close();
		} catch (IOException ioe) {
		}
		process.destroy();
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
