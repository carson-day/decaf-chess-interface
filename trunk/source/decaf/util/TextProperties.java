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
import java.awt.Font;
import java.io.Serializable;

/**
 * A class which stores properties about text.
 */
public class TextProperties implements Serializable {
	
	private static final long serialVersionUID = 11;
	
	private String fontName;

	private int fontStyle;

	private int fontSize;

	private Color foreground;

	private Color background;

	public TextProperties(Font font, Color foreground, Color background) {
		this.foreground = foreground;
		this.background = background;

		fontName = font.getName();
		fontStyle = font.getStyle();
		fontSize = font.getSize();
	}

	public Color getForeground() {
		return foreground;
	}

	public Color getBackground() {
		return background;
	}

	public Font getFont() {
		return new Font(fontName, fontStyle, fontSize);
	}

	public boolean equals(Object object) {
		if (object instanceof TextProperties) {
			TextProperties comparee = (TextProperties) object;

			return getFont().equals(comparee.getFont())
					&& getBackground().equals(comparee.getBackground())
					&& getForeground().equals(comparee.getForeground());
		} else {
			return false;
		}
	}
}