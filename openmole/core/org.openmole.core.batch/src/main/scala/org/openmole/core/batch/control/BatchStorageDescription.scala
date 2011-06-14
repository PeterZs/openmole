/*
 * Copyright (C) 2010 reuillon
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
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.core.batch.control

import java.net.URI
import org.openmole.core.batch.environment.Storage

class StorageDescription(description: String) extends BatchServiceDescription(description) {

  def this(uri: URI) = this(uri.getScheme + "://" + {if(uri.getHost == null) "localhost" else uri.getHost} + ":" + uri.getPort)
  def this(storage: Storage) = this(storage.URI)
  
}
