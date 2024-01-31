package recs.integration.testing

import org.scalatest.Suites
import org.scalatest.flatspec.AnyFlatSpec

object LocalStackTests {
  val testClasses: Seq[AnyFlatSpec] = Seq(
    new LocalStackDockerContainerSpec,
    new S3BucketSpec,
    new S3BucketsSpec,
    new AWSSecretSpec,
    new AWSSecretsSpec,
    new S3ObjectsSpec,
    new MultiBucketS3ObjectsSpec,
    new S3ObjectIteratorSpec,
    new S3TestUtilsSpec,
    new SecretsManagerUtilsSpec,
  )
}

/**
 * Suite that runs the LocalStack tests
 *
 * This has to be a suite because we do not want to start a LocalStack instance for each of these tests.
 *
 * The tests in `testClasses` have a `@DoNotDiscover` annotation so they are not run by themselves.
 */
class LocalStackTests
  extends Suites(LocalStackTests.testClasses: _*)
  with LocalStackDockerContainer
