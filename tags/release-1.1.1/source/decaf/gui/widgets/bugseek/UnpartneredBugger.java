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
package decaf.gui.widgets.bugseek;

public class UnpartneredBugger implements Comparable<UnpartneredBugger> {
	private String rating;

	private char ratingModifier;

	private String handle;

	private char handleModifier;

	public String getHandle() {
		return handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public char getHandleModifier() {
		return handleModifier;
	}

	public void setHandleModifier(char handleModifier) {
		this.handleModifier = handleModifier;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public char getRatingModifier() {
		return ratingModifier;
	}

	public void setRatingModifier(char ratingModifier) {
		this.ratingModifier = ratingModifier;
	}

	public int compareTo(UnpartneredBugger bugger) {
		int ratingAsInt = 0;
		int buggerRatingAsInt = 0;
		
		try
		{
			ratingAsInt = Integer.parseInt(getRating());
		}
		catch (NumberFormatException nfe)
		{}

		try
		{
			buggerRatingAsInt = Integer.parseInt(bugger.getRating());
		}
		catch (NumberFormatException nfe)
		{}
		
		if (ratingAsInt > buggerRatingAsInt)
		{
			return -1;
		}
		else if (ratingAsInt < buggerRatingAsInt)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
