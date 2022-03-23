// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import cats.implicits._
import cats.data.Validated.Valid
import japgolly.scalajs.react.ReactMonocle._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.math._
import lucuma.core.enum.CatalogName
import monocle.Lens
import monocle.macros.GenLens
import org.scalajs.dom.document
import org.scalajs.dom.Element
import react.aladin._
import react.aladin.reusability._
import react.common._
import scala.annotation.nowarn
import lucuma.catalog._
import lucuma.ui.reusability._
import lucuma.svgdotjs.Svg
import org.http4s.client.Client
import org.http4s.Method._
import org.http4s.Uri
import org.http4s.syntax.all._
import org.http4s.client.dsl.io._
import cats.effect.IO
import fs2.text
import japgolly.scalajs.react.util._
import lucuma.core.geom.jts.interpreter._
import lucuma.core.geom.ShapeInterpreter
import lucuma.core.syntax.string._
import monocle.Focus

final case class AladinContainer(
  s:           Size,
  coordinates: Coordinates,
  client:      Client[IO]
) extends ReactFnProps[AladinContainer](AladinContainer.component) {
  val aladinCoordsStr: String = Coordinates.fromHmsDms.reverseGet(coordinates)
}

object AladinContainer {
  type Props = AladinContainer

  val AladinComp = Aladin.component

  val coordinates = GenLens[AladinContainer](_.coordinates)

  /**
   * On the state we keep the svg to avoid recalculations during panning
   */
  final case class State(
    gs: List[Coordinates]
  )

  implicit val propsReuse: Reusability[Props] =
    Reusability.by_== // by(p => (p.aladinCoordsStr, p.s.width.toDouble, p.s.height.toDouble))
  implicit val stateReuse: Reusability[State] =
    Reusability.by(s => (s.gs.length))

  object State {
    val Zero: State = State(Nil)

    val gs: Lens[State, List[Coordinates]] = Focus[State](_.gs)

  }

  implicit class ConeSearchCatalogQueryOps(val c: ConeSearchCatalogQuery) extends AnyVal {

    def adqlGeom(implicit ev: CatalogQueryInterpreter): String = {
      val (p1, _, p3, _) = c.radiusConstraint.eval(ev.shapeInterpreter).boundingBox
      val halfP          =
        Offset.p.andThen(Offset.P.angle).modify(x => Angle.microarcseconds.modify(_ / 2)(x))
      val halfQ          =
        Offset.q.andThen(Offset.Q.angle).modify(x => Angle.microarcseconds.modify(_ / 2)(x))
      val center         = p1 - p3
      val box            = halfP(halfQ(p1)) - halfP(halfQ(p3))
      println(center)
      println(box.p.toAngle.toDoubleDegrees)
      println(box.q.toAngle.toSignedDoubleDegrees)
      // println(p1.q.toAngle.toSignedDoubleDegrees)
      // println(p3.q.toAngle.toSignedDoubleDegrees)
      // println((p1 - p3).q.toAngle.toSignedDoubleDegrees)
      println(center.q.toAngle.toSignedDoubleDegrees)
      f"BOX('ICRS', ${c.base.ra.toAngle.toDoubleDegrees}%9.8f, ${c.base.dec.toAngle.toSignedDoubleDegrees}%9.8f, ${center.p.toAngle.toDoubleDegrees}%9.8f, ${center.q.toAngle.toSignedDoubleDegrees.abs}%9.8f)"
    }
  }

  sealed trait GaiaBackend {
    val MaxResultCount              = 5
    val ProperMotionLimitMasYr: Int = 100

    val gaia = CatalogAdapter.GaiaAdapter

    def adql(cs: ConeSearchCatalogQuery)(implicit ci: CatalogQueryInterpreter): String = {
      //
      val fields    = gaia.allFields.map(_.id.value.toLowerCase).mkString(",")
      //       |     WHERE CONTAINS(POINT('ICRS',${gaia.raField.id},${gaia.decField.id}),CIRCLE('ICRS', ${cs.base.ra.toDegrees}%9.8f, ${cs.base.dec.toDegrees}%9.8f, ${cs.radiusConstraint.maxLimit.toDegrees}%9.8f))=1
      //       |       AND (${gaia.plxField.id} > 0)
      //       |       AND (${gaia.gMagField.id} BETWEEN $BrightLimit AND $FaintLimit)
      //       |       AND (${gaia.bpRpField.id} IS NOT NULL)
      //       |       AND (SQRT(POWER(${gaia.pmRaField.id}, 2.0) + POWER(${gaia.pmDecField.id}, 2.0)) < ${ProperMotionLimitMasYr})
      // println(cs.radiusConstraint)
      val shapeAdql = cs.adqlGeom

      f"""|SELECT TOP ${ci.MaxCount} $fields, DISTANCE(POINT(${cs.base.ra.toAngle.toDoubleDegrees}%9.8f, ${cs.base.dec.toAngle.toSignedDoubleDegrees}%9.8f), POINT(ra, dec)) AS ang_sep
        |     FROM gaiadr2.gaia_source
        |     WHERE CONTAINS(POINT('ICRS',${gaia.raField.id},${gaia.decField.id}),$shapeAdql)=1
        |  ORDER BY ang_sep ASC
      """.stripMargin
    }

  }

