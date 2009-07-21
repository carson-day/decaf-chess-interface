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
package decaf.speech;

import com.apple.cocoa.application.NSSpeechSynthesizer;
import com.apple.cocoa.foundation.NSSystem;

import decaf.gui.pref.SpeechPreferences;

public class OSXSpeech implements DecafSpeech {
	private NSSpeechSynthesizer synthesizer;

	private SpeechPreferences speechPreferences;

	public String getDescription() {
		return "OSX native speech.";
	}

	public void init() {
		synthesizer = new NSSpeechSynthesizer();

	}

	public void setPreferences(SpeechPreferences preferences) {
		this.speechPreferences = preferences;
	}

	public void speak(String text) {
		if (synthesizer != null && speechPreferences.isSpeechEnabled()
				&& !NSSpeechSynthesizer.isAnyApplicationSpeaking()) {
			synthesizer.startSpeakingString(text);
		}
	}

	public boolean supportsWordsPerMinute() {
		// TODO Auto-generated method stub
		return false;
	}

}
