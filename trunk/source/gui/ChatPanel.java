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
package decaf.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import decaf.com.inboundevent.InboundEvent;
import decaf.com.inboundevent.chat.CShoutEvent;
import decaf.com.inboundevent.chat.ChannelTellEvent;
import decaf.com.inboundevent.chat.KibitzEvent;
import decaf.com.inboundevent.chat.PartnerTellEvent;
import decaf.com.inboundevent.chat.ShoutEvent;
import decaf.com.inboundevent.chat.TellEvent;
import decaf.com.inboundevent.chat.WhisperEvent;
import decaf.com.inboundevent.game.GInfoEvent;
import decaf.com.inboundevent.game.GameEndEvent;
import decaf.com.inboundevent.game.GameStartEvent;
import decaf.com.inboundevent.game.IllegalMoveEvent;
import decaf.com.inboundevent.inform.AvailInfoEvent;
import decaf.com.inboundevent.inform.AvailableBugTeamsEvent;
import decaf.com.inboundevent.inform.BugClosedEvent;
import decaf.com.inboundevent.inform.BugGamesInProgressEvent;
import decaf.com.inboundevent.inform.BugOpenEvent;
import decaf.com.inboundevent.inform.ChallengeEvent;
import decaf.com.inboundevent.inform.ClosedEvent;
import decaf.com.inboundevent.inform.ConnectedEvent;
import decaf.com.inboundevent.inform.DisconnectedEvent;
import decaf.com.inboundevent.inform.NotificationEvent;
import decaf.com.inboundevent.inform.OpenEvent;
import decaf.com.inboundevent.inform.PartnershipCreatedEvent;
import decaf.com.inboundevent.inform.PartnershipEndedEvent;
import decaf.com.inboundevent.inform.SoughtEvent;
import decaf.com.inboundevent.inform.UnavailInfoEvent;
import decaf.com.inboundevent.inform.VariablesEvent;
import decaf.com.outboundevent.BugOpenRequestEvent;
import decaf.com.outboundevent.OpenRequestEvent;
import decaf.com.outboundevent.OutboundEvent;
import decaf.event.Event;
import decaf.event.EventService;
import decaf.event.Filter;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.PropertiesConstants;
import decaf.gui.util.PropertiesManager;
import decaf.gui.util.TextProperties;
import decaf.gui.util.ToolbarUtil;
import decaf.gui.widgets.PingLabel;
import decaf.speech.SpeechManager;

