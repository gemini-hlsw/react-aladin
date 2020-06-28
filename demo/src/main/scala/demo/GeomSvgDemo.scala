// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import gsp.math.geom.jts.interpreter._
import gsp.math.geom.svg2._
import gsp.math.geom.svg2.implicits._
import gsp.math.geom.ShapeExpression
import gem.geom.GmosScienceAreaGeometry
// import gem.geom.GmosOiwfsProbeArm
// import gsp.math.geom.syntax.shapeexpression._
import gsp.math.geom.jts.JtsShape
import gsp.math.syntax.int._
import gsp.math.Angle
import gsp.math.Offset
import gpp.svgdotjs.svgdotjsSvgJs.mod.SVG_
import gpp.svgdotjs.svgdotjsSvgJs.mod.Svg
import gem.enum.GmosNorthFpu
import gem.enum.GmosSouthFpu
import gem.enum.PortDisposition
import cats.data.NonEmptyList
import gpp.svgdotjs.svgdotjsSvgJs.mod.Polygon
import gpp.svgdotjs.svgdotjsSvgJs.mod.G
import gpp.svgdotjs.svgdotjsSvgJs.mod.Container
import react.common._
import gpp.svgdotjs.svgdotjsSvgJs.mod.Matrix
import scala.math._

object GeomSvgDemo {
  implicit class SvgOps(val svg: Svg) extends AnyVal {
    def size(s: Size): Svg = svg.size(s.width.toDouble, s.height.toDouble)
  }

  val posAngle: Angle =
    145.deg

  val guideStarOffset: Offset =
    Offset(170543999.µas.p, -24177003.µas.q)

  val offsetPos: Offset =
    Offset(60.arcsec.p, 60.arcsec.q)

  val fpu: Option[Either[GmosNorthFpu, GmosSouthFpu]] =
    Some(Right(GmosSouthFpu.LongSlit_5_00))

  val port: PortDisposition =
    PortDisposition.Side

  // Shape to display
  val shapes: NonEmptyList[(String, ShapeExpression)] =
    NonEmptyList.of(
      // ("probe", GmosOiwfsProbeArm.shapeAt(posAngle, guideStarOffset, offsetPos, fpu, port)),
      // ("patrol-field", GmosOiwfsProbeArm.patrolFieldAt(posAngle, offsetPos, fpu, port)),
      // ("science-area", GmosScienceAreaGeometry.shapeAt(posAngle, offsetPos, fpu)),
      // ("science-ccd", GmosScienceAreaGeometry.imaging ⟲ posAngle),
      ("science-ccd-offset", GmosScienceAreaGeometry.imaging) // ↗ offsetPos)
    )

  // Scale
  val arcsecPerPixel: Double =
    1.0

  val gridSize: Angle =
    50.arcsec

  val ScaleFactor = 1000

  // Firefox doesn't properly handle very large coordinates, scale by 100 at least
  val scalingFn: ScalingFn = _ / ScaleFactor

  val pp: SvgPostProcessor = {
    case p: Polygon   => p.addClass("jts-polygon")
    case g: G         => g.addClass("jts-group")
    case c: Container => c.addClass("jts")
    case a            => a
  }

  def generate(s: Size, pixelScale: PixelScale): Svg = {
    val svg: Svg = SVG_()
    shapes
      .map(x => x.copy(_2 = x._2.eval))
      .map {
        case (id, jts: JtsShape) => (id, jts)
        case x                   => sys.error(s"Whoa unexpected shape type: $x")
      }
      .toSvg(svg, pp, scalingFn = scalingFn)
    // Viewbox size
    val (h, w) = (svg.viewbox().height_Box, svg.viewbox().width_Box)
    // Angular size of the geometry
    // val hAngle = Angle.fromMicroarcseconds((h.toLong * ScaleFactor).toLong)
    // val wAngle = Angle.fromMicroarcseconds((w.toLong * ScaleFactor).toLong)
    // Deltas to calculate the size of the svg on aladin scale
    val ps = pixelScale.x //min(pixelScale.x, pixelScale.y)
    val dy = h * ScaleFactor * 2.7777776630942e-10 * ps
    val dx = w * ScaleFactor * 2.7777776630942e-10 * ps //(wAngle.toDoubleDegrees * ps)
    // val dy1 = (hAngle.toDoubleDegrees * ps)
    // val dx1 = (wAngle.toDoubleDegrees * ps)

    println("dim")
    println(h)
    println(w)
    println("pixelScale")
    println(pixelScale.x)
    println(pixelScale.y)
    println("deltas")
    println(dx)
    println(dy)
    // val tx = Angle
    //   .fromMicroarcseconds((w.toLong - scala.math.abs(svg.viewbox().x_Box.toLong)) * ScaleFactor)
    //   .toDoubleDegrees * pixelScale.x
    // println(abs(svg.viewbox().x_Box * ScaleFactor) / w)
    val tx = Angle
    // .fromMicroarcseconds((abs(svg.viewbox().x_Box)).toLong * ScaleFactor)
      .fromMicroarcseconds((abs(w)).toLong * ScaleFactor)
      .toDoubleDegrees * ps

    val svgSize = Size(dy, dx)
    val ty =
      Angle
        .fromMicroarcseconds((abs(svg.viewbox().y_Box.toLong)) * ScaleFactor)
        .toDoubleDegrees * ps
    println("trans")
    println(tx)
    println(ty)
    svg
      .line(-10 * dx, -10 * dy, 10 * dx, 10 * dy)
      .stroke("red")
      .attr("stroke-width", "1px")
      .attr("vector-effect", "non-scaling-stroke")
    svg
      .line(-10 * dx, 10 * dy, 10 * dx, -10 * dy)
      .stroke("red")
      .attr("stroke-width", "1px")
      .attr("vector-effect", "non-scaling-stroke")
    // svg.transform(new Matrix().translate(10, -134).scale(1, -1))
    svg.transform(
      new Matrix()
        .translate(s.width.toDouble / 2 - dx / 2, -(s.height.toDouble - dy) / 2)
        .scale(1, -1)
    )
    svg.size(svgSize)
    svg
  }
}
