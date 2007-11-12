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

import java.util.StringTokenizer;

import decaf.com.inboundevent.InboundEvent;

// Referenced classes of package caffeine.event.inbound:
// InboundEvent

public class VariablesEvent extends InboundEvent {
	// TO DO make variable text a Map.
	public VariablesEvent(Object source, String messageId, String text,
			String user) {
		super(source, messageId, text);
		this.user = user;
	}

	public String getUser() {
		return user;
	}

	public String getVariable(String s) {
		int i = getText().indexOf(s);
		StringTokenizer stringtokenizer = new StringTokenizer(getText()
				.substring(i, getText().length()), ":= ");
		if (stringtokenizer.hasMoreTokens())
			return stringtokenizer.nextToken();
		else
			return null;
	}

	public String toString() {
		return "<VariablesEvent>" + super.toString() + "</VariablesEvent>";
	}

	public static final String TIME = "time";

	public static final String INC = "inc";

	public static final String OPEN = "open";

	public static final String BUG_OPEN = "bugopen";

	public static final String SIM_OPEN = "simopen";

	public static final String TOURNEY = "tourney";

	public static final String PROV_SHOW = "provshow";

	public static final String AUTO_FLAG = "autoflag";

	public static final String MIN_MOVE_TIME = "minmovetime";

	public static final String PRIVATE = "private";

	public static final String J_PRIVATE = "jprivate";

	public static final String AUTO_MAIN = "automail";

	public static final String PGN = "pgn";

	public static final String MAIL_MESS = "mailmess";

	public static final String MESS_REPLY = "messreply";

	public static final String UNOBSERVE = "unobserve";

	public static final String SHOUT = "shout";

	public static final String CSHOUT = "cshout";

	public static final String KIBITZ = "kibitz";

	public static final String KIB_LEVEL = "kiblevel";

	public static final String TELL = "tell";

	public static final String CTELL = "ctell";

	public static final String CHAN_OFF = "chanoff";

	public static final String SILENCE = "silence";

	public static final String ECHO = "echo";

	public static final String TOLERANCE = "tolerance";

	public static final String NO_ESCAPE = "noescape";

	public static final String PIN = "pin";

	public static final String NOTIFIED_BY = "notifiedby";

	public static final String AVAIL_INFO = "availinfo";

	public static final String AVAIL_MIN = "availmin";

	public static final String AVAIL_MAX = "availmax";

	public static final String GIN = "gin";

	public static final String SEEK = "seek";

	public static final String SHOW_OWN_SEEK = "showownseek";

	public static final String EXAMINE = "examine";

	public static final String STYLE = "style";

	public static final String FLIP = "flip";

	public static final String HIGHWHITE = "highlight";

	public static final String BELL = "bell";

	public static final String WIDTH = "width";

	public static final String HEIGHT = "height";

	public static final String P_TIME = "ptime";

	public static final String TZONE = "tzone";

	public static final String LANG = "Lang";

	public static final String BUGHOUSE_PARTNER = "Bughouse partner";

	private String user;
}