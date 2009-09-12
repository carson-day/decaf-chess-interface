package decaf.speech;

import decaf.gui.pref.SpeechPreferences;

public interface DecafSpeech {
	public String getDescription();

	public void init();

	public void setPreferences(SpeechPreferences preferences);

	public void speak(String text);

	public boolean supportsWordsPerMinute();
}