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
package decaf.gui.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import decaf.gui.ChessAreaControllerBase;
import decaf.gui.Disposable;
import decaf.gui.util.CoordinatesUtil;
import decaf.gui.util.PieceUtil;
import decaf.gui.widgets.ChessBoardSquare;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;

// Referenced classes of package caffeine.gui:
// SoundManager, ControllerBase, ChessArea, PromotionDialog

public class SuggestBugMoveGUIMoveListener implements UserActionListener,
		Disposable {
	private ChessAreaControllerBase controller;

	public SuggestBugMoveGUIMoveListener(ChessAreaControllerBase controller) {
		this.controller = controller;
	}

	public void dispose() {
		controller = null;
	}

	public void incompleteMoveOccured() {
	}

	public void pieceClicked() {

	}

	/**
	 * @return Returns the controller.
	 */
	public ChessAreaControllerBase getController() {
		return controller;
	}

	/**
	 * @param controller
	 *            The controller to set.
	 */
	public void setController(ChessAreaControllerBase controller) {
		this.controller = controller;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see caffeine.gui.event.GUIMoveListener#isValidMoveStartSquare(int[])
	 */
	public boolean isValidMoveStartSquare(int[] startCoordinates) {
		// TODO Auto-generated method stub
		return controller.isPlaying() ? true : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see caffeine.gui.event.GUIMoveListener#rightClickOccured(caffeine.gui.widgets.ChessBoardSquare)
	 */
	public void rightClickOccured(ChessBoardSquare source, boolean isEmpty) {
		if (controller.isActive() && !source.isDropSquare()) {
			JPopupMenu popupMenu = new JPopupMenu();
			String suggestPrefix = getSuggestMovePrefix();
			String watchSquarePrefix = getWatchoutSquarePrefix();
			String watchOutPrefix = getWatchoutMovePrefix();
			String suffix = CoordinatesUtil.getDefaultCoordinates(source
					.getCoordinates());

			final String watchSquare = watchSquarePrefix + suffix;
			popupMenu.add(new AbstractAction(watchSquare) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(watchSquare);
				}
			});
			popupMenu.addSeparator();
			if (CoordinatesUtil.RANK_1 != source.getRank()
					&& CoordinatesUtil.RANK_8 != source.getRank()) {
				final String pawnSuggest = suggestPrefix + "Pawn at " + suffix;
				popupMenu.add(new AbstractAction(pawnSuggest) {
					public void actionPerformed(ActionEvent event) {
						controller.givePartnerAdvice(pawnSuggest);
					}
				});
			}
			final String knightSuggest = suggestPrefix + "Knight at " + suffix;
			popupMenu.add(new AbstractAction(knightSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(knightSuggest);
				}
			});
			final String bishopSuggest = suggestPrefix + "Bishop at " + suffix;
			popupMenu.add(new AbstractAction(bishopSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(bishopSuggest);
				}
			});
			final String rookSuggest = suggestPrefix + "Rook at " + suffix;
			popupMenu.add(new AbstractAction(rookSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(rookSuggest);
				}
			});
			final String queenSuggest = suggestPrefix + "Queen at " + suffix;
			popupMenu.add(new AbstractAction(queenSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(queenSuggest);
				}
			});
			popupMenu.addSeparator();
			if (CoordinatesUtil.RANK_1 != source.getRank()
					&& CoordinatesUtil.RANK_8 != source.getRank()) {
				final String pawnWatch = watchOutPrefix + "Pawn at " + suffix;
				popupMenu.add(new AbstractAction(pawnWatch) {
					public void actionPerformed(ActionEvent event) {
						controller.givePartnerAdvice(pawnWatch);
					}
				});
			}
			final String knightWatch = watchOutPrefix + "Knight at " + suffix;
			popupMenu.add(new AbstractAction(knightWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(knightWatch);
				}
			});
			final String bishopWatch = watchOutPrefix + "Bishop at " + suffix;
			popupMenu.add(new AbstractAction(bishopWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(bishopWatch);
				}
			});
			final String rookWatch = watchOutPrefix + "Rook at " + suffix;
			popupMenu.add(new AbstractAction(rookWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(rookWatch);
				}
			});
			final String queenWatch = watchOutPrefix + "Queen at " + suffix;
			popupMenu.add(new AbstractAction(queenWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(queenWatch);
				}
			});
			popupMenu.show(source, 10, 10);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see caffeine.gui.event.GUIMoveListener#moveOccured(caffeine.gui.event.GUIMoveEvent)
	 */
	public void moveOccured(UserActionEvent event) {
		synchronized (controller) {
			String moveString = null;

			if (isPromotion(event)) {
				int promotedPiece = controller.showPromotionDialog(controller
						.isPartnerWhite());
				if (promotedPiece == Piece.EMPTY) {
					controller.playIllegalMoveSound();
					return;
				}

				boolean isPartnersMove = (event.getEndCoordinates()[0] == CoordinatesUtil.RANK_8 && controller
						.isPartnerWhite())
						|| (event.getEndCoordinates()[0] == CoordinatesUtil.RANK_1 && controller
								.isPartnerWhite());

				moveString = (isPartnersMove ? getSuggestMovePrefix()
						: getWatchoutMovePrefix())
						+ " "
						+ CoordinatesUtil.getDefaultCoordinates(event
								.getEndCoordinates()[0], event
								.getEndCoordinates()[1])
						+ " = "
						+ PieceUtil.getDefaultPiece(promotedPiece);
			} else if (event.getDropPiece() != Piece.EMPTY) {
				boolean isPartnersMove = ((PieceUtil.isWhitePiece(event
						.getDropPiece()) && controller.isPartnerWhite()) || !PieceUtil
						.isWhitePiece(event.getDropPiece())
						&& !controller.isPartnerWhite());

				moveString = (isPartnersMove ? getSuggestMovePrefix()
						: getWatchoutMovePrefix())
						+ PieceUtil.getDefaultPiece(event.getDropPiece())
						+ "@"
						+ CoordinatesUtil.getDefaultCoordinates(event
								.getEndCoordinates());
			} else {

				boolean startSquareIsWhite = PieceUtil
						.isWhitePiece(controller.getPartnersPosition().get(
								event.getStartCoordinates()));
				boolean isPartnersMove = (startSquareIsWhite && controller
						.isPartnerWhite())
						|| (!startSquareIsWhite && !controller.isPartnerWhite());

				moveString = (isPartnersMove ? getSuggestMovePrefix()
						: getWatchoutMovePrefix())
						+ CoordinatesUtil.getDefaultCoordinates(event
								.getStartCoordinates()[0], event
								.getStartCoordinates()[1])
						+ "-"
						+ CoordinatesUtil.getDefaultCoordinates(event
								.getEndCoordinates()[0], event
								.getEndCoordinates()[1]);
			}

			controller.givePartnerAdvice(moveString);
		}
	}

	private String getSuggestMovePrefix() {
		return "I suggest ";
	}

	private String getWatchoutSquarePrefix() {
		return "Watch ";
	}

	private String getWatchoutMovePrefix() {
		return "Watch out for ";
	}

	public boolean isPromotion(UserActionEvent event) {
		Position partnersPosition = controller.getPartnersPosition();
		return (controller.isPartnerWhite()
				&& event.getEndCoordinates()[0] == CoordinatesUtil.RANK_8 && partnersPosition
				.get(event.getStartCoordinates()) == Piece.WHITE_PAWN)
				|| (!controller.isPartnerWhite()
						&& event.getEndCoordinates()[0] == CoordinatesUtil.RANK_1 && partnersPosition
						.get(event.getStartCoordinates()) == Piece.BLACK_PAWN);
	}
}