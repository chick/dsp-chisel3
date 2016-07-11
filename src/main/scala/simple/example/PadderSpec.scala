// See LICENSE for license details.

package simple.example

import chisel3._
import chisel3.iotesters.{PeekPokeTester, runPeekPokeTester, Backend}
import chisel3.util.{Cat, Fill}
import org.scalatest.{Matchers, FlatSpec}

class Paddable(val numBits: Int, val radixPoint: Int) extends Bundle {
  val underlying = SInt(width = numBits)
  override def cloneType: this.type = {
    new Paddable(numBits, radixPoint).asInstanceOf[this.type]
  }

  def padRight(padSize: Int): SInt = {
    val filled = Wire(init = Fill(padSize.abs, 1.U))
    Cat(underlying, filled).asSInt()
  }
}

class Padder(nb1: Int, rp1: Int, nb3: Int, rp3: Int) extends Module {
  val io = new Bundle {
    val a = new Paddable(nb1, rp1)
    val c = new Paddable(nb3, rp3)
  }

  io.c.underlying := io.a.padRight(4)
}

class PadderTester(c: Padder, backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = backend) {
  poke(c.io.a.underlying, BigInt(5))

  expect(c.io.c.underlying, BigInt("5F", 16))
}

class PadderSpec extends FlatSpec with Matchers {
  "Padder" should "add correctly" in {
    runPeekPokeTester(() => new Padder(4, 1, 9, 5), "firrtl"){
      (c,b) => new PadderTester(c,b)} should be (true)
  }
}
