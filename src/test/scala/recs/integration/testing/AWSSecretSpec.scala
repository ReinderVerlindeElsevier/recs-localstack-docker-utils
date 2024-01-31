package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

@DoNotDiscover
class AWSSecretSpec extends AnyFlatSpec with Matchers with AWSSecret {
  override val secretID = s"AWSSecretSpec-secret-ID-${UUID.randomUUID()}"
  override val secretValue = s"AWSSecretSpec-secret-value-${UUID.randomUUID()}"

  it should "automatically create a secret" in {
    val expected = secretValue
    val actual = SecretsManagerUtils.getSecret(secretID)
    actual shouldBe expected
  }
}