public class ChatPanel extends JPanel implements ActionListener,
		Preferenceable, Disposable {

	private Logger LOGGER = Logger.getLogger(ChatPanel.class);

	private static final String VALID_PERSON_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private static final String SCROLL_LOCK_ON_TOOLTIP = "Scroll Lock Enabled: Click to disable scroll lock.";

	private static final String SCROLL_LOCK_OFF_TOOLTIP = "Scroll lock Disabled: Click to enable scroll lock.";

	private static final String OPEN_TOOLTIP = "Open: Click to become closed to match requests.";

	private static final String CLOSED_TOOLTIP = "Closed: Click to become open to match requests.";

	private static final String BUGOPEN_TOOLTIP = "Bug Open: Click to become closed to bughouse partnership requests.";

	private static final String BUGCLOSED_TOOLTIP = "Bug Closed: Click to become open to bughouse partnership requests.";

	private static final EventService eventService = EventService.getInstance();

	private static final PropertiesManager resourceRepository = PropertiesManager
			.getInstance();

	private static final SoundManager soundManager = SoundManager.getInstance();

	private ScrollAdjustmentListener scrollbarAdjustmentListener;

	private JToolBar toolbar = new JToolBar();

	private int bufferSize = 25000;

	private JTextPane textPane;

	private JScrollPane scrollPane;

	private JPanel eastFillerPanel;

	private JPanel westFillerPanel;

	private JPanel northSouthFillerPanel;

	private JPanel inputPanel;

	private Map<TextProperties, SimpleAttributeSet> textPrefsToAttributesCache = new HashMap<TextProperties, SimpleAttributeSet>();

	private JLabel inputPrompt;

	private JButton scrollLockButton;

	private JButton bugOpenButton;

	private JButton openButton;

	private StyledDocument document;

	private Preferences preferences;

	private JTextField inputField;

	private java.util.List previousInput;

	private Subscriber subscriber;

	private InputKeyListener inputKeyListener;

	private int lastPreviousInputIndex;

	private boolean isAutoScrolling;

	private boolean isSmartScrollEnabled;

	private boolean isBugOpen;

	private boolean isOpen;

	private boolean isIgnoringScrollEvents;

	private boolean isInputLineOnTop;

	private PingLabel pingLabel;

	private int lastScrollbarValue;

	public void dispose() {
		removeAll();
		textPane = null;
		if (scrollPane != null) {
			scrollPane.removeAll();
			scrollPane = null;
		}
		if (eastFillerPanel != null) {
			eastFillerPanel.removeAll();
			eastFillerPanel = null;
		}
		if (westFillerPanel != null) {
			westFillerPanel.removeAll();
			westFillerPanel = null;
		}
		if (northSouthFillerPanel != null) {
			northSouthFillerPanel.removeAll();
			northSouthFillerPanel = null;
		}
		if (inputPanel != null) {
			inputPanel.removeAll();
			inputPanel = null;
		}
		if (northSouthFillerPanel != null) {
			northSouthFillerPanel.removeAll();
			northSouthFillerPanel = null;
		}
		if (northSouthFillerPanel != null) {
			northSouthFillerPanel.removeAll();
			northSouthFillerPanel = null;
		}
		scrollLockButton = null;
		bugOpenButton = null;
		document = null;
		preferences = null;
		preferences = null;
		inputField = null;
		if (previousInput != null) {
			previousInput.clear();
			previousInput = null;
		}
		if (subscriber != null) {
			eventService.unsubscribe(new Subscription(InboundEvent.class, null,
					subscriber));
			subscriber = null;
		}
		if (inputKeyListener != null) {
			inputKeyListener = null;
		}
	}

	public void setFocusToInput() {
		inputField.requestFocusInWindow();
	}

	public ChatPanel(Preferences preferences) {
		this(preferences, true);
	}

	public ChatPanel(Preferences preferences, boolean isSubscribing) {
		this.preferences = preferences;
		this.preferences = preferences;
		initControls(isSubscribing);
		if (isSubscribing) {
			subscribe();
		}
	}

	private void subscribe() {
		subscriber = new TextReceivedEventSubscriber();
	}

	private void initControls(boolean isSubscribing) {
		isIgnoringScrollEvents = false;

		inputKeyListener = new InputKeyListener();
		addKeyListener(inputKeyListener);

		inputPrompt = new JLabel(resourceRepository.getString(
				PropertiesConstants.DECAF_PROPERTIES,
				PropertiesConstants.PROMPT));
		inputField = new JTextField();
		previousInput = new LinkedList();
		inputField.addActionListener(this);
		inputField.addKeyListener(new InputFieldKeyHistoryListener());
		inputField.addKeyListener(inputKeyListener);

		document = new DefaultStyledDocument();
		textPane = new JTextPane(document);
		textPane.addMouseListener(new CLickListener());
		textPane.setEditable(false);
		// textPane.addCaretListener(new AutoscrollAdjustingCaretListener());
		textPane.addKeyListener(inputKeyListener);
		scrollPane = new JScrollPane(textPane,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		scrollPane.getVerticalScrollBar().addAdjustmentListener(
				scrollbarAdjustmentListener = new ScrollAdjustmentListener());

		pingLabel = new PingLabel(preferences);

		eastFillerPanel = new JPanel();
		westFillerPanel = new JPanel();
		northSouthFillerPanel = new JPanel();

		textPane.setBackground(Color.white);

		bugOpenButton = new JButton("Bug Closed");
		scrollLockButton = new JButton("SL On ");
		openButton = new JButton("Closed");

		if (isSubscribing) {

			scrollLockButton.setToolTipText(SCROLL_LOCK_ON_TOOLTIP);
			openButton.setToolTipText(OPEN_TOOLTIP);
			bugOpenButton.setToolTipText(BUGOPEN_TOOLTIP);

			scrollLockButton.addActionListener(new ScrollLockActionListener());
			openButton.addActionListener(new OpenActionListener());
			bugOpenButton.addActionListener(new BugOpenActionListener());

			toolbar.add(scrollLockButton);
			toolbar.addSeparator(new Dimension(5, 1));
			toolbar.add(openButton);
			toolbar.addSeparator(new Dimension(5, 1));
			toolbar.add(bugOpenButton);
			toolbar.addSeparator(new Dimension(5, 1));
			toolbar.addSeparator(new Dimension(20, 1));

			ToolbarUtil.addToolbarButtonsFromProperties("ChatToolbar", toolbar,
					20, 5);
		}

		// buttonPanel = new JPanel();
		inputPanel = new JPanel();

		setupInputPanelLayout();

		setLayout(new BorderLayout());
		add(toolbar, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(inputPanel, BorderLayout.SOUTH);

		setPreferences(preferences);

		setAutoScrolling(true);
		setOpen(false);
		setBugOpen(false);
	}

	private void setupInputPanelLayout() {
		inputPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0;
		constraints.weighty = 1.0;
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
		constraints.weightx = 0.0;
		constraints.weighty = 1.0;
		constraints.fill = GridBagConstraints.BOTH;
		inputPanel.add(pingLabel, constraints);
	}

	private void setInputProperties(TextProperties textProperties) {
		inputPrompt.setFont(textProperties.getFont());
		inputField.setFont(textProperties.getFont());
		inputField.setForeground(textProperties.getForeground());
		inputField.setBackground(textProperties.getBackground());

	}

	public void setPreferences(Preferences preferences) {
		synchronized (this) {
			this.preferences = preferences;
			textPane.setBackground(preferences.getChatPreferences()
					.getTelnetPanelBackground());
			pingLabel.setPreferences(preferences);
			textPrefsToAttributesCache.clear();

			setSmartScrollEnabled(preferences.getChatPreferences()
					.isSmartScrollEnabled());
			setBufferSize(preferences.getChatPreferences()
					.getTelnetBufferSize());
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public boolean isSmartScrollEnabled() {
		return isSmartScrollEnabled;
	}

	public void setSmartScrollEnabled(boolean isSmartScrollEnabled) {
		this.isSmartScrollEnabled = isSmartScrollEnabled;
	}

	public void setAutoScrolling(boolean isAutoScrolling) {
		this.isAutoScrolling = isAutoScrolling;
		/*
		 * scrollLockButton.setIcon(isAutoScrolling ? SCROLL_LOCK_ON_IMAGE :
		 * SCROLL_LOCK_OFF_IMAGE);
		 */
		scrollLockButton.setText(isAutoScrolling ? "SL On " : "SL Off");
		scrollLockButton
				.setToolTipText(isAutoScrolling ? SCROLL_LOCK_ON_TOOLTIP
						: SCROLL_LOCK_OFF_TOOLTIP);
		if (isAutoScrolling) {
			setScrollBarToMax();
		}
	}
	
	public boolean isAutoScrolling() {
		return isAutoScrolling;
	}

	public void setOpen(boolean isOpen) {
		if (this.isOpen != isOpen) {
			this.isOpen = isOpen;
			// openButton.setIcon(isOpen ? OPEN_IMAGE : CLOSED_IMAGE);
			openButton.setText(isOpen ? "Open  " : "Closed");
			openButton.setToolTipText(isOpen ? OPEN_TOOLTIP : CLOSED_TOOLTIP);
		}
	}

	public void setBugOpen(boolean isBugOpen) {
		if (this.isBugOpen != isBugOpen) {
			this.isBugOpen = isBugOpen;
			// bugOpenButton.setIcon(isBugOpen ? BUGOPEN_IMAGE :
			// BUGCLOSED_IMAGE);
			bugOpenButton.setText(isBugOpen ? "Bug Open  " : "Bug Closed");
			bugOpenButton.setToolTipText(isBugOpen ? BUGOPEN_TOOLTIP
					: BUGCLOSED_TOOLTIP);
		}
	}

	public void clearText() {
		try {
			document.remove(0, document.getLength());
		} catch (BadLocationException ble) {
			throw new RuntimeException(ble.getMessage());
		}
	}

	public void setFont(Font font) {
		super.setFont(font);
	}

	public void actionPerformed(ActionEvent actionevent) {
		String s = inputField.getText();
		if (s == null) {
			s = "";
		}

		eventService.publish(new OutboundEvent(inputField.getText()));
		inputField.setText("");
		previousInput.add(s);
		lastPreviousInputIndex = previousInput.size();
	}

	public void appendText(String text, AttributeSet attributes) {
		appendText(text, attributes, true);
	}

	public void appendText(String text, AttributeSet attributes,
			boolean isAppendingNewline) {
		long startTime = System.currentTimeMillis();

		AttributeSet attributeSet = attributes;

		if (isAppendingNewline) {
			text = text + "\r\n";
		}

		int i = document.getLength() + text.length();

		synchronized (this) {
			if (i > getPreferences().getChatPreferences().getTelnetBufferSize()) {
				// removing is expensive so remove 25% chunks at a time.
				int j = (int) (getPreferences().getChatPreferences()
						.getTelnetBufferSize() * .25);
				if (j > document.getLength()) {
					j = document.getLength();
				}

				try {
					document.remove(0, j);
				} catch (BadLocationException badlocationexception1) {
				}
			}
			try {
				document.insertString(document.getLength(), text, attributeSet);
			} catch (BadLocationException badlocationexception) {
			}
			if (isAutoScrolling) {

				setScrollBarToMax();
			}
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Appended text in "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	public boolean isInputLineOnTop() {
		return isInputLineOnTop;
	}

	public void setScrollBarToMax() {
		isIgnoringScrollEvents = true;

		textPane.setCaretPosition(document.getLength());
		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();

		if (scrollBar.getValue() >= scrollBar.getMaximum()) {
			scrollBar.setValue(scrollBar.getMaximum());
		}

		isIgnoringScrollEvents = false;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				isAutoScrolling = true;
			}
		});
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

	private SimpleAttributeSet getSimpleAttributes(TextProperties properties) {
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

	public class TextReceivedEventSubscriber implements Subscriber, Filter {

		public TextReceivedEventSubscriber() {
			eventService.subscribe(new Subscription(InboundEvent.class, this,
					this));
			eventService.subscribe(new Subscription(IllegalMoveEvent.class,
					this, this));
			eventService.subscribe(new Subscription(GameEndEvent.class, this,
					this));
			eventService.subscribe(new Subscription(GameStartEvent.class, this,
					this));
			eventService.subscribe(new Subscription(AvailInfoEvent.class, this,
					this));
			eventService.subscribe(new Subscription(UnavailInfoEvent.class,
					this, this));
			eventService.subscribe(new Subscription(GInfoEvent.class, this,
					this));
			eventService.subscribe(new Subscription(ConnectedEvent.class, this,
					this));
			eventService.subscribe(new Subscription(DisconnectedEvent.class,
					this, this));
			eventService
					.subscribe(new Subscription(OpenEvent.class, this, this));
			eventService.subscribe(new Subscription(ClosedEvent.class, this,
					this));
			eventService.subscribe(new Subscription(BugOpenEvent.class, this,
					this));
			eventService.subscribe(new Subscription(BugClosedEvent.class, this,
					this));
			eventService.subscribe(new Subscription(CShoutEvent.class, this,
					this));
			eventService.subscribe(new Subscription(ChannelTellEvent.class,
					this, this));
			eventService.subscribe(new Subscription(KibitzEvent.class, this,
					this));
			eventService.subscribe(new Subscription(WhisperEvent.class, this,
					this));
			eventService.subscribe(new Subscription(PartnerTellEvent.class,
					this, this));
			eventService.subscribe(new Subscription(
					PartnershipCreatedEvent.class, this, this));
			eventService.subscribe(new Subscription(SoughtEvent.class, this,
					this));
			eventService.subscribe(new Subscription(ChallengeEvent.class, this,
					this));
			eventService.subscribe(new Subscription(NotificationEvent.class,
					this, this));
			eventService.subscribe(new Subscription(
					PartnershipEndedEvent.class, this, this));
			eventService.subscribe(new Subscription(ShoutEvent.class, this,
					this));
			eventService
					.subscribe(new Subscription(TellEvent.class, this, this));
			eventService.subscribe(new Subscription(VariablesEvent.class, this,
					this));
			eventService.subscribe(new Subscription(
					BugGamesInProgressEvent.class, this, this));
			eventService.subscribe(new Subscription(
					AvailableBugTeamsEvent.class, this, this));
		}

		public boolean apply(Event event) {
			return ((InboundEvent) event).isShowingToUser();
		}

		public void inform(InboundEvent event) {
			appendText(event.getText(), getSimpleAttributes(getPreferences()
					.getChatPreferences().getDefaultTextProperties()), !event
					.isIncompleteMessage());
		}

		public void inform(IllegalMoveEvent illegalmoveevent) {
			appendText(illegalmoveevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(GameEndEvent gameendevent) {
			appendText(gameendevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(GameStartEvent gamestartevent) {
			appendText(gamestartevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(AvailInfoEvent availinfoevent) {
			appendText(availinfoevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));

		}

		public void inform(UnavailInfoEvent unavailinfoevent) {
			appendText(unavailinfoevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));

		}

		public void inform(GInfoEvent ginfoevent) {
			appendText(ginfoevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(ConnectedEvent connectedevent) {
			appendText(connectedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getAlertTextProperties()));
		}

		public void inform(DisconnectedEvent disconnectedevent) {
			appendText(disconnectedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getAlertTextProperties()));
		}

		public void inform(OpenEvent openevent) {
			appendText(openevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setOpen(true);
		}

		public void inform(ClosedEvent closedevent) {
			appendText(closedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setOpen(false);

		}

		public void inform(BugOpenEvent bugopenevent) {
			appendText(bugopenevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setBugOpen(true);

		}

		public void inform(BugClosedEvent bugclosedevent) {
			appendText(bugclosedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			setBugOpen(false);

		}

		public void inform(CShoutEvent cshoutevent) {

			appendText(cshoutevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getCshoutTextProperties()));
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
					result = result.substring(0, result.length() - 2)
							+ " queen";
				} else if (lastTwo.equalsIgnoreCase(" B")) {
					result = result.substring(0, result.length() - 2)
							+ " bishop";
				} else if (lastTwo.equalsIgnoreCase(" R")) {
					result = result.substring(0, result.length() - 2) + " rook";
				} else if (lastTwo.equalsIgnoreCase(" P")) {
					result = result.substring(0, result.length() - 2) + " pawn";
				} else if (lastTwo.equalsIgnoreCase(" D")) {
					result = result.substring(0, result.length() - 2)
							+ " dyiag";
				} else if (lastTwo.equalsIgnoreCase(" H")) {
					result = result.substring(0, result.length() - 2)
							+ " heavy";
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

		public void inform(ChannelTellEvent channeltellevent) {
			appendText(
					channeltellevent.getText(),
					getSimpleAttributes(getPreferences()
							.getChatPreferences()
							.getChannelProperties(channeltellevent.getChannel())));

		}

		public void inform(KibitzEvent kibitzevent) {
			appendText(kibitzevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getKibitzTextProperties()));
		}

		public void inform(WhisperEvent whisperevent) {

			appendText(whisperevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getWhisperTextProperties()));
		}

		public void inform(PartnerTellEvent partnertellevent) {
			appendText(partnertellevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getPtellTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingPtells()) {
				SpeechManager.getInstance().getSpeech().speak(
						tellEventToSpeechString(partnertellevent));
			}
		}

		public void inform(PartnershipCreatedEvent partnershipcreatedevent) {

			try {
				appendText(partnershipcreatedevent.getText(),
						getSimpleAttributes(getPreferences()
								.getChatPreferences()
								.getDefaultTextProperties()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void inform(PartnershipEndedEvent partnershipendedevent) {
			appendText(partnershipendedevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingNotifications()) {
				SpeechManager.getInstance().getSpeech().speak(
						"Partnership ended");
			}

		}

		public void inform(ShoutEvent shoutevent) {
			appendText(shoutevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getShoutTextProperties()));
		}

		public void inform(TellEvent tellEvent) {
			appendText(tellEvent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getTellTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingTells()) {
				SpeechManager.getInstance().getSpeech().speak(
						tellEventToSpeechString(tellEvent));
			}
		}

		public void inform(VariablesEvent variablesevent) {

			appendText(variablesevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(BugGamesInProgressEvent buggamesinprogressevent) {

			appendText(buggamesinprogressevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(AvailableBugTeamsEvent availablebugteamsevent) {

			appendText(availablebugteamsevent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getDefaultTextProperties()));
		}

		public void inform(NotificationEvent notificationEvent) {
			appendText(notificationEvent.getText(),
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
		}

		public void inform(ChallengeEvent challengeEvent) {
			appendText(challengeEvent.getText(),
					getSimpleAttributes(getPreferences().getChatPreferences()
							.getMatchTextProperties()));
			if (preferences.getSpeechPreferences().isSpeakingNotifications()) {
				SpeechManager.getInstance().getSpeech().speak("Challenge");
			}
		}

		public void inform(SoughtEvent event) {
			appendText(event.getText(), getSimpleAttributes(getPreferences()
					.getChatPreferences().getDefaultTextProperties()));

		}
	}

	private class BugOpenActionListener implements ActionListener {

		public void actionPerformed(ActionEvent actionevent) {
			eventService.publish(new BugOpenRequestEvent(!isBugOpen, true));
		}

		private BugOpenActionListener() {
		}

	}

	private class OpenActionListener implements ActionListener {

		public void actionPerformed(ActionEvent actionevent) {
			eventService.publish(new OpenRequestEvent(!isOpen, true));
		}

		private OpenActionListener() {
		}

	}

	private class ScrollLockActionListener implements ActionListener {

		public void actionPerformed(ActionEvent actionevent) {
			setAutoScrolling(!isAutoScrolling);
		}

		private ScrollLockActionListener() {
		}

	}

	private class ScrollAdjustmentListener implements AdjustmentListener {

		public void adjustmentValueChanged(AdjustmentEvent event) {
			if (isSmartScrollEnabled()) {

				int max = scrollPane.getVerticalScrollBar().getMaximum();
				int extent = scrollPane.getVerticalScrollBar()
						.getVisibleAmount();
				int value = scrollPane.getVerticalScrollBar().getValue();

				if (lastScrollbarValue > value && isAutoScrolling) {
					setAutoScrolling(false);
				} else if (lastScrollbarValue < value && !isAutoScrolling
						&& (max - extent) == value) {
					setAutoScrolling(true);
				}

				lastScrollbarValue = value;
			}
		}
	}

	/**
	 * Handles up and down input history. Also handles setting focus to text
	 * pane if a page up or page down is pressed.
	 */
	private class InputFieldKeyHistoryListener extends KeyAdapter {

		public void keyPressed(KeyEvent keyEvent) {
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
			}
		}
	}

	/**
	 * Handles setting of focus to the appropriate field when any field which
	 * uses this input key listener has focus. It is used so you can type text
	 * while the textPane has focus and text goes to the inputField, and you can
	 * press page down in the input field and the textPane gets focus.
	 */
	private class InputKeyListener extends KeyAdapter {
		private boolean isPageUpOrDown(int keyCode) {
			return keyCode == KeyEvent.VK_PAGE_DOWN
					|| keyCode == KeyEvent.VK_PAGE_UP;
		}

		private boolean isEndOrHome(int keyCode) {
			return keyCode == KeyEvent.VK_HOME || keyCode == KeyEvent.VK_END;
		}

		private void handleInputFieldUpdate(KeyEvent keyEvent) {
			int keyCode = keyEvent.getKeyCode();

			if (inputField.hasFocus() && isEndOrHome(keyCode)) {
				// EAT IT
			} else if (!textPane.hasFocus()
					&& (isPageUpOrDown(keyCode) || isEndOrHome(keyCode))) {
				textPane.requestFocus();
			} else if (!inputField.hasFocus() && !isPageUpOrDown(keyCode)
					&& !isEndOrHome(keyCode) && keyEvent.getModifiers() == 0) {
				inputField
						.setText(inputField.getText() + keyEvent.getKeyChar());
				inputField.requestFocus();
			}
		}

		public void keyTyped(KeyEvent keyEvent) {
			handleInputFieldUpdate(keyEvent);
		}
	}

	private int getPositionOnPreviousLine(int caretPosition) {
		try {
			int lineStart;
			int currentPosition = caretPosition;
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition > 0 && currentChar != '\n'
					&& currentChar != '\r') {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}
			while (currentPosition > 0
					&& (currentChar == '\n' || currentChar == '\r')) {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
				currentPosition--;
			}

			return currentPosition < 0 ? 0 : currentPosition;
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

	private String getSoughtHistoryJournalCommand(int caretPosition,
			String currentLine) {

		// Yes this is a mess and needs to definitely be refactored. It is kept
		// as is because its an efficient mess.

		// Sought adds should really look for the ads displayed at the end but i
		// was lazy when i implemented it.
		String result = null;
		String[] words = currentLine.trim().split(" ");
		if (words.length > 17) {
			int gameNumber = -1;
			String historyFor = null;
			boolean isJournal = false;
			boolean isSought = false;
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
			} else {
				try {
					gameNumber = Integer.parseInt(words[0]);
					isSought = true;
				} catch (NumberFormatException nfe) {
					return null;
				}
			}

			// Find who its the history of.
			int counter = 0;
			int maxLines = isSought ? 100 : isJournal ? 28 : 12;
			for (int i = 0; i < maxLines; i++) {
				caretPosition = getPositionOnPreviousLine(caretPosition);

				if (caretPosition >= 0) {
					String prevLine = getLine(caretPosition).trim();

					if (isSought && prevLine.startsWith("sought")) {
						result = "play " + gameNumber;
						break;
					} else if ((!isJournal && prevLine
							.startsWith("History for"))
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
				result = isSought && result != null ? result : "examine "
						+ historyFor + " " + (isJournal ? "%" : "")
						+ gameNumber;
			}

		}
		return result;
	}

	private String getWord(int caretPosition) {
		// Yes this is a mess and needs to definitely be refactored. It is kept
		// as is because its an efficient mess and im lazy.
		try {
			int lineStart;
			int lineEnd;

			int currentPosition = caretPosition;
			char currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition > 0 && !Character.isWhitespace(currentChar)) {
				currentChar = document.getText(--currentPosition, 1).charAt(0);
			}

			lineStart = currentPosition;

			currentPosition = caretPosition;
			currentChar = document.getText(currentPosition, 1).charAt(0);

			while (currentPosition < document.getLength()
					&& !Character.isWhitespace(currentChar)) {
				currentChar = document.getText(++currentPosition, 1).charAt(0);
			}

			lineEnd = currentPosition;

			return stripWord(document.getText(lineStart + 1, lineEnd
					- lineStart - 1));
		} catch (Exception e) {
			return null;
		}
	}

	public static String stripWord(String word) {
		StringTokenizer stringtokenizer = new StringTokenizer(word,
				"()~!@?#$%^&*_+|}{';/., :[]1234567890\t\r\n");
		if (stringtokenizer.hasMoreTokens())
			return stringtokenizer.nextToken();
		else
			return word;
	}

	private JPopupMenu buildPopupMenu(final String string) {
		JPopupMenu menu = new JPopupMenu();
		if (string != null) {
			menu.add(new AbstractAction(string) {
				public void actionPerformed(ActionEvent e) {
					eventService.publish(new OutboundEvent(string, false));
				}
			});
		}
		return menu;
	}

	private JPopupMenu buildPopupMenu(final int obsGameNumber,
			final String person) {
		// Yes this is a mess and needs to definitely be refactored. It is kept
		// as is because its an efficient mess and im lazy.
		JPopupMenu menu = new JPopupMenu();
		if (obsGameNumber != -1) {
			menu.add(new AbstractAction("Observe game " + obsGameNumber) {
				public void actionPerformed(ActionEvent e) {
					eventService.publish(new OutboundEvent("obs "
							+ obsGameNumber, false));
				}
			});
		} else if (person != null) {

			PropertiesManager manager = PropertiesManager.getInstance();

			int currentButton = 1;
			String currentText = manager.getString("PersonPopup", currentButton
					+ ".text");
			String isSeperator = manager.getString("PersonPopup", currentButton
					+ ".isSeperator");

			while ((currentText != null && isSeperator == null)
					|| (currentText == null && isSeperator != null)) {
				if (currentText != null) {
					int dollarIndex = currentText.indexOf("$");
					if (dollarIndex != -1) {
						currentText = currentText.substring(0, dollarIndex)
								+ person
								+ currentText.substring(dollarIndex + 1,
										currentText.length());
					} else {
						throw new IllegalStateException(
								"PersonPopup command invalid: " + currentText);
					}
					final String finalString = currentText;
					AbstractAction action = new AbstractAction(finalString) {
						public void actionPerformed(ActionEvent e) {
							EventService.getInstance().publish(
									new OutboundEvent(finalString, false));
						}
					};

					menu.add(action);
				} else if (isSeperator != null && isSeperator.equals("true")) {
					menu.addSeparator();
				}

				currentButton++;
				currentText = manager.getString("PersonPopup", currentButton
						+ ".text");
				isSeperator = manager.getString("PersonPopup", currentButton
						+ ".isSeperator");
			}

		}

		return menu;
	}

	/**
	 * Handles setting of focus to the appropriate field when any field which
	 * uses this input key listener has focus. It is used so you can type text
	 * while the textPane has focus and text goes to the inputField, and you can
	 * press page down in the input field and the textPane gets focus.
	 */
	private class CLickListener implements MouseListener {

		public void mouseClicked(MouseEvent arg0) {
			// Yes this is a mess and needs to definitely be refactored. It is
			// kept as is because its an efficient mess and im lazy.
			if ((arg0.getButton() & MouseEvent.BUTTON1) == MouseEvent.BUTTON1) {
				try {
					String line = getLine(textPane.getCaretPosition());
					String examineCommand = getSoughtHistoryJournalCommand(
							textPane.getCaretPosition(), line);

					if (examineCommand != null) {
						JPopupMenu menu = buildPopupMenu(examineCommand);
						menu.show(textPane, arg0.getX(), arg0.getY());
					} else {
						String quotedText = getQuotedText(textPane
								.getCaretPosition());

						if (quotedText != null) {
							JPopupMenu menu = buildPopupMenu(quotedText);
							menu.show(textPane, arg0.getX(), arg0.getY());
						} else {
							String word = getWord(textPane.getCaretPosition());
							boolean isPerson = isLikelyPerson(word);
							int gameNumber = getGameNumber(line);

							if (isPerson || gameNumber != -1) {
								JPopupMenu menu = buildPopupMenu(gameNumber,
										isPerson ? word : null);
								menu.show(textPane, arg0.getX(), arg0.getY());
							}
						}
					}
				} catch (Exception e) {
				}
			}

		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
		}

	}
}