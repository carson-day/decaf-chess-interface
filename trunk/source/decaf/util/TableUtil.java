package decaf.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

/**
 * An example taken from http://www.jguru.com/faq/view.jsp?EID=87579 User:
 * mkovalenko * Date: Oct 16, 2001 * Time: 6:09:23 PM
 */
public class TableUtil {
	// if you want. just call Util.setRowHeader(table); // file Util.java \

	// file
	// RowHeaderRenderer.java
	/*
	 * * User: mkovalenko * Date: Oct 22, 2001 // * Time: 4:18:01 PM * Describe
	 * file
	 */
	// javax.swing.*; import javax.swing.border.Border; import
	// javax.swing.table.JTableHeader; import java.awt.*; /* * Class
	// JavaDoc */
	public static class RowHeaderRenderer extends JLabel implements
			ListCellRenderer {
		private JTable table;

		private Border selectedBorder;

		private Border normalBorder;

		private Font selectedFont;

		private Font normalFont;

		public RowHeaderRenderer(JTable table) {
			this.table = table;
			normalBorder = UIManager.getBorder("TableHeader.cellBorder");
			selectedBorder = BorderFactory.createRaisedBevelBorder();
			final JTableHeader header = table.getTableHeader();
			normalFont = header.getFont();
			selectedFont = normalFont.deriveFont(normalFont.getStyle()
					| Font.BOLD);
			setForeground(header.getForeground());
			setBackground(header.getBackground());
			setOpaque(true);
			setHorizontalAlignment(CENTER);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (table.getSelectionModel().isSelectedIndex(index)) {
				setFont(selectedFont);
				setBorder(selectedBorder);
			} else {
				setFont(normalFont);
				setBorder(normalBorder);
			}
			String label = String.valueOf(index + 1);
			setText(label);
			return this;
		}
	}

	// file TableRowHeader.java /* * User: mkovalenko * Date: Oct 22, 2001 *
	// Time: 5:17:14 PM *
	// Describe file */
	// package controls; import javax.swing.*; import
	// javax.swing.table.JTableHeader; import java.awt.*; /* * Class JavaDoc */
	public static class TableRowHeader extends JList {
		private JTable table;

		public TableRowHeader(JTable table) {
			super(new TableRowHeaderModel(table));
			this.table = table;
			setFixedCellHeight(table.getRowHeight());
			setFixedCellWidth(preferredHeaderWidth());
			setCellRenderer(new RowHeaderRenderer(table));
			setSelectionModel(table.getSelectionModel());
			setBackground(table.getBackground());
		}

		/**
		 * * Returns the bounds of the specified range of items in JList *
		 * coordinates. Returns null if index isn't valid. * *
		 * 
		 * @param index0
		 *            the index of the first JList cell in the range *
		 * @param index1
		 *            the index of the last JList cell in the range *
		 * @return the bounds of the indexed cells in pixels
		 */
		@Override
		public Rectangle getCellBounds(int index0, int index1) {
			Rectangle rect0 = table.getCellRect(index0, 0, true);
			Rectangle rect1 = table.getCellRect(index1, 0, true);
			int y, height;
			if (rect0.y < rect1.y) {
				y = rect0.y;
				height = rect1.y + rect1.height - y;
			} else {
				y = rect1.y;
				height = rect0.y + rect0.height - y;
			}
			return new Rectangle(0, y, getFixedCellWidth(), height);
		}

		public JTable getTable() {
			return table;
		}

		// assume that row header width should be big enough to display row
		// number Integer.MAX_VALUE completely
		private int preferredHeaderWidth() {
			JLabel longestRowLabel = new JLabel("65356");
			JTableHeader header = table.getTableHeader();
			longestRowLabel.setBorder(header.getBorder());// UIManager.getBorder("TableHeader.cellBorder"));
			longestRowLabel.setHorizontalAlignment(SwingConstants.CENTER);
			longestRowLabel.setFont(header.getFont());
			return longestRowLabel.getPreferredSize().width;
		}

		public void setTable(JTable table) {
			this.table = table;
		}

	} // file

	public static class TableRowHeaderModel extends AbstractListModel {
		private JTable table;

		public TableRowHeaderModel(JTable table) {
			this.table = table;
		}

		public Object getElementAt(int index) {
			return null;
		}

		public int getSize() {
			return table.getRowCount();
		}
	}

	public static boolean isRowHeaderVisible(JTable table) {
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport rowHeaderViewPort = scrollPane.getRowHeader();
				if (rowHeaderViewPort != null)
					return rowHeaderViewPort.getView() != null;
			}
		}
		return false;
	}

	/**
	 * * Creates row header for table with row number (starting with 1)
	 * displayed
	 */
	public static void removeRowHeader(JTable table) {
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.setRowHeader(null);
			}
		}
	}

	/**
	 * * Creates row header for table with row number (starting with 1)
	 * displayed
	 */
	public static void setRowHeader(JTable table) {
		Container p = table.getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.setRowHeaderView(new TableRowHeader(table));
			}
		}
	}

	/* * Class JavaDoc */
	protected TableUtil() {
	}

}