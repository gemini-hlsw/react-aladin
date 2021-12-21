// Copyright (c) 2016-2021 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package react.aladin

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation._

import cats.implicits._
import japgolly.scalajs.react._
import japgolly.scalajs.react.facade.JsNumber
import japgolly.scalajs.react.vdom.html_<^._
import lucuma.core.math._
import org.scalajs.dom.Element
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
  var mountNodeClass: String
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
  var showCoordinates: js.UndefOr[Boolean]
  var showFov: js.UndefOr[Boolean]
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
    name:      js.UndefOr[String] = js.undefined,
    color:     js.UndefOr[String] = js.undefined,
    lineWidth: js.UndefOr[JsNumber] = js.undefined
  ): OverlayOptions = {
    val p = (new js.Object()).asInstanceOf[OverlayOptions]
    name.foreach(v => p.name = v)
    color.foreach(v => p.color = v)
    lineWidth.foreach(v => p.lineWidth = v)
    p
  }
}

@js.native
trait PolylineOptions extends js.Object {
  var color: js.UndefOr[String]
}

object PolylineOptions {
  def apply(
    color: js.UndefOr[String] = js.undefined
  ): PolylineOptions = {
    val p = (new js.Object()).asInstanceOf[PolylineOptions]
    color.foreach(v => p.color = v)
    p
  }
}

@js.native
@JSImport("/js/Source", JSImport.Namespace)
class AladinSource extends js.Object {
  val x: Double       = js.native
  val y: Double       = js.native
  val data: js.Object = js.native
}

@js.native
@JSImport("/js/Aladin", JSImport.Namespace)
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
  def box(): Unit                 = js.native
  def pix2world(x: Double, y: Double): js.Array[Double] = js.native
  def world2pix(x: Double, y: Double): js.Array[Double] = js.native
  def on(n:        String, f: js.Function): Unit        = js.native
}

@js.native
@JSImport("/js/A", JSImport.Namespace)
object A extends js.Object {
  def aladin(divSelector: String, options: AladinProps): JsAladin = js.native
  def catalog(c:          CatalogOptions): AladinCatalog = js.native
  def graphicOverlay(c:   OverlayOptions): AladinOverlay               = js.native
  def polygon(raDecArray: js.Array[js.Array[Double]]): AladinFootprint = js.native
  def polyline(
    raDecArray:           js.Array[js.Array[Double]],
    o:                    js.UndefOr[PolylineOptions]
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
  mountNodeClass:           Css,
  target:                   js.UndefOr[String] = js.undefined,
  fov:                      js.UndefOr[JsNumber] = js.undefined,
  survey:                   js.UndefOr[String] = js.undefined,
  cooFrame:                 js.UndefOr[CooFrame] = js.undefined,
  showReticle:              js.UndefOr[Boolean] = js.undefined,
  showZoomControl:          js.UndefOr[Boolean] = js.undefined,
  showFullscreenControl:    js.UndefOr[Boolean] = js.undefined,
  showLayersControl:        js.UndefOr[Boolean] = js.undefined,
  showGotoControl:          js.UndefOr[Boolean] = js.undefined,
  showShareControl:         js.UndefOr[Boolean] = js.undefined,
  showSimbadPointerControl: js.UndefOr[Boolean] = js.undefined,
  showFrame:                js.UndefOr[Boolean] = js.undefined,
  showCoordinates:          js.UndefOr[Boolean] = js.undefined,
  showFov:                  js.UndefOr[Boolean] = js.undefined,
  fullScreen:               js.UndefOr[Boolean] = js.undefined,
  reticleColor:             js.UndefOr[String] = js.undefined,
  reticleSize:              js.UndefOr[JsNumber] = js.undefined,
  imageSurvey:              js.UndefOr[String] = js.undefined,
  baseImageLayer:           js.UndefOr[String] = js.undefined,
  customize:                js.UndefOr[JsAladin => Callback] = js.undefined
) {
  def render   = Aladin.component(this)
  def renderJs = Aladin.jsComponent(Aladin.fromProps(this))

  lazy val mountNodeClassSelector = mountNodeClass.htmlClasses.map(cls => s".$cls").mkString
}

object Aladin {
  type Props = Aladin

  final case class State(a: Option[JsAladin])

  implicit val propsReuse: Reusability[Props] = Reusability.always
  implicit val stateReuse: Reusability[State] = Reusability.by(_.a.isDefined)

  class Backend(bs: BackendScope[Aladin, State]) {
    def runOnAladinOpt[A](f: JsAladin => A): CallbackOption[A] =
      bs.state
        .map {
          case State(Some(a)) => f(a).some
          case _              => none
        }
        .asCBO[A]

    def runOnAladinCB[A](f: JsAladin => CallbackTo[A]): Callback =
      bs.state.flatMap {
        case State(Some(a)) => f(a).void
        case _              => Callback.empty
      }

