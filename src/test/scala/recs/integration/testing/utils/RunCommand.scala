package recs.integration.testing.utils

import RunLocalStackCommand.prefix

import java.io.{PrintWriter, StringWriter}
import scala.sys.process.{Process, ProcessLogger}

case class RunResult(exitCode: Int, stdout: String, stderr: String)

/**
 * Helper to run a command and capture its output
 * Sample usages:
 * <code>
 *   RunCommand("ls -l")
 *   RunCommand(Seq("ls", "-l", "a filename with spaces") // works with arguments that contain spaces
 *   </code>
 */
object RunCommand {
  def apply(args: String): RunResult = RunCommand(args.split(" "))

  def apply(argv: Seq[String]): RunResult = {
    val outWriter = new StringWriter(65536)
    val errWriter = new StringWriter(65536)
    val outPrintWriter = new PrintWriter(outWriter)
    val errPrintWriter = new PrintWriter(errWriter)

    val errorCode = Process(argv) ! ProcessLogger(outPrintWriter.println, errPrintWriter.println)

    outWriter.flush()
    errWriter.flush()
    RunResult(errorCode, outWriter.toString, errWriter.toString)
  }
}
