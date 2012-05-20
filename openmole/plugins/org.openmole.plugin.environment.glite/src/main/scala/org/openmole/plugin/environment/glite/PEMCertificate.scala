/*
 * Copyright (C) 2011 reuillon
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

package org.openmole.plugin.environment.glite

import org.ogf.saga.context.Context

class PEMCertificate(
    val cypheredPassword: String,
    val certificatePath: String,
    val keyPath: String,
    val proxyTime: String = GliteAuthentication.getTimeString) extends Certificate {

  def this(cypheredPassword: String) = this(cypheredPassword,
    System.getProperty("user.home") + "/.globus/usercert.pem",
    System.getProperty("user.home") + "/.globus/userkey.pem")

  override protected def _init(ctx: Context) = {
    ctx.setAttribute(Context.USERCERT, certificatePath)
    ctx.setAttribute(Context.USERKEY, keyPath)
  }

  override def toString = "pem certificate path = " + certificatePath + ", pem key path = " + keyPath

}
