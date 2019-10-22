package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import chisel3.util._


class ADCInterfaceSpec extends FlatSpec with Matchers {
  import ADCTest._

  behavior of "ADC Spec"

  it should "Process input signal" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name","treadle"), () => new ADCInterface) { b => 
      new ADCRead(b)
    } should be(true)
  }
}

object ADCTest{
  class ADCRead(b: ADCInterface) extends PeekPokeTester(b) {
    import scala.math.abs
    import java.io.PrintWriter
    import scala.io.Source

    println("Processing input")

    val filename = "sound.txt"

    var CLK_counter = 0
    for (line <- FileUtils.getLines(filename)) {
        poke(b.io.LRCLK,true)

        for(bit <- TestUtils.toBinaryString(line.toInt,16)){

            poke(b.io.bitIn,bit.toInt)

            if((CLK_counter % 2) != 0)
            {
                poke(b.io.BCLK,true)
            }
            else{
                poke(b.io.BCLK,false)
                step(1)
            }
            step(1)

            CLK_counter = CLK_counter +1
        }
        poke(b.io.LRCLK,false)
        step(32)
    } 
  }
}




