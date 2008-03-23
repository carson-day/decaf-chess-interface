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
package decaf.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import decaf.chat.ChatFrame;
import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.widgets.BugEarPanel;
import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.KeyMapper;
import decaf.messaging.ics.ICSCommunicationsDriver;
import decaf.messaging.inboundevent.game.GameEndEvent;
import decaf.messaging.inboundevent.game.GameStartEvent;
import decaf.messaging.inboundevent.game.MoveEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.resources.ResourceManagerFactory;
import decaf.speech.SpeechManager;
import decaf.thread.ThreadManager;
import decaf.util.PropertiesConstants;

public class GUIManager implements Preferenceable {

	private static int INITIAL_CACHED_CHESS_AREA_CONTROLLERS = 0;

	private static int INITIAL_CACHED_BUG_CHESS_AREA_CONTROLLERS = 0;

	private static int MAX_CACHED_CHESS_AREA_CONTROLLERS = 0;

	private static int MAX_CACHED_BUG_CHESS_AREA_CONTROLLERS = 0;

	private static final Logger LOGGER = Logger.getLogger(GUIManager.class);

	private static final int Y_FRAME_ADJUSTMENT = 20;

	private static final int X_FRAME_ADJUSTMENT = 10;

	public static Image DECAF_ICON = null;

	static {
		try {
			INITIAL_CACHED_CHESS_AREA_CONTROLLERS = ResourceManagerFactory
					.getManager().getInt("Decaf", "initialChessControllers");
			INITIAL_CACHED_BUG_CHESS_AREA_CONTROLLERS = ResourceManagerFactory
					.getManager().getInt("Decaf", "initialBugControllers");
			MAX_CACHED_CHESS_AREA_CONTROLLERS = ResourceManagerFactory
					.getManager().getInt("Decaf", "maxChessControllers");
			MAX_CACHED_BUG_CHESS_AREA_CONTROLLERS = ResourceManagerFactory
					.getManager().getInt("Decaf", "maxBugControllers");

			// SwingUtilities.invokeLater(new Runnable() {
			// public void run() {
			try {

				DECAF_ICON = ResourceManagerFactory.getManager().getImage(
						"DECAF.BMP");
			} catch (Exception e) {
				LOGGER.error(e);
			}
			// }
			// });
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	private static GUIManager singletonInstance = null;

	private ChessControllerKeyListener KEY_FORWARDER = new ChessControllerKeyListener();

	private ChatFrame chatFrame;

	private JFrame bugEarFrame;

	private List<ChessAreaControllerBase> chessAreaControllers = Collections
			.synchronizedList(new LinkedList<ChessAreaControllerBase>());

	private Preferences preferences;

	private boolean isAddingController = false;

	private List<Integer> gameIdsIgnoring = Collections
			.synchronizedList(new LinkedList<Integer>());

	private ICSCommunicationsDriver driver;

	private LinkedList<GameStartEvent> gameStartQueue = new LinkedList<GameStartEvent>();

	private GameEndSubscriber gameEndSubscriber;

	private GameStartQueueExecutor gameStartQueueExecutor;

	private LinkedList<ChessAreaController> recycledControllerQueue = new LinkedList<ChessAreaController>();

	private LinkedList<BugChessAreaController> recycledBughouseDelegateQueue = new LinkedList<BugChessAreaController>();

	private LinkedList<GameNotificationListener> gameNotificationListeners = new LinkedList<GameNotificationListener>();

	private List<DecafMenu> menus = new LinkedList<DecafMenu>();

	private LinkedList<Component> componentsWithKeyForwarders = new LinkedList<Component>();

	private boolean isApplet = false;

	public static GUIManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GUIManager();
		}
		return singletonInstance;
	}

	public ICSCommunicationsDriver getDriver() {
		return driver;
	}

	public void setDriver(ICSCommunicationsDriver driver) {
		this.driver = driver;
	}

	public void dispose() {

		try {
			removeAllControllers();
		} catch (Exception e) {
		}

		if (chatFrame != null) {
			try {
				chatFrame.dispose();
			} catch (Exception e) {
			}
		}
		if (bugEarFrame != null) {
			try {
				bugEarFrame.dispose();
			} catch (Exception e) {
			}
		}
		if (driver != null) {
			try {
				driver.disconnect();
			} catch (Exception e) {
			}
		}
		if (gameStartQueueExecutor != null) {
			try {
				gameStartQueueExecutor.dispose();
			} catch (Exception e) {
			}
		}
		if (gameNotificationListeners != null) {
			removeAllGameNotificationListeners();
		}
		singletonInstance = null;
	}

	private GUIManager() {

	}

	public void addGameNotificationListener(GameNotificationListener listener) {
		gameNotificationListeners.add(listener);
	}

	public void removeGameNotificationListener(GameNotificationListener listener) {
		gameNotificationListeners.remove(listener);
	}

	public void removeAllGameNotificationListeners() {
		gameNotificationListeners.clear();
	}

	public void tellLast() {
		inputFieldRequestFocus();
		chatFrame.getChatPanel().tellLast();
	}

	public void inputFieldRequestFocus() {
		GUIManager.getInstance().chatFrame.toFront();
		GUIManager.getInstance().chatFrame.requestFocus();
	}

	public boolean isApplet() {
		return isApplet;
	}

	public void setApplet(boolean isApplet) {
		this.isApplet = isApplet;
	}

