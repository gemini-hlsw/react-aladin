// Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package lucuma.react.aladin

import scala.annotation.nowarn
import scala.scalajs.js
import scala.scalajs.js.annotation.*

@js.native
@JSImport("/js/Color", JSImport.Namespace)
class AladinColor extends js.Object {}

@js.native
@JSImport("/js/ColorMap", JSImport.Namespace)
@nowarn
class ColorMap extends js.Object {
  def update(a: String): Unit = js.native
}

@js.native
@JSImport("/js/Footprint", JSImport.Namespace)
class AladinFootprint extends js.Object {}

@js.native
@JSImport("/js/Polyline", JSImport.Namespace)
class AladinPolyline extends js.Object {}

@js.native
@JSImport("/js/Circle", JSImport.Namespace)
class AladinCircle extends js.Object {}

@js.native
@JSImport("/js/HpxImageSurvey", JSImport.Namespace)
@nowarn
class HpxImageSurvey extends js.Object {
  def setAlpha(a: Double): Unit = js.native
  def getColorMap(): ColorMap = js.native
}
