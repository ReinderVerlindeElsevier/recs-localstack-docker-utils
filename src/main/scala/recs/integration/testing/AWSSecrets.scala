package recs.integration.testing

import org.scalatest.{BeforeAndAfterAll, Suite}

/**
 * Helper trait for creating and destroying AWS Secrets to be used in a LocalStack based test
 */
trait AWSSecrets extends Suite with BeforeAndAfterAll {
  /**
   * S3 secrets and their values to create/destroy in the container before/after running any test
   */
  protected val secrets: Map[String, String]

  override def beforeAll(): Unit = {
    super.beforeAll()
    secrets.foreach { case (secretID, secretValue) =>
      try {
        SecretsManagerUtils.createSecret(secretID, secretValue)
      } catch {
        case e: Exception =>
          throw new Exception(s"AWSSecrets: could not create secret '$secretID'", e)
      }
    }
  }

  override def afterAll(): Unit = {
    secrets.keys.foreach { secretID =>
      try {
        SecretsManagerUtils.deleteSecret(secretID)
      } catch {
        case e: Exception =>
          throw new Exception(s"AWSSecrets: could not delete secret '$secretID'", e)
      }
    }
    super.afterAll()
  }
}
