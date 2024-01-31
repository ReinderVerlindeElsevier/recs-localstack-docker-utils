package recs.integration.testing

import com.amazonaws.services.s3.model.{ObjectListing, S3ObjectSummary}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import org.slf4j.LoggerFactory

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

case class S3ObjectIterable(amazonS3: AmazonS3, bucket: String, prefix: String, suffix: Option[String] = None) extends Iterable[S3ObjectSummary] {
  def iterator: Iterator[S3ObjectSummary] = S3ObjectIterator(amazonS3, bucket, prefix, suffix)
}

object S3ObjectIterable {
  private val logger = LoggerFactory.getLogger(getClass)
  private lazy val defaultClient = AmazonS3ClientBuilder.standard().build()

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  // Convenience constructor that uses default values for AmazonS3 and suffix
  def apply(bucket: String, prefix: String): S3ObjectIterable =
    S3ObjectIterable(defaultClient, bucket, prefix, None)

  // Convenience constructor that uses a default value for AmazonS3
  def apply(bucket: String, prefix: String, suffix: String): S3ObjectIterable =
    S3ObjectIterable(defaultClient, bucket, prefix, Some(suffix))

  // Convenience constructor that uses a default value of 'None' for the suffix
  def apply(amazonS3: AmazonS3, bucket: String, prefix: String): S3ObjectIterable =
    S3ObjectIterable(amazonS3, bucket, prefix, None)

  def getMoreItems(amazonS3: AmazonS3, objectListing: ObjectListing, suffix: Option[String]): Option[ObjectListing] = {
    if (objectListing.isTruncated) {
      var result: ObjectListing = objectListing
      do {
        result = amazonS3.listNextBatchOfObjects(result)
        val moreItems = filterItems(objectListing, suffix)
        val gotAtLeastOne = moreItems.nonEmpty
        if (gotAtLeastOne) {
          logger.debug(s"""S3ObjectIterator+(bucket "${objectListing.getBucketName}", prefix "${objectListing.getPrefix}", suffix "$suffix}): got more items""")
          return Some(result)
        }
      } while (objectListing.isTruncated)
      None
    } else {
      None
    }
  }

  def filterItems(objectListing: ObjectListing, suffix: Option[String]): List[S3ObjectSummary] = {
    val summaries = objectListing.getObjectSummaries.toList
    suffix match {
      case None => summaries
      case Some(sf) => summaries.filter {
        _.getKey.endsWith(sf)
      }
    }
  }
}

//
// (Somewhat) lazily iterates over all objects in the given bucket whose key starts with the given prefix
// and, if given, ends with the given suffix.
//
// Iteration order is undefined.
//
case class S3ObjectIterator(amazonS3: AmazonS3, bucket: String, prefix: String, suffix: Option[String] = None)
  extends Iterator[S3ObjectSummary] {
  import S3ObjectIterable._
  private val logger = LoggerFactory.getLogger(getClass)

  private var objectListing = amazonS3.listObjects(bucket, prefix)

  private var nextObjectListing: Future[Option[ObjectListing]] = Future(getMoreItems(amazonS3, objectListing, suffix))

  private var toProduce = filterItems(objectListing, suffix).iterator

  override def hasNext: Boolean =
    if (toProduce.hasNext) 
      true
    else
      Await.result(nextObjectListing, Duration.Inf) match {
        case Some(ol) =>
          objectListing = ol
          toProduce = objectListing.getObjectSummaries.toIterator
          nextObjectListing = Future(getMoreItems(amazonS3, objectListing, suffix))
          logger.debug(s"""S3ObjectIterator+(bucket "$bucket", prefix "$prefix", suffix "$suffix}): got more items""")
          true
        case None =>
          logger.debug(s"""S3ObjectIterator+(bucket "$bucket", prefix "$prefix", suffix "$suffix}): no more items""")
          toProduce = Iterator.empty
          false
      }

  override def next(): S3ObjectSummary =
    if (hasNext)
      toProduce.next()
    else
      // https://www.scala-lang.org/api/2.12.11/scala/collection/Iterator.html:
      // abstract def next(): A [...] returns the next element of this iterator, if hasNext is true, undefined behavior otherwise.
      throw new UnsupportedOperationException(s"""S3ObjectIterator(bucket "$bucket", prefix "$prefix", suffix "$suffix}): next() called while hasNext is false""")
}

object S3ObjectIterator {
  // Convenience constructor that uses a default value of 'None' for the suffix
  def apply(amazonS3: AmazonS3, bucket: String, prefix: String): S3ObjectIterator =
    S3ObjectIterator(amazonS3, bucket, prefix, None)
}
