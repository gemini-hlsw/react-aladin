// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import react.aladin._
import react.common._
import scala.scalajs.js
import demo.GeomSvgDemo

final case class TargetBody(
  ) extends ReactProps[TargetBody](TargetBody.component) {}

@js.native
trait SourceData extends js.Object {
  var name: String  = js.native
  var size: Double  = js.native
  var otype: String = js.native
}

object SourceData {
  def apply(name: String, size: Double, otype: String): SourceData = {
    val p = (new js.Object()).asInstanceOf[SourceData]
    p.name  = name
    p.size  = size
    p.otype = otype
    p
  }

}

object TargetBody {
  type Props = TargetBody

  protected implicit val propsReuse: Reusability[Props] = Reusability.derive

  val AladinComp = Aladin.component

  class Backend(bs: BackendScope[Props, Unit]) {
    // Create a mutable reference
    private val ref = Ref.toScalaComponent(AladinComp)

    def includeSvg(v: JsAladin): Unit = {
      val (h, w) = (v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
      val div    = v.getParentDiv()
      // div.an
      v.onZoomCB {
        Callback {
          val svg      = GeomSvgDemo.generate(Size(h, w), v.pixelScale)
          val previous = Option(div.querySelector("svg"))
          previous.foreach(div.removeChild)
          div.appendChild(svg.node_Svg)
        }
      }
      ()
    }

    def render(props: Props) =
      React.Fragment(
        <.div(
          ^.height := "100%",
          ^.width := "100%",
          ^.cls := "check",
          AladinComp.withRef(ref) {
            Aladin(showReticle = false,
                   target      = "0:00:00 0:00:00",
                   // target          = "M51",
                   fov             = 0.25,
                   showGotoControl = false,
                   customize       = includeSvg _)
          }
        )
      )

  }

  val component =
    ScalaComponent
      .builder[Props]
      .renderBackend[Backend]
      .build

}