    def runOnAladin[A](f: JsAladin => A): Callback =
      bs.state.flatMap {
        case State(Some(a)) => CallbackTo(f(a)).void
        case _              => Callback.empty
      }

    def render(props: Props): VdomElement = <.div(props.mountNodeClass)

    def gotoRaDec(ra: JsNumber, dec: JsNumber): Callback = runOnAladin(_.gotoRaDec(ra, dec))

    def box: Callback = runOnAladin(_.box()) *> Callback.log("ABC")
    def world2pix(c: Coordinates): CallbackTo[Option[(Double, Double)]] =
      runOnAladinOpt { j =>
        val ra  = c.ra.toAngle.toDoubleDegrees
        val dec = c.dec.toAngle.toSignedDoubleDegrees
        val p   = j.world2pix(ra, dec)
        Option(p).filter(_.length == 2).map(p => (p(0), p(1)))
      }.getOrElse(None)

    def getRaDec: CallbackTo[Coordinates] =
      runOnAladinOpt(_.getRaDec())
        .flatMapOption { a =>
          (RightAscension.fromHourAngle
             .get(Angle.hourAngle.get(Angle.fromDoubleDegrees(a(0))))
             .some,
           Declination.fromAngle.getOption(Angle.fromDoubleDegrees(a(1)))
          ).mapN(Coordinates.apply)
        }
        .getOrElse(Coordinates.Zero)

    def gotoObject(q: String, cb: (JsNumber, JsNumber) => Callback, er: Callback): Callback =
      runOnAladin(_.gotoObject(q, new GoToObjectCallback(cb, er)))

    def recalculateView: Callback =
      runOnAladin(_.recalculateView())

    def pixelScale: CallbackTo[PixelScale] =
      runOnAladinOpt(a =>
        PixelScale(a.getSize()(0) / a.getFov()(0), a.getSize()(1) / a.getFov()(1))
      ).getOrElse(PixelScale.Default)
  }

  // Say this is the Scala component you want to share
  val component = ScalaComponent
    .builder[Props]
    .initialState(State(None))
    .renderBackend[Backend]
    .componentDidMount { b =>
      for {
        aladin <-
          CallbackTo[JsAladin](A.aladin(b.props.mountNodeClassSelector, fromProps(b.props)))
        _      <- Callback(b.props.imageSurvey.toOption.map(aladin.setImageSurvey))
        _      <- Callback(b.props.baseImageLayer.toOption.map(aladin.setBaseImageLayer))
        _      <- Callback(b.props.customize.toOption.map(_(aladin).runNow()))
        _      <- b.setState(State(Some(aladin)))
      } yield ()
    }
    .configure(Reusability.shouldComponentUpdate)
    .build

  def fromProps(q: AladinProps): Props =
    Aladin(
      Css(q.mountNodeClass),
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
      q.showCoordinates,
      q.showFov,
      q.fullScreen,
      q.reticleColor,
      q.reticleSize,
      q.imageSurvey,
      q.baseImageLayer,
      q.customize.map(f => (j: JsAladin) => Callback(f(j)))
    )

  def fromProps(q: Props): AladinProps = {
    val p = new js.Object().asInstanceOf[AladinProps]
    q.fov.foreach(v => p.fov = v)
    q.target.foreach(v => p.target = v)
    q.survey.foreach(v => p.survey = v)
    q.cooFrame.foreach(v => p.cooFrame = v.toJs)
    q.reticleColor.foreach(v => p.reticleColor = v: String)
    q.reticleSize.foreach(v => p.reticleSize = v)
    q.imageSurvey.foreach(v => p.imageSurvey = v)
    q.baseImageLayer.foreach(v => p.baseImageLayer = v)
    q.customize.foreach(v => p.customize = (j: JsAladin) => v(j).runNow())
    q.showReticle.foreach(v => p.showReticle = v)
    q.showZoomControl.foreach(v => p.showZoomControl = v)
    q.showFullscreenControl.foreach(v => p.showFullscreenControl = v)
    q.showLayersControl.foreach(v => p.showLayersControl = v)
    q.showGotoControl.foreach(v => p.showGotoControl = v)
    q.showShareControl.foreach(v => p.showShareControl = v)
    q.showSimbadPointerControl.foreach(v => p.showSimbadPointerControl = v)
    q.showFrame.foreach(v => p.showFrame = v)
    q.showCoordinates.foreach(v => p.showCoordinates = v)
    q.showFov.foreach(v => p.showFov = v)
    q.fullScreen.foreach(v => p.fullScreen = v)
    p
  }

  // Make it usable from JS-Land
  @JSExportTopLevel("JSAladin")
  val jsComponent =
    component
      .cmapCtorProps[AladinProps](fromProps) // Change props from JS to Scala
      .toJsComponent                         // Create a new, real JS component
      .raw                                   // Leave the nice Scala wrappers behind and obtain the underlying JS value
}
