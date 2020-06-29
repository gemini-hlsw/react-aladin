// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.implicits._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
// import japgolly.scalajs.react.vdom.{ svg_<^ => svg }
import react.aladin._
import react.common._
import scala.scalajs.js
// import org.scalajs.dom.ext._
// import org.scalajs.dom.document
// import org.scalajs.dom.Element
// import js.annotation._
import demo.GeomSvgDemo
import org.scalajs.dom.raw.CanvasRenderingContext2D
import gsp.math._
import gsp.math.syntax.all._

final case class TargetBody(
  ) extends ReactProps[TargetBody](TargetBody.component) {}

@js.native
trait SourceData extends js.Object {
  var name: String  = js.native
  var size: Double  = js.native
  var otype: String = js.native
}

object SourceData {
  def apply(name: String, size: Double, otype: String): SourceData = {
    val p = (new js.Object()).asInstanceOf[SourceData]
    p.name  = name
    p.size  = size
    p.otype = otype
    p
  }

}

object TargetBody {
  type Props = TargetBody

  protected implicit val propsReuse: Reusability[Props] = Reusability.derive

  val AladinComp = Aladin.component

  // val gmosArea = 400.arcseconds
  val gmosArea = (330340 / 1.4).toInt.mas

  class Backend(bs: BackendScope[Props, Unit]) {
    // Create a mutable reference
    private val ref = Ref.toScalaComponent(AladinComp)

    def includeSvg(v: JsAladin): Unit = {
      val (h, w) = (v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
      val svg    = GeomSvgDemo.generate(Size(h, w), v.pixelScale)
      v.getParentDiv().appendChild(svg.node_Svg)

      val drawFunction =
        (source: AladinSource, canvasCtx: CanvasRenderingContext2D, viewParams: SourceDraw) => {
          // println("canvas")
          canvasCtx.lineWidth = 3
          // println(v.size)
          // println(v.getFov)
          // println(v.pixelScale)
          // println("fov")
          // println(v.getFov()(0))
          // println(v.getFov()(1))
          val coords = v.getRaDec()
          val raO    = RightAscension.fromAngleExact.getOption(Angle.fromDoubleDegrees(coords(0)))
          val decO   = Declination.fromAngle.getOption(Angle.fromDoubleDegrees(coords(1)))
          (raO, decO).mapN { (ra, dec) =>
            val c             = Coordinates(ra, dec)
            val decCorrection = scala.math.cos(dec.toRadians)
            val offseted =
              c.offset(HourAngle.angle.reverseGet(gmosArea),
                       Angle.fromDoubleDegrees(gmosArea.toSignedDoubleDegrees * decCorrection))
            val offseted2 =
              c.offset(HourAngle.angle.reverseGet(-gmosArea),
                       -Angle.fromDoubleDegrees(gmosArea.toSignedDoubleDegrees * decCorrection))
            // println("offseted")
            // println(offseted)
            // println(offseted2)
            val pix1 = v.world2pix(offseted.ra.toAngle.toDoubleDegrees,
                                   offseted.dec.toAngle.toSignedDoubleDegrees)
            val pix = v.world2pix(offseted2.ra.toAngle.toDoubleDegrees,
                                  offseted2.dec.toAngle.toSignedDoubleDegrees)
            // val pix = v.world2pix(coords(0) - gmosArea.toDoubleDegrees,
            //                       coords(\\\s) - gmosArea.toDoubleDegrees * decCorrection)
            // canvasCtx.strokeRect(pix1(0), pixC
            // canvasCtx.strokeRect(0, 1, pix(0), pix(1))
            // println("coords")
            // println(s"corner: ${pix1(0)}, ${pix1(1)}")
            // println(s"size: ${pix(0) - pix1(0)}, ${pix(1) - pix1(1)}")
            canvasCtx.strokeRect(pix1(0), pix1(1), pix(0) - pix1(0), pix(1) - pix1(1))

          }
          ()
        }
      val cat = A.catalog(CatalogOptions(name = "Virgo Cluster", shape = drawFunction))

      val M87 = A.source(187.7059308, 12.3911233, SourceData("M 87", 4.5, "LINER AGN"))
      cat.addSources(js.Array(M87))

      // v.addCatalog(cat)
      println(cat)
      ()
      // val svg = document.createElement("svg")
      // println(svg)
      // svg.innerHTML = strSvg
      // e.insertBefore(strSvg, null)
      // e.insertAdjacentHTML("beforebegin", strSvg)
      // val svg = v.getParentDiv().getElementsByTagName("svg")
      // svg.foreach { e =>
      //   println(e)
      //   e.setAttribute("height", "452")
      //   e.setAttribute("width", "1854")
      //   e.setAttribute("class", "instrument-geom")
      //   e.setAttribute("z-index", "10")
      //   e.setAttribute("position", "absolute")
      // }
      // println("svg")
      // println(svg.length)
    }

    def render(props: Props) =
      React.Fragment(
        <.div(
          ^.height := "100%",
          ^.width := "100%",
          ^.cls := "check",
          AladinComp.withRef(ref) {
            // Aladin(target          = "0:00:00 0:00:00",
            Aladin(showReticle     = false,
                   target          = "M51",
                   fov             = 0.25,
                   showGotoControl = false,
                   customize       = includeSvg _)
          }
        )
        // <.div(
        //   ^.dangerouslySetInnerHtml := strSvg
        // ),
        // svg.<.svg(
        //   ^.src := strSvg
        // )
      )

  }

  val component =
    ScalaComponent
      .builder[Props]
      .renderBackend[Backend]
      .build

}
