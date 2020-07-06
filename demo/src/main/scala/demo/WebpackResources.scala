package react.semanticui.demo

import scala.scalajs.js
// import scala.scalajs.js.annotation.JSImport

object WebpackResources {
  // marker trait
  trait WebpackResource extends js.Object

  implicit class WebpackResourceOps(val r: WebpackResource) extends AnyVal {
    def resource: String = r.toString
  }
}
