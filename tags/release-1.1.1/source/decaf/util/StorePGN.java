package decaf.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import decaf.gui.ChessAreaControllerBase;
import decaf.gui.GUIManager;
import decaf.gui.pref.LoggingPreferences;
import decaf.gui.widgets.movelist.MoveList;
import decaf.resources.ResourceManagerFactory;

public class StorePGN {
	private static final Logger LOGGER = Logger.getLogger(StorePGN.class);

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy.MM.dd");

	private static final SimpleDateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss");

	private static final File GAMES_DIR = new File(ResourceManagerFactory
			.getManager().getDecafUserHome()
			+ "/games");

	public static void storeBPGN(ChessAreaControllerBase controller) {
		if (!controller.isBughouse()) {
			throw new IllegalArgumentException(
					"Invoke storePGN for non bughouse games");
		}

	}

	private static void createGamesDirIfDoesntExist() {
		if (!GAMES_DIR.exists()) {
			GAMES_DIR.mkdir();
		}
	}

	public static void storePGN(ChessAreaControllerBase controller) {
		if (controller.isBughouse()) {
			throw new IllegalArgumentException(
					"Invoke storeBPGN for bughouse games");
		}

		createGamesDirIfDoesntExist();
		Date date = new Date();
		String fileName = GUIManager.getInstance().getPreferences()
				.getLoggingPreferences().getGameLogMode() == LoggingPreferences.APPEND_TO_GAMES_PGN ? "games.pgn"
				: controller.getInitialTimeSecs() / 60 + "-"
						+ controller.getInitialIncSecs() + "-"
						+ controller.getChessArea().getWhiteName() + "-"
						+ controller.getChessArea().getBlackName() + "-"
						+ FILE_NAME_DATE_FORMAT.format(date) + ".pgn";

		File file = new File(GAMES_DIR, fileName);

		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(new FileWriter(file, true));
			printWriter.println();
			printWriter.println("[Event \"" + controller.getInitialTimeSecs()
					/ 60 + " " + controller.getInitialIncSecs() + "\"]");
			printWriter.println("[Site \"freechess.org\"]");
			printWriter.println("[Date \"" + DATE_FORMAT.format(date) + "\"]");
			printWriter.println("[White \""
					+ controller.getChessArea().getWhiteName() + ":"
					+ controller.getChessArea().getWhitesRating() + "\"]");
			printWriter.println("[Black \""
					+ controller.getChessArea().getBlackName() + ":"
					+ controller.getChessArea().getBlacksRating() + "\"]");
			printWriter.println("[Result \""
					+ gameEndStateToResult(controller.getGameEndState())
					+ "\"]");
			printWriter.println();

			MoveList moveList = controller.getChessArea().getMoveList();
			for (int i = 0; i < moveList.getHalfMoves(); i++) {
				if (moveList.getMove(i).getPosition() != null) {
					boolean printNumber = i % 2 == 0;

					if (i != 0 && printNumber) {
						printWriter.print(" ");
					}

					if (printNumber) {
						printWriter.print((i + 1) + ".");
					} else {
						printWriter.print(" ");
					}
					printWriter.print(moveList.getMove(i)
							.getAlgebraicDescription());
				}
			}
			printWriter.print(" "
					+ gameEndStateToResult(controller.getGameEndState()));
			printWriter.println();
		} catch (IOException ioe) {
			LOGGER.error("Unexpected error occured reading pgn", ioe);
		} finally {
			printWriter.flush();
			printWriter.close();
		}

	}

	public static String gameEndStateToResult(int gameEndState) {
		switch (gameEndState) {
		case ChessAreaControllerBase.BLACK_WON: {
			return "0-1";
		}
		case ChessAreaControllerBase.WHITE_WON: {
			return "1-0";
		}
		case ChessAreaControllerBase.DRAW: {
			return "1/2-1/2";
		}
		case ChessAreaControllerBase.ABORTED: {
			return "*";
		}
		case ChessAreaControllerBase.ADJOURNED: {
			return "*";
		}
		case ChessAreaControllerBase.UNDETERMINED: {
			return "*";
		}
		default: {
			throw new IllegalArgumentException("Invalid gameEndState: "
					+ gameEndState);
		}
		}
	}
}
