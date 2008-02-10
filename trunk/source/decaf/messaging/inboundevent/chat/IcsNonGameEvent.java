package decaf.messaging.inboundevent.chat;

import decaf.messaging.inboundevent.IcsInboundEvent;

public class IcsNonGameEvent extends IcsInboundEvent {
	private String text;

	public IcsNonGameEvent(int icsId, String text) {
		super(icsId);
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
