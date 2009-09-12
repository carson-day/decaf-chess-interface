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

import org.apache.log4j.Logger;

import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.resources.ResourceManagerFactory;

public class SpeechManager implements Preferenceable {
	private static final Logger LOGGER = Logger.getLogger(SpeechManager.class);

	private static SpeechManager singletonInstance = new SpeechManager();

	public static SpeechManager getInstance() {
		return singletonInstance;
	}

	private DecafSpeech decafSpeech;

	private Preferences preferences;

	private SpeechManager() {
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public DecafSpeech getSpeech() {
		return decafSpeech;
	}

	public void init(Preferences preferences) {
		decafSpeech = new NoSpeechEnabled();
		String os = ResourceManagerFactory.getManager().getString("os", "os");
		this.preferences = preferences;

		if (os == null) {
			decafSpeech = new NoSpeechEnabled();
			LOGGER
					.error("Cound not find properties/os.properties:os, loading NoSpeechEnabled");
		} else {
			String impl = null;

			if (os.equals("osx")) {
				impl = "decaf.speech.OSXSpeech";
			} else {
				impl = "decaf.speech.FreeTTSSpeech";
			}
			try {
				decafSpeech = (DecafSpeech) Class.forName(impl).newInstance();
				decafSpeech.setPreferences(preferences.getSpeechPreferences());
				decafSpeech.init();
			} catch (Throwable t) {
				LOGGER.warn("Error Loading Speech Impl (Loading default) "
						+ impl, t);
				decafSpeech = new NoSpeechEnabled();
			}
		}

		LOGGER.info("Decaf Speech=" + decafSpeech);
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		if (decafSpeech != null) {
			decafSpeech.setPreferences(preferences.getSpeechPreferences());
		}
	}

}