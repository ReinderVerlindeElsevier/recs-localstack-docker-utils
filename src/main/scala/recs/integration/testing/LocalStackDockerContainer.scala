package recs.integration.testing

import org.scalatest.Suite

/**
 * Helper trait for LocalStack based tests
 */
trait LocalStackDockerContainer extends Suite with LocalStackEndpoint with DockerContainer {
  val dockerComposePath = "docker/docker-compose.yml"

  /**
   * (Port, service name) to open in the container. Must match (part of) the services defined in the docker compose file
   */
  val exposedServices: Seq[NamedPort] = Seq(
    NamedPort("localstack", localStackPort)
  )
}
