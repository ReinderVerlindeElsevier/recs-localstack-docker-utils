package recs.docker.utils

/**
 * Helper trait for creating and destroying a single S3 bucket to be used in a LocalStack based test
 *
 * Compared to LocalStackS3Buckets this makes the common case of needing a single bucket more readable
 */
trait LocalStackS3Bucket extends LocalStackS3Buckets {
  val bucket: String
  lazy val buckets: Seq[String] = Seq(bucket)
}
