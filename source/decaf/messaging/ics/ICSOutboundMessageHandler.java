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
package decaf.messaging.ics;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.event.Subscriber;
import decaf.event.Subscription;
import decaf.messaging.inboundevent.game.GameTypes;
import decaf.messaging.outboundevent.AbortRequestEvent;
import decaf.messaging.outboundevent.AdjournRequestEvent;
import decaf.messaging.outboundevent.BugOpenRequestEvent;
import decaf.messaging.outboundevent.DrawRequestEvent;
import decaf.messaging.outboundevent.ExamineBackRequestEvent;
import decaf.messaging.outboundevent.ExamineForwardRequestEvent;
import decaf.messaging.outboundevent.ExamineRevertRequestEvent;
import decaf.messaging.outboundevent.MatchRequestEvent;
import decaf.messaging.outboundevent.MoveRequestEvent;
import decaf.messaging.outboundevent.ObserveRequestEvent;
import decaf.messaging.outboundevent.OpenRequestEvent;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.messaging.outboundevent.PartnerTellRequestEvent;
import decaf.messaging.outboundevent.PauseRequestEvent;
import decaf.messaging.outboundevent.PingRequestEvent;
import decaf.messaging.outboundevent.RefreshRequestEvent;
import decaf.messaging.outboundevent.RematchRequestEvent;
import decaf.messaging.outboundevent.ResignRequestEvent;
import decaf.messaging.outboundevent.TakebackRequestEvent;
import decaf.messaging.outboundevent.UnexamineRequestEvent;
import decaf.messaging.outboundevent.UnobserveRequestEvent;

public class ICSOutboundMessageHandler implements Subscriber {
	private static final Logger LOGGER = Logger
			.getLogger(ICSOutboundMessageHandler.class);

	private ICSCommunicationsDriver driver;

	public void dispose() {
		driver = null;

	}

	public ICSOutboundMessageHandler(ICSCommunicationsDriver driver) {
		this.driver = driver;
		EventService.getInstance().subscribe(
				new Subscription(AbortRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(AdjournRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(BugOpenRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(DrawRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(ExamineBackRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(ExamineForwardRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(ExamineRevertRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(MatchRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(MoveRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(ObserveRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(OpenRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(OutboundEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(PartnerTellRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(PauseRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(PingRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(RefreshRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(RematchRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(ResignRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(TakebackRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(UnexamineRequestEvent.class, null, this));
		EventService.getInstance().subscribe(
				new Subscription(UnobserveRequestEvent.class, null, this));

		LOGGER.debug("Subscribed in ICSOutboundMessageHandler to "
				+ EventService.getInstance());
	}

	public void inform(AbortRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("abort");
	}

	public void inform(AdjournRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("adjourn");
	}

	public void inform(BugOpenRequestEvent event) {
		driver.handlePublishingEventAndLogging(event);
		driver.sendMsg("set bugopen " + (event.isBugOpen() ? "1" : "0"));
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
		if (event != null && !event.equals(null) && event.getText() != null) {
			driver.handlePublishingEventAndLogging(event);
			driver.sendMsg(event.getText());
		}
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