package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}


class BitCruchSpec extends FlatSpec with Matchers {
  import BitCrushTest._

  behavior of "BitCrush"

  it should "Set least segnificat 4 bits to 0" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b => 
      new CrushesBits(b)
    } should be(true)
  }

  it should "Bypass signal" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b => 
      new NotCrushesBits(b)
    } should be(true)
  }

  it should "print from file" in {
    chisel3.iotesters.Driver(() => new BitCrush) { b => 
      new CrushBitsFromFile(b)
    } should be(true)
  }
}

object BitCrushTest {
  
  class CrushesBits(b: BitCrush) extends PeekPokeTester(b) {
    val inputs          = List(0x444f, 0x8218, 0xbeef, 0xcace)
    val expectedOutput  = List(0x4440, 0x8210, 0xbee0, 0xcac0)
    println("Crush Bits...")
    println(inputs.mkString("[", "] [", "]"))
    println(expectedOutput.mkString("[", "] [", "]"))

    poke(b.io.bypass, false.B)
    poke(b.io.nCrushBits, 4)

    for (ii <- 0 until inputs.length) {
      poke(b.io.dataIn, inputs(ii))
      expect(b.io.dataOut, expectedOutput(ii))
      step(1)
    }
  }

  class NotCrushesBits(b: BitCrush) extends PeekPokeTester(b) {
    
    val inputs          = List(0x444f, 0x8218, 0xbeef, 0xcace)
    val expectedOutput  = List(0x444f, 0x8218, 0xbeef, 0xcace)
    
    println("Not Crush Bits...")
    println(inputs.mkString("[", "] [", "]"))
    println(expectedOutput.mkString("[", "] [", "]"))

    poke(b.io.bypass, true.B)
    poke(b.io.nCrushBits, 4)

    for (ii <- 0 until inputs.length) {
      poke(b.io.dataIn, inputs(ii))
      expect(b.io.dataOut, expectedOutput(ii))
      step(1)
    }
  }

  class CrushBitsFromFile(b: BitCrush) extends PeekPokeTester(b) {
      import scala.io.Source
      import scala.math.abs
   
      poke(b.io.bypass, false.B)
      poke(b.io.nCrushBits, 4)

      val filename = "sound.txt"

      for (line <- Source.fromFile(filename).getLines) {
          val n: Short = line.toShort
          println(n.toString)

          poke(b.io.dataIn, abs(n))
          expect(b.io.dataOut, abs(n) & 0xfff0)
          step(1)

      } 

  }
}


