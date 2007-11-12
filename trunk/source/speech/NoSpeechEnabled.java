package decaf.speech;

import decaf.gui.pref.SpeechPreferences;

public class NoSpeechEnabled implements DecafSpeech {

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Speech is not enabled. To enable speech download the speech pack";
	}

	public void init() {
		// TODO Auto-generated method stub

	}

	public void setPreferences(SpeechPreferences preferences) {
		// TODO Auto-generated method stub

	}

	public void speak(String text) {
		// TODO Auto-generated method stub

	}

	public boolean supportsWordsPerMinute() {
		// TODO Auto-generated method stub
		return false;
	}

}
