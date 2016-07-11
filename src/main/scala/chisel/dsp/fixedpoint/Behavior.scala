// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel.dsp.{DspException, dsp}

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
                     binaryPointMaximum: Option[Int],
                     binaryPointMinimum: Option[Int],
                     numberOfBitsMaximum:    Option[Int]
                   ) {
  def testBits(numberOfBits: Int): Boolean = {
    numberOfBitsMaximum match {
      case Some(max) => numberOfBits <= max
      case _ => true
    }
  }

  def assertConforming(number: Number): Unit = {
    numberOfBitsMaximum.foreach { max =>
      if(number.parameters.numberOfBits > max) {
        throw DspException(s"Number: $this, numberOfBits exceeds scoped maximum of $max")
      }
    }
    binaryPointMaximum.foreach { max =>
      if(number.parameters.binaryPoint > max) {
        throw DspException(s"Number: $this, decimal position exceeds scoped maximum of $max")
      }
    }
    binaryPointMinimum.foreach { min =>
      if(number.parameters.binaryPoint < min) {
        throw DspException(s"Number: $this, decimal position exceeds scoped minimum of $min")
      }
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
