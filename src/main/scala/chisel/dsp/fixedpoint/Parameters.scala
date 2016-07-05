// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel3.core.SInt
import chisel.dsp.DspException
import firrtl_interpreter._

object Parameters {
  def apply(numberOfBits: Int, decimalPosition: Int): Parameters = {
    val (bigLow, bigHigh) = extremaOfSIntOfWidth(numberOfBits)
    Parameters(numberOfBits, decimalPosition, bigHigh.toInt, bigLow.toInt)
  }

  /**
    * Created a parameter object, possibly adjusting the number of bits required downward
    * if hi and low require less than requested
    *
    * @param numberOfBits     bits suggested
    * @param decimalPosition  position of decimal point
    * @param high             biggest number this could be
    * @param low              smallest number this could be
    * @return
    */
  def apply(numberOfBits: Int, decimalPosition: Int, high: BigInt, low: BigInt): Parameters = {
    val adjustedBits = numberOfBits.min(requiredBitsForSInt(low).max(requiredBitsForSInt(high)))

    val result = new Parameters(adjustedBits: Int, decimalPosition: Int, high: BigInt, low: BigInt)

    result
  }
}

class Parameters private (val numberOfBits: Int, val decimalPosition: Int, val high: BigInt, val low: BigInt) {
  if(low > high) {
    throw new DspException(s"Error high must be greater than low $toString")
  }
  val (lowest, highest) = extremaOfSIntOfWidth(numberOfBits)
  if(low < lowest) {
    throw new DspException(s"Error low to small for numberOfBits $toString lowest possible is $lowest")
  }
  if(high > highest) {
    throw new DspException(s"Error high to small for numberOfBits $toString highest possible is $highest")
  }
  def generateSInt: SInt = {
    SInt(width = numberOfBits)
  }
  def + (that: Parameters): Parameters = {
    val (newHigh, newLow) = extremesOfOperation(that, plus)
    val bitsRequired = requiredBitsForSInt(newHigh).max(requiredBitsForSInt(newLow))

    Parameters(
      bitsRequired,
      this.decimalPosition.max(that.decimalPosition),
      newHigh,
      newLow
    )

//    Parameters(
//      this.numberOfBits.max(that.numberOfBits) + 1,
//      this.decimalPosition.max(that.decimalPosition),
//      this.high + that.high,
//      this.low + that.low
//    )
  }
  def * (that: Parameters): Parameters = {
    Parameters(
      this.numberOfBits + that.numberOfBits,
      this.decimalPosition + that.decimalPosition,
      Array(
        this.high * that.high,
        this.low * that.low
      ).max,
      Array(
        this.high * that.high,
        this.low * that.low,
        this.low * that.high,
        this.high * that.low
      ).min
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
    s"FPP(bits=$numberOfBits,decimal=$decimalPosition,hi=$high,lo=$low)"
  }
  def asQmn: String = {
    s"Q$numberOfBits.$decimalPosition"
  }
  def asQmnWithRange: String = {
    val (bigLow, bigHigh) = extremaOfSIntOfWidth(numberOfBits)
    s"Q$numberOfBits.$decimalPosition"
  }
}
