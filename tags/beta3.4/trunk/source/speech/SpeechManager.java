/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2007  Carson Day (carsonday@gmail.com)
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
import decaf.gui.util.PropertiesManager;

public class SpeechManager implements Preferenceable {
	private static final Logger LOGGER = Logger.getLogger(SpeechManager.class);

	private static SpeechManager singletonInstance = new SpeechManager();

	private DecafSpeech decafSpeech;

	private Preferences preferences;

	private SpeechManager() {
	}

	public static SpeechManager getInstance() {
		return singletonInstance;
	}

	public void init(Preferences preferences) {
		String impl = PropertiesManager.getInstance().getString("Speech",
				"DecafSpeech");
		this.preferences = preferences;

		if (impl == null) {
			decafSpeech = new NoSpeechEnabled();
			LOGGER
					.warn("Cound not find properties/Speech.properties:DecafSpeech, loading NoSpeechEnabled");
		} else {
			try {
				decafSpeech = (DecafSpeech) Class.forName(impl).newInstance();
				decafSpeech.setPreferences(preferences.getSpeechPreferences());
				decafSpeech.init();
			} catch (Throwable t) {
				LOGGER.error("Error instantiating " + impl, t);
				decafSpeech = new NoSpeechEnabled();
			}
		}

	}

	public DecafSpeech getSpeech() {
		return decafSpeech;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		if (decafSpeech != null) {
			decafSpeech.setPreferences(preferences.getSpeechPreferences());
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

}