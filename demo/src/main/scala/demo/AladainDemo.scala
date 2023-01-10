// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import org.scalajs.dom
import react.common._

import scala.scalajs.js

import js.annotation._

@JSExportTopLevel("Main")
object AladinDemo {

  @JSExport
  def main(): Unit = {
    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }
    TargetBody().renderIntoDOM(container)

    ()
  }
}
