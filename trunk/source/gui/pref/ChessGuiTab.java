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
package decaf.gui.pref;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import decaf.gui.util.BorderUtil;
import decaf.gui.util.TextProperties;
import decaf.gui.widgets.ChessArea;
import decaf.gui.widgets.ChessSet;
import decaf.gui.widgets.ColorSelectionControl;
import decaf.gui.widgets.ComboBoxItem;
import decaf.gui.widgets.ComboBoxItems;
import decaf.gui.widgets.ImageChessSet;
import decaf.gui.widgets.LabeledComponent;
import decaf.gui.widgets.SelectionControl;
import decaf.gui.widgets.SelectionControlListener;
import decaf.gui.widgets.TextPropertiesSelectionControl;
import decaf.moveengine.Piece;
import decaf.moveengine.Position;

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

	private static final ComboBoxItems HOLDINGS_LAYOUT = new ComboBoxItems(
			new ComboBoxItem[] {
					new ComboBoxItem("Left", new Integer(
							BoardPreferences.DROP_PIECES_ON_RIGHT)),
					new ComboBoxItem("Top/Bottom", new Integer(
							BoardPreferences.DROP_PIECES_ON_TOP_BOTTOM)), 
					new ComboBoxItem("Left/Right", new Integer(
								BoardPreferences.DROP_PIECES_ON_LEFT_RIGHT))});

	private static ComboBoxItems CHESS_SETS = null;

	static {
		String[] sets = ImageChessSet.getSetNames();
		ComboBoxItem[] items = new ComboBoxItem[sets.length];

		for (int i = 0; i < sets.length; i++) {
			items[i] = new ComboBoxItem(sets[i], new ImageChessSet(sets[i]));
		}
		CHESS_SETS = new ComboBoxItems(items);
	}

	private JComboBox chessSets = new JComboBox(CHESS_SETS.getItems());

	private JComboBox dropSquareBorder = new JComboBox(BORDERS.getItems());

	private JComboBox squareBorder = new JComboBox(BORDERS.getItems());

	private JComboBox chessBoardBorder = new JComboBox(BORDERS.getItems());

	private JComboBox holdingsLocation = new JComboBox(HOLDINGS_LAYOUT
			.getItems());

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

	private ColorSelectionControl dropSquareColor = new ColorSelectionControl(
			"Holdings Square Color Background", null, null);

	private ColorSelectionControl backgroundControlsColor = new ColorSelectionControl(
			"Background", null, null);

	private ColorSelectionControl lightSquareBackgroundColor = new ColorSelectionControl(
			"Light Board Square", null, null);

	private ColorSelectionControl darkSquareBackgroundColor = new ColorSelectionControl(
			"Dark Board Square", null, null);

	private ColorSelectionControl moveHighlightColor = new ColorSelectionControl(
			"Move Highlight", null, null);

	private ChessSet set = new ImageChessSet("BOOK");

	private ChessArea chessArea = null;

	private Preferences preferences;

	private boolean ignoreRequest = false;

	public ChessGuiTab(Preferences preferences) {
		super("Chess Gui");
		this.preferences = preferences;
		JPanel boxPanel = new JPanel();
		boxPanel.setLayout(new BoxLayout(boxPanel, BoxLayout.Y_AXIS));
		boxPanel.add(new LabeledComponent("Set", chessSets));
		boxPanel
				.add(new LabeledComponent("Holdings Location", holdingsLocation));
		boxPanel.add(new LabeledComponent("Holdings Square Border",
				dropSquareBorder));
		boxPanel.add(new LabeledComponent("Chess Square Border", squareBorder));
		boxPanel.add(new LabeledComponent("Chess Board Border",
				chessBoardBorder));

		boxPanel.add(backgroundControlsColor);
		boxPanel.add(lightSquareBackgroundColor);
		boxPanel.add(darkSquareBackgroundColor);
		boxPanel.add(dropSquareColor);
		boxPanel.add(moveHighlightColor);

		boxPanel.add(clockActiveTextProperties);
		boxPanel.add(clockInactiveTextProperties);
		boxPanel.add(controlLabelTextProperties);
		boxPanel.add(statusBarTextProperties);
		boxPanel.add(dropSquareLabelTextProperties);

		JPanel chessBoardPanel = new JPanel();
		chessArea = new ChessArea();
		chessArea.setPreferences(preferences);
		chessArea.setup("empty", "Player1", "1456", "Player2", "2600", true,
				true, 300, 0, new Position());

		chessArea.setPreferredSize(new Dimension(600, 400));

		chessArea.setWhiteDropPieces(new int[] { Piece.WHITE_KNIGHT,
				Piece.WHITE_KNIGHT });
		chessArea.setBlackDropPieces(new int[] { Piece.BLACK_PAWN,
				Piece.BLACK_PAWN, Piece.BLACK_ROOK });
		chessArea.setMinimumSize(new Dimension(600, 400));
		chessArea.setStatusText("Sample status text");

		chessBoardPanel.add(chessArea);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(boxPanel);
		splitPane.setRightComponent(chessBoardPanel);
		splitPane.setResizeWeight(.3F);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);

		chessSets.addItemListener(this);
		dropSquareBorder.addItemListener(this);
		squareBorder.addItemListener(this);
		holdingsLocation.addItemListener(this);
		chessBoardBorder.addItemListener(this);
		chessBoardBorder.addItemListener(this);

		clockActiveTextProperties.addSelectionControlListener(this);
		clockInactiveTextProperties.addSelectionControlListener(this);
		statusBarTextProperties.addSelectionControlListener(this);

		dropSquareColor.addSelectionControlListener(this);
		backgroundControlsColor.addSelectionControlListener(this);
		lightSquareBackgroundColor.addSelectionControlListener(this);
		darkSquareBackgroundColor.addSelectionControlListener(this);
		moveHighlightColor.addSelectionControlListener(this);

	}

	public void load(Preferences preferences) {
		if (!ignoreRequest) {
			ignoreRequest = true;
			this.preferences = preferences;
			chessSets.setSelectedIndex(CHESS_SETS.getIndexWithValue(preferences
					.getBoardPreferences().getSet()));
			dropSquareBorder.setSelectedIndex(BORDERS
					.getIndexWithValue(preferences.getBoardPreferences()
							.getDropSquareBorder()));
			squareBorder.setSelectedIndex(BORDERS.getIndexWithValue(preferences
					.getBoardPreferences().getSquareBorder()));
			chessBoardBorder.setSelectedIndex(BORDERS
					.getIndexWithValue(preferences.getBoardPreferences()
							.getChessBoardBorder()));

			holdingsLocation.setSelectedIndex(HOLDINGS_LAYOUT
					.getIndexWithValue(preferences.getBoardPreferences()
							.dropPiecesLocation()));

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

			chessArea.setPreferences(preferences);
			ignoreRequest = false;
		}
	}

	public void save(Preferences preferences) {
		if (!ignoreRequest) {
			ignoreRequest = true;

			preferences.getBoardPreferences().setSet(
					(ChessSet) ((ComboBoxItem) chessSets.getSelectedItem())
							.getValue());
			preferences.getBoardPreferences()
					.setDropSquareBorder(
							((ComboBoxItem) dropSquareBorder.getSelectedItem())
									.toInt());
			preferences.getBoardPreferences().setSquareBorder(
					((ComboBoxItem) squareBorder.getSelectedItem()).toInt());
			preferences.getBoardPreferences()
					.setChessBoardBorder(
							((ComboBoxItem) chessBoardBorder.getSelectedItem())
									.toInt());
			preferences.getBoardPreferences()
					.dropPiecesLocation(
							((ComboBoxItem) holdingsLocation.getSelectedItem())
									.toInt());
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

	public void valueChanged(SelectionControl source, Object newValue) {
		if (!ignoreRequest) {
			save(preferences);
			chessArea.setPreferences(preferences);
			chessArea.repaint();
		}

	}

	public void itemStateChanged(ItemEvent arg0) {
		if (!ignoreRequest) {
			save(preferences);
			chessArea.setPreferences(preferences);
			chessArea.getBoard().selectSquare(new int[] { 0, 0 });
			chessArea.repaint();
		}
	}

}
