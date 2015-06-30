/*
 * Copyright (C) 2015 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openmole.core.event

import scala.concurrent.stm._

object EventAccumulator {

  def apply[T, E](t: T*)(f: PartialFunction[(T, Event[T]), E]) = {
    val accumulator = new EventAccumulator[E]()
    val listener = f andThen accumulator.accumulate
    t.foreach(_ listen listener)
    accumulator
  }

}

class EventAccumulator[E] {

  def events = _events.single
  def clear = atomic { implicit ctx ⇒
    val res = _events()
    _events() = Nil
    res
  }

  private lazy val _events = Ref(List[E]())

  def accumulate(e: E) = atomic { implicit ctx ⇒
    _events() = e :: _events()
  }

}
