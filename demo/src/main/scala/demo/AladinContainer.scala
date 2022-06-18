// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.implicits._
import crystal.react.ReuseView
import crystal.react.hooks._
import crystal.react.reuse._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.geom.jts.interpreter._
import lucuma.core.math._
import lucuma.svgdotjs.Svg
import lucuma.ui.reusability._
import monocle.macros.GenLens
import org.scalajs.dom.Element
import org.scalajs.dom.document
import react.aladin._
import react.common._
import react.resizeDetector.hooks._

final case class AladinContainer(
  fov:         ReuseView[Fov],
  coordinates: Coordinates
) extends ReactFnProps[AladinContainer](AladinContainer.component) {
  val aladinCoordsStr: String = Coordinates.fromHmsDms.reverseGet(coordinates)
}

object AladinContainer {
  type Props = AladinContainer

  val AladinComp = Aladin.component

  val coordinates = GenLens[AladinContainer](_.coordinates)

  implicit val propsReuse: Reusability[Props] =
    Reusability.by_== // by(p => (p.aladinCoordsStr, p.s.width.toDouble, p.s.height.toDouble))

  implicit val reuseDouble = Reusability.double(0.00001)

  type World2PixFn = Coordinates => Option[(Double, Double)]
  val DefaultWorld2PixFn: World2PixFn = (_: Coordinates) => None

  def updateVisualization(svg: Svg, off: (Double, Double))(v: JsAladin): Callback = {
    val size = Size(v.getParentDiv().clientHeight.toDouble, v.getParentDiv().clientWidth.toDouble)
    val div  = v.getParentDiv()
    renderVisualization(svg, off, div, size, v.pixelScale)
  }

  def renderVisualization(
    svg:        Svg,
    offset:     (Double, Double),
    div:        Element,
    size:       Size,
    pixelScale: PixelScale
  ): Callback =
    Callback {
      val (x, y) = offset
      // Delete any viz previously rendered
      val g      = Option(div.querySelector(".aladin-visualization"))
        .map { g =>
          g.childNodes.toList.foreach(g.removeChild)
          g
        }
        .getOrElse {
          val g = document.createElement("div")
          g.classList.add("aladin-visualization")
          // Include the svg on the dom
          div.appendChild(g)
          g
        }
      // Render the svg
      visualization.geometryForAladin(svg, g, size, pixelScale, GmosGeometry.ScaleFactor, (x, y))
    }

  val component =
    ScalaFnComponent
      .withHooks[Props]
      // View coordinates (in case the user pans)
      .useStateBy(_.coordinates)
      // Memoized svg
      .useMemoBy((p, _) => p.fov) { case (_, _) =>
        _ =>
          visualization
            .shapesToSvg(GmosGeometry.shapes, GmosGeometry.pp, GmosGeometry.ScaleFactor)
      }
      // Ref to the aladin component
      .useRefToScalaComponent(AladinComp)
      // Function to calculate coordinates
      .useSerialState(DefaultWorld2PixFn)
      // resize detector
      .useResizeDetector()
      // Update the world2pix function
      .useEffectWithDepsBy { (p, currentPos, _, aladinRef, _, resize) =>
        (resize, p.fov, currentPos, aladinRef)
      } { (_, _, _, aladinRef, w, _) => _ =>
        aladinRef.get.asCBO.flatMapCB(_.backend.world2pixFn.flatMap(w.setState))
      }
      // Render the visualization, only if current pos, fov or size changes
      .useEffectWithDepsBy((p, currentPos, _, _, world2pix, resize) =>
        (p.fov, currentPos, world2pix.value(p.coordinates), resize)
      ) { (_, _, svg, aladinRef, _, _) =>
        { case (_, _, off, _) =>
          off.map { off =>
            aladinRef.get.asCBO
              .flatMapCB(_.backend.runOnAladinCB(updateVisualization(svg, off)))
              .toCallback
          }.getOrEmpty
        }
      }
      .renderWithReuse { (props, currentPos, _, aladinRef, world2pix, resize) =>
        /**
         * Called when the position changes, i.e. aladin pans. We want to offset the visualization
         * to keep the internal target correct
         */
        def onPositionChanged(u: PositionChanged): Callback =
          currentPos.setState(Coordinates(u.ra, u.dec))

        def onZoom = (v: Fov) => Callback.log(s"onZoom $v") *> props.fov.set(v)
        // def onZoom = (v: Fov) => props.fov.set(v)

        val screenOffset = world2pix.value(currentPos.value)

        def customizeAladin(v: JsAladin): Callback =
          v.onZoom(onZoom) *> // re render on zoom
            v.onPositionChanged(onPositionChanged)

        val gs =
          props.coordinates.offsetBy(Angle.Angle0, GmosGeometry.guideStarOffset)

        <.div(
          // ExploreStyles.AladinContainerBody,
          Css("react-aladin-container"),
          (resize.width, resize.height).mapN(
            VisualizationOverlay(
              _,
              _,
              GmosGeometry.ScaleFactor,
              props.fov.get,
              screenOffset,
              GmosGeometry.shapes
            )
          ),
          (resize.width, resize.height).mapN(
            SVGTargetsOverlay(
              _,
              _,
              props.fov.get,
              world2pix.value,
              List(
                SVGTarget.CrosshairTarget(props.coordinates, Css("science-target"), 10).some,
                gs.map(SVGTarget.CircleTarget(_, Css("guidestar"), 3))
              ).flatten
            )
          ),
          // This is a bit tricky. Sometimes the height can be 0 or a very low number.
          // This happens during a second render. If we let the height to be zero, aladin
          // will take it as 1. This height ends up being a denominator, which, if low,
          // will make aladin request a large amount of tiles and end up freeze explore.
          if (resize.height.exists(_ >= 100))
            AladinComp.withRef(aladinRef) {
              Aladin(
                Css("react-aladin"),
                showReticle = false,
                showLayersControl = false,
                target = props.aladinCoordsStr,
                fov = props.fov.get.x,
                showGotoControl = false,
                customize = customizeAladin _
              )
            }
          else EmptyVdom
        )
          .withRef(resize.ref)
      }

}
