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
package decaf.chat;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.gui.GUIManager;
import decaf.gui.pref.ChatPreferences;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.resources.ResourceManagerFactory;
import decaf.sound.SoundManagerFactory;
import decaf.thread.ThreadManager;
import decaf.util.ExtendedListUtil;
import decaf.util.LaunchBrowser;

public class ChatTab extends JPanel implements ClipboardOwner {

	private static final String VALID_PERSON_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private Logger LOGGER = Logger.getLogger(ChatTab.class);

	private StyledDocument document;

	private JScrollPane scrollPane;

	private ChatTextPane textPane = new ChatTextPane();

	private ChatPanel chatPanel;

	private int lastScrollbarValue;

	private int channel;

	private String topic;

	private boolean isMainTab = false;

	private class ChatTextPane extends JTextPane {
		public ChatTextPane() {
			super();
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		public String getToolTipText(MouseEvent arg0) {
			return getTextPaneHoverText(arg0.getPoint(), viewToModel(arg0
					.getPoint()));
		}
	}

	private class CLickListener extends MouseAdapter {

		public void mouseClicked(MouseEvent arg0) {

			int clickMode = chatPanel.getPreferences().getChatPreferences()
					.getPopupMenuClick();

			if ((clickMode == ChatPreferences.LEFT_CLICK_POPUP && SwingUtilities
					.isLeftMouseButton(arg0))
					|| (clickMode == ChatPreferences.RIGHT_CLICK_POPUP && SwingUtilities
							.isRightMouseButton(arg0))
					|| (clickMode == ChatPreferences.MIDDLE_CLICK_POPUP && SwingUtilities
							.isMiddleMouseButton(arg0))) {
				int documentPosition = textPane.viewToModel(arg0.getPoint());
				handleTextPaneClick(arg0.getPoint(), documentPosition);
			}
		}
	}

	public ChatTab(ChatPanel chatPanel) {
		this.chatPanel = chatPanel;
		isMainTab = true;
		channel = -1;
		init();
	}

	public ChatTab(ChatPanel chatPanel, int channel) {
		this.chatPanel = chatPanel;
		init();
		this.channel = channel;
	}

	public ChatTab(ChatPanel chatPanel, String topic) {
		this.chatPanel = chatPanel;
		init();
		this.topic = topic;
		this.channel = -1;
	}

	public int getChannel() {
		return channel;
	}

	public String getTopic() {
		return topic;
	}

	/**
	 * Returns true if this tab is greater than the specified tab.
	 * 
	 * @param tab
	 *            The tab
	 * @return The result
	 */
	public boolean isGreaterThan(ChatTab tab) {
		boolean result = false;

		if (!tab.isMainTab) {
			if (tab.getChannel() != -1 && channel == -1) {
				result = true;
			} else if (tab.getChannel() != -1 && channel != -1) {
				result = channel > tab.channel;
			} else if (tab.getChannel() == -1 && channel == -1) {
				result = getTopic().compareTo(tab.getTopic()) > 0;
			}
		}
		return result;
	}

	private void init() {
		document = new DefaultStyledDocument();
		textPane.setDocument(document);

		textPane.addMouseListener(new CLickListener());
		textPane.setEditable(false);

		scrollPane = new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		textPane.setBackground(chatPanel.getPreferences().getChatPreferences()
				.getTelnetPanelBackground());
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	public boolean isMainTab() {
		return isMainTab;
	}

	public void activate() {
		if (chatPanel.isAutoScrolling()) {
			setScrollBarToMax();
			chatPanel.removeMessagesPending(this);
		}
	}

	public void clearText() {
		try {
			document.remove(0, document.getLength());
		} catch (BadLocationException ble) {
			throw new RuntimeException(ble.getMessage());
		}
	}

	public void appendText(String text, AttributeSet attributes) {
		appendText(text, attributes, true);
	}

	public void appendText(String initialText, final AttributeSet attributes,
			final boolean isAppendingNewline) {
		long startTime = System.currentTimeMillis();

		final AttributeSet attributeSet = attributes;

		if (isAppendingNewline) {
			initialText = initialText + "\r\n";
		}

		final String text = initialText;

		int i = document.getLength() + text.length();

		synchronized (this) {

			if ((isMainTab && i > chatPanel.getPreferences()
					.getChatPreferences().getConsoleTabBufferSize())
					|| (i > chatPanel.getPreferences().getChatPreferences()
							.getChatTabBufferSize())) {
				// removing is expensive so remove 25% chunks at a time.
				int j = (int) (chatPanel.getPreferences().getChatPreferences()
						.getConsoleTabBufferSize() * .25);
				if (j > document.getLength()) {
					j = document.getLength();
				}

				final int finalj = j;
				try {

					document.remove(0, finalj);
				} catch (BadLocationException badlocationexception1) {
				}
			}
			try {

				document.insertString(document.getLength(), text, attributeSet);
			} catch (BadLocationException badlocationexception1) {
			}

			if (chatPanel.isAutoScrolling()) {

				setScrollBarToMax();
			}
		}

		if (!chatPanel.isActive((this))) {
			chatPanel.setMessagesPending(this);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Appended text in "
					+ (System.currentTimeMillis() - startTime) + " ms");
		}
	}

