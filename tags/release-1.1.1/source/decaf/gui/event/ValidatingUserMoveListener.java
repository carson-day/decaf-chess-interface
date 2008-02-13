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
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import decaf.gui.ChessAreaControllerBase;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.widgets.ChessBoardSquare;
import decaf.moveengine.IllegalMoveException;
import decaf.moveengine.Move;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;
import decaf.util.CoordinatesUtil;
import decaf.util.PieceUtil;

public class ValidatingUserMoveListener implements UserMoveInputListener {

	private static final Logger LOGGER = Logger
			.getLogger(ValidatingUserMoveListener.class);

	private static final Random SECURE_RANDOM = new SecureRandom();

	private ChessAreaControllerBase controller;

	private boolean isValidating;

	private boolean isExamineMode;

	private ChessBoardSquare clickClickMoveStartSquare;

	public ValidatingUserMoveListener(ChessAreaControllerBase icsController,
			boolean isValidating) {
		this.controller = icsController;
		this.isValidating = isValidating;
		this.isExamineMode = false;
	}

	public ValidatingUserMoveListener(ChessAreaControllerBase icsController,
			boolean isValidating, boolean isExamineMode) {
		this(icsController, isValidating);
		this.isExamineMode = isExamineMode;
	}

	public void userMadeIncompleteMove(UserIncompleteMoveEvent event) {
		controller.clearPremove();
	}

	public boolean isValidating() {
		return isValidating;
	}

	public void dispose() {
		controller = null;
	}

