// See LICENSE for license details.

package chisel.dsp

import chisel3._
import chisel3.util.log2Up

object FixedPointLiteral {
  def requiredBitsForSInt(num: BigInt): Int = {
    if(num == 0) {
      1
    }
    else {
      def computeBits(n: BigInt): Int = {
        val a = n.toDouble + 1.0
        (scala.math.ceil(scala.math.log(a) / scala.math.log(2))).toInt
      }
      if (num < 0) {
        computeBits(num.abs)
      }
      else {
        computeBits(num) + 1
      }
    }
  }

  def toBigInt(x: Double, fractionalWidth: Int): BigInt = {
    val multiplier = math.pow(2,fractionalWidth)
    val result = BigInt(math.round(x * multiplier))
    println(s"toBigInt:x = $x, width = $fractionalWidth multiplier $multiplier result $result")
    result
  }

  def toDouble(i: BigInt, fractionalWidth: Int): Double = {
    val multiplier = math.pow(2,fractionalWidth)
    val result = i.toDouble / multiplier
//    println(s"toDouble:i = $i, fw = $fractionalWidth, mult = $multiplier, result $result")
    result
  }

  def apply(x: Double, fractionalWidth: Int): FixedPointLiteral = {
    val bigInt = toBigInt(x, fractionalWidth)
    val integerWidth = requiredBitsForSInt(bigInt) - fractionalWidth

//    println(s"Creating FixedPointNUmber bigIng $bigInt iw $integerWidth fw $fractionalWidth")
    val r = Wire(new FixedPointLiteral(bigInt, integerWidth, fractionalWidth, IntRange(bigInt, bigInt)))
//    println(s"got fpn $r")
    r.value := SInt(bigInt, integerWidth + fractionalWidth)
//    println(s"got fpn $r")
    r
  }
  def apply(bigInt: BigInt, fractionalWidth: Int = 0): FixedPointLiteral = {
    val integerWidth = requiredBitsForSInt(bigInt) - fractionalWidth
    println(s"fpl apply bigInt $bigInt iw $integerWidth fw $fractionalWidth")
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

  def unsignedBinaryString: String = {
    (integerWidth + fractionalWidth - 1 to 0 by -1).map { i => if(literalValue.testBit(i)) "1" else "0"}.mkString
  }

  override def cloneType: this.type = {
    new FixedPointLiteral(literalValue, integerWidth, fractionalWidth, range, underlying).asInstanceOf[this.type]
  }
  override def toString: String = {
    f"${FixedPointLiteral.toDouble(literalValue, fractionalWidth)}" +
      f"(b$unsignedBinaryString):Q$integerWidth:$fractionalWidth"
  }
  def toDouble: Double = {
    FixedPointLiteral.toDouble(literalValue, fractionalWidth)
  }
}
