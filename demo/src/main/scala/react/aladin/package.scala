package react

import gsp.math.Angle
// import gpp.svgdotjs.svgdotjsSvgJs.mod._
import japgolly.scalajs.react.Callback
import react.common._
// import cats.data.NonEmptyList
// import gsp.math.geom.ShapeExpression
// import gsp.math.geom.ShapeInterpreter
// import gsp.math.geom.jts.JtsShape
// import gsp.math.geom.svg2._
// import gsp.math.geom.svg2.implicits._
//
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

    def fromString(s: String): Option[CooFrame] =
      s match {
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
      a.on("zoomChanged", (_: Double) => cb(fov).runNow())
    def onZoomCB(cb: Callback): Unit =
      a.on("zoomChanged", (_: Double) => cb.runNow())
    def pixelScale: PixelScale =
      PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1))
  }

  /**
    * This method will build an svg appropriate to show on aladin.
    * Note we need to transform the svg to get the correct size and location
    * This particular method uses just svg but it doesn't properly work on Safari
    */
  // def shapesToSvg(
  //   shapes:      NonEmptyList[(String, ShapeExpression)],
  //   s:           Size,
  //   pixelScale:  PixelScale,
  //   scaleFactor: Int
  // )(implicit ev: ShapeInterpreter): Svg = {
  //   val svg: Svg = SVG_()
  //   // Render the svg
  //   shapes
  //     .map(x => x.copy(_2 = x._2.eval))
  //     .map {
  //       case (id, jts: JtsShape) => (id, jts)
  //       case x                   => sys.error(s"Whoa unexpected shape type: $x")
  //     }
  //     .toSvg(svg, pp, scalingFn = scalingFn)
  //
  //   // Viewbox size
  //   val (h, w) = (svg.viewbox().height_Box, svg.viewbox().width_Box)
  //   val (x, y) = (svg.viewbox().x_Box, svg.viewbox().y_Box)
  //   // Angular size of the geometry
  //   val hAngle = Angle.fromMicroarcseconds((h.toLong * scaleFactor).toLong)
  //   val wAngle = Angle.fromMicroarcseconds((w.toLong * scaleFactor).toLong)
  //   // Deltas to calculate the size of the svg on aladin scale
  //   val dx = (wAngle.toDoubleDegrees * pixelScale.x)
  //   val dy = (hAngle.toDoubleDegrees * pixelScale.y)
  //
  //   val svgSize = Size(dy, dx)
  //
  //   // Translation coordinates
  //   val tx = abs(dx * x / w)
  //   val ty = abs(dy * y / h)
  //
  //   // Cross at 0,0 style it with css
  //   svg
  //     .line(-10 * dx, -10 * dx, 10 * dx, 10 * dx)
  //     .attr("class", "jts-svg-center")
  //   svg
  //     .line(-10 * dx, 10 * dx, 10 * dx, -10 * dx)
  //     .attr("class", "jts-svg-center")
  //
  //   // Border to the whole svg, usually hidden
  //   svg
  //     .rect(w, h)
  //     .translate(x, y)
  //     .fill("none")
  //     .attr("class", "jts-svg-border")
  //
  //   // Rotation reference point. It is a bit surprising but it is in screen coordinates
  //   val ry = ty - dy / 2
  //   // Scale and postion the center in the right location
  //   val transformation =
  //     new Matrix()
  //       .scale(1, -1, 0, ry) // Order of operations is important
  //       .translate(s.width.toDouble / 2 - tx, s.height.toDouble / 2 - ty)
  //   svg.transform(transformation)
  //   svg.size(svgSize)
  //   svg
  // }
}
