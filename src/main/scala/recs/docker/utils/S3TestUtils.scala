package recs.docker.utils

import com.amazonaws.services.s3.AmazonS3

object S3TestUtils {

  /**
   * Helper method for creating a series of objects in S3
   * Each object will have text contents equal to its S3 key.
   *
   * @param amazonS3 client to use for creating the objects
   * @param bucket   bucket in which to create the buckets
   * @param keys     keys to create objects for
   */
  def createTestObjects(amazonS3: AmazonS3, bucket: String, keys: Seq[String]): Unit = {
    keys.grouped(25).foreach { keys =>
      keys.par.foreach { key =>
        writeObject(amazonS3, bucket, key, key)
      }
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
   * @param bucket  bucket to destroy
   * @return number of objects deleted
   */
  def clearAndDeleteBucket(amazonS3: AmazonS3, bucket: String): Int = {
    val result = clearBucket(amazonS3, bucket)
    amazonS3.deleteBucket(bucket)
    result
  }
}
