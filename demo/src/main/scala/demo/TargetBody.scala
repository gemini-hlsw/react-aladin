// Copyright (c) 2016-2020 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import japgolly.scalajs.react._
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import react.aladin._
import react.common._
import react.sizeme._
import react.gridlayout._
import scala.scalajs.js
import gsp.math._

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
    p.name = name
    p.size = size
    p.otype = otype
    p
  }

}

final case class AladinTile(s: Size, c: Coordinates)
    extends ReactProps[AladinTile](AladinTile.component)

object AladinTile {
  type Props = AladinTile

  protected implicit val propsReuse: Reusability[Props] = Reusability.never

  val AladinComp = Aladin.component
  val targetH    = 16
  val targetW    = 12

  private val layoutLg: Layout = Layout(
    List(
      LayoutItem(x = 0, y = 0, w = targetW, h = 16, i = "target"),
      LayoutItem(x = 0, y = 8, w = 12, h = 8, i = "constraints")
    )
  )

  private val layoutMd: Layout = Layout(
    List(
      LayoutItem(x = 0, y = 0, w = targetW, h = 16, i = "target"),
      LayoutItem(x = 0, y = 8, w = 12, h = 8, i = "constraints")
    )
  )

  private val layouts: Map[BreakpointName, (JsNumber, JsNumber, Layout)] =
    Map(
      (BreakpointName.lg, (1200, 12, layoutLg)),
      (BreakpointName.md, (996, 10, layoutMd))
      // (BreakpointName.sm, (768, 8, layout)),
      // (BreakpointName.xs, (480, 6, layout))
    )

  class Backend(bs: BackendScope[Props, Unit]) {
    def render(props: Props) =
      SizeMe(monitorHeight = true) { s =>
        <.div(
          ^.height := "100%",
          ^.width := "100%",
          ResponsiveReactGridLayout(
            width = s.width,
            margin = (5, 5),
            containerPadding = (5, 5),
            rowHeight = 30,
            draggableHandle = ".tileTitle",
            useCSSTransforms = false, // Not ideal, but fixes flicker on first update (0.18.3).
            // onLayoutChange =
            //   (_, _) => ref.get.flatMapCB(_.backend.recalculateView) *> recalculateView,
            layouts = layouts
          )(
            <.div(
              ^.height := "100%",
              ^.width := "100%",
              ^.key := "target",
              ^.cls := "tile",
              SizeMe(monitorHeight = true) { s =>
                AladinContainer(s, props.c)
              }
            )
          )
        )
      }
  }

  val component =
    ScalaComponent
      .builder[Props]
      .renderBackend[Backend]
      .build

}

object TargetBody {
  type Props = TargetBody

  protected implicit val propsReuse: Reusability[Props] = Reusability.derive

  val component =
    ScalaComponent
      .builder[Props]
      .stateless
      .render { _ =>
        SizeMe() { s =>
          // AladinTile(s, Coordinates.fromHmsDms.getOption("17:16:56.411 -89:02:15.73").get)
          AladinTile(
            s,
            Coordinates.Zero
          )
        }.vdomElement
      }
      .build

}
