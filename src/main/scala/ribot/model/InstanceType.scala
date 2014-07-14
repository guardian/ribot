package ribot.model

case class InstanceType(instanceClass: String, instanceSize: String) {
  val name = s"$instanceClass.$instanceSize"
  def sizeNormalistionFactor = InstanceSizeNormalisationFactor(instanceSize)
}

object InstanceType {
  def fromString(s: String) = s.split('.') match {
    case Array(cls, size) => InstanceType(cls, size)
    case _ => sys.error(s"could not parse instance type $s")
  }
}
