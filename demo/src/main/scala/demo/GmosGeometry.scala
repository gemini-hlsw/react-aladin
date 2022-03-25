// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.data.NonEmptyMap
import cats.implicits._
import lucuma.svgdotjs._
import lucuma.core.enum.GmosNorthFpu
import lucuma.core.enum.GmosSouthFpu
import lucuma.core.enum.PortDisposition
import lucuma.core.geom.GmosOiwfsProbeArm
import lucuma.core.geom.GmosScienceAreaGeometry
import lucuma.core.geom.ShapeExpression
import lucuma.core.geom.svg._
import lucuma.core.geom.syntax.shapeexpression._
import lucuma.core.math.Angle
import lucuma.core.math.Offset
import lucuma.core.math.syntax.int._

object GmosGeometry {

  val posAngle: Angle =
    145.deg

  val guideStarOffset: Offset =
    Offset(170543999.µas.p, -24177003.µas.q)

  val offsetPos: Offset =
    Offset(-60.arcsec.p, 60.arcsec.q)

  val fpu: Option[Either[GmosNorthFpu, GmosSouthFpu]] =
    Some(Right(GmosSouthFpu.LongSlit_5_00))

  val port: PortDisposition =
    PortDisposition.Side

  def agsField =
    // 4.9 arcmin
    ShapeExpression.centeredEllipse((4.9 * 60 * 1000 * 2).toInt.mas,
                                    (4.9 * 60 * 1000 * 2).toInt.mas
    )

  def patrolField(posAngle: Angle, offsetPos: Offset) =
    GmosOiwfsProbeArm.patrolFieldAt(posAngle, offsetPos, fpu, port)

  // Shape to display
  def shapes(posAngle: Angle, offsetPos: Offset): NonEmptyMap[String, ShapeExpression] =
    NonEmptyMap.of(
      ("probe", GmosOiwfsProbeArm.shapeAt(posAngle, guideStarOffset, offsetPos, fpu, port)),
      ("patrol-field", patrolField(posAngle, offsetPos)),
      ("agsField", agsField),
      ("science-ccd", GmosScienceAreaGeometry.imaging ⟲ posAngle),
      ("science-ccd-offset", GmosScienceAreaGeometry.imaging ↗ offsetPos ⟲ posAngle)
    )

  // Scale
  val arcsecPerPixel: Double =
    1.0

  val gridSize: Angle =
    50.arcsec

  val ScaleFactor = 1000

  // Firefox doesn't properly handle very large coordinates, scale by 1000 at least
  val scalingFn: ScalingFn = _ / ScaleFactor

  val pp: SvgPostProcessor = {
    case p: Polygon   => p.addClass("jts-polygon")
    case g: Group     => g.addClass("jts-group")
    case c: Container => c.addClass("jts")
    case a            => a
  }

}
