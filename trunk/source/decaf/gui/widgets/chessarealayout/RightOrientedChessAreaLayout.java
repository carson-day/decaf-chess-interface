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
package decaf.gui.widgets.chessarealayout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.SquareSandwichLayout;
import decaf.gui.widgets.holdings.Right3By2HoldingsPanel;

public class RightOrientedChessAreaLayout implements ChessAreaLayout {
	private static final Logger LOGGER = Logger
			.getLogger(RightOrientedChessAreaLayout.class);

	private JPanel boardAndDropPiecesPanel;

	private JPanel whiteControlAndClockPanel;

	private JPanel whiteClockPanel;

	private JPanel whiteControlsPanel;

	private JPanel blackControlAndClockPanel;

	private JPanel blackClockPanel;

	private JPanel blackControlsPanel;

	private JPanel controlsPanel;

	private JPanel northFillerPanel;

	private JPanel southFillerPanel;

	private ChessArea chessArea;

	public void adjustForLabelChanges() {
		if (chessArea.getWhiteNameLbl().getText() != null) {
			chessArea.getWhiteNameLbl().setText(
					chessArea.getWhiteNameLbl().getText().trim());
		}
		if (chessArea.getBlackNameLbl().getText() != null) {
			chessArea.getBlackNameLbl().setText(
					chessArea.getBlackNameLbl().getText().trim());
		}
	}

	public void dispose() {
		if (northFillerPanel != null) {
			northFillerPanel.removeAll();
		}
		if (southFillerPanel != null) {
			southFillerPanel.removeAll();
		}
		if (whiteControlAndClockPanel != null) {
			whiteControlAndClockPanel.removeAll();
		}

		if (whiteClockPanel != null) {
			whiteClockPanel.removeAll();
		}
		if (whiteControlsPanel != null) {
			whiteControlsPanel.removeAll();
		}
		if (blackControlAndClockPanel != null) {
			blackControlAndClockPanel.removeAll();
		}
		if (blackClockPanel != null) {
			blackClockPanel.removeAll();
		}
		if (blackControlsPanel != null) {
			blackControlsPanel.removeAll();
		}
		if (controlsPanel != null) {
			controlsPanel.removeAll();
		}

		if (boardAndDropPiecesPanel != null) {
			boardAndDropPiecesPanel.removeAll();
		}
	}

