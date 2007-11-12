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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

import decaf.moveengine.Clock;
import decaf.moveengine.ClockListener;


public class LCDClock {
	private int hours;

	private int minutes;

	private int seconds;

	private int tenths;

	private boolean isShowingSeconds = true;

	private boolean isShowingTenths = true;

	private LCD lcd = new LCD();

	private Paint lcdOnColor;

	private Paint lcdOffColor;

	public void setTime(int hours, int minutes, int seconds, int tenths) {
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.tenths = tenths;
	}

	/**
	 * timeArray[0] = hours, timeArray[1] = minutes, timeArray[2] = seconds,
	 * timeArray[3] = millis.
	 */
	public void setTime(int[] timeArray) {
		setTime(timeArray[0], timeArray[1], timeArray[2], timeArray[3] / 100);
	}

	public Image getImage(Graphics2D graphics, int width, int height) {
		// 2 digits for hours punctuation 2 digits for minutes punctuation 2
		// digits for seconds punctuation 1 digit for tenths.
		int digitWidth = width / 9;
		int punctuationWidth = digitWidth / 3;
		int space = digitWidth / 9;
		int digitHeight = height;
		lcd.optimize(graphics, digitWidth, digitHeight);

		BufferedImage image = graphics.getDeviceConfiguration()
				.createCompatibleImage(width, height,
						BufferedImage.TYPE_INT_RGB);
		Graphics2D imageGraphics = (Graphics2D) image.getGraphics();
		imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int usedWidth = 0;
		imageGraphics.drawImage(lcd.getImage(hours / 10), 0, 0, null);
		usedWidth += digitWidth + space;
		imageGraphics.drawImage(lcd.getImage(hours % 10), usedWidth, 0, null);
		usedWidth += digitWidth + space;
		usedWidth += punctuationWidth;
		usedWidth += space;
		imageGraphics.drawImage(lcd.getImage(minutes / 10), usedWidth, 0, null);
		usedWidth += digitWidth + space;
		imageGraphics.drawImage(lcd.getImage(minutes % 10), usedWidth, 0, null);
		if (isShowingSeconds) {
			usedWidth += digitWidth + space;
			usedWidth += punctuationWidth;
			usedWidth += space;
			imageGraphics.drawImage(lcd.getImage(seconds / 10), usedWidth, 0,
					null);
			usedWidth += digitWidth + space;
			imageGraphics.drawImage(lcd.getImage(seconds % 10), usedWidth, 0,
					null);
			if (isShowingTenths) {
				usedWidth += digitWidth + space;
				usedWidth += punctuationWidth;
				usedWidth += space;
				imageGraphics.drawImage(lcd.getImage(tenths), usedWidth, 0,
						null);
			}
		}
		return image;
	}

	public static class TestClockListener implements ClockListener {
		Clock clock;

		LCDClock lcdClock;

		Component component;

		public TestClockListener(Component component, LCDClock lcdClock,
				Clock clock) {
			this.clock = clock;
			this.lcdClock = lcdClock;
			this.component = component;
		}

		public void playerFlagged(boolean isWhitePlayer) {
		}

		public void tick() {
			lcdClock.setTime(clock.getTime(true));
			component.repaint();
		}

		public void plungerPressed(boolean isWhitePlunger) {
		}
	};

	public static void main(String[] args) {
		final LCDClock lcdClock = new LCDClock();
		final Clock clock = new Clock(5, 0);
		clock.setTicInterval(1000);

		final JComponent component = new JComponent() {
			protected void paintComponent(Graphics g) {
				Graphics2D graphics = (Graphics2D) g;
				((Graphics2D) g).setComposite(AlphaComposite.SrcOver);
				g.drawImage(lcdClock.getImage((Graphics2D) g, getWidth(),
						getHeight()), 0, 0, null);
			}
		};

		TestClockListener listener = new TestClockListener(component, lcdClock,
				clock);
		clock.addClockistener(listener);

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(component);
		frame.setBounds(100, 100, 300, 100);
		frame.setVisible(true);
		clock.hitPlunger(false);
	}

}