  object GaiaBackend extends GaiaBackend

  class Backend($ : BackendScope[Props, State]) {
    implicit val ci = new CatalogQueryInterpreter {
      val MaxCount         = 5
      val shapeInterpreter = implicitly[ShapeInterpreter]
    }

    /**
     * Takes a catalog query and builds a list of urls to do the actual query
     */
    def queryUri[F[_]](
      query:       CatalogQuery
    )(implicit ci: CatalogQueryInterpreter): Option[Uri] =
      query match {
        case NameCatalogQuery(_) =>
          None

        case c: ConeSearchCatalogQuery =>
          Some(
            uri"https://lucuma-cors-proxy.herokuapp.com/https://gea.esac.esa.int/tap-server/tap/sync"
              .withQueryParam("REQUEST", "doQuery")
              .withQueryParam("LANG", "ADQL")
              .withQueryParam("FORMAT", "votable_plain")
              .withQueryParam("QUERY", GaiaBackend.adql(c))
          )
      }

    // def setWorldPixFn: Callback =
    //   world2pixFn.flatMapCB { f =>
    //     $.setStateL(State.world2pix)(Some(f))
    //   }
    //
    // def loadGuideStars: Callback =
    //   $.props.flatMap { props =>
    //     val patrolFieldPos =
    //       props.coordinates.offset(HourAngle.angle.reverseGet(-GmosGeometry.offsetPos.p.toAngle),
    //                                GmosGeometry.offsetPos.q.toAngle
    //       )
    //     Callback(
    //       implicitly[Effect.Dispatch[IO]].dispatch(
    //         queryUri(
    //           ConeSearchCatalogQuery(patrolFieldPos,
    //                                  GmosGeometry.fullPatrolField,
    //                                  Nil,
    //                                  CatalogName.Gaia
    //           )
    //         ).map { url =>
    //           val request = GET(url)
    //           props.client
    //             .stream(request)
    //             .flatMap(
    //               _.body
    //                 .through(text.utf8.decode)
    //                 .through(CatalogSearch.targets[IO](CatalogName.Gaia))
    //             )
    //             // .evalTap(t => IO.println(t))
    //             .compile
    //             .toList
    //             .flatMap { r =>
    //               val u = r.collect { case Valid(v) =>
    //                 v.target.tracking.baseCoordinates
    //               }
    //               IO($.setStateL(State.gs)(u).runNow())
    //             }
    //         // .drain
    //         }.getOrElse(IO.unit)
    //       )
    //     )
    //   }

    // def updateVisualization(v: JsAladin): Callback =
    //   $.state.flatMap(s => s.svg.map(updateVisualization(_)(v)).getOrEmpty)

    // def render(props: Props, state: State) = {
    //
    //   val points =
    //     state.world2pix.toList.flatMap(state.gs.map).collect { case Some((a, b)) => (a, b) }
    //   <.div(
    //     ^.cls := "top-container",
    //     <.label("p", ^.htmlFor := "p_select"),
    //     <.select(^.id          := "p_select", <.option("-60"), <.option("0"), <.option("60")),
    //     <.label("q", ^.htmlFor := "q_select"),
    //     <.select(^.id          := "q_select", <.option("-60"), <.option("0"), <.option("60")),
    //     React.Fragment(
    //       AGSCanvas(props.s, points),
    //       AladinComp.withRef(aladinRef) {
    //         Aladin(
    //           Css("react-aladin"),
    //           showReticle = false,
    //           showFullscreenControl = true,
    //           // showLayersControl = true,
    //           // showZoomControl = false,
    //           target = props.aladinCoordsStr,
    //           // target = "ngc 1055",
    //           fov = 0.25,
    //           showGotoControl = false,
    //           customize = includeSvg _
    //         )
    //       }
    //     )
    //   )
    // }
    //
    // val world2pixFn: CallbackOption[Coordinates => Option[(Double, Double)]] =
    //   aladinRef.get.asCBO.flatMapCB(r => r.backend.world2pixFn)

  }

