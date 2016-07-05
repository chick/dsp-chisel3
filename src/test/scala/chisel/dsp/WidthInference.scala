//// See LICENSE for license details.
//
//package chisel.dsp
//
//import chisel3.iotesters.runPeekPokeTester
//import chisel3.iotesters.Backend
//import chisel3._
//import chisel3.core.{Bundle, Module, OUTPUT}
//import org.scalatest.{Matchers, FlatSpec}
//
//// scalastyl e:off magic.number
//class VectorThingy extends Module {
//  val io = new Bundle {
//    val flag = Bool(INPUT)
//    val a = FixedPointNumber(5, 4, OUTPUT).flip()
//    val b = FixedPointNumber(5, 6, OUTPUT).flip()
//    val c = FixedPointNumber(5, 4, OUTPUT)
//  }
//  val t1 = io.a + io.b
//  val fRegister = Reg(new FixedPointNumber(1, 1, NumberRange(1.0, 2.0), Some(SInt())))
////  val fRegister = Reg(Seq(io.a,io.b))
//
//  when (io.flag) {
//    fRegister := io.a
//  }   .otherwise {
//    fRegister := io.b //fRegister should have 6 frac bits, so previous assign should be "fRegister.value := io.a.value << 2"
//  }
//  fRegister := io.a + io.b
//
//  io.c := fRegister
//
//  printf("a %d b %d c %d\n", io.a.value.asSInt() , io.b.value.asSInt() , io.c.value.asSInt() )
//}
//
//class VectorThingyTests(c: VectorThingy, backend: Option[Backend] = None) extends DspTester(c, _backend = backend) {
//
//  println(s"VectorThingy: ${Util.DoubleRange(1.0, 10.0, 0.5).toList}")
//  for {
//    aInput <- Util.DoubleRange(0.0, 5.0, 0.5)
//    bInput <- Util.DoubleRange(0.0, 5.0, 0.5)
//  } {
//    println(s"VectorThingy begin $aInput $bInput")
//
//
//  //    println(s"$aIndex $bIndex $aInput $bInput")
//    poke(c.io.a, aInput)
//    poke(c.io.b, bInput)
//    step(1)
//
//    val expectedDouble = FixedPointLiteral(aInput.toDouble + bInput.toDouble, 4)
//
//    val result = peek(c.io.c)
//    println(s"addertests: $aInput + $bInput => $result expected $expectedDouble")
//    if(result.literalValue != expectedDouble.literalValue) {
//      println("X" * 80)
//      println("X" * 80)
//      println("X" * 80)
//    }
//    expect(c.io.c, expectedDouble)
//  }
//}
//
//class WidthInference extends FlatSpec with Matchers {
//
//  "Adder" should "correctly add randomly generated numbers" in {
//    val params = AdderParams(FixedParams(2,0), FixedParams(2,1), FixedParams(4,1))
//    runPeekPokeTester(() => new VectorThingy, "firrtl"){
//      (c,b) => new VectorThingyTests(c,b)} should be (true)
//  }
//}
