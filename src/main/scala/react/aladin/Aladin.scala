package react.aladin

import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.JSConverters._
import org.scalajs.dom.ext._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.raw.JsNumber
import react.common._
import org.scalajs.dom.raw.Element

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

@js.native
@JSImport("~resources/aladin-lite/js/Source", JSImport.Namespace)
class AladinSource extends js.Object {
  val x: Double       = js.native
  val y: Double       = js.native
  val data: js.Object = js.native
}

@js.native
@JSImport("~resources/aladin-lite/js/Color", JSImport.Namespace)
class AladinColor extends js.Object {}

@js.native
@JSImport("~resources/aladin-lite/js/ColorMap", JSImport.Namespace)
class ColorMap extends js.Object {
  def update(a: String): Unit = js.native
}

@js.native
@JSImport("~resources/aladin-lite/js/Footprint", JSImport.Namespace)
class AladinFootprint extends js.Object {}

@js.native
@JSImport("~resources/aladin-lite/js/Polyline", JSImport.Namespace)
class AladinPolyline extends js.Object {}

@js.native
@JSImport("~resources/aladin-lite/js/Circle", JSImport.Namespace)
class AladinCircle extends js.Object {}

@js.native
@JSImport("~resources/aladin-lite/js/HpxImageSurvey", JSImport.Namespace)
class HpxImageSurvey extends js.Object {
  def setAlpha(a: JsNumber): Unit = js.native
  def getColorMap(): ColorMap = js.native
}

@js.native
@JSImport("js/Aladin", JSImport.Namespace)
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
  def addCatalog(c: AladinCatalog): Unit = js.native
  def addOverlay(c: AladinOverlay): Unit = js.native
  def gotoRaDec(ra: JsNumber, dec: JsNumber): Unit = js.native
  def getRaDec(): js.Array[Double] = js.native
  def gotoObject(q:      String, cb:    GoToObjectCallback): Unit = js.native
  def animateToRaDec(ra: JsNumber, dec: JsNumber, time: JsNumber): Unit = js.native
  def recalculateView(): Unit     = js.native
  def getParentDiv(): Element     = js.native
  def getSize(): js.Array[Double] = js.native
  def getFov(): js.Array[Double]  = js.native
  def pix2world(x: Double, y: Double): js.Array[Double] = js.native
  def world2pix(x: Double, y: Double): js.Array[Double] = js.native
  def on(n:        String, f: js.Function): Unit        = js.native
}

@js.native
@JSImport("js/A", JSImport.Namespace)
object A extends js.Object {
  def aladin(divSelector: String, options: AladinProps): JsAladin = js.native
  def catalog(c:          CatalogOptions): AladinCatalog = js.native
  def graphicOverlay(c:   OverlayOptions): AladinOverlay = js.native
  def polygon(raDecArray: js.Array[js.Array[Double]]): AladinFootprint = js.native
  def polyline(
    raDecArray: js.Array[js.Array[Double]],
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
    private def runOnAladinOpt[A](f: JsAladin => A): CallbackTo[Option[A]] =
      bs.state.flatMap {
        case State(Some(a)) => CallbackTo(Some(f(a)))
        case _              => CallbackTo(None)
      }
    private def runOnAladin[A](f: JsAladin => A): Callback =
      bs.state.flatMap {
        case State(Some(a)) => CallbackTo(f(a)).void
        case _              => Callback.empty
      }
    def render: VdomElement = <.div(^.cls := "react-aladin")
    def gotoRaDec(ra: JsNumber, dec: JsNumber): Callback = runOnAladin(_.gotoRaDec(ra, dec))
    def getRaDec: CallbackTo[Option[(Double, Double)]] = runOnAladinOpt(_.getRaDec()).map {
      _.map(a => (a(0), a(1)))
    }
    def gotoObject(q: String, cb: (JsNumber, JsNumber) => Callback, er: Callback): Callback =
      runOnAladin(_.gotoObject(q, new GoToObjectCallback(cb, er)))
  }

  // Say this is the Scala component you want to share
  val component = ScalaComponent
    .builder[Props]
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
