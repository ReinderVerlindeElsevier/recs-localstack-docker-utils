package recs.docker.utils

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

@DoNotDiscover
class LocalStackS3BucketsSpec extends AnyFlatSpec with Matchers with LocalStackS3Buckets {
  val fooBucket = "localstacks3bucketsspec-foo"
  val barBucket = "localstacks3bucketsspec-bar"
  val buckets: Seq[String] = Seq(fooBucket, barBucket)

  it should "automatically create buckets" in {
    val client = makeS3Client()
    S3TestUtils.createTestObjects(client, fooBucket, Seq("foo1", "foo2"))
    S3TestUtils.createTestObjects(client, barBucket, Seq("bar-one", "bar-two", "bar-three"))

    var result = RunAWSCommand(s"s3 ls --recursive s3://$fooBucket")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    stripTimeStampsFromS3Output(result.stdout) shouldBe
      """          4 foo1
        |          4 foo2""".stripMargin

 result = RunAWSCommand(s"s3 ls --recursive s3://$barBucket")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    stripTimeStampsFromS3Output(result.stdout) shouldBe
      """          7 bar-one
        |          9 bar-three
        |          7 bar-two""".stripMargin
  }
}
