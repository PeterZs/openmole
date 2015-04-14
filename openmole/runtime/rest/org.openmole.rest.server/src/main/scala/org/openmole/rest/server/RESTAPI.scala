package org.openmole.rest.server

import java.util.UUID
import java.util.zip.GZIPInputStream
import javax.servlet.annotation.MultipartConfig
import javax.servlet.http.HttpServletRequest

import com.ice.tar.TarInputStream
import groovy.ui.ConsoleView
import org.json4s.JsonDSL._
import org.json4s._
import org.openmole.console._
import org.openmole.core.workflow.puzzle._
import org.openmole.core.workflow.task._
import org.openmole.core.dsl._
import org.openmole.core.workspace.Workspace
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport
import org.scalatra.servlet.FileUploadSupport
import org.openmole.rest.messages._
import org.openmole.core.tools.io.TarArchiver._

import scala.io.Source
import scala.util.{ Try, Failure, Success }

case class MoleExecution(moleExecution: MoleExecution)
case class WorkDirectory(baseDirectory: File) {
  def inputDirectory = new File(baseDirectory, "inputs")
  def outputDirectory = new File(baseDirectory, "outputs")
}

@MultipartConfig //research scala multipart config
trait RESTAPI extends ScalatraServlet
    with FileUploadSupport
    with FlashMapSupport
    with JacksonJsonSupport
    with Authentication {

  protected implicit val jsonFormats: Formats = DefaultFormats.withBigDecimal
  private val logger = Log.log

  private lazy val moles = DataHandler[MoleExecutionId, MoleExecution]()

  implicit class ToJsonDecorator(x: Any) {
    def toJson = pretty(Extraction.decompose(x))
  }

  def arguments: RESTLifeCycle.Arguments

  def exceptionToHttpError(e: Throwable) = InternalServerError(Error(e).toJson)

  post("/token") {
    contentType = formats("json")

    Try(params("password")) map issueToken match {
      case Failure(_)                                      ⇒ ExpectationFailed(render("error", "No password sent with request"))
      case Success(Failure(InvalidPasswordException(msg))) ⇒ Forbidden(msg)
      case Success(Failure(e))                             ⇒ exceptionToHttpError(e)
      case Success(Success(AuthenticationToken(token, start, end))) ⇒
        Accepted(Token(token, end - start).toJson)
    }
  }

  post("/start") {
    contentType = formats("json")

    authenticated {
      (params get "script") match {
        case None ⇒ ExpectationFailed("Missing mandatory script parameter.")
        case Some(script) ⇒
          logger.info("starting the create operation")

          val directory = WorkDirectory(Workspace.newDir("restExecution"))
          directory.inputDirectory.mkdirs()
          directory.outputDirectory.mkdirs()

          def extract =
            for {
              archive ← fileParams get "inputs"
            } {
              val is = new TarInputStream(new GZIPInputStream(archive.getInputStream))
              try is.extractDirArchiveWithRelativePath(directory.inputDirectory) finally is.close
            }

          def launch: ActionResult = {
            val console = new Console(arguments.plugins)
            val repl = console.newREPL(ConsoleVariables(inputDirectory = directory.inputDirectory, outputDirectory = directory.outputDirectory))
            Try(repl.eval(script)) match {
              case Failure(e) ⇒ InternalServerError(Error(e).toJson)
              case Success(o) ⇒
                o match {
                  case puzzle: Puzzle ⇒
                    Try(puzzle.start) match {
                      case Success(ex) ⇒
                        val id = MoleExecutionId(UUID.randomUUID().toString)
                        Ok(id)
                      case Failure(error) ⇒ InternalServerError(Error(error))
                    }
                  case _ ⇒ InternalServerError(Error("The last line of the script should be a puzzle", None))
                }
            }
          }

          extract
          launch
      }
    }
  }

  def authenticated[T](success: ⇒ ActionResult)(implicit r: HttpServletRequest): ActionResult = {
    def fail = Unauthorized(render(("error", "This service requires a token")))

    Try(params("token")(r)) match {
      case Failure(_) ⇒ fail
      case Success(k) ⇒ if (checkToken(k)) success else fail
    }
  }

}