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

  val counter = RegInit(0.U(32.W))
  val sample = RegInit(0.S(32.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid

  val sampleDelay = ctrl.rateReduction

  when (io.in.valid) {
    when (counter >= sampleDelay || counter < 0.U) {
      counter := 0.U
      sample := io.in.bits
    } .otherwise {
      counter := counter + 1.U
    }
  }

  // Rightmost 0xD for soft transition
  val mask = 0xFFFFFFFDL.U(32.W) << ctrl.bitReduction

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
