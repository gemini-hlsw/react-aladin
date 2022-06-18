// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.Semigroup
import cats.syntax.all._
import cats.data.NonEmptyMap
import crystal.react.reuse._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import lucuma.core.math.Coordinates
import react.common._
import react.common.implicits._
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.jts.JtsShape
import lucuma.core.geom.jts.interpreter._
import lucuma.core.math.Angle
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.Polygon
import scala.math._
import react.aladin.Fov
import react.aladin.PixelScale

final case class VisualizationOverlay(
  width:        Int,
  height:       Int,
  scaleFactor:  Int,
  fov:          Fov,
  screenOffset: Option[(Double, Double)],
  shapes:       NonEmptyMap[Css, ShapeExpression]
) extends ReactFnProps[VisualizationOverlay](VisualizationOverlay.component)

object VisualizationOverlay {
  type Props = VisualizationOverlay
  val geometryUnionSemigroup: Semigroup[Geometry] =
    Semigroup.instance(_.union(_))

  val scale = (v: Double) => rint(v / 1000)

  val JtsPolygon    = Css("jts-polygon")
  val JtsCollection = Css("jts")

  def forGeometry(css: Css, g: Geometry): VdomNode =
    g match {
      case p: Polygon            =>
        val points = p.getCoordinates
          .map(c => s"${scale(c.x)},${scale(c.y)}")
          .mkString(" ")
        <.polygon(css |+| JtsPolygon, ^.points := points)
      case p: GeometryCollection =>
        <.g(
          css |+| JtsCollection,
          p.geometries.map(forGeometry(css, _)).toTagMod
        )
      case _                     => EmptyVdom
    }

