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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import decaf.com.CommunicationsDriver;
import decaf.com.inboundevent.game.GameStartEvent;
import decaf.com.inboundevent.game.GameTypes;
import decaf.com.inboundevent.game.MoveEvent;
import decaf.com.outboundevent.OutboundEvent;
import decaf.com.outboundevent.UnexamineRequestEvent;
import decaf.com.outboundevent.UnobserveRequestEvent;
import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.gui.util.PropertiesConstants;
import decaf.gui.util.PropertiesManager;
import decaf.gui.widgets.BugEarPanel;
import decaf.speech.SpeechManager;

public class GUIManager implements Preferenceable {

	private static final int INITIAL_CHESS_AREA_DELEGATE_QUEUE = 2;

	private static final int INITIAL_BUGHOUSE_CHESS_AREA_DELEGATE_QUEUE = 2;

	private static final int MAX_CHESS_AREA_DELEGATE_QUEUE = 5;

	private static final int MAX_BUGHOUSE_CHESS_AREA_DELEGATE_QUEUE = 3;

	private static final Logger LOGGER = Logger.getLogger(GUIManager.class);

	private static final int Y_FRAME_ADJUSTMENT = 20;

	private static final int X_FRAME_ADJUSTMENT = 10;

	private static final PropertiesManager resourceRepository = PropertiesManager
			.getInstance();

	private static GUIManager singletonInstance = null;

	private EventService eventService;

	private ChatFrame chatFrame;

	private JFrame bugEarFrame;

	private List<ChessAreaControllerBase> chessAreaControllers = Collections
			.synchronizedList(new LinkedList<ChessAreaControllerBase>());

	private Preferences preferences;

	private boolean isAddingController = false;

	private List<Integer> gameIdsIgnoring = Collections
			.synchronizedList(new LinkedList<Integer>());
	
	private boolean isIgnoringExaminedGames = false;

	private CommunicationsDriver driver;

	private LinkedList<GameStartEvent> gameStartQueue = new LinkedList<GameStartEvent>();

	private GameStartQueueExecutor gameStartQueueExecutor;

	private LinkedList<ChessAreaController> recycledDelegateQueue = new LinkedList<ChessAreaController>();

	private LinkedList<BugChessAreaController> recycledBughouseDelegateQueue = new LinkedList<BugChessAreaController>();

