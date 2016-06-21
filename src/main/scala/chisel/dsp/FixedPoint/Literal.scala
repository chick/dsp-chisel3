// See LICENSE for license details.

package chisel.dsp.FixedPoint

case class Literal(val value: BigInt, parameters: Parameters) {
  def + (that: Literal): Literal = {
    Literal(this.value + that.value, this.parameters + that.parameters)
  }
}
