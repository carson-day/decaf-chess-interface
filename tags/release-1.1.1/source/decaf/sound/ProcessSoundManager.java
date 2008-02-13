package decaf.sound;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import decaf.gui.GUIManager;
import decaf.resources.ResourceManagerFactory;
import decaf.thread.ThreadManager;

public class ProcessSoundManager implements SoundManager {

	private static final Logger LOGGER = Logger
			.getLogger(SoundManagerFactory.class);

	private Hashtable<String, ClipInfo> sounds = new Hashtable<String, ClipInfo>();

	private String soundExternalProcess;

	public void playSound(final String key) {
		if (GUIManager.getInstance().getPreferences().isSoundOn()) {
			ThreadManager.execute(new Runnable() {
				public void run() {
					LOGGER.debug("Playing sound " + key);
					ClipInfo clipInfo = sounds.get(key);
					if (!clipInfo.isRunning) {
						clipInfo.isRunning = true;
						try {
							Runtime.getRuntime().exec(
									new String[] { soundExternalProcess,
											clipInfo.file });
						} catch (Exception e) {
							LOGGER.error(e);
						}
						clipInfo.numPlays++;
						clipInfo.isRunning = false;
					}
				}
			});
		}
	}

	private void loadClip(String key, String file) {
		soundExternalProcess = ResourceManagerFactory.getManager().getString(
				"os", "sound.externaprocess");
		ClipInfo clipInfo = new ClipInfo();
		clipInfo.key = key;
		clipInfo.file = file;
		sounds.put(key, clipInfo);
	}

	public void loadSounds() {
		for (int i = 0; i < SoundKeys.SOUNDS_TO_LOAD.length; i++) {
			loadClip(SoundKeys.SOUNDS_TO_LOAD[i][1],
					SoundKeys.SOUNDS_TO_LOAD[i][0]);
		}
	}

	public class ClipInfo {
		// public AudioClip clip;

		public int numPlays;

		public String file;

		public String key;

		public boolean isRunning;
	}
}
