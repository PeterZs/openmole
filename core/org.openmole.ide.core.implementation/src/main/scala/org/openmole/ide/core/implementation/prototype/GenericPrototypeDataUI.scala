/*
 * Copyright (C) 2011 <mathieu.Mathieu Leclaire at openmole.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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

package org.openmole.ide.core.implementation.prototype

import org.openmole.ide.core.model.data.IPrototypeDataUI
import org.openmole.core.implementation.data._
import org.openmole.core.model.data._
import scala.reflect.runtime.universe._
import org.openmole.misc.exception.UserBadDataError
import org.openmole.misc.tools.obj.ClassUtils

object GenericPrototypeDataUI {

  def base = List(GenericPrototypeDataUI[java.lang.Integer],
    GenericPrototypeDataUI[java.lang.Double],
    GenericPrototypeDataUI[String],
    GenericPrototypeDataUI[java.io.File],
    GenericPrototypeDataUI[java.math.BigInteger],
    GenericPrototypeDataUI[java.math.BigDecimal])

  def apply[T](n: String = "", d: Int = 0)(implicit t: Manifest[T]) = new GenericPrototypeDataUI[T](n, d, t)

  def apply[T](implicit t: Manifest[T]): GenericPrototypeDataUI[T] = apply("", 0)
}

class GenericPrototypeDataUI[T](val name: String,
                                val dim: Int,
                                val protoType: Manifest[T]) extends IPrototypeDataUI[T] {

  def newInstance(n: String, d: Int) = GenericPrototypeDataUI(n, d)(protoType)

  override def toString = canonicalClassName(typeClassString)

  def typeClassString = protoType.toString

  def coreClass = classOf[Prototype[T]]

  def coreObject = Prototype[T](name)(protoType).toArray(dim).asInstanceOf[Prototype[T]]

  def fatImagePath = "img/" + canonicalClassName(protoType.toString).toLowerCase + "_fat.png"

  def canonicalClassName(c: String) = c.substring(c.lastIndexOf('.') + 1, c.length)

  def buildPanelUI = new GenericPrototypePanelUI(this)
}
