package recs.integration.testing

/**
 * Helper trait for creating and destroying an AWS Secret to be used in a LocalStack based test
 *
 * Compared to AWSSecret this makes the common case of needing a single secret more readable
 */
trait AWSSecret extends AWSSecrets {
  /**
   * S3 secret and its values to create/destroy in the container before/after running any test
   */
  protected val secretID: String
  protected val secretValue: String

  protected lazy val secrets: Map[String, String] = Map(secretID -> secretValue)
}
