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
package decaf.gui.widgets.movelist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.apache.log4j.Logger;

import decaf.gui.ChessAreaController;
import decaf.gui.pref.Preferenceable;
import decaf.gui.pref.Preferences;
import decaf.moveengine.Position;
import decaf.resources.ResourceManagerFactory;
import decaf.util.TableUtil;
import decaf.util.TableUtil.TableRowHeader;

public class MoveList extends JPanel implements Preferenceable {
	private static final Logger LOGGER = Logger.getLogger(MoveList.class);

	public static final String SAVE_TO_PGN = "Save To PGN";

	private MoveListModel moveListModel;

	private List<MoveListListener> listeners = new LinkedList<MoveListListener>();

	private JScrollPane scrollPane;

	private JPanel buttonPanel;

	private JCheckBox isRealtimeUpdate;

	private JButton firstButton;

	private JButton prevButton;

	private JButton nextButton;

	private JButton lastButton;

	private JButton saveToPGN;

	private Preferences preferences;

	private JTable table;

	// Ugly but its easier to keep two models so the updates fire correctly.
	private DefaultTableModel tableModel;

	private MoveList thisList = this;

	private boolean ignoreSelectionChange = false;

	private int selectedRow = -1;

	private int selectedColumn = -1;

	private class UneditableJTable extends JTable {
		public UneditableJTable(TableModel tableModel,
				TableColumnModel tableColumnModel) {
			super(tableModel, tableColumnModel);
		}

		public boolean isCellEditable(int rowIndex, int vColIndex) {
			return false;
		}

		@Override
		public void changeSelection(int row, int column, boolean arg2,
				boolean arg3) {
			super.changeSelection(row, column, arg2, arg3);
			int halfMoveIndex = rowColmunToHalfMoveIndex(row, column);
			if (!ignoreSelectionChange
					&& moveListModel.getSize() > halfMoveIndex
					&& moveListModel.getMove(halfMoveIndex).getPosition() != null) {
				if (column == 0) {
					for (MoveListListener listener : listeners) {
						listener.moveClicked(thisList, halfMoveIndex);
					}
				} else if (column == 1) {
					String value = (String) tableModel.getValueAt(row, column);
					if (value != null && !value.equals("")) {
						for (MoveListListener listener : listeners) {
							listener.moveClicked(thisList, halfMoveIndex);
						}
					}
				}
			}
			selectedRow = row;
			selectedColumn = column;
		}
	}

	public MoveList() {
		initGui();
	}

