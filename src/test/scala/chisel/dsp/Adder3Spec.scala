// See LICENSE for license details.

package chisel.dsp

import chisel3.iotesters.{runPeekPokeTester, Backend, PeekPokeTester, ChiselFlatSpec}
import chisel3._
import chisel.dsp.fixedpoint._

class Adder3(iw: Int, fw: Int) extends Module {
  val io = new Bundle {
    val a = (Number(fw, iw, INPUT)).flip()
    val c = Number(iw, fw, OUTPUT)
  }
  val constant = 3.0.FP(fw)

//  printf("Adder3: io.c.num %x constant %x io.a.num %x\n", io.c.value, constant.value, io.a.value)

  io.c := io.a + constant
}

class Adder3Tester(c: Adder3, backend: Option[Backend] = None) extends DspTester(c, _backend = backend) {
  for(i <- 0 to 4) {
    val double  = 0.25 * i.toDouble
    val pokeValue = double.FP(c.io.a.parameters.decimalPosition)
    poke(c.io.a, double)
    val result = peek(c.io.c)
    val expected = double + 3.0
//    val expectedLiteral = FixedPointLiteral(expected, c.io.c.fractionalWidth)
    val expectedLiteral = expected.FP(c.io.c.parameters.decimalPosition)
    println(s"Adder3Tester: a <= $pokeValue c => $result expected $expectedLiteral")
    expect(c.io.c, expectedLiteral)
  }
}
class Adder3Spec extends ChiselFlatSpec {
  val intWidth  = 8
  val fracWidth = 4

  "Adder3" should "should add 3.0 to a number" in {
    runPeekPokeTester(() => new Adder3(intWidth, fracWidth)){
      (c,b) => new Adder3Tester(c, b)} should be (true)
  }
}
