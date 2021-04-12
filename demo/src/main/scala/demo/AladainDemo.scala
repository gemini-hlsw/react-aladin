// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import scala.scalajs.js

import org.scalajs.dom

import js.annotation._
// import react.sizeme.SizeP
// import japgolly.scalajs.react._
// import japgolly.scalajs.react.vdom.html_<^._
// import react.common.Size

@JSExportTopLevel("Main")
object AladinDemo {
  @js.native
  @JSImport("react-sizeme", JSImport.Default)
  object RawComponent extends js.Object

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
