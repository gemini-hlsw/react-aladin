// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package gsp.math.geom.svg

import gsp.math.geom.jts.interpreter._
import gsp.math.geom.svg._
import gsp.math.geom.svg.implicits._
import gsp.math.geom.ShapeExpression
import gem.geom.GmosScienceAreaGeometry
import gem.geom.GmosOiwfsProbeArm
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
      ("probe", GmosOiwfsProbeArm.shapeAt(posAngle, guideStarOffset, offsetPos, fpu, port)),
      ("patrol-field", GmosOiwfsProbeArm.patrolFieldAt(posAngle, offsetPos, fpu, port)),
      ("science-area", GmosScienceAreaGeometry.shapeAt(posAngle, offsetPos, fpu))
    )

  // Scale
  val arcsecPerPixel: Double =
    1.0

  val gridSize: Angle =
    50.arcsec

  // Firefox doesn't properly handle very large coordinates, scale by 100 at least
  val scalingFn: ScalingFn = _ / 10000

  val pp: SvgPostProcessor = {
    case p: Polygon   => p.addClass("jts-polygon")
    case g: G         => g.addClass("jts-group")
    case c: Container => c.addClass("jts")
    case a            => a
  }

  def generate(s: Size): Svg = {
    println(s)
    val svg: Svg = SVG_()
    shapes
      .map(x => x.copy(_2 = x._2.eval))
      .map {
        case (id, jts: JtsShape) => (id, jts)
        case x                   => sys.error(s"Whoa unexpected shape type: $x")
      }
      .toSvg(svg, pp, scalingFn = scalingFn)
    // val (h, w) = (svg.viewbox().height_Box, svg.viewbox().width_Box)
    svg
      .line(0, 0, 100, 100)
      // .transform(svg.matrix())
      .stroke("white")
      .attr("stroke-width", "1px")
      .attr("vector-effect", "non-scaling-stroke")
    // svg.line()
    // svg.viewbox().point(10, 10)
    println(svg.viewbox())
    println(svg.matrix())
    svg.size(s)
    svg
  }
}
