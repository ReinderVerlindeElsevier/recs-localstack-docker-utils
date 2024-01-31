package recs.integration.testing

import org.scalatest.{Args, DoNotDiscover}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import recs.integration.testing.utils.TestFailureReporter

/**
 * Helper class that allows S3ObjectsSpec to create and run a test suite that implements MultiBucketS3Objects,
 * run it, and verify that it cleans up after itself.
 */
@DoNotDiscover
class MultiBucketS3ObjectsSpecHelper(val buckets: Seq[String], val bucketsAndObjects: Iterable[(String, Iterable[(String, String)])])
  extends AnyFlatSpec with Matchers with MultiBucketS3Objects {
  it should "automatically create the objects in their respective buckets" in {
    bucketsAndObjects.foreach { case (bucket, keyValuePairs) =>
      keyValuePairs.foreach { case (name, value) =>
        S3TestUtils.readObject(amazonS3, bucket, name) shouldBe value
      }
    }
  }
}

@DoNotDiscover
class MultiBucketS3ObjectsSpec extends AnyFlatSpec with Matchers with S3Buckets with MultiBucketS3Objects {

  val bucket1 = "multibuckets3objectsspec-bucket1"
  val bucket2 = "multibuckets3objectsspec-bucket2"
  val buckets: Seq[String] = Seq(bucket1, bucket2)

  val bucketsAndObjects: Iterable[(String, Iterable[(String, String)])] = Map(
    bucket1 -> Map(
      "object1" -> "contents-1",
      "object2" -> "contents-2",
    ),
    bucket2 -> Map(
      "object3" -> "contents-3",
    )
  )

  it should "automatically create the objects in their respective buckets" in {
    bucketsAndObjects.foreach { case (bucket, keyValuePairs) =>
      keyValuePairs.foreach{ case (name, contents) =>
        S3TestUtils.assertObjectExistsWithContents(amazonS3, bucket, name, contents)
      }
    }
  }

  it should "automatically delete the objects from their respective buckets" in {
    val subSpecBucketsAndObjects = Map(
      bucket1 -> Map(
        "object3" -> "contents-3",
      ),
      bucket2 -> Map(
        "object1" -> "contents-1",
      ),
    )
    val subSpec = new MultiBucketS3ObjectsSpecHelper(Seq(bucket1, bucket2), subSpecBucketsAndObjects)
    // Run the test suite. That verifies that the objects specified in subSpecBucketsAndObjects are created
    subSpec.run(None, Args(TestFailureReporter))

    // Verify that the objects specified in subSpecBucketsAndObjects were deleted
    subSpecBucketsAndObjects.foreach { case (bucket, keyValuePairs) =>
      keyValuePairs.foreach{ case (name, _) =>
        S3TestUtils.assertObjectDoesNotExist(amazonS3, bucket, name)
      }
    }
    // Also, the objects specified in bucketsAndObjects should still exist
    bucketsAndObjects.foreach { case (bucket, keyValuePairs) =>
      keyValuePairs.foreach{ case (name, contents) =>
        S3TestUtils.assertObjectExistsWithContents(amazonS3, bucket, name, contents)
      }
    }
  }
}
