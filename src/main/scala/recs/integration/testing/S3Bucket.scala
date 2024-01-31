package recs.integration.testing

/**
 * Helper trait for creating and destroying a single S3 bucket to be used in a LocalStack based test
 *
 * Compared to S3Buckets this makes the common case of needing a single bucket more readable
 */
trait S3Bucket extends S3Buckets {
  val bucket: String
  lazy val buckets: Seq[String] = Seq(bucket)
}
