// See LICENSE for license details.

package edu.berkeley.eecs.bwrc.dsp

import Chisel._

class FixedPointNumber(val fractionalWidth: Int, range: NumberRange = UndefinedRange) extends Qnm {
  val bundle = new Bundle {
    val value = UInt(width = range.width)
  }
}
