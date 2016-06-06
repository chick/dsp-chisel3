// See LICENSE for license details.

package chisel.dsp

import org.scalatest.{Matchers, FlatSpec}

class NumberRangeSpec extends FlatSpec with Matchers {
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

  they should "under addition return min of the mins and max of the maxes" in {
    NumberRange(4, 100) + NumberRange(-22, 33) should be (NumberRange(-22, 100))
  }
}
