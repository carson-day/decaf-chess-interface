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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.ObjectStreamException;
import java.io.Serializable;

import javax.swing.JButton;

import org.apache.log4j.Logger;

import decaf.moveengine.Piece;
import decaf.resources.ResourceManagerFactory;

public class ChessSet implements Serializable {
	private static final long serialVersionUID = 11;

	private static final transient Logger LOGGER = Logger
			.getLogger(ChessSet.class);

	public static Image getChessPieceImage(String location) {
		try {
			Image unTransparent = ResourceManagerFactory.getManager().getImage(
					location);

			MediaTracker mediaTracker = new MediaTracker(new JButton("test"));

			mediaTracker.waitForAll();

			Image result = makeColorTransparent(unTransparent,
					TRANSPARENT_COLOR);

			Toolkit.getDefaultToolkit().prepareImage(result, -1, -1, null);

			return result;
		} catch (Exception ioe) {
			throw new IllegalArgumentException("Error loading image "
					+ location, ioe);
		}
	}

	public static Image makeColorTransparent(Image im, final Color color) {
		ImageFilter filter = new RGBImageFilter() {
			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

			@Override
			public final int filterRGB(int x, int y, int rgb) {
				if ((rgb | 0xFF000000) == markerRGB) {
					// Mark the alpha bits as zero - transparent
					return 0x00FFFFFF & rgb;
				} else {
					// nothing to do
					return rgb;
				}
			}
		};

		ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
		return Toolkit.getDefaultToolkit().createImage(ip);
	}

	private transient Image[] initialImages = null;

	private transient static final Color TRANSPARENT_COLOR = new Color(255,
			255, 0);

	private String setName;

	private ChessSet() {
		super();
	}

	public ChessSet(String setName) {
		super();

		this.setName = setName;

		initImages();
	}

	public ChessSet cloneChessSet() {
		ChessSet result = new ChessSet();
		result.initialImages = this.initialImages;
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (o != null) {
			return getDescription().equals(((ChessSet) o).getDescription());
		} else {
			return false;
		}
	}

	public Image getChessPieceImage(int chessPiece) {
		switch (chessPiece) {
		case Piece.BB: {
			return initialImages[0];
		}
		case Piece.BK: {
			return initialImages[4];
		}
		case Piece.BN: {
			return initialImages[1];
		}
		case Piece.BP: {
			return initialImages[3];
		}
		case Piece.BQ: {
			return initialImages[2];
		}
		case Piece.BR: {
			return initialImages[5];
		}
		case Piece.WB: {
			return initialImages[6];
		}
		case Piece.WK: {
			return initialImages[10];
		}
		case Piece.WN: {
			return initialImages[7];
		}
		case Piece.WP: {
			return initialImages[9];
		}
		case Piece.WQ: {
			return initialImages[8];
		}
		case Piece.WR: {
			return initialImages[11];
		}
		default: {
			throw new IllegalArgumentException("Invalid piece " + chessPiece);
		}
		}
	}

	public Cursor getCursor(int chessPiece, int width, int height) {
		if (width <= 0 || height <= 0) {
			width = 1;
			height = 1;
		}

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getBestCursorSize(width, height);

		BufferedImage image = getScaledImage(chessPiece, width, height);

		try {
			Thread.sleep(40);
		} catch (InterruptedException ie) {
		}

		return toolkit.createCustomCursor(image, new Point((int) (dimension
				.getWidth() / 2), (int) (dimension.getHeight() / 2)),
				chessPiece + " Cursor");

	}

	public String getDescription() {
		return setName;
	}

	public BufferedImage getScaledImage(int chessPiece, int width, int height) {

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

		Image initialImage = getChessPieceImage(chessPiece);

		graphics.drawImage(initialImage, 0, 0, width, height, null);

		return result;
	}

	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}

	private void initImages() {
		final String piecePrefix = "SET." + setName + ".";

		String suffix = "BMP";

		initialImages = new Image[] {
				getChessPieceImage(piecePrefix + "BBISHOP." + suffix),
				getChessPieceImage(piecePrefix + "BKNIGHT." + suffix),
				getChessPieceImage(piecePrefix + "BQUEEN." + suffix),
				getChessPieceImage(piecePrefix + "BPAWN." + suffix),
				getChessPieceImage(piecePrefix + "BKING." + suffix),
				getChessPieceImage(piecePrefix + "BROOK." + suffix),
				getChessPieceImage(piecePrefix + "WBISHOP." + suffix),
				getChessPieceImage(piecePrefix + "WKNIGHT." + suffix),
				getChessPieceImage(piecePrefix + "WQUEEN." + suffix),
				getChessPieceImage(piecePrefix + "WPAWN." + suffix),
				getChessPieceImage(piecePrefix + "WKING." + suffix),
				getChessPieceImage(piecePrefix + "WROOK." + suffix) };

	}

	Object readResolve() throws ObjectStreamException {
		initImages();
		return this;
	}
}
