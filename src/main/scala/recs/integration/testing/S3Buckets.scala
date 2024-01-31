package recs.integration.testing

import org.scalatest.{BeforeAndAfterAll, Suite}
import S3TestUtils.clearAndDeleteBucket

/**
 * Helper trait for creating and destroying S3 buckets to be used in a LocalStack based test
 */
trait S3Buckets extends Suite with LocalStackEndpoint with BeforeAndAfterAll {
  /**
   * S3 buckets to create/destroy in the container before/after running any test
   */
  protected val buckets: Seq[String]

  override def beforeAll(): Unit = {
    super.beforeAll()
    buckets.foreach { bucket =>
      try {
        amazonS3.createBucket(bucket)
      } catch {
        case e: Exception =>
          throw new Exception(s"S3Buckets: could not create bucket '$bucket'", e)
      }
    }
  }

  override def afterAll(): Unit = {
    buckets.foreach { bucket =>
      try {
        clearAndDeleteBucket(amazonS3, bucket)
      } catch {
        case e: Exception =>
          throw new Exception(s"S3Buckets: could not clear and delete bucket '$bucket'", e)
      }
    }
    super.afterAll()
  }
}
