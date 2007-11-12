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
package decaf.gui.widgets;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import decaf.gui.Disposable;

/**
 * The base class for all selection controls. Selection controls support help
 * text. This text is added as toolTips, and if setMouseHelpListenerEnabled
 * (which it is by default), a callback will be made to all
 * SelectionControlListeners whenever the mouse enters this SelectionControl.
 * 
 * 
 */
public abstract class SelectionControl extends JPanel implements Disposable {

	private String label;

	private String helpText;

	private Object value;

	private boolean isEnabled;

	private List selectionControlListeners;

	private ShowHelpMouseListener showHelpMouseListener;

	public void dispose() {

		label = null;
		helpText = null;
		value = null;
		if (selectionControlListeners != null) {
			selectionControlListeners.clear();
		}
		showHelpMouseListener = null;
	}

	private class ShowHelpMouseListener extends MouseAdapter {
		public void mouseEntered(MouseEvent e) {
			fireShowHelp();
		}
	}

	public SelectionControl() {
		selectionControlListeners = new LinkedList();
		showHelpMouseListener = new ShowHelpMouseListener();
		setToolTipText(helpText);
	}

	/**
	 * When enabled all selectionControllisteners showHelp methods are invoked
	 * when ever the mouse enters this component.
	 */
	public void setMouseHelpListenerEnabled(boolean isEnabled) {
		if (isEnabled) {
			addMouseListener(showHelpMouseListener);
		} else {
			removeMouseListener(showHelpMouseListener);
		}
	}

	protected void fireValueChanged() {
		for (int i = 0; i < selectionControlListeners.size(); i++) {
			((SelectionControlListener) selectionControlListeners.get(i))
					.valueChanged(this, getValue());
		}
	}

	protected void fireShowHelp() {
		/*
		 * for (int i = 0; i < selectionControlListeners.size(); i++) {
		 * ((SelectionControlListener) selectionControlListeners.get(i))
		 * .showHelp(this, getHelpText()); }
		 */
	}

	public void addSelectionControlListener(SelectionControlListener listener) {
		selectionControlListeners.add(listener);
	}

	public void removeSelectionControlListener(SelectionControlListener listener) {
		selectionControlListeners.remove(listener);
	}

	public void setLabel(String text) {
		label = text;
	}

	public String getLabel() {
		return label;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
		this.setToolTipText(helpText);
	}

	public String toString() {
		return "value={" + getValue() + "},helpText={" + getHelpText() + "}";
	}
}