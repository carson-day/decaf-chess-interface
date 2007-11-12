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
package decaf.gui.util;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import decaf.com.outboundevent.OutboundEvent;
import decaf.event.EventService;
import decaf.gui.ChessAreaControllerBase;
import decaf.gui.GUIManager;

public class ToolbarUtil {

	private static final Logger LOGGER = Logger.getLogger(ToolbarUtil.class);

	public static class ToolbarButton extends JButton {
		public ToolbarButton(Action action, String command, String tooltip) {
			super(action);
			this.command = command;
			setToolTipText(tooltip);
		}

		public String getCommand() {
			return command;
		}

		public void setCommand(String command) {
			this.command = command;
		}

		private String command;

	}

	public static List<ToolbarButton> addToolbarButtonsFromProperties(
			String bundleName, JToolBar toolbar, int seperatorWidth, int spacing) {

		List<ToolbarButton> result = new LinkedList<ToolbarButton>();
		PropertiesManager manager = PropertiesManager.getInstance();

		int currentButton = 1;
		String currentText = manager.getString(bundleName, currentButton
				+ ".text");
		String currentTooltip = manager.getString(bundleName, currentButton
				+ ".tooltip");
		String currentCommand = manager.getString(bundleName, currentButton
				+ ".command");
		String isSeperator = manager.getString(bundleName, currentButton
				+ ".isSeperator");

		if (currentText == null || currentCommand == null) {
			LOGGER.error("Error reading properties file: " + bundleName
					+ " property " + currentButton + ".text");
		}

		boolean addSpacing = false;

		while ((currentText != null && isSeperator == null)
				|| (currentText == null && isSeperator != null)) {
			if (currentText != null && currentCommand != null) {
				final String finalCommand = currentCommand;
				AbstractAction action = new AbstractAction(currentText) {
					public void actionPerformed(ActionEvent e) {
						EventService.getInstance().publish(
								new OutboundEvent(finalCommand, false));
					}
				};

				ToolbarButton button = new ToolbarButton(action,
						currentCommand, currentTooltip);

				if (addSpacing) {
					toolbar.addSeparator(new Dimension(spacing, 1));
				}
				toolbar.add(button);
				result.add(button);
				addSpacing = true;
			} else if (isSeperator != null && isSeperator.equals("true")) {
				toolbar.addSeparator(new Dimension(seperatorWidth, 1));
				addSpacing = false;
			}

			currentButton++;
			currentText = manager
					.getString(bundleName, currentButton + ".text");
			currentTooltip = manager.getString(bundleName, currentButton
					+ ".tooltip");
			currentCommand = manager.getString(bundleName, currentButton
					+ ".command");
			isSeperator = manager.getString(bundleName, currentButton
					+ ".isSeperator");

		}
		return result;
	}

	public static List<ToolbarButton> addGameToolbarButtonsFromProperties(
			final String bundleName, final JToolBar toolbar,
			final int seperatorWidth, final int spacing,
			final ChessAreaControllerBase controller) {

		List<ToolbarButton> result = new LinkedList<ToolbarButton>();
		PropertiesManager manager = PropertiesManager.getInstance();

		int currentButton = 1;
		String currentText = manager.getString(bundleName, currentButton
				+ ".text");
		String currentTooltip = manager.getString(bundleName, currentButton
				+ ".tooltip");
		String currentCommand = manager.getString(bundleName, currentButton
				+ ".command");
		String isSeperator = manager.getString(bundleName, currentButton
				+ ".isSeperator");

		if (currentText == null || currentCommand == null) {
			LOGGER.error("Error reading properties file: " + bundleName
					+ " property " + currentButton + ".text");
		}

		boolean addSpacing = false;

		while ((currentText != null && isSeperator == null)
				|| (currentText == null && isSeperator != null)) {
			if (currentText != null && currentCommand != null) {
				final String finalCommand = currentCommand;

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("adding final command: " + finalCommand);
				}
				AbstractAction action = new AbstractAction(currentText) {
					public void actionPerformed(ActionEvent e) {
						if (finalCommand.equalsIgnoreCase("$FLIP")) {
							controller.flip();
						} else if (finalCommand.equalsIgnoreCase("$ROTATE")) {
							controller.rotate();
						} else if (finalCommand
								.equalsIgnoreCase("$CLEAR_PREMOVE")) {
							controller.clearPremove();
						} else if (finalCommand.equalsIgnoreCase("$CLOSE")) {
							GUIManager.getInstance().removeICSController(
									controller);
						} else {
							String adjustedCommand = replace(finalCommand,
									"$GAME_ID", "" + controller.getGameId());

							adjustedCommand = replace(adjustedCommand,
									"$PLAYER_NAME", User.getInstance()
											.getHandle());

							EventService.getInstance().publish(
									new OutboundEvent(adjustedCommand, false));
						}
					}
				};

				ToolbarButton button = new ToolbarButton(action,
						currentCommand, currentTooltip);

				if (addSpacing) {
					toolbar.addSeparator(new Dimension(spacing, 1));
				}
				toolbar.add(button);
				result.add(button);
				addSpacing = true;
			} else if (isSeperator != null && isSeperator.equals("true")) {
				toolbar.addSeparator(new Dimension(seperatorWidth, 1));
				addSpacing = false;
			}

			currentButton++;
			currentText = manager
					.getString(bundleName, currentButton + ".text");
			currentTooltip = manager.getString(bundleName, currentButton
					+ ".tooltip");
			currentCommand = manager.getString(bundleName, currentButton
					+ ".command");
			isSeperator = manager.getString(bundleName, currentButton
					+ ".isSeperator");
		}
		return result;
	}

	private static String replace(String source, String replace,
			String replacement) {
		String result = source;
		int replaceIndex = source.indexOf(replace);
		while (replaceIndex != -1) {
			result = result.substring(0, replaceIndex)
					+ replacement
					+ result.substring(replaceIndex + replace.length(), result
							.length());
			replaceIndex = result.indexOf(replace);
		}

		return result;
	}
}
