// See LICENSE for license details.

package chisel.dsp.fixedpoint

import chisel.dsp.DspException
import chisel3.core.OUTPUT
import org.scalatest.{Matchers, FlatSpec}

//scalastyle:off magic.number

class BehaviorSpec extends FlatSpec with Matchers {
  behavior of "constructor"

  it should "fail if too big for defaults" in {
    intercept[DspException] {
      val n = Number(120, 6, OUTPUT)
    }
    intercept[DspException] {
      val n = Number(120, 120, OUTPUT)
    }
    intercept[DspException] {
      val n = Number(120, -120, OUTPUT)
    }
  }

  it should "allow override of behavior" in {
    implicit val localBehavior = Behavior(Saturate, Truncate, None, None, None)

    val n = Number(120, 6, OUTPUT)
  }

//  Following does not work but I wish it did
//
//  it should "allow override of behavior elegantly" in {
//    Behavior.using(Behavior(Saturate, Truncate, None, None, None)) { localBehavior =>
//      val n = Number(120, 6, OUTPUT)
//    }
//  }
}
