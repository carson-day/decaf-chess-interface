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

import java.awt.event.KeyEvent;
import java.util.HashMap;

import decaf.event.EventService;
import decaf.gui.GUIManager;
import decaf.messaging.outboundevent.OutboundEvent;
import decaf.resources.ResourceManagerFactory;

public class KeyMapper {
	public static final String LAST_TELL = "$LAST_TELL";

	private HashMap<Integer, String> keyCodeToCommand = new HashMap<Integer, String>();

	public KeyMapper() {
		for (int i = 0; i < 12; i++) {
			String fKey = "F" + (i + 1);
			String action = ResourceManagerFactory.getManager().getString(
					"KeyMappings", fKey);

			if (action != null) {
				int keyCode = 0;
				switch (i) {
				case 0: {
					keyCode = KeyEvent.VK_F1;
					break;
				}
				case 1: {
					keyCode = KeyEvent.VK_F2;
					break;
				}
				case 2: {
					keyCode = KeyEvent.VK_F3;
					break;
				}
				case 3: {
					keyCode = KeyEvent.VK_F4;
					break;
				}
				case 4: {
					keyCode = KeyEvent.VK_F5;
					break;
				}
				case 5: {
					keyCode = KeyEvent.VK_F6;
					break;
				}
				case 6: {
					keyCode = KeyEvent.VK_F7;
					break;
				}
				case 7: {
					keyCode = KeyEvent.VK_F8;
					break;
				}
				case 8: {
					keyCode = KeyEvent.VK_F9;
					break;
				}
				case 9: {
					keyCode = KeyEvent.VK_F10;
					break;
				}
				case 10: {
					keyCode = KeyEvent.VK_F11;
					break;
				}
				case 11: {
					keyCode = KeyEvent.VK_F12;
					break;
				}
				default: {
					throw new IllegalStateException("Could not find " + i);
				}
				}
				keyCodeToCommand.put(keyCode, action);
			}

		}
	}

	public boolean process(KeyEvent event) {
		String action = keyCodeToCommand.get(event.getKeyCode());

		if (action != null) {
			if (action.equals(LAST_TELL)) {
				GUIManager.getInstance().tellLast();
			} else {
				EventService.getInstance().publish(new OutboundEvent(action));
			}
			return true;
		}
		return false;
	}
}
