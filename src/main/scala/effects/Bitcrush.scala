package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.Decoupled

/*
 * Bitcrush effect: Reduce quality by reducing bit depth or sample rate.
 */

class BitCrushControl extends Bundle {
  val bypass = Input(Bool())
  val bitReduction = Input(UInt(4.W))
  val rateReduction = Input(UInt(6.W))
}

class BitCrush extends MultiIOModule {
  val io = IO(new EffectBundle)
  val ctrl = IO(new BitCrushControl)

  val counter = Reg(UInt(32.W))
  val sample = Reg(SInt(32.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid

  when (io.in.valid) {
    when (counter >= ctrl.rateReduction) {
      counter := 1.U
      sample := io.in.bits
    } .otherwise {
      counter := counter + 1.U
    }
  }

  val mask = 0xffffffffL.U(32.W) << ctrl.bitReduction

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
