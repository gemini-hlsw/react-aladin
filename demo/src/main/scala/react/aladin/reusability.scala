// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react.aladin

import japgolly.scalajs.react.Reusability
import japgolly.scalajs.react.Reusability.*
import lucuma.core.math.Angle
import lucuma.ui.reusability.given

object reusability {
  def microArcsecondsFovReuse(tolerance: Angle): Reusability[Fov] =
    Reusability.apply { case (a: Fov, b: Fov) =>
      (a.x - b.x).toMicroarcseconds < tolerance.toMicroarcseconds && (a.y - b.y).toMicroarcseconds < tolerance.toMicroarcseconds
    }

  val exactFovReuse: Reusability[Fov] = Reusability.derive

  def pixelScaleReuse(implicit
    dr: Reusability[Double] = Reusability.double(0.001)
  ): Reusability[PixelScale] =
    Reusability.by(x => (x.x, x.y))
}
