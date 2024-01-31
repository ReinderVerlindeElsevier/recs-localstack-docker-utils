package recs.docker.utils

import org.scalatest.Suites
import org.scalatest.flatspec.AnyFlatSpec

/**
 * Suite that runs the LocalStack tests
 *
 * This has to be a suite because we do not want to start a LocalStack instance for each of these tests.
 *
 * The tests in `testClasses` have a `@DoNotDiscover` annotation so they are not run by themselves.
 */
object LocalStackTests {
  val testClasses: Seq[AnyFlatSpec] = Seq(
    new LocalStackDockerContainerSpec,
    new LocalStackS3BucketsSpec,
    new S3ObjectIteratorSpec,
    new S3TestUtilsSpec,
    new LocalStackSecretsManagerUtilsSpec,
  )
}

class LocalStackTests
  extends Suites(LocalStackTests.testClasses: _*)
  with LocalStackDockerContainer
