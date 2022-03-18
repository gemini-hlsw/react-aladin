// Copyright (c) 2016-2022 Association of Universities for Research in Astronomy, Inc. (AURA)
// For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause

package demo

import scala.scalajs.js

import cats.effect._
import org.scalajs.dom
import org.http4s.dom._
import js.annotation._

@JSExportTopLevel("Main")
object AladinDemo extends IOApp.Simple {
  @JSExport
  def resetIOApp(): Unit =
    // https://github.com/typelevel/cats-effect/pull/2114#issue-687064738
    cats.effect.unsafe.IORuntime.asInstanceOf[{ def resetGlobal(): Unit }].resetGlobal()

  @JSExport
  def runIOApp(): Unit = main(Array.empty)

  override final def run: IO[Unit] = IO {
    val container = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }
    TargetBody(FetchClientBuilder[IO].create).renderIntoDOM(container)
    ()
  }
}
