package EffectBox

import chisel3._
import chisel3.util._
import chisel3.experimental.MultiIOModule


class DelayControl extends Bundle {
  val fbFraction  = Input(new Fraction)
  val mixFraction = Input(new Fraction)
  val delaySamples = Input(UInt(16.W))
}

class Delay() extends MultiIOModule {

  val io = IO(new EffectBundle)
  val ctrl = IO(new DelayControl)

  val delayBuffer = Module(new DelayBuffer).io

  delayBuffer.in <> io.in
  delayBuffer.out <> io.out
  delayBuffer.delaySamples := ctrl.delaySamples
}
