package react.aladin

import scala.scalajs.js
import scala.scalajs.js.annotation._
import japgolly.scalajs.react.raw.JsNumber

@js.native
@JSImport("js/Color", JSImport.Namespace)
class AladinColor extends js.Object {}

@js.native
@JSImport("js/ColorMap", JSImport.Namespace)
class ColorMap extends js.Object {
  def update(a: String): Unit = js.native
}

@js.native
@JSImport("js/Footprint", JSImport.Namespace)
class AladinFootprint extends js.Object {}

@js.native
@JSImport("js/Polyline", JSImport.Namespace)
class AladinPolyline extends js.Object {}

@js.native
@JSImport("js/Circle", JSImport.Namespace)
class AladinCircle extends js.Object {}

@js.native
@JSImport("js/HpxImageSurvey", JSImport.Namespace)
class HpxImageSurvey extends js.Object {
  def setAlpha(a: JsNumber): Unit = js.native
  def getColorMap(): ColorMap = js.native
}
