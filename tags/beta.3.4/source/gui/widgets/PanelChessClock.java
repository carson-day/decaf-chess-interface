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

package decaf.gui.widgets;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import decaf.gui.Disposable;
import decaf.gui.event.ChessClockListener;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.TextProperties;

// Referenced classes of package caffeine.gui.widgets:
// Skinnable, Skin

public class PanelChessClock extends JPanel implements Preferenceable,
		Disposable {

	private static final int MAX_TIME = 59940;

	private static final int SECONDS_IN_HOUR = 3600;

	private static final int SECONDS_IN_MINUTE = 60;

	private int showTenthsWhenTimeIsLessThanSeconds;

	private int currentMilliseconds;

	private int initialSeconds;

	private int increment;

	private JLabel timeLabel;

	private ChessClockUpdater updater;

	private Preferences preferences;

	private boolean isRunning = false;

	private List chessClockListeners;

	private boolean hasBeenDisposed;

	public void dispose() {
		synchronized (this) {
			if (!hasBeenDisposed) {
				hasBeenDisposed = true;
				if (chessClockListeners != null) {
					chessClockListeners.clear();
					chessClockListeners = null;
				}
				timeLabel = null;
				if (updater != null) {
					updater.stop();
					updater = null;
				}
				preferences = null;
			}
		}
	}

	public PanelChessClock(int timeInSeconds, int increment) {
		timeLabel = new JLabel("000:00.0");
		this.initialSeconds = timeInSeconds;
		this.increment = increment;
		this.currentMilliseconds = timeInSeconds * 1000;
		add(timeLabel);
		setToCurrentTime();
		isRunning = false;
		chessClockListeners = new LinkedList();
	}

	public void addChessClockListener(ChessClockListener listener) {
		synchronized (this) {
			if (!hasBeenDisposed) {
				chessClockListeners.add(listener);
			}
		}
	}

	public void removeChessClockListener(ChessClockListener listener) {
		synchronized (this) {
			if (!hasBeenDisposed) {
				chessClockListeners.remove(listener);
			}
		}
	}

	public void fireChessClockAt(int second) {
		synchronized (this) {
			if (!hasBeenDisposed) {
				for (int i = 0; i < chessClockListeners.size(); i++) {
					((ChessClockListener) chessClockListeners.get(i))
							.clockIsAtSecond(second);
				}
			}
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
			int i = Math.abs(currentMilliseconds);
			boolean flag = currentMilliseconds < 0;
			int j = i / 1000;
			int k = j < 60 ? j : j % 60;
			int l = j < 3600 ? j / 60 : (j % 3600) / 60;
			int i1 = j / 3600;
			boolean flag1 = j < showTenthsWhenTimeIsLessThanSeconds;
			int j1 = 0;
			if (flag1)
				j1 = (i % 1000) / 100;
			String s = (flag ? "-" : "") + (i1 != 0 ? "" + i1 + ":" : "")
					+ (l != 0 ? l <= 0 || l >= 10 ? "" + l : "" + l : "0")
					+ ":"
					+ (k != 0 ? k <= 0 || k >= 10 ? "" + k : "0" + k : "00")
					+ (flag1 ? "." + j1 : "");
			timeLabel.setText(s);
		}
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
			if (isRunning) {
				isRunning = false;
				if (updater != null) {
					updater.stop();
				}
				updater = null;
				currentMilliseconds += increment * 1000;
				setTextProperties();
			}
		}
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void start() {
		synchronized (this) {
			if (!hasBeenDisposed) {
				if (!isRunning) {
					isRunning = true;
					updater = new ChessClockUpdater();
					setTextProperties();
				}
			}
		}
	}

	public void startWithoutTicking() {
		synchronized (this) {
			if (!hasBeenDisposed) {

				if (!isRunning) {
					isRunning = true;
					// updater = new ChessClockUpdater();
					setTextProperties();
				}
			}
		}
	}

	public void set(int i) {
		synchronized (this) {

			if (!hasBeenDisposed) {
				currentMilliseconds = i * 1000;
				setToCurrentTime();
			}
		}
	}

	public void set(int i, int j) {
		synchronized (this) {
			if (!hasBeenDisposed) {
				increment = j;
				set(i);
			}
		}
	}

	public boolean hasFlagged() {
		boolean flag = false;
		if (currentMilliseconds <= 0)
			flag = true;
		return flag;
	}

	private void handleFiringClockAtEvent(int currentMilliseconds) {
		if (currentMilliseconds <= 10099 && currentMilliseconds >= 10000) {
			fireChessClockAt(10);
		} else if (currentMilliseconds <= 9099 && currentMilliseconds >= 9000) {
			fireChessClockAt(9);
		} else if (currentMilliseconds <= 8099 && currentMilliseconds >= 8000) {
			fireChessClockAt(8);
		} else if (currentMilliseconds <= 7099 && currentMilliseconds >= 7000) {
			fireChessClockAt(7);
		} else if (currentMilliseconds <= 6099 && currentMilliseconds >= 6000) {
			fireChessClockAt(6);
		} else if (currentMilliseconds <= 5099 && currentMilliseconds >= 5000) {
			fireChessClockAt(5);
		} else if (currentMilliseconds <= 4099 && currentMilliseconds >= 4000) {
			fireChessClockAt(4);
		} else if (currentMilliseconds <= 3099 && currentMilliseconds >= 3000) {
			fireChessClockAt(3);
		} else if (currentMilliseconds <= 2099 && currentMilliseconds >= 2000) {
			fireChessClockAt(2);
		} else if (currentMilliseconds <= 1099 && currentMilliseconds >= 1000) {
			fireChessClockAt(1);
		} else if (currentMilliseconds <= 99 && currentMilliseconds >= 0) {
			fireChessClockAt(0);
		} else if (currentMilliseconds < -999) {
			fireChessClockAt(currentMilliseconds / 1000);
		}
	}

	private class ChessClockUpdater implements Runnable {

		public synchronized void stop() {
			stop = true;
		}

		public void run() {
			while (!stop) {
				// int i = currentMilliseconds <= 0 || currentMilliseconds /
				// 1000 > showTenthsWhenTimeIsLessThanSeconds ? 1000 : 100;

				try {
					ChessClockUpdater _tmp = this;
					Thread.sleep(100);
				} catch (InterruptedException interruptedexception) {
				}
				if (!stop) {
					currentMilliseconds -= 100;
					setToCurrentTime();
					handleFiringClockAtEvent(currentMilliseconds);
				}
			}
		}

		private Thread thread;

		private boolean stop;

		public ChessClockUpdater() {
			thread = new Thread(this);
			thread.start();
		}
	}
}