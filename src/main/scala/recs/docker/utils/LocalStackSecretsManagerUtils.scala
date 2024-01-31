package recs.docker.utils

import com.amazonaws.services.secretsmanager.model.{CreateSecretRequest, DeleteSecretRequest, GetSecretValueRequest}

object LocalStackSecretsManagerUtils extends LocalStackEndpoint {
  private val secretsManager = makeSecretsManager()
  /**
   * Creates a secret with the given ID and value
   *
   * @throws com.amazonaws.services.secretsmanager.model.ResourceExistsException if a secret with the given ID already exists
   */
  def createSecret(secretID: String, secretValue: String): Unit =
    secretsManager.createSecret(new CreateSecretRequest()
      .withName(secretID).withSecretString(secretValue)
      .withRequestCredentialsProvider(credentialsProvider)
    )

  def getSecret(secretID: String): String =
    secretsManager.getSecretValue(new GetSecretValueRequest()
      .withSecretId(secretID)).getSecretString

  /**
   * Deletes the secret with the given ID.
   *
   * Does <b>not</b> throw an exception if no secret with the given ID exists
   */
  def deleteSecret(secretID: String): Unit = {
    val deleteRequest: DeleteSecretRequest = new DeleteSecretRequest()
      .withSecretId(secretID)
      .withRequestCredentialsProvider(credentialsProvider)
    deleteRequest.setForceDeleteWithoutRecovery(true)
    secretsManager.deleteSecret(deleteRequest)
  }
}
