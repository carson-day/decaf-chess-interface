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

package decaf.com.inboundevent.inform;

import decaf.com.inboundevent.InboundEvent;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class AvailInfoEvent extends InboundEvent {

	public AvailInfoEvent(Object source, String messageId, String text,
			String user, int blitz, int standard, int wild, int lightning,
			int bughouse) {
		super(source, messageId, text);
		this.user = user;
		this.blitz = blitz;
		this.standard = standard;
		this.wild = wild;
		this.lightning = lightning;
		this.bughouse = bughouse;
	}

	public String getUser() {
		return user;
	}

	public int getBlitz() {
		return blitz;
	}

	public int getStandard() {
		return standard;
	}

	public int getWild() {
		return wild;
	}

	public int getBughouse() {
		return bughouse;
	}

	public String toString() {
		return "<AvailInfoEvent>" + super.toString() + "<user>" + user
				+ "</user>" + "<blitz>" + blitz + "</blitz>" + "<standard>"
				+ standard + "</standard>" + "<wild>" + wild + "</wild>"
				+ "<lightning>" + lightning + "</lightning>" + "<bughouse>"
				+ bughouse + "</bughouse>" + "</AvailInfoEvent>";
	}

	private String user;

	private int blitz;

	private int standard;

	private int wild;

	private int lightning;

	private int bughouse;
}