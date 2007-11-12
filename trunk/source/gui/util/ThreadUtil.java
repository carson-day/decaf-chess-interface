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

import java.lang.reflect.Method;

/**
 * 
 */
public class ThreadUtil {
	private static ThreadGroup threadGroup = new ThreadGroup(
			"chess.util.ThreadUtil");

	public static void invokeInThread(Object object, String methodName,
			Object[] args) {
		Class[] argTypes = null;
		if (args != null) {
			argTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				argTypes[i] = args[i].getClass();
			}
		}
		Invoker invoker = null;
		try {
			invoker = new Invoker(object, object.getClass().getMethod(
					methodName, argTypes), args);
		} catch (Throwable t) {
			throw new RuntimeException(t.toString());
		}

		Thread myThread = new Thread(threadGroup, invoker);
		myThread.start();
	}

	private static class Invoker implements Runnable {
		private Method method;

		private Object object;

		private Object[] args;

		public Invoker(Object object, Method method, Object[] args) {
			this.object = object;
			this.method = method;
			this.args = args;
		}

		public void run() {
			try {
				method.invoke(object, args);
			} catch (Throwable t) {
				throw new RuntimeException(t.toString());
			}
		}
	}
}