package react.aladin

import cats.data.NonEmptyMap
import cats.implicits._
import gpp.svgdotjs.svgdotjsSvgJs.mod.{ Element => _, _ }
import gsp.math.geom.svg._
import gsp.math.geom.svg.implicits._
import gsp.math.Angle
import gsp.math.geom.jts.JtsShape
import gsp.math.geom.ShapeExpression
import gsp.math.geom.ShapeInterpreter
import org.scalajs.dom.raw.Element
import react.common._
import scala.math._

package object visualization {
  implicit class SvgOps(val svg: Svg) extends AnyVal {
    def setSize(s: Size): Svg = svg.size(s.width.toDouble, s.height.toDouble)
  }

  implicit val renderJtsShapeMapOfIds: RenderSvg[NonEmptyMap[String, JtsShape]] =
    new RenderSvg[NonEmptyMap[String, JtsShape]] {
      def toSvg(
        base:      Container,
        pp:        SvgPostProcessor,
        scalingFn: ScalingFn,
        a:         NonEmptyMap[String, JtsShape]
      ): Container = {
        // Safari doesn't support transformations on the svg directly, but it can transfor a group below it
        val containerGroup = base.group()

        containerGroup.addClass("jts-root-group")
        // We should calculate the viewbox of the whole geometry
        val composite = a.toNonEmptyList.map(_.g).reduce(geometryUnionSemigroup)
        a.toNel.map {
          case (id, g) =>
            val c = g.toSvg(containerGroup, pp, scalingFn)
            // Set an id per geometry
            c.id(id)
        }
        val envelope = composite.getBoundary.getEnvelopeInternal
        base.viewbox(scalingFn(envelope.getMinX),
                     scalingFn(envelope.getMinY),
                     scalingFn(envelope.getWidth),
                     scalingFn(envelope.getHeight)
        )
        // Note the svg is reversed on y but we'll let clients do the flip
        base
      }
    }

  val pp: SvgPostProcessor = {
    case p: Polygon   => p.addClass("jts-polygon")
    case g: G         => g.addClass("jts-group")
    case c: Container => c.addClass("jts")
    case a            => a
  }

  def geometryForAladin(
    shapes:      NonEmptyMap[String, ShapeExpression],
    parent:      Element,
    s:           Size,
    pixelScale:  PixelScale,
    scaleFactor: Int
  )(implicit si: ShapeInterpreter): Element = {
    val scalingFn: ScalingFn = _ / scaleFactor

    val svgBase: Svg = SVG_()
    // Render the svg
    val evaldShapes = shapes
      .map(_.eval)
      .toSortedMap
      .map {
        case (id, jts: JtsShape) => (id, jts)
        case x                   => sys.error(s"Whoa unexpected shape type: $x")
      }

    // Unsafe call but we know the map is non empty
    NonEmptyMap
      .fromMapUnsafe(evaldShapes)
      .toSvg(svgBase, pp, scalingFn = scalingFn)
    // Viewbox size
    val (h, w) = (svgBase.viewbox().height_Box, svgBase.viewbox().width_Box)
    val (x, y) = (svgBase.viewbox().x_Box, svgBase.viewbox().y_Box)

    // Transform the group at the root of the svg
    svgBase.children().each { (svg: Container) =>
      // Angular size of the geometry
      val hAngle = Angle.fromMicroarcseconds((h.toLong * scaleFactor).toLong)
      val wAngle = Angle.fromMicroarcseconds((w.toLong * scaleFactor).toLong)
      // Deltas to calculate the size of the svg on aladin scale
      val dx = wAngle.toDoubleDegrees * pixelScale.x
      val dy = hAngle.toDoubleDegrees * pixelScale.y

      val svgSize = Size(dy, dx)

      // Translation coordinates
      val tx = abs(dx * x / w)
      val ty = abs(dy * y / h)

      // Cross at 0,0 style it with css
      svg
        .line(-10 * dx, -10 * dx, 10 * dx, 10 * dx)
        .attr("class", "jts-svg-center")
      svg
        .line(-10 * dx, 10 * dx, 10 * dx, -10 * dx)
        .attr("class", "jts-svg-center")

      // Border to the whole svg, usually hidden
      svg
        .rect(w, h)
        .translate(x, y)
        .fill("none")
        .attr("class", "jts-svg-border")

      // Rotation reference point. It is a bit surprising but it is in screen coordinates
      val ry = ty - dy / 2
      // Flip the svg, note we should flip around ry but that creates troubles with the viewbox
      // Instead we adjust the top attribute
      svg.scale(1, -1)
      svgBase.setSize(svgSize)
      // To workaround Safari we set the position of the surrounding div rather than the svg
      parent.setAttribute(
        "style",
        s"position: absolute; left:${s.width.toDouble / 2 - tx}px; top: ${s.height.toDouble / 2 - ty + 2 * ry}px"
      )
    }
    parent.appendChild(svgBase.node_Svg)
    parent
  }

  /**
    * This method will build an svg appropriate to show on aladin.
    * Note we need to transform the svg to get the correct size and location
    * This particular method uses just svg but it doesn't properly work on Safari
    */
  def geometryForAladin(
    shapes:      NonEmptyMap[String, ShapeExpression],
    s:           Size,
    pixelScale:  PixelScale,
    scaleFactor: Int
  )(implicit si: ShapeInterpreter): Svg = {
    val scalingFn: ScalingFn = _ / scaleFactor
    val svg: Svg             = SVG_()
    // Render the svg
    val evaldShapes = shapes
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

    // Viewbox size
    val (h, w) = (svg.viewbox().height_Box, svg.viewbox().width_Box)
    val (x, y) = (svg.viewbox().x_Box, svg.viewbox().y_Box)
    // Angular size of the geometry
    val hAngle = Angle.fromMicroarcseconds((h.toLong * scaleFactor).toLong)
    val wAngle = Angle.fromMicroarcseconds((w.toLong * scaleFactor).toLong)
    // Deltas to calculate the size of the svg on aladin scale
    val dx = wAngle.toDoubleDegrees * pixelScale.x
    val dy = hAngle.toDoubleDegrees * pixelScale.y

    val svgSize = Size(dy, dx)

    // Translation coordinates
    val tx = abs(dx * x / w)
    val ty = abs(dy * y / h)

    // Cross at 0,0 style it with css
    svg
      .line(-10 * dx, -10 * dx, 10 * dx, 10 * dx)
      .attr("class", "jts-svg-center")
    svg
      .line(-10 * dx, 10 * dx, 10 * dx, -10 * dx)
      .attr("class", "jts-svg-center")

    // Border to the whole svg, usually hidden
    svg
      .rect(w, h)
      .translate(x, y)
      .fill("none")
      .attr("class", "jts-svg-border")

    // Rotation reference point. It is a bit surprising but it is in screen coordinates
    val ry = ty - dy / 2
    // Scale and postion the center in the right location
    val transformation =
      new Matrix()
        .scale(1, -1, 0, ry) // Order of operations is important
        .translate(s.width.toDouble / 2 - tx, s.height.toDouble / 2 - ty)
    svg.transform(transformation)
    svg.setSize(svgSize)
    svg
  }
}
