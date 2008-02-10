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
	
	private boolean isSpeechEnabled = true;

	private boolean isSpeakingNotifications = true;

	private boolean isSpeakingTells = true;

	private boolean isSpeakingPtells = true;

	private boolean isSpeakingName = true;

	private boolean isSpeaking10SecondCountdown = true;
	
	private boolean isAnnouncingCheck = true;

	private int spokenWordsPerMinuite = 175;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public static SpeechPreferences getDefault() {
		SpeechPreferences result = new SpeechPreferences();
		return result;
	}

	public boolean isSpeaking10SecondCountdown() {
		return isSpeaking10SecondCountdown;
	}

	public void setSpeaking10SecondCountdown(boolean isSpeaking10SecondCountdown) {
		this.isSpeaking10SecondCountdown = isSpeaking10SecondCountdown;
	}

	public boolean isSpeakingName() {
		return isSpeakingName;
	}

	public void setSpeakingName(boolean isSpeakingName) {
		this.isSpeakingName = isSpeakingName;
	}

	public boolean isSpeakingNotifications() {
		return isSpeakingNotifications;
	}

	public void setSpeakingNotifications(boolean isSpeakingNotifications) {
		this.isSpeakingNotifications = isSpeakingNotifications;
	}

	public boolean isSpeakingPtells() {
		return isSpeakingPtells;
	}

	public void setSpeakingPtells(boolean isSpeakingPtells) {
		this.isSpeakingPtells = isSpeakingPtells;
	}

	public boolean isSpeakingTells() {
		return isSpeakingTells;
	}

	public void setSpeakingTells(boolean isSpeakingTells) {
		this.isSpeakingTells = isSpeakingTells;
	}

	public boolean isSpeechEnabled() {
		return isSpeechEnabled;
	}

	public void setSpeechEnabled(boolean isSpeechEnabled) {
		this.isSpeechEnabled = isSpeechEnabled;
	}

	public int getSpokenWordsPerMinuite() {
		return spokenWordsPerMinuite;
	}

	public void setSpokenWordsPerMinuite(int spokenWordsPerMinuite) {
		this.spokenWordsPerMinuite = spokenWordsPerMinuite;
	}

	public boolean isAnnouncingCheck() {
		return isAnnouncingCheck;
	}

	public void setAnnouncingCheck(boolean isAnnouncingCheck) {
		this.isAnnouncingCheck = isAnnouncingCheck;
	}
	
	

}
