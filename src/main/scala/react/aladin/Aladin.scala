package react.aladin

import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.|
import org.scalajs.dom.ext._
import org.scalajs.dom.html.Image
import org.scalajs.dom.CanvasRenderingContext2D
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.raw.JsNumber
import react.common._

// This will be the props object used from JS-land
@js.native
trait SourceDraw extends js.Object {
  val fov: js.Array[JsNumber]
  val width: JsNumber
  val height: JsNumber
}

// This will be the props object used from JS-land
@js.native
trait AladinProps extends js.Object {
  var fov: js.UndefOr[JsNumber]
  var target: js.UndefOr[String]
  var survey: js.UndefOr[String]
  var cooFrame: js.UndefOr[String]
  var showReticle: js.UndefOr[Boolean]
  var showZoomControl: js.UndefOr[Boolean]
  var showFullscreenControl: js.UndefOr[Boolean]
  var showLayersControl: js.UndefOr[Boolean]
  var showGotoControl: js.UndefOr[Boolean]
  var showShareControl: js.UndefOr[Boolean]
  var showSimbadPointerControl: js.UndefOr[Boolean]
  var showFrame: js.UndefOr[Boolean]
  var fullScreen: js.UndefOr[Boolean]
  var reticleColor: js.UndefOr[String]
  var reticleSize: js.UndefOr[JsNumber]
  var imageSurvey: js.UndefOr[String]
  var baseImageLayer: js.UndefOr[String]
  var customize: js.UndefOr[JsAladin => Unit]
}

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

// This will be the props object used from JS-land
@js.native
trait OverlayOptions extends js.Object {
  var color: js.UndefOr[String]
  var name: js.UndefOr[String]
  var lineWidth: js.UndefOr[JsNumber]
}

object OverlayOptions {
  def apply(
    name:      js.UndefOr[String]   = js.undefined,
    color:     js.UndefOr[Color]    = js.undefined,
    lineWidth: js.UndefOr[JsNumber] = js.undefined
  ): OverlayOptions = {
    val p = (new js.Object()).asInstanceOf[OverlayOptions]
    p.name      = name
    p.color     = color.map(c => c: String)
    p.lineWidth = lineWidth
    p
  }
}

@js.native
trait PolylineOptions extends js.Object {
  var color: js.UndefOr[String]
}

object PolylineOptions {
  def apply(
    color: js.UndefOr[Color] = js.undefined
  ): PolylineOptions = {
    val p = (new js.Object()).asInstanceOf[PolylineOptions]
    p.color = color.map(c => c: String)
    println(p.color)
    p
  }
}

object CatalogOptions {
  type RawOnClick      = js.Function1[AladinSource, Unit]
  type DrawFunction    = (AladinSource, CanvasRenderingContext2D, SourceDraw) => Unit
  type RawDrawFunction = js.Function3[AladinSource, CanvasRenderingContext2D, SourceDraw, Unit]
  type OnClick         = AladinSource => Unit

  def apply(
    name:         js.UndefOr[String]                        = js.undefined,
    color:        js.UndefOr[Color]                         = js.undefined,
    sourceSize:   js.UndefOr[JsNumber]                      = js.undefined,
    shape:        js.UndefOr[String | Image | DrawFunction] = js.undefined,
    limit:        js.UndefOr[JsNumber]                      = js.undefined,
    raField:      js.UndefOr[String]                        = js.undefined,
    decField:     js.UndefOr[String]                        = js.undefined,
    displayLabel: js.UndefOr[String]                        = js.undefined,
    labelColor:   js.UndefOr[String]                        = js.undefined,
    labelFont:    js.UndefOr[String]                        = js.undefined,
    labelColumn:  js.UndefOr[String]                        = js.undefined,
    onClick:      js.UndefOr[String | OnClick]              = js.undefined
  ): CatalogOptions = {
    val p = (new js.Object()).asInstanceOf[CatalogOptions]
    p.name       = name
    p.color      = color.map(c => c: String)
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
    p.limit        = limit
    p.raField      = raField
    p.decField     = decField
    p.displayLabel = displayLabel
    p.labelColor   = labelColor
    p.labelFont    = labelFont
    p.labelColumn  = labelColumn
    p.onClick = onClick.map((_: Any) match {
      case s: String => s
      case r         => ((s: AladinSource) => r.asInstanceOf[OnClick](s)): RawOnClick
    })
    p
  }
}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Source", JSImport.Namespace)
class AladinSource extends js.Object {
  val x: Double       = js.native
  val y: Double       = js.native
  val data: js.Object = js.native
}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Color", JSImport.Namespace)
class AladinColor extends js.Object {}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/ColorMap", JSImport.Namespace)
class ColorMap extends js.Object {
  def update(a: String): Unit = js.native
}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Footprint", JSImport.Namespace)
class AladinFootprint extends js.Object {}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Polyline", JSImport.Namespace)
class AladinPolyline extends js.Object {}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Circle", JSImport.Namespace)
class AladinCircle extends js.Object {}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/HpxImageSurvey", JSImport.Namespace)
class HpxImageSurvey extends js.Object {
  def setAlpha(a: JsNumber): Unit = js.native
  def getColorMap(): ColorMap = js.native
}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Catalog", JSImport.Namespace)
class AladinCatalog extends js.Object {
  def addSources(s: AladinSource | js.Array[AladinSource]) = js.native
}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Overlay", JSImport.Namespace)
class AladinOverlay extends js.Object {
  def addFootprints(s: js.Array[AladinOverlay.Shapes]): Unit = js.native
  def add(s:           AladinOverlay.Shapes): Unit           = js.native
}

