// See LICENSE for license details.

package chisel.dsp

import chisel3._
import chisel3.iotesters._

import fixedpoint._
import org.scalatest.{Matchers, FlatSpec}

//scalastyle:off magic.number

class HardCodedAdder(aValue: Double, aMant: Int, bValue: Double, bMant: Int) extends Module {
  val io = new Bundle {
    val out = Number(8, 4, OUTPUT)
  }

  val a = Literal(aValue, aMant)
  val b = Literal(bValue, bMant)

  io.out := a + b
}

class HardCodedAdderTester(c: HardCodedAdder, backend: Option[Backend], expected: Literal)
  extends DspTester(c, _backend = backend) {

  expect(c.io.out, expected)
}

class HardCodedAdderSpec extends FlatSpec with Matchers {
  behavior of "Add"

  it should "work when mantissas are the same size" in {
    for(mantissaSize <- 0 to 10) {
      for {
        value1 <- DspTester.DoubleRange(0.0, 10, 0.25)
        value2 <- DspTester.DoubleRange(0.0, 10, 0.25)
      } {
        val expected = Literal(value1 + value2, mantissaSize)

        println(s"HardCodedAdder $value1 + $value2 should be $expected")
        val result = runPeekPokeTester(
          () => new HardCodedAdder(value1, mantissaSize, value2, mantissaSize),
          "firrtl"
        ) {
            (c,b) => new HardCodedAdderTester(c,b, expected)
          }
        result should be (true)

      }
    }
  }
}
