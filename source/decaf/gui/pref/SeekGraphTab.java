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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

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

class ScalePanel extends JPanel {
	
	private JTable scaleTable;
	private JTextField startAt;
	
	private ScaleTableModel scaleTableModel;
	
	public ScalePanel(String title) {
		super(new BorderLayout());
		
		startAt = new JTextField(5);
		LabeledComponent start = new LabeledComponent("Start at: ", startAt);
		
		scaleTableModel = new ScaleTableModel();
		scaleTable = new JTable(scaleTableModel);
		
		add(start, BorderLayout.NORTH);
		add(scaleTable, BorderLayout.CENTER);
		
		setBorder(BorderFactory.createTitledBorder(title));
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
}

class ScaleTableModel implements TableModel {

	private List<ScaleData> data;
	
	public ScaleTableModel() {
		data = new ArrayList<ScaleData>();
	}
	
	public void setData(int[][] scale) {
		data.clear();
		for (int [] range : scale) {
			data.add(new ScaleData(range[0], range[1]));
		}
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
	
	public void addTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
	}

	public Class<?> getColumnClass(int columnIndex) {
		return Integer.class;
	}

	public int getRowCount() {
		return data.size();
	}

	public void removeTableModelListener(TableModelListener l) {
		// TODO Auto-generated method stub
		
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
