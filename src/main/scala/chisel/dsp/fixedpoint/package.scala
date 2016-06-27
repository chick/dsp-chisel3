package chisel.dsp

package object fixedpoint {
  implicit class fromDoubleToLiteral(val x: Double) extends AnyVal {
    def FP(fractionalWidth: Int = 0): Literal = Literal(x, fractionalWidth)
  }
  implicit class fromBigIntToLiteral(val x: BigInt) extends AnyVal {
    def FP(fractionalWidth: Int = 0): Literal = Literal(x, fractionalWidth)
  }
  implicit class fromIntToLiteral(val x: Int) extends AnyVal {
    def FP(fractionalWidth: Int = 0): Literal = Literal(BigInt(x), fractionalWidth)
  }

}
