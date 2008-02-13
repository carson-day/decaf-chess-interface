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
package decaf.thread;

import org.apache.log4j.Logger;

import decaf.resources.ResourceManagerFactory;

/**
 * Uses a strategy pattern to handle multi-threading. In applets there are
 * issues closing down an ExecutorService in 1.6 so that is why this is used.
 */
public class ThreadManager {
	private static final Logger LOGGER = Logger.getLogger(ThreadManager.class);

	private static ThreadManagementStrategy strategy = null;

	private static void initializeManagementStrategy() {
		synchronized (ThreadManager.class) {
			if (strategy == null) {
				if (ResourceManagerFactory.getManager().getString("os", "os")
						.equals("applet")) {
					strategy = new NewThreadManagementStrategy();
					LOGGER
							.debug("Initialized NewThreadManagementStrategy for handling threads");
				} else {
					strategy = new ExecutorServiceThreadManagementStrategy();
					LOGGER
							.debug("Initialized ExecutorServiceThreadManagementStrategy for handling threads");
				}
			}
		}
	}

	public static void execute(Runnable runnable) {
		initializeManagementStrategy();
		strategy.execute(runnable);
	}
	
	public static void shutdown() {
		strategy.shutdown();
	}
}