	public void dispose() {
		removeAll();
		table.removeAll();
		listeners.clear();
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}
	}

	private void initGui() {
		tableModel = new DefaultTableModel(0, 2);
		TableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.addColumn(new TableColumn(0, 90));
		columnModel.addColumn(new TableColumn(1, 90));
		table = new UneditableJTable(tableModel, columnModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setDragEnabled(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					public void valueChanged(ListSelectionEvent arg0) {

					}
				});
		scrollPane = new JScrollPane(table);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		isRealtimeUpdate = new JCheckBox("Realtime Update");
		isRealtimeUpdate.setSelected(true);
		isRealtimeUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (MoveListListener listener : listeners) {
					listener.realtimeUpdateChanged(thisList, isRealtimeUpdate
							.isSelected());
				}
			}
		});

		TableUtil.setRowHeader(table);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(firstButton = new JButton(new AbstractAction("",
				new ImageIcon(ResourceManagerFactory.getManager().getImage(
						"first.gif"))) {
			public void actionPerformed(ActionEvent e) {
				gotoFirst();
			}
		}));
		buttonPanel.add(prevButton = new JButton(new AbstractAction("",
				new ImageIcon(ResourceManagerFactory.getManager().getImage(
						"prev.gif"))) {
			public void actionPerformed(ActionEvent e) {
				gotoPrevious();
			}
		}));
		buttonPanel.add(nextButton = new JButton(new AbstractAction("",
				new ImageIcon(ResourceManagerFactory.getManager().getImage(
						"next.gif"))) {
			public void actionPerformed(ActionEvent e) {
				gotoNext();
			}
		}));

		buttonPanel.add(lastButton = new JButton(new AbstractAction("",
				new ImageIcon(ResourceManagerFactory.getManager().getImage(
						"last.gif"))) {
			public void actionPerformed(ActionEvent e) {
				gotoLast();
			}
		}));

		saveToPGN = new JButton(new AbstractAction("Save as PGN") {
			public void actionPerformed(ActionEvent e) {
				saveMoveListAsPgn();
			}
		});
		saveToPGN.setEnabled(false);

		JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
		bottomPanel.add(isRealtimeUpdate);
		bottomPanel.add(saveToPGN);

		setLayout(new BorderLayout());
		add(buttonPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);

		setBorder(new EmptyBorder(new Insets(2, 2, 2, 2)));
	}

	public void gotoFirst() {
		if (moveListModel.getSize() > 0) {
			table.changeSelection(0, 0, false, false);
		}
	}

	public void gotoLast() {
		if (moveListModel.getSize() > 0) {
			int[] rowColumn = halfMoveToRowColumn(moveListModel.getSize());
			table.changeSelection(rowColumn[0], rowColumn[1], false, false);
		}
	}

	public void gotoNext() {
		if (moveListModel.getSize() > 0 && selectedRow != -1
				&& selectedColumn != -1) {
			int[] maxRowColumn = halfMoveToRowColumn(moveListModel.getSize());

			if (selectedColumn == 0) {
				table.changeSelection(selectedRow, 1, false, false);
			} else if (selectedRow + 1 <= maxRowColumn[0]) {
				table.changeSelection(selectedRow + 1, 0, false, false);
			}
		}
	}

	public void gotoPrevious() {
		if (moveListModel.getSize() > 0 && selectedRow != -1
				&& selectedColumn != -1) {
			if (selectedColumn == 1) {
				table.changeSelection(selectedRow, 0, false, false);
			} else if (selectedRow - 1 >= 0) {
				table.changeSelection(selectedRow - 1, 1, false, false);
			}
		}

	}

	public void setRealtimeUpdateEnabled(boolean isEnabled) {
		// isRealtimeUpdate.setEnabled(isEnabled);
	}

	public boolean isRealtimeUpdate() {
		return isRealtimeUpdate.isSelected();
	}

	public void setRealtimeUpdate(boolean value) {
		isRealtimeUpdate.setSelected(value);
	}

	public MoveListModelMove getMove(int halfMoveIndex) {
		return moveListModel.getMove(halfMoveIndex);
	}

	public long getWhiteElapsedTime(int halfMoveIndex, int inc) {
		return moveListModel.getWhiteElapsedTime(halfMoveIndex, inc);
	}

	public long getBlackElapsedTime(int halfMoveIndex, int inc) {
		return moveListModel.getBlackElapsedTime(halfMoveIndex, inc);
	}

	public int getHalfMoveWithElapsedTime(long elapsedTime, int inc) {
		int result = 0;
		long accumulatedTime = 0;
		int i = 0;
		for (i = 0; result == 0 && i < moveListModel.getSize(); i++) {
			accumulatedTime += moveListModel.getMove(i).getTimeTakenMillis()
					- inc * 1000;
			if (accumulatedTime > elapsedTime) {
				result = i;
			}
		}

		if (result == 0 && i == moveListModel.getSize()) {
			result = moveListModel.getSize() - 1;
		}

		return result;
	}

	public void appendMove(String algebraicDescription, long timeMillis,
			Position position) {
		// Need to cache these if waiting on moves.
		// All kinds of bugs.
		final MoveListModelMove move = new MoveListModelMove(
				algebraicDescription, position, timeMillis);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				synchronized (moveListModel) {
					moveListModel.append(move);

					int moveListSize = moveListModel.getSize();

					if (moveListSize % 2 != 0) {
						appendNewRow(moveListModel.getMove(moveListSize - 1)
								.getAlgebraicDescription());
					} else {
						updateLastRow(moveListModel.getMove(moveListSize - 1)
								.getAlgebraicDescription());
					}

					if (isRealtimeUpdate.isSelected()) {
						setScrollBarToMax();
						selectLastMove();
					}
				}
			}
		});
	}

	/**
	 * Returns the 0 based half move index.
	 */
	public int getSelectedHalfMove() {
		return rowColmunToHalfMoveIndex(selectedRow, selectedColumn);
	}

	public int getHalfMoves() {
		return moveListModel.getSize();
	}

	public MoveListModelMove getLastMove() {
		if (moveListModel.getSize() != 0) {
			return moveListModel.getMove(moveListModel.getSize() - 1);
		} else {
			return null;
		}
	}

	public boolean isLastMoveWhite() {
		int moveListSize = moveListModel.getSize();
		return (moveListSize % 2 != 0);
	}

	public void setGameEnd(String result) {
		appendMove(result, 0, null);
		saveToPGN.setEnabled(true);
	}

	public void setMoveList(MoveListModel newMoveList) {
		this.moveListModel = newMoveList;
		selectedRow = -1;
		selectedColumn = -1;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				synchronized (moveListModel) {
					clearTableModel();

					for (int i = 0; i < moveListModel.getSize(); i++) {
						if (i % 2 == 0) {
							appendNewRow(moveListModel.getMove(i)
									.getAlgebraicDescription());

						} else {
							updateLastRow(moveListModel.getMove(i)
									.getAlgebraicDescription());
						}
					}
					setScrollBarToMax();
					selectLastMove();
					scrollPane.getRowHeader().getView().invalidate();
					validate();
				}
			}
		});
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		table.setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		table.setFont(preferences.getBoardPreferences()
				.getControlLabelTextProperties().getFont());
		table.setForeground(preferences.getBoardPreferences()
				.getControlLabelTextProperties().getForeground());
		setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		isRealtimeUpdate.setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		isRealtimeUpdate.setFont(preferences.getBoardPreferences()
				.getControlLabelTextProperties().getFont());
		isRealtimeUpdate.setForeground(preferences.getBoardPreferences()
				.getControlLabelTextProperties().getForeground());
		scrollPane.getViewport().setBackground(
				preferences.getBoardPreferences().getBackgroundControlsColor());
		buttonPanel.setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		nextButton.setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		prevButton.setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		firstButton.setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());
		lastButton.setBackground(preferences.getBoardPreferences()
				.getBackgroundControlsColor());

		if (scrollPane.getRowHeader().getView() instanceof TableUtil.TableRowHeader) {
			TableRowHeader header = (TableUtil.TableRowHeader) scrollPane
					.getRowHeader().getView();
			header.setBackground(preferences.getBoardPreferences()
					.getBackgroundControlsColor());
			header.setFont(preferences.getBoardPreferences()
					.getControlLabelTextProperties().getFont());
			header.setForeground(preferences.getBoardPreferences()
					.getControlLabelTextProperties().getForeground());

			header.setCellRenderer(new TableUtil.RowHeaderRenderer(header
					.getTable()));
		}
	}

	public void selectLastMove() {
		int[] rowColumn = halfMoveToRowColumn(moveListModel.getSize());
		synchronized (this) {
			ignoreSelectionChange = true;
			table.changeSelection(rowColumn[0], rowColumn[1], false, false);
			ignoreSelectionChange = false;
		}
	}

	public void selectMove(int halfMoveIndex) {
		int[] rowColumn = halfMoveToRowColumn(halfMoveIndex);
		table.changeSelection(rowColumn[0], rowColumn[1], false, false);
	}

	public void addMoveListListener(MoveListListener listener) {
		listeners.add(listener);
	}

	public void removeMoveListListener(MoveListListener listener) {
		listeners.remove(listener);
	}

	public void removeAllMoveListListeners() {
		listeners.clear();
	}

	public MoveListModel getMoveList() {
		return moveListModel;
	}

	public void clear() {
		synchronized (this) {
			ignoreSelectionChange = true;
			moveListModel = new MoveListModel(new Position());
			clearTableModel();
			invalidate();
			selectedRow = -1;
			selectedColumn = -1;
			ignoreSelectionChange = false;

		}
	}

	private void saveMoveListAsPgn() {
		firePropertyChange(MoveList.SAVE_TO_PGN, false, true);
	}

	private void clearTableModel() {
		while (tableModel.getRowCount() > 0) {
			tableModel.removeRow(0);
		}

	}

	public void setScrollBarToMax() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
				scrollBar.setValue(scrollBar.getMaximum());
			}
		});
	}

	private void appendNewRow(String whitesAlgebraicDescription) {
		tableModel.addRow(new String[] { whitesAlgebraicDescription, "" });

		if (scrollPane.getRowHeader().getView() instanceof TableUtil.TableRowHeader) {
			TableRowHeader header = (TableUtil.TableRowHeader) scrollPane
					.getRowHeader().getView();
			header.invalidate();
			header.validate();
		}
	}

	private void updateLastRow(String blacksAlgebraicDescription) {
		int row = tableModel.getRowCount() - 1;
		if (row == -1) {
			// Figure out how to handle this case.
			throw new IllegalStateException("Cant update row -1 with "
					+ blacksAlgebraicDescription);
		}

		tableModel.setValueAt(blacksAlgebraicDescription, row, 1);
	}

	private int rowColmunToHalfMoveIndex(int row, int column) {
		return row * 2 + (column > 0 ? 1 : 0);
	}

	private int[] halfMoveToRowColumn(int halfMoveIndex) {
		int row = halfMoveIndex / 2;
		int column = halfMoveIndex % 2 == 0 ? 1 : 0;

		if (column == 1 && row > 0) {
			row -= 1;
		}
		return new int[] { row, column };

	}

	public void setUpPgnListener(PropertyChangeListener listener) {
		saveToPGN.setEnabled(false);
		removePropertyChangeListener(SAVE_TO_PGN, listener);
		addPropertyChangeListener(SAVE_TO_PGN, listener);
	}
}
