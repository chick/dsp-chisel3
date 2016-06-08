// See LICENSE for license details.

package chisel.dsp

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

class MultiplyTestSpec extends FlatSpec with Matchers {
  "Multiply" should "correctly add randomly generated numbers" in {
    runPeekPokeTester(() => new Multiply(4, 2, 16)){
      (c,b) => new MultiplyTests(c,b)} should be (true)
  }

}