	public static GUIManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new GUIManager();
		}
		return singletonInstance;
	}

	public CommunicationsDriver getDriver() {
		return driver;
	}

	public void setDriver(CommunicationsDriver driver) {
		this.driver = driver;
	}

	public void dispose() {
		removeAll();

		if (chatFrame != null) {
			chatFrame.dispose();
		}
		if (bugEarFrame != null) {
			bugEarFrame.dispose();
		}
		if (driver != null) {
			driver.disconnect();
		}
		if (gameStartQueueExecutor != null) {
			gameStartQueueExecutor.dispose();
		}
		singletonInstance = null;
	}

	private GUIManager() {

	}

	public void init(Preferences preferences) {
		synchronized(this)
		{
			this.preferences = preferences;
			eventService = EventService.getInstance();
	
			SpeechManager.getInstance().init(preferences);
	
			setLookAndFeel();
			eventService.subscribe(new Subscription(
					decaf.com.inboundevent.game.GameStartEvent.class, null,
					new GameStartEventSubscriber()));
	
			eventService.subscribe(new Subscription(
					decaf.com.inboundevent.game.MoveEvent.class, null,
					new ExaminedGameSubscriber()));
	
			chatFrame = new ChatFrame(preferences, resourceRepository.getString(
					PropertiesConstants.DECAF_PROPERTIES,
					PropertiesConstants.CHAT_FRAME_TITLE));
	
			gameStartQueueExecutor = new GameStartQueueExecutor();
	
			chatFrame.setDefaultCloseOperation(0);
			chatFrame.addWindowListener(new ChatFrameClosingListener());
	
			snapToChatFrame();
			chatFrame.updateMenus();
			chatFrame.setVisible(true);
		}

		initializeDelegateQueues();
	}

	private ChessAreaController createChessAreaDelegate() {
		synchronized(this)
		{
			if (recycledDelegateQueue.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER
							.debug("createChessAreaDelegate: queue was empty creating a new ChessAreaDelegate");
				}
				ChessAreaController result = new ChessAreaController();
				result.setPreferences(preferences);
				return result;
			} else {
				ChessAreaController recycledController =  recycledDelegateQueue.removeFirst();
				
				if (recycledController.getChessArea() == null || recycledController.getFrame() == null)
				{
					LOGGER
					.debug("recycledDelegateQueue: encountered a controller who was disposed. Purging controller");
					recycledController.dispose();
					return createChessAreaDelegate();
				}
				else
				{
					if (LOGGER.isDebugEnabled()) {
						LOGGER
							.debug("recycledDelegateQueue: queue was not empty recycling old ChessAreaController");
					}
					return recycledController;
				}
			}
		}
	}

	private void initializeDelegateQueues() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				long startTime = System.currentTimeMillis();
				for (int i = 0; i < INITIAL_CHESS_AREA_DELEGATE_QUEUE; i++) {
					ChessAreaController controller = new ChessAreaController();
					controller.setPreferences(preferences);
					recycleChessAreaDelegate(controller);
				}
				for (int i = 0; i < INITIAL_BUGHOUSE_CHESS_AREA_DELEGATE_QUEUE; i++) {

					BugChessAreaController controller = new BugChessAreaController();
					controller.setPreferences(preferences);
					recycleBughouseChessAreaDelegate(controller);
				}

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("initializeDelegateQueues: "
							+ (System.currentTimeMillis() - startTime));
				}

			}
		});
		thread.start();
	}

	private void recycleChessAreaDelegate(ChessAreaController delegate) {
		synchronized(this)
		{
			if (recycledDelegateQueue.size() <= MAX_CHESS_AREA_DELEGATE_QUEUE) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a ChessAreaController is being recycled");
				}
	
				delegate.prepareForRecyling();
				recycledDelegateQueue.addLast(delegate);
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a ChessAreaController has been executed");
				}
	
				delegate.dispose();
			}
		}
	}

	public BugChessAreaController createBughoueChessAreaDelegate() {
		synchronized(this)
		{
			if (recycledBughouseDelegateQueue.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER
							.debug("recycledBughouseDelegateQueue: queue was empty creating a new BugChessAreaController");
				}
				BugChessAreaController result = new BugChessAreaController();
				result.setPreferences(preferences);
				return result;
			} else {
				BugChessAreaController result = recycledBughouseDelegateQueue.removeFirst();
				if (result.getFrame() == null || result.getChessArea() == null || result.getPartnersChessArea() == null || result.getBughouseChessArea() == null)
				{
					LOGGER
					.debug("recycledBughouseDelegateQueue: encountered a queued controller which was disposed. Purging controller.");					
					result.dispose();
					return createBughoueChessAreaDelegate();
				}
				else
				{
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("recycledBughouseDelegateQueue: queue was not empty recycling old BugChessAreaController");
					}
					return result;
				}
			}
		}

	}

	private void recycleBughouseChessAreaDelegate(
			BugChessAreaController delegate) {
		synchronized(this)
		{
			if (recycledBughouseDelegateQueue.size() <= MAX_BUGHOUSE_CHESS_AREA_DELEGATE_QUEUE) {
				delegate.prepareForRecyling();
				recycledBughouseDelegateQueue.addLast(delegate);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a BugChessAreaController is being recycled");
				}
	
			} else {
				delegate.dispose();
	
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("a BugChessAreaController has been executed");
				}
	
			}
		}
	}

	private void forceChatScrollMax() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatFrame.getChatPanel().setScrollBarToMax();
			}
		});
	}

	public boolean isBugGameActive() {
		synchronized(this)
		{
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
		synchronized(this)
		{
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
		synchronized(this)
		{
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
		return isActive(gameId) || isInactive(gameId);
	}

	public void setPreferences(Preferences preferences) {
		synchronized(this)
		{
			this.preferences = preferences;
	
			if (chessAreaControllers != null) {
				for (Iterator i = chessAreaControllers.iterator(); i.hasNext(); ((ChessAreaControllerBase) i
						.next()).setPreferences(preferences))
					;
			}
	
			for (ChessAreaController controller : recycledDelegateQueue) {
				controller.setPreferences(preferences);
			}
	
			for (BugChessAreaController controller : recycledBughouseDelegateQueue) {
				controller.setPreferences(preferences);
			}
			if (chatFrame != null) {
				chatFrame.setPreferences(preferences);
			}
			SpeechManager.getInstance().setPreferences(preferences);
	
			if (chessAreaControllers != null) {
				setLookAndFeel();
			}
		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	private void setLookAndFeel() {
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
				LOGGER.error("Could not load system look and feel", exception);
			}
		}

		synchronized(this)
		{
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
		}
	}

	private Point getInitialPointForNewChessWindow() {
		int numberOfControllers = chessAreaControllers.size();
		return new Point(
				preferences.getBoardPreferences().getGameWindowPoint().x
						+ X_FRAME_ADJUSTMENT * numberOfControllers, preferences
						.getBoardPreferences().getGameWindowPoint().y
						+ Y_FRAME_ADJUSTMENT * numberOfControllers);
	}

	private Point getInitialPointForNewBugWindow() {
		int numberOfControllers = chessAreaControllers.size();
		return new Point(preferences.getBughousePreferences()
				.getGameWindowPoint().x
				+ X_FRAME_ADJUSTMENT * numberOfControllers, preferences
				.getBughousePreferences().getGameWindowPoint().y
				+ Y_FRAME_ADJUSTMENT * numberOfControllers);
	}

	private void addICSController(final ChessAreaControllerBase controller) {
		synchronized(this)
		{
			isAddingController = true;
			boolean previouslySetupForBug = isBughouseLayout();
			boolean previouslySetupForChess = isChessLayout();
			boolean closedInactiveWindows = false;
			boolean isPlaying = controller.isPlaying();
			ChessAreaControllerBase playingController = getPlayingController();
	
			if (controller.isPlaying()) {
				removeAllControllers();
				closedInactiveWindows = true;
				controller.getFrame().addKeyListener(
						new ChessControllerKeyListener(controller));
			} else if (controller.isObserving()
					&& preferences.getBoardPreferences()
							.isCLosingInactiveGamesOnNewObservedGame()) {
				removeAllInactiveControllers();
				closedInactiveWindows = true;
			} else if (controller.isExamining()
					&& preferences.getBoardPreferences()
							.isCLosingInactiveGamesOnNewObservedGame()) {
				controller.getFrame().setDefaultCloseOperation(0);
			}
			controller.getFrame().addWindowListener(
					new ChessControllerClosingListener(controller));
			chessAreaControllers.add(controller);
	
			if (controller.isPlaying()) {
				snapLayoutToController(controller);
				controller.getFrame().requestFocus();
	
				if (controller.isBughouse()
						&& preferences.getBughousePreferences()
								.getAutoFirstWhiteMove() != null) {
					EventService.getInstance().publish(
							new OutboundEvent(preferences.getBughousePreferences()
									.getAutoFirstWhiteMove(), false));
				} else if (preferences.getBoardPreferences()
						.getAutoFirstWhiteMove() != null) {
					EventService.getInstance().publish(
							new OutboundEvent(preferences.getBughousePreferences()
									.getAutoFirstWhiteMove(), false));
				}
	
			} else if (playingController != null) {
				snapLayoutToController(controller);
				playingController.getFrame().toFront();
				playingController.getFrame().requestFocus();
			} else {
				snapLayoutToController(controller);
				controller.getFrame().requestFocus();
			}
	
			chatFrame.updateMenus();
	
			controller.getFrame().invalidate();
			controller.getFrame().setVisible(true);
	
			isAddingController = false;
		}
	}

	public void snapToChatFrame() {
		if (!isAddingController) {
			boolean isAutoScrolling = chatFrame.getChatPanel().isAutoScrolling();
			chatFrame.setLocation(getPreferences().getChatPreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getChatPreferences()
					.getChatWindowDimension());
			if (isAutoScrolling)
			{
			   forceChatScrollMax();
			}
			chatFrame.toFront();
		}
	}

	public void snapToBug() {
		if (!isAddingController) {
			boolean isAutoScrolling = chatFrame.getChatPanel().isAutoScrolling();
			
			chatFrame.setLocation(getPreferences().getBughousePreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getBughousePreferences()
					.getChatWindowDimension());

			ChessAreaControllerBase bugController = getLastBugController();

			chatFrame.toFront();

			if (bugController != null) {
				bugController.getFrame().setLocation(
						getPreferences().getBughousePreferences()
								.getGameWindowPoint());
				bugController.getFrame().setSize(
						getPreferences().getBughousePreferences()
								.getGameWindowDimension());

				bugController.getFrame().toFront();

			}
			if (bugEarFrame != null) {
				bugEarFrame.setLocation(getPreferences()
						.getBughousePreferences().getBugEarPoint());
				bugEarFrame.setSize(getPreferences().getBughousePreferences()
						.getBugEarDimension());

				bugEarFrame.toFront();
			}
			if (isAutoScrolling)
			{
			   forceChatScrollMax();
			}

		}
	}

	public void snapToChess() {
		if (!isAddingController) {
			
			boolean isAutoScrolling = chatFrame.getChatPanel().isAutoScrolling();
			chatFrame.setLocation(getPreferences().getBoardPreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getBoardPreferences()
					.getChatWindowDimension());

			ChessAreaControllerBase chessController = getLastChessController();

			if (chessController != null) {
				chessController.getFrame().setLocation(
						getPreferences().getBoardPreferences()
								.getGameWindowPoint());
				chessController.getFrame().setSize(
						getPreferences().getBoardPreferences()
								.getGameWindowSize());
			}

			chessController.getFrame().toFront();
			chatFrame.toFront();
			
			if (isAutoScrolling)
			{
			   forceChatScrollMax();
			}
		}
	}

	private void snapLayoutToController(ChessAreaControllerBase controller) {
		// The controllers settings take precednce over everything else.
		boolean isAutoScrolling = chatFrame.getChatPanel().isAutoScrolling();
		if (controller.isBughouse()) {
			controller.getFrame().setLocation(getInitialPointForNewBugWindow());
			controller.getFrame().setSize(
					getPreferences().getBughousePreferences()
							.getGameWindowDimension());

			chatFrame.setLocation(getPreferences().getBughousePreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getBughousePreferences()
					.getChatWindowDimension());

			chatFrame.toFront();

			if (controller.isPlaying()
					&& getPreferences().getBughousePreferences()
							.isShowingPartnerCommunicationButtons()) {
				if (bugEarFrame == null) {
					bugEarFrame = new JFrame("Bug Ear");
					bugEarFrame.add(new BugEarPanel(getPreferences()));
					bugEarFrame
							.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					bugEarFrame.setLocation(getPreferences()
							.getBughousePreferences().getBugEarPoint());
					bugEarFrame.setSize(getPreferences()
							.getBughousePreferences().getBugEarDimension());
				} else {
					bugEarFrame.setLocation(getPreferences()
							.getBughousePreferences().getBugEarPoint());
					bugEarFrame.setSize(getPreferences()
							.getBughousePreferences().getBugEarDimension());
				}

				if (!bugEarFrame.isVisible()) {
					bugEarFrame.setVisible(true);
				}

				bugEarFrame.toFront();
			}
		} else {
			controller.getFrame().setLocation(
					getInitialPointForNewChessWindow());
			controller.getFrame().setSize(
					getPreferences().getBoardPreferences().getGameWindowSize());

			chatFrame.setLocation(getPreferences().getBoardPreferences()
					.getChatWindowPoint());
			chatFrame.setSize(getPreferences().getBoardPreferences()
					.getChatWindowDimension());

			chatFrame.toFront();
			controller.getFrame().toFront();

		}
		
		if (isAutoScrolling)
		{
		   forceChatScrollMax();
		}
	}

	private void temporarilyIgnoreGame(final int gameId) {
		gameIdsIgnoring.add(new Integer(gameId));
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				gameIdsIgnoring.remove(new Integer(gameId));
			}
		}, 2250);
	}

	private boolean isIgnoring(int gameId) {
		return gameIdsIgnoring.contains(new Integer(gameId));
	}

	public void removeICSController(ChessAreaControllerBase controller) {
		synchronized (this) {
			
			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Entering removeICSController: " + controller.getGameId() + " frame=" + controller.getFrame() + " ca=" + controller.getChessArea() + " pca=" + controller.getPartnersChessArea() + " pgid=" + controller.getPartnersGameId());
			}
			
			boolean removed = chessAreaControllers.remove(controller);
			
			if (!removed)
			{
				LOGGER.error("Attempted to remove an ICSController that was not being managed!");
			}
			

			controller.getFrame().setVisible(false);

			if (controller.isObserving() && controller.isActive()) {
				eventService.publish(new UnobserveRequestEvent(controller
						.getGameId(), true));
				temporarilyIgnoreGame(controller.getGameId());
				if (controller.isBughouse()) {
					eventService.publish(new UnobserveRequestEvent(controller
							.getPartnersGameId(), true));
					temporarilyIgnoreGame(controller.getPartnersGameId());
				}
			} else if (controller.isExamining() && controller.isActive()) {
				eventService.publish(new UnexamineRequestEvent(controller
						.getGameId(), true));
				temporarilyIgnoreGame(controller.getGameId());
			}

			// set the controller to inactive so it unsubscribes
			controller.setActive(false);
			controller.unsubscribe();
		}

		if (controller.isBughouse()) {
			recycleBughouseChessAreaDelegate((BugChessAreaController) controller);
		} else {
			recycleChessAreaDelegate((ChessAreaController) controller);
		}

		if (getPreferences().getBoardPreferences().isSnapToChatIfNoGames()
				&& chessAreaControllers.isEmpty()) {
			snapToChatFrame();
		}
	}

	private void removeAllControllers() {
		synchronized (this) {
			while (chessAreaControllers.size() != 0) {
				removeICSController(chessAreaControllers.get(0));
			}
		}
	}

	public void removeAllNonPlayingControllers() {
		synchronized (this) {
			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);
				if (!controller.isPlaying()) {
					removeICSController(controller);
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
					removeICSController(controller);
					i--;
				}
			}
		}
	}

	public ChessAreaControllerBase getPlayingController() {
		synchronized(this)
		{
			ChessAreaControllerBase result = null;
			for (int i = 0; result == null && i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers.get(i);
	
				if (controller.isPlaying()) {
					result = controller;
				}
			}
			return result;
		}
	}

	public ChessAreaControllerBase getLastBugController() {
		synchronized(this)
		{
			ChessAreaControllerBase result = null;
			for (int i = 0; result == null && i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers.get(i);
	
				if (controller.isBughouse()) {
					result = controller;
				}
			}
			return result;
		}
	}

	public ChessAreaControllerBase getLastChessController() {
		synchronized(this)
		{
			ChessAreaControllerBase result = null;
			for (int i = 0; result == null && i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers.get(i);
	
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
		ChessAreaControllerBase theController = null;
		for (int i = 0; i < chessAreaControllers.size(); i++) {
			ChessAreaControllerBase controller = chessAreaControllers.get(i);

			if (controller.isBughouse()) {
				theController = controller;
			}
		}

		Point location = chatFrame.getLocation();
		Dimension dimension = chatFrame.getSize();

		getPreferences().getBughousePreferences().setChatWindowPoint(location);
		getPreferences().getBughousePreferences().setChatWindowDimension(
				dimension);

		location = theController.getFrame().getLocation();
		dimension = theController.getFrame().getSize();
		getPreferences().getBughousePreferences().setGameWindowPoint(location);
		getPreferences().getBughousePreferences().setGameWindowDimension(
				dimension);
		getPreferences().getBughousePreferences().setBoardSplitterLocation(
				theController.getBughouserDividerLocation());

		if (getPreferences().getBughousePreferences()
				.isShowingPartnerCommunicationButtons()
				&& bugEarFrame != null && bugEarFrame.isVisible()) {
			location = bugEarFrame.getLocation();
			dimension = bugEarFrame.getSize();
			getPreferences().getBughousePreferences().setBugEarPoint(location);
			getPreferences().getBughousePreferences().setBugEarDimension(
					dimension);
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

		getPreferences().save();
	}

	public void saveChessLayout() {
		ChessAreaControllerBase theController = null;
		for (int i = 0; i < chessAreaControllers.size(); i++) {
			ChessAreaControllerBase controller = chessAreaControllers.get(i);

			if (!controller.isBughouse()) {
				theController = controller;
			}
		}

		Point location = chatFrame.getLocation();
		Dimension dimension = chatFrame.getSize();

		getPreferences().getBoardPreferences().setChatWindowPoint(location);
		getPreferences().getBoardPreferences()
				.setChatWindowDimension(dimension);

		location = theController.getFrame().getLocation();
		dimension = theController.getFrame().getSize();
		getPreferences().getBoardPreferences().setGameWindowPoint(location);
		getPreferences().getBoardPreferences()
				.setGameWindowDimension(dimension);

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

		getPreferences().save();
	}

	public void saveChatLayout() {
		Point location = chatFrame.getLocation();
		Dimension dimension = chatFrame.getSize();

		getPreferences().getChatPreferences().setChatWindowPoint(location);
		getPreferences().getChatPreferences().setChatWindowDimension(dimension);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Saving chess layout. Chat frame location: "
					+ getPreferences().getChatPreferences()
							.getChatWindowPoint()
					+ " size="
					+ getPreferences().getChatPreferences()
							.getChatWindowDimension());
		}

		getPreferences().save();
	}

	public void removeAll() {
		synchronized (this) {
			for (int i = 0; i < chessAreaControllers.size(); i++) {
				ChessAreaControllerBase controller = chessAreaControllers
						.get(i);
				removeICSController(controller);
			}
		}
	}

	// Examined games come accross as a MoveEvent th at is not being managed.
	public class ExaminedGameSubscriber implements Subscriber {
		public void inform(MoveEvent moveEvent) {

			if (isIgnoringExaminedGames)
			{
				if (LOGGER.isDebugEnabled())
				{
					LOGGER
					.debug("Ingnoring move event in ExamineGameSubscriber since isIgnoringExaminedGames is true");
					
				}
			}
			else if (!isActive(moveEvent.getGameId()) && !isIgnoring(moveEvent.getGameId()))  {
				if (moveEvent.getHoldingsChangedEvent() == null
						|| !isBugGameActive()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("Received moveEvent whose game wasnt being managed, assuming its an examined game since no bug games are active and this is the first move. "
										+ moveEvent.getGameId());
					}

					GameStartEvent gameStartEvent = new GameStartEvent(this, ""
							+ System.currentTimeMillis(), "", moveEvent
							.getGameId(), -1, moveEvent.getWhiteName(), "",
							moveEvent.getBlackName(), "", "Examining", false,
							GameTypes.EXAMINING, moveEvent);
					ChessAreaController delegate = createChessAreaDelegate();
					delegate.setup(gameStartEvent);
					addICSController(delegate);

				} else {
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("Received moveEvent whose game wasnt being managed, assuming an actual game and is in the state of being added as an ICSController (hopefully this isnt a bad assumption). "
										+ moveEvent.getGameId());
					}
				}
			}
		}
	}

	// EventService is like the the us government, it must see ALL so this cant
	// be private even though it should be.
	public class GameStartEventSubscriber implements Subscriber {
		public void inform(GameStartEvent event) {
			gameStartQueue.addFirst(event);
			gameStartQueueExecutor.interrupt();
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
							decaf.com.inboundevent.game.MoveEvent.class, null,
							this));
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

	private class GameStartQueueExecutor implements Runnable {

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

		private void handleNonBugGameStart(GameStartEvent event) {
			synchronized(GUIManager.getInstance())
			{
				// To handle the case where move events are received before the
				// delegate gets created and subscribed we must queue them as well.
				OrpahnMoveEventSubscriber orphanSubscriber = new OrpahnMoveEventSubscriber(
						event);
				isIgnoringExaminedGames = true;
	
				ChessAreaController delegate = createChessAreaDelegate();
				delegate.setup(event);
	
				// Stop listening for orphaned moves now the subscriber is all set
				// up.
				orphanSubscriber.dispose();
				isIgnoringExaminedGames = false;
	
				// update with orphaned move.
				if (orphanSubscriber.getOrphanEvent() != null) {
					// grab orphaned event if its there.
					delegate.getStyle12Subsriber().inform(
							orphanSubscriber.getOrphanEvent());
	
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Sending oprhaned move to chess area ...");
					}
				}
				addICSController(delegate);
			}
		}
		
		private void handleSettingUpPartnersBugBoard(GameStartEvent partnerGameStart)
		{
			synchronized(GUIManager.getInstance())
			{
				LOGGER.debug("GameStartQueueExecutor setting up partners board");	
				OrpahnMoveEventSubscriber partnersGameOrphanSubscriber = new OrpahnMoveEventSubscriber(
						partnerGameStart);
				
				delegateWaitingOnPartnersGameStart
				.setObservingGameStartEvent(partnerGameStart);
				
				partnersGameOrphanSubscriber.dispose();
				isIgnoringExaminedGames = false;
	
				// update with orphaned move.
				if (partnersGameOrphanSubscriber.getOrphanEvent() != null) {
					// grab orphaned event if its there.
					delegateWaitingOnPartnersGameStart
							.getPartnersStyle12Subsriber().inform(
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
		
		private void handleInitialBugGameStart(GameStartEvent event)
		{
			synchronized(GUIManager.getInstance())
			{
				// To handle the case where move events are received
				// before the delegate gets created and subscribed
				// we must queue them as well.
				OrpahnMoveEventSubscriber orphanSubscriber = new OrpahnMoveEventSubscriber(
						event);
	
				isIgnoringExaminedGames = true;
				
				// Send the pobs (dont use pfollow because it gets
				// difficult to tell if its an obs game or not).
				EventService.getInstance().publish(
						new OutboundEvent("pobs "
								+ event.getWhiteName()));
	
				// Preform the expensive operations.
				delegateWaitingOnPartnersGameStart = createBughoueChessAreaDelegate();
				delegateWaitingOnPartnersGameStart.setup(event);
	
				// Stop listening for orphaned moves now the
				// subscriber is all set up.
				orphanSubscriber.dispose();
	
				// update with orphaned move.
				if (orphanSubscriber.getOrphanEvent() != null) {
					// grab orphaned event if its there.
					delegateWaitingOnPartnersGameStart
							.getStyle12Subsriber().inform(
									orphanSubscriber
											.getOrphanEvent());
	
					if (LOGGER.isDebugEnabled()) {
						LOGGER
								.debug("Sending oprhaned move to bughosue chess area ...");
					}
				}
	
				// This displays and can be expensive.
				addICSController(delegateWaitingOnPartnersGameStart);
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
							.debug("GameStartQueueExecutor reporting for duty. isIgnoringExaminedGames=" + isIgnoringExaminedGames);
				}

				if (!gameStartQueue.isEmpty()) {
					GameStartEvent event = gameStartQueue.removeLast();

					if (delegateWaitingOnPartnersGameStart != null) {
						if (event.getGameType() != GameTypes.BUGHOUSE) {
							handleNonBugGameStart(event);
						} else {
							handleSettingUpPartnersBugBoard(event);
						}
					} else {
						if (event.getGameType() != GameTypes.BUGHOUSE) {							
							handleNonBugGameStart(event);
						} else {

							handleInitialBugGameStart(event);
						}
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

	private class ChessControllerKeyListener extends KeyAdapter {
		private ChessAreaControllerBase controller;

		public ChessControllerKeyListener(ChessAreaControllerBase controller) {
			this.controller = controller;
		}

		public void keyReleased(KeyEvent arg0) {
			if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
				controller.clearPremove();
			}
		}

	}

	private class ChatFrameClosingListener extends WindowAdapter {

		public void windowClosing(WindowEvent windowevent) {
			System.exit(1);
		}

		public ChatFrameClosingListener() {
		}
	}

	private class ChessControllerClosingListener extends WindowAdapter {

		public void windowClosing(WindowEvent windowevent) {

			if (controller.isPlaying() && controller.isActive())
			{
				JOptionPane.showMessageDialog(controller.getFrame(),"You cant close a game you are playing.");
			}
			else
			{
				if (LOGGER.isDebugEnabled())
				{
					LOGGER.debug("Caught window closing event, removing icsController: " + controller.getGameId());
				}
			    removeICSController(controller);
			}
		}

		private ChessAreaControllerBase controller;

		public ChessControllerClosingListener(
				ChessAreaControllerBase icscontroller) {
			controller = icscontroller;
		}
	}
}