package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import recs.integration.testing.utils.{RunLocalStackCommand, stripTimeStampsFromS3Output}

import java.util.UUID

@DoNotDiscover
class S3TestUtilsSpec extends AnyFlatSpec with LocalStackEndpoint with Matchers with S3Bucket {
  val bucket = "s3testutilsspec"

  it should "create test objects" in {
    val items = Seq("foo", "foo/bar/baz")
    S3TestUtils.createObjects(amazonS3, bucket, items)
    val result = RunLocalStackCommand(s"s3 ls --recursive s3://$bucket")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    stripTimeStampsFromS3Output(result.stdout) shouldBe
      """          3 foo
        |         11 foo/bar/baz""".stripMargin
  }

  it should "read objects" in {
    val name = s"to-be-read-${UUID.randomUUID()}"
    S3TestUtils.writeObject(amazonS3, bucket, name, name)
    S3TestUtils.objectExists(amazonS3, bucket, name) shouldBe true
    val value = S3TestUtils.readObject(amazonS3, bucket, name)
    value shouldBe name
  }

  it should "clear a bucket" in {
    val bucket = s"to-be-cleared-${UUID.randomUUID()}"
    val folder = s"folder-${UUID.randomUUID()}"
    val names = (1 to 10).map { _ =>
      s"item-${UUID.randomUUID()}"
    } ++ (1 to 10).map { _ =>
      s"$folder/item-${UUID.randomUUID()}"
    }
    // Create a bucket and items inside it
    amazonS3.createBucket(bucket)
    S3TestUtils.createObjects(amazonS3, bucket, names)

    // Verify that they are there
    names.foreach { name =>
      S3TestUtils.readObject(amazonS3, bucket, name) shouldBe name
    }

    // Clear the bucket
    S3TestUtils.clearBucket(amazonS3, bucket)

    // Verify that they are gone
    RunLocalStackCommand(s"s3 ls --recursive s3://$bucket").stdout shouldBe ""

    // Delete the bucket
    amazonS3.deleteBucket(bucket)

    // Verify that it is gone
    RunLocalStackCommand(s"s3 ls s3://$bucket").stderr.contains("An error occurred (NoSuchBucket)") shouldBe true
  }
}
