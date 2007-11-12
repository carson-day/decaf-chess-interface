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

package decaf.gui.event;

import decaf.gui.Disposable;
import decaf.gui.SoundManager;
import decaf.speech.SpeechManager;

public class ClockCountdownListener implements ChessClockListener, Disposable {
	private static SoundManager soundManager = SoundManager.getInstance();

	public void dispose() {
	}

	public void clockIsAtSecond(final int second) {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				if (second <= 10 && second > 0)
					SpeechManager.getInstance().getSpeech().speak("" + second);
			}
		});
		thread.start();
	}
}