// See LICENSE for license details.

package chisel.dsp

import Chisel._
import Chisel.iotesters._
import org.scalatest.{Matchers, FlatSpec}

class SimpleBundleAdder extends Module {
  val io = new Bundle {
    val a = (new SimpleBundle).flip()
    val b = (new SimpleBundle).flip()
    val c = new SimpleBundle
  }
//  io.c.x := io.a.x + io.b.x
  io.c :=  io.a + io.b
}

class SimpleBundleTests(c: SimpleBundleAdder, backend: Option[Backend] = None)
  extends PeekPokeTester(c, _backend = backend) {

  poke(c.io.a.x, 1)
  poke(c.io.b.x, 2)

  expect(c.io.c.x, 3)
}

class SimpleBundleTestSpec extends FlatSpec with Matchers {
  "SimpleBundle" should "correctly add randomly generated numbers" in {
    runPeekPokeTester(() => new SimpleBundleAdder){
      (c,b) => new SimpleBundleTests(c,b)} should be (true)
  }

}
