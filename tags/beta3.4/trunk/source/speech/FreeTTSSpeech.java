package decaf.speech;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.speech.EngineCreate;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.speech.synthesis.Voice;

import org.apache.log4j.Logger;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

import decaf.gui.pref.SpeechPreferences;

public class FreeTTSSpeech implements DecafSpeech {
	private static final Logger LOGGER = Logger.getLogger(FreeTTSSpeech.class);

	private static final String VOICE_NAME = "kevin16";

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 60 * 5,
			TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	/**
	 * Returns a "no synthesizer" message, and asks the user to check if the
	 * "speech.properties" file is at <code>user.home</code> or
	 * <code>java.home/lib</code>.
	 * 
	 * @return a no synthesizer message
	 */
	private static String noSynthesizerMessage() {
		String message = "No synthesizer created.  This may be the result of any\n"
				+ "number of problems.  It's typically due to a missing\n"
				+ "\"speech.properties\" file that should be at either of\n"
				+ "these locations: \n\n";
		message += "user.home    : " + System.getProperty("user.home") + "\n";
		message += "java.home/lib: "
				+ System.getProperty("java.home")
				+ File.separator
				+ "lib\n\n"
				+ "Another cause of this problem might be corrupt or missing\n"
				+ "voice jar files in the freetts lib directory.  This problem\n"
				+ "also sometimes arises when the freetts.jar file is corrupt\n"
				+ "or missing.  Sorry about that.  Please check for these\n"
				+ "various conditions and then try again.\n";

		return message;
	}

	/**
	 * Example of how to list all the known voices for a specific mode using
	 * just JSAPI. FreeTTS maps the domain name to the JSAPI mode name. The
	 * currently supported domains are "general," which means general purpose
	 * synthesis for tasks such as reading e-mail, and "time" which means a
	 * domain that's only good for speaking the time of day.
	 */
	/*
	 * private static void listAllVoices() { String modeName = "general";
	 *  /* Create a template that tells JSAPI what kind of speech synthesizer we
	 * are interested in. In this case, we're just looking for a general domain
	 * synthesizer for US English. / SynthesizerModeDesc required = new
	 * SynthesizerModeDesc(null, // engine // name modeName, // mode name
	 * Locale.US, // locale null, // running null); // voices
	 *  /* Contact the primary entry point for JSAPI, which is the Central
	 * class, to discover what synthesizers are available that match the
	 * template we defined above. / EngineList engineList =
	 * Central.availableSynthesizers(required); for (int i = 0; i <
	 * engineList.size(); i++) {
	 * 
	 * SynthesizerModeDesc desc = (SynthesizerModeDesc) engineList.get(i);
	 * System.out.println(" " + desc.getEngineName() + " (mode=" +
	 * desc.getModeName() + ", locale=" + desc.getLocale() + "):"); Voice[]
	 * voices = desc.getVoices(); for (int j = 0; j < voices.length; j++) {
	 * System.out.println(" " + voices[j].getName()); } } }
	 */

	public FreeTTSSpeech() {
	}

	public void init() {
		if (preferences != null && preferences.isSpeechEnabled()) {
			try {

				// istAllVoices();
				SynthesizerModeDesc desc = new SynthesizerModeDesc(null,
						"general", /*
									 * use "time" or "general"
									 */
						Locale.US, Boolean.FALSE, null);

				FreeTTSEngineCentral central = new FreeTTSEngineCentral();
				EngineList list = central.createEngineList(desc);

				if (list != null && list.size() > 0) {
					EngineCreate creator = (EngineCreate) list.get(0);
					synthesizer = (Synthesizer) creator.createEngine();

				} else {
					LOGGER.warn(noSynthesizerMessage());
				}
				if (synthesizer == null) {
					LOGGER.warn(noSynthesizerMessage());
				}
				synthesizer.allocate();
				synthesizer.resume();

				/*
				 * Choose the voice.
				 */
				desc = (SynthesizerModeDesc) synthesizer.getEngineModeDesc();
				Voice[] voices = desc.getVoices();
				Voice voice = null;
				for (int i = 0; i < voices.length; i++) {
					if (voices[i].getName().equals(VOICE_NAME)) {
						voice = voices[i];
						break;
					}
				}
				if (voice == null) {
					LOGGER.error("Synthesizer does not have a voice named "
							+ VOICE_NAME + ".");
					throw new RuntimeException(
							"Synthesizer does not have a voice named "
									+ VOICE_NAME + ".");
				}

				synthesizer.getSynthesizerProperties().setSpeakingRate(
						preferences.getSpokenWordsPerMinuite());

				synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void finalize() throws Exception {
		dispose();
	}

	public void dispose() throws Exception {
		if (synthesizer != null) {
			synthesizer.deallocate();
		}
	}

	public void speak(final String text) {
		if (synthesizer != null) {
			executor.execute(new Runnable() {
				public void run() {
					if (synthesizer != null) {
						synthesizer.speakPlainText(text, null);
						try {
							synthesizer
									.waitEngineState(Synthesizer.QUEUE_EMPTY);
						} catch (InterruptedException ie) {
						}
					}
				}
			});
		}
	}

	private Synthesizer synthesizer;

	private SpeechPreferences preferences;

	public void setPreferences(SpeechPreferences preferences) {
		this.preferences = preferences;
		if (synthesizer == null && preferences.isSpeechEnabled()) {
			Thread myThread = new Thread(new Runnable() {
				public void run() {
					init();
				}
			});
			myThread.start();
		} else if (synthesizer != null && !preferences.isSpeechEnabled()) {
			Thread myThread = new Thread(new Runnable() {
				public void run() {
					try {
						dispose();
						synthesizer = null;
					} catch (Exception e) {
					}
				}
			});
			myThread.start();
		} else if (synthesizer != null) {
			try {
				synthesizer.getSynthesizerProperties().setSpeakingRate(
						preferences.getSpokenWordsPerMinuite());
			} catch (Exception e) {
				// eat it.
				LOGGER.warn(e);
			}
		}
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "Free TTS Speech 1.2 (http://freetts.sourceforge.net/docs/index.ph)";
	}

	public boolean supportsWordsPerMinute() {
		return true;
	}

}
