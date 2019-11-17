package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

class TremoloControl extends Bundle {
  val periodMultiplier = Input(UInt(16.W))
  val bypass = Input(Bool())
  val depth = Input(new Fraction)
}

class Tremolo extends MultiIOModule {

  val io = IO(new EffectBundle)
  val ctrl = IO(new TremoloControl)

  val sine = Module(new SineWave).io
  val counter = Reg(UInt(16.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid


  when(ctrl.bypass) {
    io.out.bits := io.in.bits
    sine.inc := false.B

  }.otherwise {
    when(io.in.valid) {

      when(counter >= ctrl.periodMultiplier - 1.U || counter < 0.U) {
        counter := 0.U
        sine.inc := true.B
      }.otherwise {
        counter := counter + 1.U
        sine.inc := false.B
      }

    }.otherwise {
      sine.inc := false.B
    }

    val denominator = WireInit(UInt(8.W), (sine.signal.denominator >> 8).asUInt())
    val numerator = WireInit(SInt(9.W), (sine.signal.numerator >> 8).asSInt())
    val res = Wire(SInt(32.W))

    // shift to move sine wave above y-axis, equivalent to 0.5 sin(x) + 0.5
    //    res := ctrl.depth.numerator * io.in.bits * (numerator.asSInt() + denominator.asSInt()) /
    //      (ctrl.depth.denominator * (denominator << 1).asSInt()) + io.in.bits * (ctrl.depth.denominator - ctrl.depth.numerator) / ctrl.depth.denominator.asSInt()

//    res := io.in.bits * numerator * ctrl.depth.numerator / (denominator * ctrl.depth.denominator).asSInt() +
//     io.in.bits * (ctrl.depth.denominator - ctrl.depth.numerator) / ctrl.depth.denominator.asSInt()

    res := io.in.bits * ( (numerator * ctrl.depth.numerator).asSInt() + (ctrl.depth.denominator - ctrl.depth.numerator).asSInt() * denominator) /
      (denominator * ctrl.depth.denominator).asSInt()

    // a* r* (n + d) / (b * 2d) + r (b - a) / b
    // (r *  (n * t + (b - t) * d)   ) / (d * b)
    //    when (!ctrl.bypass) {
    //
    //    }.otherwise {
    //      res := io.in.bits * (numerator.asSInt() + 3.S * denominator.asSInt()) / (denominator << 2).asSInt()
    //
    //    }

    io.out.bits := res

  }
}
