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

import decaf.event.EventService;
import decaf.gui.ChessAreaControllerBase;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.util.CoordinatesUtil;
import decaf.gui.util.PieceUtil;
import decaf.gui.widgets.ChessBoardSquare;
import decaf.moveengine.IllegalMoveException;
import decaf.moveengine.Move;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;

public class ValidatingPlayerMoveListener implements UserActionListener {
	// private static final Random RANDOM = new SecureRandom();

	private static EventService eventService = EventService.getInstance();

	private ChessAreaControllerBase controller;

	private boolean isValidating;

	private boolean isExamineMode;

	public ValidatingPlayerMoveListener(ChessAreaControllerBase icsController,
			boolean isValidating) {
		eventService = EventService.getInstance();
		this.controller = icsController;
		this.isValidating = isValidating;
	}

	public ValidatingPlayerMoveListener(ChessAreaControllerBase icsController,
			boolean isValidating, boolean isExamineMode) {
		this(icsController, isValidating);
		this.isExamineMode = isExamineMode;
	}

	public void incompleteMoveOccured() {
		controller.clearPremove();
	}

	public void pieceClicked() {

	}

	public boolean isValidating() {
		return isValidating;
	}

	public void dispose() {
		controller = null;
	}

	private boolean isUsersMove() {
		return (controller.getPosition().isWhitesMove() && controller
				.isUserWhite())
				|| (!controller.getPosition().isWhitesMove() && !controller
						.isUserWhite());
	}

	private boolean isUsersMovingHisPieces(UserActionEvent event) {
		Position position = controller.getPosition();
		boolean result = true;

		if (!event.isDrop()) {
			result = (controller.isUserWhite() && PieceUtil
					.isWhitePiece(position.get(event.getStartCoordinates())))
					|| (!controller.isUserWhite() && PieceUtil
							.isBlackPiece(position.get(event
									.getStartCoordinates())));
		}
		return result;
	}

