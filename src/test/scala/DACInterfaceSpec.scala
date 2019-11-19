/*
package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import chisel3.util._
import scala.util.control.Breaks._

class DACInterfaceSpec extends FlatSpec with Matchers {
  import DACTest._

  behavior of "DAC Spec"

  it should "Output a sample equivalent to the input bits" in {
    chisel3.iotesters.Driver
      .execute(Array("--backend-name", "treadle"), () => new DACInterface) {
        b =>
          new DACRead(b)
      } should be(true)
  }
}

object DACTest {
  class DACRead(b: DACInterface) extends PeekPokeTester(b) {
    import scala.math.abs
    import java.io.PrintWriter
    import scala.io.Source

    val filename = "sound.txt"

      for (line <- FileUtils.getLines(filename)) {
        poke(b.io.sample, line.toInt.asUInt(16.W))

        var first = true
//<<<<<<< HEAD
        for (bit <- (TestUtils.toBinaryString(line.toInt, 16))) {
          
            if(first == true){
              expect(b.io.enable,true)
              first = !first
            }
            else{
              expect(b.io.enable,false)
            }

            step(1)
            
            expect(b.io.bit_left,bit.toString.toInt)
        }
        
        poke(b.io.LRCLK, false)
        for (i <- 1 to 16) {
          poke(b.io.sample, 0.S)
          step(1)
          expect(b.io.enable,false)
//=======

        var dacString = ""
        var bitString = ""
        
        for (bit <- (TestUtils.toBinaryString(line.toInt, 16))) {
            if(first == true){
              poke(b.io.enable,true.B)
              first = false
            }
            else{
              poke(b.io.enable,false.B)
            }
            poke(b.io.BCLK, false.B)
            step(1)

            bitString = bitString + bit
            dacString = dacString + peek(b.io.bit).toString

            //println(peek(b.io.bit).toString)
            poke(b.io.BCLK,true.B)
            step(1)
>>>>>>> adc-dac-16
        }

        println("bit: " + bitString)
        println("dac: " + dacString)
        println("")
    }
  }
}
*/
