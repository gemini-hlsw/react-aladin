package react.aladin

import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.|

@js.native
@JSImport("~resources/aladin-lite/js/Overlay", JSImport.Namespace)
class AladinOverlay extends js.Object {
  def addFootprints(s: js.Array[AladinOverlay.Shapes]): Unit = js.native
  def add(s:           AladinOverlay.Shapes): Unit           = js.native
}

object AladinOverlay {
  type Shapes = AladinCircle | AladinFootprint | AladinPolyline
}
