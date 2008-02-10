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

package decaf.messaging.outboundevent;

import decaf.event.Event;

public class OutboundEvent implements Event {
	
	public OutboundEvent() {
		
	}
	/**
	 * @param text
	 *            The message to send.
	 */
	public OutboundEvent(String text) {
		this(text, false, null);
	}

	/**
	 * @param text
	 *            The message to send.
	 */
	public OutboundEvent(boolean isHidingFromUser) {
		this(null, isHidingFromUser, null);
	}

	/**
	 * @param test
	 *            The message to send.
	 * @param isHidingFromUser
	 *            True if the user should not see this message.
	 */
	public OutboundEvent(String text, boolean isHidingFromUser) {
		this(text, isHidingFromUser, null);
	}

	/**
	 * @param test
	 *            The message to send.
	 * @param isHidingFromUser
	 *            True if the user should not see this message.
	 * @param hideNextEventType
	 *            If not null the Class of the InboundEvent type to hide from
	 *            the user in response to this event.
	 */
	public OutboundEvent(String text, Class hideResponseEventType) {
		this(text, true, null);
	}

	/**
	 * @param test
	 *            The message to send.
	 * @param isHidingFromUser
	 *            True if the user should not see this message.
	 * @param hideNextEventType
	 *            If not null the Class of the InboundEvent type to hide from
	 *            the user in response to this event.
	 */
	public OutboundEvent(String text, boolean isHidingFromUser,
			Class hideResponseEventType) {
		this.text = (text != null ? text.trim() : null);
		this.isHidingFromUser = isHidingFromUser;
		this.hideResponseEventType = hideResponseEventType;
		this.creationTime = System.currentTimeMillis();
	}

	/**
	 * Returns the time this event was created.
	 */
	public long getCreationTime() {
		return creationTime;
	}

	public Class getResponseEventTypeToHideFromUser() {
		return hideResponseEventType;
	}

	public boolean isHidingFromUser() {
		return isHidingFromUser;
	}

	public String getText() {
		return text;
	}

	private long creationTime;

	private String text;

	private boolean isHidingFromUser;

	private Class hideResponseEventType;
}