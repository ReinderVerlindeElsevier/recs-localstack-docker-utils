name := "recs-integration-testing-utils"

val testcontainersVersion = "0.40.12"
val scalatestVersion = "3.2.19"
val slf4jVersion = "2.0.16"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-s3" % RecsPlugin.awsSDKVersion,
  "com.amazonaws" % "aws-java-sdk-secretsmanager" % RecsPlugin.awsSDKVersion,

  "org.codehaus.janino" % "janino" % "3.1.12", // for the 'if condition' in logback.xml

  // c.a.u.Base64: JAXB is unavailable. Will fallback to SDK implementation which may be
  // less performant.If you are using Java 9+, you will need to include
  // javax.xml.bind:jaxb-api as a dependency.
  "javax.xml.bind" % "jaxb-api" % "2.3.1",

  "com.dimafeng" %% "testcontainers-scala-scalatest" % testcontainersVersion,

  "org.scalatest" %% "scalatest" % scalatestVersion,
  "org.scalatest" %% "scalatest-flatspec" % scalatestVersion,
) ++ RecsPlugin.routeLoggingToLogbackDependencies.map(_ % Test)

//
// https://java.testcontainers.org/features/configuration/:
//
// Disabling Ryuk
//
// Ryuk must be started as a privileged container.
// If your environment already implements automatic cleanup of containers after the
// execution, but does not allow starting privileged containers, you can turn off the Ryuk
// container by setting TESTCONTAINERS_RYUK_DISABLED environment variable to true.
//
//    Tip
//    Note that Testcontainers will continue doing the cleanup at JVM's shutdown, unless
//    you kill -9 your JVM process.
//
Test / envVars := Map("TESTCONTAINERS_RYUK_DISABLED" -> "true")
