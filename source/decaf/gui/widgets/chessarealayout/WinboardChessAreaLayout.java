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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.ChessClock;
import decaf.gui.widgets.ClockStateChangedListener;
import decaf.gui.widgets.SquareSandwichLayout;
import decaf.gui.widgets.holdings.Right2By4HoldingsPanel;

public class WinboardChessAreaLayout implements ChessAreaLayout {
	private static final Logger LOGGER = Logger
			.getLogger(WinboardChessAreaLayout.class);

	private JPanel boardAndDropPiecesPanel;

	private JPanel whiteControlAndClockPanel;

	private JPanel whiteClockPanel;

	private JPanel whiteControlsPanel;

	private JPanel blackControlAndClockPanel;

	private JPanel blackClockPanel;

	private JPanel blackControlsPanel;

	private JPanel topPanel;

	private JPanel mainPanel;

	private JPanel dropPiecesPanel;

	private JPanel northFillerPanel;

	private JPanel southFillerPanel;

	private JLabel whiteLabel = new JLabel("White:");

	private JLabel blackLabel = new JLabel("Black:");

	private ComponentAdapter hideDropPiecesPanelListener;

	private ChessArea chessArea;

	private ClockStateChangedListener clockStateChangedListener = new ClockStateChangedListener() {
		public void clockStateChanged(ChessClock clock) {
			setupClockLabels();
		}
	};

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

		if (dropPiecesPanel != null) {
			dropPiecesPanel.removeAll();
		}

		if (topPanel != null) {
			topPanel.removeAll();
		}

		if (boardAndDropPiecesPanel != null) {
			boardAndDropPiecesPanel.removeAll();
		}

		if (mainPanel != null) {
			mainPanel.removeAll();
		}
		if (chessArea != null) {
			chessArea.removeAll();

			if (chessArea.getWhitesClock() != null) {
				chessArea.getWhitesClock().removeClockStateChangedListener(
						clockStateChangedListener);
			}
			if (chessArea.getBlacksClock() != null) {
				chessArea.getBlacksClock().removeClockStateChangedListener(
						clockStateChangedListener);
			}
		}

