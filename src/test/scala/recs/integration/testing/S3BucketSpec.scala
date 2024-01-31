package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import recs.integration.testing.utils.{RunLocalStackCommand, stripTimeStampsFromS3Output}

@DoNotDiscover
class S3BucketSpec extends AnyFlatSpec with Matchers with LocalStackEndpoint with S3Bucket {
  val bucket = "s3bucketspec-foo"

  it should "automatically create a bucket" in {
    S3TestUtils.createObjects(amazonS3, bucket, Seq("foo1", "foo2"))

    var result = RunLocalStackCommand(s"s3 ls --recursive s3://$bucket")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    stripTimeStampsFromS3Output(result.stdout) shouldBe
      """          4 foo1
        |          4 foo2""".stripMargin
  }
}
