/*
 * Copyright (C) 2014 Romain Reuillon
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

package org.openmole.plugin.task

import org.openmole.core.model.data.Prototype
import org.openmole.misc.tools.service.OS
import org.openmole.core.implementation.builder._

package object systemexec extends external.ExternalPackage {
  case class Commands(parts: Seq[String], os: OS = OS())

  implicit def stringToCommands(s: String) = Commands(Seq(s))
  implicit def seqOfStringToCommands(s: String*) = Commands(s)
  implicit def tupleToCommands(t: (String, OS)) = Commands(Seq(t._1), t._2)
  implicit def tupleSeqToCommands(t: (Seq[String], OS)) = Commands(t._1, t._2)

  lazy val errorOnReturnCode = new {
    def :=(b: Boolean): Op[SystemExecTaskBuilder] =
      _.setErrorOnReturnValue(b)
  }

  lazy val returnValue = new {
    def :=(v: Option[Prototype[Int]]): Op[SystemExecTaskBuilder] =
      _.setReturnValue(v)
  }

  lazy val stdOut = new {
    def :=(v: Option[Prototype[String]]): Op[SystemExecTaskBuilder] =
      _.setStdOut(v)
  }

  lazy val stdErr = new {
    def :=(v: Option[Prototype[String]]): Op[SystemExecTaskBuilder] =
      _.setStdErr(v)
  }

  lazy val commands = new {
    def +=(c: Commands): Op[SystemExecTaskBuilder] = _.addCommand(c)
  }

  lazy val environmentVariable = new {
    def +=(prototype: Prototype[_], variable: Option[String] = None): Op[SystemExecTaskBuilder] =
      _.addEnvironmentVariable(prototype, variable)
  }

  lazy val workDirectory = new {
    def :=(s: Option[String]): Op[SystemExecTaskBuilder] = _.setWorkDirectory(s)
  }
}
