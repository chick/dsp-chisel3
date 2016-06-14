// See LICENSE for license details.

package chisel.dsp

import chisel._
import chisel.util.log2Up

object FixedPointLiteral {
  def toBigInt(x: Double, fractionalWidth: Int): BigInt = {
    val multiplier = math.pow(2,fractionalWidth)
    val result = BigInt(math.round(x * multiplier))
    result
  }

  def toDouble(i: BigInt, fractionalWidth: Int): Double = {
    val multiplier = math.pow(2,fractionalWidth)
    val result = i.toDouble / multiplier
    result
  }

  def apply(x: Double, fractionalWidth: Int): FixedPointLiteral = {
    val bigInt = toBigInt(x, fractionalWidth)
    val integerWidth = log2Up(x.toInt + 1) + 1

    val r = Wire(new FixedPointLiteral(bigInt, integerWidth, fractionalWidth, IntRange(bigInt, bigInt)))
    r.value := SInt(bigInt, integerWidth + fractionalWidth)
    r
  }
  def apply(bigInt: BigInt, fractionalWidth: Int = 0): FixedPointLiteral = {
    val integerWidth = log2Up(bigInt + 1) + 1
    val r = Wire(new FixedPointLiteral(bigInt, integerWidth, fractionalWidth, IntRange(bigInt, bigInt)))
    r.value := SInt(bigInt, integerWidth + fractionalWidth)
    r
  }

}
class FixedPointLiteral(
                         val literalValue:  BigInt,
                         integerWidth:      Int,
                         fractionalWidth:   Int = 0,
                         range:             NumberRange = UndefinedRange,
                         underlying:        Option[SInt] = None
                       ) extends FixedPointNumber(integerWidth, fractionalWidth, range, underlying) {
  override val isLiteral: Boolean = true

  override def cloneType: this.type = {
    new FixedPointLiteral(literalValue, integerWidth, fractionalWidth, range, underlying).asInstanceOf[this.type]
  }
  override def toString: String = {
    f"${FixedPointLiteral.toDouble(literalValue, fractionalWidth)}(0x$literalValue%x):Q$integerWidth:$fractionalWidth"
  }
}
