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
package decaf.com.ics;

import decaf.com.inboundevent.game.GameTypes;
import decaf.com.outboundevent.AbortRequestEvent;
import decaf.com.outboundevent.AcceptSeekAddRequestEvent;
import decaf.com.outboundevent.AdjournRequestEvent;
import decaf.com.outboundevent.AvailableBugTeamsRequestEvent;
import decaf.com.outboundevent.BugGamesRequestEvent;
import decaf.com.outboundevent.BugOpenRequestEvent;
import decaf.com.outboundevent.DrawRequestEvent;
import decaf.com.outboundevent.ExamineBackRequestEvent;
import decaf.com.outboundevent.ExamineForwardRequestEvent;
import decaf.com.outboundevent.ExamineRevertRequestEvent;
import decaf.com.outboundevent.MatchRequestEvent;
import decaf.com.outboundevent.MoveRequestEvent;
import decaf.com.outboundevent.ObserveRequestEvent;
import decaf.com.outboundevent.OpenRequestEvent;
import decaf.com.outboundevent.OutboundEvent;
import decaf.com.outboundevent.PartnerTellRequestEvent;
import decaf.com.outboundevent.PauseRequestEvent;
import decaf.com.outboundevent.PingRequestEvent;
import decaf.com.outboundevent.RefreshRequestEvent;
import decaf.com.outboundevent.RematchRequestEvent;
import decaf.com.outboundevent.ResignRequestEvent;
import decaf.com.outboundevent.SeekAddsRequestEvent;
import decaf.com.outboundevent.TakebackRequestEvent;
import decaf.com.outboundevent.UnexamineRequestEvent;
import decaf.com.outboundevent.UnobserveRequestEvent;
import decaf.com.outboundevent.VariableChangeRequestEvent;
import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;

public class ICSOutboundMessageHandler implements Subscriber {
	private ICSCommunicationsDriver driver;

	private static final EventService EVENT_SERVICE = EventService
			.getInstance();

	public void dispose() {
		driver = null;

	}

	public ICSOutboundMessageHandler(ICSCommunicationsDriver driver) {
		this.driver = driver;
		EVENT_SERVICE.subscribe(new Subscription(AbortRequestEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(
				AcceptSeekAddRequestEvent.class, null, this));
		EVENT_SERVICE.subscribe(new Subscription(AdjournRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(BugOpenRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(BugGamesRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(
				AvailableBugTeamsRequestEvent.class, null, this));
		EVENT_SERVICE.subscribe(new Subscription(DrawRequestEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(ExamineBackRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(
				ExamineForwardRequestEvent.class, null, this));
		EVENT_SERVICE.subscribe(new Subscription(
				ExamineRevertRequestEvent.class, null, this));
		EVENT_SERVICE.subscribe(new Subscription(MatchRequestEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(MoveRequestEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(ObserveRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(OpenRequestEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(OutboundEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(PartnerTellRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(PauseRequestEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(PingRequestEvent.class, null,
				this));
		EVENT_SERVICE.subscribe(new Subscription(RefreshRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(RematchRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(ResignRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(SeekAddsRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(TakebackRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(UnexamineRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(UnobserveRequestEvent.class,
				null, this));
		EVENT_SERVICE.subscribe(new Subscription(
				VariableChangeRequestEvent.class, null, this));
	}

	public void inform(AbortRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("abort");
	}

	public void inform(AcceptSeekAddRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("play " + event.getSeekAddId());
	}

	public void inform(AdjournRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("adjourn");
	}

	public void inform(BugOpenRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("set bugopen " + (event.isBugOpen() ? "1" : "0"));
	}

	public void inform(BugGamesRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("bugwho g");
	}

	public void inform(AvailableBugTeamsRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("bugwho p");
	}

	public void inform(DrawRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("draw");
	}

	public void inform(ExamineBackRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("back " + event.getHalfMovesBack());
	}

	public void inform(ExamineForwardRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("forward " + event.getHalfMovesForward());
	}

	public void inform(ExamineRevertRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("revert");
	}

	public void inform(MatchRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("match " + event.getPlayerToMatch() + " "
				+ event.getTime() + " " + event.getIncrement() + " "
				+ gameTypeToString(event.getGameType()));
	}

	public void inform(MoveRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg(event.getMove());
	}

	public void inform(ObserveRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("observe " + event.getGameId());
	}

	public void inform(OpenRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("set open " + (event.isOpen() ? "1" : "0"));
	}

	public void inform(OutboundEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg(event.getText());
	}

	public void inform(PartnerTellRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("ptell " + event.getTextToTellPartner());
	}

	public void inform(PauseRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("pause");
	}

	public void inform(PingRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("~\\");
	}

	public void inform(RefreshRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("refresh " + event.getGameId());
	}

	public void inform(RematchRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("rematch");
	}

	public void inform(ResignRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("resign");
	}

	public void inform(SeekAddsRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("sought");
	}

	public void inform(TakebackRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("takeback " + event.getHalfMoves());
	}

	public void inform(UnexamineRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("unexamine");
	}

	public void inform(UnobserveRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("unobserve " + event.getGameId());
	}

	public void inform(VariableChangeRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("set " + variableConstantToString(event.getVariable())
				+ " " + event.getValue());
	}

	private String variableConstantToString(int variableConstant) {
		switch (variableConstant) {
		case VariableChangeRequestEvent.AVAIL_INFO_VARIABLE: {
			return "availinfo";
		}
		case VariableChangeRequestEvent.BELL_VARIABLE: {
			return "bell";
		}
		case VariableChangeRequestEvent.INTERFACE_VARIABLE: {
			return "interface";
		}
		case VariableChangeRequestEvent.PROMPT_VARIABLE: {
			return "prompt";
		}
		case VariableChangeRequestEvent.PTIME_VARIABLE: {
			return "ptime";
		}
		case VariableChangeRequestEvent.STYLE_VARIABLE: {
			return "style";
		}
		default: {
			throw new IllegalArgumentException("Unknown variable constant: "
					+ variableConstant);
		}
		}
	}

	private String gameTypeToString(int gameType) {
		switch (gameType) {
		case GameTypes.BLITZ:
		case GameTypes.LIGHTNING:
		case GameTypes.STANDARD: {
			return "";
		}
		case GameTypes.WILD: {
			return "wild";
		}
		case GameTypes.SUICIDE: {
			return "suicide";
		}
		case GameTypes.BUGHOUSE: {
			return "bughouse";
		}
		case GameTypes.CRAZYHOUSE: {
			return "crazyhouse";
		}
		case GameTypes.LOSERS: {
			return "losers";
		}
		case GameTypes.ATOMIC: {
			return "atomic";
		}
		default: {
			throw new IllegalArgumentException("Unknown gameType: " + gameType);
		}
		}
	}
}