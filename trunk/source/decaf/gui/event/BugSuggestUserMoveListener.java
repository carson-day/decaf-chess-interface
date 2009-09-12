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
package decaf.gui.event;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import decaf.gui.ChessAreaControllerBase;
import decaf.gui.widgets.ChessBoardSquare;
import decaf.gui.widgets.Disposable;
import decaf.moveengine.Coordinates;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;
import decaf.util.CoordinatesUtil;
import decaf.util.PieceUtil;

/**
 * The user move suggestion listener for bughouse games.
 */
public class BugSuggestUserMoveListener implements UserMoveInputListener,
		Disposable {
	private ChessAreaControllerBase controller;

	public BugSuggestUserMoveListener(ChessAreaControllerBase controller) {
		this.controller = controller;
	}

	public void dispose() {
		controller = null;
	}

	/**
	 * @return Returns the controller.
	 */
	public ChessAreaControllerBase getController() {
		return controller;
	}

	private String getSuggestMovePrefix() {
		return "I suggest ";
	}

	private String getWatchoutMovePrefix() {
		return "Watch out for ";
	}

	private String getWatchoutSquarePrefix() {
		return "Watch ";
	}

	private boolean isPromotion(UserMoveEvent event) {
		if (event.getStartCoordinates() == null) // is piece drop
		{
			return false;
		} else {
			Position partnersPosition = controller.getPartnersPosition();
			return (controller.isPartnerWhite()
					&& event.getEndCoordinates()[0] == Coordinates.RANK_8 && partnersPosition
					.get(event.getStartCoordinates()) == Piece.WHITE_PAWN)
					|| (!controller.isPartnerWhite()
							&& event.getEndCoordinates()[0] == Coordinates.RANK_1 && partnersPosition
							.get(event.getStartCoordinates()) == Piece.BLACK_PAWN);
		}
	}

	/**
	 * @param controller
	 *            The controller to set.
	 */
	public void setController(ChessAreaControllerBase controller) {
		this.controller = controller;
	}

	public void userClicked(ChessBoardSquare square) {
	}

	public void userMadeIncompleteMove(UserIncompleteMoveEvent event) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecaffeine.gui.event.GUIMoveListener#moveOccured(caffeine.gui.event.
	 * GUIMoveEvent)
	 */
	public boolean userMoved(UserMoveEvent event) {
		synchronized (controller) {
			String moveString = null;

			if (isPromotion(event)) {
				int promotedPiece = controller.showPromotionDialog(controller
						.isPartnerWhite());
				if (promotedPiece == Piece.EMPTY) {
					controller.playIllegalMoveSound();
					return false;
				}

				boolean isPartnersMove = (event.getEndCoordinates()[0] == Coordinates.RANK_8 && controller
						.isPartnerWhite())
						|| (event.getEndCoordinates()[0] == Coordinates.RANK_1 && controller
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

				int startPiece = controller.getPartnersPosition().get(
						event.getStartCoordinates());
				if (startPiece != Piece.EMPTY) {
					moveString = (isPartnersMove ? getSuggestMovePrefix()
							: getWatchoutMovePrefix())
							+ PieceUtil.getDefaultPiece(startPiece)
							+ CoordinatesUtil.getDefaultCoordinates(event
									.getEndCoordinates()[0], event
									.getEndCoordinates()[1]);
				} else {
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
			}

			controller.givePartnerAdvice(moveString);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * caffeine.gui.event.GUIMoveListener#rightClickOccured(caffeine.gui.widgets
	 * .ChessBoardSquare)
	 */
	public void userRightClicked(UserRightClickSquareEvent event) {
		final ChessBoardSquare source = event.getSource();
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
			if (Coordinates.RANK_1 != source.getRank()
					&& Coordinates.RANK_8 != source.getRank()) {
				final String pawnSuggest = suggestPrefix + "p@" + suffix;
				popupMenu.add(new AbstractAction(pawnSuggest) {
					public void actionPerformed(ActionEvent event) {
						controller.givePartnerAdvice(pawnSuggest);
					}
				});
			}
			final String knightSuggest = suggestPrefix + "n@" + suffix;
			popupMenu.add(new AbstractAction(knightSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(knightSuggest);
				}
			});
			final String bishopSuggest = suggestPrefix + "b@" + suffix;
			popupMenu.add(new AbstractAction(bishopSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(bishopSuggest);
				}
			});
			final String rookSuggest = suggestPrefix + "r@" + suffix;
			popupMenu.add(new AbstractAction(rookSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(rookSuggest);
				}
			});
			final String queenSuggest = suggestPrefix + "q@" + suffix;
			popupMenu.add(new AbstractAction(queenSuggest) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(queenSuggest);
				}
			});
			popupMenu.addSeparator();
			if (Coordinates.RANK_1 != source.getRank()
					&& Coordinates.RANK_8 != source.getRank()) {
				final String pawnWatch = watchOutPrefix + "p@" + suffix;
				popupMenu.add(new AbstractAction(pawnWatch) {
					public void actionPerformed(ActionEvent event) {
						controller.givePartnerAdvice(pawnWatch);
					}
				});
			}
			final String knightWatch = watchOutPrefix + "n@" + suffix;
			popupMenu.add(new AbstractAction(knightWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(knightWatch);
				}
			});
			final String bishopWatch = watchOutPrefix + "b@" + suffix;
			popupMenu.add(new AbstractAction(bishopWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(bishopWatch);
				}
			});
			final String rookWatch = watchOutPrefix + "r@" + suffix;
			popupMenu.add(new AbstractAction(rookWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(rookWatch);
				}
			});
			final String queenWatch = watchOutPrefix + "q@" + suffix;
			popupMenu.add(new AbstractAction(queenWatch) {
				public void actionPerformed(ActionEvent event) {
					controller.givePartnerAdvice(queenWatch);
				}
			});
			popupMenu.show(source, 10, 10);
		}

	}
}