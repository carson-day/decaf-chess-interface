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

package decaf.gui.widgets;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import decaf.gui.util.PieceUtil;
import decaf.moveengine.Piece;


// Referenced classes of package caffeine.gui.widgets:
// Skinnable, Skin

public abstract class ChessPiece extends JComponent implements Piece {
	private Dimension sizeConstraint;

	public ChessPiece(int piece) {
		setOpaque(false);
		this.piece = piece;
		PieceUtil.assertValidAndNotEmpty(piece);
		setBorder(new EmptyBorder(0, 0, 0, 0));
	}

	/**
	 * Sets a constraint the width/height of the piece in the container. This is
	 * optional if its not set the piece should try to fill all allocated space.
	 */
	public void setSizeConstraint(Dimension sizeConstraint) {
		this.sizeConstraint = sizeConstraint;
	}

	/**
	 * Gets a constraint the width/height of the piece in the container. This is
	 * optional if its null the piece should try to fill all allocated space.
	 */
	public Dimension getSizeConstraint() {
		return sizeConstraint;
	}

	public boolean isOpaque() {
		return false;
	}

	public boolean isTransparent() {
		return isTransparent;
	}

	public void setTransparent(boolean flag) {
		isTransparent = flag;
	}

	public abstract ChessPiece cloneChessPiece();

	public int getType() {
		return piece;
	}

	public boolean isLightPiece() {
		return PieceUtil.isWhitePiece(piece);
	}

	private int piece;

	private boolean isTransparent;
}