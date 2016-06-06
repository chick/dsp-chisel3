// See LICENSE for license details.

package chisel.dsp

import Chisel.iotesters.{runPeekPokeTester, PeekPokeTester, Backend}
import Chisel._
import org.scalatest.{Matchers, FlatSpec}

class Incrementer(fractionalWidth: Int, adderSize: Int) extends Module {
  val io = new Bundle {
    val a = FixedPointNumber(fractionalWidth, NumberRange(adderSize), INPUT)
    val c = FixedPointNumber(fractionalWidth, NumberRange(adderSize), OUTPUT)
  }
  val increment = FixedPointLiteral(1)
  io.c := io.a + increment
}

class IncrementerTests(c: Incrementer, backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = backend) {

  poke(c.io.a.value, 1)

  expect(c.io.c.value, 2)
}

class IncrementerTestSpec extends FlatSpec with Matchers {
  "Incrementer" should "correctly add randomly generated numbers" in {
    runPeekPokeTester(() => new Incrementer(8, 16)){
      (c,b) => new IncrementerTests(c,b)} should be (true)
  }

}
