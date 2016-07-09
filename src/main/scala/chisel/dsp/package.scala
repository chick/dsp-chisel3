// See LICENSE for license details.

package chisel.dsp

package object dsp {
  implicit class fromDoubleToFixedPointLiteral(val x: Double) extends AnyVal {
    def toFixed(fractionalWidth: Int = 0): FixedPointLiteral = FixedPointLiteral(x, fractionalWidth)
  }
  implicit class fromBigIntToFixedPointLiteral(val x: BigInt) extends AnyVal {
    def toFixed(fractionalWidth: Int = 0): FixedPointLiteral = FixedPointLiteral(x, fractionalWidth)
  }
  implicit class fromIntToFixedPointLiteral(val x: Int) extends AnyVal {
    def toFixed(fractionalWidth: Int = 0): FixedPointLiteral = FixedPointLiteral(BigInt(x), fractionalWidth)
  }

  def dspAssertion(condition: => Boolean, message: => String): Unit = {
    if(!condition) {
      throw DspException(message)
    }
  }
}
