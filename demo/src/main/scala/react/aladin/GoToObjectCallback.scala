// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react.aladin

import scala.scalajs.js

import japgolly.scalajs.react.Callback
import japgolly.scalajs.react.facade.JsNumber

class GoToObjectCallback(succ: (JsNumber, JsNumber) => Callback, e: Callback) extends js.Object {
  val success: js.Function1[js.Array[JsNumber], Unit] = (raDec: js.Array[JsNumber]) => {
    succ(raDec(0), raDec(1)).runNow()
  }
  val error: js.Function0[Unit]                       = () => e.runNow()
}