	public void init(final Preferences preferences) {
		this.preferences = preferences;
		try {
			isApplet = ResourceManagerFactory.getManager()
					.getString("os", "os").equals("applet");
			SpeechManager.getInstance().init(preferences);
			setLookAndFeel();
			gameEndSubscriber = new GameEndSubscriber();
			chatFrame = new ChatFrame(preferences, ResourceManagerFactory
					.getManager().getString(
							PropertiesConstants.DECAF_PROPERTIES,
							PropertiesConstants.CHAT_FRAME_TITLE));
			chatFrame.setDefaultCloseOperation(0);
			chatFrame.addWindowListener(new ChatFrameClosingListener());

			if (getPreferences().getWindowLayoutStrategy() == Preferences.SNAP_TO_LAYOUT_EVERY_GAME_STRATEGY) {
				snapToChatFrame();
			} else {
				chatFrame.setLocation(preferences.getRememberChatLocation());
				chatFrame.setSize(preferences.getRememberChatDimension());
			}
			chatFrame
					.addComponentListener(new RememberPositionChatFrameListener());
			DecafMenu menu = new DecafMenu();
			menus.add(menu);
			chatFrame.setJMenuBar(menu);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					chatFrame.setVisible(true);
				}
			});

			// do the folliwng later on a seperate thread.
			ThreadManager.execute(new Runnable() {
				public void run() {
					EventService
							.getInstance()
							.subscribe(
									new Subscription(
											decaf.messaging.inboundevent.game.GameStartEvent.class,
											null,
											new GameStartEventSubscriber()));

					EventService
							.getInstance()
							.subscribe(
									new Subscription(
											decaf.messaging.inboundevent.game.MoveEvent.class,
											null, new ExaminedGameSubscriber()));
					gameStartQueueExecutor = new GameStartQueueExecutor();
					addKeyForwarder(chatFrame);
					initBugEarFrame();
					initializeChessAreaControllers();
					initializeSquareImageBackgroundAndSet();
					updateMenus();
				}
			});
		} catch (Throwable e) {
			LOGGER.error("Error occured Initializing GuiManager", e);
		}
	}

	public void addKeyForwarder(Component component) {
		// prevent double adds
		if (!componentsWithKeyForwarders.contains(component)) {
			List<Component> list = new LinkedList<Component>();
			list.add(component);
			addKeyForwarder(component, list);
			componentsWithKeyForwarders.add(component);
		}
	}

	public void showCaption(final String playerName, final String text) {
		ChessAreaControllerBase foundController = null;
		boolean isChessArea1 = false;
		boolean isWhite = false;

		for (int j = 0; foundController == null
				&& j < chessAreaControllers.size(); j++) {
			ChessAreaControllerBase current = chessAreaControllers.get(j);
			if (current.isActive() && current.isBughouse()) {
				if (current.getChessArea().getBlackName().equals(playerName)) {
					foundController = current;
					isWhite = false;
					isChessArea1 = true;
				} else if (current.getChessArea().getWhiteName().equals(
						playerName)) {
					foundController = current;
					isWhite = true;
					isChessArea1 = true;
				} else if (current.getPartnersChessArea().getWhiteName()
						.equals(playerName)) {
					foundController = current;
					isWhite = true;
					isChessArea1 = false;
				} else if (current.getPartnersChessArea().getBlackName()
						.equals(playerName)) {
					foundController = current;
					isWhite = false;
					isChessArea1 = false;
				}
			} else if (current.isActive() && !current.isBughouse()) {
				if (current.getChessArea().getBlackName().equals(playerName)) {
					foundController = current;
					isWhite = false;
					isChessArea1 = true;
				} else if (current.getChessArea().getWhiteName().equals(
						playerName)) {
					foundController = current;
					isWhite = true;
					isChessArea1 = true;
				}
			}
		}

		if (foundController != null && foundController.getFrame().isVisible()) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Showing caption: " + text);
			}

			final ChessArea area = isChessArea1 ? foundController
					.getChessArea() : foundController.getPartnersChessArea();

			if (isWhite) {
				area.showWhiteCaption(text);
			} else {
				area.showBlackCaption(text);
			}
		}
	}

	public void showPartnerCaption(final String text) {
		ChessAreaControllerBase bugController = null;
		boolean isWhite = false;

		for (int j = 0; bugController == null
				&& j < chessAreaControllers.size(); j++) {
			ChessAreaControllerBase controller = (ChessAreaControllerBase) chessAreaControllers
					.get(j);
			if (controller.isActive() && controller.isBughouse()) {
				if (controller.getPartnersChessArea().getBlackName().equals(
						User.getInstance().getBughousePartner())) {
					bugController = controller;
					isWhite = false;
				} else if (controller.getPartnersChessArea().getWhiteName()
						.equals(User.getInstance().getBughousePartner())) {
					bugController = controller;
					isWhite = true;
				}
			}
		}

		if (bugController != null && bugController.getFrame().isVisible()
				&& isWhite) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Showing caption on partners chess area: " + text);
			}

			final ChessAreaControllerBase finalBugController = bugController;
			finalBugController.getPartnersChessArea().showWhiteCaption(text);

		} else if (bugController != null
				&& bugController.getFrame().isVisible() && !isWhite) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Showing caption on partners chess area: " + text);
			}
			final ChessAreaControllerBase finalBugController = bugController;
			finalBugController.getPartnersChessArea().showBlackCaption(text);

		}
	}

	public boolean isBugGameActive() {
		synchronized (this) {
			boolean result = false;
			for (int j = 0; !result && j < chessAreaControllers.size(); j++) {
				ChessAreaControllerBase controller = (ChessAreaControllerBase) chessAreaControllers
						.get(j);
				if (controller.isActive() && controller.isBughouse()) {
					result = true;
				}
			}
			return result;
		}

	}

	public boolean isActive(int gameId) {
		synchronized (this) {
			boolean result = false;
			for (int j = 0; !result && j < chessAreaControllers.size(); j++) {
				ChessAreaControllerBase controller = (ChessAreaControllerBase) chessAreaControllers
						.get(j);
				if (controller.isActive()) {
					if (controller.isBughouse())
						result = controller.getGameId() == gameId
								|| controller.getPartnersGameId() == gameId;
					else
						result = controller.getGameId() == gameId;
				}
			}
			return result;
		}
	}

	public boolean isInactive(int gameId) {
		synchronized (this) {
			boolean result = false;
			for (int j = 0; !result && j < chessAreaControllers.size(); j++) {
				ChessAreaControllerBase controller = (ChessAreaControllerBase) chessAreaControllers
						.get(j);
				if (!controller.isActive()) {
					if (controller.isBughouse())
						result = controller.getGameId() == gameId
								|| controller.getPartnersGameId() == gameId;
					else
						result = controller.getGameId() == gameId;
				}
			}
			return result;
		}
	}

	public boolean isManaging(int gameId) {
		synchronized (this) {
			return isActive(gameId) || isInactive(gameId);
		}
	}

	public void setPreferences(Preferences preferences) {
		synchronized (this) {
			this.preferences = preferences;

			for (Iterator i = chessAreaControllers.iterator(); i.hasNext(); ((ChessAreaControllerBase) i
					.next()).setPreferences(preferences))
				;

			for (ChessAreaController controller : recycledControllerQueue) {
				controller.setPreferences(preferences);
			}

			for (BugChessAreaController controller : recycledBughouseDelegateQueue) {
				controller.setPreferences(preferences);
			}
			if (chatFrame != null) {
				chatFrame.setPreferences(preferences);
			}
			SpeechManager.getInstance().setPreferences(preferences);

			setLookAndFeel();

			driver.setPreferences(preferences);

		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void snapToChatFrame() {
		if (!isAddingController) {
			boolean isAutoScrolling = chatFrame.getChatPanel()
					.isAutoScrolling();
			chatFrame.setLocation(getPreferences().getChatPreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getChatPreferences()
					.getChatWindowDimension());
			if (isAutoScrolling) {
				forceChatScrollMax();
			}
			chatFrame.toFront();
		}
	}

	public void snapToBug() {
		if (!isAddingController) {
			boolean isAutoScrolling = chatFrame.getChatPanel()
					.isAutoScrolling();

			chatFrame.setLocation(getPreferences().getBughousePreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getBughousePreferences()
					.getChatWindowDimension());

			ChessAreaControllerBase bugController = getLastBugController();

			chatFrame.toFront();
			chatFrame.validate();

			if (bugController != null) {
				bugController.getFrame().setLocation(
						getPreferences().getBughousePreferences()
								.getGameWindowPoint());
				bugController.getFrame().setSize(
						getPreferences().getBughousePreferences()
								.getGameWindowDimension());
				bugController.getBughouseChessArea().setDividerLocation(
						getPreferences().getBughousePreferences()
								.getBoardSplitterLocation());
				bugController.getFrame().toFront();
			} else if (getPreferences().getWindowLayoutStrategy() == Preferences.REMEMBER_LAST_WINDOW_POSITION_STRATEGY) {
				getPreferences().setRememberBugDimension(
						getPreferences().getBughousePreferences()
								.getGameWindowDimension());
				getPreferences().setRememberBugLocation(
						getPreferences().getBughousePreferences()
								.getGameWindowPoint());
			}

			if (bugEarFrame != null) {
				bugEarFrame.setLocation(getPreferences()
						.getBughousePreferences().getBugEarPoint());
				bugEarFrame.setSize(getPreferences().getBughousePreferences()
						.getBugEarDimension());

				bugEarFrame.toFront();
			} else if (getPreferences().getWindowLayoutStrategy() == Preferences.REMEMBER_LAST_WINDOW_POSITION_STRATEGY) {
				getPreferences().setRememberBugEarDimension(
						getPreferences().getBughousePreferences()
								.getBugEarDimension());
				getPreferences().setRememberBugEarLocation(
						getPreferences().getBughousePreferences()
								.getBugEarPoint());
			}
			if (isAutoScrolling) {
				forceChatScrollMax();
			}

		}
	}

	public void snapToChess() {
		if (!isAddingController) {

			boolean isAutoScrolling = chatFrame.getChatPanel()
					.isAutoScrolling();
			chatFrame.setLocation(getPreferences().getBoardPreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getBoardPreferences()
					.getChatWindowDimension());
			chatFrame.validate();
			ChessAreaControllerBase chessController = getLastChessController();

			if (chessController != null) {
				chessController.getFrame().setLocation(
						getPreferences().getBoardPreferences()
								.getGameWindowPoint());
				chessController.getFrame().setSize(
						getPreferences().getBoardPreferences()
								.getGameWindowSize());
				chessController.getFrame().toFront();
			} else if (getPreferences().getWindowLayoutStrategy() == Preferences.REMEMBER_LAST_WINDOW_POSITION_STRATEGY) {
				getPreferences().setRememberChessDimension(
						getPreferences().getBoardPreferences()
								.getGameWindowSize());
				getPreferences().setRememberChessLocation(
						getPreferences().getBoardPreferences()
								.getGameWindowPoint());
			}

			chatFrame.toFront();

			if (isAutoScrolling) {
				forceChatScrollMax();
			}
		}
	}

	public void removeController(final ChessAreaControllerBase controller) {
		synchronized (this) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Entering removeICSController: "
						+ controller.getGameId() + " "
						+ +controller.getPartnersGameId());
			}

			boolean removed = chessAreaControllers.remove(controller);

			if (!removed) {
				LOGGER
						.error("Attempted to remove an ICSController that was not being managed!");
			}

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (controller.getFrame() != null) {
						controller.getFrame().setVisible(false);
					}
				}
			});

			if (controller.isObserving() && controller.isActive()) {
				EventService.getInstance().publish(
						new OutboundEvent(
								"unobserve " + controller.getGameId(), false));
				temporarilyIgnoreGame(controller.getGameId());
				if (controller.isBughouse()) {
					EventService.getInstance().publish(
							new OutboundEvent("unobserve "
									+ controller.getPartnersGameId(), false));
					temporarilyIgnoreGame(controller.getPartnersGameId());
				}
			} else if (controller.isExamining() && controller.isActive()) {
				EventService.getInstance().publish(
						new OutboundEvent("unexamine", false));
				temporarilyIgnoreGame(controller.getGameId());
			}

			// set the controller to inactive so it unsubscribes
			controller.setActive(false);

			if (controller.isBughouse()) {
				recycleBughouseChessAreaController((BugChessAreaController) controller);
			} else {
				recycleChessAreaController((ChessAreaController) controller);
			}
		}
		updateMenus();

		if (getPreferences().getBoardPreferences().isSnapToChatIfNoGames()
				&& chessAreaControllers.isEmpty()) {
			snapToChatFrame();
		}

	}

	public void removeAllNonPlayingControllers() {
		synchronized (this) {
			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);
				if (controller.isExamining() || !controller.isPlaying()) {
					removeController(controller);
					i--;
				}
			}
		}
	}

	public void removeAllInactiveControllers() {
		synchronized (this) {
			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);
				if (!controller.isActive()) {
					removeController(controller);
					i--;
				}
			}
		}
	}

	public ChessAreaControllerBase getPlayingController() {
		synchronized (this) {
			ChessAreaControllerBase result = null;
			for (int i = 0; result == null && i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);

				if (controller.isPlaying()) {
					result = controller;
				}
			}
			return result;
		}
	}

	public ChessAreaControllerBase getLastBugController() {
		synchronized (this) {
			ChessAreaControllerBase result = null;
			for (int i = 0; result == null && i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);

				if (controller.isBughouse()) {
					result = controller;
				}
			}
			return result;
		}
	}

	public ChessAreaControllerBase getLastChessController() {
		synchronized (this) {
			ChessAreaControllerBase result = null;
			for (int i = 0; result == null && i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);

				if (!controller.isBughouse()) {
					result = controller;
				}
			}
			return result;
		}
	}

	public boolean isBughouseLayout() {
		boolean result = false;
		for (int i = 0; !result && i < chessAreaControllers.size(); i++) {
			ChessAreaControllerBase controller = chessAreaControllers.get(i);

			if (controller.isBughouse()) {
				result = true;
			}
		}
		return result;
	}

	public boolean isChessLayout() {
		boolean result = false;
		if (!isBughouseLayout()) {
			for (int i = 0; !result && i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);

				if (!controller.isBughouse()) {
					result = true;
				}
			}
		}
		return result;
	}

	public void saveBughoueLayout() {
		synchronized (this) {
			ChessAreaControllerBase theController = null;
			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);

				if (controller.isBughouse()) {
					theController = controller;
				}
			}

			Point location = chatFrame.getLocation();
			Dimension dimension = chatFrame.getSize();

			getPreferences().getBughousePreferences().setChatWindowPoint(
					location);
			getPreferences().getBughousePreferences().setChatWindowDimension(
					dimension);

			location = theController.getFrame().getLocation();
			dimension = theController.getFrame().getSize();
			getPreferences().getBughousePreferences().setGameWindowPoint(
					location);
			getPreferences().getBughousePreferences().setGameWindowDimension(
					dimension);
			getPreferences().getBughousePreferences().setBoardSplitterLocation(
					theController.getBughouserDividerLocation());

			if (getPreferences().getBughousePreferences()
					.isShowingPartnerCommunicationButtons()
					&& bugEarFrame != null && bugEarFrame.isVisible()) {
				location = bugEarFrame.getLocation();
				dimension = bugEarFrame.getSize();
				getPreferences().getBughousePreferences().setBugEarPoint(
						location);
				getPreferences().getBughousePreferences().setBugEarDimension(
						dimension);
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Saving bughouse layout. Chat frame location: "
					+ getPreferences().getBughousePreferences()
							.getChatWindowPoint()
					+ " size="
					+ getPreferences().getBughousePreferences()
							.getChatWindowDimension());
			LOGGER.debug("Saving bughouse layout. Game frame location: "
					+ getPreferences().getBughousePreferences()
							.getGameWindowPoint()
					+ " size="
					+ getPreferences().getBughousePreferences()
							.getGameWindowDimension());
			LOGGER.debug("Saving bughouse layout. Bugear frame location: "
					+ getPreferences().getBughousePreferences()
							.getBugEarPoint()
					+ " size="
					+ getPreferences().getBughousePreferences()
							.getBugEarDimension());
		}

		ResourceManagerFactory.getManager().savePerferences(getPreferences());
	}

	public void saveChessLayout() {
		synchronized (this) {
			ChessAreaControllerBase theController = null;
			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);

				if (!controller.isBughouse()) {
					theController = controller;
				}
			}

			Point location = chatFrame.getLocation();
			Dimension dimension = chatFrame.getSize();

			getPreferences().getBoardPreferences().setChatWindowPoint(location);
			getPreferences().getBoardPreferences().setChatWindowDimension(
					dimension);

			location = theController.getFrame().getLocation();
			dimension = theController.getFrame().getSize();
			getPreferences().getBoardPreferences().setGameWindowPoint(location);
			getPreferences().getBoardPreferences().setGameWindowDimension(
					dimension);

		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Saving chess layout. Chat frame location: "
					+ getPreferences().getBoardPreferences()
							.getChatWindowPoint()
					+ " size="
					+ getPreferences().getBoardPreferences()
							.getChatWindowDimension());
			LOGGER.debug("Saving chess layout. Game frame location: "
					+ getPreferences().getBoardPreferences()
							.getGameWindowPoint()
					+ " size="
					+ getPreferences().getBoardPreferences()
							.getGameWindowSize());
		}

		ResourceManagerFactory.getManager().savePerferences(getPreferences());
	}

	public void saveChatLayout() {
		synchronized (this) {
			Point location = chatFrame.getLocation();
			Dimension dimension = chatFrame.getSize();

			getPreferences().getChatPreferences().setChatWindowPoint(location);
			getPreferences().getChatPreferences().setChatWindowDimension(
					dimension);
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Saving chess layout. Chat frame location: "
					+ getPreferences().getChatPreferences()
							.getChatWindowPoint()
					+ " size="
					+ getPreferences().getChatPreferences()
							.getChatWindowDimension());
		}

		ResourceManagerFactory.getManager().savePerferences(getPreferences());
	}

	public void removeAllControllers() {
		synchronized (this) {
			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);
				removeController(controller);
			}
		}
	}

	public void clearPremove() {
		ChessAreaControllerBase controller = getPlayingController();
		if (controller != null) {
			controller.clearPremove();
		}
	}

	public void fillScreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getDefaultScreenDevice().getDefaultConfiguration());

		Point controllerStartPoint = new Point(insets.left, insets.top);

		int totalWidth = screenSize.width - insets.left - insets.right;
		int totalHeight = screenSize.height - insets.top - insets.bottom;

		if (bugEarFrame.isVisible()) {
			bugEarFrame.setLocation(insets.left, insets.top);
			bugEarFrame.setSize(new Dimension(250, (int) (totalHeight * .7)));
			bugEarFrame.toFront();
			controllerStartPoint = new Point(insets.left
					+ bugEarFrame.getWidth() + 1, insets.top);
		}

		if (chessAreaControllers.size() > 0) {
			int numberOfControllers = chessAreaControllers.size();
			int widthLeft = totalWidth - controllerStartPoint.x;
			int widthPerController = widthLeft / numberOfControllers;
			int height = (int) (totalHeight * .7);

			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);

				controller.getFrame().setLocation(
						new Point(insets.left + controllerStartPoint.x
								+ widthPerController * i, insets.top));
				controller.getFrame().setSize(
						new Dimension(widthPerController, height));
				controller.getFrame().toFront();

				if (controller.isBughouse()) {
					controller.getBughouseChessArea().setDividerLocation(
							widthPerController / 2);
				}
			}

			chatFrame.setLocation(insets.left, insets.top
					+ (int) (totalHeight * .7) + 1);
			chatFrame.setSize(totalWidth, (int) (totalHeight * .3));
			chatFrame.toFront();
		} else {
			if (bugEarFrame.isVisible()) {
				chatFrame.setLocation(insets.left, insets.top
						+ (int) (totalHeight * .7) + 1);
				chatFrame.setSize(screenSize.width, (int) (totalHeight * .3));
				chatFrame.toFront();
			} else {
				chatFrame.setLocation(insets.left, insets.top);
				chatFrame.setSize(totalWidth, totalHeight);
				chatFrame.toFront();
			}
		}
	}

	private void fireGameStarted(ChessAreaControllerBase controller) {
		for (GameNotificationListener listener : gameNotificationListeners) {
			if (controller.isBughouse()) {
				listener.bugGameStarted((BugChessAreaController) controller);
			} else {
				listener.gameStarted((ChessAreaController) controller);
			}
		}
	}

	private void fireGameEnded(ChessAreaControllerBase controller) {
		synchronized(gameNotificationListeners)
		{
			for (GameNotificationListener listener : gameNotificationListeners) {
				if (controller.isBughouse()) {
					listener.bugGameEnded((BugChessAreaController) controller);
				} else {
					listener.gameEnded((ChessAreaController) controller);
				}
			}
		}
	}

	private void initBugEarFrame() {
		bugEarFrame = new JFrame("Bug Ear");
		bugEarFrame.add(new BugEarPanel(getPreferences()));
		bugEarFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		bugEarFrame.setIconImage(GUIManager.DECAF_ICON);
		bugEarFrame.addComponentListener(new RememberBugEarChatFrameListener());
		bugEarFrame.setSize(getPreferences().getBughousePreferences()
				.getBugEarDimension());
		bugEarFrame.setLocation(getPreferences().getBughousePreferences()
				.getBugEarPoint());

		try {
			if (ResourceManagerFactory.getManager().getString("os", "os")
					.equals("osx")) {
				DecafMenu menu = new DecafMenu();
				menus.add(menu);
				bugEarFrame.setJMenuBar(menu);
			}
		} catch (Exception e) {
		}

		bugEarFrame.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				bugEarFrame.setVisible(false);
			}

		});
	}

	private void updateMenus() {

		try {
			for (DecafMenu menu : menus) {
				menu.updateMenus();
			}
		} catch (ConcurrentModificationException cme) {
			LOGGER.warn(cme);
		}
	}

	private void decorateChessAreaController(ChessAreaControllerBase base) {
		addKeyForwarder(base.getFrame().getContentPane());
		addKeyForwarder(base.getChessArea().getMoveList());
		base.getFrame().addComponentListener(
				new RememberControllerListener(base));
		// new CursorCompilerListener(base);
		base.setPreferences(getPreferences());
		try {
			if (ResourceManagerFactory.getManager().getString("os", "os")
					.equals("osx")) {
				DecafMenu menu = new DecafMenu();
				menus.add(menu);
				base.getFrame().setJMenuBar(menu);
			}
		} catch (Exception e) {
		}
	}

	private void addKeyForwarder(Component component, List<Component> list) {
		if (component instanceof Container) {
			Container container = (Container) component;
			Component[] comps = container.getComponents();
			for (int i = 0; i < comps.length; i++) {

				if (chatFrame != null && chatFrame.getChatPanel() != null
						&& chatFrame.getChatPanel().getInputField() != null
						&& !list.contains(comps[i])
						&& comps[i] != chatFrame.getChatPanel().getInputField()) {
					list.add(comps[i]);
					addKeyForwarder(comps[i], list);
				}
			}
		} else {
			list.add(component);
		}

		component.addKeyListener(KEY_FORWARDER);
	}

	private ChessAreaController createChessAreaController(
			boolean recycleInactive) {
		synchronized (this) {

			if (recycleInactive) {
				ChessAreaControllerBase inactiveController = null;
				// Search backwards for a controller to recycle:
				for (int i = chessAreaControllers.size() - 1; inactiveController == null
						&& i >= 0; i--) {
					if (!chessAreaControllers.get(i).isActive()
							&& !chessAreaControllers.get(i).isBughouse()) {
						inactiveController = chessAreaControllers.get(i);
						chessAreaControllers.remove(i);
						break;
					}
				}

				if (inactiveController != null) {
					return (ChessAreaController) inactiveController;
				}
			}

			if (recycledControllerQueue.isEmpty()) {
				ChessAreaController result = new ChessAreaController();
				decorateChessAreaController(result);
				result.getFrame().setLocation(
						getPreferences().getRememberChessLocation());
				Dimension size = getPreferences().getRememberChessDimension();
				if (size.width < 2) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Too narrow, setting width to 640");
					}
					size.width = 640;
				}
				if (size.height < 2) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Too small, setting height to 480");
					}
					size.height = 480;
				}
				result.getFrame().setSize(size);
				return result;
			} else {
				ChessAreaController recycledController = recycledControllerQueue
						.removeFirst();

				if (recycledController.getChessArea() == null
						|| recycledController.getFrame() == null) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("recycledDelegateQueue: encountered a controller who was disposed. Purging controller");
					}
					recycledController.dispose();
					return createChessAreaController(recycleInactive);
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("recycledDelegateQueue: queue was not empty recycling old ChessAreaController");
					}
					return recycledController;
				}
			}
		}
	}

	private void initializeChessAreaControllers() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < INITIAL_CACHED_CHESS_AREA_CONTROLLERS; i++) {
			recycleChessAreaController(createChessAreaController(false));
		}
		for (int i = 0; i < INITIAL_CACHED_BUG_CHESS_AREA_CONTROLLERS; i++) {
			recycleBughouseChessAreaController(createBughoueChessAreaDelegate(false));
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("initializeDelegateQueues: "
					+ (System.currentTimeMillis() - startTime));
		}

	}

	private void initializeSquareImageBackgroundAndSet() {
		long startTime = System.currentTimeMillis();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		if (preferences.getBoardPreferences().getSquareImageBackground() != null) {
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					toolkit.prepareImage(preferences.getBoardPreferences()
							.getSquareImageBackground().getImage(i, j), -1, -1,
							null);
				}
			}
		}

		// Chess Piece images are prepared when the chess set is
		// initialized.
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Initialized SquareImageBackground/set in "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	private void recycleChessAreaController(ChessAreaController delegate) {
		synchronized (this) {
			if (recycledControllerQueue.size() <= MAX_CACHED_CHESS_AREA_CONTROLLERS) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a ChessAreaController is being recycled");
				}
				delegate.recycle();
				recycledControllerQueue.addLast(delegate);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a ChessAreaController has been executed");
				}
				if (delegate.getFrame().getJMenuBar() != null
						&& delegate.getFrame().getJMenuBar() instanceof DecafMenu) {
					menus.remove(delegate.getFrame().getJMenuBar());
				}
				delegate.dispose();

			}
		}
	}

	private BugChessAreaController createBughoueChessAreaDelegate(
			boolean recycleInactive) {
		synchronized (this) {

			if (recycleInactive) {
				ChessAreaControllerBase inactiveController = null;
				// Search backwards for a controller to recycle:
				for (int i = chessAreaControllers.size() - 1; inactiveController == null
						&& i >= 0; i--) {
					if (!chessAreaControllers.get(i).isActive()
							&& chessAreaControllers.get(i).isBughouse()) {
						inactiveController = chessAreaControllers.get(i);
						chessAreaControllers.remove(i);
					}
				}

				if (inactiveController != null) {
					return (BugChessAreaController) inactiveController;
				}
			}

			if (recycledBughouseDelegateQueue.isEmpty()) {
				BugChessAreaController result = new BugChessAreaController();
				decorateChessAreaController(result);
				result.getFrame().setLocation(
						getPreferences().getRememberBugLocation());
				result.getFrame().setSize(
						getPreferences().getRememberBugDimension());
				result.getBughouseChessArea().setDividerLocation(
						getPreferences().getRememberBugSliderPosition());
				return result;
			} else {
				BugChessAreaController result = recycledBughouseDelegateQueue
						.removeFirst();
				if (result.getFrame() == null || result.getChessArea() == null
						|| result.getPartnersChessArea() == null
						|| result.getBughouseChessArea() == null) {
					LOGGER
							.debug("recycledBughouseDelegateQueue: encountered a queued controller which was disposed. Purging controller.");
					result.dispose();
					return createBughoueChessAreaDelegate(recycleInactive);
				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("recycledBughouseDelegateQueue: queue was not empty recycling old BugChessAreaController");
					}
					return result;
				}
			}
		}
	}

	private void recycleBughouseChessAreaController(
			BugChessAreaController delegate) {
		synchronized (this) {
			if (recycledBughouseDelegateQueue.size() <= MAX_CACHED_BUG_CHESS_AREA_CONTROLLERS) {
				delegate.recycle();
				recycledBughouseDelegateQueue.addLast(delegate);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a BugChessAreaController is being recycled");
				}

			} else {
				delegate.dispose();
				if (delegate.getFrame() != null && 
						delegate.getFrame().getJMenuBar() != null
						&& delegate.getFrame().getJMenuBar() instanceof DecafMenu) {
					menus.remove(delegate.getFrame().getJMenuBar());
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a BugChessAreaController has been executed");
				}

			}
		}
	}

	private void forceChatScrollMax() {
		if (chatFrame.getChatPanel().isAutoScrolling()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					chatFrame.getChatPanel().setScrollBarToMax();
				}
			});
		}
	}

	private void setLookAndFeel() {

		if (!UIManager.getLookAndFeel().getClass().getName().equals(
				preferences.getLookAndFeelClassName())) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setting look and feel to: "
						+ preferences.getLookAndFeelClassName());
			}
			try {
				UIManager.setLookAndFeel(preferences.getLookAndFeelClassName());
			} catch (Exception e) {
				LOGGER.error("Unable to load look and feel:"
						+ preferences.getLookAndFeelClassName()
						+ "Installing system look and feel.", e);

				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception exception) {
					LOGGER.error("Could not load system look and feel",
							exception);
				}
			}

			synchronized (this) {
				if (chessAreaControllers != null) {
					for (int i = 0; i < chessAreaControllers.size(); i++) {
						SwingUtilities
								.updateComponentTreeUI(((ChessAreaControllerBase) chessAreaControllers
										.get(i)).getFrame());
					}
				}

				if (chatFrame != null) {
					SwingUtilities.updateComponentTreeUI(chatFrame);
				}
				if (bugEarFrame != null) {
					SwingUtilities.updateComponentTreeUI(bugEarFrame);
				}

				for (int i = 0; i < recycledControllerQueue.size(); i++) {
					SwingUtilities
							.updateComponentTreeUI(recycledControllerQueue.get(
									i).getFrame());
				}

				for (int i = 0; i < recycledBughouseDelegateQueue.size(); i++) {
					SwingUtilities
							.updateComponentTreeUI(recycledBughouseDelegateQueue
									.get(i).getFrame());
				}
			}
		}
	}

	private Point adjustPointForMultipleWindows(Point initialPoint) {
		int numberOfControllers = Math.abs(chessAreaControllers.size() - 1);

		return new Point(initialPoint.x + X_FRAME_ADJUSTMENT
				* numberOfControllers, initialPoint.y + Y_FRAME_ADJUSTMENT
				* numberOfControllers);
	}

	private void addController(final ChessAreaControllerBase controller) {
		synchronized (this) {
			isAddingController = true;
			ChessAreaControllerBase playingController = getPlayingController();

			if (controller.isPlaying() && !controller.isExamining()) {

				if (getPreferences().getBoardPreferences()
						.isUnfollowingOnPlayingGame()) {
					EventService.getInstance().publish(
							new OutboundEvent("unfollow"));
				}

				if (preferences.getBoardPreferences()
						.isClosingAllWindowsOnGameStart()) {
					removeAllNonPlayingControllers();
				}
			} else if (controller.isObserving()
					&& preferences.getBoardPreferences()
							.isCLosingInactiveGamesOnNewObservedGame()) {
				removeAllInactiveControllers();
			} else if (controller.isExamining()
					&& preferences.getBoardPreferences()
							.isCLosingInactiveGamesOnNewObservedGame()) {
				controller.getFrame().setDefaultCloseOperation(0);
			}
			fireGameStarted(controller);
			controller.getFrame().addWindowListener(
					new ChessControllerClosingListener(controller));
			chessAreaControllers.add(controller);

			if (controller.isPlaying()) {
				snapLayoutToController(controller);
				if (controller.getCommandToolbar() != null)
				{
					controller.getCommandToolbar().requestFocus();
				}
				else
				{
				    controller.getFrame().requestFocus();
				}

				if (controller.isBughouse()
						&& preferences.getBughousePreferences()
								.getAutoFirstWhiteMove() != null) {
					EventService.getInstance().publish(
							new OutboundEvent(preferences
									.getBughousePreferences()
									.getAutoFirstWhiteMove(), false));
				} else if (preferences.getBoardPreferences()
						.getAutoFirstWhiteMove() != null) {
					EventService.getInstance().publish(
							new OutboundEvent(preferences
									.getBughousePreferences()
									.getAutoFirstWhiteMove(), false));
				}

			} else if (playingController != null) {
				snapLayoutToController(controller);
				playingController.getFrame().toFront();
			} else {
				snapLayoutToController(controller);
			}

		}
		updateMenus();
		invalidateValidate(controller);

		isAddingController = false;

	}

	private void invalidateValidate(ChessAreaControllerBase controller) {
		if (controller.isBughouse()) {
			controller.getBughouseChessArea().invalidate();
			controller.getChessArea().invalidate();
			controller.getPartnersChessArea().invalidate();
			controller.getChessArea().getMoveList().invalidate();
			controller.getPartnersChessArea().getMoveList().invalidate();
		} else {
			controller.getChessArea().invalidate();
			controller.getChessArea().getMoveList().invalidate();
		}
		controller.getFrame().getContentPane().validate();
	}

	private void snapLayoutToController(ChessAreaControllerBase controller) {
		if (getPreferences().getWindowLayoutStrategy() == Preferences.SNAP_TO_LAYOUT_EVERY_GAME_STRATEGY) {
			// The controllers settings take precednce over everything else.
			if (controller.isBughouse()) {
				Point location = adjustPointForMultipleWindows(getPreferences()
						.getBughousePreferences().getGameWindowPoint());
				controller.getFrame().setLocation(location);
				controller.getFrame().setSize(
						getPreferences().getBughousePreferences()
								.getGameWindowDimension());
				controller.getBughouseChessArea().setDividerLocation(
						getPreferences().getBughousePreferences()
								.getBoardSplitterLocation());

				chatFrame.setLocation(getPreferences().getBughousePreferences()
						.getChatWindowPoint());
				chatFrame.setSize(getPreferences().getBughousePreferences()
						.getChatWindowDimension());
				chatFrame.validate();

				if (controller.isPlaying()
						&& getPreferences().getBughousePreferences()
								.isShowingPartnerCommunicationButtons()) {
					if (bugEarFrame != null) {
						bugEarFrame.setLocation(getPreferences()
								.getBughousePreferences().getBugEarPoint());
						bugEarFrame.setSize(getPreferences()
								.getBughousePreferences().getBugEarDimension());
						addKeyForwarder(bugEarFrame);
					}
				}

			} else {
				Point location = adjustPointForMultipleWindows(getPreferences()
						.getBoardPreferences().getGameWindowPoint());
				controller.getFrame().setLocation(location);
				controller.getFrame().setSize(
						getPreferences().getBoardPreferences()
								.getGameWindowSize());

				chatFrame.setLocation(getPreferences().getBoardPreferences()
						.getChatWindowPoint());
				chatFrame.setSize(getPreferences().getBoardPreferences()
						.getChatWindowDimension());
				chatFrame.validate();
			}

			if (chatFrame.getChatPanel().isAutoScrolling()) {
				forceChatScrollMax();
			}
		} else {
			if (controller.isBughouse()) {
				Point location = adjustPointForMultipleWindows(getPreferences()
						.getRememberBugLocation());
				controller.getFrame().setLocation(location);
				controller.getFrame().setSize(
						getPreferences().getRememberBugDimension());
				controller.getBughouseChessArea().setDividerLocation(
						getPreferences().getRememberBugSliderPosition());

				if (bugEarFrame.isVisible()) {
					bugEarFrame.setLocation(getPreferences()
							.getRememberBugEarLocation());
					bugEarFrame.setSize(getPreferences()
							.getRememberBugEarDimension());
				}
			} else {
				Point location = adjustPointForMultipleWindows(getPreferences()
						.getRememberChessLocation());
				controller.getFrame().setLocation(location);
				controller.getFrame().setSize(
						getPreferences().getRememberChessDimension());

			}
		}

		if (controller.isPlaying()
				|| getPreferences().getBoardPreferences().getGamesToFrontMode() == BoardPreferences.ALL_GAMES_TO_FRONT) {
			chatFrame.toFront();
			controller.getFrame().toFront();
		}

		if (controller.isPlaying()
				&& controller.isBughouse()
				&& getPreferences().getBughousePreferences()
						.isShowingPartnerCommunicationButtons()) {
			if (!bugEarFrame.isVisible()) {
				bugEarFrame.setVisible(true);
			}
			bugEarFrame.toFront();
		}

		controller.getFrame().setVisible(true);
	}
	
	private void requestToolbarFocus(final ChessAreaControllerBase controller)
	{
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (controller.getCommandToolbar() != null) {
					controller.getCommandToolbar().requestToolbarFocus();
				} else {
					controller.getChessArea().getBoard().get(0, 0)
							.setRequestFocusEnabled(true);
					controller.getChessArea().getBoard().get(0, 0)
							.requestFocus();
				}
			}
		});		
	}

	private void temporarilyIgnoreGame(final int gameId) {
		synchronized (this) {
			gameIdsIgnoring.add(new Integer(gameId));
		}
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				gameIdsIgnoring.remove(new Integer(gameId));
			}
		}, 2250);
	}

	private boolean isIgnoring(int gameId) {
		synchronized (this) {
			return gameIdsIgnoring.contains(new Integer(gameId));
		}
	}

	public class GameStartEventSubscriber implements Subscriber {
		public void inform(GameStartEvent event) {
			synchronized (GUIManager.getInstance()) {
				gameStartQueue.addLast(event);
				gameStartQueueExecutor.interrupt();
			}
		}
	}

	// Examined games come accross as a MoveEvent that is not being managed.
	public class ExaminedGameSubscriber implements Subscriber {
		public void inform(MoveEvent moveEvent) {
			// now check in synchronized fashion.
			synchronized (GUIManager.getInstance()) {
				if (!isActive(moveEvent.getGameId())
						&& !isIgnoring(moveEvent.getGameId())) {

					if (moveEvent.getRelation() == MoveEvent.EXAMINING_GAME_RELATION) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER
									.debug("Received moveEvent whose game wasnt being managed, assuming its an examined game since no bug games are active and this is the first move. "
											+ moveEvent.getGameId());
						}
						ChessAreaController delegate = createChessAreaController(preferences
								.getBoardPreferences()
								.isCLosingInactiveGamesOnNewObservedGame());
						delegate.setupExamine(moveEvent);
						addController(delegate);
					} else {
						LOGGER
								.debug("Received moveEvent whose game wasnt being managed and is not an examined game. Ignoring move assuming its a timing issue. "
										+ moveEvent.getGameId());
					}
				}
			}
		}
	}

	public class GameEndSubscriber implements Subscriber {
		public GameEndSubscriber() {
			;
			EventService
					.getInstance()
					.subscribe(
							new Subscription(
									decaf.messaging.inboundevent.game.GameEndEvent.class,
									null, this));
		}

		public void inform(GameEndEvent event) {
			for (ChessAreaControllerBase controller : chessAreaControllers) {
				if (event.getGameId() == controller.getGameId()) {
					fireGameEnded(controller);
					break;
				}
			}
		}
	}

	// EventService is like the the us government, it must see ALL so this cant
	// be private even though it should be.
	public class OrpahnMoveEventSubscriber implements Subscriber {
		private Subscription subscription;

		private MoveEvent moveEventQueued = null;

		private GameStartEvent gameStartEvent = null;

		public OrpahnMoveEventSubscriber(GameStartEvent event) {
			this.gameStartEvent = event;
			EventService.getInstance().subscribe(
					subscription = new Subscription(
							decaf.messaging.inboundevent.game.MoveEvent.class,
							null, this));
		}

		public void inform(MoveEvent event) {

			boolean isWatchingGame = gameStartEvent.getGameId() == event
					.getGameId();

			if (isWatchingGame) {
				moveEventQueued = event;
			}
		}

		public MoveEvent getOrphanEvent() {
			return moveEventQueued;
		}

		public void dispose() {
			EventService.getInstance().unsubscribe(subscription);
		}
	}

	public class GameStartQueueExecutor implements Runnable {

		private BugChessAreaController delegateWaitingOnPartnersGameStart = null;

		private Thread thread;

		private boolean commitSuicide;

		public GameStartQueueExecutor() {
			thread = new Thread(this);
			thread.start();
		}

		public void dispose() {
			commitSuicide = true;
			thread.interrupt();
		}

		private boolean isPlaying(GameStartEvent event) {
			return event.getBlackName().equalsIgnoreCase(
					User.getInstance().getHandle())
					|| event.getWhiteName().equalsIgnoreCase(
							User.getInstance().getHandle());
		}

		private void handleNonBugGameStart(GameStartEvent event) {
			synchronized (GUIManager.getInstance()) {
				temporarilyIgnoreGame(event.getGameId());
				// To handle the case where move events are received before the
				// delegate gets created and subscribed we must queue them as
				// well.
				OrpahnMoveEventSubscriber orphanSubscriber = new OrpahnMoveEventSubscriber(
						event);

				ChessAreaController delegate = createChessAreaController(isPlaying(event) ? true
						: preferences.getBoardPreferences()
								.isCLosingInactiveGamesOnNewObservedGame());

				delegate.setup(event);

				// Stop listening for orphaned moves now the subscriber is all
				// set
				// up.
				orphanSubscriber.dispose();

				// update with orphaned move.
				if (orphanSubscriber.getOrphanEvent() != null) {
					// grab orphaned event if its there.
					delegate.getStyle12Subscriber().inform(
							orphanSubscriber.getOrphanEvent());

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Sending oprhaned move to chess area ...");
					}
				}
				addController(delegate);
			}
		}

		private void handleSettingUpPartnersBugBoard(
				GameStartEvent partnerGameStart) {
			synchronized (GUIManager.getInstance()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER
							.debug("GameStartQueueExecutor setting up partners board");
				}
				OrpahnMoveEventSubscriber partnersGameOrphanSubscriber = new OrpahnMoveEventSubscriber(
						partnerGameStart);

				delegateWaitingOnPartnersGameStart
						.setObservingGameStartEvent(partnerGameStart);

				partnersGameOrphanSubscriber.dispose();

				// update with orphaned move.
				if (partnersGameOrphanSubscriber.getOrphanEvent() != null) {
					// grab orphaned event if its there.
					delegateWaitingOnPartnersGameStart
							.getPartnersStyle12Subscriber().inform(
									partnersGameOrphanSubscriber
											.getOrphanEvent());

					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("Sending oprhaned move to bughosue chess area ...");
					}
				}
				// Set it to null after handling it.
				delegateWaitingOnPartnersGameStart = null;
			}

		}

		private void handleInitialBugGameStart(GameStartEvent event) {
			synchronized (GUIManager.getInstance()) {
				// To handle the case where move events are received
				// before the delegate gets created and subscribed
				// we must queue them as well.
				final int gameId = event.getGameId();
				final int partnersGameId = Integer.parseInt(event
						.getG1Param("pt"));
				temporarilyIgnoreGame(gameId);
				temporarilyIgnoreGame(partnersGameId);

				OrpahnMoveEventSubscriber orphanSubscriber = new OrpahnMoveEventSubscriber(
						event);

				// Send the pobs (dont use pfollow because it gets
				// difficult to tell if its an obs game or not).
				EventService.getInstance().publish(
						new OutboundEvent("obs " + event.getG1Param("pt")));

				// Preform the expensive operations.
				delegateWaitingOnPartnersGameStart = createBughoueChessAreaDelegate(isPlaying(event) ? true
						: preferences.getBoardPreferences()
								.isCLosingInactiveGamesOnNewObservedGame());
				delegateWaitingOnPartnersGameStart.setup(event);

				// Stop listening for orphaned moves now the
				// subscriber is all set up.
				orphanSubscriber.dispose();

				// update with orphaned move.
				if (orphanSubscriber.getOrphanEvent() != null) {
					// grab orphaned event if its there.
					delegateWaitingOnPartnersGameStart.getStyle12Subscriber()
							.inform(orphanSubscriber.getOrphanEvent());

					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("Sending oprhaned move to bughosue chess area ...");
					}
				}

				// This displays and can be expensive.
				addController(delegateWaitingOnPartnersGameStart);
			}
		}

		public void interrupt() {
			thread.interrupt();
		}

		public void run() {
			while (true) {
				if (commitSuicide == true) {
					return;
				}

				// clear interrupted status.
				Thread.interrupted();

				if (LOGGER.isDebugEnabled()) {
					LOGGER
							.debug("GameStartQueueExecutor reporting for duty. isIgnoringExaminedGames=");
				}

				synchronized (GUIManager.getInstance()) {
					try {
						if (!gameStartQueue.isEmpty()) {
							GameStartEvent event = gameStartQueue.removeLast();

							if (delegateWaitingOnPartnersGameStart != null) {
								if (!event.isBughouse()) {
									handleNonBugGameStart(event);
								} else {
									handleSettingUpPartnersBugBoard(event);
								}
							} else {
								if (!event.isBughouse()) {
									handleNonBugGameStart(event);
								} else {

									handleInitialBugGameStart(event);
								}
							}
						}
					} catch (Throwable t) {
						LOGGER
								.error(
										"Unexpected exception occured in the GameStartQueueExecutor:",
										t);
					}
				}

				if (!Thread.interrupted()) {
					try {
						if (LOGGER.isDebugEnabled()) {
							LOGGER
									.debug("GameStartQueueExecutor has fallen asleep.");
						}

						Thread.sleep(600000);
					} catch (InterruptedException ie) {
					}
				}
			}
		}
	}

	private class RememberControllerListener extends ComponentAdapter {
		private ChessAreaControllerBase controller;

		public RememberControllerListener(ChessAreaControllerBase controller) {
			this.controller = controller;
			controller.getFrame().addComponentListener(this);

			if (controller.isBughouse()) {
				controller.getChessArea().addComponentListener(this);
			}
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
			saveSettings();
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			saveSettings();
		}

		private void saveSettings() {
			Point location = controller.getFrame().getLocation();
			Dimension dimension = controller.getFrame().getSize();

			if (controller.isBughouse()) {
				int splitterLocation = controller.getBughouserDividerLocation();

				getPreferences().setRememberBugDimension(dimension);
				getPreferences().setRememberBugLocation(location);
				getPreferences().setRememberBugSliderPosition(splitterLocation);
			} else {
				getPreferences().setRememberChessDimension(dimension);
				getPreferences().setRememberChessLocation(location);

			}

			LOGGER.debug("Saving chess dimension frame size");
			ResourceManagerFactory.getManager().savePerferences(
					getPreferences());
		}
	}

	private class RememberBugEarChatFrameListener extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent arg0) {
			saveSettings();
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			saveSettings();
		}

		private void saveSettings() {
			Point location = bugEarFrame.getLocation();
			Dimension dimension = bugEarFrame.getSize();

			getPreferences().setRememberBugEarDimension(dimension);
			getPreferences().setRememberBugEarLocation(location);

			ResourceManagerFactory.getManager().savePerferences(
					getPreferences());

		}

	}

	private class RememberPositionChatFrameListener extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent arg0) {

			saveSettings();
		}

		@Override
		public void componentMoved(ComponentEvent arg0) {
			saveSettings();
		}

		private void saveSettings() {
			Point location = chatFrame.getLocation();
			Dimension dimension = chatFrame.getSize();

			getPreferences().setRememberChatDimension(dimension);
			getPreferences().setRememberChatLocation(location);

			ResourceManagerFactory.getManager().savePerferences(
					getPreferences());
		}

	}

	private class ChessControllerKeyListener extends KeyAdapter {

		private KeyMapper keyMapper = new KeyMapper();

		public ChessControllerKeyListener() {
		}

		public void keyTyped(KeyEvent arg0) {

			int keyCode = arg0.getKeyCode();
			char keyChar = arg0.getKeyChar();

			if (!keyMapper.process(arg0)) {
				if (keyCode == KeyEvent.VK_SCROLL_LOCK) {
					chatFrame.getChatPanel().setAutoScrolling(
							!chatFrame.getChatPanel().isAutoScrolling());
				} else if (keyCode == KeyEvent.VK_ESCAPE) {
					clearPremove();
				} else if (!arg0.isAltDown() && !arg0.isControlDown()

				&& !arg0.isMetaDown() && keyChar >= 32 && keyChar <= 126) {

					LOGGER.debug("Forwarding " + keyChar);
					inputFieldRequestFocus();
					chatFrame.getChatPanel().forwardKeyEvent(arg0);

				}
			}
		}
	}

	private class ChatFrameClosingListener extends WindowAdapter {

		public void windowClosing(WindowEvent windowevent) {
			if (!isApplet) {
				System.exit(1);
			} else {
				getInstance().dispose();
			}
		}

		public ChatFrameClosingListener() {
		}
	}

	private class ChessControllerClosingListener extends WindowAdapter {

		public void windowClosing(WindowEvent windowevent) {

			if (controller.isPlaying() && !controller.isExamining()
					&& controller.isActive()) {
				JOptionPane.showMessageDialog(controller.getFrame(),
						"You cant close a game you are playing.");
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER
							.debug("Caught window closing event, removing icsController: "
									+ controller.getGameId());
				}
				removeController(controller);
			}
		}

		public void windowOpened(WindowEvent arg0) {
			LOGGER.debug("Inside window opening");
			requestToolbarFocus(controller);
		}

		private ChessAreaControllerBase controller;

		public ChessControllerClosingListener(
				ChessAreaControllerBase icscontroller) {
			controller = icscontroller;
		}
	}
}