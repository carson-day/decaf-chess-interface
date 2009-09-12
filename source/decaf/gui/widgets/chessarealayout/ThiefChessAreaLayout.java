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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import decaf.gui.SwingUtils;
import decaf.gui.pref.BoardPreferences;
import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.SquareSandwichLayout;
import decaf.gui.widgets.holdings.LeftRightHoldingsPanel;
import decaf.gui.widgets.holdings.Right2By4HoldingsPanel;
import decaf.gui.widgets.holdings.Right3By2HoldingsPanel;
import decaf.gui.widgets.holdings.TopBottomHoldingsPanel;
import decaf.util.StringUtility;

public class ThiefChessAreaLayout implements ChessAreaLayout {

	private static final int MAX_NAME_LENGTH = 24;

	private static final Logger LOGGER = Logger
			.getLogger(ThiefChessAreaLayout.class);

	private JPanel boardAndDropPiecesPanel;

	private JPanel whiteControlsPanel;

	private JPanel blackControlsPanel;

	private JPanel mainPanel;

	private JPanel boradAndControlsPanel;

	private JPanel northFillerPanel;

	private JPanel southFillerPanel;

	private JPanel eastFillerPanel;

	private JPanel westFillerPanel;

	private JPanel auxileryPanel;

	private ChessArea chessArea;

	private ComponentAdapter hideDropPiecesPanelListener;

	private boolean isWhiteOnTop;

	public void adjustForLabelChanges() {
		if (chessArea.getWhiteNameLbl().getText() != null) {
			String infoString = chessArea.getWhiteNameLbl().getText();
			if (infoString.length() > MAX_NAME_LENGTH) {
				infoString = infoString.substring(0, MAX_NAME_LENGTH);
			}
			chessArea.getWhiteNameLbl().setText(
					StringUtility.padCharsToRight(infoString, ' ',
							MAX_NAME_LENGTH));
		}
		if (chessArea.getBlackNameLbl().getText() != null) {
			String infoString = chessArea.getBlackNameLbl().getText();
			if (infoString.length() > MAX_NAME_LENGTH) {
				infoString = infoString.substring(0, MAX_NAME_LENGTH);
			}
			chessArea.getBlackNameLbl().setText(
					StringUtility.padCharsToRight(infoString, ' ',
							MAX_NAME_LENGTH));
		}
	}

	public void dispose() {
		LOGGER.error("Disposing ThiefChessAreaLAyout");
		SwingUtils.dispose(boradAndControlsPanel);
		SwingUtils.dispose(northFillerPanel);
		SwingUtils.dispose(southFillerPanel);
		SwingUtils.dispose(eastFillerPanel);
		SwingUtils.dispose(westFillerPanel);
		SwingUtils.dispose(auxileryPanel);
		SwingUtils.dispose(whiteControlsPanel);
		SwingUtils.dispose(boardAndDropPiecesPanel);
		SwingUtils.dispose(blackControlsPanel);
		SwingUtils.dispose(mainPanel);
		SwingUtils.dispose(boardAndDropPiecesPanel);
	}

