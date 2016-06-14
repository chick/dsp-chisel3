// See LICENSE for license details.

package chisel.dsp

import chisel.iotesters.{Backend, PeekPokeTester}
import chisel.core.Module

class DspTester[T <: Module](c: T, _backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = _backend){
  def poke(port: FixedPointNumber, number: Double): Unit = {
    poke(port.value, FixedPointLiteral(number, port.fractionalWidth).literalValue)
  }

  def poke(port: FixedPointNumber, fixedPointLiteral: FixedPointLiteral): Unit = {
    poke(port.value, fixedPointLiteral.literalValue)
  }

  def peek(port: FixedPointNumber): FixedPointLiteral = {
    val sInt = peek(port.value)
    FixedPointLiteral(sInt, port.integerWidth, port.fractionalWidth)
  }
}
