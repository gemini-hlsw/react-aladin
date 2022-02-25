// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react

import scala.scalajs.js

import japgolly.scalajs.react.Callback
import lucuma.core.math.Angle
import lucuma.core.math.Declination
import lucuma.core.math.RightAscension
import react.common._

package aladin {

  /**
   * ALadin field of view angles horizontally and vertically
   *
   * @param x
   *   Horizontal (RA) field of view
   * @param y
   *   Vertical (Dec) field of view
   */
  final case class Fov(x: Angle, y: Angle)

  /**
   * Aladin pixel scala in degrees per pixel
   *
   * @param x
   * @param y
   */
  final case class PixelScale(x: Double, y: Double)

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

  final case class PositionChanged(ra: RightAscension, dec: Declination, dragging: Boolean)

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
    val x: Int
    val y: Int
  }

  final case class MouseMoved(ra: RightAscension, dec: Declination, x: Int, y: Int)

  object MouseMoved   {
    def fromJs(p: JsMouseMoved): MouseMoved =
      MouseMoved(
        RightAscension.fromDoubleDegrees(p.ra),
        Declination.fromDoubleDegrees(p.dec).getOrElse(Declination.Zero),
        p.x,
        p.y
      )
  }

}

package object aladin {
  implicit class JsAladinOps(val a: JsAladin) extends AnyVal {
    def size: Size = Size(a.getSize()(0), a.getSize()(1))

    def fov: Fov =
      Fov(Angle.fromDoubleDegrees(a.getFov()(0)), Angle.fromDoubleDegrees(a.getFov()(1)))

    def onPositionChanged(cb: PositionChanged => Callback): Callback =
      Callback(
        a.on("positionChanged", (o: JsPositionChanged) => cb(PositionChanged.fromJs(o)).runNow())
      )

    def onZoom(cb: Fov => Callback): Callback =
      Callback(a.on("zoomChanged", (_: Double) => cb(fov).runNow()))

    def onZoom(cb: => Callback): Callback =
      Callback(a.on("zoomChanged", (_: Double) => cb.runNow()))

    def onFullScreenToggle(cb: Boolean => Callback): Callback =
      Callback(a.on("fullScreenToggled", (t: Boolean) => cb(t).runNow()))

    def onFullScreenToggle(cb: => Callback): Callback =
      Callback(a.on("fullScreenToggled", (_: Boolean) => cb.runNow()))

    def onMouseMove(cb: MouseMoved => Callback): Callback =
      Callback(a.on("mouseMove", (t: JsMouseMoved) => cb(MouseMoved.fromJs(t)).runNow()))

    def pixelScale: PixelScale =
      PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1))
  }

}
