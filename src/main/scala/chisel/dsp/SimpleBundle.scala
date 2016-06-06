// See LICENSE for license details.

package chisel.dsp

import Chisel._

class SimpleBundle extends Bundle {
  val x = UInt(OUTPUT, width = 16)

  def + (that: SimpleBundle): SimpleBundle = {
    val result = Wire(this.cloneType)
    result.x := this.x + that.x
    result
  }
}
