// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import react.common._
import lucuma.core.math.Coordinates
import lucuma.ui.reusability._
import org.scalajs.dom.window.performance
import react.aladin.Fov
import react.aladin.reusability._

final case class AGSSVGOverlay(
  width:     Int,
  height:    Int,
  fov:       Fov,
  current:   Coordinates,
  world2pix: Coordinates => Option[(Double, Double)],
  gs:        List[Coordinates]
) extends ReactFnProps[AGSSVGOverlay](AGSSVGOverlay.component)

object AGSSVGOverlay {
  // TODO use a counter
  val canvasWidth  = VdomAttr("width")
  val canvasHeight = VdomAttr("height")
  val component    =
    ScalaFnComponent
      .withHooks[AGSSVGOverlay]
      .useMemoBy(p => (p.width, p.height, p.fov, p.current, p.gs)) { p => _ =>
        performance.mark("ags-svg-start")
        val svg = <.svg(
          ^.`class`    := "ags-svg",
          canvasWidth  := s"${p.width}px",
          canvasHeight := s"${p.height}px",
          p.gs
            .map(c => (c, p.world2pix(c)))
            .collect { case (c, Some((x, y))) =>
              val u =
                s"coords catalog ra: ${c.ra.toAngle.toDoubleDegrees}, dec: ${c.dec.toAngle.toSignedDoubleDegrees}"
              <.circle(^.cx      := x,
                       ^.cy      := y,
                       ^.r       := 3,
                       ^.`class` := "catalog-target",
                       <.title(s"$u $x $y")
              )
            }
            .toTagMod
        )
        performance.mark("ags-svg-end")
        performance.measure("ags-svg", "ags-svg-start", "ags-svg-end")
        svg
      }
      .render { (p, svg) =>
        svg
      }
}
