import org.scalajs.linker.interface.ModuleSplitStyle

val reactJS            = "18.2.0"
val scalaJsReact       = "3.0.0-beta6"
val lucumaCoreVersion  = "0.104.0"
val lucumaUIVersion    = "0.124.0"
val lucumaReactVersion = "0.71.3"
val aladinLiteVersion  = "0.6.2"
val munitVersion       = "1.0.2"

ThisBuild / tlBaseVersion       := "0.32"
ThisBuild / tlCiReleaseBranches := Seq("master")

Global / onChangedBuildSource  := ReloadOnSourceChanges
ThisBuild / scalacOptions ~= { _.filterNot(Set("-Wunused:params")) }
ThisBuild / coverageEnabled    := false
Global / resolvers ++= Resolver.sonatypeOssRepos("public")
ThisBuild / scalaVersion       := "3.6.0"
ThisBuild / crossScalaVersions := Seq("3.6.0")
ThisBuild / scalacOptions ++= Seq(
  "-language:implicitConversions"
)
enablePlugins(NoPublishPlugin)

val demo =
  project
    .in(file("demo"))
    .enablePlugins(ScalaJSPlugin, NoPublishPlugin)
    .settings(commonSettings: _*)
    .settings(
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
      Compile / fastLinkJS / scalaJSLinkerConfig ~= (_.withModuleSplitStyle(
        ModuleSplitStyle.FewestModules
      )),
      Compile / fullLinkJS / scalaJSLinkerConfig ~= (_.withModuleSplitStyle(
        ModuleSplitStyle.FewestModules
      )),
      Compile / fastOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      Compile / fullOptJS / scalaJSLinkerConfig ~= { _.withSourceMap(false) },
      test := {},
      libraryDependencies ++= Seq(
        "edu.gemini"                        %%% "lucuma-core"                  % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-ui"                    % lucumaUIVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"            % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"           % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test"                         % scalaJsReact % Test,
        "edu.gemini"                        %%% "lucuma-react-common"          % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-grid-layout"     % lucumaReactVersion,
        "edu.gemini"                        %%% "lucuma-react-resize-detector" % lucumaReactVersion
      )
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
    .settings(commonSettings: _*)
    .settings(
      name                            := "react-aladin",
      Compile / npmDependencies ++= Seq(
        "react"                -> reactJS,
        "react-dom"            -> reactJS,
        "@cquiroz/aladin-lite" -> aladinLiteVersion
      ),
      Test / npmDevDependencies ++= Seq(
        "chokidar" -> "3.6.0"
      ),
      // Requires the DOM for tests
      Test / requireJsDomEnv          := true,
      installJsdom / version          := "19.0.0",
      webpack / version               := "5.76.1",
      startWebpackDevServer / version := "4.12.0",
      scalaJSUseMainModuleInitializer := false,
      // Compile tests to JS using fast-optimisation
      Test / scalaJSStage             := FastOptStage,
      libraryDependencies ++= Seq(
        "edu.gemini"                        %%% "lucuma-core"         % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-ui"           % lucumaUIVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"   % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test"                % scalaJsReact % Test,
        "edu.gemini"                        %%% "lucuma-react-common" % lucumaReactVersion,
        "org.scalameta"                     %%% "munit"               % munitVersion % Test
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      Test / webpackConfigFile        := Some(
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
  description     := "react component for aladin",
  // By necessity facades will have unused params
  tlFatalWarnings := false
)
