// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import react.common._
import org.scalajs.dom.html
import org.scalajs.dom.CanvasRenderingContext2D

final case class AGSCanvas(
  width:  Int,
  height: Int,
  gs:     (Int, List[(Double, Double)])
) extends ReactFnProps[AGSCanvas](AGSCanvas.component)

object AGSCanvas {
  // TODO use a counter
  import Reusability.DecimalImplicitsWithoutTolerance._
  val canvasWidth  = VdomAttr("width")
  val canvasHeight = VdomAttr("height")
  val component    =
    ScalaFnComponent
      .withHooks[AGSCanvas]
      .useRefToVdom[html.Canvas]
      .useEffectWithDepsBy((p, _) => (p.width, p.height, p.gs._2)) { (p, canvasRef) => _ =>
        canvasRef.get.flatMap(ref =>
          Callback(ref.foreach { canvas =>
            val ctx = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
            ctx.fillStyle = "red"
            ctx.clearRect(0, 0, canvas.width.toDouble, canvas.height.toDouble)
            p.gs._2.foreach { case (x, y) =>
              ctx.fillRect(x, y, 2, 2)
            }
          })
        )
      }
      .render { (p, canvas) =>
        <.canvas(
          ^.pointerEvents := "none",
          canvasWidth     := s"${p.width}px",
          canvasHeight    := s"${p.height}px"
        ).withRef(canvas)
      }
}
