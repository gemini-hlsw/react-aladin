// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import react.common._
import org.scalajs.dom.html
import org.scalajs.dom.CanvasRenderingContext2D

final case class AGSCanvas(
  s:  Size,
  gs: List[(Double, Double)]
) extends ReactFnProps[AGSCanvas](AGSCanvas.component)

object AGSCanvas {
  implicit val sizeReuse: Reusability[Size] = Reusability.by(x => (x.width.toInt, x.height.toInt))
  // val propsReuse: Reusability[AGSCanvas] = Reusability.derive
  val canvasWidth                           = VdomAttr("width")
  val canvasHeight                          = VdomAttr("height")
  val component                             =
    ScalaFnComponent
      .withHooks[AGSCanvas]
      .useRefToVdom[html.Canvas]
      .useEffectWithDepsBy((p, _) => (p.s, p.gs.length)) { (p, canvasRef) => _ =>
        canvasRef.get.flatMap(ref =>
          Callback(ref.foreach { canvas =>
            val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
            ctx.fillStyle = "red"
            p.gs.map { case (x, y) =>
              ctx.fillRect(x, y, 2, 2)
            }
          })
        )
      }
      .render { (p, canvas) =>
        <.canvas(
          ^.pointerEvents := "none",
          canvasWidth     := s"${p.s.width}px",
          canvasHeight    := s"${p.s.height}px"
        ).withRef(canvas)
      }
}
