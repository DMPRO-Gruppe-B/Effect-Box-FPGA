package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.Decoupled

/*
 * Bitcrush effect: Reduce quality by reducing bit depth or sample rate.
 */

class BitCrushControl extends Bundle {
  val bypass = Input(Bool())
  val distortion = Input(UInt(4.W))
  val bitReduction = Input(UInt(4.W))
  val rateReduction = Input(UInt(6.W))
}

class BitCrush extends MultiIOModule {
  val io = IO(new EffectBundle)
  val ctrl = IO(new BitCrushControl)

  io.in.ready := true.B
  io.out.valid := io.in.valid

  // Rate reduction
  val counter = Reg(UInt(32.W))
  val sample = Reg(SInt(32.W))
  when (io.in.valid) {
    when (counter >= ctrl.rateReduction) {
      counter := 1.U
      sample := io.in.bits
    } .otherwise {
      counter := counter + 1.U
    }
  }

  // Distortion and depth reduction
  val distortionLimit = 0xffffL.S(32.W) >> ctrl.distortion
  val depthMask = 0xffffL.U(32.W) << ctrl.bitReduction
  val crushedSample = Wire(SInt(32.W))
  when (sample >= 0.S) {
    val distortedSample = Mux(sample > distortionLimit, distortionLimit, sample)
    crushedSample := (distortedSample.asUInt() & depthMask).asSInt()
  } .otherwise {
    val distortedSample = Mux(sample < -distortionLimit, -distortionLimit, sample)
    crushedSample := (distortedSample.asUInt() | ~depthMask).asSInt()
  }

  when (ctrl.bypass) {
    io.out <> io.in
  } .otherwise {
    io.out.bits := crushedSample
  }
}
