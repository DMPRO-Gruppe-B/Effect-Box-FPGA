package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.Decoupled

/*
 * Bitcrush effect: Reduce quality by reducing bit depth or sample rate.
 */

class BitCrushControl extends Bundle {
  // 0-10
  val mix = Input(UInt(4.W))
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

  when (io.in.valid) {
    when (counter >= ctrl.rateReduction) {
      counter := 1.U
      sample := io.in.bits
    } .otherwise {
      counter := counter + 1.U
    }
  }

  val mask = 0xffffffffL.U(32.W) << ctrl.bitReduction
  var truncatedSample = SInt(32.W)
  // Truncate towards zero from both sides
  when (sample >= 0.S) {
    truncatedSample = (sample.asUInt() & mask).asSInt()
  } .otherwise {
    truncatedSample = (sample.asUInt() | ~mask).asSInt()
  }
  io.out.bits := (truncatedSample * ctrl.mix + sample * (10.U - ctrl.mix)) / 10.S
}
