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
package decaf.gui;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SoundManager {
	private SoundManager() {
		sounds = new Hashtable();
		loadSounds();
	}

	public static SoundManager getInstance() {
		return singletonInstance;
	}

	public void playSound(final String s) {
		executor.execute(new Runnable() {
			public void run() {
				AudioClip audioclip = (AudioClip) sounds.get(s);
				audioclip.play();
			}
		});
	}

	public boolean isManagingKey(String s) {
		return sounds.get(s) != null;
	}

	private void addSound(String s, String s1) {
		try {
			sounds.put(s, Applet.newAudioClip(new URL("file:" + USER_DIR + "/"
					+ s1)));
		} catch (MalformedURLException malformedurlexception) {
			throw new RuntimeException("Error loading file " + s1);
		}
	}

	private void loadSounds() {
		for (int i = 0; i < SoundKeys.SOUNDS_TO_LOAD.length; i++) {
			addSound(SoundKeys.SOUNDS_TO_LOAD[i][1],
					SoundKeys.SOUNDS_TO_LOAD[i][0]);
		}
	}

	private static class WavFileNameFilter implements FilenameFilter {
		public boolean accept(File dir, String name) {
			return name.endsWith(".wav");
		}

	}

	private Hashtable sounds;

	private static final WavFileNameFilter wavFileNameFilter = new WavFileNameFilter();

	private static final String USER_DIR = System.getProperty("user.dir");

	private static SoundManager singletonInstance = new SoundManager();

	private ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 5, 60 * 5,
			TimeUnit.SECONDS, new LinkedBlockingQueue());

}