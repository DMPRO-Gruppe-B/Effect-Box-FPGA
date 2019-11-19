package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}
import chisel3.util._

import scala.sys.process.Process


class DelaySpec extends FlatSpec with Matchers {
  import DelayTest._

  behavior of "Delay"

  it should "Delay signal" in {
    chisel3.iotesters.Driver.execute(Array("--backend-name","treadle"), () => new Delay) { b =>
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

//    val fbFractionReduce = new FractionReduce(0.5)
//    val mixFractionReduce = new FractionReduce(0.5)

    poke(b.ctrl.fbFraction.numerator, 9)
    poke(b.ctrl.fbFraction.denominator, 10)

    poke(b.ctrl.mixFraction.numerator, 5)
    poke(b.ctrl.mixFraction.denominator, 10)

    poke(b.ctrl.delaySamples, 500 * 64)

    poke(b.io.in.valid, true.B)
    poke(b.io.in.ready, true.B)

    TestUtils.wrapInScript((source, pw) => {

      var i = 0
      for (line <- source.getLines()) {
        val n = line.toInt

        poke(b.io.in.bits, n)

//        if(i >= bufferSize){
//          poke(b.io.emptyBuffer, true.B)
//        }
//        else{
//          poke(b.io.emptyBuffer,false.B)
//          i = i+1
//        }

        step(1)

        val a = peek(b.io.out.bits)
        pw.write(s"$a\n")
      }
    })
    Process("python3 plotsine.py sound.txt new_sound.txt").run()
  }
}


