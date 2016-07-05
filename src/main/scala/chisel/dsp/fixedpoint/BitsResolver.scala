// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel3._
import chisel3.util.{Fill, Cat}

case class BitsResolver(a: Number, b: Number) {
  val aParameters = a.parameters
  val bParameters = b.parameters

  def maxFractionalWidth: Int = aParameters.decimalPosition.max(bParameters.decimalPosition)

  def fractionalDifference: Int = aParameters.decimalPosition - bParameters.decimalPosition

  def addableSInts: (SInt, SInt) = {
    if(aParameters.decimalPosition < bParameters.decimalPosition) {
      (a.value, padRight(b, fractionalDifference.abs))
    }
    else if(aParameters.decimalPosition > bParameters.decimalPosition) {
      (padRight(a, fractionalDifference.abs), b.value)
    }
    else {
      (a.value, b.value)
    }
  }

  def padRight(number: Number, numberOfBits: Int): SInt = {
//    Cat(number.value, Fill(fractionalDifference, 0.U)).asSInt()
    val filled = Wire(Fill(fractionalDifference.abs, UInt(0, width = 1)))
    Cat(number.value, filled).asSInt()
  }
}
