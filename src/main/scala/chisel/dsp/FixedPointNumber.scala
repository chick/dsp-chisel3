// See LICENSE for license details.

package chisel.dsp

import chisel._
import chisel.util.{log2Up, Fill, Cat}

object FixedPointNumber {
  def apply(fractionalWidth: Int, integerWidth: Int = 0, direction: Direction = OUTPUT): FixedPointNumber = {
    apply(fractionalWidth, integerWidth, NumberRange.fromWidth(fractionalWidth), direction)
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
  val isLiteral: Boolean = false

  def + (that: FixedPointNumber): FixedPointNumber = {
    val (a, b, aRange, bRange, _) = this.matchFractionalWidths(that)

    val newRange = aRange + bRange

    val newIntWidth = this.integerWidth.max(that.integerWidth)

    val result = Wire(new FixedPointNumber(newIntWidth, fractionalWidth, newRange))

    result.value := a + b
    result
  }

  def * (that: FixedPointNumber): FixedPointNumber = {
    val (a, b, aRange, bRange, newFractionalWidth) = this.matchFractionalWidths(that)

    val newRange = aRange * bRange

    val newIntWidth = this.integerWidth + that.integerWidth
    val multipliedFractionalWidth = newFractionalWidth + newFractionalWidth

    val result = Wire(new FixedPointNumber(newIntWidth, multipliedFractionalWidth, newRange, Some(SInt())))

    result.value := a * b
    result
  }

  def := (that: FixedPointNumber): Unit = {
    val fractionalDifference = this.fractionalWidth - that.fractionalWidth
    if(fractionalDifference > 0) {
      this.value := that.value << fractionalDifference
    }
    else {
      this.value := that.value >> fractionalDifference.abs
    }
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
  def toBigInt(x: Double, fractionalWidth: Int): BigInt = {
    val multiplier = math.pow(2,fractionalWidth)
    val result = BigInt(math.round(x * multiplier))
    result
  }

  def toDouble(i: BigInt, fractionalWidth: Int) = {
    val multiplier = math.pow(2,fractionalWidth)
    val result = i.toDouble / multiplier
    result
  }

  def apply(x: Double, fractionalWidth: Int): FixedPointLiteral = {
    val bigInt = toBigInt(x, fractionalWidth)
    val integerWidth = log2Up(x.toInt) + 1

    val r = Wire(new FixedPointLiteral(bigInt, integerWidth, fractionalWidth, IntRange(bigInt, bigInt)))
    r.value := SInt(bigInt, integerWidth + fractionalWidth)
    r
  }
  def apply(bigInt: BigInt, integerWidth: Int = 0, fractionalWidth: Int = 0): FixedPointLiteral = {

    val r = Wire(new FixedPointLiteral(bigInt, integerWidth, fractionalWidth, IntRange(bigInt, bigInt)))
    r.value := SInt(bigInt, integerWidth + fractionalWidth)
    r
  }

}
class FixedPointLiteral(
                        val literalValue:  BigInt,
                        integerWidth:      Int,
                        fractionalWidth:   Int = 0,
                        range:             NumberRange = UndefinedRange,
                        underlying:        Option[SInt] = None
) extends FixedPointNumber(integerWidth, fractionalWidth, range, underlying) {
  override val isLiteral: Boolean = true

  override def cloneType: this.type = {
    new FixedPointLiteral(literalValue, integerWidth, fractionalWidth, range, underlying).asInstanceOf[this.type]
  }
  override def toString: String = {
    f"${FixedPointLiteral.toDouble(literalValue, fractionalWidth)}($literalValue%x):Q$integerWidth:$fractionalWidth"
  }
}
