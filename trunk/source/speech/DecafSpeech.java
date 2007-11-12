package decaf.speech;

import decaf.gui.pref.SpeechPreferences;

public interface DecafSpeech {
	public void speak(String text);

	public void init();

	public void setPreferences(SpeechPreferences preferences);

	public String getDescription();

	public boolean supportsWordsPerMinute();
}