val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "telegram-llm-bot",
    version := "0.1.1",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "io.cequence" %% "openai-scala-client" % "1.0.0",
      "com.softwaremill.sttp.client" %% "okhttp-backend" % "2.3.0" cross CrossVersion.for3Use2_13,
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-future" % "3.9.7" cross CrossVersion.for3Use2_13,
      "com.bot4s" %% "telegram-core" % "5.8.2" cross CrossVersion.for3Use2_13,
    )
  )
