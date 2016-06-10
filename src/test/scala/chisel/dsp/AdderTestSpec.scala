// See LICENSE for license details.

package chisel.dsp

import Chisel._
import Chisel.iotesters.{runPeekPokeTester, PeekPokeTester, Backend}
import org.scalatest.{Matchers, FlatSpec}

/**
  * Adds arbitrarily parameterized fixeds together
  */

trait AdderIndex {
  def n: Int
}
case object A extends AdderIndex { val n = 0 }
case object B extends AdderIndex { val n = 1 }
case object C extends AdderIndex { val n = 2 }

case class FixedParams(i: Int, f: Int, l: Int, h: Int)

object FixedParams {
  def apply(i: Int, f: Int): FixedParams = {
    val maxPositive = BigInt("1" * (i + f - 1), 2)
    val maxNegative = -(maxPositive + 1)
    FixedParams(i, f, maxNegative.toInt, maxPositive.toInt)
  }
}
case class AdderParams(a: FixedParams, b: FixedParams, c: FixedParams) {
  val which = Array(a, b, c)
  def makeIO(i: AdderIndex, direction: Direction): FixedPointNumber = {
    val param = which(i.n)
    FixedPointNumber(param.i, param.f, NumberRange(param.l, param.h), direction)
  }
  def getRange(i: AdderIndex): Iterator[Int] = {
    val param = which(i.n)
    val maxPositive = BigInt("1" * (param.i + param.f - 1), 2)
    val maxNegative = -(maxPositive + 1)
//    new Range(maxNegative.toInt, maxPositive.toInt, 1).iterator
    new Range(0, maxPositive.toInt, 1).iterator
  }
  def getIncrement(i: AdderIndex): Double = {
    val param = which(i.n)
    1.0 / math.pow(2, param.f)
  }
}

/**
  * Add two arbitrarily configured fixed point numbers into a third arbitrarily configured
  * @param params
  */
class Adder(val params: AdderParams) extends Module {
  val io = new Bundle {
    val a = params.makeIO(A, INPUT)
    val b = params.makeIO(B, INPUT)
    val c = params.makeIO(C, OUTPUT)
  }
  io.c := io.a + io.b
  printf("a %d b %d c %d\n", io.a.value.asUInt() , io.b.value.asUInt() , io.c.value.asUInt() )
}

/**
  * run through a range of values, validating add works as it is supposed to
  * @param c
  * @param backend
  */
class AdderTests(c: Adder, backend: Option[Backend] = None) extends PeekPokeTester(c, _backend = backend) {
  val params = c.params

  val r1 = params.getRange(A)
  val r2 = params.getRange(B)
  for {
    aIndex <- r1
    bIndex <- r2
  } {
    val aVal   = aIndex.toDouble * params.getIncrement(A)
    val bVal   = bIndex.toDouble * params.getIncrement(B)

    println(s"$aIndex $bIndex $aVal $bVal")
    poke(c.io.a.value, BigInt(aIndex))
    poke(c.io.b.value, BigInt(bIndex))

    val expectedDouble = aVal + bVal

    val result = peek(c.io.c.value)
    val doubleResult = FixedPointLiteral.toDouble(result, c.io.c.fractionalWidth)

    println(s"poke(a,$aIndex($aVal)) + poke(b,$bIndex($bVal)) => $result($doubleResult) expected $expectedDouble   $params")


  }
}

class AdderTestSpec extends FlatSpec with Matchers {
  "Adder" should "correctly add randomly generated numbers" in {
    val params = AdderParams(FixedParams(3,1), FixedParams(3,1), FixedParams(4,1))
    runPeekPokeTester(() => new Adder(params)){
      (c,b) => new AdderTests(c,b)} should be (true)
  }
}
