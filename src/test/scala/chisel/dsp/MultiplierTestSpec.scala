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

trait MultiplierIndex {
  def n: Int
}
case object MultA extends MultiplierIndex { val n = 0 }
case object MultB extends MultiplierIndex { val n = 1 }
case object MultC extends MultiplierIndex { val n = 2 }

case class MultiplierParams(a: FixedParams, b: FixedParams, c: FixedParams) {
  val which = Array(a, b, c)

  def makeIO(i: MultiplierIndex, direction: Direction): Number = {
    val param = which(i.n)
    Number(param.i, param.f, direction)
  }
  def getRange(i: MultiplierIndex): Array[Int] = {
    val param = which(i.n)
    val (maxNegative, maxPositive) = extremaOfSIntOfWidth(param.i)
    println(s"Range for (${param.i},${param.f}) is $maxNegative to $maxPositive")
    //    (maxNegative.toInt to maxPositive.toInt).toArray
    Array(maxNegative.toInt, maxPositive.toInt)
  }
  def getIncrement(i: MultiplierIndex): Double = {
    val param = which(i.n)
    1.0 / math.pow(2, param.f)
  }
  def getFractionalWidth(i: MultiplierIndex): Int = {
    val param = which(i.n)
    param.f
  }
}

/**
  * Add two arbitrarily configured fixed point numbers into a third arbitrarily configured
  *
  * @param params
  */
class Multiplier(val params: MultiplierParams) extends Module {
  val io = new Bundle {
    val a = params.makeIO(MultA, INPUT)
    val b = params.makeIO(MultB, INPUT)
    val c = params.makeIO(MultC, OUTPUT)
  }
  io.c := io.a * io.b
  printf(s"a %d.S<${io.a.width.get}> b %d.S<${io.b.width.get}> c %d.S<${io.c.width.get}>\n",
    io.a.value.asSInt() , io.b.value.asSInt() , io.c.value.asSInt() )
}

/**
  * run through a range of values, validating add works as it is supposed to
  *
  * @param c
  * @param backend
  */
class MultiplierTests(c: Multiplier, backend: Option[Backend] = None) extends DspTester(c, _backend = backend) {
  val params = c.params

  val r1 = params.getRange(MultA)
  val r2 = params.getRange(MultB)
  for {
    aIndex <- r1
    bIndex <- r2
  } {
    println(s"Multiplier begin $aIndex $bIndex")
    val aInput   = (aIndex.toDouble * params.getIncrement(MultA)).FP(params.getFractionalWidth(MultA))
    val bInput   = (bIndex.toDouble * params.getIncrement(MultB)).FP(params.getFractionalWidth(MultB))

    println(s"$aIndex $bIndex $aInput $bInput")
    poke(c.io.a, aInput)
    poke(c.io.b, bInput)

    val expectedDouble = Literal(aInput.toDouble * bInput.toDouble, c.io.c.parameters.binaryPoint)

    val result = peek(c.io.c)
    println(s"multiply tests: $aInput * $bInput => $result expected $expectedDouble")
    if(result.literalValue != expectedDouble.literalValue) {
      println("X" * 80)
      println("X" * 80)
      println("X" * 80)
    }
    expect(c.io.c, expectedDouble)
  }
}

class MultiplierTestSpec extends FlatSpec with Matchers {
  "Multiplier" should "correctly add randomly generated numbers" in {
    val params = MultiplierParams(FixedParams(4,1), FixedParams(4,2), FixedParams(8,3))
    runPeekPokeTester(() => new Multiplier(params), "firrtl"){
      (c,b) => new MultiplierTests(c,b)} should be (true)
  }
}
