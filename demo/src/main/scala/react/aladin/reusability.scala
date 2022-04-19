// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react.aladin

import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.Reusability._
import lucuma.ui.reusability._

object reusability {
  implicit val fovReuse: Reusability[Fov] = Reusability.derive

  implicit val pixelScaleReuse: Reusability[PixelScale] = {
    implicit val dr = Reusability.double(0.001)
    Reusability.by(x => (x.x, x.y))
  }
}
