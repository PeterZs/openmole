/**
  * Created by Mathieu Leclaire on 23/04/18.
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
  *
  */
package org.openmole.gui.plugin.wizard.jar

import scala.concurrent.ExecutionContext.Implicits.global
import boopickle.Default._
import org.openmole.gui.ext.data._
import org.openmole.gui.ext.tool.client.{InputFilter, OMPost}
import scaladget.bootstrapnative.bsn._
import scaladget.tools._
import autowire._
import org.scalajs.dom.raw.HTMLElement
import scaladget.bootstrapnative.SelectableButtons
import scaladget.bootstrapnative.Selector.Options

import scala.concurrent.Future
import scala.scalajs.js.annotation._
import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import org.openmole.core.workspace.Workspace
import org.openmole.gui.ext.data.DataUtils._
import rx._

@JSExportTopLevel("org.openmole.gui.plugin.wizard.jar.JarWizardFactory")
class JarWizardFactory extends WizardPluginFactory {
  type WizardType = JarWizardData

  val fileType = JarArchive

  def build(safePath: SafePath, onPanelFilled: (LaunchingCommand) ⇒ Unit = (LaunchingCommand) ⇒ {}): WizardGUIPlugin = new JarWizardGUI(safePath, onPanelFilled)

  def parse(safePath: SafePath): Future[Option[LaunchingCommand]] = OMPost()[JarWizardAPI].parse(safePath).call()

  def help: String = "If your Jar sript depends on plugins, you should upload an archive (tar.gz, tgz) containing the root workspace. Then set the empeddWorkspace option to true in the oms script."

  def name: String = "Jar"
}

@JSExportTopLevel("org.openmole.gui.plugin.wizard.jar.JarWizardGUI")
class JarWizardGUI(safePath: SafePath, onMethodSelected: (LaunchingCommand) ⇒ Unit) extends WizardGUIPlugin {
  type WizardType = JarWizardData

  def factory = new JarWizardFactory

  val jarClasses: Var[Seq[FullClass]] = Var(Seq())

  lazy val embedAsPluginCheckBox: SelectableButtons = radios()(
    selectableButton("Yes", onclick = () ⇒ println("YES")),
    selectableButton("No", onclick = () ⇒ println("NO"))
  )

  val classTable: Var[Option[scaladget.bootstrapnative.Table]] = Var(None)
  val methodTable: Var[Option[scaladget.bootstrapnative.Table]] = Var(None)

  searchClassInput.nameFilter.trigger {
    classTable.now.foreach { t ⇒
      t.filter(searchClassInput.nameFilter.now)
    }
  }

  OMPost()[JarWizardAPI].jarClasses(safePath).call().foreach { jc ⇒
    val table = scaladget.bootstrapnative.Table(
      rows = jc.map { c ⇒
        // println("last " + c.name.split(".").last)
        scaladget.bootstrapnative.Row(Seq(c.name))
      }.toSeq,
      bsTableStyle = scaladget.bootstrapnative.BSTableStyle(bordered_table +++ hover_table, emptyMod))

    classTable() = Some(table)

    classTable.now.get.selected.trigger {
      classTable.now.get.selected.now.foreach { s ⇒
        OMPost()[JarWizardAPI].jarMethods(safePath, s.values.head).call().foreach { jm ⇒
          val methodMap = jm.map { m ⇒ m.expand -> m }.toMap
          methodTable() = Some(
            scaladget.bootstrapnative.Table(
              rows = jm.map { m ⇒
                scaladget.bootstrapnative.Row(Seq(m.expand))
              }.toSeq,
              bsTableStyle = scaladget.bootstrapnative.BSTableStyle(bordered_table +++ hover_table, emptyMod))
          )

          methodTable.now.get.selected.trigger {
            println("Metho trigger")
            methodTable.now.get.selected.now.map(r ⇒ methodMap(r.values.head)).map { selectedMethod ⇒
              onMethodSelected(JavaLaunchingCommand(
                selectedMethod,
                selectedMethod.args, selectedMethod.ret.map {
                  Seq(_)
                }.getOrElse(Seq()))
              )
            }
          }
        }
      }

    }
  }

  lazy val searchClassInput = InputFilter("", "Ex: mynamespace.MyClass")

  val tableCSS: ModifierSeq = Seq(
    overflow := "auto",
    height := 300,
  )

  lazy val columnCSS: ModifierSeq = Seq(
    width := "50%",
    display := "inline-block",
    padding := 15
  )

  lazy val panel: TypedTag[HTMLElement] = div(
    div(columnCSS)(
      hForm(
        div(embedAsPluginCheckBox.render)
          .render.withLabel("Embed jar as plugin ?")
      ),
      h3("Classes"),
      searchClassInput.tag,
      div(tableCSS)(
        Rx {
          classTable().map {
            _.render
          }.getOrElse(div())
        }).render
    ),
    div(columnCSS)(
      h3("Methods"),
      div(tableCSS)(
        Rx {
          methodTable().map {
            _.render
          }.getOrElse(div())
        }).render
    )
  )

  def save(
            target: SafePath,
            executableName: String,
            command: String,
            inputs: Seq[ProtoTypePair],
            outputs: Seq[ProtoTypePair],
            libraries: Option[String],
            resources: Resources) = {
    val embedAsPlugin = if (embedAsPluginCheckBox.activeIndex == 0) true else false

    val plugin: Option[String] = {
      if(embedAsPlugin) classTable.now.map{_.selected.now.map{_.values.headOption}.flatten}.flatten
      else None
    }

    OMPost()[JarWizardAPI].toTask(
      target,
      executableName,
      command,
      inputs,
      outputs,
      libraries,
      resources,
      JarWizardData(embedAsPlugin, plugin)).call()
  }
}