// See LICENSE for license details.

package chisel.dsp

import Chisel._

object FixedPointNumber {
  def apply(fractionalWidth: Int, integerWidth: Int = 0): FixedPointNumber = {
    apply(fractionalWidth, integerWidth, NumberRange.fromWidth(fractionalWidth), NO_DIR)
  }
  def apply(fractionalWidth: Int, integerWidth: Int, range: NumberRange, direction: Direction): FixedPointNumber = {
    val bitWidth = fractionalWidth + integerWidth
    direction match {
      case OUTPUT => new FixedPointNumber(fractionalWidth, integerWidth, range, Some(SInt(OUTPUT, bitWidth)))
      case INPUT  => new FixedPointNumber(fractionalWidth, integerWidth, range, Some(SInt(OUTPUT, bitWidth))).flip()
      case NO_DIR => new FixedPointNumber(fractionalWidth, integerWidth, range, Some(SInt(NO_DIR, bitWidth)))
      case _      => new FixedPointNumber(fractionalWidth, integerWidth, range, Some(SInt(OUTPUT, bitWidth)))
    }
  }
}

/**
  * A fixed point number has an integer width and a fractional width
  * It has an underlying SInt, that has a bit width, possibly inferred that does not have
  * to match the sum of it's fractional and integer components, this is a simple form of
  * exponential notation.
  * A fixed point number has a minimum and maximum value, these are used in to possilby limit bit
  * expansion below the default behavior of various arithmetic and logic operations.
  *
  * @param integerWidth    the number of bits to the left of the binary decimal point
  * @param fractionalWidth the number of bits to the rigth of binary decimal point
  * @param range           a range object, with a minimum and maximum value
  * @param gen             an optional generator for the underlying SInt
  */
class FixedPointNumber(
                        val integerWidth:    Int,
                        val fractionalWidth: Int,
                        val range:           NumberRange,
                        gen:                 Option[SInt] = None
                      ) extends Bundle with Qnm {

  val value = gen.getOrElse(SInt(OUTPUT, width = integerWidth + fractionalWidth))

  def + (that: FixedPointNumber): FixedPointNumber = {
    val (a, b, aRange, bRange, newFractionalWidth) = this.matchFractionalWidths(that)

    val newRange = aRange + bRange

    val newIntWidth = this.integerWidth.max(that.integerWidth)

    val result = Wire(new FixedPointNumber(newIntWidth, fractionalWidth, newRange, Some(SInt())))

    result.value := a + b
    result
  }

  def * (that: FixedPointNumber): FixedPointNumber = {
    val (a, b, aRange, bRange, newFractionalWidth) = this.matchFractionalWidths(that)

    val newRange = aRange * bRange

    val newIntWidth = this.integerWidth.max(that.integerWidth)
    val multipliedFractionalWidth = newFractionalWidth * newFractionalWidth

    val result = Wire(new FixedPointNumber(newIntWidth, fractionalWidth, newRange, Some(SInt())))

    result.value := a + b
    result
  }

  def getRange(dummy: Int = 0): NumberRange = range

  def matchFractionalWidths(that: FixedPointNumber): (SInt, SInt, NumberRange, NumberRange, Int) = {
    val newFractionalWidth = fractionalWidth.max(that.fractionalWidth)
    val fractionalDifference = fractionalWidth - that.fractionalWidth

    if(fractionalDifference > 0) {
      (
        this.value,
        Cat(that.value, Fill(fractionalDifference, 0.U)).asSInt(),
        this.range,
        that.range.shift(fractionalDifference),
        newFractionalWidth
      )
    }
    else if(fractionalDifference < 0) {
      (
        Cat(this.value, Fill(fractionalDifference.abs, 0.U)).asSInt(),
        that.value,
        this.range.shift(fractionalWidth.abs),
        that.range,
        newFractionalWidth
        )
    }
    else {
      (this.value, that.value, range, that.range, fractionalWidth)
    }
  }

//  def matchWidth(that: FixedPointNumber): (SInt, SInt, NumberRange, NumberRange, Int, Int) = {
//    val (a, b, aRange, bRange, fractionalWidth) = matchFractionalWidths(that)
//    val widthDifference = b.getWidth - a.getWidth
//    val (newA, newB) = {
//      if (widthDifference > 0) {
//        (Cat(Fill(widthDifference, sign), a).asSInt(), b)
//      }
//      else if (widthDifference < 0) {
//        (a, Cat(Fill(widthDifference.abs, that.sign), b).asSInt())
//      }
//      else {
//        (a, b)
//      }
//    }
//    val newIntWidth = a.getWidth - fractionalWidth
//    (newA, newB, aRange, bRange, newIntWidth, fractionalWidth)
//  }

  override def cloneType: this.type = {
    new FixedPointNumber(integerWidth, fractionalWidth, range, gen).asInstanceOf[this.type]
  }
}

object FixedPointLiteral {
  def apply(
             literalValue : Int,
             fractionalWidth: Int = 0,
             range: NumberRange = UndefinedRange,
             underlying: Option[SInt] = None
           ): FixedPointLiteral = {
    Wire(new FixedPointLiteral(literalValue, range = NumberRange(literalValue.abs)))
  }
}
class FixedPointLiteral(
                        literalValue : Int,
                        fractionalWidth: Int = 0,
                        range: NumberRange = UndefinedRange,
                        underlying: Option[SInt] = None
) extends FixedPointNumber(0, fractionalWidth, range, underlying) {
  override val value = Wire(underlying.getOrElse(SInt(width = range.width)))

  value := literalValue.U

  override def cloneType: this.type = {
    new FixedPointLiteral(literalValue, fractionalWidth, range, underlying).asInstanceOf[this.type]
  }
}
