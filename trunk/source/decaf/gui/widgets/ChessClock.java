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

package decaf.gui.widgets;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.speech.SpeechManager;
import decaf.util.TextProperties;

public class ChessClock extends JPanel implements Preferenceable, Disposable {
	private static final Logger LOGGER = Logger.getLogger(ChessClock.class);

	private int showTenthsWhenTimeIsLessThanSeconds;

	private long currentMilliseconds;

	private int initialSeconds;

	private int increment;

	private JLabel timeLabel;

	private Preferences preferences;

	private boolean isRunning = false;

	private boolean isRunningWithoutTicking = false;

	private boolean hasBeenDisposed;

	private String boardId;

	private boolean isWhiteClock;

	private boolean isSpeakingCountdown;

	private Timer timer = new Timer();

	private int updateInterval = 1000;

	private ChessClock thisClock = this;

	private List<ClockStateChangedListener> clockStateChangedListeners = new LinkedList<ClockStateChangedListener>();

	public void dispose() {
		if (timer != null) {
			timer.cancel();
		}
		removeAll();
		removeAllClockStateChangedListeners();

	}

	public ChessClock(int timeInSeconds, int increment) {
		timeLabel = new JLabel("0:00.0");
		this.initialSeconds = timeInSeconds;
		this.increment = increment;
		this.currentMilliseconds = timeInSeconds * 1000;
		add(timeLabel);
		setToCurrentTime();
		isRunning = false;
	}

	public boolean isSpeakingCountdown() {
		return isSpeakingCountdown;
	}

