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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * A class that creates 7 segment LCD images.
 */
public class LCD {
	private Paint onPaint = new Color(40,40,40);

	private Paint offPaint = Color.red;

	/**
	 * 0 = top 1 = top left 2 = top right 3 = bottom left 4 = bottom right 5 =
	 * bottom 6 = middle
	 */
	public static final boolean[][] PATTERNS = new boolean[][] {
			{ true, true, true, true, true, true, false },
			{ false, false, true, false, true, false, false },
			{ true, false, true, true, false, true, true },
			{ true, false, true, false, true, true, true },
			{ false, true, true, false, true, false, true },
			{ true, true, false, false, true, true, true },
			{ true, true, false, true, true, true, true },
			{ true, false, true, false, true, false, false },
			{ true, true, true, true, true, true, true },
			{ true, true, true, false, true, false, true },
			{ false, false, false, false, false, false, true }, // - signe
			{ false, false, false, false, false, false, false } // empty
	};

	private Image[] digitImages = new Image[PATTERNS.length];

	private int lastWidth = -1234;

	private int lastHeight = -1234;

	public Paint getOnPaint() {
		return onPaint;
	}

	public void setOnPaint(Paint onPaint) {
		this.onPaint = onPaint;
	}

	public Paint getOffPaint() {
		return offPaint;
	}

	public void setOffPaint(Paint offPaint) {
		this.offPaint = offPaint;
	}

	public void optimize(Graphics2D graphics, int width, int height) {
		if (width != lastWidth || height != lastHeight) {
			for (int i = 0; i < digitImages.length; i++) {
				digitImages[i] = buildImage(graphics, width, height, i);
			}
			lastWidth = width;
			lastHeight = height;
		}
	}

	/**
	 * Digits 0-9 are self explanatory. Digit 10 is a negative sign. Digit 11
	 * turns all lights off.
	 */
	public Image getImage(int digit) {
		if (digit < 0 || digit > digitImages.length) {
			throw new IllegalArgumentException("Invalid digit: " + digit);
		}

		if (digitImages[0] == null) {
			throw new IllegalStateException(
					"Must call optimize before getImage");
		}

		return digitImages[digit];
	}

	/**
	 * Builds a seven segment LED.
	 */
	private Image buildImage(Graphics2D graphics, int width, int height,
			int digit) {
		double xSegmentHeight = height / 7;
		double border = xSegmentHeight / 2;
		double xSegmentWidth = width - 2 * border;

		double ySegmentWidth = (height - xSegmentHeight) / 2;
		double ySegmentHeight = xSegmentHeight;

		Shape xSegment = buildSegment((int) xSegmentWidth, (int) xSegmentHeight);
		Shape ySegment = buildSegment((int) ySegmentWidth, (int) ySegmentHeight);

		double halfXHeight = xSegment.getBounds().getHeight() / 2;
		boolean[] isLit = PATTERNS[digit];

		BufferedImage image = graphics.getDeviceConfiguration()
				.createCompatibleImage(width, height,
						BufferedImage.TYPE_INT_RGB);
		Graphics2D imageGraphics = (Graphics2D) image.getGraphics();
		imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// top
		AffineTransform transform = AffineTransform.getTranslateInstance(
				border, 0);
		imageGraphics.setPaint(isLit[0] ? offPaint : onPaint);
		imageGraphics.setTransform(transform);
		imageGraphics.fill(xSegment);

		// top left
		transform = AffineTransform.getTranslateInstance(border, 0);
		transform.concatenate(AffineTransform.getRotateInstance(Math.PI / 2D,
				0, halfXHeight));
		imageGraphics.setTransform(transform);
		imageGraphics.setPaint(isLit[1] ? offPaint : onPaint);
		imageGraphics.fill(ySegment);

		// top right
		transform = AffineTransform.getTranslateInstance(width - border, 0);
		transform.concatenate(AffineTransform.getRotateInstance(Math.PI / 2D,
				0, halfXHeight));
		imageGraphics.setPaint(isLit[2] ? offPaint : onPaint);
		imageGraphics.setTransform(transform);
		imageGraphics.fill(ySegment);

		// middle
		transform = AffineTransform.getTranslateInstance(border, ySegmentWidth);
		imageGraphics.setTransform(transform);
		imageGraphics.setPaint(isLit[6] ? offPaint : onPaint);
		imageGraphics.fill(xSegment);

		// bottom left
		transform = AffineTransform.getTranslateInstance(border, ySegmentWidth);
		transform.concatenate(AffineTransform.getRotateInstance(Math.PI / 2D,
				0, halfXHeight));
		imageGraphics.setTransform(transform);
		imageGraphics.setPaint(isLit[3] ? offPaint : onPaint);
		imageGraphics.fill(ySegment);

		// bottom right
		transform = AffineTransform.getTranslateInstance(width - border,
				ySegmentWidth);
		transform.concatenate(AffineTransform.getRotateInstance(Math.PI / 2D,
				0, halfXHeight));
		imageGraphics.setTransform(transform);
		imageGraphics.setPaint(isLit[4] ? offPaint : onPaint);
		imageGraphics.fill(ySegment);

		// bottom
		transform = AffineTransform.getTranslateInstance(border, height
				- xSegmentHeight);
		imageGraphics.setTransform(transform);
		imageGraphics.setPaint(isLit[5] ? offPaint : onPaint);
		imageGraphics.fill(xSegment);

		return image;
	}

	/**
	 * Builds an LCD segment in a 10x10 rectangle.
	 */
	private Shape buildSegment(int segmentWidth, int segmentHeight) {
		int border = 0;

		int halfHeight = segmentHeight / 2;
		int forthWidth = segmentWidth / 4;

		GeneralPath result = new GeneralPath();
		result.moveTo(border, halfHeight);
		result.lineTo(border + forthWidth, border);
		result.lineTo(segmentWidth - border - forthWidth, border);
		result.lineTo(segmentWidth - border, halfHeight);
		result.lineTo(segmentWidth - border - forthWidth, segmentHeight
				- border);
		result.lineTo(border + forthWidth, segmentHeight - border);
		result.lineTo(border, halfHeight);
		result.closePath();
		return result.createTransformedShape(new AffineTransform());
	}

	public static void main(String args[]) {
		final LCD lcd = new LCD();
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JComponent() {
			protected void paintComponent(Graphics g) {
				lcd.optimize((Graphics2D) g, getWidth(), getHeight());
				g.drawImage(lcd.getImage(1), 0, 0, null);
			}
		});
		frame.setBounds(0, 0, 100, 100);
		frame.setVisible(true);
	}
}