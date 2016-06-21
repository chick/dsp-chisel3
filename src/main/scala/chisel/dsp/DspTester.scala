// See LICENSE for license details.

package chisel.dsp

import chisel.iotesters.{PeekPokeTester, Backend}
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
    FixedPointLiteral(sInt, port.fractionalWidth)
  }

  def expect(port: FixedPointNumber, expected: FixedPointLiteral): Unit = {
    val sInt = peek(port.value)
    val result = FixedPointLiteral(sInt, port.fractionalWidth)
//    println(s"XXXXX ${result.literalValue} != ${expected.literalValue}")

    if(result.literalValue != expected.literalValue) {
      println(s"Error: expect(${port}, $expected) got $result instead")
      expect(port.value, expected.literalValue)
    }
  }

//  def step(n: Int): Unit = {
//    super.step(n)
//  }
}
