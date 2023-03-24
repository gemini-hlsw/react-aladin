import org.scalajs.linker.interface.ModuleSplitStyle

lazy val reactJS           = "17.0.2"
lazy val scalaJsReact      = "2.1.1"
val lucumaCoreVersion      = "0.70.0"
val lucumaUIVersion        = "0.65.1"
val lucumaReactVersion     = "0.32.0"
lazy val aladinLiteVersion = "0.6.2"
lazy val munitVersion      = "0.7.29"

ThisBuild / tlBaseVersion       := "0.29"
ThisBuild / tlCiReleaseBranches := Seq("master")

Global / onChangedBuildSource  := ReloadOnSourceChanges
ThisBuild / scalacOptions ~= { _.filterNot(Set("-Wunused:params")) }
ThisBuild / coverageEnabled    := false
Global / resolvers ++= Resolver.sonatypeOssRepos("public")
ThisBuild / scalaVersion       := "3.2.2"
ThisBuild / crossScalaVersions := Seq("3.2.2")
ThisBuild / scalacOptions ++= Seq(
  "-language:implicitConversions"
)
enablePlugins(NoPublishPlugin)

ThisBuild / githubWorkflowBuildPreamble ++= Seq(
  WorkflowStep.Use(
    UseRef.Public("actions", "setup-node", "v3"),
    name = Some("Setup Node"),
    params = Map("node-version" -> "18", "cache" -> "npm")
  ),
  WorkflowStep.Run(List("npm install"))
)

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
    .settings(commonSettings: _*)
    .settings(
      name  := "react-aladin",
      libraryDependencies ++= Seq(
        "edu.gemini"                        %%% "lucuma-core"         % lucumaCoreVersion,
        "edu.gemini"                        %%% "lucuma-ui"           % lucumaUIVersion,
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cb_io"   % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test"                % scalaJsReact % Test,
        "edu.gemini"                        %%% "lucuma-react-common" % lucumaReactVersion,
        "org.scalameta"                     %%% "munit"               % munitVersion % Test
      ),
      Compile / sourceGenerators += Def.task {
        val srcDir         = (demo / Compile / scalaSource).value
        val srcFiles       = srcDir ** "*.scala"
        val destinationDir = (Compile / sourceManaged).value
        copyAndReplace(srcFiles.get, srcDir, destinationDir)
      }.taskValue,
      scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
      jsEnv := new lucuma.LucumaJSDOMNodeJSEnv()
    )

lazy val commonSettings = Seq(
  description     := "react component for aladin",
  // By necessity facades will have unused params
  tlFatalWarnings := false
)
