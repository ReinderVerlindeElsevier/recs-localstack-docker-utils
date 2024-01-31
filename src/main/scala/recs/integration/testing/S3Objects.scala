package recs.integration.testing

/**
 * Helper trait for creating and destroying S3 objects to be used in a LocalStack based test
 *
 * Compared to MultiBucketS3Objects this makes the common case of needing objects in a single bucket more readable
 */
trait S3Objects extends MultiBucketS3Objects {
  protected val bucket: String
  protected val s3Objects: Iterable[(String, String)]
  /**
   * S3 objects and their contents to create/destroy before/after running any test
   */
  protected lazy val bucketsAndObjects: Iterable[(String, Iterable[(String, String)])] = Seq(bucket -> s3Objects)
}
