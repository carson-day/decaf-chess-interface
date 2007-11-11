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

package decaf.com.inboundevent.game;

public interface GameTypes {
	public static final int BLITZ = 0;

	public static final int LIGHTNING = 1;

	public static final int WILD = 2;

	public static final int STANDARD = 3;

	public static final int SUICIDE = 4;

	public static final int BUGHOUSE = 5;

	public static final int CRAZYHOUSE = 6;

	public static final int EXAMINING = 7;

	public static final int UNTIMED = 8;

	public static final int ATOMIC = 9;

	public static final int LOSERS = 10;
}