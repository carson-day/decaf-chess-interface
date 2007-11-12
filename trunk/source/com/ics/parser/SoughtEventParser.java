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

import java.util.StringTokenizer;

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.inform.SeekAd;

public class SoughtEventParser extends InboundEventParser {
	public SoughtEventParser(Object source) {
		super(source, false, false);
	}

	/**
	 * 2 1715 andyc 3 0 rated blitz 0-9999 m 3 ++++ smokeybear 10 12 unrated
	 * standard 0-9999 mf 6 1858 StAndersen(C) 5 0 rated blitz 0-9999 f 15 1954
	 * StAndersen(C) 15 0 rated standard 0-9999 f 18 1982 StAndersen(C) 2 0
	 * rated lightning 0-9999 f 19 1521 sivakumar 15 0 rated standard 1500-9999
	 * f 20 2465 IFDCrafty(C) 15 0 rated standard 0-9999 f 21 1786 Chapablanca 3
	 * 3 rated blitz 0-9999 m 24 1259 gzimmIII 5 0 rated blitz [white] 0-9999 25
	 * 2249 muriel(C) 1 0 rated wild/2 0-9999 f 26 1919 GriffyJr(C) 15 0 rated
	 * standard 0-9999 mf 27 1805 GriffyJr(C) 5 0 rated blitz 0-9999 mf 28 ++++
	 * GuestHMKW 2 12 unrated blitz 0-9999 f 29 1625 TopoJeejo 3 8 rated
	 * crazyhouse 0-9999 30 1947 TheNeo(C) 1 0 rated lightning 0-9999 32 1756
	 * TheNeo(C) 5 0 rated blitz 0-9999 36 2103 muriel(C) 1 0 rated lightning
	 * 0-9999 f 37 2075 GriffyJr(C) 1 0 rated lightning 0-9999 mf 39 2103
	 * muriel(C) 2 1 rated lightning 0-9999 f 40 1958 TheNeo(C) 15 0 rated
	 * standard 0-9999 42 1883 nondiscrete 5 5 unrated suicide 0-9999 43 2467
	 * Sly(C) 1 0 rated lightning 0-9999 mf 44 2314 Sly(C) 3 0 rated blitz
	 * 0-9999 mf 46 1869 NovagSapphire(C) 5 0 rated blitz 0-9999 f 47 ++++
	 * LizzieRose 2 12 unrated suicide 0-9999 m 48 ++++ LizzieRose 3 12 unrated
	 * wild/8 0-9999 m 49 2321 CraftyNovus(C) 60 0 rated standard 0-9999 mf 50
	 * 2314 Sly(C) 5 0 rated blitz 0-9999 mf 52 1869 NovagSapphire(C) 10 0 rated
	 * blitz 0-9999 f 53 2156 NovagSapphire(C) 15 0 rated standard 0-9999 f 54
	 * 1580 garfio 2 12 rated blitz 0-9999 58 1631 igor 5 0 rated blitz 0-9999 f
	 * 59 1625 TopoJeejo 2 12 rated crazyhouse 0-9999 61 1914 Chapablanca 3 3
	 * rated crazyhouse 0-9999 m 63 2340 blik(C) 2 0 rated lightning 0-9999 mf
	 * 66 2184 blik(C) 5 0 rated blitz 0-9999 mf 67 1928 Chapablanca 3 3 rated
	 * wild/fr 0-9999 m 37 ads displayed.
	 */

	/**
	 * Usage: sought [all]
	 * 
	 * The "sought" command can be used in two ways: (a) typing "sought all"
	 * will display all current ads including your own; (b) typing "sought"
	 * alone will display only those current ads for which you are eligible
	 * based on any formula you might have (default). An example output is as
	 * follows:
	 * 
	 * 0 1900 Hawk blitz 5 0 rated 1800-2000 f 1 1700 Friar wild7 2 12 unrated
	 * [white] 0-9999 4 1500 loon standard 5 0 unrated 0-9999 m
	 * 
	 * The various columns have this information:
	 * 
	 * Ad index number Player's rating Player's handle Type of chess match Time
	 * at start Increment per move Rated/unrated Color (if specified) Rating
	 * range Auto start/manual start and whether formula will be checked
	 * 
	 * See also: formula match play seek unseek
	 * 
	 * [Last modified: February 5, 1998 -- Friar]
	 */

	private SeekAd parseLine(String line) {
		StringTokenizer tok = new StringTokenizer(line, " ");
		int gameId = Integer.parseInt(tok.nextToken());
		String rating = tok.nextToken();
		String playersName = tok.nextToken();
		int time = Integer.parseInt(tok.nextToken());
		int inc = Integer.parseInt(tok.nextToken());
		boolean isRated = tok.nextToken().equals(RATED);
		String description = tok.nextToken();
		String nextToken = tok.nextToken();
		String rangeString = null;
		boolean isWhiteSpecified = false;
		boolean isBlackSpecified = false;
		boolean isManual = false;
		boolean isFormula = false;
		int lowRange = 0;
		int highRange = 0;

		if (nextToken.equals(WHITE)) {
			isWhiteSpecified = true;
			rangeString = tok.nextToken();
		} else if (nextToken.equals(BLACK)) {
			isBlackSpecified = true;
			rangeString = tok.nextToken();
		} else {
			rangeString = nextToken;
		}
		String flagsString = tok.hasMoreTokens() ? tok.nextToken() : null;

		if (flagsString != null) {
			if (flagsString.indexOf(MANUAL) != -1) {
				isManual = true;
			}
			if (flagsString.indexOf(FORMULA) != -1) {
				isFormula = true;
			}
		}

		StringTokenizer rangeTok = new StringTokenizer(rangeString, "-");
		lowRange = Integer.parseInt(rangeTok.nextToken());
		highRange = Integer.parseInt(rangeTok.nextToken());

		return new SeekAd(gameId, rating, playersName, time, inc, isRated,
				description, isWhiteSpecified, isBlackSpecified, lowRange,
				highRange, isManual, isFormula);
	}

	public InboundEvent parse(String text) {
		/*
		 * if (text.indexOf(END_IDENTIFIER) != -1 ||
		 * text.indexOf(END_IDENTIFIER2) != -1) { StringTokenizer
		 * newLinedTokenizer = new StringTokenizer(text, "\r\n"); LinkedList
		 * list = new LinkedList(); String currentLine = null;
		 * 
		 * do { currentLine = newLinedTokenizer.nextToken().trim(); if
		 * (currentLine.indexOf(END_IDENTIFIER) == -1 &&
		 * currentLine.indexOf(END_IDENTIFIER2) == -1) {
		 * list.add(parseLine(currentLine)); } else { break; } } while
		 * (newLinedTokenizer.hasMoreTokens()); return new
		 * SoughtEvent(getSource(), "" + id++, text, list); } else { return
		 * null; }
		 */
		return null;
	}

	private static final String MANUAL = "m";

	private static final String FORMULA = "f";

	private static final String RATED = "rated";

	private static final String WHITE = "[white]";

	private static final String BLACK = "[black]";

	private static final String END_IDENTIFIER = "ads displayed.";

	private static final String END_IDENTIFIER2 = "ad displayed.";

	private static int id;
}