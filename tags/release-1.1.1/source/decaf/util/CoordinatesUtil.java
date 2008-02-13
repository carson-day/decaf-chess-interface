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
package decaf.util;

import decaf.moveengine.Coordinates;
import decaf.moveengine.LongAlgebraicEncoder;
import decaf.moveengine.Move;
import decaf.moveengine.MoveEncoder;

/**
 * A class containing utility methods for coordinates.
 */
public class CoordinatesUtil implements Coordinates {
	private static final MoveEncoder DEFAULT_MOVE_ENCODER = new LongAlgebraicEncoder();

	private CoordinatesUtil() {
	}

	public static int algebraicRankToCoordinatesRank(char rankChar) {
		int rank = -1;

		switch (rankChar) {
		case '1': {
			rank = 7;
			break;
		}
		case '2': {
			rank = 6;
			break;
		}
		case '3': {
			rank = 5;
			break;
		}
		case '4': {
			rank = 4;
			break;
		}
		case '5': {
			rank = 3;
			break;
		}
		case '6': {
			rank = 2;
			break;
		}
		case '7': {
			rank = 1;
			break;
		}
		case '8': {
			rank = 0;
			break;
		}
		default: {
			throw new IllegalArgumentException("Invalid rank char " + rankChar);
		}
		}
		return rank;
	}

	public static int algebraicFileToCoordinatesFile(char fileChar) {
		int file = -1;

		switch (fileChar) {
		case 'A':
		case 'a': {
			file = A;
			break;
		}
		case 'B':
		case 'b': {
			file = B;
			break;
		}
		case 'C':
		case 'c': {
			file = C;
			break;
		}
		case 'D':
		case 'd': {
			file = D;
			break;
		}
		case 'E':
		case 'e': {
			file = E;
			break;
		}
		case 'F':
		case 'f': {
			file = F;
			break;
		}
		case 'G':
		case 'g': {
			file = G;
			break;
		}
		case 'H':
		case 'h': {
			file = H;
			break;
		}
		default: {
			throw new IllegalArgumentException("Invalid file char " + fileChar);
		}
		}

		return file;
	}

	public static int[] algebraicToCoordinates(String square) {
		square = square.toUpperCase().trim();

		if (square.length() == 2) {
			char letter = square.charAt(0);
			char number = square.charAt(1);

			int rank;
			int file;

			switch (letter) {
			case 'A': {
				file = A;
				break;
			}
			case 'B': {
				file = B;
				break;
			}
			case 'C': {
				file = C;
				break;
			}
			case 'D': {
				file = D;
				break;
			}
			case 'E': {
				file = E;
				break;
			}
			case 'F': {
				file = F;
				break;
			}
			case 'G': {
				file = G;
				break;
			}
			case 'H': {
				file = H;
				break;
			}
			default: {
				return null;
			}
			}

			switch (number) {
			case '1': {
				rank = 7;
				break;
			}
			case '2': {
				rank = 6;
				break;
			}
			case '3': {
				rank = 5;
				break;
			}
			case '4': {
				rank = 4;
				break;
			}
			case '5': {
				rank = 3;
				break;
			}
			case '6': {
				rank = 2;
				break;
			}
			case '7': {
				rank = 1;
				break;
			}
			case '8': {
				rank = 0;
				break;
			}
			default: {
				return null;
			}
			}

			return new int[] { rank, file };
		}
		return null;
	}

	public static boolean equals(int[] coordinates1, int[] coordinates2) {
		assertInBounds(coordinates1);
		assertInBounds(coordinates2);
		return coordinates1[0] == coordinates2[0]
				&& coordinates1[1] == coordinates2[1];
	}

	public static void assertValidFile(int file) {
		if (file < 0 || file > 7) {
			throw new IllegalArgumentException("Invalid file: " + file);
		}
	}

	public static void assertValidRank(int rank) {
		if (rank < 0 || rank > 7) {
			throw new IllegalArgumentException("Invalid rank: " + rank);
		}

	}

	public static void assertValid(int[] coordinates) {
		assertInBounds(coordinates);

	}

	public static void assertWithoutBounds(int[] coordinates) {
		if (coordinates == null) {
			throw new IllegalArgumentException("coordiantes cant be null");
		} else if (coordinates.length != 2) {
			throw new IllegalArgumentException(
					"coordinates must be 2 in length.");
		}
	}

	public static void assertInBounds(int rank, int file) {
		assertValidRank(rank);
		assertValidFile(file);
	}

	public static void assertInBounds(int[] coordinates) {
		assertWithoutBounds(coordinates);
		assertInBounds(coordinates[0], coordinates[1]);
	}

	public static boolean isInBounds(int startRank, int startFile) {
		return startRank > -1 && startRank < 8 && startFile > -1
				&& startFile < 8;
	}

	public static boolean isInBounds(int[] coordinates) {
		assertWithoutBounds(coordinates);
		return isInBounds(coordinates[0], coordinates[1]);
	}

	/**
	 * Returns rank,file if board is rotated 180 degrees.
	 */
	public static int[] getOpposite(int rank, int file) {
		return new int[] { getOppositeRank(rank), getOppositeFile(file) };
	}

	/**
	 * Returns rank,file if board is rotated 180 degrees.
	 */
	public static int[] getOpposite(int[] coordinates) {
		assertInBounds(coordinates);
		return getOpposite(coordinates[0], coordinates[1]);
	}

	public static int getOppositeRank(int rank) {
		assertValidRank(rank);
		return Math.abs(7 - rank);
	}

	public static int getOppositeFile(int file) {
		assertValidFile(file);
		return Math.abs(7 - file);
	}

	public static String getDefaultCoordinates(int[] coordinates) {
		assertWithoutBounds(coordinates);
		if (isInBounds(coordinates))
			return DEFAULT_MOVE_ENCODER.encode(coordinates);
		else
			return coordinates[0] + "/" + coordinates[1];
	}

	public static String getDefaultCoordinates(int rank, int file) {
		if (isInBounds(rank, file))
			return DEFAULT_MOVE_ENCODER.encode(rank, file);
		else
			return rank + "/" + file;

	}

	public static String getDefaultMove(Move move) {
		return DEFAULT_MOVE_ENCODER.encode(move, null);
	}

	public static boolean isWhiteSquare(int[] coordinates) {
		boolean result = coordinates[0] % 2 == 0;
		return coordinates[1] % 2 == 0 ? result : !result;
	}
}