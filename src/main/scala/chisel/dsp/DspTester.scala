// See LICENSE for license details.

package chisel.dsp

import chisel3.iotesters.{PeekPokeTester, Backend}
import chisel3.core.Module

import chisel.dsp.fixedpoint._

class DspTester[T <: Module](c: T, _backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = _backend){
  def poke(port: FixedPointNumber, number: Double): Unit = {
    poke(port.value, FixedPointLiteral(number, port.fractionalWidth).literalValue)
  }

  def poke(port: FixedPointNumber, fixedPointLiteral: FixedPointLiteral): Unit = {
    poke(port.value, fixedPointLiteral.literalValue)
  }

  def poke(port:Number, number: Double): Unit = {
    poke(port.value, FixedPointLiteral(number, port.parameters.decimalPosition).literalValue)
  }

  def poke(port: Number, fixedPointLiteral: Literal): Unit = {
    poke(port.value, fixedPointLiteral.literalValue)
  }

  def peek(port: Number): Literal = {
    val sInt = peek(port.value)
    Literal(sInt, port.parameters.decimalPosition)
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

  def expect(port: Number, expected: Literal): Unit = {
    val sInt = peek(port.value)
    val result = FixedPointLiteral(sInt, port.parameters.decimalPosition)
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
