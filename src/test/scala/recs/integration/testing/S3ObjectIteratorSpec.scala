package recs.integration.testing

import org.scalatest.{BeforeAndAfterAll, DoNotDiscover}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import java.util.UUID

@DoNotDiscover
class S3ObjectIteratorSpec extends AnyFlatSpec with Matchers with S3Buckets with BeforeAndAfterAll {

  private val bucket = "s3objectiteratorspec"
  private val emptyBucket = "empty-s3objectiteratorspec"

  val buckets: Seq[String] = Seq(bucket, emptyBucket)

  private val names = (1 to 10).map { _ =>
    s"item-${UUID.randomUUID()}"
  }
  private val folder = s"folder-${UUID.randomUUID()}"
  private val infolderNames = (1 to 10).map { _ =>
    s"$folder/item-${UUID.randomUUID()}"
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    S3TestUtils.createObjects(amazonS3, bucket, names ++ infolderNames)
  }

  behavior of "S3ObjectIterator"

  it should "iterate over an empty bucket" in {
    val iter = new S3ObjectIterator(amazonS3, emptyBucket, "")
    iter.hasNext shouldBe false
  }

  it should "iterate over an entire bucket" in {
    val allItems = new S3ObjectIterator(amazonS3, bucket, "").toList
    allItems.map(_.getKey) should contain theSameElementsAs (names ++ infolderNames)
  }

  it should "iterate over a folder" in {
    val inFolderItems = new S3ObjectIterator(amazonS3, bucket, folder).toList
    inFolderItems.map(_.getKey) should contain theSameElementsAs infolderNames
  }

  it should "return an empty list for a non-existent folder" in {
    val noItems = new S3ObjectIterator(amazonS3, bucket, "non-existent-folder").toList
    noItems shouldBe empty
  }

  behavior of "S3ObjectIterable"

  it should "repeatably iterate over a folder" in {
    val iterable = new S3ObjectIterable(amazonS3, bucket, folder)
    val inFolderItems1 = iterable.iterator.toList
    val inFolderItems2 = iterable.iterator.toList

    inFolderItems1.map(_.getKey) should contain theSameElementsAs infolderNames
    inFolderItems2.map(_.getKey) should contain theSameElementsAs infolderNames
  }

  it should "see new items when iterating" in {
    val iterable = new S3ObjectIterable(amazonS3, bucket, "")
    val key1 = "new-items-key-1"
    val key2 = "new-items-key-2"
    S3TestUtils.writeObject(amazonS3, bucket, key1, "new-items-content-1")

    val inFolderItems1 = iterable.iterator.toList

    S3TestUtils.deleteObject(amazonS3, bucket, key1)
    S3TestUtils.writeObject(amazonS3, bucket, key2, "new-items-content-2")
    val inFolderItems2 = iterable.iterator.toList

    inFolderItems1.map(_.getKey) should contain theSameElementsAs names ++ infolderNames ++ Some(key1)
    inFolderItems2.map(_.getKey) should contain theSameElementsAs names ++ infolderNames ++ Some(key2)
  }
}
