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
import gsp.math.geom.svg.GeomSvgDemo
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
  // @js.native
  // @JSImport("star.svg", JSImport.Default)
  // val strSvg: String = js.native

  type Props = TargetBody

  protected implicit val propsReuse: Reusability[Props] = Reusability.derive

  val AladinComp = Aladin.component

  val gmosArea = 400.arcseconds

  class Backend(bs: BackendScope[Props, Unit]) {
    // Create a mutable reference
    private val ref = Ref.toScalaComponent(AladinComp)

    def includeSvg(v: JsAladin): Unit = {
      //v.getParentDiv()//.getElementsByClassName("aladin-imageCanvas").foreach { e =>
      println(v.getSize)
      println(v.getFov)
      val (h, w) = (v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
      val svg    = GeomSvgDemo.generate(Size(h, w))
      v.getParentDiv().appendChild(svg.node_Svg)

      val drawFunction =
        (source: AladinSource, canvasCtx: CanvasRenderingContext2D, viewParams: SourceDraw) => {
          println("canvas")
          canvasCtx.lineWidth = 3
          val coords = v.getRaDec()
          val raO    = RightAscension.fromAngleExact.getOption(Angle.fromDoubleDegrees(coords(0)))
          val decO   = Declination.fromAngle.getOption(Angle.fromDoubleDegrees(coords(1)))
          (raO, decO).mapN { (ra, dec) =>
            println("ra")
            println(ra)
            println(dec)
            val c = Coordinates(ra, dec)
            // val x1 = ra.toAngle - gmosArea
            // val y1 = dec.toAngle - gmosArea
            // println(RightAscension.fromAngleExact.getOption(x1))
            println(c.offset(HourAngle.angle.reverseGet(gmosArea), gmosArea))
            val decCorrection = scala.math.cos(dec.toRadians)
            println(decCorrection)
            val offseted =
              c.offset(HourAngle.angle.reverseGet(gmosArea),
                       Angle.fromDoubleDegrees(gmosArea.toSignedDoubleDegrees * decCorrection))
            // println(x1.toDoubleDegrees)
            // println(x1.toSignedDoubleDegrees)
            // val pix1 = v.world2pix(x1.toSignedDoubleDegrees, y1.toSignedDoubleDegrees)
            // val pix1 = v.world2pix(coords(0) + gmosArea.toDoubleDegrees,
            //                        coords(1) + gmosArea.toDoubleDegrees)
            val pix1 = v.world2pix(offseted.ra.toAngle.toDoubleDegrees,
                                   offseted.dec.toAngle.toSignedDoubleDegrees)
            val pix = v.world2pix(coords(0) - gmosArea.toDoubleDegrees,
                                  coords(1) - gmosArea.toDoubleDegrees * decCorrection)
            // val r = v.world2pix(0, 0)
            // println(r)
            // canvasCtx.globalAlpha = 0.7
            // canvasCtx.moveTo(0, 0)
            // println(v.
            println(s"${pix1(0)}, ${pix1(1)}, ${pix(0)}, ${pix(1)}")
            canvasCtx.strokeRect(pix1(0), pix1(1), pix(0) - pix1(0), pix(1) - pix1(1))

          }
          ()
        }
      val cat = A.catalog(CatalogOptions(name = "Virgo Cluster", shape = drawFunction))

      val M87 = A.source(187.7059308, 12.3911233, SourceData("M 87", 4.5, "LINER AGN"))
      cat.addSources(js.Array(M87))

      v.addCatalog(cat)
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
        <.div(^.height := "100%", ^.width := "100%", ^.cls := "check", AladinComp.withRef(ref) {
          Aladin(target = "M51", fov = 0.25, showGotoControl = false, customize = includeSvg _)
        })
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
