// See LICENSE for license details.

package chisel.dsp

import chisel3.iotesters.{runPeekPokeTester, Backend}
import chisel3._
import chisel3.iotesters.{runPeekPokeTester, PeekPokeTester, Backend}
import firrtl_interpreter._
import org.scalatest.{Matchers, FlatSpec}
import chisel.dsp.fixedpoint._

/**
  * Adds arbitrarily parameterized fixed numbers together
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

  def makeIO(i: AdderIndex, direction: Direction): Number = {
    val param = which(i.n)
    Number(param.i + param.f, param.f, direction)
  }
  def getRange(i: AdderIndex): Array[Int] = {
    val param = which(i.n)
    val (maxNegative, maxPositive) = extremaOfSIntOfWidth(param.i + param.f)
    println(s"Range for (${param.i},${param.f}) is $maxNegative to $maxPositive")
//    (maxNegative.toInt to maxPositive.toInt).toArray
    Array(maxNegative.toInt, maxPositive.toInt)
  }
  def getIncrement(i: AdderIndex): Double = {
    val param = which(i.n)
    1.0 / math.pow(2, param.f)
  }
  def getFractionalWidth(i: AdderIndex): Int = {
    val param = which(i.n)
    param.f
  }
}

/**
  * Add two arbitrarily configured fixed point numbers into a third arbitrarily configured
  *
  * @param params
  */
class Adder(val params: AdderParams) extends Module {
  val io = new Bundle {
    val a = params.makeIO(A, INPUT)
    val b = params.makeIO(B, INPUT)
    val c = params.makeIO(C, OUTPUT)
  }
  io.c := io.a + io.b
  printf(s"a %d.S<${io.a.width.get}> b %d.S<${io.b.width.get}> c %d.S<${io.c.width.get}>\n",
    io.a.value.asSInt() , io.b.value.asSInt() , io.c.value.asSInt() )
}

/**
  * run through a range of values, validating add works as it is supposed to
  *
  * @param c
  * @param backend
  */
class AdderTests(c: Adder, backend: Option[Backend] = None) extends DspTester(c, _backend = backend) {
  val params = c.params

  val r1 = params.getRange(A)
  val r2 = params.getRange(B)
  for {
    aIndex <- r1
    bIndex <- r2
  } {
    println(s"Adder begin $aIndex $bIndex")
    val aInput   = (aIndex.toDouble * params.getIncrement(A)).FP(params.getFractionalWidth(A))
    val bInput   = (bIndex.toDouble * params.getIncrement(B)).FP(params.getFractionalWidth(B))

    println(s"$aIndex $bIndex $aInput $bInput")
    poke(c.io.a, aInput)
    poke(c.io.b, bInput)

    val expectedDouble = Literal(aInput.toDouble + bInput.toDouble, c.io.c.parameters.binaryPoint)

    val result = peek(c.io.c)
    println(s"addertests: $aInput + $bInput => $result expected $expectedDouble")
    if(result.literalValue != expectedDouble.literalValue) {
      println("X" * 80)
      println("X" * 80)
      println("X" * 80)
    }
    expect(c.io.c, expectedDouble)
  }
}

class AdderTestSpec extends FlatSpec with Matchers {
  "Adder" should "correctly add randomly generated numbers" in {
    val params = AdderParams(FixedParams(2,0), FixedParams(2,1), FixedParams(4,1))
    runPeekPokeTester(() => new Adder(params), "firrtl"){
      (c,b) => new AdderTests(c,b)} should be (true)
  }
}