	public void populatePersonalTellsTab(ChatTab tab, String person) {
		int currentPosition = 0;
		boolean isAddingContinuation = false;
		boolean wasLastContinuationTellsYou = false;
		while (currentPosition != -1) {
			String currentLine = getLine(currentPosition);

			// chestutr(41):
			int slashIndex = currentLine.indexOf("\\");

			if (slashIndex == 0 && isAddingContinuation) {
				tab.appendText(currentLine,
						wasLastContinuationTellsYou ? chatPanel
								.getSimpleAttributes(chatPanel.getPreferences()
										.getChatPreferences()
										.getTellTextProperties()) : chatPanel
								.getSimpleAttributes(chatPanel.getPreferences()
										.getChatPreferences()
										.getDefaultTextProperties()));
			} else {
				if (isAddingContinuation) {
					isAddingContinuation = false;
				}

				int tellsYouIndex = currentLine.indexOf(" tells you:");
				int tellIndex = currentLine.indexOf("tell ");
				int tIndex = currentLine.indexOf("t ");
				if (tellsYouIndex != -1) {

					String teller = stripWord(currentLine.substring(0,
							tellsYouIndex));

					if (teller.equalsIgnoreCase(person)) {
						tab.appendText(currentLine, chatPanel
								.getSimpleAttributes(chatPanel.getPreferences()
										.getChatPreferences()
										.getTellTextProperties()));
						isAddingContinuation = true;
						wasLastContinuationTellsYou = true;
					}
				} else if (tellIndex != -1 && tellIndex == 0) {
					int nextSpace = currentLine.indexOf(' ', 5);

					if (nextSpace != -1) {
						String personTold = currentLine.substring(5, nextSpace);
						if (personTold.equalsIgnoreCase(person)) {
							tab.appendText(currentLine, chatPanel
									.getSimpleAttributes(chatPanel
											.getPreferences()
											.getChatPreferences()
											.getDefaultTextProperties()));
							isAddingContinuation = true;
							wasLastContinuationTellsYou = false;
						}
					}
				} else if (tIndex != -1 && tIndex == 0) {
					int nextSpace = currentLine.indexOf(' ', 2);

					if (nextSpace != -1) {
						String personTold = currentLine.substring(2, nextSpace);
						if (personTold.equalsIgnoreCase(person)) {
							tab.appendText(currentLine, chatPanel
									.getSimpleAttributes(chatPanel
											.getPreferences()
											.getChatPreferences()
											.getDefaultTextProperties()));
							isAddingContinuation = true;
							wasLastContinuationTellsYou = false;
						}
					}
				}
			}
			currentPosition = getPositionOnNextLine(currentPosition);
		}
		;
	}

