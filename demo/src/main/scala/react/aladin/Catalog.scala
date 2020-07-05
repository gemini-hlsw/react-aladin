package react.aladin

import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.|
import org.scalajs.dom.ext._
import org.scalajs.dom.html.Image
import org.scalajs.dom.CanvasRenderingContext2D
import japgolly.scalajs.react.raw.JsNumber

// This will be the props object used from JS-land
@js.native
trait CatalogOptions extends js.Object {
  var name: js.UndefOr[String]
  var color: js.UndefOr[String]
  var sourceSize: js.UndefOr[JsNumber]
  var shape: js.UndefOr[String | Image | CatalogOptions.RawDrawFunction]
  var limit: js.UndefOr[JsNumber]
  var raField: js.UndefOr[String]
  var decField: js.UndefOr[String]
  var displayLabel: js.UndefOr[String]
  var labelColor: js.UndefOr[String]
  var labelFont: js.UndefOr[String]
  var labelColumn: js.UndefOr[String]
  var onClick: js.UndefOr[String | CatalogOptions.RawOnClick]
}

object CatalogOptions {
  type RawOnClick      = js.Function1[AladinSource, Unit]
  type DrawFunction    = (AladinSource, CanvasRenderingContext2D, SourceDraw) => Unit
  type RawDrawFunction = js.Function3[AladinSource, CanvasRenderingContext2D, SourceDraw, Unit]
  type OnClick         = AladinSource => Unit

  def apply(
    name:         js.UndefOr[String] = js.undefined,
    color:        js.UndefOr[Color] = js.undefined,
    sourceSize:   js.UndefOr[JsNumber] = js.undefined,
    shape:        js.UndefOr[String | Image | DrawFunction] = js.undefined,
    limit:        js.UndefOr[JsNumber] = js.undefined,
    raField:      js.UndefOr[String] = js.undefined,
    decField:     js.UndefOr[String] = js.undefined,
    displayLabel: js.UndefOr[String] = js.undefined,
    labelColor:   js.UndefOr[String] = js.undefined,
    labelFont:    js.UndefOr[String] = js.undefined,
    labelColumn:  js.UndefOr[String] = js.undefined,
    onClick:      js.UndefOr[String | OnClick] = js.undefined
  ): CatalogOptions = {
    val p = (new js.Object()).asInstanceOf[CatalogOptions]
    p.name = name
    p.color = color.map(c => c: String)
    p.sourceSize = sourceSize
    p.shape = shape.map((_: Any) match {
      case s: String => s
      case i: Image  => i
      case f =>
        (
          (
            s: AladinSource,
            c: CanvasRenderingContext2D,
            p: SourceDraw
          ) => f.asInstanceOf[DrawFunction](s, c, p)
        ): RawDrawFunction
    })
    p.limit = limit
    p.raField = raField
    p.decField = decField
    p.displayLabel = displayLabel
    p.labelColor = labelColor
    p.labelFont = labelFont
    p.labelColumn = labelColumn
    p.onClick = onClick.map((_: Any) match {
      case s: String => s
      case r         => ((s: AladinSource) => r.asInstanceOf[OnClick](s)): RawOnClick
    })
    p
  }
}

@js.native
@JSImport("@cquiroz/aladin-lite/lib/js/Catalog", JSImport.Namespace)
class AladinCatalog extends js.Object {
  def addSources(s: AladinSource | js.Array[AladinSource]): Unit = js.native
}
