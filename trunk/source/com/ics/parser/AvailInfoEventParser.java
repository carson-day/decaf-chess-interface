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

package decaf.com.ics.parser;

// howard james a md

// 11:30
// 1100 cambridge square alpharetta 30004
// office park on alpharetta hw
// call aetna make sure i have out patient hb met

import decaf.com.inboundevent.InboundEvent;

public class AvailInfoEventParser extends InboundEventParser {
	public AvailInfoEventParser(Object source) {
		super(source, false, false);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		/*
		 * if (text.endsWith(IDENTIFIER)) { StringTokenizer stringtokenizer =
		 * new StringTokenizer(text, " \r\n"); if
		 * (stringtokenizer.hasMoreTokens()) { String s1 =
		 * ParserUtil.removeTitles(stringtokenizer .nextToken()); if
		 * (stringtokenizer.hasMoreTokens() &&
		 * stringtokenizer.nextToken().equals("Blitz")) { int i =
		 * stringtokenizer.countTokens(); if (i == 12) { int j = 0; int k = 0;
		 * int l = 0; int i1 = 0; int j1 = 0; try { String s2 =
		 * stringtokenizer.nextToken(); stringtokenizer.nextToken(); String s3 =
		 * stringtokenizer.nextToken(); stringtokenizer.nextToken(); String s4 =
		 * stringtokenizer.nextToken(); String s5 = stringtokenizer.nextToken();
		 * String s6 = stringtokenizer.nextToken(); s2 = s2.substring(1,
		 * s2.length() - 2); s3 = s3.substring(1, s3.length() - 2); s4 =
		 * s4.substring(1, s4.length() - 2); s5 = s5.substring(6, s5.length() -
		 * 2); s6 = s6.substring(4, s6.length() - 1); if (!s2.equals("----")) j =
		 * Integer.parseInt(s2); if (!s3.equals("----")) k =
		 * Integer.parseInt(s3); if (!s4.equals("----")) l =
		 * Integer.parseInt(s4); if (!s5.equals("----")) i1 =
		 * Integer.parseInt(s5); if (!s6.equals("----")) j1 =
		 * Integer.parseInt(s6); return new AvailInfoEvent(getSource(), "" +
		 * id++, text, s1, j, k, l, i1, j1); } catch (Exception exception) {
		 * exception.printStackTrace(); } } } } } return null;
		 */
		return null;
	}

	private static final String IDENTIFIER = "is now available for matches.";

	private static int id;
}