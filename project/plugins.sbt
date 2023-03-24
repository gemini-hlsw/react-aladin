addSbtPlugin("edu.gemini"       % "sbt-lucuma-lib" % "0.10.11")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"    % "0.6.4")

libraryDependencies ++= Seq(
  "edu.gemini" %% "lucuma-jsdom" % "0.10.11"
)
