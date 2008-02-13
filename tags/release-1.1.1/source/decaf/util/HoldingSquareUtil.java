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

import decaf.moveengine.Piece;

public class HoldingSquareUtil implements HoldingSquares, Piece {

	public HoldingSquareUtil() {
	}

	public static boolean isLightHoldingSquare(int i) {
		return i == 100 || i == 101 || i == 102 || i == 103 || i == 104;
	}

	public static boolean isDarkHoldingSquare(int i) {
		return i == 201 || i == 202 || i == 203 || i == 204 || i == 205;
	}

	public static boolean isHoldingSquare(int i) {
		return i == 100 || i == 101 || i == 102 || i == 103 || i == 104
				|| i == 201 || i == 202 || i == 203 || i == 204 || i == 205;
	}

	public static int getHoldingSquareForPiece(int i) {
		char c = '\uFFFF';
		switch (i) {
		case 1: // '\001'
			c = 'd';
			break;

		case 2: // '\002'
			c = 'e';
			break;

		case 3: // '\003'
			c = 'f';
			break;

		case 4: // '\004'
			c = 'g';
			break;

		case 5: // '\005'
			c = 'h';
			break;

		case 513:
			c = '\311';
			break;

		case 514:
			c = '\312';
			break;

		case 515:
			c = '\313';
			break;

		case 516:
			c = '\314';
			break;

		case 517:
			c = 'h';
			break;

		default:
			throw new RuntimeException(
					"Piece does not represent holding square : " + i);
		}
		return c;
	}
}