package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.Decoupled

/*
 * Distortion effect: Clip amplitude to some chosen constant value.
 */

class DistortionControl extends Bundle {
  // 0-10
  val amplitude = Input(UInt(4.W))
}

class Distortion extends MultiIOModule {
  val MAX_POS_AMPLITUDE = 0x7fffL.S(32.W)
  val MAX_NEG_AMPLITUDE = -0x8000L.S(32.W)

  val io = IO(new EffectBundle)
  val ctrl = IO(new DistortionControl)

  io.in.ready := true.B
  io.out.valid := io.in.valid
  
  val sample = io.in.bits
  when (sample >= 0.S) {
    val maxSample = Multiply(ctrl.amplitude, 10.U, MAX_POS_AMPLITUDE)
    when (sample > maxSample) {
      io.out.bits := maxSample
    } .otherwise {
      io.out.bits := sample
    }
  } .otherwise {
    val maxSample = Multiply(ctrl.amplitude, 10.U, MAX_NEG_AMPLITUDE)
    when (sample < maxSample) {
      io.out.bits := maxSample
    } .otherwise {
      io.out.bits := sample
    }
  }
}
