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

package org.openmole.core.batch.environment

import java.io.File
import java.net.URI
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

import java.util.concurrent.locks.ReentrantLock
import org.openmole.misc.exception.InternalProcessingError
import org.openmole.core.batch.control.AccessToken
import org.openmole.core.batch.file.IURIFile
import org.openmole.core.implementation.execution.Environment
import org.openmole.misc.workspace.ConfigurationLocation

import org.openmole.core.model.job.IJob
import org.openmole.misc.executorservice.ExecutorService
import org.openmole.misc.executorservice.ExecutorType
import org.openmole.misc.updater.Updater
import org.openmole.misc.workspace.Workspace
import org.openmole.misc.workspace.InteractiveConfiguration
import org.openmole.misc.eventdispatcher.Event
import org.openmole.misc.eventdispatcher.IObjectListener
import org.openmole.misc.pluginmanager.PluginManager

object BatchEnvironment {
  
  val MemorySizeForRuntime = new ConfigurationLocation("BatchEnvironment", "MemorySizeForRuntime")
    
  @InteractiveConfiguration(label = "Runtime location")
  val RuntimeLocation = new ConfigurationLocation("BatchEnvironment", "RuntimeLocation")
    
  @InteractiveConfiguration(label = "JVM location")
  val JVMLocation = new ConfigurationLocation("BatchEnvironment", "JVMLocation")

  
  val MinValueForSelectionExploration = new ConfigurationLocation("BatchEnvironment", "MinValueForSelectionExploration")

  val QualityHysteresis = new ConfigurationLocation("BatchEnvironment", "QualityHysteresis")
  val CheckInterval = new ConfigurationLocation("BatchEnvironment", "CheckInterval")
  
  val DataAllReadyPresentOnStoragePreference = new ConfigurationLocation("BatchEnvironment", "DataAllReadyPresentOnStoragePreference")
  
  Workspace += (RuntimeLocation, () => new File(new File(Workspace.location, "runtime"), "org.openmole.runtime.tar.gz").getAbsolutePath)
  Workspace += (JVMLocation, () => new File(new File(Workspace.location, "runtime"), "jvm.tar.gz").getAbsolutePath)

  Workspace += (MemorySizeForRuntime, "512")
  Workspace += (QualityHysteresis, "1000")
  Workspace += (CheckInterval, "PT2M")
  Workspace += (MinValueForSelectionExploration, "0.001")
  Workspace += (DataAllReadyPresentOnStoragePreference, "10.0")
  
  /*final val FileUpload = new Event[BatchEnvironment, IFileUpload]
  
  trait IFileUpload extends IObjectListener[BatchEnvironment] {
    override def eventOccured(obj: BatchEnvironment, args: Array[Any]) = 
      transfer(obj, 
               args(0).asInstanceOf[IJob],
               args(1).asInstanceOf[File],
               args(2).asInstanceOf[URI],
               args(3).asInstanceOf[Long])
    
    def transfer(env: BatchEnvironment, 
                 job: IJob, 
                 from: File, 
                 uri: URI,
                 bytes: Long)
  }
  
  final val FileUploadFinished = new Event[BatchEnvironment, IUploadFinished]
  
  trait IUploadFinished extends IObjectListener[BatchEnvironment] {
    override def eventOccured(obj: BatchEnvironment, args: Array[Any]) = 
      finished(obj, 
               args(0).asInstanceOf[IJob],
               args(1).asInstanceOf[File],
               args(2).asInstanceOf[Option[Throwable]])
    
    def finished(env: BatchEnvironment, job: IJob, file: File, t: Option[Throwable])
  }
  
  
  final val FileDownload = new Event[BatchEnvironment, IFileDownload]
  
  trait IFileDownload extends IObjectListener[BatchEnvironment] {
    override def eventOccured(obj: BatchEnvironment, args: Array[Any]) = 
      transfer(obj, 
               args(0).asInstanceOf[IJob],
               args(1).asInstanceOf[IURIFile],
               args(2).asInstanceOf[URI],
               args(3).asInstanceOf[Long])
    
    def transfer(env: BatchEnvironment, 
                 job: IJob, 
                 from: IURIFile,
                 to: URI,
                 bytes: Long)
  }
  
  final val FileDownloadFinished = new Event[BatchEnvironment, IDownloadFinished]
  
  trait IDownloadFinished extends IObjectListener[BatchEnvironment] {
    override def eventOccured(obj: BatchEnvironment, args: Array[Any]) = 
      finished(obj, 
               args(0).asInstanceOf[IJob],
               args(1).asInstanceOf[IURIFile],
               args(2).asInstanceOf[Option[Throwable]])
    
    def finished(env: BatchEnvironment, job: IJob, file: IURIFile, t: Option[Throwable])
  }
  */
}


abstract class BatchEnvironment(inMemorySizeForRuntime: Option[Int]) extends Environment {
  
  val jobRegistry = new ExecutionJobRegistry[BatchExecutionJob]
  
  AuthenticationRegistry.initAndRegisterIfNotAllreadyIs(authentication)
      
  val memorySizeForRuntime = inMemorySizeForRuntime match {
    case Some(mem) => mem
    case None => Workspace.preferenceAsInt(BatchEnvironment.MemorySizeForRuntime)
  }
  
  Updater.registerForUpdate(new BatchJobWatcher(this), ExecutorType.OWN)
    
  override def submit(job: IJob) = {
    val bej = new BatchExecutionJob(this, job, nextExecutionJobId)
    Updater.delay(bej, ExecutorType.UPDATE)
    jobRegistry.register(bej)
  }
  
  @transient lazy val runtime = new File(Workspace.preference(BatchEnvironment.RuntimeLocation))
  @transient lazy val jvm = new File(Workspace.preference(BatchEnvironment.JVMLocation))

  @transient lazy val jobServices = new JobServiceGroup(this, allJobServices) 
  @transient lazy val storages = new StorageGroup(this, allStorages)
  
  def allStorages: Iterable[Storage]
  def allJobServices: Iterable[JobService]
  
  def authentication: Authentication
  
  def selectAJobService: (JobService, AccessToken) = jobServices.selectAService

  def selectAStorage(usedFiles: Iterable[File]):  (Storage, AccessToken) = storages.selectAService(usedFiles)
  
  
  @transient private[environment] lazy val plugins = PluginManager.pluginsForClass(this.getClass)

}