		if (hideDropPiecesPanelListener != null) {
			if (chessArea.getWhiteHoldings() != null) {
				chessArea.getWhiteHoldings().removeComponentListener(
						hideDropPiecesPanelListener);
			}
			if (chessArea.getBlackHoldings() != null) {
				chessArea.getBlackHoldings().removeComponentListener(
						hideDropPiecesPanelListener);
			}
		}

	}

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

	public void init(final ChessArea chessArea) {
		long startTime = System.currentTimeMillis();
		this.chessArea = chessArea;

		whiteClockPanel = new JPanel();
		whiteControlsPanel = new JPanel();
		whiteControlAndClockPanel = new JPanel();
		blackClockPanel = new JPanel();
		blackControlsPanel = new JPanel();
		blackControlAndClockPanel = new JPanel();
		dropPiecesPanel = new JPanel();
		boardAndDropPiecesPanel = new JPanel();
		northFillerPanel = new JPanel();
		southFillerPanel = new JPanel();
		topPanel = new JPanel();
		mainPanel = new JPanel();

		chessArea.getMoveList().setMaximumSize(null);
		chessArea.getMoveList().setPreferredSize(null);
		chessArea.getMoveList().setMinimumSize(null);
		chessArea.getMoveList().setAlignmentX(Component.CENTER_ALIGNMENT);
		chessArea.getMoveList().setAlignmentY(Component.CENTER_ALIGNMENT);

		adjustForLabelChanges();

		whiteControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		whiteControlsPanel.add(chessArea.getWhiteNameLbl());

		whiteClockPanel.setLayout(new BoxLayout(whiteClockPanel,
				BoxLayout.X_AXIS));
		whiteClockPanel.add(Box.createHorizontalStrut(5));
		whiteClockPanel.add(whiteLabel);
		whiteClockPanel.add(chessArea.getMarkWhiteLabel());
		whiteClockPanel.add(chessArea.getWhitesClock());
		whiteClockPanel.add(chessArea.getWhiteLagLbl());
		whiteClockPanel.add(Box.createHorizontalGlue());

		blackControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		blackControlsPanel.add(chessArea.getBlackNameLbl());

		blackClockPanel.setLayout(new BoxLayout(blackClockPanel,
				BoxLayout.X_AXIS));
		whiteClockPanel.add(Box.createHorizontalStrut(5));
		blackClockPanel.add(blackLabel);
		blackClockPanel.add(chessArea.getMarkBlackLabel());
		blackClockPanel.add(chessArea.getBlacksClock());
		blackClockPanel.add(chessArea.getBlackLagLbl());
		blackClockPanel.add(Box.createHorizontalGlue());

		whiteControlAndClockPanel.setLayout(new BoxLayout(
				whiteControlAndClockPanel, BoxLayout.Y_AXIS));
		whiteControlAndClockPanel.add(whiteControlsPanel);
		whiteControlAndClockPanel.add(whiteClockPanel);

		blackControlAndClockPanel.setLayout(new BoxLayout(
				blackControlAndClockPanel, BoxLayout.Y_AXIS));
		blackControlAndClockPanel.add(blackControlsPanel);
		blackControlAndClockPanel.add(blackClockPanel);

		topPanel.setLayout(new GridLayout(1, 2));
		topPanel.add(whiteControlAndClockPanel);
		topPanel.add(blackControlAndClockPanel);

		setupDropPieces();

		SquareSandwichLayout layout = new SquareSandwichLayout();
		layout.setEastWeight(.20);
		layout.setWestWeight(.30);
		boardAndDropPiecesPanel.setLayout(layout);
		boardAndDropPiecesPanel.add(northFillerPanel,
				SquareSandwichLayout.NORTH);
		boardAndDropPiecesPanel.add(chessArea.getMoveList(),
				SquareSandwichLayout.WEST);
		boardAndDropPiecesPanel.add(chessArea.getBoard(),
				SquareSandwichLayout.CENTER);
		boardAndDropPiecesPanel.add(dropPiecesPanel, SquareSandwichLayout.EAST);
		boardAndDropPiecesPanel.add(southFillerPanel,
				SquareSandwichLayout.SOUTH);
		// boardAndDropPiecesPanel.setMinimumSize(new Dimension(200,200));
		// boardAndDropPiecesPanel.setPreferredSize(new Dimension(1000,1000));

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(boardAndDropPiecesPanel, BorderLayout.CENTER);

		chessArea.setLayout(new BorderLayout());
		chessArea.add(chessArea.getStatusField(), BorderLayout.SOUTH);
		chessArea.add(mainPanel, BorderLayout.CENTER);

		setBackground(chessArea.getPreferences().getBoardPreferences()
				.getBackgroundControlsColor());

		setupClockLabels();

		chessArea.getWhitesClock().addClockStateChangedListener(
				clockStateChangedListener);
		chessArea.getBlacksClock().addClockStateChangedListener(
				clockStateChangedListener);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Time to create WinboardChessAreaLayout: "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	private void setupClockLabels() {
		if (chessArea.getWhitesClock().isRunning()
				|| chessArea.getWhitesClock().isRunningWithoutTicking()) {
			whiteClockPanel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockActiveTextProperties()
					.getBackground());
			whiteLabel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockActiveTextProperties()
					.getBackground());
			whiteLabel.setForeground(chessArea.getPreferences()
					.getBoardPreferences().getClockActiveTextProperties()
					.getForeground());
			whiteLabel.setFont(chessArea.getPreferences().getBoardPreferences()
					.getClockActiveTextProperties().getFont());
			chessArea.getWhiteLagLbl().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getBackground());
			chessArea.getMarkWhiteLabel().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getBackground());
			chessArea.getWhiteLagLbl().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getForeground());
			chessArea.getMarkWhiteLabel().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getForeground());

		} else {
			whiteClockPanel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockInactiveTextProperties()
					.getBackground());
			whiteLabel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockInactiveTextProperties()
					.getBackground());
			whiteLabel.setForeground(chessArea.getPreferences()
					.getBoardPreferences().getClockInactiveTextProperties()
					.getForeground());
			whiteLabel.setFont(chessArea.getPreferences().getBoardPreferences()
					.getClockInactiveTextProperties().getFont());
			chessArea.getWhiteLagLbl().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
			chessArea.getMarkWhiteLabel().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
			chessArea.getWhiteLagLbl().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getControlLabelTextProperties().getForeground());
			chessArea.getMarkWhiteLabel().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getControlLabelTextProperties().getForeground());

		}
		if (chessArea.getBlacksClock().isRunning()
				|| chessArea.getBlacksClock().isRunningWithoutTicking()) {

			blackClockPanel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockActiveTextProperties()
					.getBackground());
			blackLabel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockActiveTextProperties()
					.getBackground());
			blackLabel.setForeground(chessArea.getPreferences()
					.getBoardPreferences().getClockActiveTextProperties()
					.getForeground());
			blackLabel.setFont(chessArea.getPreferences().getBoardPreferences()
					.getClockActiveTextProperties().getFont());
			chessArea.getBlackLagLbl().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getBackground());
			chessArea.getMarkBlackLabel().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getBackground());
			chessArea.getBlackLagLbl().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getForeground());
			chessArea.getMarkBlackLabel().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getClockActiveTextProperties().getForeground());
		} else {
			blackClockPanel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockInactiveTextProperties()
					.getBackground());
			blackLabel.setBackground(chessArea.getPreferences()
					.getBoardPreferences().getClockInactiveTextProperties()
					.getBackground());
			blackLabel.setForeground(chessArea.getPreferences()
					.getBoardPreferences().getClockInactiveTextProperties()
					.getForeground());
			blackLabel.setFont(chessArea.getPreferences().getBoardPreferences()
					.getClockInactiveTextProperties().getFont());
			chessArea.getBlackLagLbl().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
			chessArea.getMarkBlackLabel().setBackground(
					chessArea.getPreferences().getBoardPreferences()
							.getBackgroundControlsColor());
			chessArea.getBlackLagLbl().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getControlLabelTextProperties().getForeground());
			chessArea.getMarkBlackLabel().setForeground(
					chessArea.getPreferences().getBoardPreferences()
							.getControlLabelTextProperties().getForeground());
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
		dropPiecesPanel.setBackground(color);
		topPanel.setBackground(color);
		mainPanel.setBackground(color);
		dropPiecesPanel.setBackground(color);
		northFillerPanel.setBackground(color);
		southFillerPanel.setBackground(color);
	}

	private void setupDropPieces() {
		Right2By4HoldingsPanel whiteHoldings = new Right2By4HoldingsPanel(
				chessArea.getBoard(), chessArea.isDroppable() ? true : false);
		Right2By4HoldingsPanel blackHoldings = new Right2By4HoldingsPanel(
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

		dropPiecesPanel.setLayout(new BoxLayout(dropPiecesPanel,
				BoxLayout.Y_AXIS));
		if (chessArea.isWhiteOnTop()) {
			dropPiecesPanel.add(whiteHoldings);
			dropPiecesPanel.add(blackHoldings);
		} else {
			dropPiecesPanel.add(blackHoldings);
			dropPiecesPanel.add(whiteHoldings);
		}

		hideDropPiecesPanelListener = new ComponentAdapter() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
				if (!chessArea.getWhiteHoldings().isVisible()) {
					dropPiecesPanel.setVisible(false);
					dropPiecesPanel.invalidate();
					boardAndDropPiecesPanel.validate();
				}
			}

			@Override
			public void componentShown(ComponentEvent arg0) {

				if (chessArea.getWhiteHoldings().isVisible()) {
					dropPiecesPanel.setVisible(true);
					dropPiecesPanel.invalidate();
					boardAndDropPiecesPanel.validate();
				}
			}
		};
		chessArea.getWhiteHoldings().addComponentListener(
				hideDropPiecesPanelListener);
		chessArea.getBlackHoldings().addComponentListener(
				hideDropPiecesPanelListener);
	}
}
