// See LICENSE for license details.

package chisel.dsp.fixedpoint

abstract class OverflowType
case object Saturate extends OverflowType
case object Wrap extends OverflowType
case object Grow extends OverflowType

abstract class TrimType
case object Truncate extends TrimType
case object Round extends TrimType
case object NoTrim extends TrimType

case class Behavior(
                     overflow: OverflowType,
                     trimType: TrimType,
                     decimalPositionMaximum: Option[Int],
                     decimalPositionMinimum: Option[Int],
                     numberOfBitsMaximum:    Option[Int]
                   ) {
  def testBits(numberOfBits: Int): Boolean = {
    numberOfBitsMaximum match {
      case Some(max) => numberOfBits <= max
      case _ => true
    }
  }
}

//scalastyle:off magic.number

object Behavior {
  val DefaultMaximumDecimalPosition = Some(16)
  val DefaultMinimumDecimalPosition = Some(-16)
  val DefaultMaximumNumberOfBits    = Some(32)

  implicit def defaultBehavior: Behavior = {
    Behavior(
      Saturate,
      Truncate,
      DefaultMaximumDecimalPosition,
      DefaultMinimumDecimalPosition,
      DefaultMaximumNumberOfBits
    )
  }

  def using(behavior: Behavior)(block: Behavior => Unit): Unit = {
    implicit val localBehavior = behavior

    block(localBehavior)
  }
}
