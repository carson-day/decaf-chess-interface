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

package decaf.gui.pref;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

public class Preferences implements Serializable, Cloneable {

	private static final long serialVersionUID = 5;

	private static final Logger LOGGER = Logger.getLogger(Preferences.class);

	static final String OPTIONS_PATH = "properties/preferences.object";

	private String lookAndFeelClassName = UIManager
			.getSystemLookAndFeelClassName();

	BughousePreferences bughousePreferences;

	BoardPreferences boardPreferences;

	LoginPreferences loginPreferences;

	ChatPreferences chatPreferences;

	SpeechPreferences speechPreferences;

	Object growingRoom;

	private Preferences() {

	}

	public static Preferences loadPreferences() {

		Preferences result = null;
		try {
			ObjectInputStream objectIn = new ObjectInputStream(
					new FileInputStream(OPTIONS_PATH));
			result = (Preferences) objectIn.readObject();
			objectIn.close();
		} catch (Throwable ioe) {
			LOGGER.warn("Could not load preferences: " + OPTIONS_PATH
					+ " Loading default preferences.");
			result = getDefault();
		}
		return result;
	}

	public static Preferences getDefault() {
		Preferences result = new Preferences();
		result.bughousePreferences = BughousePreferences.getDefault();
		result.chatPreferences = ChatPreferences.getDefault();
		result.boardPreferences = BoardPreferences.getDefault();
		result.loginPreferences = LoginPreferences.getDefault();
		result.speechPreferences = SpeechPreferences.getDefault();
		return result;
	}

	public void save() {
		try {
			ObjectOutputStream objOut = new ObjectOutputStream(
					new FileOutputStream(OPTIONS_PATH));
			objOut.writeObject(this);
			objOut.close();

		} catch (Exception ioe) {
			LOGGER.error("Error occured saving options: " + OPTIONS_PATH, ioe);
			ioe.printStackTrace();
		}
	}

	public Preferences getDeepCopy() {
		try {
			Preferences result = (Preferences) this.clone();
			result.bughousePreferences = (BughousePreferences) bughousePreferences
					.clone();
			result.chatPreferences = (ChatPreferences) chatPreferences.clone();
			result.boardPreferences = (BoardPreferences) boardPreferences
					.clone();
			result.loginPreferences = (LoginPreferences) loginPreferences
					.clone();
			result.speechPreferences = (SpeechPreferences) speechPreferences
					.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			LOGGER.error(e);
			throw new RuntimeException(e.getMessage());
		}
	}

	public BoardPreferences getBoardPreferences() {
		return boardPreferences;
	}

	public void setBoardPreferences(BoardPreferences boardPreferences) {
		this.boardPreferences = boardPreferences;
	}

	public BughousePreferences getBughousePreferences() {
		return bughousePreferences;
	}

	public void setBughousePreferences(BughousePreferences bughousePreferences) {
		this.bughousePreferences = bughousePreferences;
	}

	public ChatPreferences getChatPreferences() {
		return chatPreferences;
	}

	public void setChatPreferences(ChatPreferences chatPreferences) {
		this.chatPreferences = chatPreferences;
	}

	public LoginPreferences getLoginPreferences() {
		return loginPreferences;
	}

	public void setLoginPreferences(LoginPreferences loginPreferences) {
		this.loginPreferences = loginPreferences;
	}

	public String getLookAndFeelClassName() {
		return lookAndFeelClassName;
	}

	public void setLookAndFeelClassName(String lookAndFeelClassName) {
		this.lookAndFeelClassName = lookAndFeelClassName;
	}

	public SpeechPreferences getSpeechPreferences() {
		return speechPreferences;
	}

	public void setSpeechPreferences(SpeechPreferences speechPreferences) {
		this.speechPreferences = speechPreferences;
	}

}