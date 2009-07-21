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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.WeakHashMap;

public class ChessPieceCache {

	private Map<ChessPieceCacheKey, BufferedImage> keyToImageMap = new WeakHashMap<ChessPieceCacheKey, BufferedImage>();

	private class ChessPieceCacheKey {
		private int hash;

		private int chessPiece;

		private Dimension dimension;

		public boolean equals(Object object) {
			if (object == null) {
				return false;
			}
			else {
				ChessPieceCacheKey compare = (ChessPieceCacheKey) object;
				return chessPiece == compare.chessPiece
						&& dimension.equals(compare.dimension);
			}
		}

		public int hashCode() {
			if (hash == 0) {
				hash = chessPiece + dimension.width + dimension.height;

			}
			return hash;
		}
	}

	public BufferedImage getChessPiece(ChessSet set, int chessPiece, int width,
			int height) {

		if (width <= 0 || height <= 0) {
			width = 1;
			height = 1;
		}

		ChessPieceCacheKey key = new ChessPieceCacheKey();
		key.dimension = new Dimension(width, height);
		key.chessPiece = chessPiece;

		BufferedImage result = null;

		result = keyToImageMap.get(key);

		if (result == null) {
			result = set.getScaledImage(chessPiece, width, height);
			keyToImageMap.put(key, result);
		}
		return result;
	}
}