object AladinOverlay {
  type Shapes = AladinCircle | AladinFootprint | AladinPolyline
}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/Aladin", JSImport.Namespace)
class JsAladin extends js.Object {
  def setImageSurvey(s:    String): Unit = js.native
  def setBaseImageLayer(s: String): Unit = js.native
  def getBaseImageLayer(): HpxImageSurvey = js.native
  def setOverlayImageLayer(i: HpxImageSurvey): Unit = js.native
  def getOverlayImageLayer(): HpxImageSurvey = js.native
  def createImageSurvey(
    id:       String,
    name:     String,
    rootUrl:  String,
    cooFrame: String,
    maxOrder: JsNumber,
    options:  js.Object
  ): HpxImageSurvey = js.native
  def addCatalog(c:      AladinCatalog): Unit = js.native
  def addOverlay(c:      AladinOverlay): Unit = js.native
  def gotoRaDec(ra:      JsNumber, dec: JsNumber): Unit = js.native
  def animateToRaDec(ra: JsNumber, dec: JsNumber, time: JsNumber): Unit = js.native
}

@js.native
@JSImport("@cquiroz/aladin-react/lib/js/A", JSImport.Namespace)
object A extends js.Object {
  def aladin(divSelector: String, options: AladinProps): JsAladin = js.native
  def catalog(c:          CatalogOptions): AladinCatalog = js.native
  def graphicOverlay(c:   OverlayOptions): AladinOverlay = js.native
  def polygon(raDecArray: js.Array[js.Array[JsNumber]]): AladinFootprint = js.native
  def polyline(
    raDecArray: js.Array[js.Array[JsNumber]],
    o:          js.UndefOr[PolylineOptions]
  ): AladinPolyline = js.native
  def circle(
    ra:        JsNumber,
    dec:       JsNumber,
    radiusDeg: JsNumber,
    options:   js.UndefOr[js.Object] = js.undefined
  ): AladinCircle = js.native
  def source(
    ra:      JsNumber,
    dec:     JsNumber,
    data:    js.UndefOr[js.Object] = js.undefined,
    options: js.UndefOr[js.Object] = js.undefined
  ): AladinSource = js.native
  def marker(
    ra:      JsNumber,
    dec:     JsNumber,
    data:    js.UndefOr[js.Object] = js.undefined,
    options: js.UndefOr[js.Object] = js.undefined
  ): AladinSource = js.native
  def catalogFromURL(
    url:             String,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined
  ): AladinCatalog = js.native
  def catalogFromSimbad(
    url:             String,
    radius:          JsNumber,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined
  ): AladinCatalog = js.native
  def catalogFromNED(
    url:             String,
    radius:          JsNumber,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined
  ): AladinCatalog = js.native
  def catalogFromVizieR(
    vizCatId:        String,
    target:          String,
    radius:          JsNumber,
    options:         CatalogOptions,
    successCallback: js.UndefOr[js.Object] = js.undefined
  ): AladinCatalog = js.native
}