	public StringBuffer getChannelTells(int channel) {
		StringBuffer result = new StringBuffer(2500);
		int currentPosition = 0;
		boolean isAddingContinuation = false;
		while (currentPosition != -1) {
			String currentLine = getLine(currentPosition);

			// chestutr(41):
			int slashIndex = currentLine.indexOf("\\");

			if (slashIndex == 0 && isAddingContinuation) {
				result.append(currentLine + "\r\n");
			} else {
				if (isAddingContinuation) {
					isAddingContinuation = false;
				}

				int spaceIndex = currentLine.indexOf(" ");
				if (spaceIndex != -1) {

					int openParenIndex = currentLine.indexOf("(");
					int closeParenIndex = currentLine.indexOf(")");

					if (openParenIndex != -1 && closeParenIndex != -1
							&& openParenIndex < spaceIndex
							&& closeParenIndex < spaceIndex
							&& openParenIndex < closeParenIndex) {
						try {
							if (Integer.parseInt(currentLine.substring(
									openParenIndex + 1, closeParenIndex)) == channel) {
								result.append(currentLine + "\r\n");
								isAddingContinuation = true;
							}
						} catch (Exception e) {
						}
					}
				}
			}
			currentPosition = getPositionOnNextLine(currentPosition);
		}

		// if its not empty remove the \r\n at the end.
		if (result.length() != 0) {
			result.delete(result.length() - 2, result.length());
		}
		return result;
	}

	private int getChannel(String word) {

		int result = -1;
		if (word != null)
		{
			int openParenIndex = word.lastIndexOf("(");
			int closeParenIndex = word.lastIndexOf(")");
	
			if (openParenIndex != -1 && closeParenIndex != -1
					&& openParenIndex < closeParenIndex) {
				try {
					result = Integer.parseInt(word.substring(openParenIndex + 1,
							closeParenIndex));
				} catch (NumberFormatException nfe) {
					result = -1;
				}
	
			}
		}
		return result;
	}

	private String getTextPaneHoverText(Point point, int documentPosition) {
		String result = null;
		String word = getStrippedWord(documentPosition);

		if (word != null) {
			List<String> tells = chatPanel.getPreviousTells(word);

			if (tells != null && tells.size() > 0) {
				result = "<html>";
				for (int i = 0; i < tells.size(); i++) {
					result += tells.get(i);

					if (i != tells.size() - 1) {
						result += "<br/>";
					}
				}
				result += "</html>";
			}
			return result;
		} else {
			return null;
		}

	}

