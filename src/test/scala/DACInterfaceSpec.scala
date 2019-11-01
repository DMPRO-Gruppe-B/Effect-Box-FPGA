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
        poke(b.io.LRCLK, true)
        poke(b.io.sample, line.toInt)

        var first = true
        for (bit <- (TestUtils.toBinaryString(line.toInt, 16))) {
          
            if(first == true){
              expect(b.io.enable,true)
              first = !first
            }
            else{
              expect(b.io.enable,false)
            }

            step(1)
            
            expect(b.io.bit,bit.toString.toInt)
        }
        
        poke(b.io.LRCLK, false)
        for (i <- 1 to 16) {
          poke(b.io.sample, 0.S)
          step(1)
          expect(b.io.enable,false)
        }
    }
  }
}
