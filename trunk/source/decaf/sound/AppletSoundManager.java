package decaf.sound;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import decaf.gui.GUIManager;
import decaf.resources.ResourceManagerFactory;
import decaf.thread.ThreadManager;

public class AppletSoundManager implements SoundManager {

	private static final Logger LOGGER = Logger
			.getLogger(SoundManagerFactory.class);

	private Hashtable<String, ClipInfo> sounds = new Hashtable<String, ClipInfo>();

	public void playSound(final String key) {
		if (GUIManager.getInstance().getPreferences().isSoundOn()) {
			ThreadManager.execute(new Runnable() {
				public void run() {
					ClipInfo clipInfo = sounds.get(key);
					try {
						if (!clipInfo.isRunning) {
							clipInfo.isRunning = true;
							clipInfo.clip.play();
							clipInfo.isRunning = false;
						}
					} catch (Exception e) {
						LOGGER.error("Error playing sound: key=" + key, e);
						loadClip(clipInfo.key, clipInfo.file);
					}
				}
			});
		}
	}

	private void loadClip(String key, String file) {
		try {
			ClipInfo clipInfo = new ClipInfo();
			clipInfo.clip = Applet.newAudioClip(ResourceManagerFactory
					.getManager().getUrl(file));

			clipInfo.key = key;
			clipInfo.file = file;
			sounds.put(key, clipInfo);

		} catch (Exception e) {
			LOGGER.error("Error clip of sound " + file, e);
		}
	}

	public void loadSounds() {
		for (int i = 0; i < SoundKeys.SOUNDS_TO_LOAD.length; i++) {
			loadClip(SoundKeys.SOUNDS_TO_LOAD[i][1],
					SoundKeys.SOUNDS_TO_LOAD[i][0]);
		}
	}

	public class ClipInfo {
		public AudioClip clip;

		public int numPlays;

		public String file;

		public String key;

		public boolean isRunning;
	}
}
