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

import java.awt.Color;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

public class BorderUtil {
	public static final int EMPTY_BORDER = 0;

	public static final int BLACK_LINE_SQUARE_BORDER = 1;

	public static final int BLACK_LINE_THIN_SQUARE_BORDER = 2;

	public static final int BLACK_LINE_ROUNDED_BORDER = 3;

	public static final int BLACK_LINE_THIN_ROUNDED_BORDER = 4;

	public static final int WHITE_LINE_SQUARE_BORDER = 5;

	public static final int WHITE_LINE_THIN_SQUARE_BORDER = 6;

	public static final int WHITE_LINE_ROUNDED_BORDER = 7;

	public static final int WHITE_LINE_THIN_ROUNDED_BORDER = 8;

	public static final int RAISED_BEVEL_BORDER = 9;

	public static final int LOWERED_BEVEL_BORDER = 10;

	public static final int RAISED_ETCHED_BORDER = 11;

	public static final int LOWERED_ETCHED_BORDER = 12;

	public static Border intToBorder(int borderConstant) {
		switch (borderConstant) {
		case EMPTY_BORDER: {
			return new EmptyBorder(0, 0, 0, 0);
		}
		case BLACK_LINE_SQUARE_BORDER: {
			return new LineBorder(Color.black, 3, false);
		}
		case BLACK_LINE_THIN_SQUARE_BORDER: {
			return new LineBorder(Color.black, 1, false);
		}

		case BLACK_LINE_ROUNDED_BORDER: {
			return new LineBorder(Color.black, 3, true);
		}
		case BLACK_LINE_THIN_ROUNDED_BORDER: {
			return new LineBorder(Color.black, 1, false);
		}

		case WHITE_LINE_SQUARE_BORDER: {
			return new LineBorder(Color.white, 3, false);
		}
		case WHITE_LINE_THIN_SQUARE_BORDER: {
			return new LineBorder(Color.white, 1, false);
		}

		case WHITE_LINE_ROUNDED_BORDER: {
			return new LineBorder(Color.white, 3, true);
		}
		case WHITE_LINE_THIN_ROUNDED_BORDER: {
			return new LineBorder(Color.white, 1, false);
		}

		case RAISED_BEVEL_BORDER: {
			return new BevelBorder(BevelBorder.RAISED);
		}
		case LOWERED_BEVEL_BORDER: {
			return new BevelBorder(BevelBorder.LOWERED);
		}
		case RAISED_ETCHED_BORDER: {
			return new EtchedBorder(EtchedBorder.RAISED);
		}
		case LOWERED_ETCHED_BORDER: {
			return new EtchedBorder(EtchedBorder.LOWERED);
		}
		default: {
			throw new IllegalArgumentException("Invalid border constant:"
					+ borderConstant);
		}
		}
	}

}
