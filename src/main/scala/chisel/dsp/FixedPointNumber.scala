// See LICENSE for license details.

package chisel.dsp

import Chisel._

object FixedPointNumber {
  def apply(fractionalWidth: Int, range: NumberRange, direction: Direction): FixedPointNumber = {
    direction match {
      case OUTPUT => new FixedPointNumber(fractionalWidth, range, Some(UInt(OUTPUT, range.width)))
      case INPUT  => new FixedPointNumber(fractionalWidth, range, Some(UInt(OUTPUT, range.width))).flip()
      case NO_DIR => new FixedPointNumber(fractionalWidth, range, Some(UInt(NO_DIR, range.width)))
      case _      => new FixedPointNumber(fractionalWidth, range, Some(UInt(OUTPUT, range.width)))
    }
  }
}
class FixedPointNumber(
                        fractionalWidth: Int,
                        range: NumberRange = UndefinedRange,
                        underlying: Option[UInt] = None
                      ) extends Bundle with Qnm {

  val value = underlying.getOrElse(UInt(OUTPUT, width = range.width))

  def + (that: FixedPointNumber): FixedPointNumber = {
    val newRange = range + that.getRange()

    val result = Wire(new FixedPointNumber(fractionalWidth, newRange))

    result.value := this.value + that.value

    result
  }

  def getRange(dummy: Int = 0): NumberRange = range

  override def cloneType: this.type = {
    (new FixedPointNumber(fractionalWidth, range, underlying)).asInstanceOf[this.type ]
  }
}

object FixedPointLiteral {
  def apply(
             literalValue : Int,
             fractionalWidth: Int = 0,
             range: NumberRange = UndefinedRange,
             underlying: Option[UInt] = None
           ): FixedPointLiteral = {
    Wire(new FixedPointLiteral(literalValue, range = NumberRange(literalValue.abs)))
  }
}
class FixedPointLiteral(
                        literalValue : Int,
                        fractionalWidth: Int = 0,
                        range: NumberRange = UndefinedRange,
                        underlying: Option[UInt] = None
) extends FixedPointNumber(fractionalWidth, range, underlying) {
  override val value = Wire(underlying.getOrElse(UInt(width = range.width)))

  value := literalValue.U

  override def cloneType: this.type = {
    (new FixedPointLiteral(literalValue, fractionalWidth, range, underlying)).asInstanceOf[this.type ]
  }
}