	private void handleTextPaneClick(Point point, int documentPosition) {
		// A mess and needs to be refactored.
		try {
			final String selectedText = textPane.getSelectedText();

			if (selectedText != null && selectedText.length() > 0) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new AbstractAction("Copy") {
					public void actionPerformed(ActionEvent ae) {
						StringSelection stringSelection = new StringSelection(
								selectedText);
						Clipboard clipboard = Toolkit.getDefaultToolkit()
								.getSystemClipboard();
						clipboard.setContents(stringSelection, ChatTab.this);
					}
				});
				menu.show(textPane, point.x, point.y);
			} else {
				if (!handleUrlClick(point, documentPosition)) {
					String line = getLine(documentPosition);
					String examineCommand = getHistoryJournalCommand(
							documentPosition, line);

					if (examineCommand != null) {
						JPopupMenu menu = buildPopupMenu(examineCommand);
						menu.show(textPane, point.x, point.y);
					} else {
						String soughtCommand = getSoughtCommand(
								documentPosition, line);

						if (soughtCommand != null) {
							JPopupMenu menu = buildPopupMenu(soughtCommand);
							menu.show(textPane, point.x, point.y);
						} else {
							String quotedText = getQuotedText(documentPosition);

							if (quotedText != null) {
								JPopupMenu menu = buildPopupMenu(quotedText);
								menu.show(textPane, point.x, point.y);
							} else {
								final int channel = getChannel(getWord(documentPosition));
								JPopupMenu menu = new JPopupMenu();

								if (channel != -1
										&& chatPanel.getChannelTab(channel) == null
										&& !chatPanel.getPreferences()
												.getChatPreferences()
												.isDisableTabs()) {
									menu.add(new AbstractAction(
											"Add temporary channel tab for "
													+ channel) {
										public void actionPerformed(
												ActionEvent e) {
											ThreadManager
													.execute(new Runnable() {
														public void run() {
															final StringBuffer tells = getChannelTells(channel);
															chatPanel
																	.addChannelTab(
																			channel,
																			tells);
														}
													});
										}
									});
								}
								final String word = getStrippedWord(documentPosition);
								boolean isPerson = isLikelyPerson(word);
								int gameNumber = getGameNumber(line);

								if (isPerson
										&& gameNumber == -1
										&& chatPanel.getConversationTab(word) == null
										&& !chatPanel.getPreferences()
												.getChatPreferences()
												.isDisableTabs()) {
									menu.add(new AbstractAction(
											"Add temporary channel tab for conversations with "
													+ word) {
										public void actionPerformed(
												ActionEvent e) {
											ThreadManager.execute(
													new Runnable() {
														public void run() {
															chatPanel
																	.addConversationTab(
																			word,
																			null);
															populatePersonalTellsTab(
																	chatPanel
																			.getConversationTab(word),
																	word);
														}
													});
										}
									});
									menu.addSeparator();
								}

								if (isPerson || gameNumber != -1) {
									addPopupActions(menu, "PersonPopup",gameNumber,
											isPerson ? word : null);
								} else {
									addPopupActions(menu, "RightClickPopup",-1,null);
								}

								if (menu.getSubElements().length > 0) {
									menu.show(textPane, point.x, point.y);
								} else if (!isMainTab()) {
									menu.show(textPane, point.x, point.y);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Unexpected error", e);
		}
	}

	private boolean handleUrlClick(Point point, int position) {
		final String candidateWord = getWrappedWord(position);
		if (candidateWord != null && candidateWord.startsWith("http://")) {
			JPopupMenu popupMenu = new JPopupMenu();
			popupMenu.add(new AbstractAction("Launch in default browser") {
				public void actionPerformed(ActionEvent e) {
					LaunchBrowser.openURL(candidateWord);
				}
			});
			popupMenu.show(textPane, point.x, point.y);
			return true;
		}
		return false;
	}

	private int getLastNewlineIndex() {
		for (int i = document.getLength(); i >= 0; i -= 80) {
			String s = null;
			int j = i - 80 >= 0 ? i - 80 : 0;
			int k = i - 80 >= 0 ? 80 : i;
			try {
				s = document.getText(j, k);
			} catch (BadLocationException badlocationexception) {
				badlocationexception.printStackTrace();
			}
			int l = s.lastIndexOf("\n");
			if (l != -1)
				return j + l;
		}

		return -1;
	}

	private String[] getPreviousLines(int numberOfLinesToRetrieve,
			int caretPosition) {
		List<String> results = new LinkedList<String>();

		String currentLine = getLine(caretPosition);
		while (currentLine != null && !currentLine.equals("")) {
			results.add(currentLine);
			int prevPosition = getPositionOnPreviousLine(caretPosition);
			if (prevPosition == -1) {
				currentLine = null;
			} else {
				currentLine = getLine(prevPosition);
			}
		}

		return results.toArray(new String[0]);
	}

	private int getPositionOnPreviousLine(int currentPosition) {
		try {
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition > 0 && currentChar != '\n'
					&& currentChar != '\r') {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}
			while (currentPosition > 0
					&& (currentChar == '\n' || currentChar == '\r')) {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}

			return currentPosition < 0 ? 0 : currentPosition;
		} catch (BadLocationException ble) {
			return -1;
		}
	}

	private int getPositionOnNextLine(int currentPosition) {
		try {
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition < document.getLength()
					&& currentChar != '\n' && currentChar != '\r') {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}
			while (currentPosition < document.getLength()
					&& (currentChar == '\n' || currentChar == '\r')) {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}

			return currentPosition == document.getLength() ? -1
					: currentPosition;
		} catch (BadLocationException ble) {
			return -1;
		}
	}

	private String getQuotedText(int caretPosition) {
		try {
			int quoteStart;
			int quoteStop;

			int currentPosition = caretPosition;
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentChar != '\"') {
				if (currentChar == '\r' || currentChar == '\n') {
					return null;
				}
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}

			quoteStart = currentPosition;
			currentPosition = caretPosition;
			currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentChar != '\"') {
				if (currentChar == '\r' || currentChar == '\n') {
					return null;
				}
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}

			quoteStop = currentPosition;

			return document.getText(quoteStart + 1, quoteStop - quoteStart - 1);
		} catch (Exception e) {
			return null;
		}
	}

	private String getLine(int caretPosition) {
		try {
			int lineStart;
			int lineEnd;

			int currentPosition = caretPosition;
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition > 0 && currentChar != '\n'
					&& currentChar != '\r') {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}

			lineStart = currentPosition;

			currentPosition = caretPosition;
			currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition < document.getLength()
					&& currentChar != '\n' && currentChar != '\r') {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}

