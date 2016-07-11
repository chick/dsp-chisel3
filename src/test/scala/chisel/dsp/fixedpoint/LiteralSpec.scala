// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel.dsp.DspTester
import org.scalatest.{FlatSpec, Matchers}
import chisel3._

class LiteralSpec extends FlatSpec with Matchers {
  behavior of "Literal construction"

  it should "get handle bits and mantissa properly for zero" in {
    val a = Literal(0.0, 0)

    a.literalValue should be (0.0)
  }

  it should "get handle bits and mantissa properly for 16" in {
    val a = Literal(15.0, 0)

    a.literalValue should be (15.0)
    a.parameters.numberOfBits should be (5)
    a.parameters.binaryPoint should be (0)

    a.value.getWidth should be (5)
  }

  it should "work when mantissas are the same size" in {
    for(mantissaSize <- 2 to 10) {
      for {
        value1 <- DspTester.DoubleRange(0.0, 10, 0.25)
        value2 <- DspTester.DoubleRange(0.0, 10, 0.25)
      } {
        val expected = Literal(value1 + value2, mantissaSize)

//        println(s"$value1 + $value2 = $expected")
        expected.toDouble should be (value1 + value2)
      }
    }
  }
}
