# About

Common code that helps in writing tests that use LocalStack.

Main functionality:

- Launch and bring down a Docker Container in a test: `with LocalStackDockerContainer`
- In that container, create and destroy S3 bucket(s): `with LocalStackS3Bucket`, 
  `with LocalStackS3Buckets`
- Create and read SecretsManager Secrets
- Read and write S3 objects for tests to find them: `S3TestUtils`
- Iterate over S3 objects: `S3ObjectIterable`, `S3ObjectIterator`
