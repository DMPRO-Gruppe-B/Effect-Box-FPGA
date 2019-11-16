package EffectBox

import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule

// TODO: Update with bypass
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


  delayBuffer.delaySamples := ctrl.delaySamples

  // Get the feedback as output of buffer mixed with the input signal
  val feedback = Multiply(ctrl.fbFraction.numerator, ctrl.fbFraction.denominator, delayBuffer.out.bits) + OneMinusMultiply(ctrl.fbFraction.numerator, ctrl.fbFraction.denominator, io.in.bits)

  // Set io in with control signals, then set feedback to bits
  delayBuffer.in <> io.in
  delayBuffer.in.bits := feedback


  // Get the mix of input and output of buffer
  val mix = Multiply(ctrl.mixFraction.numerator, ctrl.mixFraction.denominator, delayBuffer.out.bits) + OneMinusMultiply(ctrl.mixFraction.numerator, ctrl.mixFraction.denominator, io.in.bits)


  // Set control signals, then send mix out
  delayBuffer.out <> io.out

  // Send how many delay samples
  when(ctrl.bypass) {
    io.out.bits := io.in.bits
  } .otherwise {
    io.out.bits := mix
  }

}
