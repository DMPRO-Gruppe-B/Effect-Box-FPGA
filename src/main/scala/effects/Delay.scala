package EffectBox

import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class DelayControl extends Bundle {
  val fbFraction  = Input(new Fraction)
  val mixFraction = Input(new Fraction)
  val delaySamples = Input(UInt(16.W))
  val bypass = Input(Bool())
}

class Delay() extends MultiIOModule {
  val io = IO(new EffectBundle)
  val ctrl = IO(new DelayControl)

  val delayBuffer = Module(new DelayBuffer).io

  // Get the feedback as output of buffer mixed with the input signal
  val feedback = (Multiply(ctrl.fbFraction.numerator, ctrl.fbFraction.denominator, delayBuffer.out)
    + OneMinusMultiply(ctrl.fbFraction.numerator, ctrl.fbFraction.denominator, io.in.bits))

  // Get the mix of input and output of buffer
  val mix = (Multiply(ctrl.mixFraction.numerator, ctrl.mixFraction.denominator, delayBuffer.out)
    + OneMinusMultiply(ctrl.mixFraction.numerator, ctrl.mixFraction.denominator, io.in.bits))

  // Write samples to BRAM, even when the delay is bypassed
  delayBuffer.enable       := io.in.valid && io.in.ready
  delayBuffer.delaySamples := ctrl.delaySamples

  io.in.ready  := true.B
  io.out.valid := io.in.valid

  when(ctrl.bypass) {
    io.out <> io.in
    delayBuffer.in := io.in.bits
  }.otherwise {
    delayBuffer.in := feedback
    io.out.bits    := mix
  }
}
