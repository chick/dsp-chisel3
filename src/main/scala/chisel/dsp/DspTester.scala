// See LICENSE for license details.

package chisel.dsp

import chisel3.iotesters.{PeekPokeTester, Backend}
import chisel3.core.Module

import chisel.dsp.fixedpoint._

import scala.collection.mutable.ArrayBuffer

class DspTester[T <: Module](c: T, _backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = _backend) {
  def poke(port: FixedPointNumber, number: Double): Unit = {
    poke(port.value, FixedPointLiteral(number, port.fractionalWidth).literalValue)
  }

  def poke(port: FixedPointNumber, fixedPointLiteral: FixedPointLiteral): Unit = {
    poke(port.value, fixedPointLiteral.literalValue)
  }

  def poke(port: Number, number: Double): Unit = {
    poke(port.value, FixedPointLiteral(number, port.parameters.binaryPoint).literalValue)
  }

  def poke(port: Number, fixedPointLiteral: Literal): Unit = {
    poke(port.value, fixedPointLiteral.literalValue)
  }

  def peek(port: Number): Literal = {
    val sInt = peek(port.value)
    new Literal(sInt, port.parameters)
  }

  def expect(port: Number, expected: FixedPointLiteral): Unit = {
    val sInt = peek(port.value)
    val result = Literal(sInt, port.parameters.binaryPoint)
    //    println(s"XXXXX ${result.literalValue} != ${expected.literalValue}")

    if (result.literalValue != expected.literalValue) {
      println(s"Error: expect(${port}, $expected) got $result instead")
      expect(port.value, expected.literalValue)
    }
  }

  def expect(port: Number, expected: Literal): Unit = {
    val sInt = peek(port.value)
    val result = Literal(sInt, port.parameters.binaryPoint)
    //    println(s"XXXXX ${result.literalValue} != ${expected.literalValue}")

    if (result.literalValue != expected.literalValue) {
      println(s"Error: expect(${port}, $expected) got $result instead")
      expect(port.value, expected.literalValue)
    }
  }
}

object DspTester {
  case class DoubleRange(start: Double, end: Double, step: Double) extends Iterator[Double] {
    var currentValue = start

    override def hasNext: Boolean = currentValue < end

    override def next(): Double = {
      val returnValue = currentValue
      currentValue += step
      returnValue
    }
  }
}