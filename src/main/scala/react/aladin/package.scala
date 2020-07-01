package react

import gsp.math.Angle
import react.common.EnumValue
import react.common.Size
import japgolly.scalajs.react.Callback

package aladin {

  /**
    * ALadin field of view angles horizontally and vertically
    *
    * @param x Horizontal (RA) field of view
    * @param y Vertical (Dec) field of view
    */
  final case class Fov(x: Angle, y: Angle)

  /**
    * Aladin pixel scala in degrees per pixel
    *
    * @param x
    * @param y
    */
  final case class PixelScale(x: Double, y: Double)

  sealed trait CooFrame extends Product with Serializable
  object CooFrame {
    implicit val enum: EnumValue[CooFrame] = EnumValue.toLowerCaseString
    case object J2000 extends CooFrame
    case object J2000d extends CooFrame
    case object Galactic extends CooFrame

    def fromString(s: String): Option[CooFrame] = s match {
      case "j2000"    => Some(J2000)
      case "j2000d"   => Some(J2000d)
      case "galactic" => Some(Galactic)
    }
  }
}

package object aladin {
  implicit class JsAladinOps(val a: JsAladin) extends AnyVal {
    def size: Size = Size(a.getSize()(0), a.getSize()(1))
    def fov: Fov =
      Fov(Angle.fromDoubleDegrees(a.getFov()(0)), Angle.fromDoubleDegrees(a.getFov()(1)))
    def onZoom(cb: Fov => Callback): Unit =
      a.on("zoomChanged", (_: Double) => cb(fov).runNow)
    def onZoomCB(cb: Callback): Unit =
      a.on("zoomChanged", (_: Double) => cb.runNow)
    def pixelScale: PixelScale =
      PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1))
  }
}
