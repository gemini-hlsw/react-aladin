// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
// import japgolly.scalajs.react.vdom.{ svg_<^ => svg }
import react.aladin.Aladin
import react.common._
// import scala.scalajs.js
import org.scalajs.dom.ext._
// import org.scalajs.dom.document
// import org.scalajs.dom.Element
// import js.annotation._
import react.aladin.JsAladin

final case class TargetBody(
  ) extends ReactProps[TargetBody](TargetBody.component) {}

object TargetBody {
  // @js.native
  // @JSImport("star.svg", JSImport.Default)
  // val strSvg: String = js.native

  type Props = TargetBody

  protected implicit val propsReuse: Reusability[Props] = Reusability.derive

  val AladinComp = Aladin.component

  class Backend(bs: BackendScope[Props, Unit]) {
    // Create a mutable reference
    private val ref = Ref.toScalaComponent(AladinComp)
    // println(strSvg)

    def includeSvg(v: JsAladin): Unit =
      v.getParentDiv().getElementsByClassName("aladin-imageCanvas").foreach { e =>
        // val svg = document.createElement("svg")
        // println(svg)
        // svg.innerHTML = strSvg
        // e.insertBefore(strSvg, null)
        // e.insertAdjacentHTML("beforebegin", strSvg)
        // val svg = v.getParentDiv().getElementsByTagName("svg")
        // svg.foreach { e =>
        //   println(e)
        //   e.setAttribute("height", "452")
        //   e.setAttribute("width", "1854")
        //   e.setAttribute("class", "instrument-geom")
        //   e.setAttribute("z-index", "10")
        //   e.setAttribute("position", "absolute")
        // }
        // println("svg")
        // println(svg.length)
      }

    def render(props: Props) =
      React.Fragment(
        <.div(^.height := "100%", ^.width := "100%", ^.cls := "check", AladinComp.withRef(ref) {
          Aladin(target = "M51", fov = 0.25, showGotoControl = false, customize = includeSvg _)
        })
        // <.div(
        //   ^.dangerouslySetInnerHtml := strSvg
        // ),
        // svg.<.svg(
        //   ^.src := strSvg
        // )
      )

  }

  val component =
    ScalaComponent
      .builder[Props]
      .renderBackend[Backend]
      .build

}