	public void init(final ChessArea chessArea) {
		long startTime = System.currentTimeMillis();
		this.chessArea = chessArea;

		whiteClockPanel = new JPanel();
		whiteControlsPanel = new JPanel();
		whiteControlAndClockPanel = new JPanel();
		blackClockPanel = new JPanel();
		blackControlsPanel = new JPanel();
		blackControlAndClockPanel = new JPanel();
		boardAndDropPiecesPanel = new JPanel();
		northFillerPanel = new JPanel();
		southFillerPanel = new JPanel();
		controlsPanel = new JPanel();

		chessArea.getMoveList().setMaximumSize(null);
		chessArea.getMoveList().setPreferredSize(null);
		chessArea.getMoveList().setMinimumSize(null);
		chessArea.getMoveList().setAlignmentX(Component.CENTER_ALIGNMENT);
		chessArea.getMoveList().setAlignmentY(Component.CENTER_ALIGNMENT);

		adjustForLabelChanges();

		whiteControlsPanel.setLayout(new BoxLayout(whiteControlsPanel,
				BoxLayout.X_AXIS));
		chessArea.getMarkWhiteLabel().setAlignmentX(0);
		chessArea.getWhiteNameLbl().setAlignmentX(0);

		whiteControlsPanel.add(chessArea.getMarkWhiteLabel());
		whiteControlsPanel.add(chessArea.getWhiteNameLbl());
		whiteControlsPanel.add(Box.createHorizontalGlue());

		whiteClockPanel.setLayout(new BoxLayout(whiteClockPanel,
				BoxLayout.X_AXIS));
		chessArea.getWhitesClock().setAlignmentX(0);
		chessArea.getWhiteLagLbl().setAlignmentX(0);

		whiteClockPanel.add(chessArea.getWhitesClock());
		whiteClockPanel.add(Box.createHorizontalStrut(10));
		whiteClockPanel.add(chessArea.getWhiteLagLbl());
		whiteClockPanel.add(Box.createHorizontalGlue());

		blackControlsPanel.setLayout(new BoxLayout(blackControlsPanel,
				BoxLayout.X_AXIS));
		chessArea.getMarkBlackLabel().setAlignmentX(0);
		chessArea.getBlackNameLbl().setAlignmentX(0);

		blackControlsPanel.add(chessArea.getMarkBlackLabel());
		blackControlsPanel.add(chessArea.getBlackNameLbl());
		blackControlsPanel.add(Box.createHorizontalGlue());

		blackClockPanel.setLayout(new BoxLayout(blackClockPanel,
				BoxLayout.X_AXIS));
		chessArea.getBlacksClock().setAlignmentX(0);
		chessArea.getBlackLagLbl().setAlignmentX(0);

		blackClockPanel.add(chessArea.getBlacksClock());
		blackClockPanel.add(Box.createHorizontalStrut(10));
		blackClockPanel.add(chessArea.getBlackLagLbl());
		blackClockPanel.add(Box.createHorizontalGlue());

		setupDropPieces(chessArea);

		whiteControlAndClockPanel.setLayout(new BoxLayout(
				whiteControlAndClockPanel, BoxLayout.Y_AXIS));
		whiteControlsPanel.setAlignmentY(0);
		whiteClockPanel.setAlignmentY(0);
		whiteClockPanel.setAlignmentX(1);
		blackControlsPanel.setAlignmentY(0);
		blackClockPanel.setAlignmentY(0);
		blackClockPanel.setAlignmentX(1);
		chessArea.getWhiteHoldings().setAlignmentY(0);
		chessArea.getBlackHoldings().setAlignmentY(0);

		if (chessArea.isWhiteOnTop()) {

			whiteControlAndClockPanel.add(whiteControlsPanel);
			whiteControlAndClockPanel.add(whiteClockPanel);
			whiteControlAndClockPanel.add(chessArea.getWhiteHoldings());
			whiteControlAndClockPanel.add(Box.createVerticalGlue());
		} else {
			whiteControlAndClockPanel.add(Box.createVerticalGlue());
			whiteControlAndClockPanel.add(chessArea.getWhiteHoldings());
			whiteControlAndClockPanel.add(whiteClockPanel);
			whiteControlAndClockPanel.add(whiteControlsPanel);
		}

		blackControlAndClockPanel.setLayout(new BoxLayout(
				blackControlAndClockPanel, BoxLayout.Y_AXIS));
		if (chessArea.isWhiteOnTop()) {
			blackControlAndClockPanel.add(Box.createVerticalGlue());
			blackControlAndClockPanel.add(chessArea.getBlackHoldings());
			blackControlAndClockPanel.add(blackClockPanel);
			blackControlAndClockPanel.add(blackControlsPanel);
		} else {
			blackControlAndClockPanel.add(blackControlsPanel);
			blackControlAndClockPanel.add(blackClockPanel);
			blackControlAndClockPanel.add(Box.createVerticalGlue());
		}

		controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
		whiteControlAndClockPanel.setAlignmentX(0);
		blackControlAndClockPanel.setAlignmentX(0);
		if (chessArea.isWhiteOnTop()) {
			controlsPanel.add(whiteControlAndClockPanel);
			controlsPanel.add(Box.createVerticalGlue());
			controlsPanel.add(blackControlAndClockPanel);
		} else {
			controlsPanel.add(blackControlAndClockPanel);
			controlsPanel.add(Box.createVerticalGlue());
			controlsPanel.add(whiteControlAndClockPanel);
		}

		SquareSandwichLayout layout = new SquareSandwichLayout();
		layout.setEastWeight(.27);
		layout.setMaxWestWidth(200);
		layout.setWestWeight(.25);
		layout.setNorthWeight(.02);
		layout.setSouthWeight(.02);
		boardAndDropPiecesPanel.setLayout(layout);
		boardAndDropPiecesPanel.add(northFillerPanel,
				SquareSandwichLayout.NORTH);
		boardAndDropPiecesPanel.add(chessArea.getMoveList(),
				SquareSandwichLayout.WEST);
		boardAndDropPiecesPanel.add(chessArea.getBoard(),
				SquareSandwichLayout.CENTER);
		boardAndDropPiecesPanel.add(controlsPanel, SquareSandwichLayout.EAST);
		boardAndDropPiecesPanel.add(southFillerPanel,
				SquareSandwichLayout.SOUTH);

		chessArea.setLayout(new BorderLayout());
		chessArea.add(chessArea.getStatusField(), BorderLayout.SOUTH);
		chessArea.add(boardAndDropPiecesPanel, BorderLayout.CENTER);

		setBackground(chessArea.getPreferences().getBoardPreferences()
				.getBackgroundControlsColor());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Time to create WinboardChessAreaLayout: "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	private void setBackground(Color color) {
		boardAndDropPiecesPanel.setBackground(color);
		whiteControlAndClockPanel.setBackground(color);
		whiteClockPanel.setBackground(color);
		whiteControlsPanel.setBackground(color);
		blackControlAndClockPanel.setBackground(color);
		blackClockPanel.setBackground(color);
		blackControlsPanel.setBackground(color);
		controlsPanel.setBackground(color);
		northFillerPanel.setBackground(color);
		southFillerPanel.setBackground(color);
	}

	private void setupDropPieces(ChessArea chessArea) {
		Right3By2HoldingsPanel whiteHoldings = new Right3By2HoldingsPanel(
				chessArea.getBoard(), chessArea.isDroppable() ? true : false);
		Right3By2HoldingsPanel blackHoldings = new Right3By2HoldingsPanel(
				chessArea.getBoard(), chessArea.isDroppable() ? false : true);

		whiteHoldings.setBoardId(chessArea.getBoardId());
		blackHoldings.setBoardId(chessArea.getBoardId());

		if (chessArea.getWhiteHoldings() != null) {
			whiteHoldings.setFromPieceArray(chessArea.getWhiteHoldings()
					.getPieceArray());
			whiteHoldings
					.setMoveable(chessArea.getWhiteHoldings().isMoveable());
			chessArea.getWhiteHoldings().dispose();
		}
		if (chessArea.getBlackHoldings() != null) {
			blackHoldings.setFromPieceArray(chessArea.getBlackHoldings()
					.getPieceArray());
			blackHoldings
					.setMoveable(chessArea.getWhiteHoldings().isMoveable());
			chessArea.getBlackHoldings().dispose();
		}

		chessArea.setWhiteHoldings(whiteHoldings);
		chessArea.setBlackHoldings(blackHoldings);

	}
}
