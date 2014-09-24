package ribot

import com.amazonaws.auth.{InstanceProfileCredentialsProvider, SystemPropertiesCredentialsProvider, EnvironmentVariableCredentialsProvider, AWSCredentialsProviderChain}
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.s3.AmazonS3Client

object AWS {
  val credentials =
    new AWSCredentialsProviderChain(
      new EnvironmentVariableCredentialsProvider,
      new SystemPropertiesCredentialsProvider,
      new ProfileCredentialsProvider("billing"),
      new InstanceProfileCredentialsProvider
    )

  lazy val s3 = new AmazonS3Client(credentials)
}
