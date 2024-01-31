package recs.docker.utils

/**
 * Helper trait for creating and destroying an AWS Secret to be used in a LocalStack based test
 */
trait LocalStackAWSSecret extends LocalStackAWSSecrets {
  /**
   * S3 secret and its values to create/destroy in the container before/after running any test
   */
  protected val secretID: String
  protected val secretValue: String

  protected val secrets = Map(secretID -> secretValue)
}
