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
package decaf.event;

/**
 * To use the subscriber you must supply methods of the form inform(TypeOfEvent
 * event). Only events of the type specified will be invoked on the subscriber.
 * Super-types will not be invoked. i.e. if you have an inform(Event event) and
 * you subscribe for type Event and inform() will never be invoked since Event
 * is an interface.
 */
public interface Subscriber {
}