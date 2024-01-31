package recs.docker.utils

import java.io.{PrintWriter, StringWriter}
import scala.sys.process.{Process, ProcessLogger}

case class RunResult(exitCode: Int, stdout: String, stderr: String)

/**
 * Helper to run a command and capture its output
 */
object RunCommand {
  def apply(command: String): RunResult = {
    val outWriter = new StringWriter(65536)
    val errWriter = new StringWriter(65536)
    val outPrintWriter = new PrintWriter(outWriter)
    val errPrintWriter = new PrintWriter(errWriter)

    val errorCode = Process(command) ! ProcessLogger(outPrintWriter.println, errPrintWriter.println)

    outWriter.flush()
    errWriter.flush()
    RunResult(errorCode, outWriter.toString, errWriter.toString)
  }
}
