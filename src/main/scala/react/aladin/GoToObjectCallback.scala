package react.aladin

import scala.scalajs.js
import japgolly.scalajs.react.raw.JsNumber
import japgolly.scalajs.react.Callback

class GoToObjectCallback(succ: (JsNumber, JsNumber) => Callback, e: Callback) extends js.Object {
  val success: js.Function1[js.Array[JsNumber], Unit] = (raDec: js.Array[JsNumber]) => {
    succ(raDec(0), raDec(1)).runNow()
  }
  val error: js.Function0[Unit] = () => e.runNow()
}
