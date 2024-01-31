package recs.docker.utils

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

/**
* Minimalistic test to verify we can launch a LocalStack container
*/
@DoNotDiscover
class LocalStackDockerContainerSpec extends AnyFlatSpec with Matchers with LocalStackEndpoint {

  it should "run a container" in {
    succeed
  }

  it should "know how to add one and one" in {
    (1 + 1) shouldBe 2
  }

  it should "access the container" in {
    import scala.language.postfixOps
    val result = RunAWSCommand("s3api create-bucket --bucket foo")
    result.exitCode shouldBe 0
    result.stderr shouldBe ""
    result.stdout shouldBe
      """{
        |    "Location": "/foo"
        |}
        |""".stripMargin
  }
}