			lineEnd = currentPosition;

			return document.getText(lineStart + 1, lineEnd - lineStart - 1);
		} catch (Exception e) {
			return "";
		}
	}

	private int getGameNumber(String line) {
		try {
			int gameNumber;

			line = line.trim();
			String[] words = line.split(" ");

			if (words.length > 12 && line.indexOf("[") > 15
					&& line.indexOf("]") > 15 && line.indexOf("(") > 16
					&& line.indexOf(")") > 17 && line.lastIndexOf(":") > 18) {
				return Integer.parseInt(words[0]);
			}

			return -1;

		} catch (Exception e) {
			return -1;
		}
	}

	private boolean isLikelyPerson(String word) {
		if (word != null && word.length() > 2) {
			boolean result = true;
			for (int i = 0; result && i < word.length(); i++) {
				result = VALID_PERSON_CHARS.indexOf(word.charAt(i)) != -1;
			}
			return result;
		} else {
			return false;
		}
	}

	private String getSoughtCommand(int currentPosition, String currentLine) {
		String result = null;
		String[] words = currentLine.trim().split(" ");
		if (words.length >= 7) {
			int gameNumber = -1;
			try {
				gameNumber = Integer.parseInt(words[0]);
			} catch (NumberFormatException nfe) {
				return null;
			}

			// Find out if its really a seek
			int maxLines = 200;
			for (int i = 0; result == null && i < maxLines; i++) {
				currentPosition = getPositionOnNextLine(currentPosition);

				if (currentPosition == -1) {
					return null;
				} else {
					String nextLine = getLine(currentPosition).trim();
					String[] nextLineWords = nextLine.trim().split(" ");

					try {
						Integer.parseInt(words[0]);
					} catch (NumberFormatException nfe) {
						return null;
					}

					if (nextLineWords.length >= 3
							&& nextLineWords[1].equals("ads")
							&& nextLineWords[2].equals("displayed.")) {
						result = "play " + gameNumber;
					} else if (words.length < 7) {
						return null;
					}
				}
			}
		}
		return result;
	}

	private String getHistoryJournalCommand(int currentPosition,
			String currentLine) {
		String result = null;
		String[] words = currentLine.trim().split(" ");
		if (words.length > 17) {
			int gameNumber = -1;
			String historyFor = null;
			boolean isJournal = false;
			if (words[0].startsWith("%") && words[0].endsWith(":")) {
				try {
					gameNumber = Integer.parseInt(words[0].substring(1,
							words[0].length() - 1));
					isJournal = true;
				} catch (NumberFormatException nfe) {
					return null;
				}
			} else if (words[0].endsWith(":")) {
				try {
					gameNumber = Integer.parseInt(words[0].substring(0,
							words[0].length() - 1));
				} catch (NumberFormatException nfe) {
					return null;
				}
			}

			// Find who its the history of.
			int counter = 0;
			int maxLines = isJournal ? 28 : 12;
			for (int i = 0; i < maxLines; i++) {
				currentPosition = getPositionOnPreviousLine(currentPosition);

				if (currentPosition >= 0) {
					String prevLine = getLine(currentPosition).trim();

					if ((!isJournal && prevLine.startsWith("History for"))
							|| (isJournal && prevLine.startsWith("Journal for"))) {

						String[] historyWords = prevLine.split(" ");
						if (historyWords.length == 3) {
							historyFor = historyWords[2].substring(0,
									historyWords[2].length() - 1);
							break;
						}

					}
				} else {
					return null;
				}
			}

			if (historyFor != null) {
				result = "examine " + historyFor + " " + (isJournal ? "%" : "")
						+ gameNumber;
			}

		}
		return result;
	}

	private static String stripWord(String word) {
		if (word != null) {
			StringTokenizer stringtokenizer = new StringTokenizer(word,
					"()~!@?#$%^&*_+|}{'\";/?<>., :[]1234567890\t\r\n");
			if (stringtokenizer.hasMoreTokens())
				return stringtokenizer.nextToken();
			else
				return word;
		}
		return null;
	}

	public void setScrollBarToMax() {

		textPane.setCaretPosition(document.getLength());
		final JScrollBar scrollBar = scrollPane.getVerticalScrollBar();

		if (scrollBar.getValue() >= scrollBar.getMaximum()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scrollBar.setValue(scrollBar.getMaximum());
				}
			});
		}

		if (chatPanel.isActive(this)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					chatPanel.setAutoScrolling(true);
				}
			});
		}
	}

	private JPopupMenu buildPopupMenu(final String string) {
		JPopupMenu menu = new JPopupMenu();
		if (string != null) {
			menu.add(new AbstractAction(string) {
				public void actionPerformed(ActionEvent e) {
					EventService.getInstance().publish(
							new OutboundEvent(string, false));
				}
			});
		}
		return menu;
	}

	private String getStrippedWord(int caretPosition) {
		String word = getWord(caretPosition);

		if (word != null) {
			return stripWord(word);
		} else {
			return null;
		}
	}

	private String getWrappedWord(int position) {
		try {
			String result = null;
			int lineStart;
			int lineEnd;

			int currentPosition = position;
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition > 0 && !Character.isWhitespace(currentChar)) {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}

			lineStart = currentPosition;

			currentPosition = position;
			currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition < document.getLength()
					&& !Character.isWhitespace(currentChar)) {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}
			lineEnd = currentPosition;
			result = document.getText(lineStart + 1, lineEnd - lineStart - 1);

			// now check to see if its a wrap
			while (Character.isWhitespace(currentChar)
					&& currentPosition < document.getLength()) {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}
			while (currentChar == '\\') {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
				while (Character.isWhitespace(currentChar)
						&& currentPosition < document.getLength()) {
					currentChar = document.getText(++currentPosition, 1)
							.charAt(0);
				}

				lineStart = currentPosition - 1;
				while (!Character.isWhitespace(currentChar)
						&& currentPosition < document.getLength()) {
					currentChar = document.getText(++currentPosition, 1)
							.charAt(0);
				}

				lineEnd = currentPosition;
				result += document.getText(lineStart + 1, lineEnd - lineStart
						- 1);

				while (Character.isWhitespace(currentChar)
						&& currentPosition < document.getLength()) {
					currentChar = document.getText(++currentPosition, 1)
							.charAt(0);
				}
			}

			return result;

		} catch (Exception e) {
			return null;
		}
	}

	private String getWord(int position) {
		try {
			int lineStart;
			int lineEnd;

			int currentPosition = position;
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition > 0 && !Character.isWhitespace(currentChar)) {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}

			lineStart = currentPosition;

			currentPosition = position;
			currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition < document.getLength()
					&& !Character.isWhitespace(currentChar)) {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}

			lineEnd = currentPosition;

			return document.getText(lineStart + 1, lineEnd - lineStart - 1);

		} catch (Exception e) {
			return null;
		}
	}

	private void addPopupActions(JPopupMenu menu, String propertiesName, final int obsGameNumber,
			final String person) {
		// Yes this is a mess and needs to definitely be refactored. It is kept
		// as is because its an efficient mess and im lazy.
		if (obsGameNumber != -1) {
			menu.add(new AbstractAction("Observe game " + obsGameNumber) {
				public void actionPerformed(ActionEvent e) {
					EventService.getInstance().publish(
							new OutboundEvent("obs " + obsGameNumber, false));
				}
			});
		} else  {

			int currentButton = 1;

			String currentText = ResourceManagerFactory.getManager().getString(
					propertiesName, currentButton + ".text");
			String isSeperator = ResourceManagerFactory.getManager().getString(
					propertiesName, currentButton + ".isSeperator");
			String subMenu = ResourceManagerFactory.getManager().getString(
					propertiesName, currentButton + ".menu");
			
			while (currentText != null || isSeperator != null
					|| subMenu != null) {
				if (currentText != null) {
					addPersonPopupMenuItem(currentText, person, menu);
				} else if (isSeperator != null && isSeperator.equals("true")) {
					menu.addSeparator();
				} else if (subMenu != null) {
					JMenu newMenu = new JMenu(subMenu);
					buildPersonPopupSubMenu(subMenu, person, newMenu,propertiesName);
					menu.add(newMenu);
				}

				currentButton++;
				currentText = ResourceManagerFactory.getManager().getString(
						propertiesName, currentButton + ".text");
				isSeperator = ResourceManagerFactory.getManager().getString(
						propertiesName, currentButton + ".isSeperator");
				subMenu = ResourceManagerFactory.getManager().getString(
						propertiesName, currentButton + ".menu");
			}

		}
	}

	private void addPersonPopupMenuItem(String command,
			final String dollarReplace, JPopupMenu base) {

		if (command.equals("+extendedCensor")) {
			AbstractAction action = new AbstractAction(command + " "
					+ dollarReplace) {
				public void actionPerformed(ActionEvent e) {
					ExtendedListUtil.add(ExtendedListUtil.ExtendedList.CENSOR,
							dollarReplace);
					appendText(dollarReplace + " added to extended censor.",
							chatPanel.getSimpleAttributes(chatPanel
									.getPreferences().getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("-extendedCensor")) {
			AbstractAction action = new AbstractAction(command + " "
					+ dollarReplace) {
				public void actionPerformed(ActionEvent e) {
					ExtendedListUtil
							.remove(ExtendedListUtil.ExtendedList.CENSOR,
									dollarReplace);
					appendText(
							dollarReplace + " removed from extended censor.",
							chatPanel.getSimpleAttributes(chatPanel
									.getPreferences().getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("Enable Sound")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences().setSoundOn(true);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Sound Enabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("Disable Sound")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences().setSoundOn(false);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Sound Disabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("Enable Speech")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences()
							.getSpeechPreferences().setSpeechEnabled(true);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Speech Enabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("Disable Speech")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences()
							.getSpeechPreferences().setSpeechEnabled(false);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Speech Disabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("Scroll Lock On")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					chatPanel.setAutoScrolling(true);
					appendText("Scroll Lock Enabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("Scroll Lock Off")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					chatPanel.setAutoScrolling(false);
					appendText("Scroll Lock Disabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else if (command.equals("Show Extended Censor List")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					appendText(ExtendedListUtil
							.getContents(ExtendedListUtil.ExtendedList.CENSOR),
							chatPanel.getSimpleAttributes(chatPanel
									.getPreferences().getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(action);

		} else {
			int dollarIndex = command.indexOf("$");

			if (dollarIndex != -1) {
				command = command.substring(0, dollarIndex) + dollarReplace
						+ command.substring(dollarIndex + 1, command.length());
			} else {
				throw new IllegalStateException("PersonPopup command invalid: "
						+ command);
			}
			final String finalString = command;
			AbstractAction action = new AbstractAction(finalString) {
				public void actionPerformed(ActionEvent e) {
					EventService.getInstance().publish(
							new OutboundEvent(finalString, false));
				}
			};
			base.add(action);
		}
	}

	private void addPersonPopupMenuItem(String command,
			final String dollarReplace, JMenu base) {

		if (command.equals("+extendedCensor")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					ExtendedListUtil.add(ExtendedListUtil.ExtendedList.CENSOR,
							dollarReplace);
					appendText(dollarReplace + " added to extended censor.",
							chatPanel.getSimpleAttributes(chatPanel
									.getPreferences().getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("-extendedCensor")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					ExtendedListUtil
							.remove(ExtendedListUtil.ExtendedList.CENSOR,
									dollarReplace);
					appendText(
							dollarReplace + " removed from extended censor.",
							chatPanel.getSimpleAttributes(chatPanel
									.getPreferences().getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("Enable Sound")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences().setSoundOn(true);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Sound Enabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("Disable Sound")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences().setSoundOn(false);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Sound Disabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("Enable Speech")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences()
							.getSpeechPreferences().setSpeechEnabled(true);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Speech Enabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("Disable Speech")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					GUIManager.getInstance().getPreferences()
							.getSpeechPreferences().setSpeechEnabled(false);
					GUIManager.getInstance().setPreferences(
							GUIManager.getInstance().getPreferences());
					appendText("Speech Disabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("Scroll Lock On")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					chatPanel.setAutoScrolling(true);
					appendText("Scroll Lock Enabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("Scroll Lock Off")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					chatPanel.setAutoScrolling(false);
					appendText("Scroll Lock Disabled", chatPanel
							.getSimpleAttributes(chatPanel.getPreferences()
									.getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else if (command.equals("Show Extended Censor List")) {
			AbstractAction action = new AbstractAction(command) {
				public void actionPerformed(ActionEvent e) {
					ExtendedListUtil.add(ExtendedListUtil.ExtendedList.CENSOR,
							dollarReplace);
					appendText(ExtendedListUtil
							.getContents(ExtendedListUtil.ExtendedList.CENSOR),
							chatPanel.getSimpleAttributes(chatPanel
									.getPreferences().getChatPreferences()
									.getAlertTextProperties()));
				}
			};
			base.add(new JMenuItem(action));

		} else {
			int dollarIndex = command.indexOf("$");

			if (dollarIndex != -1) {
				command = command.substring(0, dollarIndex) + dollarReplace
						+ command.substring(dollarIndex + 1, command.length());
			} else {
				throw new IllegalStateException("PersonPopup command invalid: "
						+ command);
			}
			final String finalString = command;
			AbstractAction action = new AbstractAction(finalString) {
				public void actionPerformed(ActionEvent e) {
					EventService.getInstance().publish(
							new OutboundEvent(finalString, false));
				}
			};
			base.add(new JMenuItem(action));
		}
	}

	private void buildPersonPopupSubMenu(String menuName, String person,
			JMenu baseMenu,String propertiesName) {
		int currentButton = 1;
		String currentText = ResourceManagerFactory.getManager().getString(
				propertiesName, menuName + "." + currentButton + ".text");
		String isSeperator = ResourceManagerFactory.getManager().getString(
				propertiesName, menuName + "." + currentButton + ".isSeperator");
		String subMenu = ResourceManagerFactory.getManager().getString(
				propertiesName, menuName + "." + currentButton + ".menu");

		while (currentText != null || isSeperator != null || subMenu != null) {
			if (currentText != null) {
				addPersonPopupMenuItem(currentText, person, baseMenu);
			} else if (isSeperator != null && isSeperator.equals("true")) {
				baseMenu.addSeparator();
			} else if (subMenu != null) {
				JMenu menu = new JMenu(subMenu);
				buildPersonPopupSubMenu(subMenu, person, menu,propertiesName);
				baseMenu.add(menu);
			}

			currentButton++;
			currentText = ResourceManagerFactory.getManager().getString(
					propertiesName, menuName + "." + currentButton + ".text");
			isSeperator = ResourceManagerFactory.getManager().getString(
					propertiesName,
					menuName + "." + currentButton + ".isSeperator");
			subMenu = ResourceManagerFactory.getManager().getString(
					propertiesName, menuName + "." + currentButton + ".menu");
		}
	}

	public void search(String text, boolean isSearchingForwards) {
		int result = isSearchingForwards ? searchForwards(text)
				: searchBackwards(text);
		if (result == -1) {
			appendText("\"" + text + "\" not found.", chatPanel
					.getSimpleAttributes(chatPanel.getPreferences()
							.getChatPreferences().getAlertTextProperties()));
		} else {
			chatPanel.setAutoScrolling(false);
			// textPane.setSelectedTextColor(new Color(0, 0, 0, 15));
			textPane.select(result, result + text.length());
			textPane.getHighlighter().removeAllHighlights();

			try {
				scrollPane.scrollRectToVisible(textPane.modelToView(result));
				textPane.getHighlighter().addHighlight(result,
						result + text.length(),
						DefaultHighlighter.DefaultPainter);

			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

	private int searchBackwards(String string) {
		int currentPosition = textPane.getCaretPosition();
		int length = string.length();

		while (currentPosition - length > 0) {
			try {
				if (document.getText(currentPosition - length, length)
						.equalsIgnoreCase(string)) {
					return currentPosition - length;

				}
			} catch (BadLocationException ble) {
				return -1;
			}

			currentPosition--;
		}
		return -1;
	}

	private int searchForwards(String string) {
		int currentPosition = textPane.getCaretPosition();
		int length = string.length();

		while (currentPosition + length < document.getLength()) {
			try {
				if (document.getText(currentPosition, length).equalsIgnoreCase(
						string)) {
					return currentPosition;

				}
			} catch (BadLocationException ble) {
				return -1;
			}

			currentPosition++;
		}
		return -1;
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// TODO Auto-generated method stub

	}

}
