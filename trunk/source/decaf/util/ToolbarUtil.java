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
package decaf.util;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import decaf.event.EventService;
import decaf.gui.BugChessAreaController;
import decaf.gui.ChessAreaController;
import decaf.gui.ChessAreaControllerBase;
import decaf.gui.GUIManager;
import decaf.gui.GameNotificationListener;
import decaf.gui.User;
import decaf.gui.widgets.ChessArea;
import decaf.messaging.ics.nongameparser.ParserUtil;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.resources.ResourceManager;
import decaf.resources.ResourceManagerFactory;

public class ToolbarUtil {

	public static class ToolbarButton extends JButton {
		private String command;

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

	}

	private static final Logger LOGGER = Logger.getLogger(ToolbarUtil.class);

	public static List<ToolbarButton> addGameToolbarButtonsFromProperties(
			final String bundleName, final JToolBar toolbar,
			final int seperatorWidth, final int spacing,
			final ChessAreaControllerBase controller) {

		List<ToolbarButton> result = new LinkedList<ToolbarButton>();
		ResourceManager manager = ResourceManagerFactory.getManager();

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

				final AbstractAction action = new AbstractAction(currentText) {
					public void actionPerformed(ActionEvent e) {
						if (finalCommand.equalsIgnoreCase("$FLIP")) {
							controller.flip();
						} else if (finalCommand.equalsIgnoreCase("$ROTATE")) {
							controller.rotate();
						} else if (finalCommand
								.equalsIgnoreCase("$CLEAR_PREMOVE")) {
							controller.clearPremove();
						} else if (finalCommand.equalsIgnoreCase("$CLOSE")) {
							GUIManager.getInstance().removeController(
									controller);
						} else if (finalCommand
								.equalsIgnoreCase("$MATCH_WINNER")) {
							if (controller.isActive()) {
								final GameNotificationListener listener = new GameNotificationListener() {

									private int clicks = 0;

									public void bugGameEnded(
											BugChessAreaController controller) {
										fire(controller);

									}

									public void bugGameStarted(
											BugChessAreaController controller) {
										// TODO Auto-generated method stub

									}

									public void fire(
											ChessAreaControllerBase controller) {
										if (clicks > 0) {
											return;
										}

										try {
											// sleep 100 so the controller can
											// update with the games result.
											Thread.sleep(100);
										} catch (InterruptedException ie) {
										}

										String playerName = null;
										String playerName2 = null;
										int control = controller
												.getInitialTimeSecs() / 60;
										int inc = controller
												.getInitialIncSecs();

										ChessArea chessArea = controller
												.getChessArea();

										if (controller.getGameEndState() == ChessAreaControllerBase.WHITE_WON) {
											playerName = ParserUtil
													.removeTitles(chessArea
															.getWhiteName());

											if (controller.isBughouse()) {
												playerName2 = ParserUtil
														.removeTitles(controller
																.getPartnersChessArea()
																.getBlackName());
											}
										} else if (controller.getGameEndState() == ChessAreaControllerBase.BLACK_WON) {
											playerName = ParserUtil
													.removeTitles(chessArea
															.getBlackName());
											if (controller.isBughouse()) {
												playerName2 = ParserUtil
														.removeTitles(controller
																.getPartnersChessArea()
																.getWhiteName());
											}

										} else if (controller.getGameEndState() == ChessAreaControllerBase.DRAW) {
											playerName = ParserUtil
													.removeTitles(chessArea
															.getBlackName());
											if (controller.isBughouse()) {
												playerName2 = ParserUtil
														.removeTitles(controller
																.getPartnersChessArea()
																.getWhiteName());
											}

										} else {
											playerName = ParserUtil
													.removeTitles(chessArea
															.getWhiteName());

											if (controller.isBughouse()) {
												playerName2 = ParserUtil
														.removeTitles(controller
																.getPartnersChessArea()
																.getBlackName());
											}
										}

										String matchString = "match "
												+ playerName + " " + control
												+ " " + inc;

										if (controller.isBughouse()) {
											matchString += " bughouse";
										} else if (controller.isDroppable()) {
											matchString += " zh";
										}

										EventService.getInstance().publish(
												new OutboundEvent(matchString));

										if (controller.isBughouse()) {
											String matchString2 = "match "
													+ playerName2 + " "
													+ control + " " + inc
													+ " bughouse";
											EventService.getInstance().publish(
													new OutboundEvent(
															matchString2));

										}
										GUIManager
												.getInstance()
												.removeGameNotificationListener(
														this);

										clicks++;
									}

									public void gameEnded(
											ChessAreaController controller) {
										fire(controller);
									}

									public void gameStarted(
											ChessAreaController controller) {
										// TODO Auto-generated method stub

									}
								};

								GUIManager.getInstance()
										.addGameNotificationListener(listener);

							}
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

	public static List<ToolbarButton> addToolbarButtonsFromProperties(
			String bundleName, JToolBar toolbar, int seperatorWidth, int spacing) {

		List<ToolbarButton> result = new LinkedList<ToolbarButton>();
		ResourceManager manager = ResourceManagerFactory.getManager();

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
