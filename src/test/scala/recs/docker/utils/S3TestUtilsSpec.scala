package recs.docker.utils

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

@DoNotDiscover
class S3TestUtilsSpec extends AnyFlatSpec with Matchers with LocalStackS3Bucket {
  val bucket = "s3testutilsspec"

  it should "create test objects" in {
    val amazonS3 = makeS3Client()
    val items = Seq("foo", "foo/bar/baz")
    S3TestUtils.createTestObjects(amazonS3, bucket, items)
    val result = RunAWSCommand(s"s3 ls --recursive s3://$bucket")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    stripTimeStampsFromS3Output(result.stdout) shouldBe
      """          3 foo
        |         11 foo/bar/baz""".stripMargin
  }

  it should "read objects" in {
    val amazonS3 = makeS3Client()
    val name = s"to-be-read-${UUID.randomUUID()}"
    val items = Seq(name)
    S3TestUtils.createTestObjects(amazonS3, bucket, items)
    val value = S3TestUtils.readObject(amazonS3, bucket, name)
    value shouldBe name
  }

  it should "clear a bucket" in {
    val amazonS3 = makeS3Client()

    val bucket = s"to-be-cleared-${UUID.randomUUID()}"
    val folder = s"folder-${UUID.randomUUID()}"
    val names = (1 to 10).map { _ =>
      s"item-${UUID.randomUUID()}"
    } ++ (1 to 10).map { _ =>
      s"$folder/item-${UUID.randomUUID()}"
    }
    // Create a bucket and items inside it
    amazonS3.createBucket(bucket)
    S3TestUtils.createTestObjects(amazonS3, bucket, names)

    // Verify that they are there
    names.foreach{ name =>
      S3TestUtils.readObject(amazonS3, bucket, name) shouldBe name
    }

    // Clear the bucket
    S3TestUtils.clearBucket(amazonS3, bucket)

    // Verify that they are gone
    RunAWSCommand(s"s3 ls --recursive s3://$bucket").stdout shouldBe ""

    // Delete the bucket
    amazonS3.deleteBucket(bucket)
  }
}
