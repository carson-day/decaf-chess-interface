package decaf.speech;

import com.apple.cocoa.application.NSSpeechSynthesizer;

import decaf.gui.pref.SpeechPreferences;

public class OSXSpeech implements DecafSpeech {
	private NSSpeechSynthesizer synthesizer;

	private SpeechPreferences speechPreferences;

	public String getDescription() {
		return "OSX native speech. The default voice and its settings in SystemPreferences/Speech are being used.";
	}

	public void init() {
		synthesizer = new NSSpeechSynthesizer();

	}

	public void setPreferences(SpeechPreferences preferences) {
		this.speechPreferences = preferences;
	}

	public void speak(String text) {
		if (synthesizer != null && speechPreferences.isSpeechEnabled()) {
			synthesizer.startSpeakingString(text);
		}

	}

	public boolean supportsWordsPerMinute() {
		// TODO Auto-generated method stub
		return false;
	}

}