	public void setSpeakingCountdown(boolean isSpeakingCountdown) {
		this.isSpeakingCountdown = isSpeakingCountdown;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public boolean isWhiteClock() {
		return isWhiteClock;
	}

	public void setWhiteClock(boolean isWhiteClock) {
		this.isWhiteClock = isWhiteClock;
	}

	private void speakTime(int second) {
		if (isSpeakingCountdown() && second <= 10 && second > 0) {
			SpeechManager.getInstance().getSpeech().speak("" + second);
		}
	}

	public void setShowTenthsWhenTimeIsLessThanSeconds(int value) {
		this.showTenthsWhenTimeIsLessThanSeconds = value;
	}

	public int getShowTenthsWhenTimeIsLessThanSeconds() {
		return showTenthsWhenTimeIsLessThanSeconds;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
		if (preferences != null) {
			setTextProperties();
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	private void setToCurrentTime() {
		if (!hasBeenDisposed) {
			long i = Math.abs(currentMilliseconds);
			boolean flag = currentMilliseconds < 0;
			long j = i / 1000;
			long k = j < 60 ? j : j % 60;
			long l = j < 3600 ? j / 60 : (j % 3600) / 60;
			long i1 = j / 3600;
			boolean flag1 = j < showTenthsWhenTimeIsLessThanSeconds;
			long j1 = 0;
			if (flag1)
				j1 = (i % 1000) / 100;
			final String s = flag ? "0:00.0" : (i1 != 0 ? "" + i1 + ":" : "")
					+ (l != 0 ? l <= 0 || l >= 10 ? "" + l : "0" + l : "0")
					+ ":"
					+ (k != 0 ? k <= 0 || k >= 10 ? "" + k : "0" + k : "00")
					+ (flag1 ? "." + j1 : "");

			timeLabel.setText(s);
		}
	}

	public void addClockStateChangedListener(ClockStateChangedListener listener) {
		clockStateChangedListeners.add(listener);
	}

	public void removeClockStateChangedListener(
			ClockStateChangedListener listener) {
		clockStateChangedListeners.remove(listener);
	}

	public void removeAllClockStateChangedListeners() {
		clockStateChangedListeners.clear();
	}

	public long getCurrentMilliseconds() {
		return (long) currentMilliseconds;
	}

	public int getInitialSeconds() {
		return initialSeconds;
	}

	public int getIncrement() {
		return increment;
	}

	private void fireClockStateChanged() {
		for (ClockStateChangedListener listener : clockStateChangedListeners) {
			listener.clockStateChanged(this);
		}
	}

	private void setTextProperties() {
		TextProperties properties = isRunning ? preferences
				.getBoardPreferences().getClockActiveTextProperties()
				: preferences.getBoardPreferences()
						.getClockInactiveTextProperties();

		timeLabel.setBackground(properties.getBackground());
		setBackground(properties.getBackground());
		timeLabel.setForeground(properties.getForeground());
		timeLabel.setFont(properties.getFont());
	}

	public void stop() {
		synchronized (this) {
			isRunning = false;
			isRunningWithoutTicking = false;
			currentMilliseconds += increment * 1000;
			setTextProperties();
			if (timer != null) {
				timer.cancel();
			}
			fireClockStateChanged();
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void start() {
		synchronized (this) {
			if (!hasBeenDisposed) {
				if (!isRunning) {
					timer.cancel();
					timer = new Timer();
					isRunning = true;
					isRunningWithoutTicking = false;
					setTextProperties();
					setUpdateInterval();
					timer.schedule(new ChessClockUpdater(),
							getNextClockUpdate());
					fireClockStateChanged();
				}
			}
		}
	}

	public boolean isRunningWithoutTicking() {
		return isRunningWithoutTicking;
	}

	public void startWithoutTicking() {
		if (!hasBeenDisposed) {

			if (!isRunning) {
				isRunning = true;
				setTextProperties();
				isRunning = false;
				isRunningWithoutTicking = true;
				fireClockStateChanged();
			}
		}
	}

	public void set(long time) {
		synchronized (this) {
			if (!hasBeenDisposed) {
				currentMilliseconds = time;
				setToCurrentTime();
			}
		}
	}

	public void set(int initialTime, int intiialInc) {
		synchronized (this) {
			if (!hasBeenDisposed) {
				increment = intiialInc;
				set(initialTime);
			}
		}
	}

	public boolean hasFlagged() {
		boolean flag = false;
		if (currentMilliseconds <= 0)
			flag = true;
		return flag;
	}

	private void handleFiringSpeech(long currentMilliseconds) {
		if (currentMilliseconds <= 10000 && currentMilliseconds >= 9900) {
			speakTime(10);
		} else if (currentMilliseconds <= 9000 && currentMilliseconds >= 8900) {
			speakTime(9);
		} else if (currentMilliseconds <= 8000 && currentMilliseconds >= 7900) {
			speakTime(8);
		} else if (currentMilliseconds <= 7000 && currentMilliseconds >= 6900) {
			speakTime(7);
		} else if (currentMilliseconds <= 6000 && currentMilliseconds >= 5900) {
			speakTime(6);
		} else if (currentMilliseconds <= 5000 && currentMilliseconds >= 4900) {
			speakTime(5);
		} else if (currentMilliseconds <= 4000 && currentMilliseconds >= 3900) {
			speakTime(4);
		} else if (currentMilliseconds <= 3000 && currentMilliseconds >= 2900) {
			speakTime(3);
		} else if (currentMilliseconds <= 2000 && currentMilliseconds >= 1900) {
			speakTime(2);
		} else if (currentMilliseconds <= 1000 && currentMilliseconds >= 900) {
			speakTime(1);
		} else if (currentMilliseconds <= 100 && currentMilliseconds >= 0) {
			speakTime(0);
		} else if (currentMilliseconds < -999) {
		}
	}

	private synchronized void setUpdateInterval() {
		if (currentMilliseconds < (preferences.getBoardPreferences()
				.getShowTenthsWhenTimeIsLessThanSeconds() * 1000 + 1000)
				|| (isSpeakingCountdown() && currentMilliseconds < 12)) {
			updateInterval = 100;
		} else {
			updateInterval = 1000;
		}
	}

	private long getNextClockUpdate() {
		return updateInterval;
	}

	private class ChessClockUpdater extends TimerTask {
		public void run() {
			synchronized (thisClock) {
				currentMilliseconds -= updateInterval;
			}
			setToCurrentTime();
			setUpdateInterval();
			handleFiringSpeech(currentMilliseconds);
			if (isRunning && currentMilliseconds > 0) {
				timer.schedule(new ChessClockUpdater(), getNextClockUpdate());
			}

		}
	}
}