package decaf.sound;

import java.util.Hashtable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

import decaf.gui.GUIManager;
import decaf.resources.ResourceManagerFactory;
import decaf.thread.ThreadManager;

public class JavaxSoundManager implements SoundManager {

	public class ClipInfo {
		public int numPlays;

		public String file;

		public String key;

		public boolean isRunning;
	}

	private static final Logger LOGGER = Logger
			.getLogger(SoundManagerFactory.class);

	private static final int BUFFER_SIZE = 128000;

	private Hashtable<String, ClipInfo> sounds = new Hashtable<String, ClipInfo>();

	private void loadClip(String key, String file) {
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

	public void playSound(final String key) {
		if (GUIManager.getInstance().getPreferences().isSoundOn()) {
			ThreadManager.execute(new Runnable() {
				public void run() {
					LOGGER.debug("Playing sound " + key);
					ClipInfo clipInfo = sounds.get(key);
					SourceDataLine dataLine = null;
					AudioInputStream stream = null;
					if (!clipInfo.isRunning) {
						try {
							stream = AudioSystem
									.getAudioInputStream(ResourceManagerFactory
											.getManager().getUrl(clipInfo.file));

							// At present, ALAW and ULAW encodings must be
							// converted
							// to PCM_SIGNED before it can be played
							AudioFormat format = stream.getFormat();
							if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
								format = new AudioFormat(
										AudioFormat.Encoding.PCM_SIGNED, format
												.getSampleRate(), format
												.getSampleSizeInBits(), format
												.getChannels(), format
												.getFrameSize(), format
												.getFrameRate(), true); // big
								// endian
								stream = AudioSystem.getAudioInputStream(
										format, stream);
							}

							// Create the dataLine
							DataLine.Info info = new DataLine.Info(
									SourceDataLine.class, stream.getFormat(),
									((int) stream.getFrameLength() * format
											.getFrameSize()));
							dataLine = (SourceDataLine) AudioSystem
									.getLine(info);

							// This method does not return until the audio file
							// is
							// completely loaded
							dataLine.open(stream.getFormat());

							// Start playing
							dataLine.start();

							byte[] buffer = new byte[BUFFER_SIZE];
							int r = stream.read(buffer, 0, BUFFER_SIZE);
							while (r != -1) {
								if (r > 0) {
									dataLine.write(buffer, 0, r);
								}
								r = stream.read(buffer, 0, BUFFER_SIZE);
							}
							dataLine.drain();

						} catch (Exception e) {
							LOGGER.error("Error playing sound: key=" + key, e);
						} finally {
							clipInfo.isRunning = false;

							dataLine.close();
							try {
								stream.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					}

				}
			});
		}
	}
}
