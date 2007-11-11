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
import java.awt.Container;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import decaf.com.inboundevent.game.GameStartEvent;
import decaf.com.inboundevent.game.MoveEvent;
import decaf.gui.util.PropertiesManager;
import decaf.gui.util.User;
import decaf.gui.widgets.ChessArea;

/**
 * Delegates commands to an underlying chess area based on events received from
 * EventService and offers methods as well. This class is thread safe.
 */
public class ChessAreaController extends ChessAreaControllerBase implements
		Disposable {
	private static final Logger LOGGER = Logger
			.getLogger(ChessAreaController.class);
	
	private static String[] DO_NOT_VALIDATE_KEYWORDS = null;
	
	static
	{
		String dontValidate = PropertiesManager.getInstance().getString("Decaf","dontValidateGameTypes");
		if (dontValidate == null)
		{
			DO_NOT_VALIDATE_KEYWORDS = new String[0];
		}
		else
		{
			DO_NOT_VALIDATE_KEYWORDS = dontValidate.trim().split(",");
		}
	}

	public void prepareForRecyling() {
		synchronized(this)
		{
			unsubscribe();
			if (this.getChessArea() != null) {
				getChessArea().prepareForRecycling();
			}
			if (this.getPartnersChessArea() != null) {
				getPartnersChessArea().prepareForRecycling();
			}
	
			if (getCommandToolbar() != null) {
				getFrame().getContentPane().remove(getCommandToolbar());
				getCommandToolbar().dispose();
			}
			
			WindowListener[] listeners = getFrame().getWindowListeners();
			for (int i = 0; i < listeners.length; i++)
			{
				getFrame().removeWindowListener(listeners[i]);
			}
		}

	}
	
	private boolean isValidatingGameType(String gameDescription)
	{
         boolean result = true;
         
         for (int i = 0; result && i < DO_NOT_VALIDATE_KEYWORDS.length; i++)
         {
        	    if (gameDescription.indexOf(DO_NOT_VALIDATE_KEYWORDS[i]) != -1)
        	    {
        	    	   result = false;
        	    }
         }
         
         if (LOGGER.isDebugEnabled())
         {
	        LOGGER.debug("isValidatingGameType=" + result + " gameDescription=" + gameDescription);
         }
        return result;

	}

	public void setup(GameStartEvent event) {
       
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("WhiteName=" + event.getWhiteName() + " BlackName="
					+ event.getBlackName() + " playerName="
					+ User.getInstance().getHandle());
		}

		long startTime = System.currentTimeMillis();

		if (event.getGameType() == GameStartEvent.BUGHOUSE) {
			IllegalArgumentException iae = new IllegalArgumentException(
					INVALID_GAME_TYPE);
			LOGGER.error(iae);
			throw iae;
		}


        synchronized(this)
        {
			MoveEvent moveEvent = event.getInitialInboundChessMoveEvent();
	
			setPlaying(event.getBlackName().equalsIgnoreCase(user.getHandle())
					|| event.getWhiteName().equalsIgnoreCase(user.getHandle()));
	
			if (isPlaying()) {
				setUserWhite(moveEvent.getWhiteName().equalsIgnoreCase(
						user.getHandle()));
			} else {
				setUserWhite(false);
			}
			setObserving(!isPlaying());
	
			setActive(true);
			setObserving(!isPlaying());
			setDroppable(event.getGameType() == GameStartEvent.CRAZYHOUSE);
	
			String frameTitle = null;
			if (event.getGameType() == GameStartEvent.EXAMINING) {
				// possible bug need to see what happens if this user observes an
				// examined game.
				setExamining(true);
				setObserving(false);
				setPlaying(false);
				setDroppable(moveEvent.getHoldingsChangedEvent() != null);
	
				getChessArea().setup("" + getGameId(), event.getWhiteName(), "",
						event.getBlackName(), "", isDroppable(), false,
						moveEvent.getPosition());
	
				frameTitle = event.getGameDescription();
			} else {
				setValidating(isValidatingGameType(event.getGameDescription()));			
				getChessArea().setup(
						"" + getGameId(),
						event.getWhiteName(),
						event.getWhiteRating().equals("0") ? "++++" : event
								.getWhiteRating(),
						event.getBlackName(),
						event.getBlackRating().equals("0") ? "++++" : event
								.getBlackRating(),
						isDroppable(),
						isPlaying() ? !isUserWhite() : false,
						moveEvent.getInitialTime() == 0 ? 10 : moveEvent
								.getInitialTime() * 60, moveEvent.getInitialInc(),
						moveEvent.getPosition());
	
				frameTitle = event.getGameDescription();
			}
			setGameId(event.getGameId());
	
			getFrame().setTitle(frameTitle);
	
			if (preferences.getBoardPreferences().isShowingToolbar()) {
				setCommandToolbar(new ChessAreaToolbar(preferences, this));
				Container container = getFrame().getContentPane();
				container.add(getCommandToolbar(), BorderLayout.NORTH);
			}
	
			getChessArea()
					.setWhiteTime(
							event.getInitialInboundChessMoveEvent()
									.getWhiteRemainingTime());
			getChessArea()
					.setBlackTime(
							event.getInitialInboundChessMoveEvent()
									.getBlackRemainingTime());
	
			if (!moveEvent.isClockTicking()) {
				getChessArea().startOrStopClocksWithoutTicking();
			}
	
			getChessArea().setShowTenthsWhenTimeIsLessThanSeconds(
					preferences.getBoardPreferences()
							.getShowTenthsWhenTimeIsLessThanSeconds());
	
			if (isDroppable()) {
				getChessArea().setWhiteDropPieces(
						moveEvent.getHoldingsChangedEvent().getWhiteHoldings());
				getChessArea().setBlackDropPieces(
						moveEvent.getHoldingsChangedEvent().getBlackHoldings());
			}
	
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Setup non-bughouse game. Elapsed Time="
						+ (System.currentTimeMillis() - startTime)
						+ " + isUserPlaying=" + isPlaying());
			}
	
			this.handleGameStarted();
			subscribe();
        }
	}

	public ChessAreaController() {
		setChessArea(new ChessArea());

		setFrame(new JFrame("Not Initialized this is a bug message cday"));
		getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Container contentPane = getFrame().getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(getChessArea(), BorderLayout.CENTER);
	}

	public void dispose() {
		super.dispose();
	}

	private static User user = User.getInstance();

	private static final String INVALID_GAME_TYPE = "ChessAreaController does not handle bughouse. "
			+ "Try BughouseChessAreaController instead.";
}