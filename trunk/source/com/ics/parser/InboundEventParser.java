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
package decaf.com.ics.parser;

import decaf.com.inboundevent.InboundEvent;

public abstract class InboundEventParser {
	private Object source;

	private boolean isComposite;

	private boolean isChatEvent;

	public InboundEventParser(Object source, boolean isComposite,
			boolean isChatEvent) {
		this.source = source;
		this.isComposite = isComposite;
		this.isChatEvent = isChatEvent;
	}

	/**
	 * Returns null if text does not match the event this class produces.
	 */
	public abstract InboundEvent parse(String text);

	public Object getSource() {
		return source;
	}

	/**
	 * Returns true if the event this class parses can be nested in text with
	 * other events.
	 */
	public boolean isComposite() {
		return isComposite;
	}

	/**
	 * Returns true if this is a chat event. This is intended to prevents chat
	 * events from nesting other event text.
	 */
	public boolean isChatEvent() {
		return isChatEvent;
	}

}