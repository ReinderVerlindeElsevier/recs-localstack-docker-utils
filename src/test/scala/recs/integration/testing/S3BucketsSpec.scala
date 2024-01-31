package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import recs.integration.testing.utils.{RunLocalStackCommand, stripTimeStampsFromS3Output}

@DoNotDiscover
class S3BucketsSpec extends AnyFlatSpec with Matchers with LocalStackEndpoint with S3Buckets {
  val fooBucket = "s3bucketsspec-foo"
  val barBucket = "s3bucketsspec-bar"
  val buckets: Seq[String] = Seq(fooBucket, barBucket)

  it should "automatically create buckets" in {
    S3TestUtils.createObjects(amazonS3, fooBucket, Seq("foo1", "foo2"))
    S3TestUtils.createObjects(amazonS3, barBucket, Seq("bar-one", "bar-two", "bar-three"))

    var result = RunLocalStackCommand(s"s3 ls --recursive s3://$fooBucket")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    stripTimeStampsFromS3Output(result.stdout) shouldBe
      """          4 foo1
        |          4 foo2""".stripMargin

 result = RunLocalStackCommand(s"s3 ls --recursive s3://$barBucket")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    stripTimeStampsFromS3Output(result.stdout) shouldBe
      """          7 bar-one
        |          9 bar-three
        |          7 bar-two""".stripMargin
  }
}
