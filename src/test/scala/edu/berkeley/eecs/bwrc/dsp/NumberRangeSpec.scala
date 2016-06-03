// See LICENSE for license details.

package edu.berkeley.eecs.bwrc.dsp

import org.scalatest.{Matchers, FlatSpec}

class NumberRangeSpec extends FlatSpec with Matchers {
  behavior of "Undefined ranges"

  they should "add with anything to produce an unknown range" in {
    UndefinedRange + UndefinedRange should be (UndefinedRange)
  }
}
