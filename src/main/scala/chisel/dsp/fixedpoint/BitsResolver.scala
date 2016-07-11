// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel3._
import chisel3.util.{Fill, Cat}

case class BitsResolver(a: Number, b: Number) {
  val aParameters = a.parameters
  val bParameters = b.parameters

  def maxFractionalWidth: Int = aParameters.binaryPoint.max(bParameters.binaryPoint)

  def fractionalDifference: Int = aParameters.binaryPoint - bParameters.binaryPoint

  def addableSInts: (SInt, SInt) = {
    if(aParameters.binaryPoint < bParameters.binaryPoint) {
      (a.value, padRight(b, fractionalDifference.abs))
    }
    else if(aParameters.binaryPoint > bParameters.binaryPoint) {
      (padRight(a, fractionalDifference.abs), b.value)
    }
    else {
      (a.value, b.value)
    }
  }

  def padRight(number: Number, numberOfBits: Int): SInt = {
    val filled = Wire(init = Fill(fractionalDifference.abs, UInt(0, width = 1)))
    Cat(number.value, filled).asSInt()
  }
}
