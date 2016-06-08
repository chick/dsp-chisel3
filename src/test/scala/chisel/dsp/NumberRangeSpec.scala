// See LICENSE for license details.

package chisel.dsp

import org.scalatest.{Matchers, FlatSpec}

class NumberRangeSpec extends FlatSpec with Matchers {
  val MaxProductTestValue = 2
  behavior of "Undefined ranges"

  they should "add with anything to produce an unknown range" in {
    UndefinedRange + UndefinedRange should be (UndefinedRange)
    NumberRange(4) + UndefinedRange should be (UndefinedRange)
    NumberRange(4, 16) + UndefinedRange should be (UndefinedRange)
    UndefinedRange + NumberRange(4) should be (UndefinedRange)
    UndefinedRange + NumberRange(4, 16) should be (UndefinedRange)
  }

  behavior of "IntRange"

  it should "throw exception if min < max" in {
    intercept[DspException] {
      NumberRange(6, 5)
    }
  }

  it should "under addition sum of mins and sum of maxes" in {
    NumberRange(4, 100) + NumberRange(-22, 33) should be (NumberRange(-18, 133))
  }

  it should "compute proper max an min for all values in range" in {
    for {
      i <- -MaxProductTestValue to MaxProductTestValue
      j <- i                    to MaxProductTestValue
      m <- -MaxProductTestValue to MaxProductTestValue
      n <- m                    to MaxProductTestValue
    } {
      var foundLow = false
      var foundHigh = false
      val multRange = (IntRange(i, j) * IntRange(m, n)).asInstanceOf[IntRange]
      for {
          x <- i to j
          y <- m to n
      } {
        val product = x * y
        if(product == multRange.min) foundLow = true
        if(product == multRange.max) foundHigh = true
        // println(s"a $i,$j b $m,$n t $x,$y p $product nr $multRange")
        multRange.contains(product) should be (true)
      }
      foundLow should be (true)
      foundHigh should be (true)
    }
  }
}
