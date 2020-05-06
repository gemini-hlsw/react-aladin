val reactJS      = "16.7.0"
val scalaJsReact = "1.6.0"

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

def copyAndReplace(srcFiles: Seq[File], destinationDir: File): Seq[File] = {
  // Copy a directory and return the list of files
  def copyDirectory(
    source:               File,
    target:               File,
    overwrite:            Boolean = false,
    preserveLastModified: Boolean = false
  ): Set[File] =
    IO.copy(PathFinder(source).allPaths.pair(Path.rebase(source, target)).toTraversable,
            overwrite,
            preserveLastModified,
            false)
  def replacements(line: String): String =
    line
      .replaceAll("../aladin/", "@cquiroz/aladin-lite/lib/")

  // Visit each file and read the content replacing key strings
  srcFiles.foreach { f =>
    val replacedLines = IO.readLines(f).map(replacements)
    IO.writeLines(f, replacedLines)
  }
  srcFiles
}
lazy val facade =
  project
    .in(file("."))
    .enablePlugins(ScalaJSPlugin)
    .enablePlugins(ScalaJSBundlerPlugin)
    .settings(commonSettings: _*)
    .settings(
      name := "react-aladin",
      npmDependencies in Compile ++= Seq(
        "react" -> reactJS,
        "react-dom" -> reactJS,
        "@cquiroz/aladin-lite" -> "0.1.3"
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
        "com.github.japgolly.scalajs-react" %%% "core" % scalaJsReact,
        "com.github.japgolly.scalajs-react" %%% "test" % scalaJsReact % Test,
        "io.github.cquiroz.react" %%% "common" % "0.7.1",
        "com.lihaoyi" %%% "utest" % "0.7.4" % Test
      ),
      testFrameworks += new TestFramework("utest.runner.Framework"),
      Compile / sourceGenerators += Def.task {
        val srcDirs        = (Compile / unmanagedSources).value
        val destinationDir = (Compile / sourceManaged).value
        copyAndReplace(srcDirs, destinationDir)
      }.taskValue
    )

lazy val commonSettings = Seq(
  scalaVersion := "2.13.2",
  organization := "io.github.cquiroz.react",
  sonatypeProfileName := "io.github.cquiroz",
  description := "react component for aladin",
  homepage := Some(url("https://github.com/cquiroz/react-aladin")),
  scalacOptions ~= (_.filterNot(
    Set(
      // By necessity facades will have unused params
      "-Wdead-code",
      "-Wunused:params"
    )
  )),
  scalacOptions += "-P:scalajs:sjsDefinedByDefault"
)
