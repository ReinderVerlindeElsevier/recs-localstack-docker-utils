package recs.docker.utils

/**
 * Helper to run an AWS command and capture its output
 */
object RunAWSCommand extends LocalStackEndpoint {
  def apply(command: String): RunResult = RunCommand(s"aws --endpoint-url $localStackEndpointUrl $command")
}