	public boolean isExamineMode() {
		return isExamineMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see caffeine.gui.event.GUIMoveListener#rightClickOccured(caffeine.gui.widgets.ChessBoardSquare)
	 */
	public void userRightClicked(UserRightClickSquareEvent event) {
		final ChessBoardSquare source = event.getSource();

		if (controller.isExamining()) {
			// For BSETUP as well as dropping
			// There is no easy way to tell if an examined game is in bsetup
			// mode.
			JPopupMenu popupMenu = new JPopupMenu();
			populateExamineModeDropPopup(popupMenu, source);
			if (popupMenu.getSubElements().length > 0)
			{
			   popupMenu.show(source, 10, 10);
			}
		} else {
			if (!isUsersMove()
					&& controller.isDroppable()
					&& controller.isPlaying()
					&& source.getRank() != -1
					&& controller.getPreferences().getBoardPreferences()
							.getPremoveType() != BoardPreferences.NO_PREMOVE) {
				JPopupMenu popupMenu = new JPopupMenu();

				int[] usersPieces = controller.isUserWhite() ? controller
						.getPosition().getWhiteHoldings() : controller
						.getPosition().getBlackHoldings();

				if (controller.isBughouse()) {
					fillBughouseDropMenu(popupMenu, source, usersPieces);
				} else if (controller.isDroppable()) // zh
				{
					populateZhPremoveDropMenu(popupMenu, source, usersPieces);
				}

				if (popupMenu.getSubElements().length > 0) {
					popupMenu.show(source, 10, 10);
				}
			} else if (isUsersMove() && controller.isDroppable()
					&& controller.isPlaying() && source.getRank() != -1) {
				int[] usersPieces = controller.isUserWhite() ? controller
						.getPosition().getWhiteHoldings() : controller
						.getPosition().getBlackHoldings();

				JPopupMenu popupMenu = new JPopupMenu();

				if (controller.isBughouse()) {
					fillBughouseDropMenu(popupMenu, source, usersPieces);
					popupMenu.addSeparator();
				} else {
					populateZhDropMenu(popupMenu, source, usersPieces);
				}
				if (popupMenu.getSubElements().length > 0) {
					popupMenu.show(source, 10, 10);
				}


			}
		}
	}

	public void userClicked(ChessBoardSquare square) {
		int[] coordinates = square.getCoordinates();
		boolean isClickClickDND = controller.getPreferences()
				.getBoardPreferences().getDragAndDropMode() == BoardPreferences.CLICK_CLICK_DRAG_AND_DROP;
		boolean wasSuccessful = false;

		//Smart move.
		if (controller.getPreferences().getBoardPreferences()
				.isSmartMoveEnabled()
				&& controller.isUsersMove()) {
			Position position = controller.getChessArea().getPosition();

			Move[] legalMoves = position.getLegalMoves();
			List<Move> movesWithDestinationSquare = new LinkedList<Move>();

			for (int i = 0; i < legalMoves.length; i++) {
				//Dont add drop moves.
				if (CoordinatesUtil.equals(coordinates, legalMoves[i]
						.getEndCoordinates()) && !legalMoves[i].isDropMove()) {
					movesWithDestinationSquare.add(legalMoves[i]);
				}
			}

			if (movesWithDestinationSquare.size() == 0) {
				controller.playIllegalMoveSound();
			} else {
				// Randomly pick a move.
				Move move = movesWithDestinationSquare.size() == 1 ? movesWithDestinationSquare
						.get(0)
						: movesWithDestinationSquare.get(SECURE_RANDOM
								.nextInt(movesWithDestinationSquare.size()));

				try {
					controller.makeMove(move, position.makeMove(move));
					clickClickMoveStartSquare = null;
					wasSuccessful = true;
				} catch (IllegalMoveException ime) {
					if (!isClickClickDND) {
						controller.playIllegalMoveSound();
					}
				}
			}
		}

		// Handle click click DND
		if (!wasSuccessful && isClickClickDND) {
			if (clickClickMoveStartSquare == null) {
				controller.getChessArea().getBoard().unselectAllSquares();				
				clickClickMoveStartSquare = square;
				clickClickMoveStartSquare.select();
			} else {
				if (clickClickMoveStartSquare == square) {
					clickClickMoveStartSquare.unselect();
					controller.playIllegalMoveSound();
				} else {
					if ((PieceUtil.isWhitePiece(clickClickMoveStartSquare.getChessPiece()) && PieceUtil.isWhitePiece(square.getChessPiece())) ||
					    (PieceUtil.isBlackPiece(clickClickMoveStartSquare.getChessPiece()) && PieceUtil.isBlackPiece(square.getChessPiece())))
					{
						controller.getChessArea().getBoard().unselectAllSquares();	
						clickClickMoveStartSquare = square;
						clickClickMoveStartSquare.select();
					}
					else
					{						
						controller.getChessArea().getBoard().unselectAllSquares();
						makeMove(clickClickMoveStartSquare.getCoordinates(), square
								.getCoordinates(), clickClickMoveStartSquare
								.getDropPiece(), clickClickMoveStartSquare
								.isDropSquare());
					}
				}
			}
		}
	}

	public boolean userMoved(UserMoveEvent event) {
		return makeMove(event.getStartCoordinates(), event.getEndCoordinates(),
				event.getDropPiece(), event.isDrop());
	}

	private boolean makeMove(int[] startCoordinates, int[] endCoordinates,
			int dropPiece, boolean isDrop) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Entering move occured: " + this + " "
					+ controller.getGameId() + " " + controller.getPosition());
		}

		try {
			synchronized (controller) {
				Position position = controller.getPosition();

				if (isExamineMode()) {
					LOGGER.debug("Entering examine mode");
					// just use a move string from the start and end coordinates
					// and
					// let the controller sort it all out.
					String moveString = null;

					if (isPromotion(position, startCoordinates, endCoordinates)) {
						int promotedPiece = controller
								.showPromotionDialog(controller.isUserWhite());

						if (promotedPiece == Piece.EMPTY) {
							controller.playIllegalMoveSound();
							return false;
						}
						moveString = CoordinatesUtil
								.getDefaultCoordinates(startCoordinates)
								+ "-"
								+ CoordinatesUtil
										.getDefaultCoordinates(endCoordinates)
								+ "="
								+ PieceUtil.getDefaultPiece(promotedPiece);
					} else {
						moveString = CoordinatesUtil
								.getDefaultCoordinates(startCoordinates)
								+ "-"
								+ CoordinatesUtil
										.getDefaultCoordinates(endCoordinates);
					}
					controller.makeUnvalidatedMove(moveString);
				} else if (!isValidating()) {
					// just use a move string from the start and end coordinates
					// and
					// let the controller sort it all out.
					String moveString = null;

					if (isPromotion(position, startCoordinates, endCoordinates)) {
						int promotedPiece = controller
								.showPromotionDialog(controller.isUserWhite());

						if (promotedPiece == Piece.EMPTY) {
							controller.playIllegalMoveSound();
							return false;
						}
						moveString = CoordinatesUtil
								.getDefaultCoordinates(startCoordinates)
								+ "-"
								+ CoordinatesUtil
										.getDefaultCoordinates(endCoordinates)
								+ "="
								+ PieceUtil.getDefaultPiece(promotedPiece);
					} else {
						moveString = CoordinatesUtil
								.getDefaultCoordinates(startCoordinates)
								+ "-"
								+ CoordinatesUtil
										.getDefaultCoordinates(endCoordinates);
					}

					if (isExamineMode()) {
						controller.makeUnvalidatedMove(moveString);
					} else if (isUsersMove()) {
						controller.makeUnvalidatedMove(moveString);
					} else if (controller.getPreferences()
							.getBoardPreferences().getPremoveType() != BoardPreferences.NO_PREMOVE) {
						controller.setPremove(moveString, startCoordinates,
								endCoordinates);
					}

				} else {
					Move move = null;

					if (isDrop) {
						move = new Move(dropPiece, endCoordinates, position
								.isWhitesMove());
					} else if (isPromotion(position, startCoordinates,
							endCoordinates)) {
						int promotedPiece = controller
								.showPromotionDialog(controller.isUserWhite());
						if (promotedPiece == Piece.EMPTY) {
							controller.playIllegalMoveSound();
							return false;
						} else {
							move = new Move(promotedPiece, startCoordinates,
									endCoordinates, position
											.get(endCoordinates), position
											.isWhitesMove());
						}
					} else {
						move = Move.createMove(startCoordinates,
								endCoordinates, position);
					}

					if (!isUsersMove()) {
						if (controller.getPreferences().getBoardPreferences()
								.getPremoveType() != BoardPreferences.NO_PREMOVE) {
							controller.setPremove(move.toString(),
									startCoordinates, endCoordinates);

							// return false on a premove so if its a zh/bug
							// piece
							// the labels will be updated correctly.
							return false;
						}

					} else {
						try {

							controller.makeMove(move, position.makeMove(move));
						} catch (IllegalMoveException ime) {
							// ime.printStackTrace();
							controller.handleInvalidMove(move);
							return false;
						}
					}
				}
			}
			return true;
		} finally {
			// ugly but there are multiple returns and this is the easiest way
			// to pull it off.
			clickClickMoveStartSquare = null;
		}

	}

	private boolean isPromotion(Position position, int[] startCoordinates,
			int[] endCoordinates) {
		return (position.isWhitesMove()
				&& endCoordinates[0] == CoordinatesUtil.RANK_8 && position
				.get(startCoordinates) == Piece.WHITE_PAWN)
				|| (!position.isWhitesMove()
						&& endCoordinates[0] == CoordinatesUtil.RANK_1 && position
						.get(startCoordinates) == Piece.BLACK_PAWN);
	}

	private boolean isUsersMove() {
		return (controller.getPosition().isWhitesMove() && controller
				.isUserWhite())
				|| (!controller.getPosition().isWhitesMove() && !controller
						.isUserWhite());
	}

	private void fillBughouseDropMenu(final JPopupMenu popupMenu,
			final ChessBoardSquare source, int[] userPieces) {
		final boolean isUsersMove = controller.isUsersMove();
		final boolean isEmpty = source.getChessPiece() == Piece.EMPTY;

		if (controller.isBughouse()) {
			if (CoordinatesUtil.RANK_1 != source.getRank()
					&& CoordinatesUtil.RANK_8 != source.getRank()
					&& !PieceUtil.containsPawn(userPieces)) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						"Premove Drop Pawn") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WP : Piece.BP,
								source.getCoordinates(), controller
										.isWhitesMove());
						controller.setDropPremove(dropMove.toString(),
								controller.isUserWhite() ? Piece.WP : Piece.BP,
								new int[] { -1, -1 }, source.getCoordinates());
					}
				}));
			} else if (CoordinatesUtil.RANK_1 != source.getRank()
					&& CoordinatesUtil.RANK_8 != source.getRank()
					&& ((isUsersMove && !isEmpty) || !isUsersMove)) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						isUsersMove ? "Drop Pawn" : "Premove Drop Pawn") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WP : Piece.BP,
								source.getCoordinates(), controller
										.isWhitesMove());
						if (isUsersMove) {
							try {
								controller.makeMove(dropMove, controller
										.getPosition().makeMove(dropMove));
							} catch (IllegalMoveException ime) {
								ime.printStackTrace();
								controller.handleInvalidMove(dropMove);
							}
						} else {
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, source
											.getCoordinates());
						}
					}
				}));
			}
			if (!PieceUtil.containsKnight(userPieces)) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						"Premove Drop Knight") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WN : Piece.BN,
								source.getCoordinates(), controller
										.isWhitesMove());
						controller.setDropPremove(dropMove.toString(),
								controller.isUserWhite() ? Piece.WN : Piece.BN,
								new int[] { -1, -1 }, source.getCoordinates());
					}
				}));
			} else if ((isUsersMove && !isEmpty) || !isUsersMove) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						isUsersMove ? "Drop Knight" : "Premove Drop Knight") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WN : Piece.BN,
								source.getCoordinates(), controller
										.isWhitesMove());
						if (isUsersMove) {
							try {
								controller.makeMove(dropMove, controller
										.getPosition().makeMove(dropMove));
							} catch (IllegalMoveException ime) {
								ime.printStackTrace();
								controller.handleInvalidMove(dropMove);
							}
						} else {
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, source
											.getCoordinates());
						}
					}
				}));
			}
			if (!PieceUtil.containsBishop(userPieces)) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						"Premove Drop Bishop") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WB : Piece.BB,
								source.getCoordinates(), controller
										.isWhitesMove());
						controller.setDropPremove(dropMove.toString(),
								controller.isUserWhite() ? Piece.WB : Piece.BB,
								new int[] { -1, -1 }, source.getCoordinates());
					}
				}));
			} else if ((isUsersMove && !isEmpty) || !isUsersMove) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						isUsersMove ? "Drop Bishop" : "Premove Drop Bishop") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WB : Piece.BB,
								source.getCoordinates(), controller
										.isWhitesMove());
						if (isUsersMove) {
							try {
								controller.makeMove(dropMove, controller
										.getPosition().makeMove(dropMove));
							} catch (IllegalMoveException ime) {
								ime.printStackTrace();
								controller.handleInvalidMove(dropMove);
							}
						} else {
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, source
											.getCoordinates());
						}
					}
				}));
			}
			if (!PieceUtil.containsRook(userPieces)) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						"Premove Drop Rook") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WR : Piece.BR,
								source.getCoordinates(), controller
										.isWhitesMove());
						controller.setDropPremove(dropMove.toString(),
								controller.isUserWhite() ? Piece.WR : Piece.BR,
								new int[] { -1, -1 }, source.getCoordinates());
					}
				}));
			} else if ((isUsersMove && !isEmpty) || !isUsersMove) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						isUsersMove ? "Drop Rook" : "Premove Drop Rook") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WR : Piece.BR,
								source.getCoordinates(), controller
										.isWhitesMove());
						if (isUsersMove) {
							try {
								controller.makeMove(dropMove, controller
										.getPosition().makeMove(dropMove));
							} catch (IllegalMoveException ime) {
								ime.printStackTrace();
								controller.handleInvalidMove(dropMove);
							}
						} else {
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, source
											.getCoordinates());
						}
					}
				}));
			}
			if (!PieceUtil.containsQueen(userPieces)) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						"Premove Drop queen") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WQ : Piece.BQ,
								source.getCoordinates(), controller
										.isWhitesMove());
						controller.setDropPremove(dropMove.toString(),
								controller.isUserWhite() ? Piece.WQ : Piece.BQ,
								new int[] { -1, -1 }, source.getCoordinates());
					}
				}));
			} else if ((isUsersMove && !isEmpty) || !isUsersMove) {
				popupMenu.add(popupMenu.add(new AbstractAction(
						isUsersMove ? "Drop Queen" : "Premove Drop Queen") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WQ : Piece.BQ,
								source.getCoordinates(), controller
										.isWhitesMove());
						if (isUsersMove) {
							try {
								controller.makeMove(dropMove, controller
										.getPosition().makeMove(dropMove));
							} catch (IllegalMoveException ime) {
								ime.printStackTrace();
								controller.handleInvalidMove(dropMove);
							}
						} else {
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, source
											.getCoordinates());
						}
					}
				}));
			}
		}
	}

	private void populateExamineModeDropPopup(final JPopupMenu popupMenu,
			final ChessBoardSquare source) {
		popupMenu.add(new AbstractAction("Clear Square") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("x@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.addSeparator();
		popupMenu.add(new AbstractAction("Drop White Pawn") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("P@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop White Knight") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("N@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop White Bishop") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("B@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop White Rook") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("R@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop White Queen") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("Q@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop White King") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("K@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.addSeparator();
		popupMenu.add(new AbstractAction("Drop Black Pawn") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("p@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop Black Knight") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("n@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop Black Bishop") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("b@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop Black Queen") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("q@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop Black Rook") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("r@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
		popupMenu.add(new AbstractAction("Drop Black King") {
			public void actionPerformed(ActionEvent event) {
				controller.makeUnvalidatedMove("k@"
						+ CoordinatesUtil.getDefaultCoordinates(source
								.getCoordinates()));
			}
		});
	}

	private void populateZhDropMenu(final JPopupMenu popupMenu,
			final ChessBoardSquare source, final int[] usersPieces) {
		if (usersPieces.length != 0) {

			if (CoordinatesUtil.RANK_1 != source.getRank()
					&& CoordinatesUtil.RANK_8 != source.getRank()
					&& PieceUtil.containsPawn(usersPieces)) {
				popupMenu.add(popupMenu.add(new AbstractAction("Drop pawn") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WP : Piece.BP,
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
			if (PieceUtil.containsKnight(usersPieces)) {
				popupMenu.add(popupMenu.add(new AbstractAction("Drop knight") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WN : Piece.BN,
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
				popupMenu.add(popupMenu.add(new AbstractAction("Drop bishop") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WB : Piece.BB,
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

				popupMenu.add(popupMenu.add(new AbstractAction("Drop rook") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WR : Piece.BR,
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
			if (PieceUtil.containsQueen(usersPieces)) {

				popupMenu.add(popupMenu.add(new AbstractAction("Drop queen") {
					public void actionPerformed(ActionEvent event) {
						Move dropMove = new Move(
								controller.isUserWhite() ? Piece.WQ : Piece.BQ,
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
		}
	}

	private void populateZhPremoveDropMenu(final JPopupMenu popupMenu,
			final ChessBoardSquare source, final int[] usersPieces) {
		if (controller.isBughouse()) {
			fillBughouseDropMenu(popupMenu, source, usersPieces);
		} else if (controller.isDroppable()) // zh
		{

			if (usersPieces.length != 0) {
				if (CoordinatesUtil.RANK_1 != source.getRank()
						&& CoordinatesUtil.RANK_8 != source.getRank()
						&& PieceUtil.containsPawn(usersPieces)) {
					popupMenu.add(popupMenu.add(new AbstractAction(
							"Premove Drop pawn") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(
									controller.isUserWhite() ? Piece.WP
											: Piece.BP,
									source.getCoordinates(), controller
											.isWhitesMove());

							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, dropMove
											.getEndCoordinates());
						}
					}));
				}
				if (PieceUtil.containsKnight(usersPieces)) {
					popupMenu.add(popupMenu.add(new AbstractAction(
							"Premove Drop knight") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(
									controller.isUserWhite() ? Piece.WN
											: Piece.BN,
									source.getCoordinates(), controller
											.isWhitesMove());
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, dropMove
											.getEndCoordinates());
						}
					}));
				}
				if (PieceUtil.containsBishop(usersPieces)) {
					popupMenu.add(popupMenu.add(new AbstractAction(
							"Premove Drop bishop") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(
									controller.isUserWhite() ? Piece.WB
											: Piece.BB,
									source.getCoordinates(), controller
											.isWhitesMove());
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, dropMove
											.getEndCoordinates());
						}
					}));
				}
				if (PieceUtil.containsRook(usersPieces)) {

					popupMenu.add(popupMenu.add(new AbstractAction(
							"Premove Drop rook") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(
									controller.isUserWhite() ? Piece.WR
											: Piece.BR,
									source.getCoordinates(), controller
											.isWhitesMove());
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, dropMove
											.getEndCoordinates());
						}
					}));
				}
				if (PieceUtil.containsQueen(usersPieces)) {

					popupMenu.add(popupMenu.add(new AbstractAction(
							"Premove Drop queen") {
						public void actionPerformed(ActionEvent event) {
							Move dropMove = new Move(
									controller.isUserWhite() ? Piece.WQ
											: Piece.BQ,
									source.getCoordinates(), controller
											.isWhitesMove());
							controller.setPremove(dropMove.toString(),
									new int[] { -1, -1 }, dropMove
											.getEndCoordinates());
						}
					}));
				}
			}
		}

	}
}