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
package decaf.gui.pref;

import java.io.Serializable;

public class LoginPreferences implements Cloneable, Serializable {

	private static final long serialVersionUID = 11;

	public static LoginPreferences getDefault() {
		LoginPreferences result = new LoginPreferences();
		return result;
	}

	private String defaultUserName;

	private String defaultPassword;

	private String server = "freechess.org";

	private int serverPort = 5000;

	private boolean isDefaultTimesealEnabled = true;

	private boolean isDefaultGuestEnabled = false;

	private boolean autoLogin = false;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getDefaultPassword() {
		return defaultPassword;
	}

	public String getDefaultUserName() {
		return defaultUserName;
	}

	public String getServer() {
		return server;
	}

	public int getServerPort() {
		return serverPort;
	}

	public boolean isAutoLogin() {
		return autoLogin;
	}

	public boolean isDefaultGuestEnabled() {
		return isDefaultGuestEnabled;
	}

	public boolean isDefaultTimesealEnabled() {
		return isDefaultTimesealEnabled;
	}

	public void setAutoLogin(boolean autoLogin) {
		this.autoLogin = autoLogin;
	}

	public void setDefaultGuestEnabled(boolean isDefaultGuestEnabled) {
		this.isDefaultGuestEnabled = isDefaultGuestEnabled;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public void setDefaultTimesealEnabled(boolean isDefaultTimesealEnabled) {
		this.isDefaultTimesealEnabled = isDefaultTimesealEnabled;
	}

	public void setDefaultUserName(String defaultUserName) {
		this.defaultUserName = defaultUserName;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

}
