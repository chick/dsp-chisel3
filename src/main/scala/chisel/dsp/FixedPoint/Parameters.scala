// See LICENSE for license details.

package chisel.dsp.FixedPoint

import chisel.core.SInt
import firrtl_interpreter._

object Parameters {
  def apply(numberOfBits: Int, decimalPosition: Int): Parameters = {
    val (bigLow, bigHigh) = extremaOfSIntOfWidth(numberOfBits)
    new Parameters(numberOfBits, decimalPosition, bigHigh.toInt, bigLow.toInt)
  }
}
case class Parameters(numberOfBits: Int, decimalPosition: Int, high: Int, low: Int) {
  def generateSInt: SInt = {
    SInt(width = numberOfBits)
  }
  def + (that: Parameters): Parameters = {
    Parameters(
      this.numberOfBits.max(that.numberOfBits) + 1,
      this.decimalPosition.max(that.decimalPosition),
      this.high + that.high,
      this.low + that.low
    )
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
}