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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChessPieceCache {

	private static final int MAX_DIMENSIONS_MANAGED = 5;

	private Map<ChessPieceCacheKey, BufferedImage> keyToImageMap = new HashMap<ChessPieceCacheKey, BufferedImage>();

	private ChessSet lastSet;

	private LinkedList<Dimension> dimensionsManaged = new LinkedList<Dimension>();

	private class ChessPieceCacheKey {
		private int hash;

		private int chessPiece;

		private Dimension dimension;

		public boolean equals(Object object) {
			ChessPieceCacheKey compare = (ChessPieceCacheKey) object;
			return chessPiece == compare.chessPiece
					&& dimension.equals(compare.dimension);
		}

		public int hashCode() {
			if (hash == 0) {
				hash = chessPiece + dimension.width + dimension.height;

			}
			return hash;
		}
	}

	public void clear() {
		synchronized (this) {
			keyToImageMap.clear();
			dimensionsManaged.clear();
		}
	}

	private synchronized void purgeDimension(Dimension dimension) {
		List<ChessPieceCacheKey> list = new ArrayList<ChessPieceCacheKey>(
				keyToImageMap.keySet());
		for (int i = 0; i < list.size(); i++) {
			ChessPieceCacheKey key = (ChessPieceCacheKey) list.get(i);
			if (key.dimension.equals(dimension)) {
				keyToImageMap.remove(key);
			}
		}
	}

	private synchronized void purgeOldDimensions() {
		if (dimensionsManaged.size() > MAX_DIMENSIONS_MANAGED) {
			// dump the first one in the list.
			purgeDimension(dimensionsManaged.getFirst());
			dimensionsManaged.removeFirst();
		}
	}

	public BufferedImage getChessPiece(ChessSet set, int chessPiece, int width,
			int height) {
		
		if (width <= 0 || height <= 0)
		{
			width=1;
			height=1;
		}

		purgeOldDimensions();

		if (!set.equals(lastSet)) {
			clear();
		}

		lastSet = set;
		ChessPieceCacheKey key = new ChessPieceCacheKey();
		key.dimension = new Dimension(width, height);
		key.chessPiece = chessPiece;

		BufferedImage result = null;
		synchronized (this) {
			result = keyToImageMap.get(key);
		}

		if (result == null) {
			result = set.getScaledImage(chessPiece, width, height);

			if (!dimensionsManaged.contains(key.dimension)) {
				dimensionsManaged.addLast(key.dimension);
			}
			keyToImageMap.put(key, result);
		}
		return result;
	}
}
