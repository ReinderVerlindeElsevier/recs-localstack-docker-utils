package recs.integration.testing

import com.dimafeng.testcontainers.{Container, DockerComposeContainer, ExposedService}
import org.scalatest.{BeforeAndAfterAll, Suite, SuiteMixin}

import java.io.File
import scala.util.{Failure, Success, Try}

/**
 * SuiteMixin that starts a testcontainers Docker container before all tests and stops it after all tests
 */
trait DockerContainer extends SuiteMixin with Suite with BeforeAndAfterAll {
  this: Suite =>

  /**
   * Where to find the docker compose file (full or relative path)
   */
  val dockerComposePath: String

  /**
   * (Port, service name) to open in the container. Must match (part of) the services defined in the docker compose file
   */
  val exposedServices: Seq[NamedPort]

  /**
   * Name of environment variable that can be used to allow tests to run against an externally started Docker to avoid
   * having to restart Docker when running tests repeatedly in the IDE.
   *
   * - if false or not set: lets JUnit start and stop containers when running tests
   * (this is the default, to be used in Jenkins jobs)
   * - if set to true, run `docker-compose up` once, and then run tests as often as required.
   * (this is useful during development, when you're likely to run tests repeatedly)
   */
  private val useExternalResourcesEnvironmentVariableName = "LOCALSTACK_USE_EXTERNAL_RESOURCES"

  private val useExternalResources: Boolean = scala.util.Properties.envOrNone(useExternalResourcesEnvironmentVariableName) match {
    case None => false
    case Some(s) => s match {
      case "" => true
      case _ => Try(s.toBoolean) match {
        case Success(b) => b
        case Failure(e) =>
          throw new Exception(s"Could not interpret value of $useExternalResourcesEnvironmentVariableName environment variable as a boolean (got '$s')", e)
      }
    }
  }

  private lazy val container: Container = {
    if (useExternalResources) {
      new Container {
        override def stop(): Unit = {}

        override def start(): Unit = {}
      }
    } else {
      DockerComposeContainer(new File(dockerComposePath),
        exposedServices = exposedServices.map { pair => ExposedService(pair.name, pair.port) }
      )
    }
  }

  override def beforeAll(): Unit = {
    container.start()
  }

  override def afterAll(): Unit = {
    container.stop()
  }
}
