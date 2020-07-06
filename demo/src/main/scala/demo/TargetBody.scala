// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import react.aladin._
import react.common._
import react.sizeme._
import scala.scalajs.js
import org.scalajs.dom.document
import gsp.math.geom.jts.interpreter._
import demo.GeomSvgDemo
import org.scalajs.dom.raw.Element

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
    p.name = name
    p.size = size
    p.otype = otype
    p
  }

}

final case class AladinContainer(s: Size)
    extends ReactProps[AladinContainer](AladinContainer.component)

object AladinContainer {
  type Props = AladinContainer

  protected implicit val propsReuse: Reusability[Props] = Reusability.never

  val AladinComp = Aladin.component

  class Backend(bs: BackendScope[Props, Unit]) {
    // Create a mutable reference
    private val ref = Ref.toScalaComponent(AladinComp)

    def renderVisualization(div: Element, size: Size, pixelScale: => PixelScale): Callback =
      Callback {
        // Delete any viz previously rendered
        val previous = Option(div.querySelector(".aladin-visualization"))
        previous.foreach(div.removeChild)
        val g = document.createElement("div")
        g.classList.add("aladin-visualization")
        visualization.geometryForAladin(GeomSvgDemo.shapes,
                                        g,
                                        size,
                                        pixelScale,
                                        GeomSvgDemo.ScaleFactor
        )
        div.appendChild(g)
      }

    def includeSvg(v: JsAladin): Unit = {
      val size = Size(v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
      val div  = v.getParentDiv()
      v.onZoomCB(renderVisualization(div, size, v.pixelScale))
      ()
    }

    def updateVisualization(v: JsAladin): Callback = {
      val size = Size(v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
      val div  = v.getParentDiv()
      renderVisualization(div, size, v.pixelScale)
    }

    def render(props: Props) =
      SizeMe() { s =>
        <.div(
          ^.height := "100%",
          ^.width := "100%",
          ^.cls := "check",
          AladinComp.withRef(ref) {
            Aladin(showReticle = true,
                   target = "0:00:00 0:00:00",
                   // target          = "M51",
                   fov = 0.25,
                   showGotoControl = false,
                   customize = includeSvg _
            )
          }
        )
      }

    def recalculateView =
      Callback.log("here") *>
        ref.get.flatMapCB(r => r.backend.runOnAladinCB(updateVisualization))
  }

  val component =
    ScalaComponent
      .builder[Props]
      .renderBackend[Backend]
      .componentDidUpdate(_.backend.recalculateView)
      .build

}

object TargetBody {
  type Props = TargetBody

  protected implicit val propsReuse: Reusability[Props] = Reusability.derive

  val component =
    ScalaComponent
      .builder[Props]
      .stateless
      .render { _ =>
        SizeMe() { s =>
          AladinContainer(s)
        }.vdomElement
      }
      .build

}
