// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import gsp.math.geom.jts.interpreter._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document
import org.scalajs.dom.raw.Element
import react.aladin._
import react.common._

final case class AladinContainer(
  s: Size
) extends ReactProps[AladinContainer](AladinContainer.component)

object AladinContainer {
  type Props = AladinContainer

  val AladinComp = Aladin.component

  class Backend($ : BackendScope[Props, Unit]) {
    // Create a mutable reference
    private val aladinRef = Ref.toScalaComponent(AladinComp)

    def renderVisualization(
      div:        Element,
      size:       => Size,
      pixelScale: => PixelScale
    ): Callback =
      ($.props |> { (p: Props) =>
        // Delete any viz previously rendered
        val previous = Option(div.querySelector(".aladin-visualization"))
        previous.foreach(div.removeChild)
        val g = document.createElement("div")
        g.classList.add("aladin-visualization")
        visualization.geometryForAladin(GmosGeometry.shapes,
                                        g,
                                        size,
                                        pixelScale,
                                        GmosGeometry.ScaleFactor
        )
        // Switch the visibility
        div.appendChild(g)
      }).void

    def includeSvg(v: JsAladin): Callback =
      v.onFullScreenToggle(recalculateView) *>
        v.onZoom(recalculateView)

    def updateVisualization(v: JsAladin): Callback = {
      val size = Size(v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
      val div  = v.getParentDiv()
      renderVisualization(div, size, v.pixelScale)
    }

    def render(props: Props) =
      <.div(
        ^.width := 100.pct,
        ^.height := 100.pct,
        AladinComp.withRef(aladinRef) {
          Aladin(showReticle = true,
                 target = "0:00:00 0:00:00",
                 // target = "ngc 1055",
                 fov = 0.25,
                 showGotoControl = false,
                 customize = includeSvg _
          )
        }
      )

    def recalculateView =
      aladinRef.get.flatMapCB(r =>
        r.backend.recalculateView *> r.backend.runOnAladinCB(updateVisualization)
      )
  }

  val component =
    ScalaComponent
      .builder[Props]
      .renderBackend[Backend]
      .componentDidUpdate(_.backend.recalculateView)
      .build

}
