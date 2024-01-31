package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

@DoNotDiscover
class SecretsManagerUtilsSpec extends AnyFlatSpec with Matchers {
  it should "create and retrieve a secret" in {
    val secretID = s"just-a-secret-${UUID.randomUUID()}"
    val secretValue = s"just-a-value-${UUID.randomUUID()}"

    SecretsManagerUtils.createSecret(secretID, secretValue)

    val actual = SecretsManagerUtils.getSecret(secretID)

    actual shouldBe secretValue

    SecretsManagerUtils.deleteSecret(secretID)
  }

  it should "delete a secret" in {
    val secretID = s"just-a-secret-${UUID.randomUUID()}"
    val secretValue = s"just-a-value-${UUID.randomUUID()}"

    SecretsManagerUtils.createSecret(secretID, secretValue)
    SecretsManagerUtils.deleteSecret(secretID)
  }

  it should "throw ResourceNotFoundException when trying to read a non-existent secret" in {
    val secretID = s"just-a-secret-${UUID.randomUUID()}"
    intercept[com.amazonaws.services.secretsmanager.model.ResourceNotFoundException](
      SecretsManagerUtils.getSecret(secretID)
    )
  }

  it should "throw ResourceExistsException when trying to create an existing secret" in {
    val secretID = s"just-a-secret-${UUID.randomUUID()}"
    val secretValue = s"just-a-value-${UUID.randomUUID()}"

    SecretsManagerUtils.createSecret(secretID, secretValue)

    intercept[com.amazonaws.services.secretsmanager.model.ResourceExistsException] {
      SecretsManagerUtils.createSecret(secretID, secretValue)
    }

    SecretsManagerUtils.deleteSecret(secretID)
  }

  it should "ignore deleting a non-existent secret" in {
    //
    // To support debugging with LOCALSTACK_USE_EXTERNAL_RESOURCES=true, we want the ability
    // to delete an existing secret and then rapidly create one with the same name.
    //
    // To do that we have to set request.setForceDeleteWithoutRecovery(true) in 'deleteSecret' to prevent
    // an InvalidRequestException in the subsequent 'createSecret' call:
    //
    // "You can't create this secret because a secret with this name is already scheduled for deletion."
    //
    // Doing 'request.setForceDeleteWithoutRecovery(true)' gives us this unwanted behavior
    // that we'll have to live with.
    //
    val secretID = s"nonexistent-secret-${UUID.randomUUID()}"
    SecretsManagerUtils.deleteSecret(secretID)
  }

  it should "recreate a secret just after deleting it" in {
    val secretID = s"just-a-secret-${UUID.randomUUID()}"
    val secretValue = s"just-a-value-${UUID.randomUUID()}"

    SecretsManagerUtils.createSecret(secretID, secretValue)
    SecretsManagerUtils.deleteSecret(secretID)
    SecretsManagerUtils.createSecret(secretID, secretValue)
    SecretsManagerUtils.deleteSecret(secretID)
  }
}
