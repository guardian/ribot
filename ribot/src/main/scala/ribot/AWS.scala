package ribot

import com.amazonaws.auth.{InstanceProfileCredentialsProvider, SystemPropertiesCredentialsProvider, EnvironmentVariableCredentialsProvider, AWSCredentialsProviderChain}
import com.amazonaws.auth.profile.ProfileCredentialsProvider

object AWS {
  val credentials =
    new AWSCredentialsProviderChain(
      new EnvironmentVariableCredentialsProvider,
      new SystemPropertiesCredentialsProvider,
      new ProfileCredentialsProvider("profile billing"),
      new InstanceProfileCredentialsProvider
    )

}
