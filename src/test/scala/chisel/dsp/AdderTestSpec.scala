// See LICENSE for license details.

package chisel.dsp

import Chisel._
import Chisel.iotesters.{runPeekPokeTester, PeekPokeTester, Backend}
import org.scalatest.{Matchers, FlatSpec}

class Adder(fractionalWidth: Int, adderSize: Int) extends Module {
  val io = new Bundle {
    val a = FixedPointNumber(fractionalWidth, NumberRange(adderSize), INPUT)
    val b = FixedPointNumber(fractionalWidth, NumberRange(adderSize), INPUT)
    val c = FixedPointNumber(fractionalWidth, NumberRange(adderSize), OUTPUT)
  }
  io.c := io.a + io.b
}

class AdderTests(c: Adder, backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = backend) {

  poke(c.io.a.value, 1)
  poke(c.io.b.value, 2)

  expect(c.io.c.value, 3)
}

class AdderTestSpec extends FlatSpec with Matchers {
  "Adder" should "correctly add randomly generated numbers" in {
    runPeekPokeTester(() => new Adder(8, 16)){
      (c,b) => new AdderTests(c,b)} should be (true)
  }

}
