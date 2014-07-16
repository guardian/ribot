package ribot

import com.amazonaws.auth.profile.ProfileCredentialsProvider

object AWS {
  val credentials = new ProfileCredentialsProvider("profile billing")

}
