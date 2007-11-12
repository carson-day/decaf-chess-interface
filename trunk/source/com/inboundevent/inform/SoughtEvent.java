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

import java.util.List;

import decaf.com.inboundevent.InboundEvent;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent, BugGameInProgress

public class SoughtEvent extends InboundEvent {

	public SoughtEvent(Object source, String messageId, String text,
			List seekAddsList) {
		super(source, messageId, text);
		this.seekAddsList = seekAddsList;
	}

	public int getNumberOfSeekAds() {
		return seekAddsList.size();
	}

	public SeekAd getSeekAdAt(int i) {
		return (SeekAd) seekAddsList.get(i);
	}

	public String toString() {
		String result = "<SoughtEvent>" + super.toString();
		for (int i = 0; i < getNumberOfSeekAds(); i++)
			result += getSeekAdAt(i);
		return result + "</SoughtEvent>";
	}

	private List seekAddsList;
}