  def updateVisualization(svg: Svg, off: (Double, Double))(v: JsAladin): Callback = {
    val size = Size(v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
    val div  = v.getParentDiv()
    renderVisualization(svg, off, div, size, v.pixelScale)
  }

  def renderVisualization(
    svg:        Svg,
    offset:     (Double, Double),
    div:        Element,
    size:       => Size,
    pixelScale: => PixelScale
  ): Callback =
    Callback {
      val (x, y)   = offset
      // Delete any viz previously rendered
      val previous = Option(div.querySelector(".aladin-visualization"))
      previous.foreach(div.removeChild)
      val g        = document.createElement("div")
      g.classList.add("aladin-visualization")
      visualization.geometryForAladin(svg, g, size, pixelScale, GmosGeometry.ScaleFactor, (x, y))
      // Include visibility on the dom
      div.appendChild(g)
    }

  val component =
    ScalaFnComponent
      .withHooks[Props]
      .useRefToScalaComponent(AladinComp)
      .useState(Offset.Zero)
      .useState(Fov(Angle.fromDoubleDegrees(0.25), Angle.fromDoubleDegrees(0.25)))
      .useState(none[Coordinates => Option[(Double, Double)]])
      .useMemoBy((_, _, o, f, _) => (o, f))((_, _, _, _, _) => { case (offset, fov) =>
        println(offset.value)
        println(fov.value)
        visualization
          .shapesToSvg(GmosGeometry.shapes(offset.value), GmosGeometry.pp, GmosGeometry.ScaleFactor)
      })
      .useEffectWithDepsBy((_, ref, o, _, _, _) => (o, Option(ref.raw.current).isDefined)) {
        (_, ref, _, _, w, _) => _ =>
          ref.get.asCBO.flatMapCB(r => r.backend.world2pixFn.flatMap(x => w.setState(x.some)))
      }
      .useEffectBy { (p, ref, _, _, w, svg) =>
        w.value
          .flatMap(_(p.coordinates))
          .map(off =>
            ref.get.asCBO
              .flatMapCB(v => v.backend.runOnAladinCB(updateVisualization(svg, off)))
              .toCallback
          )
          .getOrEmpty
      }
      .useState(State.Zero)
      .render { (props, aladinRef, offset, fov, world2pix, svg, state) =>
        /**
         * Called when the position changes, i.e. aladin pans. We want to offset the visualization
         * to keep the internal target correct
         */
        @nowarn
        def onPositionChanged(v: JsAladin)(
          s:                     PositionChanged
        ): Callback = {
          val size     = Size(v.getParentDiv().clientHeight, v.getParentDiv().clientWidth)
          val div      = v.getParentDiv()
          // Update the existing visualization in place
          val previous = Option(div.querySelector(".aladin-visualization"))
          (svg.some, previous).mapN { case (svg, previous) =>
            aladinRef.get.asCBO
              .flatMapCB(
                _.backend.world2pix(props.coordinates)
              )
              .flatMapCB { off =>
                Callback {
                  // Offset the visualization
                  visualization
                    .updatePosition(svg,
                                    previous,
                                    size,
                                    v.pixelScale,
                                    GmosGeometry.ScaleFactor,
                                    off.getOrElse((0, 0))
                    )
                }
              }
              .toCallback
          }.getOrEmpty
        }

        def includeSvg(v: JsAladin): Callback =
          v.onZoom(onZoom(v)) *>                      // re render on zoom
            v.onPositionChanged(onPositionChanged(v)) // *>

        def onZoom = (v: JsAladin) => fov.setState(v.fov)

        val points =
          world2pix.value.toList.flatMap(state.value.gs.map).collect { case Some((a, b)) =>
            (a, b)
          }

        def changeQOffset(e: ReactEventFromInput) =
          e.target.value.parseDoubleOption
            .map(x => offset.modState(Offset.qAngle.replace(Angle.fromDoubleArcseconds(x))))
            .getOrEmpty

        def changePOffset(e: ReactEventFromInput) =
          e.target.value.parseDoubleOption
            .map(x => offset.modState(Offset.pAngle.replace(Angle.fromDoubleArcseconds(x))))
            .getOrEmpty

        <.div(
          ^.cls := "top-container",
          <.label("p", ^.htmlFor := "p_select"),
          <.select(
            ^.id                 := "p_select",
            ^.onChange ==> changePOffset,
            ^.value              := (Angle.arcseconds.get(Offset.pAngle.get(offset.value))),
            <.option("-60"),
            <.option("0"),
            <.option("60")
          ),
          <.label("q", ^.htmlFor := "q_select"),
          <.select(
            ^.id                 := "q_select",
            ^.onChange ==> changeQOffset,
            ^.value              := (Angle.arcseconds.get(Offset.qAngle.get(offset.value))),
            <.option("-60"),
            <.option("0"),
            <.option("60")
          ),
          React.Fragment(
            AGSCanvas(props.s, points),
            AladinComp.withRef(aladinRef) {
              Aladin(
                Css("react-aladin"),
                showReticle = false,
                showFullscreenControl = true,
                // showLayersControl = true,
                // showZoomControl = false,
                target = props.aladinCoordsStr,
                // target = "ngc 1055",
                fov = fov.value.x,
                showGotoControl = false,
                customize = includeSvg _
              )
            }
          )
        )

      }
}
