package decaf.messaging.inboundevent.inform;

import decaf.messaging.inboundevent.chat.IcsNonGameEvent;

public class NotFollowingEvent extends IcsNonGameEvent {
	public NotFollowingEvent(int icsId, String text) {
		super(icsId, text);

	}

	public String toString() {
		return "<NotFollowingEvent>" + getText()
				+ "</NotFollowingEvent>";
	}
}
