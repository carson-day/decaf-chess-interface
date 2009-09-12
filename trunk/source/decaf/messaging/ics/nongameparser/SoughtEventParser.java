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
package decaf.messaging.ics.nongameparser;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import decaf.gui.widgets.seekgraph.Seek;
import decaf.messaging.inboundevent.chat.IcsNonGameEvent;
import decaf.messaging.inboundevent.inform.SoughtEvent;

public class SoughtEventParser extends NonGameEventParser {

	private static final Logger logger = Logger
			.getLogger(SoughtEventParser.class);

	private static final String AD_DISPLAYED = "ad displayed.";

	private static final String ADS_DISPLAYED = "ads displayed.";

	public SoughtEventParser(int icsId) {
		super(icsId);
	}

	@Override
	public IcsNonGameEvent parse(String text) {
		if (text.endsWith(ADS_DISPLAYED) || text.endsWith(AD_DISPLAYED)) {

			// Make sure the first word is an integer this
			// is to make sure events are not running together.
			// Since its rare if it happens just return null.
			int firstSpace = text.indexOf(" ");
			try {
				if (firstSpace == -1) {
					return null;
				}
				Integer.parseInt(text.substring(0, firstSpace));
			} catch (Throwable t) {
				return null;
			}

			String[] lines = text.split("\n\\s*");

			List<Seek> seeks = new LinkedList<Seek>();

			for (int i = 0; i < lines.length - 1; i++) { // we don't care
				// about last line
				String line = lines[i];
				if (logger.isDebugEnabled()) {
					logger.debug("Sought line: " + line);
				}

				String[] parts = line.split("\\s+");
				// for (String part : parts) {
				// System.err.println("Part: '" + part + "'");
				// }

				int rating = -1;

				try {
					rating = Integer.parseInt(parts[1].trim());
				} catch (NumberFormatException e) {

				}

				Seek in = new Seek(Integer.parseInt(parts[0].trim()), rating,
						parts[2].trim(), Integer.parseInt(parts[3].trim()),
						Integer.parseInt(parts[4].trim()), "rated"
								.equals(parts[5].trim()) ? true : false);
				in.setType(parts[6].trim());
				seeks.add(in);
			}

			return new SoughtEvent(getIcsId(), text, seeks);
		} else {
			return null;
		}
	}

}
