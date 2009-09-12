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
package decaf.gui.widgets.selectioncontrol;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import decaf.gui.widgets.Disposable;

/**
 * The base class for all selection controls. Selection controls support help
 * text. This text is added as toolTips, and if setMouseHelpListenerEnabled
 * (which it is by default), a callback will be made to all
 * SelectionControlListeners whenever the mouse enters this SelectionControl.
 * 
 * 
 */
public abstract class SelectionControl extends JPanel implements Disposable {

	private class ShowHelpMouseListener extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent e) {
			fireShowHelp();
		}
	}

	private String label;

	private String helpText;

	private Object value;

	private boolean isEnabled;

	private List<SelectionControlListener> selectionControlListeners;

	private ShowHelpMouseListener showHelpMouseListener;

	public SelectionControl() {
		selectionControlListeners = new LinkedList<SelectionControlListener>();
		showHelpMouseListener = new ShowHelpMouseListener();
		setToolTipText(helpText);
	}

	public void addSelectionControlListener(SelectionControlListener listener) {
		selectionControlListeners.add(listener);
	}

	public void dispose() {

		label = null;
		helpText = null;
		value = null;
		if (selectionControlListeners != null) {
			selectionControlListeners.clear();
		}
		showHelpMouseListener = null;
	}

	protected void fireShowHelp() {
		/*
		 * for (int i = 0; i < selectionControlListeners.size(); i++) {
		 * ((SelectionControlListener) selectionControlListeners.get(i))
		 * .showHelp(this, getHelpText()); }
		 */
	}

	protected void fireValueChanged() {
		for (int i = 0; i < selectionControlListeners.size(); i++) {
			(selectionControlListeners.get(i)).valueChanged(this, getValue());
		}
	}

	public String getHelpText() {
		return helpText;
	}

	public String getLabel() {
		return label;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean isEnabled() {
		return isEnabled;
	}

	public void removeSelectionControlListener(SelectionControlListener listener) {
		selectionControlListeners.remove(listener);
	}

	@Override
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
		this.setToolTipText(helpText);
	}

	public void setLabel(String text) {
		label = text;
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

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "value={" + getValue() + "},helpText={" + getHelpText() + "}";
	}
}