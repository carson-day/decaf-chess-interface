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

public class SquareImageBackgroundCache {

	private static final int MAX_DIMENSIONS_MANAGED = 5;

	private Map<SquareImageCacheKey, BufferedImage> keyToImageMap = new HashMap<SquareImageCacheKey, BufferedImage>();

	private SquareImageBackground lastSquareImageBackground;

	private LinkedList<Dimension> dimensionsManaged = new LinkedList<Dimension>();

	private class SquareImageCacheKey {
		private int file;

		private int rank;

		private Dimension dimension;

		private int hash;

		public boolean equals(Object object) {
			SquareImageCacheKey compare = (SquareImageCacheKey) object;
			return file == compare.file && rank == compare.rank
					&& dimension.equals(compare.dimension);
		}

		public int hashCode() {
			if (hash == 0) {
				hash = file * rank + dimension.width + dimension.height;

			}
			return hash;
		}
	}

	private synchronized void purgeDimension(Dimension dimension) {
		List<SquareImageCacheKey> list = new ArrayList<SquareImageCacheKey>(
				keyToImageMap.keySet());
		for (int i = 0; i < list.size(); i++) {
			SquareImageCacheKey key = (SquareImageCacheKey) list.get(i);
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

	public void clear() {
		synchronized (this) {
			keyToImageMap.clear();
			dimensionsManaged.clear();
		}
	}

	public BufferedImage getSquareBackgroundImage(
			SquareImageBackground squareImageBackground, int width, int height,
			int rank, int file) {

		if (width <= 0 || height <= 0)
		{
			width=1;
			height=1;
		}
		
		if (!squareImageBackground.equals(lastSquareImageBackground)) {
			clear();
		}

		lastSquareImageBackground = squareImageBackground;

		SquareImageCacheKey key = new SquareImageCacheKey();
		key.dimension = new Dimension(width, height);
		key.rank = rank;
		key.file = file;

		BufferedImage result = null;
		synchronized (this) {
			result = keyToImageMap.get(key);
		}

		if (result == null) {
			result = squareImageBackground.getScaledImage(rank, file, width,
					height);

			if (!dimensionsManaged.contains(key.dimension)) {
				dimensionsManaged.addLast(key.dimension);
			}
			keyToImageMap.put(key, result);
		}

		purgeOldDimensions();

		return result;
	}
}