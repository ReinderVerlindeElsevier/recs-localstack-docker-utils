package recs.docker.utils

import org.scalatest.{BeforeAndAfterAll, Suite}

/**
 * Helper trait for creating and destroying AWS Secrets to be used in a LocalStack based test
 */
trait LocalStackAWSSecrets extends Suite with BeforeAndAfterAll {
  /**
   * S3 secrets and their values to create/destroy in the container before/after running any test
   */
  protected val secrets: Map[String, String]

  override def beforeAll(): Unit = {
    super.beforeAll()
    secrets.foreach { case (secretID, secretValue) =>
      LocalStackSecretsManagerUtils.createSecret(secretID, secretValue)
    }
  }

  override def afterAll(): Unit = {
    secrets.keys.foreach { secretID =>
      LocalStackSecretsManagerUtils.deleteSecret(secretID)
    }
    super.afterAll()
  }
}
