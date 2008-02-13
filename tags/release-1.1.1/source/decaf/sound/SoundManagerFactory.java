/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Carson Day (carsonday@gmail.com)
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
package decaf.sound;

import org.apache.log4j.Logger;

import decaf.resources.ResourceManagerFactory;

public class SoundManagerFactory {

	private static final Logger LOGGER = Logger
			.getLogger(SoundManagerFactory.class);

	private static SoundManager instance = null;

	private SoundManagerFactory() {
	}

	public static SoundManager getInstance() {
		if (instance == null) {
			String os = ResourceManagerFactory.getManager().getString("os",
					"os");
			String soundExternalProcess = ResourceManagerFactory.getManager()
					.getString("os", "sound.externaprocess");

			if (soundExternalProcess != null) {
				// In linux to mix sounds properly for some people you have to
				// go
				// through a process.
				instance = new ProcessSoundManager();
			} else if (os != null && os.equalsIgnoreCase("osx")
					|| os.equalsIgnoreCase("generic")) {
				// works best in osx, the javaxsound cuts out after a while and
				// the sounds dont mix well.
				instance = new AppletSoundManager();
			} else {
				instance = new JavaxSoundManager();
			}

			instance.loadSounds();
		}
		return instance;
	}
}