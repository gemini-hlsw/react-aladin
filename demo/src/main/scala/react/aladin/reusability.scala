// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react.aladin

import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.Reusability._
import lucuma.ui.reusability._

object reusability {
  implicit def fovReuse(implicit dr: Reusability[Double]): Reusability[Fov] =
    Reusability.by(f => (f.x.toSignedDoubleDegrees, f.y.toSignedDoubleDegrees))

  implicit def pixelScaleReuse(implicit dr: Reusability[Double]): Reusability[PixelScale] =
    Reusability.by(x => (x.x, x.y))
}
