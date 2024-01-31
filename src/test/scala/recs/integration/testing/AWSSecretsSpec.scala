package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

@DoNotDiscover
class AWSSecretsSpec extends AnyFlatSpec with Matchers with AWSSecrets {
  override val secrets: Map[String, String] = Map(
    s"AWSSecretsSpec-secret-ID-${UUID.randomUUID()}" -> s"AWSSecretsSpec-secret-value-${UUID.randomUUID()}",
    s"AWSSecretsSpec-secret-ID-${UUID.randomUUID()}" -> s"AWSSecretsSpec-secret-value-${UUID.randomUUID()}"
  )

  it should "automatically create secrets" in {
    secrets.foreach { case (secretID, secretValue) =>
      val expected = secretValue
      val actual = SecretsManagerUtils.getSecret(secretID)
      actual shouldBe expected
    }
  }
}
