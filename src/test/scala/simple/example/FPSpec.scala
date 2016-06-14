// See LICENSE for license details.

package simple.example

//import Chisel.iotesters.ChiselFlatSpec
//import chisel.iotesters.{runPeekPokeTester, Backend, PeekPokeTester}
//import chisel._
//import chisel.testers.BasicTester
//import chisel.util.{log2Up, Fill, Cat}
//import org.scalatest.{Matchers, FlatSpec}
//
//class FP(val integerWidth: Int, val fractionalWidth: Int) extends Bundle {
//  val num = Wire(SInt(OUTPUT, integerWidth + fractionalWidth))
//  val isLiteral = false
//
//  def + (that: FP): FP = {
//    val fractionalDifference = this.fractionalWidth - that.fractionalWidth
//    val (num1, num2) = {
//      if(fractionalDifference > 0) {
//        (this.num, Cat(that.num, Fill(fractionalDifference, 0.U)).asSInt())      // pad that.num
//      }
//      else {
//        (Cat(this.num, Fill(fractionalDifference.abs, 0.U)).asSInt(), that.num)  // bad this.num
//      }
//    }
//
//    val returnFP = Wire(
//      new FP(this.integerWidth.max(that.integerWidth) + 1, this.fractionalWidth.max(that.fractionalWidth))
//    )
//    returnFP.num := num1 + num2
//
//    returnFP
//  }
//  override def cloneType: this.type = new FP(integerWidth, fractionalWidth: Int).asInstanceOf[this.type]
//}
//
//// class FPL(initialValue: Double, integerWidth: Int, fractionalWidth: Int)
//class FPL(override val integerWidth: Int, override val fractionalWidth: Int)
//  extends FP(integerWidth: Int, fractionalWidth: Int) {
//  override val isLiteral = true
//}
//
//object FPL {
//  def toBigInt(x: Double, fractionalWidth: Int): BigInt = {
//    val multiplier = math.pow(2,fractionalWidth)
//    val result = BigInt(math.round(x * multiplier))
//    result
//  }
//
//  def apply(initialValue: Double, fractionalWidth: Int): FPL = {
//    val bigInt = toBigInt(initialValue, fractionalWidth)
//    val integerWidth = log2Up(initialValue.toInt)
//
//    val r = new FPL(integerWidth, fractionalWidth)
//    r.num := bigInt.S
//    r
//  }
//}
//class FPAdd3(iw: Int, fw: Int) extends Module {
//  val io = new Bundle {
//    val a = (new FP(iw, fw)).flip()
//    val c = new FP(iw, fw) + FPL((3<<fw).toDouble, fw)
//  }
//}
//class FPTester(c: FPAdd3) extends BasicTester  {
//  c.io.a.num := 1.S
//
//  assert(c.io.c.num === "h30".S)
//
//  stop()
//}
//class FPSpec extends ChiselFlatSpec {
//  val intWidth = 4
//  val fracWidth = 4
//
//  "fp adder" should "add 2 numbers" in {
//    assertTesterPasses {
//      val c = Module(new FPAdd3(4, 4))
//      new FPTester(c)
//    }
//  }
//
//}