final case class Aladin(
  target:                   js.UndefOr[String]           = js.undefined,
  fov:                      js.UndefOr[JsNumber]         = js.undefined,
  survey:                   js.UndefOr[String]           = js.undefined,
  cooFrame:                 js.UndefOr[CooFrame]         = js.undefined,
  showReticle:              js.UndefOr[Boolean]          = js.undefined,
  showZoomControl:          js.UndefOr[Boolean]          = js.undefined,
  showFullscreenControl:    js.UndefOr[Boolean]          = js.undefined,
  showLayersControl:        js.UndefOr[Boolean]          = js.undefined,
  showGotoControl:          js.UndefOr[Boolean]          = js.undefined,
  showShareControl:         js.UndefOr[Boolean]          = js.undefined,
  showSimbadPointerControl: js.UndefOr[Boolean]          = js.undefined,
  showFrame:                js.UndefOr[Boolean]          = js.undefined,
  fullScreen:               js.UndefOr[Boolean]          = js.undefined,
  reticleColor:             js.UndefOr[Color]            = js.undefined,
  reticleSize:              js.UndefOr[JsNumber]         = js.undefined,
  imageSurvey:              js.UndefOr[String]           = js.undefined,
  baseImageLayer:           js.UndefOr[String]           = js.undefined,
  customize:                js.UndefOr[JsAladin => Unit] = js.undefined
) {
  def render   = Aladin.component(this)
  def renderJs = Aladin.jsComponent(Aladin.fromProps(this))
}
object Aladin {
  type Props = Aladin

  final case class State(a: Option[JsAladin])
  class Backend(bs:         BackendScope[Aladin, State]) {
    def render: VdomElement = <.div(^.cls := "react-aladin")
    def goZero: Callback    = bs.state.flatMap(_.a.map(f => Callback(f.gotoRaDec(0, 0))).getOrEmpty)
  }

  // Say this is the Scala component you want to share
  val component = ScalaComponent
    .builder[Props]("Aladin")
    .initialState(State(None))
    .renderBackend[Backend]
    .componentDidMount { b =>
      for {
        aladin <- CallbackTo[JsAladin](A.aladin(".react-aladin", fromProps(b.props)))
        _      <- Callback(b.props.imageSurvey.toOption.map(aladin.setImageSurvey))
        _      <- Callback(b.props.baseImageLayer.toOption.map(aladin.setBaseImageLayer))
        _      <- Callback(b.props.customize.toOption.map(_(aladin)))
        _      <- b.setState(State(Some(aladin)))
      } yield ()
    }
    .build

  def fromProps(q: AladinProps): Props =
    Aladin(
      q.target,
      q.fov,
      q.survey,
      q.cooFrame.flatMap(CooFrame.fromString(_).orUndefined),
      q.showReticle,
      q.showZoomControl,
      q.showFullscreenControl,
      q.showLayersControl,
      q.showGotoControl,
      q.showShareControl,
      q.showSimbadPointerControl,
      q.showFrame,
      q.fullScreen,
      q.reticleColor.map(Color.apply),
      q.reticleSize,
      q.imageSurvey,
      q.baseImageLayer,
      q.customize
    )

  def fromProps(q: Props): AladinProps = {
    val p = new js.Object().asInstanceOf[AladinProps]
    p.fov                      = q.fov
    p.target                   = q.target
    p.survey                   = q.survey
    p.cooFrame                 = q.cooFrame.toJs
    p.reticleColor             = q.reticleColor.map(c => c: String)
    p.reticleSize              = q.reticleSize
    p.imageSurvey              = q.imageSurvey
    p.baseImageLayer           = q.baseImageLayer
    p.customize                = q.customize
    p.showReticle              = q.showReticle
    p.showZoomControl          = q.showZoomControl
    p.showFullscreenControl    = q.showFullscreenControl
    p.showLayersControl        = q.showLayersControl
    p.showGotoControl          = q.showGotoControl
    p.showShareControl         = q.showShareControl
    p.showSimbadPointerControl = q.showSimbadPointerControl
    p.showFrame                = q.showFrame
    p.fullScreen               = q.fullScreen
    p
  }

  // Make it usable from JS-Land
  @JSExportTopLevel("JSAladin")
  val jsComponent =
    component
      .cmapCtorProps[AladinProps](fromProps) // Change props from JS to Scala
      .toJsComponent // Create a new, real JS component
      .raw // Leave the nice Scala wrappers behind and obtain the underlying JS value
}
