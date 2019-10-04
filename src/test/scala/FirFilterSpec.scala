package EffectBox

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}


class FirFilterSpec extends FlatSpec with Matchers {
  import FirFilerTest._

  // behavior of "FirFilter"

  // it should "Set least segnificat 4 bits to 0" in {
  //   chisel3.iotesters.Driver(() => new BitCrush) { b => 
  //     new FirFilter(b)
  //   } should be(true)
  // }
}

object FirFilerTest {
  
  class Delay(b: DelayFilter) extends PeekPokeTester(b) {
    val inputs          = List(0x444f, 0x8218, 0xbeef, 0xcace)
    val expectedOutput  = List(0x4440, 0x8210, 0xbee0, 0xcac0)
    println("Delay Tester")
    println(inputs.mkString("[", "] [", "]"))
    println(expectedOutput.mkString("[", "] [", "]"))


    for (ii <- 0 until inputs.length) {
      poke(b.io.in, inputs(ii))
      // expect(b.io.dataOut, expectedOutput(ii))
      step(1)
    }
  }
  class DelayFromFile(b: DelayFilter) extends PeekPokeTester(b) {
    val inputs          = List(0x444f, 0x8218, 0xbeef, 0xcace)
    val expectedOutput  = List(0x4440, 0x8210, 0xbee0, 0xcac0)
    println("Delay Tester")
    println(inputs.mkString("[", "] [", "]"))
    println(expectedOutput.mkString("[", "] [", "]"))

    for (ii <- 0 until inputs.length) {
      poke(b.io.in, inputs(ii))
      // expect(b.io.dataOut, expectedOutput(ii))
      step(1)
    }
  }

}
