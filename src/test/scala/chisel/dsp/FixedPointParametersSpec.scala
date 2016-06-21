// See LICENSE for license details.

package chisel.dsp

import chisel.dsp.FixedPoint.Parameters
import org.scalatest.{Matchers, FlatSpec}

import firrtl_interpreter._

// scalastyle:off magic.number
class FixedPointParametersSpec extends FlatSpec with Matchers {
  behavior of "addition"

  it should "create new parameters" in {
    val firstParameter = Parameters(4, 1, -8, 7)
    val secondParameter = Parameters(5, 2, -1, 15)

    val newParameter = firstParameter + secondParameter

    println(s"$firstParameter $secondParameter $newParameter")
    newParameter.numberOfBits should be (6)
  }

  behavior of "highs and lows under addition"

  they should "form a regular pattern" in {
    for {
      bitSize1 <- 1 to 4
      bitSize2 <- bitSize1 to 4
    } {
      var maxMax = Int.MinValue
      var maxExample = ""
      var minMin = Int.MaxValue
      var minExample = ""

      val (bigLow1, bigHigh1) = extremaOfSIntOfWidth(bitSize1)
      val (low1: Int, high1: Int) = (bigLow1.toInt, bigHigh1.toInt)

      val (bigLow2, bigHigh2) = extremaOfSIntOfWidth(bitSize2)
      val (low2: Int, high2: Int) = (bigLow2.toInt, bigHigh2.toInt)

      val fullRange1 = Parameters(bitSize1, 0)
      val fullRange2 = Parameters(bitSize2, 0)
      val fullResult = fullRange1 + fullRange2

      for {
        i <- low1 to high1
        j <- i to high1
      } {
        for {
          m <- low2 to high2
          n <- m to high2
        } {
          val p1 = Parameters(bitSize1, 0, i, j)
          val p2 = Parameters(bitSize1, 0, m, n)

          val p3 = p1 + p2
          // println(s"$p1 + $p2 = $p3")
          if (p3.high > maxMax) {
            maxMax = p3.high
            maxExample = s"Max from $p1  + $p2 = $p3"
          }
          if (p3.low < minMin) {
            minMin = p3.low
            minExample = s"Min from $p1 + $p2 = $p3"
          }
        }
      }
      println(s"Bits $bitSize1 + $bitSize2 = ($minMin,$maxMax)")
      println(s"$maxExample")
      println(s"$minExample")

      fullResult.low should be(minMin)
      fullResult.high should be(maxMax)
    }
  }

  behavior of "highs and lows under multiplication"

  they should "form a regular pattern" in {
    for {
      bitSize1 <- 1 to 4
      bitSize2 <- bitSize1 to 4
    } {
      var maxMax = Int.MinValue
      var maxExample = ""
      var minMin = Int.MaxValue
      var minExample = ""

      val (bigLow1, bigHigh1) = extremaOfSIntOfWidth(bitSize1)
      val (low1: Int, high1: Int) = (bigLow1.toInt, bigHigh1.toInt)

      val (bigLow2, bigHigh2) = extremaOfSIntOfWidth(bitSize2)
      val (low2: Int, high2: Int) = (bigLow2.toInt, bigHigh2.toInt)

      val fullRange1 = Parameters(bitSize1, 0)
      val fullRange2 = Parameters(bitSize2, 0)
      val fullResult = fullRange1 * fullRange2

      for {
        i <- low1 to high1
        j <- i to high1
      } {
        for {
          m <- low2 to high2
          n <- m to high2
        } {
          val p1 = Parameters(bitSize1, 0, i, j)
          val p2 = Parameters(bitSize1, 0, m, n)

          val p3 = p1 * p2
          // println(s"$p1 * $p2 = $p3")
          if (p3.high > maxMax) {
            maxMax = p3.high
            maxExample = s"Max from $p1  * $p2 = $p3"
          }
          if (p3.low < minMin) {
            minMin = p3.low
            minExample = s"Min from $p1 * $p2 = $p3"
          }
        }
      }
      println(s"Bits $bitSize1 * $bitSize2 = ($minMin,$maxMax)")
      println(s"$maxExample")
      println(s"$minExample")

      fullResult.low should be(minMin)
      fullResult.high should be(maxMax)
    }
  }
}
