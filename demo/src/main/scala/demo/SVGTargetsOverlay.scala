// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.Eq
import cats.syntax.all._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.svg_<^._
import react.common._
import react.common.implicits._
import lucuma.core.math.Coordinates
import lucuma.ui.reusability._
import react.aladin.Fov
import crystal.react.reuse._

sealed trait SVGTarget {
  def coordinates: Coordinates
  def css: Css
}

object SVGTarget {
  final case class CircleTarget(
    coordinates: Coordinates,
    css:         Css,
    radius:      Double,
    title:       Option[String] = None
  ) extends SVGTarget

  final case class CrosshairTarget(
    coordinates: Coordinates,
    css:         Css,
    side:        Double,
    title:       Option[String] = None
  ) extends SVGTarget

  implicit val eqSVGTarget: Eq[SVGTarget] = Eq.instance {
    case (CircleTarget(c1, s1, r1, t1), CircleTarget(c2, s2, r2, t2))       =>
      c1 === c2 && s1 === s2 & r1 === r2 && t1 === t2
    case (CrosshairTarget(c1, s1, r1, t1), CrosshairTarget(c2, s2, r2, t2)) =>
      c1 === c2 && s1 === s2 & r1 === r2 && t1 === t2
    case _                                                                  => false
  }

  implicit val svgTargetReusability: Reusability[SVGTarget] = Reusability.byEq
}

final case class SVGTargetsOverlay(
  width:     Int,
  height:    Int,
  fov:       Fov,
  world2pix: Coordinates ==> Option[(Double, Double)],
  targets:   List[SVGTarget]
) extends ReactFnProps[SVGTargetsOverlay](SVGTargetsOverlay.component)

object SVGTargetsOverlay {
  type Props = SVGTargetsOverlay
  implicit val doubleReuse: Reusability[Double] = Reusability.double(1)
  implicit val exactFovReuse: Reusability[Fov]  = Reusability.derive
  implicit val reuse: Reusability[Props]        = Reusability.derive

  val canvasWidth  = VdomAttr("width")
  val canvasHeight = VdomAttr("height")
  val component    =
    ScalaFnComponent
      .withReuse[Props] { p =>
        val svg = <.svg(
          ^.`class`    := "targets-overlay-svg",
          canvasWidth  := s"${p.width}px",
          canvasHeight := s"${p.height}px",
          p.targets
            .map(c => (c, p.world2pix(c.coordinates)))
            .collect {
              case (SVGTarget.CircleTarget(_, css, radius, title), Some((x, y)))  =>
                val pointCss = Css("circle-target") |+| css
                <.circle(^.cx := x, ^.cy := y, ^.r := radius, pointCss, title.map(<.title(_)))
              case (SVGTarget.CrosshairTarget(_, css, side, title), Some((x, y))) =>
                val pointCss = Css("crosshair-target") |+| css
                <.g(
                  <.line(^.x1 := x - side, ^.x2 := x + side, ^.y1 := y, ^.y2        := y, pointCss),
                  <.line(^.x1 := x, ^.x2        := x, ^.y1        := y - side, ^.y2 := y + side, pointCss),
                  title.map(<.title(_))
                )
            }
            .toTagMod
        )
        svg
      }
}