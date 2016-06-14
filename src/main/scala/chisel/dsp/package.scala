package chisel

package object dsp {
  implicit class fromDoubleToFixedPointLiteral(val x: Double) extends AnyVal {
    def toFixed(fractionalWidth: Int = 0): FixedPointLiteral = FixedPointLiteral(x, fractionalWidth)
  }
  implicit class fromBigIntToFixedPointLiteral(val x: BigInt) extends AnyVal {
    def toFixed(fractionalWidth: Int = 0): FixedPointLiteral = FixedPointLiteral(x, fractionalWidth)
  }
}
