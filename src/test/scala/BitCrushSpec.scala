package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}


class BitCrushSpec extends FlatSpec with Matchers {
  import BitCrushTest._

  behavior of "BitCrush"

  it should "Signed 0x0 should be 0x0" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b =>
      new ZeroCrushed(b)
    } should be(true)
  }
  it should "Set least segnificat 4 bits to 0" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b => 
      new CrushesBits(b)
    } should be(true)
  }
  it should "Process file" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b => 
      new CrushBitsFromFile(b)
    } should be(true)
  }

  it should "Bypass signal" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b => 
      new NotCrushesBits(b)
    } should be(true)
  }
}

object BitCrushTest {

  class ZeroCrushed(b: BitCrush) extends PeekPokeTester(b) {

    poke(b.io.bypass, false.B)
    poke(b.io.nCrushBits, 8)

    for (_ <- 0 until 100) {
      poke(b.io.dataIn, 0.S)
      step(1)
      expect(b.io.dataOut, 0.S)
    }

  }
  class CrushesBits(b: BitCrush) extends PeekPokeTester(b) {
    val inputs          = List(0x044f, 0x0218, 0x0eef, 0x5ace, -4)
    val expectedOutput  = List(0x0440, 0x0210, 0x0ee0, 0x5ac0, -16)
    println("Crush Bits...")
    println(inputs.mkString("[", "] [", "]"))
    println(expectedOutput.mkString("[", "] [", "]"))

    poke(b.io.bypass, false.B)
    poke(b.io.nCrushBits, 4)

    for (ii <- 0 until inputs.length) {
      poke(b.io.dataIn, inputs(ii))
      step(1)
      expect(b.io.dataOut, expectedOutput(ii))
    }
  }

  class CrushBitsFromFile(b: BitCrush) extends PeekPokeTester(b) {
   
      poke(b.io.bypass, false.B)
      poke(b.io.nCrushBits, 4)

      FileUtils.readWrite("sound.txt", "new_sound.txt", 
        poke(b.io.dataIn, _),
        () => peek(b.io.dataOut),
        step
      )
  }
  
  class NotCrushesBits(b: BitCrush) extends PeekPokeTester(b) {
    
    val inputs          = List(0x044f, 0x0218, 0x0eef, 0x0ace)
    
    println("Not Crush Bits...")
    println(inputs.mkString("[", "] [", "]"))

    poke(b.io.bypass, true.B)
    poke(b.io.nCrushBits, 4)

    for (ii <- 0 until inputs.length) {
      poke(b.io.dataIn, inputs(ii))
      expect(b.io.dataOut, inputs(ii))
      step(1)
    }
  }


}


