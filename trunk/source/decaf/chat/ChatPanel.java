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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;

import decaf.event.Event;
import decaf.event.EventService;
import decaf.event.Filter;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.GUIManager;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.widgets.Disposable;
import decaf.gui.widgets.KeyMapper;
import decaf.gui.widgets.PingLabel;
import decaf.gui.widgets.bugseek.BugSeekFrame;
import decaf.gui.widgets.seekgraph.SeekFrame;
import decaf.messaging.ics.nongameparser.ParserUtil;
import decaf.messaging.inboundevent.IcsInboundEvent;
import decaf.messaging.inboundevent.chat.CShoutEvent;
import decaf.messaging.inboundevent.chat.ChannelTellEvent;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.chat.KibitzEvent;
import decaf.messaging.inboundevent.chat.PartnerTellEvent;
import decaf.messaging.inboundevent.chat.ShoutEvent;
import decaf.messaging.inboundevent.chat.TellEvent;
import decaf.messaging.inboundevent.chat.WhisperEvent;
import decaf.messaging.inboundevent.inform.BugClosedEvent;
import decaf.messaging.inboundevent.inform.BugOpenEvent;
import decaf.messaging.inboundevent.inform.BugWhoGEvent;
import decaf.messaging.inboundevent.inform.BugWhoPEvent;
import decaf.messaging.inboundevent.inform.BugWhoUEvent;
import decaf.messaging.inboundevent.inform.ChallengeEvent;
import decaf.messaging.inboundevent.inform.ClosedEvent;
import decaf.messaging.inboundevent.inform.FollowingEvent;
import decaf.messaging.inboundevent.inform.MoveListEvent;
import decaf.messaging.inboundevent.inform.NotFollowingEvent;
import decaf.messaging.inboundevent.inform.NotificationEvent;
import decaf.messaging.inboundevent.inform.OpenEvent;
import decaf.messaging.inboundevent.inform.PartnershipCreatedEvent;
import decaf.messaging.inboundevent.inform.PartnershipEndedEvent;
import decaf.messaging.inboundevent.inform.SoughtEvent;
import decaf.messaging.outboundevent.BugOpenRequestEvent;
import decaf.messaging.outboundevent.OpenRequestEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.resources.ResourceManagerFactory;
import decaf.sound.SoundKeys;
import decaf.sound.SoundManagerFactory;
import decaf.speech.NoSpeechEnabled;
import decaf.speech.SpeechManager;
import decaf.thread.ThreadManager;
import decaf.util.ExtendedListUtil;
import decaf.util.PropertiesConstants;
import decaf.util.TextProperties;
import decaf.util.ToolbarUtil;

