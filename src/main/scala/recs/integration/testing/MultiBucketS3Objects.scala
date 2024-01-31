package recs.integration.testing

import org.scalatest.{BeforeAndAfterAll, Suite}

/**
 * Helper trait for creating and destroying S3 objects to be used in a LocalStack based test
 */
trait MultiBucketS3Objects extends Suite with LocalStackEndpoint with BeforeAndAfterAll {
  /**
   * S3 buckets, objects and their contents to create/destroy before/after running any test
   */
  protected val bucketsAndObjects: Iterable[(String, Iterable[(String, String)])]

  override def beforeAll(): Unit = {
    super.beforeAll()
    bucketsAndObjects.par.foreach { case (bucket, keyValuePairs) =>
      try {
        S3TestUtils.createObjects(amazonS3, bucket, keyValuePairs)
      } catch {
        case e: Exception =>
          throw new Exception(s"MultiBucketS3Objects: could not create objects in bucket '$bucket'", e)
      }
    }
  }

  override def afterAll(): Unit = {
    bucketsAndObjects.par.foreach { case (bucket, keyValuePairs) =>
      try {
        S3TestUtils.deleteObjects(amazonS3, bucket, keyValuePairs.map(_._1))
      } catch {
        case e: Exception =>
          throw new Exception(s"MultiBucketS3Objects: could not delete objects from bucket '$bucket'", e)
      }
    }
    super.afterAll()
  }
}
