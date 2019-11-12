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
  //val delayedSignal = Wire(SInt(16.W))

  io.in.ready := true.B
  delayBuffer.in <> io.in
  delayBuffer.delaySamples := ctrl.delaySamples
  //delayedSignal := delayBuffer.out

  io.out.valid := io.in.valid

  //Output = delayedSignal*mix + cleanSignal*(1-mix)
  //io.out.bits := InverseMultiply(ctrl.mixFraction, delayedSignal, io.in.bits)
  io.out.bits := delayBuffer.out
}

