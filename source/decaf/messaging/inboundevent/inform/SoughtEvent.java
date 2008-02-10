/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Sergei Kozyrenko (kozyr82@gmail.com)
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
package decaf.messaging.inboundevent.inform;

import java.util.List;

import decaf.gui.widgets.seekgraph.Seek;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;

public class SoughtEvent extends IcsNonGameEvent {

	private List<Seek> seeks;

	public SoughtEvent(int icsId, String text, List<Seek> seeks) {
		super(icsId, text);
		// System.err.println("Seek List: " + seeks);
		this.seeks = seeks;
	}

	public List<Seek> getSeeks() {
		return seeks;
	}
}
