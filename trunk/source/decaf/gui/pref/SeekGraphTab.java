/**
 *   Decaf/Decaffeinate ICS server interface
 *   Copyright (C) 2008  Sergei Kozyrenko (kozyr82@gmail.com)
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import decaf.gui.widgets.ChooseColorPanel;
import decaf.gui.widgets.LabeledComponent;

public class SeekGraphTab extends PreferencesTab {

	private JCheckBox showUnrated;
	private JCheckBox showComputer;
	
	private ScalePanel vscale;
	private ScalePanel hscale;
	
	private ChooseColorPanel computerColor;
	private ChooseColorPanel ratedColor;
	private ChooseColorPanel unratedColor;
	private ChooseColorPanel manyColor;
	
	public SeekGraphTab() {
		super("Seek Graph");
		createComponents();
		layoutComponents();
	}
	
	protected void createComponents() {
		showUnrated = new JCheckBox("Show Unrated Seeks");
		showComputer = new JCheckBox("Show Computer Seeks");
		
		vscale = new ScalePanel("Time Scale");
		hscale = new ScalePanel("Rating Scale");
		
		computerColor = new ChooseColorPanel("Computer Color");
		ratedColor = new ChooseColorPanel("Rated Color");
		unratedColor = new ChooseColorPanel("Unrated Color");
		manyColor = new ChooseColorPanel("Multiple Color");
	}
	
	protected void layoutComponents() {
		setLayout(new BorderLayout());
		
		JPanel top = new JPanel(new GridLayout(2,1));
		top.add(showUnrated);
		top.add(showComputer);
		
		JPanel center = new JPanel(new GridLayout(1,2));
		center.add(hscale);
		center.add(vscale);
		
		JPanel bottom = new JPanel(new GridLayout(2,2));
		bottom.add(ratedColor);
		bottom.add(unratedColor);
		bottom.add(computerColor);
		bottom.add(manyColor);
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void load(Preferences preferences) {
		SeekGraphPreferences sgp = preferences.getSeekGraphPreferences();
		
		showUnrated.setSelected(sgp.isShowUnrated());
		showComputer.setSelected(sgp.isShowComputer());
		
		hscale.setData(sgp.getHscale());
		hscale.setStart(sgp.getHstart());
		
		vscale.setData(sgp.getVscale());
		vscale.setStart(sgp.getVstart());
		
		computerColor.setColor(sgp.getComputerColor());
		ratedColor.setColor(sgp.getRatedColor());
		unratedColor.setColor(sgp.getUnratedColor());
		manyColor.setColor(sgp.getManyColor());
	}

	@Override
	public void save(Preferences preferences) {
		
		SeekGraphPreferences sgp = preferences.getSeekGraphPreferences();
		
		sgp.setShowComputer(showComputer.isSelected());
		sgp.setShowUnrated(showUnrated.isSelected());
		
		sgp.setHscale(hscale.getData());
		sgp.setHstart(hscale.getStart());
		
		sgp.setVscale(vscale.getData());
		sgp.setVstart(vscale.getStart());
		
		sgp.setComputerColor(computerColor.getSelectedColor());
		sgp.setRatedColor(ratedColor.getSelectedColor());
		sgp.setUnratedColor(unratedColor.getSelectedColor());
		sgp.setManyColor(manyColor.getSelectedColor());
	}
}

class ScalePanel extends JPanel implements ActionListener, IntegerChecker, ListSelectionListener {
	
	private JTable scaleTable;
	private JTextField startAt;
	private JButton insertBefore;
	private JButton insertAfter;
	private JButton remove;
	
	private ScaleTableModel scaleTableModel;
	
	public ScalePanel(String title) {
		super(new BorderLayout());
		
		startAt = new JTextField(5);
		startAt.setDocument(new IntegerDocument(this));
		LabeledComponent start = new LabeledComponent("Start at: ", startAt);
		
		scaleTableModel = new ScaleTableModel();
		scaleTable = new JTable(scaleTableModel);
		
		JScrollPane tableScrollPane = new JScrollPane(scaleTable);
		// 1.6 ONLY! Eclipse sucks...
		// scaleTable.setFillsViewportHeight(true); 
		scaleTable.getSelectionModel().addListSelectionListener(this);
		
		insertBefore = new JButton("Insert Before");
		insertAfter = new JButton("Insert After");
		remove = new JButton("Remove");
		
		JPanel control = new JPanel();
		control.add(insertBefore);
		control.add(insertAfter);
		control.add(remove);
		
		insertBefore.addActionListener(this);
		insertAfter.addActionListener(this);
		remove.addActionListener(this);
		
		add(start, BorderLayout.NORTH);
		add(tableScrollPane, BorderLayout.CENTER);
		add(control, BorderLayout.SOUTH);
		
		setBorder(BorderFactory.createTitledBorder(title));
		
		updateControlState();
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if (source == insertBefore) {
			insertBefore();
		} else if (source == insertAfter) {
			insertAfter();
		} else if (source == remove) {
			removeSelected();
		}
	}
	
	private void removeSelected() {
		int size = scaleTableModel.size();
		if (size > 1) {
			int row = scaleTable.getSelectedRow();
			if (row != -1) {
				scaleTableModel.removeRow(row);
				size = scaleTableModel.size();
				if (row < size) {
					scaleTable.getSelectionModel().setSelectionInterval(row, row);
				} else {
					scaleTable.getSelectionModel().setSelectionInterval(size - 1, size - 1 );
				}
			}
		}	
	}

	private void insertAfter() {
		int newRowIndex = scaleTableModel.insertAfter(scaleTable.getSelectedRow());
		scaleTable.getSelectionModel().setSelectionInterval(newRowIndex, newRowIndex);
	}

	private void insertBefore() {
		int newRowIndex = scaleTableModel.insertBefore(scaleTable.getSelectedRow(), 
				Integer.parseInt(startAt.getText()));
		scaleTable.getSelectionModel().setSelectionInterval(newRowIndex, newRowIndex);
	}
	
	public void updateControlState() {
		int row = scaleTable.getSelectedRow();
		enableAllControls(row != -1);
		if (scaleTableModel.size() <= 1) {
			remove.setEnabled(false);
		}
	}
	
	public void enableAllControls(boolean value) {
		insertBefore.setEnabled(value);
		insertAfter.setEnabled(value);
		remove.setEnabled(value);
	}
	
	public void valueChanged(ListSelectionEvent e) {
		updateControlState();
	}

	public void setStart(int start) {
		startAt.setText(String.valueOf(start));
	}
	
	public int getStart() {
		return Integer.parseInt(startAt.getText());
	}

	public void setData(int [][] scale) {
		scaleTableModel.setData(scale);
	}
	
	public int [][] getData() {
		return scaleTableModel.getData();
	}
	
	static class IntegerDocument
	extends PlainDocument {

		private IntegerChecker checker;
		
		public IntegerDocument(IntegerChecker checker) {
			super();
			this.checker = checker;
		}
		
		public void insertString(int offset,
				String string, AttributeSet attributes)
		throws BadLocationException {

			if (string == null) {
				return;
			} else {
				String newValue;
				int length = getLength();
				if (length == 0) {
					newValue = string;
				} else {
					String currentContent =
						getText(0, length);
					StringBuilder currentBuffer =
						new StringBuilder(currentContent);
					currentBuffer.insert(offset, string);
					newValue = currentBuffer.toString();
				}
				try {
					int incoming = Integer.parseInt(newValue);
					if (!checker.allow(incoming)) {
						throw new NumberFormatException(incoming + " not allowed!");
					}
					super.insertString(offset, string,
							attributes);
				} catch (NumberFormatException exception) {
					
				}
			}
		}
	}



	public boolean allow(int value) {
		int tableStart = scaleTableModel.getStartValue();
		
		return (tableStart < 0) || (value < tableStart);
	}
}

interface IntegerChecker {
	
	boolean allow(int value);
	
}

class ScaleTableModel extends AbstractTableModel {

	private List<ScaleData> data;
	
	public ScaleTableModel() {
		data = new ArrayList<ScaleData>();
	}
	
	public int insertAfter(int row) {
		int newRowIndex = -1;
		
		if (row != -1) {
			ScaleData current = data.get(row);
			ScaleData next;
			if (row + 1 < data.size()) {
				next = data.get(row + 1);
			} else {
				next = new ScaleData(current.start + 200, 1);
			}
			
			ScaleData newRange = new ScaleData( 
					current.start+ (next.start-current.start)/2,
					1);
			newRowIndex = row + 1;
			data.add(newRowIndex, newRange);
			fireTableRowsInserted(row, newRowIndex);
		}
		
		return newRowIndex;
	}
	
	public int insertBefore(int row, int start) {
		int newRowIndex = -1;
		
		if (row != -1) {
			ScaleData current = data.get(row);
			ScaleData prev;
			if (row - 1 >= 0) {
				prev = data.get(row - 1);
			} else {
				prev = new ScaleData(start, 1);
			}
			
			ScaleData newRange = new ScaleData( 
					prev.start + (current.start-prev.start)/2,
					1);
			newRowIndex = row;
			data.add(newRowIndex, newRange);
			fireTableRowsInserted(newRowIndex, newRowIndex+1);
		}
		
		return newRowIndex;
	}
	
	public void removeRow(int row) {
		if (row > -1 && row < data.size()) {
			data.remove(row);
			fireTableRowsDeleted(row, row);
		}
	}

	public void setData(int[][] scale) {
		data.clear();
		for (int [] range : scale) {
			data.add(new ScaleData(range[0], range[1]));
		}
		fireTableDataChanged();
	}
	
	public int [][] getData() {
		int [][] result = new int[data.size()][2];
		
		for (int i = 0; i < data.size(); i++) {
			ScaleData range = data.get(i);
			result[i][0] = range.start;
			result[i][1] = range.weight;
		}
		
		return result;
	}
	
	public int size() {
		return data.size();
	}
	
	public int getStartValue() {
		if (data.size() > 0) {
			ScaleData range = data.get(0);
			return range.start;
		} else {
			return -1;
		}
	}
	
	public int getColumnCount() {
		return 2;
	}
	
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	public Object getValueAt(int row, int column) {
		
		ScaleData range = data.get(row);
		if (column == 0) {
			return range.start;
		} else if (column == 1) {
			return range.weight;
		} else {
			return "";
		}
	}
	
	public void setValueAt(Object value, int row, int column) {
		ScaleData range = data.get(row);
		if (column == 0) {
			range.start = (Integer) value;
		} else if (column == 1) {
			range.weight = (Integer) value;
		}
	}
	
	public String getColumnName(int column) {
		String name;
		
		if (column == 0) {
			name = "Line At"; 
		} else if (column == 1) {
			name = "Weight";
		} else {
			name = "Unknown";
		}
		
		return name;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return Integer.class;
	}

	public int getRowCount() {
		return data.size();
	}
	
	class ScaleData {
		int start;
		int weight;
		
		public ScaleData(int start, int weight) {
			super();
			this.start = start;
			this.weight = weight;
		}
	}

	
}