// See LICENSE for license details.

package chisel.dsp

import chisel.internal.firrtl.Width
import chisel._
import chisel.util._

trait NumberRange {
  def width: Width
  def + (that: NumberRange): NumberRange
  def * (that: NumberRange): NumberRange
  def shift(n: Int): NumberRange
  def contains(value: BigInt): Boolean
}

object NumberRange {
  def apply(): NumberRange                         = UndefinedRange
  def apply(max: Int): NumberRange                 = IntRange(0, max)
  def apply(min: BigInt, max: BigInt): NumberRange = IntRange(min, max)

  def apply(max: Double): NumberRange = {
    //TODO: Make this more accurate
    IntRange(0, max.toInt)
  }
  def apply(min: Double, max: Double): NumberRange = {
    //TODO: Make this more accurate
    IntRange(min.toInt, max.toInt)
  }

  def fromWidth(fractionalWidth: Int, integerWidth: Int = 0): NumberRange = {
    val bitWidth = integerWidth + fractionalWidth
    val magnitude = BigInt("1" * bitWidth, 2)

    IntRange(-magnitude, magnitude)
  }
}

object UndefinedRange extends NumberRange {
  def width: Width = Width()
  def + (that: NumberRange): NumberRange = this
  def * (that: NumberRange): NumberRange = this
  def shift(n: Int): NumberRange = this
  def contains(value: BigInt): Boolean = true
}

case class IntRange(min: BigInt, max: BigInt) extends NumberRange {
  if(min > max) throw new DspException(s"IntRange: Bad values min:$min must be strictly less than max:$max")

  def width: Width = Width(log2Up(max))
  def + (other: NumberRange): NumberRange = other match {
    case UndefinedRange    => UndefinedRange
    case that: IntRange    => IntRange(this.min + that.min, this.max + that.max)
  }
  def * (other: NumberRange): NumberRange = other match {
    case UndefinedRange    => UndefinedRange
    case that: IntRange    => {
      val products = Array(this.min * that.min, this.min * that.max, this.max * that.min, this.max * that.max)
      IntRange(products.min, products.max)
    }
  }

  def contains(value: BigInt): Boolean = {
    min <= value && value <= max
  }
//  override def equals(other: Any): Boolean = other match {
//    case that : IntRange => min == that.min && max == that.max
//    case _ => false
//  }
  def shift(n: Int): NumberRange = {
    if(n >= 0) {
      new IntRange(min << n, max << n)
    }
    else {
      new IntRange(min >> n, max >> n)

    }
  }
}

