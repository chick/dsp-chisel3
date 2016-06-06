// See LICENSE for license details.

package chisel.dsp

import Chisel._
import Chisel.internal.firrtl.Width

trait NumberRange {
  def width: Width
  def + (that: NumberRange): NumberRange
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
}

object UndefinedRange extends NumberRange {
  def width: Width = Width()
  def + (that: NumberRange): NumberRange = this
}

case class IntRange(min: BigInt, max: BigInt) extends NumberRange {
  if(min > max) throw new DspException(s"IntRange: Bad values min:$min must be strictly less than max:$max")

  def width: Width = Width(log2Up(max))
  def + (other: NumberRange): NumberRange = other match {
    case UndefinedRange    => UndefinedRange
    case that: IntRange    => IntRange(min.min(that.min), max.max(that.max))
    case that: DoubleRange => DoubleRange(min.min(that.min), max.max(that.max))
  }
//  override def equals(other: Any): Boolean = other match {
//    case that : IntRange => min == that.min && max == that.max
//    case _ => false
//  }
}

case class DoubleRange(min: BigInt, max: BigInt) extends NumberRange {
  if(min > max) throw new DspException(s"IntRange: Bad values min:$min must be strictly less than max:$max")

  def width: Width = Width(log2Up(max))
  def + (other: NumberRange): NumberRange = other match {
    case UndefinedRange    => UndefinedRange
    case that: IntRange    => DoubleRange(min.min(that.min), max.max(that.max))
    case that: DoubleRange => DoubleRange(min.min(that.min), max.max(that.max))
  }
}
