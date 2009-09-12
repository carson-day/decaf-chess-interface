package decaf.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.AncestorListener;
import javax.swing.event.MenuKeyListener;

public class SwingUtils {

	private static void disposeComponent(JComponent component) {
		if (component == null) {
			return;
		}
		component.removeNotify();
		FocusListener[] focusListeners = component.getFocusListeners();
		for (int i = 0; i < focusListeners.length; i++) {
			component.removeFocusListener(focusListeners[i]);
		}
		KeyListener[] keyListeners = component.getKeyListeners();
		for (int i = 0; i < keyListeners.length; i++) {
			component.removeKeyListener(keyListeners[i]);
		}
		AncestorListener[] ancestorListener = component.getAncestorListeners();
		for (int i = 0; i < ancestorListener.length; i++) {
			component.removeAncestorListener(ancestorListener[i]);
		}
		ComponentListener[] componentListeners = component
				.getComponentListeners();
		for (int i = 0; i < componentListeners.length; i++) {
			component.removeComponentListener(componentListeners[i]);
		}
		HierarchyListener[] hierarchyListeners = component
				.getHierarchyListeners();
		for (int i = 0; i < hierarchyListeners.length; i++) {
			component.removeHierarchyListener(hierarchyListeners[i]);
		}
		HierarchyBoundsListener[] hierarchyBoundsListeners = component
				.getHierarchyBoundsListeners();
		for (int i = 0; i < hierarchyBoundsListeners.length; i++) {
			component
					.removeHierarchyBoundsListener(hierarchyBoundsListeners[i]);
		}
		InputMethodListener[] inputMethodListeners = component
				.getInputMethodListeners();
		for (int i = 0; i < inputMethodListeners.length; i++) {
			component.removeInputMethodListener(inputMethodListeners[i]);
		}
		MouseListener[] mouseLisetners = component.getMouseListeners();
		for (int i = 0; i < mouseLisetners.length; i++) {
			component.removeMouseListener(mouseLisetners[i]);
		}
		MouseMotionListener[] mouseMotionLisetners = component
				.getMouseMotionListeners();
		for (int i = 0; i < mouseMotionLisetners.length; i++) {
			component.removeMouseMotionListener(mouseMotionLisetners[i]);
		}
		MouseWheelListener[] mouseWheelListeners = component
				.getMouseWheelListeners();
		for (int i = 0; i < mouseWheelListeners.length; i++) {
			component.removeMouseWheelListener(mouseWheelListeners[i]);
		}
		PropertyChangeListener[] propertyChangeListeners = component
				.getPropertyChangeListeners();
		for (int i = 0; i < propertyChangeListeners.length; i++) {
			component.removePropertyChangeListener(propertyChangeListeners[i]);
		}
		VetoableChangeListener[] vetoableChangeListener = component
				.getVetoableChangeListeners();
		for (int i = 0; i < vetoableChangeListener.length; i++) {
			component.removeVetoableChangeListener(vetoableChangeListener[i]);
		}

	}

	private static void disposeContainer(Container container) {
		if (container == null) {
			return;
		}

		if (container instanceof JComponent) {
			disposeComponent((JComponent) container);
		}
		container.removeAll();
	}

	public static void dispose(Container container) {
		disposeContainer(container);
	}

	public static void dispose(Component component) {
		if (component instanceof JComponent) {
			disposeComponent((JComponent) component);
		}
	}

	public static void dispose(JComponent component) {
		disposeComponent(component);
	}

	public static void dispose(JButton component) {
		if (component == null) {
			return;
		}
		disposeContainer(component);
		ActionListener[] actionListeners = component.getActionListeners();
		for (int i = 0; i < actionListeners.length; i++) {
			component.removeActionListener(actionListeners[i]);
		}
	}

	public static void dispose(JMenu component) {
		if (component == null) {
			return;
		}
		disposeContainer(component);
		ActionListener[] actionListeners = component.getActionListeners();
		for (int i = 0; i < actionListeners.length; i++) {
			component.removeActionListener(actionListeners[i]);
		}
		MenuKeyListener[] menuKeyListeners = component.getMenuKeyListeners();
		for (int i = 0; i < menuKeyListeners.length; i++) {
			component.removeMenuKeyListener(menuKeyListeners[i]);
		}
	}

	public static void dispose(JTable component) {
		if (component == null) {
			return;
		}
		disposeContainer(component);
	}

	public static void dispose(JTextField component) {
		if (component == null) {
			return;
		}
		disposeContainer(component);
		ActionListener[] actionListeners = component.getActionListeners();
		for (int i = 0; i < actionListeners.length; i++) {
			component.removeActionListener(actionListeners[i]);
		}
	}
}
