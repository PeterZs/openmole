package org.openmole.site

/*
 * Copyright (C) 23/06/17 // mathieu.leclaire@openmole.org
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

import tools._
import stylesheet._
import scalatags.Text.all._

object UserGuide {

  private def buildTabs(docPages: Seq[DocumentationPage], current: Page) = {
    val tabs = Tabs()

    docPages.foldLeft(tabs)((tabs, p) ⇒ {

      val isCurrent = p.location == current.location

      val withDetails = div(
        div(detailButtons)(
          for {
            d ← p.details
          } yield {
            div(paddingTop := 10)(linkButton(d.name, d.file, classIs(btn ++ btn_default)))
          }
        ),
        div(paddingTop := 10)(
          if (isCurrent) div(p.content)
          else div()
        )
      )

      tabs.add(p.name, withDetails, isCurrent, p)

    })
  }

  val firstModel = DocumentationPages.scala
  val firstMethod = DocumentationPages.calibration
  val firstEnvironment = DocumentationPages.multithread

  lazy val imgStyle = Seq(
    width := 100,
    paddingRight := 15
  )

  def addCarousel(current: Page) = {

    val currentStep = {
      if (DocumentationPages.modelPages.contains(current))
        Step(span(img(src := Resource.img.codeAnimated.file, imgStyle), "Run your own MODEL"), buildTabs(DocumentationPages.modelPages, current).render, firstModel, firstEnvironment, firstMethod)
      else if (DocumentationPages.methodPages.contains(current))
        Step(span(img(src := Resource.img.exploreMapAnimated.file, imgStyle), "Explore models with a METHOD"), buildTabs(DocumentationPages.methodPages, current).render, firstMethod, firstModel, firstEnvironment)
      else Step(span(img(src := Resource.img.scaleAnimated.file, imgStyle), "Scale on an ENVIRONMENT "), buildTabs(DocumentationPages.environmentPages, current).render, firstEnvironment, firstMethod, firstModel)
    }

    new StepCarousel(
      currentStep
    ).render

  }

}
