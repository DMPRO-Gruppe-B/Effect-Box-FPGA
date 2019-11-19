package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}

import scala.sys.process.Process


class BitCrushSpec extends FlatSpec with Matchers {
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

    poke(b.ctrl.bypass, false.B)
    poke(b.ctrl.bitReduction, 12)
    poke(b.ctrl.rateReduction, 0)

    poke(b.io.in.valid, true.B)
    poke(b.io.in.ready, true.B)

    TestUtils.wrapInScript((source, pw) => {
      val lines = source.getLines()
      for ((line, i) <- lines.zipWithIndex) {
        val sample = line.toInt

        poke(b.io.in.bits, sample)
        step(1)
        val out = peek(b.io.out.bits)
        pw.write(f"$out\n")
      }
    })
    Process("python3 plotsine.py sound.txt new_sound.txt").run()
  }

}


