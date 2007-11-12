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

/**
 * An EventService subscription. Currently polymorphism is not supported in
 * subscriptions. Only events of the type specified will be invoked on the
 * subscriber. Supertypes will not be invoked.
 */
public class Subscription {

	private Class eventClass;

	private Filter filter;

	private Subscriber subscriber;

	public Subscription(Class eventClass, Subscriber subscriber) {
		this(eventClass, null, subscriber);
	}

	public Subscription(Class eventClass, Filter filter, Subscriber subscriber) {
		if (eventClass == null) {
			throw new IllegalArgumentException("eventClass can not be null");
		}
		if (subscriber == null) {
			throw new IllegalArgumentException("subscriber can not be null");
		}
		if (!Event.class.isAssignableFrom(eventClass)) {
			throw new IllegalArgumentException(
					"eventClass must be of type Event");
		}
		this.eventClass = eventClass;
		this.filter = filter;
		this.subscriber = subscriber;
	}

	public Filter getFilter() {
		return filter;
	}

	public Class getEventClass() {
		return eventClass;
	}

	public Subscriber getSubscriber() {
		return subscriber;
	}

	public boolean equals(Subscription subscription) {

		return subscription.eventClass.equals(eventClass)
				&& subscription.subscriber.equals(subscriber);
	}

	public String toString() {
		return "<Subscription>" + "<eventClass>" + eventClass.getName()
				+ "</eventClass>" + "<filter>" + filter + "</filter>"
				+ "<subscriber>" + subscriber + "</subscriber>"
				+ "</Subscription>";
	}
}