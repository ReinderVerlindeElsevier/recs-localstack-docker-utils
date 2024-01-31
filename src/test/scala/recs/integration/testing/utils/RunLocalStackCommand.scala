package recs.integration.testing.utils

import recs.integration.testing.LocalStackEndpoint

/**
 * Helper to run a LocalStack command and capture its output
 *
 * Sample usages:
 * <code>
 *   RunLocalStackCommand("s3 ls s3://my-bucket")
 *   RunLocalStackCommand(Seq("s3", "ls", "s3://my-bucket") // works with arguments that contain spaces
 *   </code>
 */
object RunLocalStackCommand extends LocalStackEndpoint {
  private val prefix = Seq("aws", "--endpoint-url", localStackEndpointUrl)

  def apply(args: String): RunResult = RunCommand(prefix ++ args.split(" "))

  def apply(argv: Seq[String]): RunResult = RunCommand(prefix ++ argv)
}
