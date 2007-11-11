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
package decaf.com.outboundevent;

public class VariableChangeRequestEvent extends OutboundEvent {
	public static final int PTIME_VARIABLE = 0;

	public static final int PROMPT_VARIABLE = 1;

	public static final int STYLE_VARIABLE = 2;

	public static final int AVAIL_INFO_VARIABLE = 3;

	public static final int BELL_VARIABLE = 4;

	public static final int INTERFACE_VARIABLE = 5;

	public VariableChangeRequestEvent(int variable, String value,
			boolean isHidingFromUser) {
		super(isHidingFromUser);
		this.variable = variable;
		this.value = value;
	}

	public int getVariable() {
		return variable;
	}

	public String getValue() {
		return value;
	}

	private int variable;

	private String value;
}