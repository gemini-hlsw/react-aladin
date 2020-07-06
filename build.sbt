lazy val reactJS           = "16.13.1"
lazy val scalaJsReact      = "1.7.3"
lazy val gspMathVersion    = "0.2.3"
lazy val gspCoreVersion    = "0.2.5"
lazy val aladinLiteVersion = "0.1.8"

parallelExecution in (ThisBuild, Test) := false

Global / onChangedBuildSource := ReloadOnSourceChanges

Global / resolvers += Resolver.sonatypeRepo("public")

inThisBuild(
  List(
    homepage := Some(url("https://github.com/cquiroz/react-aladin")),
    licenses := Seq(
      "BSD 3-Clause License" -> url(
        "https://opensource.org/licenses/BSD-3-Clause"
      )
    ),
    developers := List(
      Developer(
        "cquiroz",
        "Carlos Quiroz",
        "carlos.m.quiroz@gmail.com",
        url("https://github.com/cquiroz")
      )
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/cquiroz/react-aladin"),
        "scm:git:git@github.com:cquiroz/react-aladin.git"
      )
    )
  )
)

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS::stopWebpackDevServer; demo/fastOptJS::startWebpackDevServer; ~demo/fastOptJS"
)

val demo =
  project
    .in(file("demo"))
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(commonSettings: _*)
    .settings(
      version in webpack := "4.43.0",
      version in startWebpackDevServer := "3.11.0",
      webpackConfigFile in fastOptJS := Some(
        baseDirectory.value / "webpack" / "dev.webpack.config.js"
      ),
      webpackConfigFile in fullOptJS := Some(
        baseDirectory.value / "webpack" / "prod.webpack.config.js"
      ),
      webpackMonitoredDirectories += (resourceDirectory in Compile).value,
      webpackResources := (baseDirectory.value / "webpack") * "*.js",
      includeFilter in webpackMonitoredFiles := "*",
      webpackExtraArgs := Seq("--progress"),
      // webpackExtraArgs                       := Seq("--progress", "--display", "verbose"),
      useYarn := true,
      webpackBundlingMode in fastOptJS := BundlingMode.LibraryOnly(),
      webpackBundlingMode in fullOptJS := BundlingMode.Application,
      test := {},
      scalaJSLinkerConfig in (Compile, fastOptJS) ~= { _.withSourceMap(false) },
      scalaJSLinkerConfig in (Compile, fullOptJS) ~= { _.withSourceMap(false) },
      // NPM libs for development, mostly to let webpack do its magic
      npmDevDependencies in Compile ++= Seq(
        "postcss-loader" -> "3.0.0",
        "autoprefixer" -> "9.7.6",
        "url-loader" -> "4.1.0",
        "file-loader" -> "6.0.0",
        "css-loader" -> "3.5.3",
        "style-loader" -> "1.2.1",
        "less" -> "3.11.1",
        "less-loader" -> "6.1.0",
        "webpack-merge" -> "4.2.2",
        "mini-css-extract-plugin" -> "0.9.0",
        "webpack-dev-server-status-bar" -> "1.1.2",
        "cssnano" -> "4.1.10",
        "uglifyjs-webpack-plugin" -> "2.2.0",
        "html-webpack-plugin" -> "4.3.0",
        "optimize-css-assets-webpack-plugin" -> "5.0.3",
        "favicons-webpack-plugin" -> "3.0.1",
        "why-did-you-update" -> "1.0.8",
        "svg-inline-loader" -> "0.8.2",
        "babel-loader" -> "8.1.0",
        "@babel/core" -> "7.10.2",
        "@babel/preset-env" -> "7.10.2"
      ),
      npmDependencies in Compile ++= Seq(
        "react" -> reactJS,
        "react-dom" -> reactJS,
        "jquery" -> "1.12.4",
        "raf" -> "3.4.1",
        "stats.js" -> "0.17.0"
      ),
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "gsp-core-model" % gspCoreVersion,
        "edu.gemini" %%% "gsp-math" % gspMathVersion,
        "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReact % Test,
        "io.github.cquiroz.react" %%% "common" % "0.9.3",
        "io.github.cquiroz.react" %%% "react-sizeme" % "0.4.4"
      ),
      // don't publish the demo
      publish := {},
      publishLocal := {},
      publishArtifact := false,
      Keys.`package` := file("")
    )

def copyAndReplace(srcFiles: Seq[File], srcRoot: File, destinationDir: File): Seq[File] = {
  def replacements(line: String): String =
    line
      .replaceAll("js/", "@cquiroz/aladin-lite/lib/js/")

  // Visit each file and read the content replacing key strings
  srcFiles.filter(_.getPath.contains("react/aladin")).flatMap { f =>
    f.relativeTo(srcRoot)
      .map { r =>
        val target        = new File(destinationDir, r.getPath)
        val replacedLines = IO.readLines(f).map(replacements)
        IO.createDirectory(target.getParentFile)
        IO.writeLines(target, replacedLines)
        Seq(target)
      }
      .getOrElse(Seq.empty)
  }
}

lazy val facade =
  project
    .in(file("facade"))
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "react-aladin",
      npmDependencies in Compile ++= Seq(
        "react" -> reactJS,
        "react-dom" -> reactJS,
        "@cquiroz/aladin-lite" -> aladinLiteVersion
      ),
      // Requires the DOM for tests
      requireJsDomEnv in Test := true,
      // Use yarn as it is faster than npm
      useYarn := true,
      version in webpack := "4.20.2",
      version in startWebpackDevServer := "3.1.8",
      scalaJSUseMainModuleInitializer := false,
      // Compile tests to JS using fast-optimisation
      scalaJSStage in Test := FastOptStage,
      libraryDependencies ++= Seq(
        "edu.gemini" %%% "gsp-core-model" % gspCoreVersion,
        "edu.gemini" %%% "gsp-math" % gspMathVersion,
        "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReact % Test,
        "io.github.cquiroz.react" %%% "common" % "0.9.3",
        "com.lihaoyi" %%% "utest" % "0.7.4" % Test
      ),
      testFrameworks += new TestFramework("utest.runner.Framework"),
      Compile / sourceGenerators += Def.task {
        val srcDir         = (demo / Compile / scalaSource).value
        val srcFiles       = srcDir ** "*.scala"
        val destinationDir = (Compile / sourceManaged).value
        copyAndReplace(srcFiles.get, srcDir, destinationDir)
      }.taskValue
    )

lazy val commonSettings = Seq(
  scalaVersion := "2.13.3",
  organization := "io.github.cquiroz.react",
  sonatypeProfileName := "io.github.cquiroz",
  description := "react component for aladin",
  homepage := Some(url("https://github.com/cquiroz/react-aladin")),
  scalacOptions ~= (_.filterNot(
    Set(
      // By necessity facades will have unused params
      "-Wdead-code",
      "-Wunused:params",
      "-Wunused:explicits"
    )
  ))
)
