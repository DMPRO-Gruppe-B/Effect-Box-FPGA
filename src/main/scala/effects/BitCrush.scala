package EffectBox

import chisel3._
import chisel3.MultiIOModule

class BitCrushControl extends Bundle {
  val bypass = Input(Bool())
  val bitReduction = Input(UInt(4.W))
  val rateReduction = Input(UInt(4.W))
}

class BitCrush extends MultiIOModule {
  val RATE_RED_MULT = 500.U

  val io = IO(new EffectBundle)
  val ctrl = IO(new BitCrushControl)

  val counter = Reg(UInt(32.W))
  val sample = Reg(SInt(32.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid

  // Rightmost 0xD for soft transition
  val mask = 0xFFFFFFFDL.U(32.W) << ctrl.bitReduction

  when (counter >= ctrl.rateReduction * RATE_RED_MULT || counter < 0.U) {
    counter := 0.U
    sample := io.in.bits
  } .otherwise {
    counter := counter + 1.U
  }

  when (!ctrl.bypass) {
    // Truncate towards zero from both sides
    when (sample >= 0.S) {
      io.out.bits := (sample.asUInt() & mask).asSInt()
    } .otherwise {
      io.out.bits := (sample.asUInt() | ~mask).asSInt()
    }
  } .otherwise {
    io.out.bits := io.in.bits
  }
}
