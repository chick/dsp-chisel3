// See LICENSE for license details.

package chisel.dsp

object Util {
  case class DoubleRange(start: Double, stop: Double, step: Double) extends Iterator[Double] {
    var current = start
    override def hasNext: Boolean = current <= stop

    override def next(): Double = {
      val next = current
      current += step
      next
    }
  }
}
