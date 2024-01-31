package recs.integration.testing.utils

import org.scalatest.Reporter
import org.scalatest.events.{Event, TestFailed}

/**
 * scalatest Reporter that prints out test failures and ignores all other events
 */
object TestFailureReporter extends Reporter {
  override def apply(event: Event): Unit = {
    event match {
      case event: TestFailed => println(event.message)
      case _ =>
    }
  }
}
