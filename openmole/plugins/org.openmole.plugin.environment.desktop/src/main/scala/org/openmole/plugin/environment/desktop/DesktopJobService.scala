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

package org.openmole.plugin.environment.desktop

import org.openmole.core.batch.control.AccessToken
import org.openmole.core.batch.control.JobServiceDescription
import org.openmole.core.batch.environment.BatchJob
import org.openmole.core.batch.environment.JobService
import org.openmole.core.batch.environment.Runtime
import org.openmole.core.batch.file.IURIFile

class DesktopJobService(environment: DesktopEnvironment, description: JobServiceDescription) extends JobService(environment, description){
  override protected def doSubmit(inputFile: IURIFile, outputFile: IURIFile, runtime: Runtime, token: AccessToken): BatchJob = new DesktopJob(description)
  override def test = true
}
