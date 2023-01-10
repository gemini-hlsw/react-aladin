// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react

import japgolly.scalajs.react.Callback
import lucuma.core.math.Angle
import lucuma.core.math.Declination
import lucuma.core.math.RightAscension
import react.common._

import scala.scalajs.js

package aladin {

  /**
   * ALadin field of view angles horizontally and vertically
   *
   * @param x
   *   Horizontal (RA) field of view
   * @param y
   *   Vertical (Dec) field of view
   */
  case class Fov(x: Angle, y: Angle)

  object Fov {
    def square(a: Angle): Fov = Fov(a, a)
  }

  /**
   * Aladin pixel scala in degrees per pixel
   *
   * @param x
   * @param y
   */
  case class PixelScale(x: Double, y: Double)

  object PixelScale {
    val Default: PixelScale = PixelScale(1, 1)
  }

  sealed trait CooFrame extends Product with Serializable
  object CooFrame {
    implicit val enumCooFrame: EnumValue[CooFrame] = EnumValue.toLowerCaseString
    case object J2000    extends CooFrame
    case object J2000d   extends CooFrame
    case object Galactic extends CooFrame

    def fromString(s: String): Option[CooFrame] =
      s match {
        case "j2000"    => Some(J2000)
        case "j2000d"   => Some(J2000d)
        case "galactic" => Some(Galactic)
      }
  }

  @js.native
  trait JsPositionChanged extends js.Object {
    val ra: Double
    val dec: Double
    val dragging: Boolean
  }

  case class PositionChanged(ra: RightAscension, dec: Declination, dragging: Boolean)

  object PositionChanged {
    def fromJs(p: JsPositionChanged): PositionChanged =
      PositionChanged(
        RightAscension.fromDoubleDegrees(p.ra),
        Declination.fromDoubleDegrees(p.dec).getOrElse(Declination.Zero),
        p.dragging
      )
  }

  @js.native
  trait JsMouseMoved extends js.Object {
    val ra: Double
    val dec: Double
    val x: Double
    val y: Double
  }

  case class MouseMoved(ra: RightAscension, dec: Declination, x: Double, y: Double)

  object MouseMoved {
    def fromJs(p: JsMouseMoved): MouseMoved =
      MouseMoved(
        RightAscension.fromDoubleDegrees(p.ra),
        Declination.fromDoubleDegrees(p.dec).getOrElse(Declination.Zero),
        p.x,
        p.y
      )
  }

}
