package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import chisel3.util._


class DelaySpec extends FlatSpec with Matchers {
  import DelayTest._

  behavior of "Delay"

  it should "Delay signal" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name","treadle"), () => new Delay(32)) { b => 
      new DelaySignal(b)
    } should be(true)
  }
}

object DelayTest{
  class DelaySignal(b: Delay) extends PeekPokeTester(b) {
    import scala.math.abs
    import java.io.PrintWriter
    import scala.io.Source

    println("Delaying signal")

    val sampleRate = 11865
    val delayTime = 0.5
    val bufferSize = sampleRate*delayTime

    // Should be changed to something more sane
    val mixNum = 1
    val mixDenom = 2

    val fbNum = 4
    val fbDenom = 5

    poke(b.io.mixNum,mixNum)
    poke(b.io.mixDenom,mixDenom)
    poke(b.io.fbNum,fbNum)
    poke(b.io.fbDenom,fbDenom)

    val filename = "sound.txt"
    val pw = new PrintWriter("new_" ++ filename)

    var i = 0
    for (line <- FileUtils.getLines(filename)) {
        val n = line.toInt

        poke(b.io.in, n)
        
        step(1)
        val a = peek(b.io.out)
        pw.write(s"$a\n")

        if(i > bufferSize){
          poke(b.io.emptyBuffer, true.B)
        }
        else{
          poke(b.io.emptyBuffer,false.B)
          i = i+1
        }
    } 
    pw.close()
  }
}


