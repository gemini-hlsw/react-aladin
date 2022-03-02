// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import scala.scalajs.js

import japgolly.scalajs.react._
import cats.syntax.all._
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.math._
import react.aladin._
import react.common._
import react.gridlayout._
import react.resizeDetector._
import react.resizeDetector.hooks._
import scala.annotation.nowarn

final case class TargetBody(
) extends ReactFnProps[TargetBody](TargetBody.component) {}

@js.native
@nowarn
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
    extends ReactFnProps[AladinTile](AladinTile.component)

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

  private val layouts: Map[BreakpointName, (Int, Int, Layout)] =
    Map(
      (BreakpointName.lg, (1200, 12, layoutLg)),
      (BreakpointName.md, (996, 10, layoutMd))
      // (BreakpointName.sm, (768, 8, layout)),
      // (BreakpointName.xs, (480, 6, layout))
    )

  val component =
    ScalaFnComponent
      .withHooks[Props]
      .useResizeDetector()
      .renderWithReuse { (props, s) =>
        <.div(
          ^.height := "100%",
          ^.width  := "100%",
          ResponsiveReactGridLayout(
            width = s.width.orEmpty,
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
              ^.width  := "100%",
              ^.key    := "target",
              ^.cls    := "tile",
              ResizeDetector() { s =>
                AladinContainer(Size(s.height.orEmpty, s.width.orEmpty), props.c)
              }
            )
          )
        )
      }

}

object TargetBody {
  type Props = TargetBody

  protected implicit val propsReuse: Reusability[Props] = Reusability.derive

  val component =
    ScalaFnComponent
      .withHooks[TargetBody]
      .useResizeDetector()
      .renderWithReuse { (_, s) =>
        AladinTile(
          Size(s.height.orEmpty, s.width.orEmpty),
          Coordinates.Zero
        )
      }

}
