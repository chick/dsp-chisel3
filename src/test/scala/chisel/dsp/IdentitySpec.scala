// See LICENSE for license details.

package chisel.dsp

import chisel3.core.OUTPUT
import chisel3.{Module, Bundle}
import chisel3.iotesters.{runPeekPokeTester, PeekPokeTester, Backend}
import chisel.dsp.fixedpoint._
import org.scalatest.{FlatSpec, Matchers}

class Identity(iw: Int, fw: Int) extends Module {
  val io = new Bundle {
    val a = Number(iw, fw, OUTPUT).flip()
    val c = Number(iw, fw, OUTPUT)
  }
  io.c := io.a
}

class IdentityTests(c: Identity, backend: Option[Backend] = None) extends DspTester(c, _backend = backend) {
  var input = Literal(1.0, c.io.a.parameters.binaryPoint)
  poke(c.io.a, input)
  var output = peek(c.io.c)
  println(s"identity poke $input")
  println(s"identity peek $output")


  input = Literal(-1.0, c.io.a.parameters.binaryPoint)
  poke(c.io.a, input)
  output = peek(c.io.c)
  println(s"identity poke $input")
  println(s"identity peek $output")
}


class IdentitySpec  extends FlatSpec with Matchers {
  "Identity" should "return what its given" in {
    runPeekPokeTester(() => new Identity(2, 0)){
      (c,b) => new IdentityTests(c,b)} should be (true)
  }


}
