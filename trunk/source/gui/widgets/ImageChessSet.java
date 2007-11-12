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
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.ObjectStreamException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JButton;

public class ImageChessSet extends ChessSet {

	private static final String RESOURCES_DIR = "./Resources";

	private transient Image[] images = null;

	private String setName;

	private static final Color TRANSPARENT_COLOR = new Color(255, 255, 0);

	private ImageChessSet() {
		super();
	}

	public ImageChessSet(String setName) {
		super();

		this.setName = setName;

		initImages();
	}

	public static String[] getSetNames() {
		List result = new LinkedList();

		File file = new File(RESOURCES_DIR);
		File[] files = file.listFiles(new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				return arg1.startsWith("SET.") && arg1.indexOf("WBISHOP") != -1;
			}

		});

		for (int i = 0; i < files.length; i++) {
			StringTokenizer tok = new StringTokenizer(files[i].getName(), ".");
			tok.nextToken();
			result.add(tok.nextToken());
		}
		return (String[]) result.toArray(new String[0]);
	}

	Object readResolve() throws ObjectStreamException {
		initImages();
		return this;
	}

	private void initImages() {
		final String piecePrefix = "SET." + setName + ".";
		File file = new File(RESOURCES_DIR);
		File[] files = file.listFiles(new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				return arg1.indexOf(piecePrefix + "WBISHOP") != -1;
			}

		});

		if (files.length == 0) {
			throw new IllegalArgumentException("Set " + setName + " not found.");

		}

		String suffix = files[0].getName().substring(
				files[0].getName().lastIndexOf(".") + 1,
				files[0].getName().length());

		images = new Image[] {
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix
						+ "BBISHOP." + suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix
						+ "BKNIGHT." + suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix
						+ "BQUEEN." + suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix + "BPAWN."
						+ suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix + "BKING."
						+ suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix + "BROOK."
						+ suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix
						+ "WBISHOP." + suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix
						+ "WKNIGHT." + suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix
						+ "WQUEEN." + suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix + "WPAWN."
						+ suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix + "WKING."
						+ suffix),
				getChessPieceImage(RESOURCES_DIR + "/" + piecePrefix + "WROOK."
						+ suffix) };

	}

	public String getDescription() {
		return setName;
	}

	public ChessSet cloneChessSet() {
		ImageChessSet result = new ImageChessSet();
		result.images = this.images;
		return result;
	}

	public ChessPiece createDarkBishop() {
		return new ImageChessPiece(ChessPiece.BB, images[0]);
	}

	public ChessPiece createDarkKing() {
		return new ImageChessPiece(ChessPiece.BK, images[4]);
	}

	public ChessPiece createDarkKnight() {
		return new ImageChessPiece(ChessPiece.BN, images[1]);
	}

	public ChessPiece createDarkPawn() {
		return new ImageChessPiece(ChessPiece.BP, images[3]);
	}

	public ChessPiece createDarkQueen() {
		return new ImageChessPiece(ChessPiece.BQ, images[2]);
	}

	public ChessPiece createDarkRook() {
		return new ImageChessPiece(ChessPiece.BR, images[5]);
	}

	public ChessPiece createLightBishop() {
		return new ImageChessPiece(ChessPiece.WB, images[6]);
	}

	public ChessPiece createLightKing() {
		return new ImageChessPiece(ChessPiece.WK, images[10]);
	}

	public ChessPiece createLightKnight() {
		return new ImageChessPiece(ChessPiece.WN, images[7]);
	}

	public ChessPiece createLightPawn() {
		return new ImageChessPiece(ChessPiece.WP, images[9]);
	}

	public ChessPiece createLightQueen() {
		return new ImageChessPiece(ChessPiece.WQ, images[8]);
	}

	public ChessPiece createLightRook() {
		return new ImageChessPiece(ChessPiece.WR, images[11]);
	}

	private class ImageChessPiece extends ChessPiece {
		private Image pieceImage = null;

		public ImageChessPiece(int piece, Image image) {
			super(piece);
			pieceImage = image;
		}

		public ChessPiece cloneChessPiece() {
			return new ImageChessPiece(getType(), pieceImage);
		}

		protected void paintComponent(Graphics g) {
			if (!isTransparent()) {

				((Graphics2D) g).setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				g.drawImage(pieceImage, 0, 0, getWidth(), getHeight(), null);
			}
		}
	}

	public static Image getChessPieceImage(String location) {
		try {
			Image unTransparent = ImageIO.read(new File(location));

			MediaTracker mediaTracker = new MediaTracker(new JButton("test"));

			mediaTracker.waitForAll();

			return makeColorTransparent(unTransparent, TRANSPARENT_COLOR);
		} catch (Exception ioe) {
			throw new IllegalArgumentException("Error loading image "
					+ location, ioe);
		}
	}

	public static Image makeColorTransparent(Image im, final Color color) {
		ImageFilter filter = new RGBImageFilter() {
			// the color we are looking for... Alpha bits are set to opaque
			public int markerRGB = color.getRGB() | 0xFF000000;

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

}
