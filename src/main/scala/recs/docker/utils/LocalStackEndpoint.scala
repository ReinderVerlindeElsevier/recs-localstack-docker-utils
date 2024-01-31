package recs.docker.utils

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicSessionCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.services.secretsmanager.{AWSSecretsManager, AWSSecretsManagerClientBuilder}

import java.util.UUID

trait LocalStackEndpoint {
  val localStackPort = 4566
  val localStackEndpointUrl: String = s"http://localhost:$localStackPort"
  private val defaultRegion = "us-east-1"

  def makeS3Client(region: String = defaultRegion): AmazonS3 =
    AmazonS3ClientBuilder
      .standard
      .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(localStackEndpointUrl, region))
      .withPathStyleAccessEnabled(true) // Hack; we need this for LocalStack
      .build

  private val awsCredentials = new BasicSessionCredentials(
    "1234",
    "1234",
    UUID.randomUUID().toString)

  val credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials)

  def makeSecretsManager(region: String = defaultRegion): AWSSecretsManager = AWSSecretsManagerClientBuilder
    .standard()
    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(localStackEndpointUrl, region))
    .withCredentials(credentialsProvider)
    .build()
}
