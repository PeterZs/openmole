/*
 * Copyright (C) 02/10/13 Romain Reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.serializer

import java.io.File

import org.openmole.core.serializer.converter.Serialiser
import org.openmole.core.serializer.file.{ FileListing, FileConverterNotifier }
import org.openmole.core.serializer.plugin.{ PluginListing, PluginClassConverter, PluginConverter }
import org.openmole.core.serializer.structure.PluginClassAndFiles
import org.openmole.tool.file._
import org.openmole.tool.stream.NullOutputStream

import scala.collection.immutable.TreeSet

trait PluginAndFilesListing <: PluginListing with FileListing { this: Serialiser ⇒
  private var plugins: TreeSet[File] = null
  private var listedFiles = TreeSet[File]()(fileOrdering)

  xStream.registerConverter(new FileConverterNotifier(this))
  xStream.registerConverter(new PluginConverter(this, reflectionConverter))
  xStream.registerConverter(new PluginClassConverter(this))

  def pluginUsed(f: File): Unit = plugins += f
  def fileUsed(file: File) = listedFiles += file

  def list(obj: Any) = synchronized {
    plugins = TreeSet[File]()(fileOrdering)
    listedFiles = TreeSet[File]()(fileOrdering)
    xStream.toXML(obj, new NullOutputStream())
    val retPlugins = plugins
    val retFile = listedFiles
    plugins = null
    listedFiles = null
    PluginClassAndFiles(retFile.toSeq, retPlugins.toSeq)
  }

}