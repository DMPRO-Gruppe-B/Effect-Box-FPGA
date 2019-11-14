/*package EffectBox

import chisel3._
import chisel3.util.Counter
import chisel3.MultiIOModule


class FirFilterControl extends Bundle {
  val bypass = Input(Bool())
}

// https://www.chisel-lang.org/
// Generalized FIR filter parameterized by the convolution coefficients
class FirFilter(bitWidth: Int, coeffs: Seq[UInt]) extends MultiIOModule {

  val io = IO(new EffectBundle)
  val ctrl = IO(new FirFilterControl)


  // Create the serial-in, parallel-out shift register
  val zs = Reg(Vec(coeffs.length, SInt(bitWidth.W)))

  io.in.ready := true.B
  io.out.valid := io.in.valid


  when (ctrl.bypass){
    io.out.bits := io.in.bits
  } .otherwise {

    when(io.in.valid) {
      //  val counter = Counter()
      zs(0) := io.in.bits
      for (i <- 1 until coeffs.length) {
        zs(i) := zs(i - 1)
      }
    }

    val denominator = coeffs.reduce(_ + _)
    // Do the multiplies
    val products = VecInit.tabulate(coeffs.length)(i => Multiply(coeffs(i), denominator, zs(i)))

    // Sum up the products
    io.out.bits := products.reduce(_ + _)

  }

}*/