  val canvasWidth  = VdomAttr("width")
  val canvasHeight = VdomAttr("height")
  val component    =
    ScalaFnComponent[Props] { p =>
      val scalingFn: Double => Double = (v: Double) => v // rint(v / p.scaleFactor)

      // Render the svg
      val evaldShapes: NonEmptyMap[Css, JtsShape] = p.shapes
        .fmap(_.eval)
        .map {
          case jts: JtsShape => jts
          case x             => sys.error(s"Whoa unexpected shape type: $x")
        }

      val pixelScale: PixelScale =
        PixelScale(p.width / p.fov.x.toDoubleDegrees, p.height / p.fov.y.toDoubleDegrees)

      // Unit: pixels / microArcseconds
      val pixelsPerMicroarcsecondsX: Double =
        p.width / p.fov.x.toMicroarcseconds.toDouble
      val pixelsPerMicroarcsecondsY: Double =
        p.height / p.fov.y.toMicroarcseconds.toDouble

      // We should calculate the viewbox of the whole geometry
      val composite    = evaldShapes.map(_.g).reduce(geometryUnionSemigroup)
      // a.map(_.g).toList.map(_.toSvg(containerGroup, pp, scalingFn))
      val envelope     = composite.getBoundary.getEnvelopeInternal
      // dimension in micro arcseconds
      val (x, y, w, h) =
        (envelope.getMinX, envelope.getMinY, envelope.getWidth, envelope.getHeight)

      println(s"minx: ${envelope.getMinX}")
      println(s"maxx: ${envelope.getMaxX}")
      val px  = abs(x / w) - 0.5
      val mx  = p.fov.x.toMicroarcseconds.toDouble * px // + dox
      println(s"pct: ${px}")
      println(
        s"width: ${abs(envelope.getMinX) + abs(envelope.getMaxX)} ${envelope.getWidth} ${w - mx}"
      )
      // println(pixelsPerMicroarcsecondsY)
      // println(p.height / (h * pixelsPerMicroarcsecondsY))
      val sx2 = p.width / (w * pixelsPerMicroarcsecondsX)
      val sy2 = p.height / (h * pixelsPerMicroarcsecondsY)
      val sx3 = p.fov.x.toMicroarcseconds / w
      // Angular size of the geometry
      // val hAngle = Angle.fromMicroarcseconds((h.toLong * p.scaleFactor).toLong)
      // val wAngle = Angle.fromMicroarcseconds((w.toLong * p.scaleFactor).toLong)
      // Deltas to calculate the size of the svg on aladin scale
      // val dx     = (wAngle.toDoubleDegrees) * pixelScale.x
      // val dy     = (hAngle.toDoubleDegrees) * pixelScale.y

      // Svg on screen coordinates
      // val svgSize = Size(rint(dy), rint(dx))
      // println(s"W $w ${scalingFn(wAngle.toMicroarcseconds)}")
      val dox = p.width.toDouble / 2  // - offX
      val doy = p.height.toDouble / 2 // - offY

      // println(s"coord $x $w ${x / w}")
      // println(s"off ${p.screenOffset}")
      // Translation coordinates
      val tx = p.fov.x.toMicroarcseconds.toDouble * px      // + dox
      val ty = p.fov.y.toMicroarcseconds.toDouble * (y / h) // + dox
      // val ty = abs(dy * y / h)                                            // - doy

      val dx2     = (p.fov.x.toMicroarcseconds.toDouble) + tx - w / 2
      val dy2     = (p.fov.y.toMicroarcseconds.toDouble) + ty - h / 2
      println(s"tx: $tx ty: $ty")
      // println(s"tot: ${p.fov.x.toMicroarcseconds} x: $x")
      // val ttx     = p.scaleFactor * (tx / pixelScale.x)
      // val tty     = p.scaleFactor * (ty / pixelScale.y)
      // println(s"dx: $dox px  dy: $doy px")
      // println(s"outx: ${p.width} outy: ${p.height}")
      // val sx      = dx / p.width
      // val sy      = dy / p.height
      // val ttx     = p.scaleFactor * (w / 2) / pixelScale.x
      // val tty     = p.scaleFactor * (h / 2) / pixelScale.y
      // val ry      = p.scaleFactor * (ty - dy / 2)
      // println(s"ttx: ${ttx} tty: $tty")
      println(s"sx: $mx $sx2 sy: $sy2 sx3: $sx3")
      // val viewBox =::
      //   s"${x} ${y - dy2} ${w} ${h * sy2}"
      val viewBox =
        s"${scale(x + px * w) * sx3} ${scale(y)} ${scale(w) * sx3} ${scale(h)}"

      // val viewBox = s"${x - dx2} ${y - dy2} ${w * sx2} ${h * sy2}"
      // val viewBox = s"$x $y ${w * sx2} ${h * sy2}"
      // println(s"sy: ${1 / sy} $sy2")
      println(s"dx2: $dx2 ${2 * dx2 * sy2}")
      // println(s"dx2: $tx ${dox * sx2}")
      // println(s"dx2: ${x / w} ${y / h} $dx2")
      // println(s"viewBox: $viewBox")

      val svg = <.svg(
        ^.`class`    := "visualization-overlay-svg",
        ^.viewBox    := viewBox,
        // canvasWidth  := s"${dx}px",
        // canvasHeight := s"${dy}px",
        canvasWidth  := s"${p.width}px",
        canvasHeight := s"${p.height}px",
        <.g(
          ^.`class`   := "jts-root-group",
          ^.transform := s"scale(1, -1)",
          evaldShapes.toNel
            .map { case (css, shape) =>
              forGeometry(css, shape.g)
            }
            .toList
            .toTagMod
        ),
        <.rect(
          ^.`class`   := "helper",
          ^.x         := scale(x),
          ^.y         := scale(y),
          ^.width     := scale(w),
          ^.height    := scale(h)
        ),
        <.line(
          ^.`class`   := "helper",
          ^.x1        := 0,
          ^.y1        := 0,
          ^.x2        := scale(w) / 2,
          ^.y2        := scale(h) / 2
        ),
        <.line(
          ^.`class`   := "helper",
          ^.x1        := -scale(w) / 2,
          ^.y1        := scale(h) / 2,
          ^.x2        := 0,
          ^.y2        := 0
        )
      )
      svg
    }
}
