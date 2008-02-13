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

package decaf.gui.widgets;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class ChooseColorPanel extends JPanel implements ActionListener {
	
	public static final String COLOR_CHANGED = "ChooseColorPanel Color Changed";

	public ChooseColorPanel(String caption) {
		super(new GridLayout(1,2));
		
        int gridx = 0;
        int gridy = 0;
        
        colorTextField = new JTextField();
        changeColor = new JButton("Change Color");

        add(colorTextField);
        add(changeColor);
        
        colorTextField.setEditable(false);
        
        changeColor.addActionListener(this);

        setBorder(new TitledBorder(caption));
	}
	
	public ChooseColorPanel(String caption, Color color) {
		this(caption);
		setColor(color);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == changeColor) {
            Color c = JColorChooser.showDialog(this, "Choose color", changeColor.getBackground());
            if (c != null) {
            	if (!c.equals(getSelectedColor())) {
            		setColor(c);
            		firePropertyChange(ChooseColorPanel.COLOR_CHANGED , false, true);
            	}
            }
        }
	}
	
	public Color getSelectedColor() {
		return changeColor.getBackground();
	}
	
	public void setColor(Color color) {
		if (color == null) {
			color = Color.white;
		}
		String hex = Integer.toHexString(color.getRGB()).toUpperCase();
		colorTextField.setText(hex);
		changeColor.setBackground(color);
	}
	
    protected JButton changeColor;
    protected JTextField colorTextField;
}
