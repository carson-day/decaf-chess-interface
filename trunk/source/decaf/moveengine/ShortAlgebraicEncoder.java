package decaf.moveengine;

import org.apache.log4j.Logger;

import decaf.util.CoordinatesUtil;

/**
 * A Short Algebraic Encoder using the FICS short algebraic syntax:
 * 
 * <pre>
 *                   e4 (pawn move forward to e4).
 *                   exf4 (pawn takes e4).
 *                   N5xc6 (two aN can capture on c6).
 *                   Na5c6 (three knights can move to c6, two on the 5th rank, two on 
 *                   the same file).
 *                   Naxc6 (two knights on the same file can capture c6).
 *                   Nc6 (only one knight can move to c6).
 *                   p@e3 (Pawn dropped on e3).
 *                   e8=Q
 *                   fxg8=Q 
 *                  Files are always lower-case and pieces are always upper-case.
 * </pre>
 * 
 */
public class ShortAlgebraicEncoder extends LongAlgebraicEncoder {
	/**
	 * Known issues: [java] GuestHBCX (UNR) vs. GuestCRSG (UNR) --- Tue Jan 1,
	 * 09:02 PST 2008 [java] Unrated blitz match, initial time: 10 minutes,
	 * increment: 0 seconds.
	 * 
	 * [java] Move GuestHBCX GuestCRSG [java] ---- ---------------------
	 * --------------------- [java] 1. e4 (0:00.000) e5 (0:00.000) [java] 2. f3
	 * (0:08.784) f6 (0:11.141) [java] 3. Bc4 (0:16.519) Nh6 (0:38.402) [java]
	 * 4. d4 (0:02.535) d6 (0:14.082) [java] 5. Bxh6 (0:03.918) gxh6 (0:04.471)
	 * [java] 6. dxe5 (0:01.027) fxe5 (0:09.628) [java] 7. Nc3 (0:06.191) Nc6
	 * (0:04.483) [java] 8. Bb5 (0:02.839) Bd7 (0:02.018) [java] 9. Bxc6
	 * (0:02.267) Bxc6 (0:03.789) [java] 10. Nh3 (0:04.375) Qe7 (0:02.817)
	 * [java] 11. Qd2 (0:14.054) O-O-O (0:05.389) [java] 12. O-O-O (0:03.137)
	 * Qe6 (0:34.871) [java] 13. Qe3 (0:10.572) d5 (0:05.228) [java] 14. Qxa7
	 * (0:06.518) Bb4 (0:24.798) [java] 15. Qa8+ (0:42.964) Kd7 (0:03.654)
	 * [java] 16. Rxd5+ (0:11.359) Bxd5 (0:13.865) [java] 17. Qa4+ (0:03.748)
	 * Qc6 (0:54.802) [java] 18. Qxb4 (0:03.396) Qa6 (0:31.370) [java] 19. exd5
	 * (0:10.043) Kc8 (0:20.958) [java] 20. Qg4+ (0:14.338) Kb8 (0:30.444)
	 * [java] 21. Qe6 (0:28.347) Qxe6 (0:36.080) [java] 22. dxe6 (0:01.747) Rhe8
	 * (0:04.392) [java] 23. Rd1 (0:09.061) Rxd1+ (0:05.873) [java] 24. Nxd1
	 * (0:01.413) Rxe6 (0:01.464) [java] 25. Ne3 (0:01.698) Rg6 (0:02.384)
	 * [java] 26. Kd2 (0:02.857) Rd6+ (0:05.852) [java] 27. Ke2 (0:03.962) Rc6
	 * (0:01.590) [java] 28. Nf2 (0:08.127) Ra6 (0:05.552) [java] 29. a3
	 * (0:03.702) b5 (0:05.784) [java] 30. Nd3 (0:03.727) c6 (0:01.773) [java]
	 * 31. Nc5 (0:02.262) Ra7 (0:08.040) [java] 32. Kd3 (0:16.829) Re7
	 * (0:06.638) [java] 33. Ke4 (0:11.176) Kc7 (0:01.886) [java] 34. b4
	 * (0:06.964) Kb6 (0:02.888) [java] 35. h4 (0:26.668) Ra7 (0:03.074) [java]
	 * 36. Kxe5 (0:12.252) Rxa3 (0:05.861) [java] 37. Kd4 (0:01.654) Ra2
	 * (0:06.516) [java] 38. Nd7+ (0:09.214) Kc7 (0:07.969) [java] 39. Nf6
	 * (0:03.352) Rb2 (0:04.122) [java] 40. Kc3 (0:02.915) Ra2 (0:18.576) [java]
	 * 41. Nxh7 (0:02.359) Kb6 (0:06.140) [java] 42. Nf6 (0:04.303) c5
	 * (0:02.198) [java] 43. Nd7+ (0:03.105) Kc6 (0:06.138) [java] 44. Nxc5
	 * (0:07.206) Ra3+ (0:04.358) [java] 45. Kd4 (0:02.579) Ra4 (0:05.587)
	 * [java] 46. c3 (0:15.311) Ra7 (0:28.344) [java] 47. Nf5 (0:13.621) Rf7
	 * (0:10.898) [java] 48. Nxh6 (0:05.254) Rh7 (0:08.134) [java] 49. Nf5
	 * (0:03.282) Rh8 (0:02.451) [java] 50. Ne7+ (0:03.696) Kd6 (0:07.573)
	 * [java] 51. Nf5+ (0:05.869) Kc6 (0:12.328) [java] {Still in progress}
	 * *2008-01-01 12:18:46,679 ERROR (MoveListParser.java:107) - Error occured
	 * parsing movelist [java] java.lang.IllegalArgumentException: Could not
	 * find start coordinates d5 position= [java] --- --- --- --- --- --- ---
	 * --- [java] : : : k : r : : b : : r : Blacks move. [java] --- --- --- ---
	 * --- --- --- --- [java] : p : p : p : : : : : p : White can castle
	 * Kingside Queenside [java] --- --- --- --- --- --- --- --- [java] : : : b
	 * : : q : p : : p : Black can castle Kingside Queenside [java] --- --- ---
	 * --- --- --- --- --- [java] : : : : : p : : : : [java] --- --- --- --- ---
	 * --- --- --- [java] : : : : : P : : : : White holdings [java] --- --- ---
	 * --- --- --- --- --- [java] : : : N : : Q : P : : N : Black holdings
	 * [java] --- --- --- --- --- --- --- --- [java] : P : P : P : : : : P : P :
	 * [java] --- --- --- --- --- --- --- --- [java] : : : K : R : : : : R :
	 * [java] --- --- --- --- --- --- --- ---
	 * 
	 * [java] at decaf.moveengine.ShortAlgebraicEncoder.getStartCoordinates(
	 * ShortAlgebraicEncoder.java:196) [java] at
	 * decaf.moveengine.ShortAlgebraicEncoder
	 * .decode(ShortAlgebraicEncoder.java:57) [java] at
	 * decaf.messaging.ics.nongameparser
	 * .MoveListParser.appendMove(MoveListParser.java:123) [java] at
	 * decaf.messaging
	 * .ics.nongameparser.MoveListParser.parse(MoveListParser.java:100) [java]
	 * at
	 * decaf.messaging.ics.FicsParser.parseNonGameMessages(FicsParser.java:476)
	 * [java] at decaf.messaging.ics.FicsParser.parse(FicsParser.java:355)
	 * [java] at
	 * decaf.messaging.ics.ICSCommunicationsDriver.publishInboundEvents
	 * (ICSCommunicationsDriver.java:481) [java] at
	 * decaf.messaging.ics.ICSCommunicationsDriver
	 * .messageArrived(ICSCommunicationsDriver.java:477) [java] at
	 * decaf.messaging
	 * .ics.ICSCommunicationsDriver.access$900(ICSCommunicationsDriver.java:51)
	 * [java] at
	 * decaf.messaging.ics.ICSCommunicationsDriver$InboundMessageHandler
	 * .run(ICSCommunicationsDriver.java:728) [java] at
	 * java.lang.Thread.run(Thread.java:613)
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ShortAlgebraicEncoder.class);

	private static final int[] WHITE_DROP_HOLDINGS = { Piece.WP, Piece.WN,
			Piece.WB, Piece.WQ, Piece.WR };

	private static final int[] BLACK_DROP_HOLDINGS = { Piece.BP, Piece.BN,
			Piece.BB, Piece.BQ, Piece.BR };

	private static final String VALID_FILES = "abcdefgh";

	private static final String VALID_RANKS = "12345678";

	private int charToPiece(char character, boolean isWhitesMove) {
		switch (character) {
		case 'p':
		case 'P': {
			return isWhitesMove ? Piece.WP : Piece.BP;
		}
		case 'n':
		case 'N': {
			return isWhitesMove ? Piece.WN : Piece.BN;
		}
		case 'b':
		case 'B': {
			return isWhitesMove ? Piece.WB : Piece.BB;
		}
		case 'r':
		case 'R': {
			return isWhitesMove ? Piece.WR : Piece.BR;
		}
		case 'q':
		case 'Q': {
			return isWhitesMove ? Piece.WQ : Piece.BQ;
		}
		case 'k':
		case 'K': {
			return isWhitesMove ? Piece.WK : Piece.BK;
		}
		default: {
			throw new IllegalArgumentException("Unknown piece: " + character);
		}
		}
	}

	@Override
	public Move decode(String moveString, Position position)
			throws IllegalArgumentException {

		if (moveString.endsWith("#")) {
			moveString = moveString.substring(0, moveString.length() - 1);
		}
		if (moveString.endsWith("+")) {
			moveString = moveString.substring(0, moveString.length() - 1);
		}

		Move result = null;
		// Get pseudo legal moves so suicide,bug,zh,losers,atomic will all work
		// It will choke on fisher random castling.
		Move[] moves = getPseudoLegalMoves(position);
		moveString = stripCheck(moveString);
		int pieceMoving = getPieceMoving(moveString, position);
		int promotedPiece = getPromotedPiece(moveString, position);
		int[] endCoordinates = getEndCoordinates(moveString, position);
		int[] startCoordinates = getStartCoordinates(moveString, position,
				promotedPiece, pieceMoving, endCoordinates, moves);
		// LOGGER.debug("move=" + moveString);
		// LOGGER.debug("pieceMoving=" + pieceMoving);
		// LOGGER.debug("endCoordinates=" + endCoordinates[0] + ","
		// + endCoordinates[1]);
		// LOGGER.debug("startCoordinates=" + startCoordinates[0] + ","
		// + startCoordinates[1]);
		// LOGGER.debug("position=" + position.toString());

		for (int i = 0; result == null && i < moves.length; i++) {
			Move move = moves[i];
			if (promotedPiece != -1) {
				if (CoordinatesUtil.equals(endCoordinates, move
						.getEndCoordinates())
						&& CoordinatesUtil.equals(startCoordinates, move
								.getStartCoordinates())
						&& move.isPromotion()
						&& move.getPromotedPiece() == promotedPiece) {
					result = move;
				}

			} else if (move.isDropMove() && startCoordinates[0] == -1
					&& startCoordinates[1] == -1
					&& move.getDroppedPiece() == pieceMoving) {
				if (CoordinatesUtil.equals(endCoordinates, move
						.getEndCoordinates())) {
					result = move;
				}

			} else if (move.getPieceMoving() == pieceMoving
					&& startCoordinates[0] != -1 && startCoordinates[1] != -1) {
				/*
				 * LOGGER.error("Testing move: " + move + " pieceMoving=" +
				 * pieceMoving + " endCoordinates=" +
				 * CoordinatesUtil.getDefaultCoordinates(move
				 * .getEndCoordinates()) + " startCoordinates=" +
				 * CoordinatesUtil.getDefaultCoordinates(move
				 * .getStartCoordinates()) + " expectedEndCoordinates=" +
				 * CoordinatesUtil.getDefaultCoordinates(endCoordinates) + "
				 * expectedStartCoordinates=" + CoordinatesUtil
				 * .getDefaultCoordinates(startCoordinates));
				 */

