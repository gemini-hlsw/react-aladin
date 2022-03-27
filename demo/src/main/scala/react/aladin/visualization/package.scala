// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react.aladin

import scala.math._

import cats.data.NonEmptyMap
import cats.implicits._
import lucuma.svgdotjs.Svg
import lucuma.svgdotjs.Polygon
import lucuma.svgdotjs.Rect
import lucuma.svgdotjs.Matrix
import lucuma.svgdotjs.Group
import lucuma.svgdotjs.Container
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.ShapeExpression._
import lucuma.core.geom.ShapeInterpreter
import lucuma.core.geom.jts.syntax.all._
import lucuma.core.geom.jts.JtsShape
import lucuma.core.geom2.svg._
import lucuma.core.geom2.svg.implicits._
import lucuma.core.math.Angle
import org.scalajs.dom.Element
import react.common._
import scala.collection.immutable.SortedMap
import lucuma.svgdotjs.svgdotjsSvgJs.mod.Circle
import org.locationtech.jts.geom.util.AffineTransformation
import org.locationtech.jts.geom.Geometry
import lucuma.core.math.Offset

package object visualization {
  implicit class SvgOps(val svg: Svg) extends AnyVal {
    def setSize(s: Size): Svg = svg.size(s.width.toDouble, s.height.toDouble)
  }

  val boxProcessor: SvgPostProcessor = { case a =>
    if (a.id().endsWith("bbox")) a.addClass("bbox") else a
  }
  val pp: SvgPostProcessor           = {
    case c: Circle    => c.addClass("jts-circle")
    case p: Polygon   => p.addClass("jts-polygon")
    case g: Group     => g.addClass("jts-group")
    case c: Container => c.addClass("jts")
    case a            => a
  }

  def pointToCoords(
    s: ShapeExpression
  ): Option[Offset] = {
    def find(s: ShapeExpression): Geometry =
      s match {
        // Constructors
        case Point(a) => a.point

        // Combinations
        case Difference(a, b)   => find(a).difference(find(b))
        case Intersection(a, b) => find(a).intersection(find(b))
        case Union(a, b)        => find(a).union(find(b))

        // Transformations
        case FlipP(e) =>
          AffineTransformation
            .scaleInstance(-1.0, 1.0)
            .transform(find(e))

        case FlipQ(e) =>
          AffineTransformation
            .scaleInstance(1.0, -1.0)
            .transform(find(e))

        case Rotate(e, a) =>
          AffineTransformation
            .rotationInstance(a.toDoubleRadians)
            .transform(find(e))

        case RotateAroundOffset(e, a, o) =>
          val c = o.coordinate
          AffineTransformation
            .rotationInstance(a.toDoubleRadians, c.x, c.y)
            .transform(find(e))

        case Translate(e, o) =>
          val c = o.coordinate
          AffineTransformation
            .translationInstance(c.x, c.y)
            .transform(find(e))
      }
    find(s) match {
      case a: org.locationtech.jts.geom.Point =>
        Some(
          Offset(Offset.P(Angle.fromMicroarcseconds(-a.getX.toLong)),
                 Offset.Q(Angle.fromMicroarcseconds(a.getY.toLong))
          )
        )
      case _                                  => None
    }
  }
  // si.interpret(point).
  // val scalingFn: ScalingFn = (v: Double) => rint(v / scaleFactor)

  def shapesToSvg(
    shapes:      NonEmptyMap[String, ShapeExpression],
    pp:          SvgPostProcessor,
    scaleFactor: Int
  )(implicit si: ShapeInterpreter): Svg = {
    val scalingFn: ScalingFn = (v: Double) => rint(v / scaleFactor)

    val svg: Svg = new Svg()

    // Render the svg
    val evaldShapes: SortedMap[String, JtsShape] = shapes
      .map(_.eval)
      .toSortedMap
      .map {
        case (id, jts: JtsShape) => (id, jts)
        case x                   => sys.error(s"Whoa unexpected shape type: $x")
      }

    // Unsafe call but we know the map is non empty
    NonEmptyMap
      .fromMapUnsafe(evaldShapes)
      .toSvg(svg, pp, scalingFn = scalingFn)

    // Render the svg boxes
    // val evaldShapesBoundingBoxes: SortedMap[String, JtsShape] = shapes
    //   .map(_.eval)
    //   .toSortedMap
    //   .map {
    //     case (id, jts: JtsShape) =>
    //       (s"$id-bbox",
    //        ShapeExpression.Rectangle(jts.boundingBox._1, jts.boundingBox._3).eval match {
    //          case jts: JtsShape => jts
    //          case x             => sys.error(s"Whoa unexpected shape type: $x")
    //        }
    //       )
    //     case x                   => sys.error(s"Whoa unexpected shape type: $x")
    //   }
    //
    // NonEmptyMap
    //   .fromMapUnsafe(evaldShapesBoundingBoxes)
    //   .toSvg(svg, pp.andThen(boxProcessor), scalingFn = scalingFn)
    svg
  }

  def addBorder(svg: Container, x: Double, y: Double, w: Double, h: Double): Rect =
    // Border to the whole svg, usually hidden
    svg
      .rect(w, h)
      .translate(x, y)
      .fill("none")
      .attr("class", "jts-svg-border")

  val ReticleSize = 14

  def addCross(svg: Container, reticleSize: Double): Group = {
    val g = svg.group()
    g.attr("class", "jts-svg-reticle")

    // Cross at 0,0 style it with css
    g.line(0, -reticleSize, 0, reticleSize)
      .attr("class", "jts-svg-center")
    g.line(-reticleSize, 0, reticleSize, 0)
      .attr("class", "jts-svg-center")
    g
  }

  def updatePosition(
    svgBase:     Svg,
    parent:      Element,
    s:           Size,
    pixelScale:  PixelScale,
    scaleFactor: Int,
    coordOffset: (Double, Double)
  ): Element = {
    // Viewbox size
    val (h, w) = (svgBase.viewbox().height_Box, svgBase.viewbox().width_Box)
    val (x, y) = (svgBase.viewbox().x_Box, svgBase.viewbox().y_Box)

    // Transform the group at the root of the svg
    svgBase.children().each { (_: Container) =>
      // Angular size of the geometry
      val hAngle       = Angle.fromMicroarcseconds((h.toLong * scaleFactor).toLong)
      val wAngle       = Angle.fromMicroarcseconds((w.toLong * scaleFactor).toLong)
      // Deltas to calculate the size of the svg on aladin scale
      val dx           = (wAngle.toDoubleDegrees) * pixelScale.x
      val dy           = (hAngle.toDoubleDegrees) * pixelScale.y
      val (offX, offY) = coordOffset
      val dox          = s.width.toDouble / 2 - offX
      val doy          = s.height.toDouble / 2 - offY

      // Translation coordinates
      val tx = abs(dx * x / w) + dox
      val ty = abs(dy * y / h) - doy

      // Rotation reference point. It is a bit surprising but it is in screen coordinates
      val ry = ty - dy / 2
      // To workaround Safari we set the position of the surrounding div rather than the svg
      parent.setAttribute(
        "style",
        s"position: absolute; left: ${rint(s.width.toDouble / 2 - tx)}px; top: ${rint(s.height.toDouble / 2 - ty + 2 * ry)}px"
      )
    }
    parent
  }

  def geometryForAladin(
    svgBase:     Svg,
    parent:      Element,
    s:           Size,
    pixelScale:  PixelScale,
    scaleFactor: Int,
    coordOffset: (Double, Double)
  ): Element = {
    // Viewbox size
    val (h, w) = (svgBase.viewbox().height_Box, svgBase.viewbox().width_Box)
    val (x, y) = (svgBase.viewbox().x_Box, svgBase.viewbox().y_Box)

    // Transform the group at the root of the svg
    svgBase.children().each { (svg: Container) =>
      // Angular size of the geometry
      val hAngle       = Angle.fromMicroarcseconds((h.toLong * scaleFactor).toLong)
      val wAngle       = Angle.fromMicroarcseconds((w.toLong * scaleFactor).toLong)
      // Deltas to calculate the size of the svg on aladin scale
      val dx           = (wAngle.toDoubleDegrees) * pixelScale.x
      val dy           = (hAngle.toDoubleDegrees) * pixelScale.y
      val (offX, offY) = coordOffset
      val dox          = s.width.toDouble / 2 - offX
      val doy          = s.height.toDouble / 2 - offY

      val svgSize = Size(rint(dy), rint(dx))

      // Translation coordinates
      val tx = abs(dx * x / w) + dox
      val ty = abs(dy * y / h) - doy

      // center cross
      val reticleSizeX = ReticleSize * x / dx
      addCross(svg, reticleSizeX)

      // Border to the whole svg, usually hidden
      addBorder(svg, x, y, w, h)

      // Rotation reference point. It is a bit surprising but it is in screen coordinates
      val ry = ty - dy / 2
      // Flip the svg, note we should flip around ry but that creates troubles with the viewbox
      // Instead we adjust the top attribute
      if (scala.scalajs.js.isUndefined(svg.attr("transform")))
        svg.scale(1, -1)
      svgBase.setSize(svgSize)
      // To workaround Safari we set the position of the surrounding div rather than the svg
      parent.setAttribute(
        "style",
        s"position: absolute; left: ${rint(s.width.toDouble / 2 - tx)}px; top: ${rint(s.height.toDouble / 2 - ty + 2 * ry)}px"
      )
    }
    parent.appendChild(svgBase.node_Svg)
    parent
  }

  /**
   * This method will build an svg appropriate to show on aladin. Note we need to transform the svg
   * to get the correct size and location This particular method uses just svg but it doesn't
   * properly work on Safari
   */
  // def geometryForAladin(
  //   shapes:      NonEmptyMap[String, ShapeExpression],
  //   s:           Size,
  //   pixelScale:  PixelScale,
  //   scaleFactor: Int
  // )(implicit si: ShapeInterpreter): Svg = {
  //   val svg    = shapesToSvg(shapes, pp, scaleFactor)
  //   // Viewbox size
  //   val (h, w) = (svg.viewbox().height_Box, svg.viewbox().width_Box)
  //   val (x, y) = (svg.viewbox().x_Box, svg.viewbox().y_Box)
  //   // Angular size of the geometry
  //   val hAngle = Angle.fromMicroarcseconds((h.toLong * scaleFactor).toLong)
  //   val wAngle = Angle.fromMicroarcseconds((w.toLong * scaleFactor).toLong)
  //   // Deltas to calculate the size of the svg on aladin scale
  //   val dx     = wAngle.toDoubleDegrees * pixelScale.x
  //   val dy     = hAngle.toDoubleDegrees * pixelScale.y
  //
  //   val svgSize = Size(dy, dx)
  //
  //   // Translation coordinates
  //   val tx = abs(dx * x / w)
  //   val ty = abs(dy * y / h)
  //
  //   // center cross
  //   val reticleSizeX = ReticleSize * x / dx
  //   addCross(svg, reticleSizeX)
  //
  //   // Border to the whole svg, usually hidden
  //   svg
  //     .rect(w, h)
  //     .translate(x, y)
  //     .fill("none")
  //     .attr("class", "jts-svg-border")
  //
  //   // Rotation reference point. It is a bit surprising but it is in screen coordinates
  //   val ry             = ty - dy / 2
  //   // Scale and postion the center in the right location
  //   val transformation =
  //     new Matrix()
  //       .scale(1, -1, 0, ry) // Order of operations is important
  //       .translate(s.width.toDouble / 2 - tx, s.height.toDouble / 2 - ty)
  //   svg.transform(transformation)
  //   svg.setSize(svgSize)
  //   svg
  // }
}
