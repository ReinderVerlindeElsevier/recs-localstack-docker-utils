package recs.integration.testing

import org.scalatest.DoNotDiscover
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import recs.integration.testing.utils.RunCommand

/**
 * Minimalistic test to verify we can launch a Docker container
 *
 * This is disabled because it cannot be run in parallel with LocalStackTests.
 *
 * Run it in isolation if LocalStackDockerContainerSpec fails.
 */
@DoNotDiscover
class DockerContainerSpec extends AnyFlatSpec with Matchers with DockerContainer {

  val dockerComposePath = "src/test/resources/docker-container-spec.yml"

  override val exposedServices: Seq[NamedPort] = Seq.empty

  it should "run a container" in {
    val expected = "resources-docker-container-spec"
    RunCommand(s"docker ps | grep $expected").stdout.contains(expected)
  }
}
