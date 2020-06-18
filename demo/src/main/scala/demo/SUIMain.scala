package demo

import org.scalajs.dom
import scala.scalajs.js
import js.annotation._
// import japgolly.scalajs.react._

@JSExportTopLevel("Main")
object SUiMain {

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
