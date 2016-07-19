// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel3.core.SInt
import chisel.dsp.DspException
import firrtl_interpreter._

object Parameters {
  def apply(numberOfBits: Int, binaryPoint: Int): Parameters = {
    val (bigLow, bigHigh) = extremaOfSIntOfWidth(numberOfBits)
    Parameters(numberOfBits, binaryPoint, bigHigh, bigLow)
  }

  /**
    * Created a parameter object, possibly adjusting the number of bits required downward
    * if hi and low require less than requested
    *
    * @param numberOfBits     bits suggested
    * @param binaryPoint  position of decimal point
    * @param high             biggest number this could be
    * @param low              smallest number this could be
    * @return
    */
  def apply(numberOfBits: Int, binaryPoint: Int, high: BigInt, low: BigInt): Parameters = {
    val adjustedBits = numberOfBits.min(requiredBitsForSInt(low).max(requiredBitsForSInt(high)))

    val result = new Parameters(adjustedBits: Int, binaryPoint: Int, high: BigInt, low: BigInt)

    result
  }
}

class Parameters private (val numberOfBits: Int, val binaryPoint: Int, val high: BigInt, val low: BigInt) {
  if(low > high) {
    throw new DspException(s"Error high must be greater than low $toString")
  }
  val (lowest, highest) = extremaOfSIntOfWidth(numberOfBits)
  if(low < lowest) {
    throw new DspException(s"Error low to small for numberOfBits $toString lowest possible is $lowest")
  }
  if(high > highest) {
    throw new DspException(s"Error high to large for numberOfBits $toString highest possible is $highest")
  }

  // if the decimal position is less than zero this further restricts the possible low and high values
//  if(binaryPoint < 0) {
//    val mask = BigInt("1" * binaryPoint, 2)
//  }

  def generateSInt: SInt = {
    SInt(width = numberOfBits)
  }
  def + (that: Parameters): Parameters = {
    val (newHigh, newLow) = extremesOfOperation(that, plus)
    val bitsRequired = requiredBitsForSInt(newHigh).max(requiredBitsForSInt(newLow))

    Parameters(
      bitsRequired,
      this.binaryPoint.max(that.binaryPoint),
      newHigh,
      newLow
    )
  }
  def * (that: Parameters): Parameters = {
    val (newHigh, newLow) = extremesOfOperation(that, multiply)
    val bitsRequired = requiredBitsForSInt(newHigh).max(requiredBitsForSInt(newLow))

    Parameters(
      bitsRequired,
      this.binaryPoint + that.binaryPoint,
      newHigh,
      newLow
    )
  }

  def extremesOfOperation(that: Parameters, op: (BigInt, BigInt) => BigInt): (BigInt, BigInt) = {
    (
      op(this.high, that.high).max(op(this.high, that.low)).max(op(this.low, that.high)).max(op(this.low, that.low)),
      op(this.high, that.high).min(op(this.high, that.low)).min(op(this.low, that.high)).min(op(this.low, that.low))
    )
  }

  val plus = (a: BigInt, b: BigInt) => a + b
  val multiply = (a: BigInt, b: BigInt) => a * b

  override def toString: String = {
    s"FP(bits=$numberOfBits,decimal=$binaryPoint,hi=$high,lo=$low)"
  }
  def asQmn: String = {
    s"Q$numberOfBits.$binaryPoint"
  }
  def asFP: String = {
    val (bigLow, bigHigh) = extremaOfSIntOfWidth(numberOfBits)
    if(bigLow == low && bigHigh == high) {
      s"FP$numberOfBits.$binaryPoint"
    }
    else {
      val doubleHigh = Literal.toDouble(high, binaryPoint)
      val doubleLow  = Literal.toDouble(low, binaryPoint)
      if(high == low) {
        s"FP$numberOfBits.$binaryPoint[$doubleHigh]"
      }
      else {
        s"FP$numberOfBits.$binaryPoint[$doubleHigh,$doubleLow]"
      }
    }
  }
}
