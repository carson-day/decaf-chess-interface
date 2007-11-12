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

package decaf.moveengine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import decaf.gui.util.ThreadUtil;

/**
 * A chess clock.
 */
public class Clock {
	private static final Logger LOGGER = Logger.getLogger(Clock.class);

	private long whiteRemainingTime;

	private long blackRemainingTime;

	private int increment;

	private long ticInterval;

	private long lastTicInterval;

	private boolean isTicking = false;

	private boolean isWhiteTicking = false;

	private List listeners = new LinkedList();

	private Thread thread = null;

	private ClockSlave slave = null;

	public Clock(int initialMinutes, int initialSeconds) {
		whiteRemainingTime = initialMinutes * 60000 + initialSeconds * 1000;
		blackRemainingTime = whiteRemainingTime;
		lastTicInterval = ticInterval;
	}

	public static void main(String args[]) throws Exception {
		Clock clock = new Clock(60, 30);
		clock.hitPlunger(false);
	}

	/**
	 * return [0] = hours [1] = minutes [2] = seconds [3] = millis.
	 */
	public int[] getTime(boolean isWhiteClock) {
		long time = isWhiteClock ? whiteRemainingTime : blackRemainingTime;

		int hours = (int) (time / 3600000);
		int minutes = (int) ((time - (3600000 * hours)) / 60000);
		int seconds = (int) ((time - (3600000 * hours) - (60000 * minutes)) / 1000);
		int millis = (int) (time % 1000);
		return new int[] { hours, minutes, seconds, millis };
	}

	public long getRemainingTime(boolean isWhiteClock) {
		return isWhiteClock ? whiteRemainingTime : blackRemainingTime;
	}

	public void hitPlunger(boolean isWhitePlunger) {
		if (slave == null) {
			slave = new ClockSlave();
			thread = new Thread(slave);
			isWhiteTicking = !isWhitePlunger;
			thread.start();
		} else {
			isWhiteTicking = !isWhitePlunger;
		}
	}

	public void stop() {

	}

	public void reset() {

	}

	public void add(int hours, int minutes, int seconds, boolean isWhiteClock) {
		long millisToAdd = hours * 3600000 + minutes * 60000 + seconds * 1000;
		if (isWhiteClock) {
			whiteRemainingTime += millisToAdd;
		} else {
			blackRemainingTime += millisToAdd;
		}
	}

	public boolean isTicking() {
		return isTicking;
	}

	public boolean isWhiteClockTicking() {
		return isWhiteTicking;
	}

	public void addClockistener(ClockListener listener) {
		listeners.add(listener);
	}

	public void setTicInterval(int milliseconds) {
		this.ticInterval = milliseconds;
	}

	private void notifyTick() {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			ThreadUtil.invokeInThread(i.next(), "tick", null);
		}
	}

	private void tick() {
		if (isWhiteTicking) {
			whiteRemainingTime -= 100;
		} else {
			blackRemainingTime -= 100;
		}
		notifyTick();
	}

	public String toString() {
		int[] whiteTime = getTime(true);
		int[] blackTime = getTime(false);
		return "white " + whiteTime[0] + ":" + whiteTime[1] + ":"
				+ whiteTime[2] + "." + whiteTime[3] + " black  " + blackTime[0]
				+ ":" + blackTime[1] + ":" + blackTime[2] + "." + blackTime[3];
	}

	private class ClockSlave implements Runnable {
		public void run() {
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ie) {
					LOGGER.error("Unexpected interrupted error in clock slave",
							ie);
				}
				tick();
			}
		}
	}
}