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
package decaf.gui.pref;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.ChessSet;
import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.SquareImageBackground;
import decaf.gui.widgets.selectioncontrol.ColorSelectionControl;
import decaf.gui.widgets.selectioncontrol.ComboBoxItem;
import decaf.gui.widgets.selectioncontrol.ComboBoxItems;
import decaf.gui.widgets.selectioncontrol.SelectionControl;
import decaf.gui.widgets.selectioncontrol.SelectionControlListener;
import decaf.gui.widgets.selectioncontrol.TextPropertiesSelectionControl;
import decaf.moveengine.Coordinates;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;
import decaf.resources.ResourceManagerFactory;
import decaf.util.BorderUtil;
import decaf.util.TextProperties;

public class ChessGuiTab extends PreferencesTab implements
		SelectionControlListener, ItemListener {

	private static final ComboBoxItems BORDERS = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Empty", new Integer(
							BorderUtil.EMPTY_BORDER)),
					new ComboBoxItem("Black Thin", new Integer(
							BorderUtil.BLACK_LINE_THIN_SQUARE_BORDER)),
					new ComboBoxItem("Black Thick", new Integer(
							BorderUtil.BLACK_LINE_SQUARE_BORDER)),
					new ComboBoxItem("White Thin", new Integer(
							BorderUtil.WHITE_LINE_THIN_SQUARE_BORDER)),
					new ComboBoxItem("White Thick", new Integer(
							BorderUtil.WHITE_LINE_SQUARE_BORDER)),
					new ComboBoxItem("Raised Bevel", new Integer(
							BorderUtil.RAISED_BEVEL_BORDER)),
					new ComboBoxItem("Lowered Bevel", new Integer(
							BorderUtil.LOWERED_BEVEL_BORDER)),
					new ComboBoxItem("Raised Etched", new Integer(
							BorderUtil.RAISED_ETCHED_BORDER)),
					new ComboBoxItem("Lowered Etched", new Integer(
							BorderUtil.LOWERED_ETCHED_BORDER)) });

	private static final ComboBoxItems LAYOUTS = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Thief Layout",
							"decaf.gui.widgets.chessarealayout.ThiefChessAreaLayout"),
					// new ComboBoxItem("Winboard Layout",
					// "decaf.gui.widgets.chessarealayout.WinboardChessAreaLayout"),
					new ComboBoxItem("Right Oriented Layout",
							"decaf.gui.widgets.chessarealayout.RightOrientedChessAreaLayout") });

	private static final ComboBoxItems HOLDINGS_LAYOUT = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Right (2x4)", new Integer(
							BoardPreferences.DROP_PIECES_ON_RIGHT_2x4)),
					new ComboBoxItem("Right (3x2)", new Integer(
							BoardPreferences.DROP_PIECES_ON_RIGHT_3x2)),
					new ComboBoxItem("Top/Bottom", new Integer(
							BoardPreferences.DROP_PIECES_ON_TOP_BOTTOM)),
					new ComboBoxItem("Left/Right", new Integer(
							BoardPreferences.DROP_PIECES_ON_LEFT_RIGHT)) });

	private static final ComboBoxItems PIECE_SIZE_DELTA = new ComboBoxItems(
			new ComboBoxItem[] { new ComboBoxItem("0", new Integer(0)),
					new ComboBoxItem("2", new Integer(2)),
					new ComboBoxItem("4", new Integer(4)),
					new ComboBoxItem("6", new Integer(6)),
					new ComboBoxItem("8", new Integer(8)),
					new ComboBoxItem("10", new Integer(10)) });

	private static final ComboBoxItems HIGHLIGHT_MODE = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("None", new Integer(
							BoardPreferences.NONE_SQUARE_SELECTION_MODE)),
					new ComboBoxItem("Border Fade Away", new Integer(
							BoardPreferences.FADE_SQUARE_SELECTION_MODE)),
					new ComboBoxItem("Border Solid", new Integer(
							BoardPreferences.BORDER_SQUARE_SELECTION_MODE)),
					new ComboBoxItem(
							"Fill Square",
							new Integer(
									BoardPreferences.FILL_BACKGROUND_SQUARE_SELECTION_MODE)),
					new ComboBoxItem(
							"Diagonal Line Square",
							new Integer(
									BoardPreferences.DIAGONAL_LINE_BACKGROUND_SQUARE_SELECTION_MODE)) });

	private static ComboBoxItems CHESS_SETS = null;

	private static ComboBoxItems IMAGE_BOARD_BACXKGROUNDS = null;

	static {
		String[] sets = ResourceManagerFactory.getManager().getChessSetNames();
		ComboBoxItem[] items = new ComboBoxItem[sets.length];

		for (int i = 0; i < sets.length; i++) {
			items[i] = new ComboBoxItem(sets[i], new ChessSet(sets[i]));
		}
		CHESS_SETS = new ComboBoxItems(items);

		String[] boardBackgrounds = ResourceManagerFactory.getManager()
				.getBackgroundNames();
		ComboBoxItem[] backgroundItems = new ComboBoxItem[boardBackgrounds.length + 1];

		backgroundItems[0] = new ComboBoxItem("Use Solid Colors", null);
		for (int i = 0; i < boardBackgrounds.length; i++) {
			backgroundItems[i + 1] = new ComboBoxItem(boardBackgrounds[i],
					new SquareImageBackground(boardBackgrounds[i]));
		}
		IMAGE_BOARD_BACXKGROUNDS = new ComboBoxItems(backgroundItems);
	}

	private JComboBox layouts = new JComboBox(LAYOUTS.getItems());

	private JComboBox chessSets = new JComboBox(CHESS_SETS.getItems());

	private JComboBox squareBackgrounds = new JComboBox(
			IMAGE_BOARD_BACXKGROUNDS.getItems());

	private JComboBox dropSquareBorder = new JComboBox(BORDERS.getItems());

	private JComboBox squareBorder = new JComboBox(BORDERS.getItems());

	private JComboBox holdingsLocation = new JComboBox(HOLDINGS_LAYOUT
			.getItems());

	private JComboBox pieceSizeDelta = new JComboBox(PIECE_SIZE_DELTA
			.getItems());

	private JComboBox highlightMode = new JComboBox(HIGHLIGHT_MODE.getItems());

	private JCheckBox isShowingCoordinates = new JCheckBox("");

	private TextPropertiesSelectionControl clockActiveTextProperties = new TextPropertiesSelectionControl(
			"Clock Ticking", null, null);

	private TextPropertiesSelectionControl clockInactiveTextProperties = new TextPropertiesSelectionControl(
			"Clock Stopped", null, null);

	private TextPropertiesSelectionControl controlLabelTextProperties = new TextPropertiesSelectionControl(
			"Text", null, null);

	private TextPropertiesSelectionControl statusBarTextProperties = new TextPropertiesSelectionControl(
			"Status Bar text", null, null);

	private TextPropertiesSelectionControl dropSquareLabelTextProperties = new TextPropertiesSelectionControl(
			"Holdings Piece Label", null, null);

	private TextPropertiesSelectionControl timeUpTextProperties = new TextPropertiesSelectionControl(
			"Time Up Label", null, null);

	private ColorSelectionControl dropSquareColor = new ColorSelectionControl(
			"Holdings Square Background", null, null);

	private ColorSelectionControl backgroundControlsColor = new ColorSelectionControl(
			"Background", null, null);

	private ColorSelectionControl lightSquareBackgroundColor = new ColorSelectionControl(
			"Light Board Square", null, null);

	private ColorSelectionControl darkSquareBackgroundColor = new ColorSelectionControl(
			"Dark Board Square", null, null);

	private ColorSelectionControl moveHighlightColor = new ColorSelectionControl(
			"Move Marker Color", null, null);

	private ChessArea chessArea = null;

	private Preferences preferences;

	private JPanel boxPanel = null;

	private JPanel leftTopPanel = null;

	JSplitPane topButtomSplit;

	JSplitPane splitPane;

	private boolean ignoreRequest = false;

	public ChessGuiTab(Preferences preferences) {
		super("Chess Gui");
		this.preferences = preferences;
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
		boxPanel.add(new LabeledComponent("Set", chessSets));
		boxPanel.add(backgroundControlsColor);
		boxPanel.add(new LabeledComponent("Square Backgrounds",
				squareBackgrounds));
		boxPanel.add(lightSquareBackgroundColor);
		boxPanel.add(darkSquareBackgroundColor);
		boxPanel.add(new LabeledComponent("Layout", layouts));
		boxPanel
				.add(new LabeledComponent("Holdings Location", holdingsLocation));
		boxPanel.add(new LabeledComponent("Holdings Square Border",
				dropSquareBorder));
		boxPanel.add(dropSquareColor);
		boxPanel.add(new LabeledComponent("Chess Square Border", squareBorder));
		boxPanel.add(new LabeledComponent("Square Size - Piece Size Delta",
				pieceSizeDelta));
		boxPanel.add(new LabeledComponent("Move Marker Mode", highlightMode));
		boxPanel.add(moveHighlightColor);
		boxPanel.add(new LabeledComponent("Show Coordinates",
				isShowingCoordinates));

		JPanel leftTopPanel = new JPanel();
		leftTopPanel.setLayout(new BoxLayout(leftTopPanel, BoxLayout.Y_AXIS));
		leftTopPanel.add(clockActiveTextProperties);
		leftTopPanel.add(clockInactiveTextProperties);
		leftTopPanel.add(controlLabelTextProperties);
		leftTopPanel.add(statusBarTextProperties);
		leftTopPanel.add(dropSquareLabelTextProperties);
		leftTopPanel.add(timeUpTextProperties);

		JPanel chessBoardPanel = new JPanel();
		chessBoardPanel.setLayout(new BoxLayout(chessBoardPanel,
				BoxLayout.Y_AXIS));
		chessArea = new ChessArea();
		chessArea.setPreferences(preferences);
		Position position = new Position();
		position = position.set(Coordinates.E2, Piece.EMPTY);
		position = position.set(Coordinates.E4, Piece.WP);
		chessArea.setup("empty", "Player1", "1456", "Player2", "1567", true,
				false, 300, 0, position, true);
		chessArea.getBoard().selectSquare(Coordinates.E4);
		chessArea.getBoard().selectSquare(Coordinates.E2);

		chessArea.setPreferredSize(new Dimension(350, 400));

		chessArea.setBlackMarkText("(2)");

		chessArea.setWhiteDropPieces(new int[] { Piece.WR, Piece.WB, Piece.WN,
				Piece.WQ, Piece.WP });
		chessArea.setBlackDropPieces(new int[] { Piece.BR, Piece.BB, Piece.BN,
				Piece.BQ, Piece.BP });
		chessArea.setMinimumSize(new Dimension(400, 200));
		chessArea.setStatusText("Sample status text");
		chessArea.setWhiteTime(58 * 1000 * 2);
		chessArea.setBlackTime(60 * 1000 * 2);

		chessBoardPanel.add(Box.createVerticalGlue());
		chessBoardPanel.add(chessArea);
		chessBoardPanel.add(Box.createVerticalGlue());

		topButtomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topButtomSplit.setTopComponent(leftTopPanel);
		topButtomSplit.setRightComponent(chessBoardPanel);
		topButtomSplit.setOneTouchExpandable(true);
		topButtomSplit.setResizeWeight(.4F);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(boxPanel);
		splitPane.setRightComponent(topButtomSplit);
		splitPane.setResizeWeight(.3F);
		splitPane.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

		layouts.addItemListener(this);
		chessSets.addItemListener(this);
		squareBackgrounds.addItemListener(this);
		dropSquareBorder.addItemListener(this);
		squareBorder.addItemListener(this);
		holdingsLocation.addItemListener(this);
		pieceSizeDelta.addItemListener(this);
		isShowingCoordinates.addItemListener(this);
		highlightMode.addItemListener(this);

		clockActiveTextProperties.addSelectionControlListener(this);
		clockInactiveTextProperties.addSelectionControlListener(this);
		statusBarTextProperties.addSelectionControlListener(this);
		controlLabelTextProperties.addSelectionControlListener(this);
		timeUpTextProperties.addSelectionControlListener(this);

		dropSquareColor.addSelectionControlListener(this);
		backgroundControlsColor.addSelectionControlListener(this);
		lightSquareBackgroundColor.addSelectionControlListener(this);
		darkSquareBackgroundColor.addSelectionControlListener(this);
		moveHighlightColor.addSelectionControlListener(this);

	}

	@Override
	public void dispose() {
		boxPanel.removeAll();
		leftTopPanel.removeAll();
		splitPane.removeAll();
		topButtomSplit.removeAll();
		chessArea.dispose();
		removeAll();

		layouts.removeItemListener(this);
		chessSets.removeItemListener(this);
		squareBackgrounds.removeItemListener(this);
		dropSquareBorder.removeItemListener(this);
		squareBorder.removeItemListener(this);
		holdingsLocation.removeItemListener(this);
		pieceSizeDelta.removeItemListener(this);
		isShowingCoordinates.removeItemListener(this);
		highlightMode.removeItemListener(this);

		clockActiveTextProperties.removeSelectionControlListener(this);
		clockInactiveTextProperties.removeSelectionControlListener(this);
		statusBarTextProperties.removeSelectionControlListener(this);
		controlLabelTextProperties.removeSelectionControlListener(this);
		timeUpTextProperties.removeSelectionControlListener(this);

		dropSquareColor.removeSelectionControlListener(this);
		backgroundControlsColor.removeSelectionControlListener(this);
		lightSquareBackgroundColor.removeSelectionControlListener(this);
		darkSquareBackgroundColor.removeSelectionControlListener(this);
		moveHighlightColor.removeSelectionControlListener(this);
	}

	public void itemStateChanged(ItemEvent arg0) {
		if (!ignoreRequest) {
			save(preferences);
			chessArea.setPreferences(preferences);
			chessArea.getBoard().unselectAllSquares();
			chessArea.getBoard().selectSquare(Coordinates.E4);
			chessArea.getBoard().selectSquare(Coordinates.E2);
			chessArea.getLayout().layoutContainer(chessArea);
			updateHoldingsState();
			chessArea.repaint();
		}
	}

	@Override
	public void load(Preferences preferences) {
		if (!ignoreRequest) {
			ignoreRequest = true;
			this.preferences = preferences;
			chessSets.setSelectedIndex(CHESS_SETS.getIndexWithValue(preferences
					.getBoardPreferences().getSet()));
			squareBackgrounds.setSelectedIndex(IMAGE_BOARD_BACXKGROUNDS
					.getIndexWithValue(preferences.getBoardPreferences()
							.getSquareImageBackground()));
			dropSquareBorder.setSelectedIndex(BORDERS
					.getIndexWithValue(preferences.getBoardPreferences()
							.getDropSquareBorder()));
			squareBorder.setSelectedIndex(BORDERS.getIndexWithValue(preferences
					.getBoardPreferences().getSquareBorder()));
			pieceSizeDelta.setSelectedIndex(PIECE_SIZE_DELTA
					.getIndexWithValue(preferences.getBoardPreferences()
							.getPieceSizeDelta()));
			highlightMode.setSelectedIndex(HIGHLIGHT_MODE
					.getIndexWithValue(preferences.getBoardPreferences()
							.getSquareSelectionMode()));

			layouts.setSelectedIndex(LAYOUTS.getIndexWithValue(preferences
					.getBoardPreferences().getLayoutClassName()));
			holdingsLocation.setSelectedIndex(HOLDINGS_LAYOUT
					.getIndexWithValue(preferences.getBoardPreferences()
							.getDropPiecesLocation()));

			isShowingCoordinates.setSelected(preferences.getBoardPreferences()
					.isShowingCoordinates());

			clockActiveTextProperties.setValue(preferences
					.getBoardPreferences().getClockActiveTextProperties());
			clockInactiveTextProperties.setValue(preferences
					.getBoardPreferences().getClockInactiveTextProperties());
			controlLabelTextProperties.setValue(preferences
					.getBoardPreferences().getControlLabelTextProperties());
			statusBarTextProperties.setValue(preferences.getBoardPreferences()
					.getStatusBarTextProperties());
			dropSquareLabelTextProperties.setValue(preferences
					.getBoardPreferences().getDropSquareLabelTextProperties());

			timeUpTextProperties.setValue(preferences.getBoardPreferences()
					.getMarkTextProperties());

			dropSquareColor.setValue(preferences.getBoardPreferences()
					.getDropSquareColor());
			backgroundControlsColor.setValue(preferences.getBoardPreferences()
					.getBackgroundControlsColor());
			lightSquareBackgroundColor.setValue(preferences
					.getBoardPreferences().getLightSquareBackgroundColor());
			darkSquareBackgroundColor.setValue(preferences
					.getBoardPreferences().getDarkSquareBackgroundColor());
			moveHighlightColor.setValue(preferences.getBoardPreferences()
					.getMoveHighlightColor());

			updateHoldingsState();

			chessArea.setPreferences(preferences);
			ignoreRequest = false;
		}
	}

	@Override
	public void save(Preferences preferences) {
		if (!ignoreRequest) {
			ignoreRequest = true;

			preferences.getBoardPreferences().setSet(
					(ChessSet) ((ComboBoxItem) chessSets.getSelectedItem())
							.getValue());
			preferences.getBoardPreferences().setSquareImageBackground(
					(SquareImageBackground) ((ComboBoxItem) squareBackgrounds
							.getSelectedItem()).getValue());

			preferences.getBoardPreferences()
					.setDropSquareBorder(
							((ComboBoxItem) dropSquareBorder.getSelectedItem())
									.toInt());
			preferences.getBoardPreferences().setSquareBorder(
					((ComboBoxItem) squareBorder.getSelectedItem()).toInt());

			preferences.getBoardPreferences().setLayoutClassName(
					(String) ((ComboBoxItem) layouts.getSelectedItem())
							.getValue());
			preferences.getBoardPreferences()
					.setDropPiecesLocation(
							((ComboBoxItem) holdingsLocation.getSelectedItem())
									.toInt());
			preferences.getBoardPreferences().setPieceSizeDelta(
					((ComboBoxItem) pieceSizeDelta.getSelectedItem()).toInt());

			preferences.getBoardPreferences().setSquareSelectionMode(
					((ComboBoxItem) highlightMode.getSelectedItem()).toInt());

			preferences.getBoardPreferences().setShowingCoordinates(
					isShowingCoordinates.isSelected());
			preferences.getBoardPreferences().setClockActiveTextProperties(
					(TextProperties) clockActiveTextProperties.getValue());
			preferences.getBoardPreferences().setClockInactiveTextProperties(
					(TextProperties) clockInactiveTextProperties.getValue());
			preferences.getBoardPreferences().setControlLabelTextProperties(
					(TextProperties) controlLabelTextProperties.getValue());
			preferences.getBoardPreferences().setStatusBarTextProperties(
					(TextProperties) statusBarTextProperties.getValue());
			preferences.getBoardPreferences().setDropSquareLabelTextProperties(
					(TextProperties) dropSquareLabelTextProperties.getValue());
			preferences.getBoardPreferences().setMarkTextProperties(
					(TextProperties) timeUpTextProperties.getValue());
			preferences.getBoardPreferences().setDropSquareColor(
					(Color) dropSquareColor.getValue());
			preferences.getBoardPreferences().setBackgroundControlsColor(
					(Color) backgroundControlsColor.getValue());
			preferences.getBoardPreferences().setLightSquareBackgroundColor(
					(Color) lightSquareBackgroundColor.getValue());
			preferences.getBoardPreferences().setDarkSquareBackgroundColor(
					(Color) darkSquareBackgroundColor.getValue());
			preferences.getBoardPreferences().setMoveHighlightColor(
					(Color) moveHighlightColor.getValue());
			ignoreRequest = false;
		}
	}

	private void updateHoldingsState() {
		if (layouts.getSelectedIndex() == 0) {
			holdingsLocation.setEnabled(true);
			holdingsLocation.repaint();
		} else {
			holdingsLocation.setEnabled(false);
			holdingsLocation.repaint();
		}
	}

	public void valueChanged(SelectionControl source, Object newValue) {
		if (!ignoreRequest) {
			save(preferences);
			chessArea.setPreferences(preferences);
			chessArea.repaint();
			chessArea.getLayout().layoutContainer(chessArea);
			updateHoldingsState();
			chessArea.repaint();

		}

	}

}
