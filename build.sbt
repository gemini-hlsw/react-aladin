import org.scalajs.linker.interface.ModuleSplitStyle

lazy val reactJS                = "17.0.2"
lazy val scalaJsReact           = "2.0.0"
lazy val lucumaCoreVersion      = "0.14.3"
lazy val lucumaUIVersion        = "0.18.1"
lazy val aladinLiteVersion      = "0.5.1"
lazy val reactCommonVersion     = "0.14.7"
lazy val reactGridLayoutVersion = "0.14.2"
lazy val munitVersion           = "0.7.29"
lazy val svgdotjsVersion        = "0.2.1"

inThisBuild(
  Seq(
    homepage := Some(url("https://github.com/gemini-hlsw/react-aladin")),
    Global / onChangedBuildSource := ReloadOnSourceChanges,
    scalacOptions += "-Ymacro-annotations"
  ) ++ lucumaPublishSettings
)

Global / resolvers += Resolver.sonatypeRepo("public")

addCommandAlias(
  "restartWDS",
  "; demo/fastOptJS::stopWebpackDevServer; demo/fastOptJS::startWebpackDevServer; ~demo/fastOptJS"
)

publish / skip := true

val demo =
  project
    .in(file("demo"))
    // .enablePlugins(ScalaJSBundlerPlugin)
    .enablePlugins(ScalaJSPlugin)
    .settings(lucumaScalaJsSettings: _*)
    .settings(commonSettings: _*)
    .settings(
      publish / skip := true,
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
      scalaJSLinkerConfig ~= (_.withModuleSplitStyle(ModuleSplitStyle.SmallestModules)),
      webpack / version := "4.44.1",
      startWebpackDevServer / version := "3.11.0",
      fastOptJS / webpackConfigFile := Some(
        baseDirectory.value / "webpack" / "dev.webpack.config.js"
      ),
      fullOptJS / webpackConfigFile := Some(
        baseDirectory.value / "webpack" / "prod.webpack.config.js"
      ),
      webpackResources := (baseDirectory.value / "webpack") * "*.js",
      webpackMonitoredFiles / includeFilter := "*",
      webpackExtraArgs := Seq("--progress"),
      useYarn := true,
      fastOptJS / webpackBundlingMode := BundlingMode.LibraryOnly(),
      fullOptJS / webpackBundlingMode := BundlingMode.Application,
      test := {},
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      libraryDependencies ++= Seq(
        "edu.gemini"                        %%% "lucuma-core"        % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-ui"          % lucumaUIVersion,
        "edu.gemini"                        %%% "lucuma-svgdotjs"    % svgdotjsVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"  % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3" % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test"               % scalaJsReact % Test,
        "io.github.cquiroz.react"           %%% "common"             % reactCommonVersion,
        "io.github.cquiroz.react"           %%% "react-grid-layout"  % reactGridLayoutVersion
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
      .replaceAll("/js/", "@cquiroz/aladin-lite/lib/js/")

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
    .enablePlugins(AutomateHeaderPlugin)
    .settings(lucumaScalaJsSettings: _*)
    .settings(commonSettings: _*)
    .settings(
      name := "react-aladin",
      Compile / npmDependencies ++= Seq(
        "react"                -> reactJS,
        "react-dom"            -> reactJS,
        "@cquiroz/aladin-lite" -> aladinLiteVersion
      ),
      Test / npmDevDependencies ++= Seq(
        "chokidar" -> "3.4.2"
      ),
      // Requires the DOM for tests
      Test / requireJsDomEnv := true,
      // Use yarn as it is faster than npm
      useYarn := true,
      webpack / version := "4.20.2",
      startWebpackDevServer / version := "3.1.8",
      scalaJSUseMainModuleInitializer := false,
      // Compile tests to JS using fast-optimisation
      Test / scalaJSStage := FastOptStage,
      libraryDependencies ++= Seq(
        "edu.gemini"                        %%% "lucuma-core"       % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-ui"         % lucumaUIVersion,
        "edu.gemini"                        %%% "lucuma-svgdotjs"   % svgdotjsVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io" % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test"              % scalaJsReact % Test,
        "io.github.cquiroz.react"           %%% "common"            % reactCommonVersion,
        "org.scalameta"                     %%% "munit"             % munitVersion % Test
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      Test / webpackConfigFile := Some(
        baseDirectory.value / "src" / "webpack" / "test.webpack.config.js"
      ),
      Compile / sourceGenerators += Def.task {
        val srcDir         = (demo / Compile / scalaSource).value
        val srcFiles       = srcDir ** "*.scala"
        val destinationDir = (Compile / sourceManaged).value
        copyAndReplace(srcFiles.get, srcDir, destinationDir)
      }.taskValue
    )

lazy val commonSettings = Seq(
  description := "react component for aladin",
  scalacOptions ~= (_.filterNot(
    Set(
      // By necessity facades will have unused params
      "-Wdead-code",
      "-Wunused:params",
      "-Wunused:explicits"
    )
  ))
)
