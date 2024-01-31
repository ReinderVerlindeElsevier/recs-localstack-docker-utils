package recs.integration.testing

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Args, DoNotDiscover}
import recs.integration.testing.utils.TestFailureReporter

/**
 * Helper class that allows S3ObjectsSpec to create and run a test suite that implements S3Objects,
 * run it, and verify that it cleans up after itself.
 */
@DoNotDiscover
class S3ObjectsSpecHelper(val bucket: String, val s3Objects: Iterable[(String, String)])
  extends AnyFlatSpec with Matchers with S3Objects {
  it should "automatically create the objects in the bucket" in {
    s3Objects.foreach { case (name, contents) =>
      S3TestUtils.assertObjectExistsWithContents(amazonS3, bucket, name, contents)
    }
  }
}

@DoNotDiscover
class S3ObjectsSpec extends AnyFlatSpec with Matchers with S3Bucket with S3Objects {

  val bucket = "s3objectsspec"

  val s3Objects: Iterable[(String, String)] = Map(
    "object1" -> "contents-1",
    "object2" -> "contents-2",
  )

  it should "automatically create the objects in the bucket" in {
    s3Objects.foreach { case (name, contents) =>
      S3TestUtils.assertObjectExistsWithContents(amazonS3, bucket, name, contents)
    }
  }

  it should "automatically delete the objects from the bucket" in {
    val subSpecS3Objects = Map(
      "object3" -> "contents-3",
    )
    val subSpec = new S3ObjectsSpecHelper(bucket, subSpecS3Objects)
    // Run the test suite. That verifies that the objects specified in subSpecBucketsAndObjects are created
    subSpec.run(None, Args(TestFailureReporter))

    // Verify that the objects specified in subSpecBucketsAndObjects were deleted
    subSpecS3Objects.foreach { case (name, _) =>
      S3TestUtils.assertObjectDoesNotExist(amazonS3, bucket, name)
    }
    // Also, the objects specified in bucketsAndObjects should still exist
    s3Objects.foreach { case (name, contents) =>
      S3TestUtils.assertObjectExistsWithContents(amazonS3, bucket, name, contents)
    }
  }
}
