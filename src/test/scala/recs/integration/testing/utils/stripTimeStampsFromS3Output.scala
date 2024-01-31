package recs.integration.testing.utils

/**
 * Helper to strip timestamps from the output of 'aws s3 ls' commands
 * (useful when writing tests that compare such output with expected output)
 */
object stripTimeStampsFromS3Output {
  def apply(s: String): String = s.split("\\n")
    .map(_.replaceFirst("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", ""))
    .mkString("\n")
}
