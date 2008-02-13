package decaf.gui.pref;

import java.io.Serializable;

public class LoggingPreferences implements Serializable, Cloneable {
	private static final long serialVersionUID = 11;

	private int maxFileSize = 5000000;

	public static final int APPEND_TO_GAMES_PGN = 1;

	public static final int SEPERATE_FILE_FOR_EACH_GAME = 2;

	private boolean isLoggingConsole = false;

	private boolean isLoggingChannels = false;

	private boolean isLoggingPersonalTells = false;

	private boolean isLoggingEnabled = false;

	private boolean isLoggingGames = false;

	private int gameLogMode = APPEND_TO_GAMES_PGN;

	public boolean isLoggingConsole() {
		return isLoggingConsole;
	}

	public void setLoggingConsole(boolean isLoggingConsole) {
		this.isLoggingConsole = isLoggingConsole;
	}

	public boolean isLoggingChannels() {
		return isLoggingChannels;
	}

	public void setLoggingChannels(boolean isLoggingIndividualChannels) {
		this.isLoggingChannels = isLoggingIndividualChannels;
	}

	public boolean isLoggingPersonalTells() {
		return isLoggingPersonalTells;
	}

	public void setLoggingPersonalTells(boolean isLoggingPersonalTells) {
		this.isLoggingPersonalTells = isLoggingPersonalTells;
	}

	public static LoggingPreferences getDefault() {
		return new LoggingPreferences();
	}

	public boolean isLoggingEnabled() {
		return isLoggingEnabled;
	}

	public void setLoggingEnabled(boolean isLoggingEnabled) {
		this.isLoggingEnabled = isLoggingEnabled;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cnse) {
			throw new RuntimeException(cnse);
		}
	}

	public int getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(int maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public boolean isLoggingGames() {
		return isLoggingGames;
	}

	public void setLoggingGames(boolean isLoggingGames) {
		this.isLoggingGames = isLoggingGames;
	}

	public int getGameLogMode() {
		return gameLogMode;
	}

	public void setGameLogMode(int gameLogMode) {
		this.gameLogMode = gameLogMode;
	}
}
