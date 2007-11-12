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
package decaf.com.inboundevent;

import decaf.event.Event;

public class InboundEvent implements Event {
	public InboundEvent(Object source, String eventId, String text) {
		this.source = source;
		this.eventId = eventId;
		this.text = text;
		isShowingToUser = true;
		this.creationTime = System.currentTimeMillis();
		this.isIncompleteMessage = false;
	}

	public InboundEvent(Object source, String eventId, String text,
			boolean isIncompleteMessage) {
		this(source, eventId, text);
		this.isIncompleteMessage = isIncompleteMessage;
	}

	/**
	 * Returns the time this event was created.
	 */
	public long getCreationTime() {
		return creationTime;
	}

	public Object getSource() {
		return source;
	}

	public String getText() {
		return text;
	}

	public String toString() {
		return "<InboundEvent type=\"" + getClass().getName() + "\"/>" + "<source>" + source + "</source>"
				+ "<eventId>" + eventId + "</eventId></InboundEvent>";
	}

	public void setShowingToUser(boolean flag) {
		isShowingToUser = flag;
	}

	public boolean isShowingToUser() {
		return isShowingToUser;
	}

	public boolean isIncompleteMessage() {
		return isIncompleteMessage;
	}

	private String text;

	private String eventId;

	private long creationTime;

	private boolean isShowingToUser;

	private boolean isIncompleteMessage;

	private Object source;
}