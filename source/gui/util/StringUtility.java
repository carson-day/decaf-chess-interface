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

package decaf.gui.util;

public class StringUtility {

	private StringUtility() {
	}

	public static String intArrayToString(int[] intArray) {
		String result = "";
		for (int i = 0; i < intArray.length; i++) {
			result += (i == 0 ? "" : ",") + intArray[i];
		}
		return result;
	}

	public static void setStringAt(StringBuffer stringbuffer, int i, String s) {
		for (int j = 0; j < s.length(); j++)
			stringbuffer.setCharAt(i + j, s.charAt(j));

	}

	public static int numOccurancesOfString(String s, String s1) {
		int i = 0;
		if (!s1.equals(""))
			while (s.length() > 0) {
				int j = s.indexOf(s1);
				if (j == -1) {
					s = "";
				} else {
					i++;
					s = s.substring(j + s1.length(), s.length());
				}
			}
		return i;
	}

	public static String replaceStringWithString(String source, String find,
			String replace) {
		String s3 = "";
		String s4 = "";
		int i = source.indexOf(find);
		if (i != -1) {
			String s5 = source.substring(0, i) + replace;
			String s6 = source.substring(i + find.length(), source.length());
			s3 = s5 + replaceStringWithString(s6, find, replace);
		}
		return s3.equals("") ? source : s3;
	}

	public static String padCharsToLeft(String s, char c, int i) {
		int j = i - s.length();
		String s1 = s;
		for (; j > 0; j--)
			s1 = c + s1;

		return s1;
	}

	public static String padCharsToRight(String s, char c, int i) {
		int j = i - s.length();
		String s1 = s;
		for (; j > 0; j--)
			s1 = s1 + c;

		return s1;
	}

	public static String toLeftPaddedFixedLenNumber(String s, int i) {
		return padCharsToLeft(s, '0', i);
	}

	public static String toRightPaddedFixedLenNumber(String s, int i) {
		return padCharsToRight(s, '0', i);
	}

	public static String toLeftPaddedFixedLenString(String s, int i) {
		return padCharsToLeft(s, ' ', i);
	}

	public static String toRightPaddedFixedLenString(String s, int i) {
		return padCharsToRight(s, ' ', i);
	}

	public static boolean isNumeric(String s) {
		boolean flag = true;
		int i = s.length();
		for (int j = 0; j < i && flag; j++) {
			char c = s.charAt(j);
			if (c < '0' || c > '9')
				flag = false;
		}

		return flag;
	}

	public static boolean isAlphabetChar(char c) {
		char c1 = c;
		return c1 >= 'A' && c1 <= 'Z' || c1 >= 'a' && c1 <= 'z';
	}

	public static boolean isAlaphabetString(String s) {
		boolean flag = true;
		for (int i = 0; i < s.length() && flag; i++)
			if (!isAlphabetChar(s.charAt(i)))
				flag = false;

		return flag;
	}

	public static boolean containsWhiteSpace(String s) {
		return s.indexOf('\t') != -1 || s.indexOf('\n') != -1
				|| s.indexOf('\r') != -1;
	}
}