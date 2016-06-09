// See LICENSE for license details.

package chisel.dsp

import Chisel._
import Chisel.iotesters.{runPeekPokeTester, PeekPokeTester, Backend}
import org.scalatest.{Matchers, FlatSpec}

class Multiply(integerWidth: Int, fractionalWidth: Int, adderSize: Int) extends Module {
  val io = new Bundle {
    val a = FixedPointNumber(1, 1, NumberRange(adderSize), INPUT)
    val b = FixedPointNumber(1, 2, NumberRange(adderSize), INPUT)
    val c = FixedPointNumber(integerWidth, fractionalWidth, NumberRange(adderSize), OUTPUT)
  }
  io.c := io.a * io.b
}

class MultiplyTests(c: Multiply, backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = backend) {

  poke(c.io.a.value, 1)
  poke(c.io.b.value, 1)

  expect(c.io.c.value, 2)
}

class TimesX(val integerWidth: Int, val fractionalWidth: Int, val coefficient: Double) extends Module {
  val io = new Bundle {
    val in  = FixedPointNumber(integerWidth, fractionalWidth, INPUT)
    val out = FixedPointNumber(integerWidth, fractionalWidth, OUTPUT)
  }

  val c = Wire(FixedPointLiteral(coefficient, 2))
  c.value := FixedPointLiteral.toBigInt(coefficient, c.fractionalWidth).S
  io.out := io.in * c
}

class TimeXTest(c: TimesX, backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = backend) {
  for(i <- 1 to 8) {
    val x = i.toDouble * 0.5
    val y = x * c.coefficient

    val input = FixedPointLiteral.toBigInt(x, c.fractionalWidth)
    poke(c.io.in.value, input)
    val output = peek(c.io.out.value)

    val result = FixedPointLiteral.toDouble(output, c.fractionalWidth)

    println(s"i $i x $x y $y input $input output $output result $result")
    assert(result == y)


  }
}

class MultiplyTestSpec extends FlatSpec with Matchers {
  "Multiply" should "correctly multiply randomly generated numbers" in {
    runPeekPokeTester(() => new Multiply(4, 2, 16)){
      (c,b) => new MultiplyTests(c,b)} should be (true)
  }

  "TimesX" should "multiply number by supplied coefficient" in {
    val intWidth = 4
    val fracWidth = 4

    runPeekPokeTester(() => new TimesX(4, 4, 0.5)){
      (c,b) => new TimeXTest(c,b)
    } should be (true)

  }
}
