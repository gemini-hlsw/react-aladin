addSbtPlugin("edu.gemini"       % "sbt-lucuma-lib" % "0.11.15")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"    % "0.6.4")

libraryDependencies ++= Seq(
  "edu.gemini" %% "lucuma-jsdom" % "0.11.15"
)
