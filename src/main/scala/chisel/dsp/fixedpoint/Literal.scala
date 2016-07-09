// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel3._
import firrtl_interpreter._

object Literal {
  implicit val defaultBehavior = Behavior(Saturate, Truncate, Some(16), Some(-16), Some(32))

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

  def apply(x: Double, fractionalWidth: Int): Literal = {
    val bigInt = toBigInt(x, fractionalWidth)
    val integerWidth = requiredBitsForSInt(bigInt) - fractionalWidth

//    println(s"Creating FixedPointNUmber bigIng $bigInt iw $integerWidth fw $fractionalWidth")
    val r = Wire(new Literal(bigInt, Parameters(integerWidth + fractionalWidth, fractionalWidth, bigInt, bigInt)))
//    println(s"got fpn $r")
    r.value := SInt(bigInt, integerWidth + fractionalWidth)
//    println(s"got fpn $r")
    r
  }
  def apply(bigInt: BigInt, fractionalWidth: Int = 0): Literal = {
    val integerWidth = requiredBitsForSInt(bigInt) - fractionalWidth
    println(s"fpl apply bigInt $bigInt iw $integerWidth fw $fractionalWidth")
    val r = Wire(new Literal(bigInt, Parameters(integerWidth + fractionalWidth, fractionalWidth, bigInt, bigInt)))
    r.value := SInt(bigInt, integerWidth + fractionalWidth)
    r
  }

}

class Literal(val literalValue:  BigInt, parameters: Parameters)(implicit behavior: Behavior)
  extends Number(parameters)(behavior) {

  override val isLiteral: Boolean = true

  def unsignedBinaryString: String = {
    (parameters.numberOfBits - 1 to 0 by -1).map { i => if(literalValue.testBit(i)) "1" else "0"}.mkString
  }

  override def cloneType: this.type = {
    new Literal(literalValue, parameters).asInstanceOf[this.type]
  }
  override def toString: String = {
    f"${Literal.toDouble(literalValue, parameters.decimalPosition)}" +
      f"(b$unsignedBinaryString):Q${parameters.numberOfBits}:${parameters.decimalPosition}"
  }
  def toDouble: Double = {
    Literal.toDouble(literalValue, parameters.decimalPosition)
  }
}
