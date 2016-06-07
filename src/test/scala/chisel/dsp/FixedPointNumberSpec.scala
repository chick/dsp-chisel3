// See LICENSE for license details.

package chisel.dsp

import Chisel.internal.firrtl.KnownWidth
import org.scalatest.{Matchers, FlatSpec}

class FixedPointNumberSpec extends FlatSpec with Matchers {
  behavior of "FixedPointNumber#matchFractionalWidth"

  it should "return originals and their respective ranges when fractional difference is 0" in {
    val f1 = FixedPointNumber(10)
    val f2 = FixedPointNumber(10)

    val (a, b, aRange, bRange, fractionalWidth) = f1.matchFractionalWidths(f2)

    fractionalWidth should be (10)
    aRange should be (NumberRange.fromWidth(10))
    bRange should be (NumberRange.fromWidth(10))

    a.width.isInstanceOf[KnownWidth] should be (true)
    a.width.asInstanceOf[KnownWidth].get should be (10)
  }

  it should "pad right pad b to match a when a has bigger fractional width" in {
    val f1 = FixedPointNumber(7)
    val f2 = FixedPointNumber(5)

    val (a, b, aRange, bRange, fractionalWidth) = f1.matchFractionalWidths(f2)

    fractionalWidth should be (7)
    aRange should be (f1.range)
    bRange should be (f2.range.shift(2))

    a.width.isInstanceOf[KnownWidth] should be (true)
    a.width.asInstanceOf[KnownWidth].get should be (7)
    b.width.isInstanceOf[KnownWidth] should be (true)
    b.width.asInstanceOf[KnownWidth].get should be (7)
  }

  it should "pad right pad b to match a when a has bigger fractional width" in {
    val f1 = FixedPointNumber(7)
    val f2 = FixedPointNumber(5)

    val (a, b, aRange, bRange, fractionalWidth) = f1.matchFractionalWidths(f2)

    fractionalWidth should be (7)
    aRange should be (f1.range)
    bRange should be (f2.range.shift(2))

    a.width.isInstanceOf[KnownWidth] should be (true)
    a.width.asInstanceOf[KnownWidth].get should be (7)
    b.width.isInstanceOf[KnownWidth] should be (true)
    b.width.asInstanceOf[KnownWidth].get should be (7)
  }
}