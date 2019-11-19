package EffectBox

import java.io.PrintWriter

import EffectBox.TestUtils._
import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{FlatSpec, Matchers}

import scala.sys.process.Process


class FirFilterSpec extends FlatSpec with Matchers {
  import FirFilerTest._

  behavior of "FirFilter"

//  it should "Should write to file from delay" in {
//    chisel3.iotesters.Driver(() => new DelayFilter(16)) { b =>
//      new DelayFromFile(b)
//    } should be(true)
//  }
//
//  it should "Should write to file from combined" in {
//    chisel3.iotesters.Driver(() => new Combiner(16)) { b =>
//      new CombinedFromFile(b)
//    } should be(true)
//  }
//
//  it should "Should write to file from bitcrush" in {
//    chisel3.iotesters.Driver(() => new BitCrush) { b =>
//      new CrushBitsFromFile(b, false, "bitcrush_sound.txt")
//    } should be(true)
//  }

  it should "Should generate sine wave" in {
    chisel3.iotesters.Driver(() => new SquareWave) { b =>
      new GeneratesSineWave(b).asInstanceOf[PeekPokeTester[SquareWave]]
    } should be(true)
  }
  it should "Should generate tremolo wave" in {
    chisel3.iotesters.Driver(() => new Tremolo) { b =>
      new DrawTremolo(b)
    } should be(true)
  }
  it should "Do tremolo" in {
    chisel3.iotesters.Driver(() => new Tremolo) { b =>
      new TremoloTest(b)
    } should be(true)
  }

//  it should "Bypass signal" in {
//    chisel3.iotesters.Driver(() => new DelayFilter(32)) { b =>
//      new BypassSignal(b)
//    } should be(true)
//  }
}

object FirFilerTest {
  class GeneratesSineWave(b: Wave) extends PeekPokeTester(b) {
    import scala.sys.process._
//    val wav = "bi"
//    val n = python("../software_prototype/music.py", "-p 1", )
    val pw = new PrintWriter("sine.txt")

    for (ii <- 0 until 360 * 2) {
      poke(b.io.inc, true)
      val top = peek(b.io.signal.numerator)
      val bot = peek(b.io.signal.denominator)
      val value = top.toDouble / bot.toDouble

      pw.write(f"$value\n")
      step(1)
    }
    pw.close()

    Process("python3 plotsine.py sine.txt").run()
//    Process("open sine.png").run()
  }

  class DrawTremolo(b: Tremolo) extends PeekPokeTester(b) {

    import scala.sys.process._

    poke(b.io.in.valid, true.B)
    poke(b.io.in.ready, true.B)
    //    val wav = "bi"
    //    val n = python("../software_prototype/music.py", "-p 1", )
    var p = 1
    var d = false
    var top = 1
    var bot = 10
    var w = 0
    val pw = new PrintWriter("sine.txt")
    for (ii <- 0 until 6480) {



      if (ii % 720 == 0) {
        w += 1
        w %= 4
      }


      poke(b.ctrl.periodMultiplier, p)
      poke(b.ctrl.bypass, false.B)
      poke(b.io.in.bits, 1000)
      poke(b.ctrl.depth, bot)
      poke(b.ctrl.waveSelect, w)
//      poke(b.ctrl.depth.denominator, bot)
      //      val top = peek(b.io.signal.numerator)
//      val bot = peek(b.io.signal.denominator)
//      val value = top.toDouble / bot.toDouble

      val value = peek(b.io.out.bits)
//      assert(value <= 1000 && value >= -1000)
      pw.write(f"$value\n")
      step(1)
    }
    pw.close()

    Process("python3 plotsine.py sine.txt").run()

  }

  class TremoloTest(b: Tremolo) extends PeekPokeTester(b) {

    TestUtils.wrapInScript((source, pw) => {
      poke(b.io.in.valid, true.B)
      poke(b.io.in.ready, true.B)
      var p = 12
      var d = false
      var bot = 8
      poke(b.ctrl.bypass, false)

      val lines = source.getLines()
      for ((line, i) <- lines.zipWithIndex) {
        val sample = line.toInt

//        if (i== 4000) {
//           bot = 3
//        }

        poke(b.ctrl.periodMultiplier, p)
        poke(b.ctrl.depth, bot)
//        poke(b.ctrl.depth.denominator, bot)
//        poke(b.ctrl.bypass, d)
        poke(b.io.in.bits, sample)

        step(1)
        val out = peek(b.io.out.bits)
        pw.write(f"$out\n")
      }

    })
    Process("python3 plotsine.py sound.txt new_sound.txt").run()
  }

  class Delay(b: DelayFilter) extends PeekPokeTester(b) {
    val inputs          = List(0x444f, 0x8218, 0xbeef, 0xcace)
    val expectedOutput  = List(0x4440, 0x8210, 0xbee0, 0xcac0)
    println("Delay Tester")
    println(inputs.mkString("[", "] [", "]"))
    println(expectedOutput.mkString("[", "] [", "]"))


    for (ii <- inputs.indices) {
      poke(b.io.bypass, false.B)
      poke(b.io.in, inputs(ii))
      // expect(b.io.dataOut, expectedOutput(ii))
      step(1)
    }
  }
  class DelayFromFile(b: DelayFilter) extends PeekPokeTester(b) {

    poke(b.io.bypass, false.B)

    FileUtils.readWrite("sound.txt", "fir_sound.txt",
        poke(b.io.in, _),
        () => peek(b.io.out),
        step
    )
  }
  class CombinedFromFile(b: Combiner) extends PeekPokeTester(b) {

    FileUtils.readWrite("sound.txt", "combined_sound.txt",
        poke(b.io.in, _),
        () => peek(b.io.out),
        step
    )
  }

// class CrushBitsFromFile(b: BitCrush, bypass: Boolean, outname: String) extends PeekPokeTester(b) {
//
//    poke(b.io.bypass, bypass.B)
//    poke(b.io.bitReduction, 4)
//
//    FileUtils.readWrite("sound.txt", outname,
//      poke(b.io.dataIn, _),
//      () => peek(b.io.dataOut),
//      step
//    )
//  }

  class BypassSignal(b: DelayFilter) extends PeekPokeTester(b) {

    val inputs = List(3244876, 362, 637288964, 725489652)

    println("Not Delay...")
    println(inputs.mkString("[", "] [", "]"))

    poke(b.io.bypass, true.B)

    for (ii <- 0 until inputs.length) {
      poke(b.io.in, inputs(ii))
      expect(b.io.out, inputs(ii))
      step(1)
    }
  }

}