				if (CoordinatesUtil.equals(endCoordinates, move
						.getEndCoordinates())
						&& CoordinatesUtil.equals(startCoordinates, move
								.getStartCoordinates())) {
					result = move;
				}
			}
		}

		if (result == null) {
			/*
			 * LOGGER.error("All legal moves on failure: " +
			 * position.getBoardState()); for (int i = 0; i < moves.length; i++)
			 * { Move move = moves[i]; LOGGER.error("Testing move: " + move + "
			 * pieceMoving=" + pieceMoving + " endCoordinates=" +
			 * CoordinatesUtil.getDefaultCoordinates(move .getEndCoordinates())
			 * + " startCoordinates=" +
			 * CoordinatesUtil.getDefaultCoordinates(move
			 * .getStartCoordinates()) + " expectedEndCoordinates=" +
			 * CoordinatesUtil.getDefaultCoordinates(endCoordinates) + "
			 * expectedStartCoordinates=" + CoordinatesUtil
			 * .getDefaultCoordinates(startCoordinates)); } Move[] debugMoves =
			 * new Move[5];
			 * PositionUtil.getPsuedoKingCastlingMoves(debugMoves,0,
			 * position.getBoard
			 * (),position.getBoardState(),startCoordinates[0],startCoordinates
			 * [1]);
			 * 
			 * LOGGER.error("Debug moves after failure:"); for (int i = 0; i <
			 * debugMoves.length; i++) { Move debugMove = debugMoves[i]; if
			 * (debugMove != null) {
			 * LOGGER.error(CoordinatesUtil.getDefaultCoordinates(debugMove
			 * .getStartCoordinates()) + " " +
			 * CoordinatesUtil.getDefaultCoordinates(debugMove
			 * .getEndCoordinates())); } }
			 */
			throw new IllegalArgumentException("Could not find " + moveString);
		}
		return result;
	}

	@Override
	public String encode(Move move, Position position) {

		throw new UnsupportedOperationException();
	}

	private int[] getEndCoordinates(String moveString, Position position) {
		if (moveString.length() >= 2) {

			if (moveString.equalsIgnoreCase("o-o")) {
				return position.isWhitesMove() ? Coordinates.H1
						: Coordinates.H8;
			} else if (moveString.equalsIgnoreCase("o-o-o")) {
				return position.isWhitesMove() ? Coordinates.A1
						: Coordinates.A8;
			} else if (moveString.endsWith("ep")) {
				throw new IllegalArgumentException("Cant handle ep yet "
						+ moveString);
			} else if (moveString.charAt(1) == ('@')) {
				char file = moveString.charAt(moveString.length() - 2);
				char rank = moveString.charAt(moveString.length() - 1);
				return new int[] { rankFromChar(rank), fileFromChar(file) };
			} else if (moveString.indexOf('=') != -1) {
				// bxa8=Q
				int xIndex = moveString.indexOf('x');

				char file = 0;
				char rank = 0;

				if (xIndex == -1) {

					file = moveString.charAt(0);
					rank = moveString.charAt(1);
				} else {
					file = moveString.charAt(xIndex + 1);
					rank = moveString.charAt(xIndex + 2);
				}
				return new int[] { rankFromChar(rank), fileFromChar(file) };
			} else if (Character.isDigit(moveString
					.charAt(moveString.length() - 1))
					&& VALID_FILES.indexOf(moveString.charAt(moveString
							.length() - 2)) != -1) {
				char file = moveString.charAt(moveString.length() - 2);
				char rank = moveString.charAt(moveString.length() - 1);

				return new int[] { rankFromChar(rank), fileFromChar(file) };
			} else {
				throw new IllegalArgumentException(
						"Cant determine end coordinates " + moveString);
			}
		} else {
			throw new IllegalArgumentException("Invalid short algebraic move "
					+ moveString);
		}
	}

	private int getPieceMoving(String moveString, Position position) {
		int result = Piece.EMPTY;
		if (moveString.length() < 2) {
			throw new IllegalArgumentException("Invalid short algebraic move "
					+ moveString);
		} else if (moveString.length() == 2) {
			result = position.isWhitesMove() ? Piece.WP : Piece.BP;
		} else if (moveString.indexOf('=') != -1) {
			result = position.isWhitesMove() ? Piece.WP : Piece.BP;
		} else if (moveString.charAt(1) == ('@')) {
			result = charToPiece(moveString.charAt(0), position.isWhitesMove());
		} else if (moveString.equalsIgnoreCase("o-o")
				|| moveString.equalsIgnoreCase("o-o-o")) {
			result = position.isWhitesMove() ? Piece.WK : Piece.BK;
		} else if (moveString.length() == 3) {
			result = charToPiece(moveString.charAt(0), position.isWhitesMove());
		} else if (moveString.endsWith("ep")) {
			result = position.isWhitesMove() ? Piece.WP : Piece.BP;
		} else {
			char pieceMoving = moveString.charAt(0);
			if (VALID_FILES.indexOf(pieceMoving) == -1) {
				result = charToPiece(pieceMoving, position.isWhitesMove());
			} else {
				result = position.isWhitesMove() ? Piece.WP : Piece.BP;
			}

		}
		return result;
	}

	private int getPromotedPiece(String moveString, Position position) {
		int result = -1;

		if (moveString.indexOf('=') != -1) {
			result = charToPiece(moveString.charAt(moveString.length() - 1),
					position.isWhitesMove());
		}
		return result;
	}

	private Move[] getPseudoLegalMoves(Position position) {
		Move[] moves = new Move[420];
		int lastMovesIndex = -1;

		// This will still choke on fischer random when castling occurs.

		// get psudo legal moves.
		lastMovesIndex = PositionUtil.getPsuedoLegalMoves(moves,
				lastMovesIndex, position.getBoard(), position.getBoardState());

		lastMovesIndex = PositionUtil.getPseudoDropMoves(moves, lastMovesIndex,
				position.getBoard(), position.getBoardState(), position
						.isWhitesMove() ? WHITE_DROP_HOLDINGS
						: BLACK_DROP_HOLDINGS);

		return moves;

	}

	private int[] getStartCoordinates(String moveString, Position position,
			int promotedPiece, int pieceMoving, int[] endCoordinates,
			Move[] legalMoves) {
		int[] result = null;
		if (moveString.charAt(1) == ('@')) {
			result = new int[] { -1, -1 };
		} else if (moveString.equalsIgnoreCase("o-o")
				|| moveString.equalsIgnoreCase("o-o-o")) {
			result = position.isWhitesMove() ? Coordinates.E1 : Coordinates.E8;
		} else if (moveString.indexOf('=') != -1) {
			// fxg8=Q
			// e8=q
			int file = moveString.indexOf('x') != -1 ? fileFromChar(moveString
					.charAt(0)) : -1;
			LOGGER.debug(moveString);
			for (int i = 0; result == null && i < legalMoves.length; i++) {
				Move currentMove = legalMoves[i];

				if (currentMove != null
						&& currentMove.getPieceMoving() == pieceMoving
						&& CoordinatesUtil.equals(currentMove
								.getEndCoordinates(), endCoordinates)
						&& currentMove.getPromotedPiece() == promotedPiece
						&& (file == -1 || currentMove.getStartCoordinates()[1] == file)) {
					// then this most be the move since the string was 3 in
					// length.
					result = currentMove.getStartCoordinates();
				}
			}

			if (result == null) {
				throw new IllegalArgumentException(
						"Could not find start coordinates " + moveString
								+ " position=" + position.toString());
			}
		} else if (moveString.length() == 2
				|| moveString.length() == 3
				|| (moveString.length() == 4 && (moveString.indexOf("x") != -1 || moveString
						.indexOf("X") != -1))) {
			for (int i = 0; result == null && i < legalMoves.length; i++) {
				Move currentMove = legalMoves[i];

				// There is a bug where current move is null sometimes. This
				// needs to be tracked down.
				if (currentMove != null
						&& currentMove.getPieceMoving() == pieceMoving
						&& CoordinatesUtil.equals(currentMove
								.getEndCoordinates(), endCoordinates)) {
					// then this most be the move since the string was 3 in
					// length.
					result = currentMove.getStartCoordinates();
				}
			}

			if (result == null) {
				throw new IllegalArgumentException(
						"Could not find start coordinates " + moveString
								+ " position=" + position.toString());
			}
		} else if (moveString.length() == 4
				|| (moveString.length() == 5 && (moveString.indexOf("x") != -1 || moveString
						.indexOf("X") != -1))) {
			char positionChar = moveString.charAt(1);
			boolean isRank = VALID_RANKS.indexOf(positionChar) != -1;
			int positionValue = isRank ? CoordinatesUtil
					.algebraicRankToCoordinatesRank(positionChar)
					: CoordinatesUtil
							.algebraicFileToCoordinatesFile(positionChar);

			for (int i = 0; result == null && i < legalMoves.length; i++) {
				Move currentMove = legalMoves[i];

				if (currentMove.getPieceMoving() == pieceMoving
						&& CoordinatesUtil.equals(currentMove
								.getEndCoordinates(), endCoordinates)) {
					if (isRank
							&& currentMove.getStartCoordinates()[0] == positionValue) {
						// then this most be the move since the string was 3 in
						// length.
						result = currentMove.getStartCoordinates();
					} else if (currentMove.getStartCoordinates()[1] == positionValue) {
						result = currentMove.getStartCoordinates();
					}
				}
			}

			if (result == null) {
				throw new IllegalArgumentException(
						"Could not find start coordinates " + moveString);
			}
		} else {
			int rank = CoordinatesUtil
					.algebraicRankToCoordinatesRank(moveString.charAt(2));
			int file = CoordinatesUtil
					.algebraicFileToCoordinatesFile(moveString.charAt(1));

			for (int i = 0; result == null && i < legalMoves.length; i++) {
				Move currentMove = legalMoves[i];

				if (currentMove.getPieceMoving() == pieceMoving
						&& CoordinatesUtil.equals(currentMove
								.getEndCoordinates(), endCoordinates)) {
					if (currentMove.getStartCoordinates()[0] == rank
							&& currentMove.getStartCoordinates()[1] == file) {
						// then this most be the move since the string was 3 in
						// length.
						result = currentMove.getStartCoordinates();
					}
				}
			}

			if (result == null) {
				throw new IllegalArgumentException(
						"Could not find start coordinates " + moveString);
			}
		}
		return result;
	}

	private String stripCheck(String moveString) {
		if (moveString.endsWith("+")) {
			return moveString.substring(0, moveString.length() - 1);
		} else {
			return moveString;
		}
	}
}
