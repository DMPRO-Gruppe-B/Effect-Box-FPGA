package EffectBox

import chisel3._
import chisel3.util.Counter

class FirFilterControl extends Bundle {
  val bypass = Input(Bool())
}

// https://www.chisel-lang.org/
// Generalized FIR filter parameterized by the convolution coefficients
class FirFilter(bitWidth: Int, coeffs: Seq[SInt]) extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(bitWidth.W))
    val out = Output(SInt(bitWidth.W))
  })
  val ctrl = IO(new FirFilterControl)

  when (ctrl.bypass){
    io.out := io.in
  } .otherwise {

    //  val counter = Counter()
    // Create the serial-in, parallel-out shift register
    val zs = Reg(Vec(coeffs.length, SInt(bitWidth.W)))
    zs(0) := io.in
    for (i <- 1 until coeffs.length) {
      zs(i) := zs(i-1)
    }

    // Do the multiplies
    val products = VecInit.tabulate(coeffs.length)(i => zs(i) * coeffs(i))

    // Sum up the products
    io.out := products.reduce(_ + _)

  }

}
