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
package decaf.gui.widgets;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;

public class BugChessArea extends JPanel implements Preferenceable, Disposable {

	private static final Logger LOGGER = Logger.getLogger(BugChessArea.class);

	public static final int AREA1_ON_LEFT = 0;

	public static final int AREA1_ON_RIGHT = 1;

	public JPanel area1AndButtonPanel;

	private ChessArea area1;

	private ChessArea area2;

	private JSplitPane boardDividingSplitPane;

	private Preferences preferences;

	public void dispose() {
		preferences = null;
		if (area1 != null) {
			area1.dispose();
		}
		if (area2 != null) {
			area2.dispose();
		}
	}

	public BugChessArea(ChessArea chessArea, ChessArea partnersChessArea) {
		area1 = chessArea;
		area2 = partnersChessArea;
		boardDividingSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		boardDividingSplitPane.setOneTouchExpandable(true);
		setupLayout();
	}

	public void setBugOrientation(int orientation) {
		synchronized (this) {
			boolean isArea1OnLeft = boardDividingSplitPane.getLeftComponent() == area1;
			if (isArea1OnLeft && orientation != AREA1_ON_LEFT) {
				rotate();
			} else if (!isArea1OnLeft && orientation != AREA1_ON_RIGHT) {
				rotate();
			}
		}
	}

	private void setupLayout() {
		setLayout(new BorderLayout());
		add(boardDividingSplitPane, BorderLayout.CENTER);
		boardDividingSplitPane.setLeftComponent(area1);
		boardDividingSplitPane.setRightComponent(area2);
	}

	public void rotate() {
		synchronized (this) {
			int splitLocation = boardDividingSplitPane.getLastDividerLocation();
			boolean isArea1PreviouslyOnLeft = boardDividingSplitPane
					.getLeftComponent() == area1;
			boardDividingSplitPane.remove(area1);
			boardDividingSplitPane.remove(area2);

			boardDividingSplitPane
					.setLeftComponent(isArea1PreviouslyOnLeft ? area2 : area1);
			boardDividingSplitPane
					.setRightComponent(isArea1PreviouslyOnLeft ? area1 : area2);

			boardDividingSplitPane.setDividerLocation(splitLocation);

		}
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setDividerLocation(int location) {
		if (location == -1) {
			boardDividingSplitPane.setDividerLocation(.5);
		} else {
			boardDividingSplitPane.setDividerLocation(location);
		}
		invalidate();
	}

	public void setBugChessAreaPrefsOnly(Preferences preferences) {
		this.preferences = preferences;
		setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
	}

	public void setPreferences(Preferences preferences) {

		long startTime = System.currentTimeMillis();
		this.preferences = preferences;
		setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		area1.setPreferences(preferences);
		area2.setPreferences(preferences);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Set preferenes in bug chess area in "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	public int getSplitPaneLocation() {
		return boardDividingSplitPane.getDividerLocation();
	}
}