// See LICENSE for license details.

package chisel.dsp.FixedPoint

import chisel3._
import chisel3.util.{Fill, Cat}

case class BitsResolver(a: Number, b: Number) {
  val aParameters = a.parameters
  val bParameters = b.parameters

  def maxFractionalWidth: Int = aParameters.decimalPosition.max(bParameters.decimalPosition)

  def fractionalDifference: Int = aParameters.decimalPosition - bParameters.decimalPosition

  def addableSInts: (SInt, SInt) = {
    if(aParameters.decimalPosition < bParameters.decimalPosition) {
      (a.value, padRight(b, fractionalDifference))
    }
    else {
      (padRight(a, fractionalDifference.abs), b.value)
    }
  }

  def padRight(number: Number, numberOfBits: Int): SInt = {
    Cat(number.value, Fill(fractionalDifference, 0.U)).asSInt()
  }
}
