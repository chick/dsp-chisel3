// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel3._
import chisel.dsp.{Qnm, NumberRange}
import chisel3.util.{log2Up, Fill, Cat}
import chisel.dsp._

object Number {
//  implicit val defaultBehavior = Behavior(Saturate, Truncate, Some(16), Some(-16), Some(32))

  def apply(numberOfBits: Int, binaryPoint: Int, direction: Direction)
           (implicit behavior: Behavior): Number = {
    val bitWidth = binaryPoint + numberOfBits
    direction match {
      case OUTPUT => new Number(Parameters(numberOfBits, binaryPoint))
      case INPUT  => new Number(Parameters(numberOfBits, binaryPoint)).flip()
      case NO_DIR => new Number(Parameters(numberOfBits, binaryPoint))
      case _      => new Number(Parameters(numberOfBits, binaryPoint))
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
  * @param initialParameters    information about this Number
  */
class Number(initialParameters: Parameters)(implicit behavior: Behavior) extends Bundle with Qnm {
  var parameters = initialParameters

  behavior.assertConforming(this)

  val value = SInt(OUTPUT, width = parameters.numberOfBits)
  val isLiteral: Boolean = false

  def + (that: Number): Number = {
    val resolver = BitsResolver(this, that)
    val newParameters = this.parameters + that.parameters

    println(s"Number.add newParameters are $newParameters")
    val result = Wire(new Number(newParameters))
    val (newThisValue, newThatValue) = resolver.addableSInts

    result.value := newThisValue +& newThatValue
    result
  }

  def * (that: Number): Number = {
    val resolver = BitsResolver(this, that)
    val newParameters = this.parameters * that.parameters

    val result = Wire(new Number(newParameters))

    result.value := this.value * that.value
    result
  }

  def := (that: Number): Unit = {
    val resolver = BitsResolver(this, that)

    val fractionalDifference = resolver.fractionalDifference
    if(fractionalDifference > 0) {
      this.value := that.value << fractionalDifference
    }
    else {
      this.value := that.value >> fractionalDifference.abs
    }
  }

  override def cloneType: this.type = {
    new Number(parameters).asInstanceOf[this.type]
  }
  override def toString: String = {
    parameters.asFP
  }
}


