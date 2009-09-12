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

public class SpeechPreferences implements Cloneable, Serializable {

	private static final long serialVersionUID = 11;

	public static SpeechPreferences getDefault() {
		SpeechPreferences result = new SpeechPreferences();
		return result;
	}

	private boolean isSpeechEnabled = true;

	private boolean isSpeakingNotifications = true;

	private boolean isSpeakingTells = true;

	private boolean isSpeakingPtells = true;

	private boolean isSpeakingName = true;

	private boolean isSpeaking10SecondCountdown = true;

	private boolean isAnnouncingCheck = true;

	private int spokenWordsPerMinuite = 175;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public int getSpokenWordsPerMinuite() {
		return spokenWordsPerMinuite;
	}

	public boolean isAnnouncingCheck() {
		return isAnnouncingCheck;
	}

	public boolean isSpeaking10SecondCountdown() {
		return isSpeaking10SecondCountdown;
	}

	public boolean isSpeakingName() {
		return isSpeakingName;
	}

	public boolean isSpeakingNotifications() {
		return isSpeakingNotifications;
	}

	public boolean isSpeakingPtells() {
		return isSpeakingPtells;
	}

	public boolean isSpeakingTells() {
		return isSpeakingTells;
	}

	public boolean isSpeechEnabled() {
		return isSpeechEnabled;
	}

	public void setAnnouncingCheck(boolean isAnnouncingCheck) {
		this.isAnnouncingCheck = isAnnouncingCheck;
	}

	public void setSpeaking10SecondCountdown(boolean isSpeaking10SecondCountdown) {
		this.isSpeaking10SecondCountdown = isSpeaking10SecondCountdown;
	}

	public void setSpeakingName(boolean isSpeakingName) {
		this.isSpeakingName = isSpeakingName;
	}

	public void setSpeakingNotifications(boolean isSpeakingNotifications) {
		this.isSpeakingNotifications = isSpeakingNotifications;
	}

	public void setSpeakingPtells(boolean isSpeakingPtells) {
		this.isSpeakingPtells = isSpeakingPtells;
	}

	public void setSpeakingTells(boolean isSpeakingTells) {
		this.isSpeakingTells = isSpeakingTells;
	}

	public void setSpeechEnabled(boolean isSpeechEnabled) {
		this.isSpeechEnabled = isSpeechEnabled;
	}

	public void setSpokenWordsPerMinuite(int spokenWordsPerMinuite) {
		this.spokenWordsPerMinuite = spokenWordsPerMinuite;
	}

}
