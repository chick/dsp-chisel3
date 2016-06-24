// See LICENSE for license details.

package chisel.dsp

import chisel3.internal.firrtl.KnownWidth
import org.scalatest.{Matchers, FlatSpec}

class FixedPointNumberSpec extends FlatSpec with Matchers {
  val MaxTestFractionalWidth = 10
  val MaxSIntWidth = 100
  behavior of "FixedPointNumber#matchFractionalWidth"

  it should "return a and b of matching fractional widths, when b, fractional width is less than a's" in {
    for {
      sIntWidth        <- 0 to MaxSIntWidth
      fractionalWidth1 <- 2 to MaxTestFractionalWidth
      fractionalWidth2 <- 1 to fractionalWidth1
    } {
      println(s"Testing $fractionalWidth1 and $fractionalWidth2")
      val f1 = FixedPointNumber(fractionalWidth = fractionalWidth1)
      val f2 = FixedPointNumber(fractionalWidth = fractionalWidth2)

      val (a, b, aRange, bRange, newFractionalWidth) = f1.matchFractionalWidths(f2)

      newFractionalWidth should be (fractionalWidth1.max(fractionalWidth2))

      aRange should be (f1.range)
      bRange should be (f2.range.shift(fractionalWidth1 - fractionalWidth2))

      a.width.isInstanceOf[KnownWidth] should be(true)
      a.width.asInstanceOf[KnownWidth].get should be(newFractionalWidth)
      b.width.isInstanceOf[KnownWidth] should be(true)
      b.width.asInstanceOf[KnownWidth].get should be(newFractionalWidth)
    }
  }

  it should "return originals and their respective ranges when fractional difference is 0" in {
    val f1 = FixedPointNumber(10)
    val f2 = FixedPointNumber(10)

    val (a, b, aRange, bRange, fractionalWidth) = f1.matchFractionalWidths(f2)

    fractionalWidth should be (10)
    aRange should be (NumberRange.fromWidth(10))
    bRange should be (NumberRange.fromWidth(10))

    a.width.isInstanceOf[KnownWidth] should be (true)
    a.width.asInstanceOf[KnownWidth].get should be (10)
    b.width.isInstanceOf[KnownWidth] should be (true)
    b.width.asInstanceOf[KnownWidth].get should be (10)
  }

  it should "right pad b to match a when a has bigger fractional width" in {
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

  it should "right pad a to match b when b has bigger fractional width" in {
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

  behavior of "matchWidth"

  it should "pad left and right sides respectively of two fixed pointer numbers to make them similar" in {
    val f1 = FixedPointNumber(4, 2)
    val f2 = FixedPointNumber(3, 1)

//    val (a, b, aRange, bRange, intWidth, fractionalWidth) = f1.matchWidth(f2)
//
//    println(a)

  }
}
