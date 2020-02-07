package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}

import scala.sys.process.Process


class DistortionSpec extends FlatSpec with Matchers {
  import DistortionTest._

  behavior of "Distortion"

  it should "Limit the max amplitude" in {
    chisel3.iotesters.Driver(() => new Distortion) { b =>
      new Distorts(b)
    } should be(true)
  }

}

object DistortionTest {
  class Distorts(b: Distortion) extends PeekPokeTester(b) {

    poke(b.ctrl.amplitude, 5)

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
