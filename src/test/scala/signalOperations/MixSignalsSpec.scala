package signalOperations

import chisel3._
import chisel3.iotesters.PeekPokeTester
import org.scalatest.{Matchers, FlatSpec}


class MixSignalsSpec extends FlatSpec with Matchers {
  import MixSignalsTest._
  behavior of "MixSignalsSpec"

  it should "Mix some signals at given ratios from a list." in {
    chisel3.iotesters.Driver(() => new MixSignals()) { c =>
      new MixSpecificSignals(c)
    } should be(true)
  }

}


object MixSignalsTest {

  class MixSpecificSignals(c: MixSignals) extends PeekPokeTester(c) {

    // Create some test data:
    val signal1_list : List[BigInt] = List(47483648, -47806, 900867564, 6438758, 824, 0, 2147483647)
    val signal2_list : List[BigInt] = List(54624633, 432452, -664538086, 0, 344, 50, 567391956)
    val numerator_list : List[BigInt] = List(1, 5, 12, 8, 16, 0, 1)
    val denominator_list : List[BigInt] = List(5, 7, 56, 8, 70, 1, 5)

    // Poke all test data:
    for (ii <- 0 until signal1_list.length) {
      val a = signal1_list(ii)
      val b = signal2_list(ii)
      val n = numerator_list(ii)
      val d = denominator_list(ii)

      poke(c.io.operand1, a.S(32.W))
      poke(c.io.operand2, b.S(32.W))
      poke(c.io.numerator1, n.U(8.W))
      poke(c.io.denominator1, d.U(8.W))
      val result = peek(c.io.result)

      // Calculate the exact "ish" result using BigInt:
      val exact = (a*n + b*(d-n))/d

      // Print the results_
      println("Mixing signals:_")
      println("Expected " + exact + ", and got " + result)

      // Check if the results are equal (could be considered to make the check less strict):
      expect(c.io.result, exact)


    }


  }

}