package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import recs.integration.testing.utils.RunLocalStackCommand

/**
* Minimalistic test to verify we can launch a LocalStack container
*/
@DoNotDiscover
class LocalStackDockerContainerSpec extends AnyFlatSpec with Matchers {

  it should "be able to access the container" in {
    import scala.language.postfixOps
    val result = RunLocalStackCommand("s3api create-bucket --bucket foo")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    result.stdout shouldBe
      """{
        |    "Location": "/foo"
        |}
        |""".stripMargin
  }
}
