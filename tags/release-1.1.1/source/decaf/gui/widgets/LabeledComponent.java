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

import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabeledComponent extends JPanel {
	public LabeledComponent(String label, String toolTip, JComponent component) {
		setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel jlabel = new JLabel(label);
		add(jlabel);
		add(component);
		jlabel.setToolTipText(toolTip);
		component.setToolTipText(toolTip);
		setAlignmentY(JComponent.TOP_ALIGNMENT);
	}

	public LabeledComponent(String label, JComponent component) {
		this(label, null, component);
	}
}