	public void init(ChessArea chessArea) {
		long startTime = System.currentTimeMillis();
		this.chessArea = chessArea;
		isWhiteOnTop = chessArea.isWhiteOnTop();

		blackControlsPanel = new JPanel();
		whiteControlsPanel = new JPanel();
		boardAndDropPiecesPanel = new JPanel();
		boradAndControlsPanel = new JPanel();
		northFillerPanel = new JPanel();
		southFillerPanel = new JPanel();
		eastFillerPanel = new JPanel();
		westFillerPanel = new JPanel();
		auxileryPanel = new JPanel();
		boradAndControlsPanel = new JPanel();
		mainPanel = new JPanel();

		adjustForLabelChanges();

		whiteControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		whiteControlsPanel.add(chessArea.getMarkWhiteLabel());
		whiteControlsPanel.add(chessArea.getWhiteNameLbl());
		whiteControlsPanel.add(chessArea.getWhitesClock());
		whiteControlsPanel.add(chessArea.getWhiteLagLbl());

		blackControlsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		blackControlsPanel.add(chessArea.getMarkBlackLabel());
		blackControlsPanel.add(chessArea.getBlackNameLbl());
		blackControlsPanel.add(chessArea.getBlacksClock());
		blackControlsPanel.add(chessArea.getBlackLagLbl());

		layoutControlsAndBoard();

		layoutBoardAndDropPieces();

		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(boradAndControlsPanel, BorderLayout.CENTER);

		chessArea.setLayout(new BorderLayout());
		chessArea.add(chessArea.getStatusField(), BorderLayout.SOUTH);
		chessArea.add(mainPanel, BorderLayout.CENTER);

		setBackground(chessArea.getPreferences().getBoardPreferences()
				.getBackgroundControlsColor());

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Time to create DefaultChessAreaLayout: "
					+ (System.currentTimeMillis() - startTime));
		}
	}

	private void layoutBoardAndDropPieces() {
		auxileryPanel.removeAll();
		boardAndDropPiecesPanel.removeAll();
		chessArea.getMoveList().setMaximumSize(null);
		chessArea.getMoveList().setPreferredSize(null);
		chessArea.getMoveList().setMinimumSize(null);
		chessArea.getMoveList().setAlignmentY(0.5F);
		chessArea.getMoveList().setAlignmentX(0.5F);

		if (hideDropPiecesPanelListener != null) {
			chessArea.getMoveList().removeComponentListener(
					hideDropPiecesPanelListener);
			chessArea.getWhiteHoldings().removeComponentListener(
					hideDropPiecesPanelListener);
			chessArea.getBlackHoldings().removeComponentListener(
					hideDropPiecesPanelListener);
		}

		if (chessArea.getPreferences().getBoardPreferences()
				.getDropPiecesLocation() == BoardPreferences.DROP_PIECES_ON_RIGHT_2x4) {

			Right2By4HoldingsPanel whiteHoldings = new Right2By4HoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? true
							: false);
			Right2By4HoldingsPanel blackHoldings = new Right2By4HoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? false
							: true);

			whiteHoldings.setBoardId(chessArea.getBoardId());
			blackHoldings.setBoardId(chessArea.getBoardId());

			if (chessArea.getWhiteHoldings() != null) {
				whiteHoldings.setFromPieceArray(chessArea.getWhiteHoldings()
						.getPieceArray());
				whiteHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getWhiteHoldings().dispose();
			}
			if (chessArea.getBlackHoldings() != null) {
				blackHoldings.setFromPieceArray(chessArea.getBlackHoldings()
						.getPieceArray());
				blackHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getBlackHoldings().dispose();
			}

			chessArea.setWhiteHoldings(whiteHoldings);
			chessArea.setBlackHoldings(blackHoldings);

			auxileryPanel.setLayout(new BoxLayout(auxileryPanel,
					BoxLayout.Y_AXIS));
			if (isWhiteOnTop) {
				auxileryPanel.add(whiteHoldings);
				auxileryPanel.add(blackHoldings);
			} else {
				auxileryPanel.add(blackHoldings);
				auxileryPanel.add(whiteHoldings);
			}
			auxileryPanel.add(Box.createVerticalGlue());
			auxileryPanel.invalidate();

			hideDropPiecesPanelListener = new ComponentAdapter() {

				@Override
				public void componentHidden(ComponentEvent arg0) {
					if (!chessArea.getWhiteHoldings().isVisible()) {
						auxileryPanel.setVisible(false);
						auxileryPanel.invalidate();
						boardAndDropPiecesPanel.validate();
					}
				}

				@Override
				public void componentShown(ComponentEvent arg0) {

					if (chessArea.getWhiteHoldings().isVisible()) {
						auxileryPanel.setVisible(true);
						auxileryPanel.invalidate();
						boardAndDropPiecesPanel.validate();
					}
				}
			};
			chessArea.getWhiteHoldings().addComponentListener(
					hideDropPiecesPanelListener);
			chessArea.getBlackHoldings().addComponentListener(
					hideDropPiecesPanelListener);

			SquareSandwichLayout layout = new SquareSandwichLayout();
			layout.setEastWeight(.20);
			layout.setSouthWeight(.25);
			layout.setMaxSouthWidth(200);
			boardAndDropPiecesPanel.setLayout(layout);

			boardAndDropPiecesPanel.add(northFillerPanel,
					SquareSandwichLayout.NORTH);
			boardAndDropPiecesPanel.add(westFillerPanel,
					SquareSandwichLayout.WEST);
			boardAndDropPiecesPanel.add(chessArea.getBoard(),
					SquareSandwichLayout.CENTER);
			boardAndDropPiecesPanel.add(auxileryPanel,
					SquareSandwichLayout.EAST);
			boardAndDropPiecesPanel.add(chessArea.getMoveList(),
					SquareSandwichLayout.SOUTH);
		} else if (chessArea.getPreferences().getBoardPreferences()
				.getDropPiecesLocation() == BoardPreferences.DROP_PIECES_ON_RIGHT_3x2) {

			Right3By2HoldingsPanel whiteHoldings = new Right3By2HoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? true
							: false);
			Right3By2HoldingsPanel blackHoldings = new Right3By2HoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? false
							: true);

			whiteHoldings.setBoardId(chessArea.getBoardId());
			blackHoldings.setBoardId(chessArea.getBoardId());

			whiteHoldings
					.setOrientation(isWhiteOnTop ? Right3By2HoldingsPanel.TOP_ORIENTATION
							: Right3By2HoldingsPanel.BOTTOM_ORIENTATION);
			blackHoldings
					.setOrientation(isWhiteOnTop ? Right3By2HoldingsPanel.BOTTOM_ORIENTATION
							: Right3By2HoldingsPanel.TOP_ORIENTATION);

			if (chessArea.getWhiteHoldings() != null) {
				whiteHoldings.setFromPieceArray(chessArea.getWhiteHoldings()
						.getPieceArray());
				whiteHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getWhiteHoldings().dispose();
			}
			if (chessArea.getBlackHoldings() != null) {
				blackHoldings.setFromPieceArray(chessArea.getBlackHoldings()
						.getPieceArray());
				blackHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getBlackHoldings().dispose();
			}

			chessArea.setWhiteHoldings(whiteHoldings);
			chessArea.setBlackHoldings(blackHoldings);

			chessArea.getMoveList().setMaximumSize(new Dimension(200, 3000));
			chessArea.getMoveList().setPreferredSize(new Dimension(200, 3000));
			chessArea.getMoveList().setAlignmentY(0);
			whiteHoldings.setAlignmentY(0);
			blackHoldings.setAlignmentY(0);
			chessArea.getMoveList().setAlignmentX(0);
			whiteHoldings.setAlignmentX(0);
			blackHoldings.setAlignmentX(0);

			auxileryPanel.setLayout(new BoxLayout(auxileryPanel,
					BoxLayout.Y_AXIS));
			if (isWhiteOnTop) {
				auxileryPanel.add(whiteHoldings);
				auxileryPanel.add(chessArea.getMoveList());
				auxileryPanel.add(Box.createVerticalGlue());
				auxileryPanel.add(blackHoldings);
			} else {
				auxileryPanel.add(blackHoldings);
				auxileryPanel.add(chessArea.getMoveList());
				auxileryPanel.add(Box.createVerticalGlue());
				auxileryPanel.add(whiteHoldings);
			}
			auxileryPanel.invalidate();

			hideDropPiecesPanelListener = new ComponentAdapter() {

				@Override
				public void componentHidden(ComponentEvent arg0) {
					if (!chessArea.getWhiteHoldings().isVisible()
							&& !chessArea.getMoveList().isVisible()) {
						auxileryPanel.setVisible(false);
						auxileryPanel.invalidate();
						boardAndDropPiecesPanel.validate();
					}
				}

				@Override
				public void componentShown(ComponentEvent arg0) {

					if (chessArea.getWhiteHoldings().isVisible()
							|| chessArea.getMoveList().isVisible()) {
						auxileryPanel.setVisible(true);
						auxileryPanel.invalidate();
						boardAndDropPiecesPanel.validate();
					}
				}
			};
			chessArea.getMoveList().addComponentListener(
					hideDropPiecesPanelListener);
			chessArea.getWhiteHoldings().addComponentListener(
					hideDropPiecesPanelListener);
			chessArea.getBlackHoldings().addComponentListener(
					hideDropPiecesPanelListener);

			SquareSandwichLayout layout = new SquareSandwichLayout();
			layout.setEastWeight(.27);
			boardAndDropPiecesPanel.setLayout(layout);
			boardAndDropPiecesPanel.add(northFillerPanel,
					SquareSandwichLayout.NORTH);
			boardAndDropPiecesPanel.add(westFillerPanel,
					SquareSandwichLayout.WEST);
			boardAndDropPiecesPanel.add(chessArea.getBoard(),
					SquareSandwichLayout.CENTER);
			boardAndDropPiecesPanel.add(auxileryPanel,
					SquareSandwichLayout.EAST);
			boardAndDropPiecesPanel.add(southFillerPanel,
					SquareSandwichLayout.SOUTH);

		} else if (chessArea.getPreferences().getBoardPreferences()
				.getDropPiecesLocation() == BoardPreferences.DROP_PIECES_ON_LEFT_RIGHT) {

			LeftRightHoldingsPanel whiteHoldings = new LeftRightHoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? true
							: false);
			LeftRightHoldingsPanel blackHoldings = new LeftRightHoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? false
							: true);

			whiteHoldings.setBoardId(chessArea.getBoardId());
			blackHoldings.setBoardId(chessArea.getBoardId());

			whiteHoldings
					.setOrientation(isWhiteOnTop ? LeftRightHoldingsPanel.LEFT_ORIENTATION
							: LeftRightHoldingsPanel.RIGHT_ORIENTATION);
			blackHoldings
					.setOrientation(isWhiteOnTop ? LeftRightHoldingsPanel.RIGHT_ORIENTATION
							: LeftRightHoldingsPanel.LEFT_ORIENTATION);

			if (chessArea.getWhiteHoldings() != null) {
				whiteHoldings.setFromPieceArray(chessArea.getWhiteHoldings()
						.getPieceArray());
				whiteHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getWhiteHoldings().dispose();
			}
			if (chessArea.getBlackHoldings() != null) {
				blackHoldings.setFromPieceArray(chessArea.getBlackHoldings()
						.getPieceArray());
				blackHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getBlackHoldings().dispose();
			}

			chessArea.setWhiteHoldings(whiteHoldings);
			chessArea.setBlackHoldings(blackHoldings);

			SquareSandwichLayout layout = new SquareSandwichLayout();
			layout.setWestWeight(.11);
			layout.setEastWeight(.11);
			layout.setSouthWeight(.25);
			layout.setMaxSouthWidth(200);
			boardAndDropPiecesPanel.setLayout(layout);

			boardAndDropPiecesPanel.add(northFillerPanel,
					SquareSandwichLayout.NORTH);
			boardAndDropPiecesPanel.add(isWhiteOnTop ? whiteHoldings
					: blackHoldings, SquareSandwichLayout.WEST);
			boardAndDropPiecesPanel.add(chessArea.getBoard(),
					SquareSandwichLayout.CENTER);
			boardAndDropPiecesPanel.add(chessArea.getMoveList(),
					SquareSandwichLayout.SOUTH);
			boardAndDropPiecesPanel.add(isWhiteOnTop ? blackHoldings
					: whiteHoldings, SquareSandwichLayout.EAST);

		} else if (chessArea.getPreferences().getBoardPreferences()
				.getDropPiecesLocation() == BoardPreferences.DROP_PIECES_ON_TOP_BOTTOM) {

			TopBottomHoldingsPanel whiteHoldings = new TopBottomHoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? true
							: false);
			TopBottomHoldingsPanel blackHoldings = new TopBottomHoldingsPanel(
					chessArea.getBoard(), chessArea.isDroppable() ? false
							: true);

			whiteHoldings.setBoardId(chessArea.getBoardId());
			blackHoldings.setBoardId(chessArea.getBoardId());

			whiteHoldings
					.setOrientation(isWhiteOnTop ? TopBottomHoldingsPanel.NORTH_ORIENTATION
							: TopBottomHoldingsPanel.SOUTH_ORIENTATION);
			blackHoldings
					.setOrientation(isWhiteOnTop ? TopBottomHoldingsPanel.SOUTH_ORIENTATION
							: TopBottomHoldingsPanel.NORTH_ORIENTATION);

			if (chessArea.getWhiteHoldings() != null) {
				whiteHoldings.setFromPieceArray(chessArea.getWhiteHoldings()
						.getPieceArray());
				whiteHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getWhiteHoldings().dispose();
			}
			if (chessArea.getBlackHoldings() != null) {
				blackHoldings.setFromPieceArray(chessArea.getBlackHoldings()
						.getPieceArray());
				blackHoldings.setMoveable(chessArea.getWhiteHoldings()
						.isMoveable());
				chessArea.getBlackHoldings().dispose();
			}
			chessArea.setWhiteHoldings(whiteHoldings);
			chessArea.setBlackHoldings(blackHoldings);

			SquareSandwichLayout layout = new SquareSandwichLayout();
			layout.setNorthWeight(.10);
			layout.setSouthWeight(.10);
			layout.setEastWeight(.20);
			layout.setMaxEastWidth(200);
			boardAndDropPiecesPanel.setLayout(layout);

			boardAndDropPiecesPanel.add(isWhiteOnTop ? whiteHoldings
					: blackHoldings, SquareSandwichLayout.NORTH);
			boardAndDropPiecesPanel.add(westFillerPanel,
					SquareSandwichLayout.WEST);
			boardAndDropPiecesPanel.add(chessArea.getBoard(),
					SquareSandwichLayout.CENTER);
			boardAndDropPiecesPanel.add(isWhiteOnTop ? blackHoldings
					: whiteHoldings, SquareSandwichLayout.SOUTH);
			boardAndDropPiecesPanel.add(chessArea.getMoveList(),
					SquareSandwichLayout.EAST);

			/*
			 * boardAndDropPiecesPanel.setLayout(new GridBagLayout());
			 * GridBagConstraints constraints = new GridBagConstraints();
			 * constraints.gridx = 0; constraints.gridy = 0; constraints.fill =
			 * GridBagConstraints.BOTH; constraints.weightx = .9;
			 * constraints.weighty = 1.0;
			 * boardAndDropPiecesPanel.add(auxileryPanel, constraints);
			 * 
			 * constraints.gridx = 1; constraints.gridy = 0; constraints.fill =
			 * GridBagConstraints.BOTH; constraints.weightx = .1;
			 * constraints.weighty = 1.0;
			 * boardAndDropPiecesPanel.add(chessArea.getMoveList(),
			 * constraints); chessArea.getMoveList().setMaximumSize(new
			 * Dimension(200, 1000));
			 * chessArea.getMoveList().setPreferredSize(new Dimension(100,
			 * 400)); chessArea.getMoveList().setMinimumSize(new Dimension(100,
			 * 300));
			 */
		} else {
			throw new IllegalStateException("Invalid Drop Location: "
					+ chessArea.getPreferences().getBoardPreferences()
							.getDropPiecesLocation());
		}
		boardAndDropPiecesPanel.invalidate();
		auxileryPanel.invalidate();
	}

	private void layoutControlsAndBoard() {
		boradAndControlsPanel.removeAll();
		boradAndControlsPanel.setLayout(new BorderLayout());
		boradAndControlsPanel.add(whiteControlsPanel,
				isWhiteOnTop ? BorderLayout.NORTH : BorderLayout.SOUTH);
		boradAndControlsPanel.add(blackControlsPanel,
				isWhiteOnTop ? BorderLayout.SOUTH : BorderLayout.NORTH);
		boradAndControlsPanel.add(boardAndDropPiecesPanel, BorderLayout.CENTER);
		boradAndControlsPanel.invalidate();

	}

	private void setBackground(Color color) {
		boardAndDropPiecesPanel.setBackground(color);
		blackControlsPanel.setBackground(color);
		whiteControlsPanel.setBackground(color);
		boradAndControlsPanel.setBackground(color);
		northFillerPanel.setBackground(color);
		southFillerPanel.setBackground(color);
		eastFillerPanel.setBackground(color);
		westFillerPanel.setBackground(color);
		auxileryPanel.setBackground(color);
		mainPanel.setBackground(color);
	}
}
