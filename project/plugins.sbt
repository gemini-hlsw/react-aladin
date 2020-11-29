resolvers in Global += Resolver.sonatypeRepo("public")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.3.1")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.20.0")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.3.2")

addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.4")

addSbtPlugin("edu.gemini" % "sbt-lucuma" % "0.3.3")
