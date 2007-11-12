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
import decaf.com.inboundevent.game.DroppableHoldingsChangedEvent;
import decaf.moveengine.Piece;

public class DroppableHoldingsChangedEventParser extends InboundEventParser {
	public DroppableHoldingsChangedEventParser(Object source) {
		super(source, true, false);
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public InboundEvent parse(String text) {
		return parse(true, text);
	}

	public InboundEvent parseInsideStyle12(String text) {
		return parse(false, text);
	}

	private InboundEvent parse(boolean onlyParseIfNoStyle12, String text) {
		// only handle droppable holdings changed events that ARE NOT OCCURING
		// in style 12 events.
		// this will prevent duplicates
		if (text.length() < 600) {
			int dropStartIndex = text.indexOf(DROP_START_TAG);
			if (dropStartIndex != -1) {
				if (onlyParseIfNoStyle12) {
					int style12Index = text.indexOf(STYLE_12_TAG);
					if (style12Index != -1 && style12Index < dropStartIndex) {
						return null;
					}
				}
				text = text.substring(dropStartIndex);
				StringTokenizer stringtokenizer = new StringTokenizer(text,
						" {}><-\r\n");
				stringtokenizer.nextToken();
				stringtokenizer.nextToken();
				int j = Integer.parseInt(stringtokenizer.nextToken());
				stringtokenizer.nextToken();
				String s1 = stringtokenizer.nextToken();
				int ai[] = lightPiecesToIntArray(s1.substring(1,
						s1.length() - 1));
				stringtokenizer.nextToken();
				String s2 = stringtokenizer.nextToken();
				int ai1[] = darkPiecesToIntArray(s2.substring(1,
						s2.length() - 1));
				int k = text.indexOf("<12>");
				return new DroppableHoldingsChangedEvent(getSource(),
						"" + id++, k != -1 ? text.substring(0, k) : text, j,
						ai1, ai);
			} else {
				return null;
			}
		}
		return null;
	}

	private static int[] lightPiecesToIntArray(String s) {
		int ai[] = new int[s.length()];
		for (int i = 0; i < s.length(); i++) {
			int piece = -1;
			switch (s.charAt(i)) {
			case 'P': // 'P'
			case 'p': // 'p'
				piece = Piece.WP;
				break;

			case 'N': // 'N'
			case 'n': // 'n'
				piece = Piece.WN;
				break;

			case 'B': // 'B'
			case 'b': // 'b'
				piece = Piece.WB;
				break;

			case 'R': // 'R'
			case 'r': // 'r'
				piece = Piece.WR;
				break;

			case 'Q': // 'Q'
			case 'q': // 'q'
				piece = Piece.WQ;
				break;

			default:
				throw new RuntimeException("Invalid piece " + s.charAt(i));
			}
			ai[i] = piece;
		}

		return ai;
	}

	private static int[] darkPiecesToIntArray(String s) {
		int ai[] = new int[s.length()];
		for (int i = 0; i < s.length(); i++) {
			int piece = 0;
			switch (s.charAt(i)) {
			case 'P': // 'P'
			case 'p': // 'p'
				piece = Piece.BP;
				break;

			case 'N': // 'N'
			case 'n': // 'n'
				piece = Piece.BN;
				break;

			case 'B': // 'B'
			case 'b': // 'b'
				piece = Piece.BB;
				break;

			case 'R': // 'R'
			case 'r': // 'R'
				piece = Piece.BR;
				break;
			case 'Q': // 'Q'
			case 'q': // 'q'
				piece = Piece.BQ;
				break;

			default:
				throw new RuntimeException("Invalid piece " + s.charAt(i));
			}
			ai[i] = piece;
		}

		return ai;
	}

	private static int id;

	private static final String STYLE_12_TAG = "<12>";

	private static final String DROP_START_TAG = "<b1>";
}