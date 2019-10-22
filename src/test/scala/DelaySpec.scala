package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}
import chisel3.util._


class DelaySpec extends FlatSpec with Matchers {
  import DelayTest._

  behavior of "Delay"

  it should "Delay signal" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name","treadle"), () => new EffectBox) { b => 
      new DelaySignal(b)
    } should be(true)
  }
}

object DelayTest{
  class DelaySignal(b: EffectBox) extends PeekPokeTester(b) {
    import scala.math.abs
    import java.io.PrintWriter
    import scala.io.Source

    println("Delaying signal")

    val sampleRate = 11865
    val delayTime = 0.5
    val bufferSize = sampleRate*delayTime

    val fbFractionReduce = new FractionReduce(0.9)
    val mixFractionReduce = new FractionReduce(0.5)

    poke(b.io.fbNum,fbFractionReduce.numUInt)
    poke(b.io.fbDenom,fbFractionReduce.denomUInt)

    poke(b.io.mixNum,mixFractionReduce.numUInt)
    poke(b.io.mixDenom,mixFractionReduce.denomUInt)

    val filename = "sound.txt"
    val pw = new PrintWriter("new_" ++ filename)

    var i = 0
    for (line <- FileUtils.getLines(filename)) {
        val n = line.toInt

        poke(b.io.in, n)

        if(i >= bufferSize){
          poke(b.io.emptyBuffer, true.B)
        }
        else{
          poke(b.io.emptyBuffer,false.B)
          i = i+1
        }
        
        step(1)

        val a = peek(b.io.out)
        pw.write(s"$a\n")
    } 
    pw.close()
  }
}


