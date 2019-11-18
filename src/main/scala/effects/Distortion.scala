package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.Decoupled

/*
 * Distortion effect: Clip amplitude to some chosen constant value.
 */

class DistortionControl extends Bundle {
  val bypass = Input(Bool())
  // 0-100
  val mix = Input(UInt(7.W))
  // 0-100
  val amplitude = Input(UInt(7.W))
}

class Distortion extends MultiIOModule {
  val MAX_POS_AMPLITUDE = 0x7fff.S(32.W)
  val MAX_NEG_AMPLITUDE = -0x8000.S(32.W)

  val io = IO(new EffectBundle)
  val ctrl = IO(new DistortionControl)

  io.in.ready := true.B
  io.out.valid := io.in.valid
  
  val sample = io.in.bits

  when (!ctrl.bypass) {
    var clippedSample = SInt(32.W)
    when (sample >= 0.S) {
      val maxSample = ctrl.amplitude * MAX_POS_AMPLITUDE / 100.S
      when (sample > maxSample) {
        clippedSample = maxSample
      } .otherwise {
        clippedSample = sample
      }
    } .otherwise {
      val maxSample = ctrl.amplitude * MAX_NEG_AMPLITUDE / 100.S
      when (sample < maxSample) {
        clippedSample = maxSample
      } .otherwise {
        clippedSample = sample
      }
    }
    io.out.bits := (clippedSample * ctrl.mix + sample * (100.U - ctrl.mix)) / 100.S
  } .otherwise {
    io.out.bits := io.in.bits
  }
}
