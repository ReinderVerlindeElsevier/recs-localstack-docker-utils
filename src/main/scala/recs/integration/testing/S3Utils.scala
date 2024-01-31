package recs.integration.testing

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.DeleteObjectsRequest
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion

object S3Utils {
  /**
   * Recursively deletes all S3 objects in the given bucket whose key has the given prefix,
   * using the AWS CLI utilities
   *
   * returns the number of objects deleted
   */
  def deleteRecursively(amazonS3: AmazonS3, bucket: String, prefix: String): Int = {
    var result = 0
    val maxItemsInDeleteObjectsRequest = 1000 // http://docs.aws.amazon.com/AmazonS3/latest/API/multiobjectdeleteapi.html
    new S3ObjectIterator(amazonS3, bucket, prefix, None)
      .grouped(maxItemsInDeleteObjectsRequest)
      .foreach { group =>
        import collection.JavaConverters._
        val keyVersions = group.map(item => new KeyVersion(item.getKey)).toList.asJava
        val deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keyVersions)
        amazonS3.deleteObjects(deleteObjectsRequest)
        result += keyVersions.size()
      }
    result
  }
}
