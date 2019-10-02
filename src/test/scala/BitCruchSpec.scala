package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
// import TestUtils._


class BitCruchSpec extends FlatSpec with Matchers {
  import BitCrushTest._

  behavior of "BitCrush"

  it should "Set least segnificat 4 bits to 0" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b => 
      new CrushesBits(b)
    } should be(true)
  }

}



object BitCrushTest {
  
  class CrushesBits(b: BitCrush) extends PeekPokeTester(b) {
    val inputs = List(0x444f, 0x8218, 0xbeef, 0xcace)
    val expectedOutput = inputs.map { in => in & 0xfff0 }
    println("Crush Bits...")
    println(inputs.mkString("[", "] [", "]"))
    println(expectedOutput.mkString("[", "] [", "]"))

    for (ii <- 0 until inputs.length) {
      poke(b.io.dataIn, inputs(ii))
      poke(b.io.nCrushBits, 4)
      expect(b.io.dataOut, expectedOutput(ii))
      step(1)
    }
  }


}


