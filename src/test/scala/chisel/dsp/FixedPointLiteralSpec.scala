// See LICENSE for license details.

package chisel.dsp

import chisel.util.{log2Ceil, log2Up}

import org.scalatest.{FlatSpec, Matchers}

class FixedPointLiteralSpec extends FlatSpec with Matchers {
  behavior of "constructors"

  they should "create literals with proper values" in {

    for(i <- -9 to 9) {
      println(s"num $i  ${FixedPointLiteral.requiredBitsForSInt(BigInt(i))} ${BigInt(i).bitLength}")
    }
    var a = FixedPointLiteral(1.0, 0)
    println(s"FixedPointLiteral(1.0, 0) => $a")

    a = FixedPointLiteral(-2.0, 1)
    println(s"FixedPointLiteral(-2.0, 1) => $a")
  }
  behavior of "toBigInt"

  it should "round trip with toDouble" in {
    for(i <- -10 to 10) {
      val x = i.toDouble / 8.0
      val bigInt = FixedPointLiteral.toBigInt(x, 3)
      println(f"$i%10d $x%4.6f $bigInt%6d ${toBin(bigInt)}")
    }
  }

  def toBin(x: BigInt): String = {
    (8 to 0 by -1).map { i => if(x.testBit(i)) "1" else "0" }.mkString
  }
}
