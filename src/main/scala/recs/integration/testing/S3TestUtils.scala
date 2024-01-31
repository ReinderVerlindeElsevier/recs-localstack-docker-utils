package recs.integration.testing

import com.amazonaws.services.s3.AmazonS3

object S3TestUtils {
  private val groupSize = 25
  /**
   * Helper method for creating a series of objects in S3
   * Each object will have text contents equal to its S3 key.
   *
   * @param amazonS3 client to use for creating the objects
   * @param bucket   bucket in which to create the buckets
   * @param keys     keys to create objects for
   */
  def createObjects(amazonS3: AmazonS3, bucket: String, keys: Seq[String]): Unit =
    createObjects(amazonS3, bucket, keys.map(key => (key, key)))
  
  /**
   * Helper method for creating a series of objects in S3
   *
   * @param amazonS3      client to use for creating the objects
   * @param bucket        bucket in which to create the buckets
   * @param keyValuePairs (key, value) pairs to create objects for
   */
  def createObjects(amazonS3: AmazonS3, bucket: String, keyValuePairs: Iterable[(String, String)]): Unit = {
    keyValuePairs.grouped(groupSize).foreach { keyValuePair =>
      keyValuePair.par.foreach { keyValuePair =>
        writeObject(amazonS3, bucket, keyValuePair._1, keyValuePair._2)
      }
    }
  }

  def objectExists(amazonS3: AmazonS3, bucket: String, key: String): Boolean = amazonS3.doesObjectExist(bucket, key)

  def assertObjectExists(amazonS3: AmazonS3, bucket: String, key: String): Unit = {
    if(!amazonS3.doesObjectExist(bucket, key)) {
      throw new AssertionError(s"Object $key does not exist in bucket $bucket")
    }
  }

  def assertObjectExistsWithContents(amazonS3: AmazonS3, bucket: String, key: String, contents: String): Unit = {
    try {
      val value = readObject(amazonS3, bucket, key)
      if (value != contents) {
        throw new AssertionError(s"Object $key exists in bucket $bucket, but its contents are '$value'. Expected '$contents'")
      }
    } catch {
      case e: Exception =>
        throw new AssertionError(s"Object $key does not exist in bucket $bucket. Expected it to be there with contents '$contents", e)
    }
  }

  def assertObjectDoesNotExist(amazonS3: AmazonS3, bucket: String, key: String): Unit = {
    if(amazonS3.doesObjectExist(bucket, key)) {
      throw new AssertionError(s"Object $key exists in bucket $bucket. Did not expect it to be there")
    }
  }

  def writeObject(amazonS3: AmazonS3, bucket: String, key: String, value: String): Unit = {
    amazonS3.putObject(bucket, key, value)
  }

  def readObject(amazonS3: AmazonS3, bucket: String, key: String): String = {
    val s3Object = amazonS3.getObject(bucket, key)
    val source = scala.io.Source.fromInputStream(s3Object.getObjectContent)
    try {
      source.mkString
    } finally {
      source.close()
    }
  }

  def deleteObjects(amazonS3: AmazonS3, bucket: String, keys: Iterable[String]): Unit = {
    keys.grouped(groupSize).foreach { keys =>
      keys.par.foreach { key =>
        deleteObject(amazonS3, bucket, key)
      }
    }
  }
  
  def deleteObject(amazonS3: AmazonS3, bucket: String, key: String): Unit = {
    amazonS3.deleteObject(bucket, key)
  }

  /**
   * Helper method for deleting all files from a S3 bucket
   *
   * @param amazonS3 client to use for deleting the objects and the bucket
   * @param bucket   bucket to delete all files from
   * @return number of objects deleted
   */
  def clearBucket(amazonS3: AmazonS3, bucket: String): Int =
    S3Utils.deleteRecursively(amazonS3, bucket, "")

  /**
   * Helper method for deleting a bucket from S3, deleting any files in it
   *
   * @param amazonS3 client to use for deleting the objects and the bucket
   * @param bucket   bucket to destroy
   * @return number of objects deleted
   */
  def clearAndDeleteBucket(amazonS3: AmazonS3, bucket: String): Int = {
    val result = clearBucket(amazonS3, bucket)
    amazonS3.deleteBucket(bucket)
    result
  }
}
