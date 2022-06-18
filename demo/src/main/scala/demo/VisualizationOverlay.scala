// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.Semigroup
import cats.syntax.all._
import cats.data.NonEmptyMap
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import lucuma.core.math.Offset
import react.common._
import react.common.implicits._
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.jts.JtsShape
import lucuma.core.geom.jts.interpreter._
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryCollection
import org.locationtech.jts.geom.Polygon
import scala.math._
import react.aladin.Fov

final case class VisualizationOverlay(
  width:        Int,
  height:       Int,
  fov:          Fov,
  screenOffset: Offset,
  shapes:       NonEmptyMap[Css, ShapeExpression]
) extends ReactFnProps[VisualizationOverlay](VisualizationOverlay.component)

object VisualizationOverlay {
  type Props = VisualizationOverlay
  val geometryUnionSemigroup: Semigroup[Geometry] =
    Semigroup.instance(_.union(_))

  // The values on the geometry are in microarcseconds
  // They are fairly large and break is some browsers
  // We apply a scaling factor uniformily
  val scale = (v: Double) => rint(v / 1000)

  val JtsPolygon    = Css("viz-polygon")
  val JtsCollection = Css("viz-collecttion")
  val JtsGuides     = Css("viz-guides")

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
    ScalaFnComponent
      .withHooks[Props]
      .render { p =>
      // Render the svg
      val evaldShapes: NonEmptyMap[Css, JtsShape] = p.shapes
        .fmap(_.eval)
        .map {
          case jts: JtsShape => jts
          case x             => sys.error(s"Whoa unexpected shape type: $x")
        }

      // We should calculate the viewbox of the whole geometry
      val composite    = evaldShapes.map(_.g).reduce(geometryUnionSemigroup)
      val envelope     = composite.getBoundary.getEnvelopeInternal
      // dimension in micro arcseconds
      val (x, y, w, h) =
        (envelope.getMinX, envelope.getMinY, envelope.getWidth, envelope.getHeight)

      // Shift factors on x/y, basically the percentage shifted on x/y
      val px = abs(x / w) - 0.5
      val py = abs(y / h) - 0.5
      // scaling factors on x/y
      val sx = p.fov.x.toMicroarcseconds / w
      val sy = p.fov.y.toMicroarcseconds / h

      // Offset amount
      val offP =
        Offset.P.signedDecimalArcseconds.get(p.screenOffset.p).toDouble * 1e6

      val offQ =
        Offset.Q.signedDecimalArcseconds.get(p.screenOffset.q).toDouble * 1e6

      // Do the shifting and offseting via viewbox
      val viewBox =
        s"${scale(x + px * w) * sx + scale(offP)} ${scale(y + py * h) * sy + scale(offQ)} ${scale(w) * sx} ${scale(h) * sy}"

      val svg = <.svg(
        ^.`class`    := "visualization-overlay-svg",
        ^.viewBox    := viewBox,
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
          ^.x1        := scale(x),
          ^.y1        := scale(y),
          ^.x2        := scale(x + w),
          ^.y2        := scale(y + h)
        ),
        <.line(
          ^.`class`   := "helper",
          ^.x1        := scale(x),
          ^.y1        := -scale(y),
          ^.x2        := scale(x + w),
          ^.y2        := -scale(y + h)
        )
      )
      svg
    }
}