	public boolean isExamineMode() {
		return isExamineMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see caffeine.gui.event.GUIMoveListener#isValidMoveStartSquare(int[])
	 */
	public boolean isValidMoveStartSquare(int[] startCoordinates) {
		// TO DO
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see caffeine.gui.event.GUIMoveListener#rightClickOccured(caffeine.gui.widgets.ChessBoardSquare)
	 */
	public void rightClickOccured(final ChessBoardSquare source, boolean isEmpty) {
		if (isEmpty
				&& !isUsersMove()
				&& controller.isDroppable()
				&& controller.isPlaying()
				&& source.getChessPieceType() == PieceUtil.EMPTY
				&& source.getRank() != -1
				&& controller.getPreferences().getBoardPreferences()
						.getPremoveType() != BoardPreferences.NO_PREMOVE) {
			JPopupMenu popupMenu = new JPopupMenu();

			if (CoordinatesUtil.RANK_1 != source.getRank()
					&& CoordinatesUtil.RANK_8 != source.getRank()) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						"Premove Drop pawn") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isPartnerWhite() ? Piece.WP
										: Piece.BP, source.getCoordinates(),
								controller.isWhitesMove());
						controller.setPremove(dropMove.toString(), new int[] {
								-1, -1 }, source.getCoordinates());
					}
				}));
			}
			popupMenu.add(popupMenu.add(new AbstractAction(
					"Premove Drop knight") {
				public void actionPerformed(ActionEvent event) {
					Move dropMove = new Move(
							controller.isPartnerWhite() ? Piece.WN : Piece.BN,
							source.getCoordinates(), controller.isWhitesMove());
					controller.setPremove(dropMove.toString(), new int[] { -1,
							-1 }, source.getCoordinates());
				}
			}));
			popupMenu.add(popupMenu.add(new AbstractAction(
					"Premove Drop bishop") {
				public void actionPerformed(ActionEvent event) {
					Move dropMove = new Move(
							controller.isPartnerWhite() ? Piece.WB : Piece.BB,
							source.getCoordinates(), controller.isWhitesMove());
					controller.setPremove(dropMove.toString(), new int[] { -1,
							-1 }, source.getCoordinates());
				}
			}));
			popupMenu.add(popupMenu
					.add(new AbstractAction("Premove Drop rook") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(controller
									.isPartnerWhite() ? Piece.WR : Piece.BR,
									source.getCoordinates(), controller
											.isWhitesMove());
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, source
											.getCoordinates());
						}
					}));
			popupMenu.add(popupMenu
					.add(new AbstractAction("Premove Drop queen") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(controller
									.isPartnerWhite() ? Piece.WQ : Piece.BQ,
									source.getCoordinates(), controller
											.isWhitesMove());
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, source
											.getCoordinates());
						}
					}));

			popupMenu.show(source, 10, 10);
		} else if (isEmpty && isUsersMove() && controller.isDroppable()
				&& controller.isPlaying()
				&& source.getChessPieceType() == PieceUtil.EMPTY
				&& source.getRank() != -1) {
			int[] usersPieces = controller.isUserWhite() ? controller
					.getPosition().getWhiteHoldings() : controller
					.getPosition().getBlackHoldings();

			if (usersPieces.length != 0) {
				JPopupMenu popupMenu = new JPopupMenu();

				if (CoordinatesUtil.RANK_1 != source.getRank()
						&& CoordinatesUtil.RANK_8 != source.getRank()
						&& PieceUtil.containsPawn(usersPieces)) {
					popupMenu.add(popupMenu
							.add(new AbstractAction("Drop pawn") {
								public void actionPerformed(ActionEvent event) {
									Move dropMove = new Move(controller
											.isUserWhite() ? Piece.WP
											: Piece.BP,
											source.getCoordinates(), controller
													.isWhitesMove());
									try {
										controller.makeMove(dropMove,
												controller.getPosition()
														.makeMove(dropMove));
									} catch (IllegalMoveException ime) {
										ime.printStackTrace();
										controller.handleInvalidMove(dropMove);
									}
								}
							}));
				}
				if (PieceUtil.containsKnight(usersPieces)) {
					popupMenu.add(popupMenu.add(new AbstractAction(
							"Drop knight") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(
									controller.isUserWhite() ? Piece.WN
											: Piece.BN,
									source.getCoordinates(), controller
											.isWhitesMove());
							try {
								controller.makeMove(dropMove, controller
										.getPosition().makeMove(dropMove));
							} catch (IllegalMoveException ime) {
								ime.printStackTrace();
								controller.handleInvalidMove(dropMove);
							}
						}
					}));
				}
				if (PieceUtil.containsBishop(usersPieces)) {
					popupMenu.add(popupMenu.add(new AbstractAction(
							"Drop bishop") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(
									controller.isUserWhite() ? Piece.WB
											: Piece.BB,
									source.getCoordinates(), controller
											.isWhitesMove());
							try {
								controller.makeMove(dropMove, controller
										.getPosition().makeMove(dropMove));
							} catch (IllegalMoveException ime) {
								ime.printStackTrace();
								controller.handleInvalidMove(dropMove);
							}
						}
					}));
				}
				if (PieceUtil.containsRook(usersPieces)) {

					popupMenu.add(popupMenu
							.add(new AbstractAction("Drop rook") {
								public void actionPerformed(ActionEvent event) {
									Move dropMove = new Move(controller
											.isUserWhite() ? Piece.WR
											: Piece.BR,
											source.getCoordinates(), controller
													.isWhitesMove());
									try {
										controller.makeMove(dropMove,
												controller.getPosition()
														.makeMove(dropMove));
									} catch (IllegalMoveException ime) {
										ime.printStackTrace();
										controller.handleInvalidMove(dropMove);
									}
								}
							}));
				}
				if (PieceUtil.containsQueen(usersPieces)) {

					popupMenu.add(popupMenu
							.add(new AbstractAction("Drop queen") {
								public void actionPerformed(ActionEvent event) {
									Move dropMove = new Move(controller
											.isUserWhite() ? Piece.WQ
											: Piece.BQ,
											source.getCoordinates(), controller
													.isWhitesMove());
									try {
										controller.makeMove(dropMove,
												controller.getPosition()
														.makeMove(dropMove));
									} catch (IllegalMoveException ime) {
										ime.printStackTrace();
										controller.handleInvalidMove(dropMove);
									}
								}
							}));
				}

				popupMenu.show(source, 10, 10);
			}
		}

	}

	public void moveOccured(UserActionEvent event) {
		synchronized (controller) {
			Position position = controller.getPosition();

			if (!isExamineMode()) {
				if (!isUsersMovingHisPieces(event)) {
					controller.playIllegalMoveSound();
					return;
				}
				if (!isUsersMove()) {
					position = position.reverseToMove();
				}

			}

			if (isExamineMode() || !isValidating()) {
				// just use a move string from the start and end coordinates
				// and
				// let the controller sort it all out.
				String moveString = null;

				if (event.isPromotion(position)) {
					int promotedPiece = controller
							.showPromotionDialog(controller.isUserWhite());

					if (promotedPiece == Piece.EMPTY) {
						controller.playIllegalMoveSound();
						return;
					}
					moveString = CoordinatesUtil.getDefaultCoordinates(event
							.getEndCoordinates()[0],
							event.getEndCoordinates()[1])
							+ "=" + PieceUtil.getDefaultPiece(promotedPiece);
				} else {
					moveString = CoordinatesUtil.getDefaultCoordinates(event
							.getStartCoordinates()[0], event
							.getStartCoordinates()[1])
							+ "-"
							+ CoordinatesUtil.getDefaultCoordinates(event
									.getEndCoordinates()[0], event
									.getEndCoordinates()[1]);
				}

				if (isExamineMode()) {
					controller.makeUnvalidatedMove(moveString);
				} else if (isUsersMove()) {
					controller.makeUnvalidatedMove(moveString);
				} else if (controller.getPreferences().getBoardPreferences()
						.getPremoveType() != BoardPreferences.NO_PREMOVE) {
					controller.setPremove(moveString, event
							.getStartCoordinates(), event.getEndCoordinates());
				}

			} else {
				Move move = null;

				if (event.isDrop()) {
					move = new Move(event.getDropPiece(), event
							.getEndCoordinates(), position.isWhitesMove());
				} else if (event.isPromotion(position)) {
					int promotedPiece = controller
							.showPromotionDialog(controller.isUserWhite());
					if (promotedPiece == Piece.EMPTY) {
						controller.playIllegalMoveSound();
						return;
					} else {
						move = new Move(promotedPiece, event
								.getStartCoordinates(), event
								.getEndCoordinates(), position.get(event
								.getEndCoordinates()), position.isWhitesMove());
					}
				} else {
					move = Move.createMove(event.getStartCoordinates(), event
							.getEndCoordinates(), position);
				}

				if (!isUsersMove()) {
					if (controller.getPreferences().getBoardPreferences()
							.getPremoveType() != BoardPreferences.NO_PREMOVE) {
						controller.setPremove(move.toString(), event
								.getStartCoordinates(), event
								.getEndCoordinates());
					}

				} else {
					try {
						
						controller.makeMove(move, position.makeMove(move));
					} catch (IllegalMoveException ime) {
						ime.printStackTrace();
						controller.handleInvalidMove(move);
					}
				}
			}
		}
	}
}