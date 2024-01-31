package recs.docker.utils

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

@DoNotDiscover
class LocalStackAWSSecretsSpec extends AnyFlatSpec with Matchers with LocalStackAWSSecrets {
  override val secrets: Map[String, String] = Map(
    s"LocalStackAWSSecretsSpec-secret-ID-${UUID.randomUUID()}" -> s"LocalStackAWSSecretsSpec-secret-value-${UUID.randomUUID()}",
    s"LocalStackAWSSecretsSpec-secret-ID-${UUID.randomUUID()}" -> s"LocalStackAWSSecretsSpec-secret-value-${UUID.randomUUID()}"
  )

  it should "automatically create secrets" in {
    secrets.foreach { case (secretID, secretValue) =>
      val expected = secretValue
      val actual = LocalStackSecretsManagerUtils.getSecret(secretID)
      actual shouldBe expected
    }
  }
}
