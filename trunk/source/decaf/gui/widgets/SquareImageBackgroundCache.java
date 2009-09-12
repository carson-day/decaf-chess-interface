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

public class SquareImageBackgroundCache {

	private class SquareImageCacheKey {
		private int file;

		private int rank;

		private Dimension dimension;

		private int hash;

		@Override
		public boolean equals(Object object) {
			SquareImageCacheKey compare = (SquareImageCacheKey) object;
			if (compare == null) {
				return false;
			} else {
				return file == compare.file && rank == compare.rank
						&& dimension.equals(compare.dimension);
			}
		}

		@Override
		public int hashCode() {
			if (hash == 0) {
				hash = file * rank + dimension.width + dimension.height;

			}
			return hash;
		}
	}

	private Map<SquareImageCacheKey, BufferedImage> keyToImageMap = new WeakHashMap<SquareImageCacheKey, BufferedImage>();

	public BufferedImage getSquareBackgroundImage(
			SquareImageBackground squareImageBackground, int width, int height,
			int rank, int file) {

		if (width <= 0 || height <= 0) {
			width = 1;
			height = 1;
		}

		SquareImageCacheKey key = new SquareImageCacheKey();
		key.dimension = new Dimension(width, height);
		key.rank = rank;
		key.file = file;

		BufferedImage result = null;

		result = keyToImageMap.get(key);

		if (result == null) {
			result = squareImageBackground.getScaledImage(rank, file, width,
					height);

			keyToImageMap.put(key, result);
		}

		return result;
	}
}