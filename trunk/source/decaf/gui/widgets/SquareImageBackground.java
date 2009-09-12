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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;

import javax.swing.JButton;

import org.apache.log4j.Logger;

import decaf.resources.ResourceManagerFactory;

public class SquareImageBackground implements Serializable {
	private static final long serialVersionUID = 11;

	private transient static final Logger LOGGER = Logger
			.getLogger(SquareImageBackground.class);

	private String name;

	private transient boolean isCrop;

	private transient Image darkSquareImage;

	private transient Image lightSquareImage;

	private transient HashMap<String, Image> coordinatesToImage = new HashMap<String, Image>();

	public SquareImageBackground(String name) {
		this.name = name;
		initImages();
	}

	private void compile() {
		boolean isWhiteSquare = false;

		int width = lightSquareImage.getWidth(null);
		int height = lightSquareImage.getHeight(null);
		int w = width > height ? height / 8 : width / 8;

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Compiling SquareImageBackground (" + name + ") " + w);
		}
		for (int i = 0; i < 8; i++) {
			isWhiteSquare = !isWhiteSquare;

			for (int j = 0; j < 8; j++) {
				if (isCrop) {
					CropImageFilter filter = new CropImageFilter(i * w, j * w,
							w, w);
					ImageProducer ip = new FilteredImageSource(
							isWhiteSquare ? lightSquareImage.getSource()
									: darkSquareImage.getSource(), filter);
					coordinatesToImage.put("" + i + j, Toolkit
							.getDefaultToolkit().createImage(ip));
				} else {
					coordinatesToImage.put("" + i + j,
							isWhiteSquare ? lightSquareImage : darkSquareImage);
				}
				isWhiteSquare = !isWhiteSquare;
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			return getName().equals(((SquareImageBackground) o).getName());
		} else {
			return false;
		}

	}

	public Image getImage(int rank, int file) {
		return coordinatesToImage.get("" + rank + file);
	}

	public String getName() {
		return name;
	}

	public BufferedImage getScaledImage(int rank, int file, int width,
			int height) {

		if (width <= 0 || height <= 0) {
			width = 1;
			height = 1;
		}

		BufferedImage result = new BufferedImage(width, height, 2);

		Graphics2D graphics = (Graphics2D) result.getGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

		Image initialImage = getImage(rank, file);

		graphics.drawImage(initialImage, 0, 0, width, height, null);

		return result;
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	private void initImages() {
		String suffix = "BMP";
		String darkImage = "SQUARE." + name + ".DARK." + suffix;
		String lightImage = "SQUARE." + name + ".LIGHT." + suffix;

		try {
			darkSquareImage = ResourceManagerFactory.getManager().getImage(
					darkImage);
			lightSquareImage = ResourceManagerFactory.getManager().getImage(
					lightImage);
			isCrop = false;
		} catch (Exception e) {
			darkImage = "SQUARE.CROP." + name + ".DARK." + suffix;
			lightImage = "SQUARE.CROP." + name + ".LIGHT." + suffix;

			darkSquareImage = ResourceManagerFactory.getManager().getImage(
					darkImage);
			lightSquareImage = ResourceManagerFactory.getManager().getImage(
					lightImage);
			isCrop = true;
		}

		MediaTracker mediaTracker = new MediaTracker(new JButton("test"));

		try {
			mediaTracker.waitForAll();
		} catch (InterruptedException ie) {
		}
		compile();
	}

	Object readResolve() throws ObjectStreamException {
		try {
			coordinatesToImage = new HashMap<String, Image>();
			initImages();
		} catch (Exception e) {
			LOGGER.error(e);
		}
		return this;
	}

}
