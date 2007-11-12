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
package decaf.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import decaf.gui.util.InvalidEventTypeException;

/**
 * The Event handling mechanism for Caffeine. The design allows for publishers
 * not knowing about subscribers and vice-versa. This was modified from an
 * EventService pattern found in Java Report. EventService is thread safe and
 * multi-threaded. Event time you publish each subscriber is informed on a new
 * thread. Because of this sequential delivery is not guarenteed for the
 * tradeoff of efficency.
 */
public class EventService {
	private static final Logger LOGGER = Logger.getLogger(EventService.class);

	private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10,
			100, 60 * 5, TimeUnit.SECONDS, new LinkedBlockingQueue());

	private List<Subscription> subscriptions = new ArrayList<Subscription>(500);

	private EventService() {
	}

	public static EventService getInstance() {
		return singleton;
	}

	public void publish(Event event) {
		synchronized (this) {
			
			try
			{
				for (Subscription subscription : subscriptions) {
					if (subscription.getEventClass().equals(event.getClass())
							&& (subscription.getFilter() == null || subscription
									.getFilter().apply(event))) {
						threadPoolExecutor.execute(new Publisher(event,
								subscription));
					}
				}
			}
			catch (ConcurrentModificationException cme)
			{
				LOGGER.error(cme);
			}
		}
	}

	public void subscribe(Subscription subscription)
			throws InvalidEventTypeException {
		synchronized (this) {
			subscriptions.add(subscription);
		}

	}

	public void unsubscribe(Subscription subscription)
			throws InvalidEventTypeException {
		synchronized (this) {
			subscriptions.remove(subscription);
		}

	}

	public void unsubscribeAll() {
		synchronized (this) {
			subscriptions.clear();
		}
	}

	private class Publisher implements Runnable {
		private Event event;

		private Subscription subscription;

		public Publisher(Event e, Subscription subscription) {
			event = e;
			this.subscription = subscription;
		}

		public void run() {
			// look for an inform method of the type in the subscription.
			Method informMethod = null;
			try {
				informMethod = subscription.getSubscriber().getClass()
						.getDeclaredMethod("inform",
								new Class[] { subscription.getEventClass() });

			} catch (Exception e) {

				RuntimeException exception = new RuntimeException(
						"ERROR: Could not find method inform("
								+ subscription.getEventClass().getName()
								+ " event) " + "for subscription "
								+ subscription, e);
				LOGGER.error(exception);
				throw exception;
			}

			// invoke it
			try {
				informMethod.invoke(subscription.getSubscriber(),
						new Object[] { event });
			} catch (InvocationTargetException ite) {
				ite.getCause().printStackTrace();
				LOGGER
						.error(
								"Invocation target exception occured in EventService. This is a bug.",
								ite.getCause());
				throw new RuntimeException("Invocation Target Exception", ite
						.getCause());
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
	}

	private static EventService singleton = new EventService();
}