public class ChatPanel extends JPanel implements ActionListener,
		Preferenceable, Disposable, ClipboardOwner {

	private class BugOpenActionListener implements ActionListener {

		private BugOpenActionListener() {
		}

		public void actionPerformed(ActionEvent actionevent) {
			EventService.getInstance().publish(
					new BugOpenRequestEvent(!isBugOpen, true));
		}

	}

	private class ExposedJTextField extends JTextField {

		public void processKeyEvent(KeyEvent arg0) {
			// TODO Auto-generated method stub
			super.processKeyEvent(arg0);
		}

	}

	/**
	 * Handles up and down input history. Also handles setting focus to text
	 * pane if a page up or page down is pressed.
	 */
	private class InputFieldKeyHistoryListener extends KeyAdapter {

		public void keyPressed(KeyEvent keyEvent) {
			inputFieldKeyTyped(keyEvent);

		}
	}

	private class OpenActionListener implements ActionListener {

		private OpenActionListener() {
		}

		public void actionPerformed(ActionEvent actionevent) {
			EventService.getInstance().publish(
					new OpenRequestEvent(!isOpen, true));
		}

	}

	private class ScrollLockActionListener implements ActionListener {

		private ScrollLockActionListener() {
		}

		public void actionPerformed(ActionEvent actionevent) {
			setAutoScrolling(!isAutoScrolling);
		}
	}

	private class ShowBughActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (lastBugSeekFrame != null && lastBugSeekFrame.isVisible()) {
				lastBugSeekFrame.toFront();
			} else {
				lastBugSeekFrame = new BugSeekFrame();
				GUIManager.getInstance().addKeyForwarder(lastBugSeekFrame);
				lastBugSeekFrame.setVisible(true);
			}
		}

	}

	private class ShowSeekGraphActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (lastSeekFrame != null && lastSeekFrame.isVisible()) {
				lastSeekFrame.toFront();
			} else {
				lastSeekFrame = new SeekFrame();
				GUIManager.getInstance().addKeyForwarder(lastSeekFrame);
				lastSeekFrame.setVisible(true);
			}
		}

	}

	public class TextReceivedEventSubscriber implements Subscriber, Filter {

		public TextReceivedEventSubscriber() {
			EventService.getInstance().subscribe(
					new Subscription(IcsNonGameEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(OpenEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(ClosedEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(BugOpenEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(BugClosedEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(CShoutEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(ChannelTellEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(KibitzEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(WhisperEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(PartnerTellEvent.class, this, this));
			EventService.getInstance()
					.subscribe(
							new Subscription(PartnershipCreatedEvent.class,
									this, this));
			EventService.getInstance().subscribe(
					new Subscription(ChallengeEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(NotificationEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(PartnershipEndedEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(ShoutEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(TellEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(SoughtEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(BugWhoPEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(BugWhoUEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(BugWhoGEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(MoveListEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(FollowingEvent.class, this, this));
			EventService.getInstance().subscribe(
					new Subscription(NotFollowingEvent.class, this, this));

		}

		public boolean apply(Event event) {
			return true;
		}

		public void inform(BugClosedEvent bugclosedevent) {
			getMainTab().appendText(
					bugclosedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setBugOpen(false);

		}

		public void inform(BugOpenEvent bugopenevent) {
			getMainTab().appendText(
					bugopenevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setBugOpen(true);

		}

		public void inform(BugWhoGEvent bugWhoGEvent) {
			if (!bugWhoGEvent.isHideFromUser()) {
				getMainTab().appendText(
						bugWhoGEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));

			}
		}

		public void inform(BugWhoPEvent bugWhoPEvent) {
			if (!bugWhoPEvent.isHideFromUser()) {
				getMainTab().appendText(
						bugWhoPEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));

			}
		}

		public void inform(BugWhoUEvent bugWhoIEvent) {
			if (!bugWhoIEvent.isHideFromUser()) {
				getMainTab().appendText(
						bugWhoIEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));

			}
		}

		public void inform(ChallengeEvent challengeEvent) {
			getMainTab().appendText(
					challengeEvent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getMatchTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingNotifications()) {
				SpeechManager.getInstance().getSpeech().speak("Challenge");
			}

			if (SpeechManager.getInstance().getSpeech() instanceof NoSpeechEnabled
					|| !preferences.getSpeechPreferences().isSpeechEnabled()
					|| !preferences.getSpeechPreferences()
							.isSpeakingNotifications()) {
				SoundManagerFactory.getInstance()
						.playSound(SoundKeys.ALERT_KEY);
			}

		}

		public void inform(ChannelTellEvent channeltellevent) {

			String text = channeltellevent.getText();
			AttributeSet attributes = getSimpleAttributes(getPreferences()
					.getChatPreferences().getChannelProperties(
							channeltellevent.getChannel()));

			getMainTab().appendText(text, attributes);

			ChatTab channelTab = getChannelTab(channeltellevent.getChannel());

			if (channelTab != null) {
				channelTab.appendText(text, attributes);
			}

		}

		public void inform(ClosedEvent closedevent) {
			getMainTab().appendText(
					closedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setOpen(false);

		}

		public void inform(CShoutEvent cshoutevent) {

			getMainTab().appendText(
					cshoutevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getCshoutTextProperties()));
		}

		public void inform(FollowingEvent followingEvent) {
			if (!followingEvent.isHideFromUser()) {
				getMainTab().appendText(
						followingEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));

			}
		}

		public void inform(IcsNonGameEvent event) {
			getMainTab().appendText(
					event.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(KibitzEvent kibitzevent) {
			if (!isExtendedCensor(kibitzevent.getKibitzer())) {
				getMainTab()
						.appendText(
								kibitzevent.getText(),
								getSimpleAttributes(getPreferences()
										.getChatPreferences()
										.getKibitzTextProperties()));

				if (preferences.getBoardPreferences().isShowingGameCaptions()) {
					GUIManager.getInstance().showCaption(
							kibitzevent.getKibitzer(), kibitzevent.getText());
				}
			}
		}

		public void inform(MoveListEvent moveListEvent) {
			if (!moveListEvent.isHideFromUser()) {
				getMainTab().appendText(
						moveListEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));

			}
		}

		public void inform(NotFollowingEvent notFollowingEvent) {
			if (!notFollowingEvent.isHideFromUser()) {
				getMainTab().appendText(
						notFollowingEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));

			}
		}

		public void inform(NotificationEvent notificationEvent) {
			getMainTab().appendText(
					notificationEvent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getNotificationTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingNotifications()) {

				String text = notificationEvent.getText();

				int notIndex = text.indexOf("Notification:");

				if (notIndex != -1) {
					text = text.substring(notIndex + "Notification:".length(),
							text.length());
				}

				SpeechManager.getInstance().getSpeech().speak(text);
			}

			if (SpeechManager.getInstance().getSpeech() instanceof NoSpeechEnabled
					|| !preferences.getSpeechPreferences().isSpeechEnabled()
					|| !preferences.getSpeechPreferences()
							.isSpeakingNotifications()) {
				SoundManagerFactory.getInstance()
						.playSound(SoundKeys.ALERT_KEY);
			}
		}

		public void inform(OpenEvent openevent) {
			getMainTab().appendText(
					openevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setOpen(true);
		}

		public void inform(PartnershipCreatedEvent partnershipcreatedevent) {

			try {
				getMainTab().appendText(
						partnershipcreatedevent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void inform(PartnershipEndedEvent partnershipendedevent) {
			getMainTab().appendText(
					partnershipendedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingNotifications()) {
				SpeechManager.getInstance().getSpeech().speak(
						"Partnership ended");
			}

		}

		public void inform(PartnerTellEvent partnertellevent) {
			getMainTab().appendText(
					partnertellevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getPtellTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingPtells()) {
				SpeechManager.getInstance().getSpeech().speak(
						tellEventToSpeechString(partnertellevent));
			}

			if (preferences.getBughousePreferences().isShowingPartnerCaptions()) {
				GUIManager.getInstance().showPartnerCaption(
						partnertellevent.getText());
			}

			if (SpeechManager.getInstance().getSpeech() instanceof NoSpeechEnabled
					|| !preferences.getSpeechPreferences().isSpeechEnabled()
					|| !preferences.getSpeechPreferences().isSpeakingPtells()) {
				SoundManagerFactory.getInstance().playSound(SoundKeys.CHAT_KEY);
			}

		}

		public void inform(ShoutEvent shoutevent) {
			if (!isExtendedCensor(shoutevent.getShouter())) {
				getMainTab()
						.appendText(
								shoutevent.getText(),
								getSimpleAttributes(getPreferences()
										.getChatPreferences()
										.getShoutTextProperties()));
			}
		}

		public void inform(SoughtEvent soughtEvent) {
			if (!soughtEvent.isHideFromUser()) {
				getMainTab().appendText(
						soughtEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));

			}
		}

		public void inform(TellEvent tellEvent) {
			if (!isExtendedCensor(tellEvent.getSender())) {
				getMainTab().appendText(
						tellEvent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences().getTellTextProperties()));
				if (preferences.getSpeechPreferences().isSpeakingTells()
						&& tellEvent.getText().indexOf("http://") == -1) {
					SpeechManager.getInstance().getSpeech().speak(
							tellEventToSpeechString(tellEvent));
				}

				addLastTellSender(tellEvent.getSender());

				LinkedList<String> previousTellList = previousTells
						.get(tellEvent.getSender());
				if (previousTellList == null) {
					previousTellList = new LinkedList<String>();
					previousTells.put(tellEvent.getSender(), previousTellList);
				}

				if (previousTellList.size() > MAX_PREVIOUS_TELLS) {
					previousTellList.removeFirst();
				}

				previousTellList.addLast(tellEvent.getText());

				if (preferences.getBoardPreferences().isShowingGameCaptions()) {
					GUIManager.getInstance().showCaption(tellEvent.getSender(),
							tellEvent.getText());
				}

				ChatTab conversationTab = getConversationTab(tellEvent
						.getSender());
				if (conversationTab != null) {
					conversationTab.appendText(tellEvent.getText(),
							getSimpleAttributes(getPreferences()
									.getChatPreferences()
									.getTellTextProperties()));
				}

				if (SpeechManager.getInstance().getSpeech() instanceof NoSpeechEnabled
						|| !preferences.getSpeechPreferences()
								.isSpeechEnabled()
						|| !preferences.getSpeechPreferences()
								.isSpeakingTells()) {
					SoundManagerFactory.getInstance().playSound(
							SoundKeys.CHAT_KEY);
				}
			} else {
				EventService.getInstance().publish(
						new OutboundEvent("tell "
								+ ParserUtil
										.removeTitles(tellEvent.getSender())
								+ " You are on my extended censor list."));
			}
		}

		public void inform(WhisperEvent whisperevent) {

			if (!isExtendedCensor(whisperevent.getWhisperer())) {
				getMainTab().appendText(
						whisperevent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getWhisperTextProperties()));

				if (preferences.getBoardPreferences().isShowingGameCaptions()) {
					GUIManager.getInstance()
							.showCaption(whisperevent.getWhisperer(),
									whisperevent.getText());
				}
			}
		}
	}

	private Logger LOGGER = Logger.getLogger(ChatPanel.class);

	private static final int MAX_PREVIOUS_TELLS = 20;

	private static final String SCROLL_LOCK_TOOLTIP = "Scroll Lock: Click to toggle on/off.";

	private static final String OPEN_TOOLTIP = "Open: Click to toggle on/off.";

	private static final String BUGOPEN_TOOLTIP = "Bug Open: Click to toggle on/off.";

	private JToolBar toolbar = new JToolBar();

	private JPanel inputPanel;

	private Map<TextProperties, SimpleAttributeSet> textPrefsToAttributesCache = new HashMap<TextProperties, SimpleAttributeSet>();

	private JLabel inputPrompt;

	private JCheckBox slEnabled;

	private JCheckBox openEnabled;

	private JCheckBox bugOpenEnabled;

	private Preferences preferences;

	private ExposedJTextField inputField = new ExposedJTextField();

	private java.util.List<String> previousInput;

	private Subscriber subscriber;

	private JTabbedPane tabbedPane = null;

	private JButton removeTabButton = new JButton(new AbstractAction(
			"Remove Tab") {
		public void actionPerformed(ActionEvent e) {
			if (!getCurrentTab().isMainTab()) {
				removeTab(getCurrentTab());
			}
		}
	});

	private int lastPreviousInputIndex;

	private boolean isAutoScrolling;

	private boolean isBugOpen;

	private boolean isOpen;

	private boolean isInputLineOnTop;

	private PingLabel pingLabel;

	private String lastSearch;

	private boolean isTabbingDisabled;

	private JButton showSeekGraph;

	private JButton showBugSeek;

	private ChatTab mainTab;

	private LinkedList<String> lastTells = new LinkedList<String>();

	private int lastTellsIndex = 0;

	private KeyMapper keyMapper = new KeyMapper();

	private JSplitPane tabSplitPane;

	private SeekFrame lastSeekFrame;

	private JFrame lastBugSeekFrame;

	private JCheckBox prependTellText = new JCheckBox("Prepend Tell Text");

	private HashMap<String, LinkedList<String>> previousTells = new HashMap<String, LinkedList<String>>();

	public ChatPanel(Preferences preferences) {
		this.preferences = preferences;
		initControls();
		subscribe();
	}

	public void actionPerformed(ActionEvent actionevent) {
		String s = inputField.getText();
		if (s == null) {
			s = "";
		}

		EventService.getInstance().publish(new OutboundEvent(s));
		inputField.setText("");
		previousInput.add(s);
		lastPreviousInputIndex = previousInput.size();

		if (s.startsWith("t") || s.startsWith("tell")) {
			StringTokenizer tok = new StringTokenizer(s, " ");
			if (tok.hasMoreTokens()) {
				tok.nextToken();
				if (tok.hasMoreTokens()) {
					final String personTold = tok.nextToken();
					final ChatTab conversationTab = getConversationTab(personTold);
					final String finalS = s;
					if (conversationTab != null) {
						ThreadManager.execute(new Runnable() {
							public void run() {
								conversationTab.appendText(finalS,
										getSimpleAttributes(getPreferences()
												.getChatPreferences()
												.getDefaultTextProperties()));
							}
						});
					}
				}
			}
		}
		lastTellsIndex = lastTells.size() - 1;
		prependInputText();
	}

	public void addChannelTab(int channel) {
		addChannelTab(channel, null);
	}

	public void addChannelTab(int channel, StringBuffer buffer) {
		ChatTab newTab = new ChatTab(this, channel);
		addTab(newTab);

		if (buffer != null) {
			AttributeSet attributes = getSimpleAttributes(getPreferences()
					.getChatPreferences().getChannelProperties(channel));
			getChannelTab(channel).appendText(buffer.toString(), attributes);
		}
	}

	public void addConversationTab(String person, StringBuffer buffer) {
		ChatTab newTab = new ChatTab(this, person);
		addTab(newTab);

		if (buffer != null) {
			AttributeSet attributes = getSimpleAttributes(getPreferences()
					.getChatPreferences().getTellTextProperties());

			getConversationTab(person)
					.appendText(buffer.toString(), attributes);
		}
	}

	private void addLastTellSender(String name) {
		synchronized (this) {
			lastTells.remove(name);
			if (lastTells.size() >= MAX_PREVIOUS_TELLS) {
				lastTells.removeFirst();
			}
			lastTells.addLast(name);
			lastTellsIndex = lastTells.size() - 1;
		}

	}

	public void addTab(ChatTab tab) {

		if (tabbedPane == null) {
			setupSplitPaneMode();
		}

		int index = -1;
		for (int i = 0; index == -1 && i < tabbedPane.getTabCount(); i++) {
			if (getTabAt(i).isGreaterThan(tab)) {
				index = i;
			}
		}

		if (index == -1) {
			index = tabbedPane.getTabCount();
		}

		tabbedPane.add(tab, tab.getChannel() != -1 ? "" + tab.getChannel()
				: tab.getTopic(), index);

		GUIManager.getInstance().addKeyForwarder(tab);

	}

	public void dispose() {
		removeAll();
		slEnabled = null;
		openEnabled = null;
		bugOpenEnabled = null;
		preferences = null;
		preferences = null;
		inputField = null;
		if (previousInput != null) {
			previousInput.clear();
			previousInput = null;
		}
		if (subscriber != null) {
			EventService.getInstance().unsubscribe(
					new Subscription(IcsInboundEvent.class, null, subscriber));
			subscriber = null;
		}
	}

	public void forwardKeyEvent(KeyEvent keyEvent) {
		inputField.setText(inputField.getText() + keyEvent.getKeyChar());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// For OSX Jaguar (unselect all text for some reason it selects
				// it.)
				inputField.select(inputField.getDocument().getLength(),
						inputField.getDocument().getLength());
			}
		});
	}

	public ChatTab getChannelTab(int channel) {
		if (isTabbingDisabled || tabbedPane == null) {
			return null;
		} else {
			ChatTab result = null;
			for (int i = 0; result == null
					&& i < tabbedPane.getComponentCount(); i++) {
				ChatTab current = getChatTab(i);

				if (current.getChannel() == channel) {
					result = current;
				}
			}
			return result;
		}
	}

	public ChatTab getChatTab(int index) {
		if (isTabbingDisabled || tabbedPane == null) {
			return mainTab;
		} else {
			return (ChatTab) tabbedPane.getComponentAt(index);
		}
	}

	public ChatTab getConversationTab(String person) {
		if (isTabbingDisabled || tabbedPane == null) {
			return null;
		} else if (person != null) {
			ChatTab result = null;
			for (int i = 1; result == null
					&& i < tabbedPane.getComponentCount(); i++) {
				ChatTab current = getChatTab(i);

				if (current.getTopic() != null
						&& current.getTopic().equalsIgnoreCase(person)) {
					result = current;
				}
			}
			return result;
		} else {
			return null;
		}

	}

	public ChatTab getCurrentTab() {
		if (isTabbingDisabled || tabbedPane == null) {
			return mainTab;
		} else {
			return (ChatTab) tabbedPane.getSelectedComponent();
		}
	}

	public JTextField getInputField() {
		return inputField;
	}

	public ChatTab getMainTab() {
		return mainTab;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public List<String> getPreviousTells(String name) {
		return previousTells.get(name);
	}

	public SimpleAttributeSet getSimpleAttributes(TextProperties properties) {
		SimpleAttributeSet result = textPrefsToAttributesCache.get(properties);

		if (result == null) {
			result = new SimpleAttributeSet();
			StyleConstants.setBackground(result, getPreferences()
					.getChatPreferences().getTelnetPanelBackground());
			StyleConstants.setFontSize(result, properties.getFont().getSize());
			StyleConstants.setFontFamily(result, properties.getFont()
					.getFamily());
			StyleConstants.setForeground(result, properties.getForeground());
			textPrefsToAttributesCache.put(properties, result);
		}

		return result;
	}

	public ChatTab getTabAt(int index) {
		if (isTabbingDisabled || tabbedPane == null) {
			return mainTab;
		} else {
			return (ChatTab) tabbedPane.getComponentAt(index);
		}
	}

	private void initControls() {
		inputPrompt = new JLabel(ResourceManagerFactory.getManager().getString(
				PropertiesConstants.DECAF_PROPERTIES,
				PropertiesConstants.PROMPT)
				+ " ");

		previousInput = new LinkedList<String>();
		inputField.addActionListener(this);
		inputField.addKeyListener(new InputFieldKeyHistoryListener());
		inputField.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				final JPopupMenu popupMenu = new JPopupMenu();

				if (SwingUtilities.isRightMouseButton(arg0)) {

					final String selectedText = inputField.getSelectedText();

					if (selectedText != null && selectedText.length() != 0) {
						popupMenu.add(new AbstractAction("Copy") {

							public void actionPerformed(ActionEvent e) {
								StringSelection stringSelection = new StringSelection(
										selectedText);
								Clipboard clipboard = Toolkit
										.getDefaultToolkit()
										.getSystemClipboard();
								clipboard.setContents(stringSelection,
										ChatPanel.this);
							}
						});
					}

					Clipboard clipboard = Toolkit.getDefaultToolkit()
							.getSystemClipboard();
					Transferable contents = clipboard.getContents(null);
					boolean hasTransferableText = (contents != null)
							&& contents
									.isDataFlavorSupported(DataFlavor.stringFlavor);

					if (hasTransferableText) {
						try {
							final String clippedString = (String) contents
									.getTransferData(DataFlavor.stringFlavor);

							if (clippedString != null
									&& clippedString.length() > 0) {
								popupMenu.add(new AbstractAction("Paste") {
									public void actionPerformed(
											ActionEvent event) {
										try {
											inputField
													.getDocument()
													.insertString(
															inputField
																	.getCaretPosition(),
															clippedString, null);
										} catch (BadLocationException e) {
											LOGGER.warn(e);
										}
									}
								});
							}

						} catch (UnsupportedFlavorException ex) {
							LOGGER.warn(ex);
						} catch (IOException ex) {
							LOGGER.warn(ex);
						}
					}
				}

				if (popupMenu.getSubElements() != null
						&& popupMenu.getSubElements().length > 0) {
					popupMenu.show(inputField, arg0.getX(), arg0.getY());
				}
			}
		});
		isTabbingDisabled = preferences.getChatPreferences().isDisableTabs();
		mainTab = new ChatTab(this);

		removeTabButton.setEnabled(false);
		removeTabButton.setVisible(false);
		prependTellText.setEnabled(false);
		prependTellText.setVisible(false);

		pingLabel = new PingLabel(preferences);

		bugOpenEnabled = new JCheckBox("Bug Open");
		openEnabled = new JCheckBox("Open ");
		slEnabled = new JCheckBox("SL");

		slEnabled.setToolTipText(SCROLL_LOCK_TOOLTIP);
		openEnabled.setToolTipText(OPEN_TOOLTIP);
		bugOpenEnabled.setToolTipText(BUGOPEN_TOOLTIP);

		slEnabled.addActionListener(new ScrollLockActionListener());
		openEnabled.addActionListener(new OpenActionListener());
		bugOpenEnabled.addActionListener(new BugOpenActionListener());

		showSeekGraph = new JButton("Seek Graph");
		showSeekGraph.setToolTipText("Shows available seeks in a graph.");
		showSeekGraph.addActionListener(new ShowSeekGraphActionListener());

		showBugSeek = new JButton("Bug Seek");
		showBugSeek.setToolTipText("Shows available bughouse teams.");
		showBugSeek.addActionListener(new ShowBughActionListener());

		toolbar.add(slEnabled);
		toolbar.addSeparator(new Dimension(5, 1));
		toolbar.add(openEnabled);
		if (preferences.getChatPreferences().isShowingBugOpenCheckbox()) {
			toolbar.addSeparator(new Dimension(5, 1));
			toolbar.add(bugOpenEnabled);
		}
		if (preferences.getChatPreferences().isShowingSeekGraphButton()) {
			toolbar.addSeparator(new Dimension(5, 1));
			toolbar.add(showSeekGraph);
		}
		if (preferences.getChatPreferences().isShowingBugSeekButton()) {
			toolbar.addSeparator(new Dimension(5, 1));
			toolbar.add(showBugSeek);
		}
		toolbar.addSeparator(new Dimension(20, 1));

		ToolbarUtil.addToolbarButtonsFromProperties("ChatToolbar", toolbar, 20,
				5);

		inputPanel = new JPanel();

		setupInputPanelLayout();

		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		add(mainTab, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);

		setPreferences(preferences);

		setAutoScrolling(true);
		setOpen(false);
		setBugOpen(false);
	}

	private void inputFieldKeyTyped(KeyEvent keyEvent) {
		int keyCode = keyEvent.getKeyCode();
		if (!keyMapper.process(keyEvent)) {
			if (keyEvent.getKeyCode() == 38 && lastPreviousInputIndex > 0) {
				lastPreviousInputIndex--;
				inputField.setText(""
						+ previousInput.get(lastPreviousInputIndex));
			} else if (keyEvent.getKeyCode() == 40) {
				if (lastPreviousInputIndex < previousInput.size() - 1) {
					lastPreviousInputIndex++;
					inputField.setText(""
							+ previousInput.get(lastPreviousInputIndex));
				} else {
					lastPreviousInputIndex = previousInput.size();
					inputField.setText("");
				}
			} else if (keyCode == KeyEvent.VK_ESCAPE) {
				if (inputField.getText().equals("")) {
					prependInputText();
				} else {
					inputField.setText("");
				}
				GUIManager.getInstance().clearPremove();
			} else if (keyCode == KeyEvent.VK_SCROLL_LOCK) {
				setAutoScrolling(!isAutoScrolling());
			}
		}
	}

	public boolean isActive(ChatTab tab) {
		if (isTabbingDisabled) {
			return tab == mainTab;
		} else if (tab == mainTab) {
			return true;
		} else {
			return (ChatTab) tabbedPane.getSelectedComponent() == tab;
		}
	}

	public boolean isAutoScrolling() {
		return isAutoScrolling;
	}

	private boolean isEndOrHome(int keyCode) {
		return keyCode == KeyEvent.VK_HOME || keyCode == KeyEvent.VK_END;
	}

	public boolean isExtendedCensor(String name) {
		return ExtendedListUtil.contains(ExtendedListUtil.ExtendedList.CENSOR,
				ParserUtil.removeTitles(name));
	}

	public boolean isInputLineOnTop() {
		return isInputLineOnTop;
	}

	private boolean isPageUpOrDown(int keyCode) {
		return keyCode == KeyEvent.VK_PAGE_DOWN
				|| keyCode == KeyEvent.VK_PAGE_UP;
	}

	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		// TODO Auto-generated method stub

	}

	private void prependInputText() {
		if (getPreferences().getChatPreferences().isPreprendTellToTabs()
				&& prependTellText.isSelected()) {
			ChatTab currentTab = getCurrentTab();
			if (currentTab.getChannel() != -1) {
				inputField.setText("tell " + currentTab.getChannel() + " ");
				inputField.setCaretPosition(inputField.getDocument()
						.getLength());
			} else if (currentTab.getTopic() != null) {
				inputField.setText("tell " + currentTab.getTopic() + " ");
				inputField.setCaretPosition(inputField.getDocument()
						.getLength());
			} else {
				// main tab.
				inputField.setText("");
				inputField.setCaretPosition(0);
			}
		}
	}

	public void removeMessagesPending(ChatTab tab) {
		synchronized (this) {
			for (int i = 0; i < tabbedPane.getTabCount(); i++) {
				if (getTabAt(i) == tab) {
					if (tabbedPane.getTitleAt(i).startsWith("*")) {
						String title = tabbedPane.getTitleAt(i);
						title = title.substring(1, title.length() - 1);
						tabbedPane.setTitleAt(i, title);
						break;
					}
				}
			}
		}
	}

	public void removeTab(ChatTab tab) {
		synchronized (this) {
			for (int i = 0; i < tabbedPane.getTabCount(); i++) {
				if (getTabAt(i) == tab) {
					tabbedPane.removeTabAt(i);
				}
			}
		}

		if (tabbedPane.getComponentCount() == 0) {
			setupMainConsoleMode();
		}
	}

	public void requestFocus() {
		super.requestFocus();
		inputField.requestFocus();
	}

	public void setAutoScrolling(boolean isAutoScrolling) {
		this.isAutoScrolling = isAutoScrolling;
		slEnabled.setSelected(isAutoScrolling);
	}

	public void setBugOpen(boolean isBugOpen) {
		if (this.isBugOpen != isBugOpen) {
			this.isBugOpen = isBugOpen;
			bugOpenEnabled.setSelected(isBugOpen);
		}
	}

	public void setFocusToInput() {
		inputField.requestFocusInWindow();
	}

	public void setFont(Font font) {
		super.setFont(font);
	}

	public void setInputField(ExposedJTextField inputField) {
		this.inputField = inputField;
	}

	private void setInputProperties(TextProperties textProperties) {
		inputPrompt.setFont(textProperties.getFont());
		inputField.setFont(textProperties.getFont());
		inputField.setForeground(textProperties.getForeground());
		inputField.setBackground(textProperties.getBackground());

	}

	public void setMessagesPending(ChatTab tab) {
		synchronized (this) {
			for (int i = 0; i < tabbedPane.getTabCount(); i++) {
				if (getTabAt(i) == tab) {
					if (!tabbedPane.getTitleAt(i).startsWith("*")) {
						String title = tabbedPane.getTitleAt(i);
						title = "*" + title + "*";
						tabbedPane.setTitleAt(i, title);
						break;
					}
				}
			}
		}
	}

	public void setOpen(boolean isOpen) {
		if (this.isOpen != isOpen) {
			this.isOpen = isOpen;
			openEnabled.setSelected(isOpen);
		}
	}

	public void setPreferences(Preferences preferences) {
		synchronized (this) {
			this.preferences = preferences;

			pingLabel.setPreferences(preferences);
			textPrefsToAttributesCache.clear();

			if (tabbedPane != null) {
				tabbedPane.setTabPlacement(preferences.getChatPreferences()
						.getTabOrientation());
			}

			for (Integer integer : preferences.getChatPreferences()
					.getChannelTabs()) {
				if (getChannelTab(integer) == null) {
					addChannelTab(integer);
				}
			}

			if (lastSeekFrame != null) {
				lastSeekFrame.setPreferences(preferences);
			}
		}
	}

	public void setScrollBarToMax() {
		getCurrentTab().setScrollBarToMax();
	}

	private void setupInputPanelLayout() {
		inputPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets(5, 3, 5, 3);
		constraints.fill = GridBagConstraints.BOTH;
		inputPanel.add(inputPrompt, constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		inputPanel.add(inputField, constraints);

		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		inputPanel.add(prependTellText, constraints);

		constraints.gridx = 3;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		inputPanel.add(removeTabButton, constraints);

		constraints.gridx = 4;
		constraints.gridy = 0;
		constraints.weightx = 0.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		inputPanel.add(pingLabel, constraints);
	}

	public void setupMainConsoleMode() {
		remove(tabSplitPane);
		add(mainTab, BorderLayout.CENTER);
		tabSplitPane.removeAll();
		tabbedPane.removeAll();
		revalidate();
		removeTabButton.setVisible(false);
		removeTabButton.setEnabled(false);
		prependTellText.setVisible(false);
		prependTellText.setEnabled(false);
		tabSplitPane.removeAll();
		tabSplitPane = null;
		tabbedPane = null;
	}

	public void setupSplitPaneMode() {

		if (!isTabbingDisabled) {
			tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

			tabbedPane.setTabPlacement(preferences.getChatPreferences()
					.getTabOrientation());

			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					if (getCurrentTab() != null) {
						getCurrentTab().activate();
						prependInputText();
					}

				}
			});

			tabSplitPane = new JSplitPane();
			tabSplitPane.setDividerLocation((getHeight() - 100) / 2);
			tabSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			tabSplitPane.setTopComponent(mainTab);
			tabSplitPane.setBottomComponent(tabbedPane);

			removeTabButton.setVisible(true);
			removeTabButton.setEnabled(true);
			prependTellText.setEnabled(true);
			prependTellText.setVisible(true);

			remove(mainTab);
			add(tabSplitPane, BorderLayout.CENTER);
			revalidate();

			if (isAutoScrolling()) {
				mainTab.setScrollBarToMax();
			}
		}
	}

	private void subscribe() {
		subscriber = new TextReceivedEventSubscriber();
	}

	public String tellEventToSpeechString(PartnerTellEvent tellEvent) {
		String result = tellEvent.getMessage();
		result = result.replace('\\', ' ');
		result = result.replace('\n', ' ');
		result = result.replace('\r', ' ');

		result = result.replaceAll("\\:\\)", " smile ");
		result = result.replaceAll("\\=\\)", " smile ");
		result = result.replaceAll("\\;\\)", " wink ");
		result = result.replaceAll("\\:b", "  sticks tung out ");
		result = result.replaceAll("\\:\\(", " frown ");
		result = result.replaceAll("\\:\\D", " laugh");

		result = result.replaceAll("\\+p", "\\+ Pawn");
		result = result.replaceAll("\\+P", "\\+ Pawn");
		result = result.replaceAll("\\+b", "\\+ Bishop");
		result = result.replaceAll("\\+B", "\\+ Bishop");
		result = result.replaceAll("\\+n", "\\+ Nite");
		result = result.replaceAll("\\+N", "\\+ Nite");
		result = result.replaceAll("\\+r", "\\+ Rook");
		result = result.replaceAll("\\+R", "\\+ Rook");
		result = result.replaceAll("\\+q", "\\+ Queen");
		result = result.replaceAll("\\+Q", "\\+ Queen");
		result = result.replaceAll("\\+D", "\\+ Dyiag");
		result = result.replaceAll("\\+d", "\\+ Dyiag");
		result = result.replaceAll("\\+h", "\\+ Heavy");
		result = result.replaceAll("\\+H", "\\+ Heavy");

		result = result.replaceAll(" p ", " Pawn ");
		result = result.replaceAll(" P ", " Pawn ");
		result = result.replaceAll(" b ", " Bishop ");
		result = result.replaceAll(" B ", " Bishop ");
		result = result.replaceAll(" n ", " Nite ");
		result = result.replaceAll(" N ", " Nite ");
		result = result.replaceAll(" r ", " Rook ");
		result = result.replaceAll(" R ", " Rook ");
		result = result.replaceAll(" q ", " Queen ");
		result = result.replaceAll(" Q ", " Queen ");
		result = result.replaceAll(" d ", " Dyiag ");
		result = result.replaceAll(" D ", " Dyiag ");
		result = result.replaceAll(" h ", " Heavy ");
		result = result.replaceAll(" h ", " Heavy ");

		if (result.length() > 2) {
			String firstTwo = result.substring(0, 2);

			if (firstTwo.equalsIgnoreCase("N ")) {
				result = "nite " + result.substring(2, result.length());
			} else if (firstTwo.equalsIgnoreCase("Q ")) {
				result = "queen " + result.substring(2, result.length());
			} else if (firstTwo.equalsIgnoreCase("B ")) {
				result = "bishop " + result.substring(2, result.length());
			} else if (firstTwo.equalsIgnoreCase("R ")) {
				result = "rook " + result.substring(2, result.length());
			} else if (firstTwo.equalsIgnoreCase("P ")) {
				result = "pawn " + result.substring(2, result.length());
			} else if (firstTwo.equalsIgnoreCase("D ")) {
				result = "dyiag " + result.substring(2, result.length());
			} else if (firstTwo.equalsIgnoreCase("H ")) {
				result = "heavy " + result.substring(2, result.length());
			}
		}

		if (result.length() > 2) {
			String lastTwo = result.substring(result.length() - 2, result
					.length());

			if (lastTwo.equalsIgnoreCase(" N")) {
				result = result.substring(0, result.length() - 2) + " nite";
			} else if (lastTwo.equalsIgnoreCase(" Q")) {
				result = result.substring(0, result.length() - 2) + " queen";
			} else if (lastTwo.equalsIgnoreCase(" B")) {
				result = result.substring(0, result.length() - 2) + " bishop";
			} else if (lastTwo.equalsIgnoreCase(" R")) {
				result = result.substring(0, result.length() - 2) + " rook";
			} else if (lastTwo.equalsIgnoreCase(" P")) {
				result = result.substring(0, result.length() - 2) + " pawn";
			} else if (lastTwo.equalsIgnoreCase(" D")) {
				result = result.substring(0, result.length() - 2) + " dyiag";
			} else if (lastTwo.equalsIgnoreCase(" H")) {
				result = result.substring(0, result.length() - 2) + " heavy";
			}
		}

		if (result.length() == 1) {
			if (result.equalsIgnoreCase("N")) {
				result = "nite";
			} else if (result.equalsIgnoreCase("Q")) {
				result = "queen";
			} else if (result.equalsIgnoreCase("B")) {
				result = "bishop";
			} else if (result.equalsIgnoreCase("R")) {
				result = "rook";
			} else if (result.equalsIgnoreCase("P")) {
				result = "pawn";
			} else if (result.equalsIgnoreCase("D")) {
				result = "dyiag";
			} else if (result.equalsIgnoreCase("H")) {
				result = "heavy";
			}

		}

		result = result.replaceAll("\\-p", "\\minus Pawn");
		result = result.replaceAll("\\-P", "\\minus Pawn");
		result = result.replaceAll("\\-b", "\\minus Bishop");
		result = result.replaceAll("\\-B", "\\minus Bishop");
		result = result.replaceAll("\\-n", "\\minus Nite");
		result = result.replaceAll("\\-N", "\\minus Nite");
		result = result.replaceAll("\\-r", "\\minus Rook");
		result = result.replaceAll("\\-R", "\\minus Rook");
		result = result.replaceAll("\\-q", "\\minus Queen");
		result = result.replaceAll("\\-Q", "\\minus Queen");
		result = result.replaceAll("\\-D", "\\minus Dyiag");
		result = result.replaceAll("\\-d", "\\minus Dyiag");
		result = result.replaceAll("\\-h", "\\minus Heavy");
		result = result.replaceAll("\\-H", "\\minus Heavy");

		return result;
	}

	public String tellEventToSpeechString(TellEvent tellEvent) {
		String result = tellEvent.getMessage();
		if (!preferences.getSpeechPreferences().isSpeakingName()) {
			int spaceIndex = result.indexOf(" ");
			result = result.substring(spaceIndex, result.length());
		}
		result = result.replace('\\', ' ');
		result = result.replace('\n', ' ');
		result = result.replace('\r', ' ');
		result = result.replaceFirst("tells you:", preferences
				.getSpeechPreferences().isSpeakingName() ? " says " : "");
		result = result.replaceAll("\\:\\)", " smile ");
		result = result.replaceAll("\\=\\)", " smile ");
		result = result.replaceAll("\\;\\)", " wink  ");
		result = result.replaceAll("\\:b", " sticks tung out ");
		result = result.replaceAll("\\:\\(", " frown ");
		return result;
	}

	public void tellLast() {
		if (!lastTells.isEmpty()) {
			String lastTell = lastTells.get(lastTellsIndex);
			if (lastTell != null) {
				inputField.setText("tell " + lastTell + " ");

				synchronized (this) {
					if (lastTellsIndex != 0) {
						lastTellsIndex--;
					} else {
						lastTellsIndex = lastTells.size() - 1;
					}
				}
			}
		}
	}

}