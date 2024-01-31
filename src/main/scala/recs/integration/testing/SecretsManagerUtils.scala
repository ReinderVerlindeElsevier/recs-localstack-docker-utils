package recs.integration.testing

import com.amazonaws.services.secretsmanager.model.{CreateSecretRequest, DeleteSecretRequest, GetSecretValueRequest}

object SecretsManagerUtils extends LocalStackEndpoint {
  /**
   * Creates a secret with the given ID and value
   *
   * @throws com.amazonaws.services.secretsmanager.model.ResourceExistsException if a secret with the given ID already exists
   */
  def createSecret(secretID: String, secretValue: String): Unit =
    awsSecretsManager.createSecret(new CreateSecretRequest()
      .withName(secretID).withSecretString(secretValue)
      .withRequestCredentialsProvider(credentialsProvider)
    )

  def getSecret(secretID: String): String =
    awsSecretsManager.getSecretValue(new GetSecretValueRequest()
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
    awsSecretsManager.deleteSecret(deleteRequest)
  }
}
