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
package decaf.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import decaf.gui.widgets.ChessSet;
import decaf.moveengine.Piece;

public class PromotionDialog extends JDialog {

	private static final ImageIcon WHITE_KNIGHT = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.WKNIGHT.BMP"));

	private static final ImageIcon WHITE_BISHOP = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.WBISHOP.BMP"));

	private static final ImageIcon WHITE_QUEEN = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.WQUEEN.BMP"));

	private static final ImageIcon WHITE_ROOK = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.WROOK.BMP"));

	private static final ImageIcon BLACK_KNIGHT = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.BKNIGHT.BMP"));

	private static final ImageIcon BLACK_BISHOP = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.BBISHOP.BMP"));

	private static final ImageIcon BLACK_QUEEN = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.BQUEEN.BMP"));

	private static final ImageIcon BLACK_ROOK = new ImageIcon(ChessSet
			.getChessPieceImage("SET.BOOK.BROOK.BMP"));

	public static int showPromotionDialog(JFrame jframe, boolean isWhitesMove,
			Color background) {

		PromotionDialog promotiondialog = new PromotionDialog(jframe,
				isWhitesMove, background);
		promotiondialog.setVisible(true);
		return promotiondialog.pieceSelected;
	}

	private int pieceSelected = Piece.EMPTY;

	private boolean isWhitePromotion;

	public PromotionDialog(JFrame jframe, final boolean isWhitePromotion,
			Color background) {
		super(jframe, "Promote to piece:", true);
		setDefaultCloseOperation(2);
		this.isWhitePromotion = isWhitePromotion;
		setBackground(background);

		Container container = getContentPane();
		container.setLayout(new GridLayout(1, 4));
		final JButton queenButton = new JButton(isWhitePromotion ? WHITE_QUEEN
				: BLACK_QUEEN);
		final JButton bishopButton = new JButton(
				isWhitePromotion ? WHITE_BISHOP : BLACK_BISHOP);
		final JButton rookButton = new JButton(isWhitePromotion ? WHITE_ROOK
				: BLACK_ROOK);
		final JButton knightButton = new JButton(
				isWhitePromotion ? WHITE_KNIGHT : BLACK_KNIGHT);

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == queenButton) {
					pieceSelected = isWhitePromotion ? Piece.WQ : Piece.BQ;
				} else if (e.getSource() == bishopButton) {
					pieceSelected = isWhitePromotion ? Piece.WB : Piece.BB;
				} else if (e.getSource() == rookButton) {
					pieceSelected = isWhitePromotion ? Piece.WR : Piece.BR;
				} else if (e.getSource() == knightButton) {
					pieceSelected = isWhitePromotion ? Piece.WN : Piece.BN;
				}
				setVisible(false);
			}
		};

		queenButton.addActionListener(listener);
		bishopButton.addActionListener(listener);
		rookButton.addActionListener(listener);
		knightButton.addActionListener(listener);

		container.add(queenButton);
		container.add(knightButton);
		container.add(bishopButton);
		container.add(rookButton);

		pack();
	}
}