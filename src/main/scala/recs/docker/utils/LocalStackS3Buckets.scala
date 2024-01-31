package recs.docker.utils

import org.scalatest.{BeforeAndAfterAll, Suite}
import recs.docker.utils.S3TestUtils.clearAndDeleteBucket

/**
 * Helper trait for creating and destroying S3 buckets to be used in a LocalStack based test
 */
trait LocalStackS3Buckets extends Suite with LocalStackEndpoint with BeforeAndAfterAll {
  /**
   * S3 buckets to create/destroy in the container before/after running any test
   */
  protected val buckets: Seq[String]

  override def beforeAll(): Unit = {
    super.beforeAll()
    val amazonS3 = makeS3Client()
    buckets.foreach { bucket =>
      amazonS3.createBucket(bucket)
    }
  }

  override def afterAll(): Unit = {
    val amazonS3 = makeS3Client()
    buckets.foreach { bucket =>
      clearAndDeleteBucket(amazonS3, bucket)
    }
    super.afterAll()
  }
}
