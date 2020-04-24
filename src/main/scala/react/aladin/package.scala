package react

import react.common.EnumValue

package aladin {
  sealed trait CooFrame extends Product with Serializable
  object CooFrame {
    implicit val enum: EnumValue[CooFrame] = EnumValue.toLowerCaseString
    case object J2000 extends CooFrame
    case object J2000d extends CooFrame
    case object Galactic extends CooFrame

    def fromString(s: String): Option[CooFrame] = s match {
      case "j2000"    => Some(J2000)
      case "j2000d"   => Some(J2000d)
      case "galactic" => Some(Galactic)
    }
  }
}
