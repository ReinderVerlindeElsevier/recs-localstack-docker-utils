# About

Common code that helps in writing tests that use LocalStack.

Main functionality:

- Launch and bring down a Docker Container in a test: `with LocalStackDockerContainer`
- Traits to create and destroy S3 bucket(s) in a test: `with LocalStackS3Bucket`, 
  `with LocalStackS3Buckets`
- Traits to create and delete SecretsManager Secrets in a test: `with AWSSecret`, `with AWSSecrets`
- Read and write S3 objects for tests to find them: `S3TestUtils`
- Iterate over S3 objects: `S3ObjectIterable`, `S3ObjectIterator`
- delete all S3 objects in a bucket: `S3Utils.deleteRecursively`

# Examples

For examples, look in the  `src/test/scala/recs/integration/testing` directory:

- a test that runs a Docker container: see `DockerContainerSpec`
- a test that runs LocalStack in a Docker container: `LocalStackDockerContainerSpec`
- a test that requires LocalStack and an S3 bucket: `S3BucketSpec`
- a test that requires LocalStack and multiple S3 buckets: `S3BucketsSpec`
- a test that requires LocalStack and an AWS Secrets: `AWSSecretSpec`
- a test that requires LocalStack and multiple AWS Secrets: `AWSSecretsSpec`
- a test that requires LocalStack, S3 buckets and objects in S3: `S3ObjectSpec`
- 