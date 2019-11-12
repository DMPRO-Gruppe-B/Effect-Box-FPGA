package EffectBox

import chisel3._
import chisel3.util._

class DelayControl extends Bundle {
  val fbFraction  = Input(new Fraction)
  val mixFraction = Input(new Fraction)
  val delaySamples = Input(UInt(16.W))
}

class Delay(val addr_width: Int) extends Module {

  val io = IO(new EffectBundle)
  val ctrl = IO(new DelayControl)

  val delayBuffer = Module(new DelayBuffer(addr_width)).io
  val delayedSignal = Wire(SInt(32.W))

  
  delayBuffer.in := InverseMultiply(ctrl.fbFraction, delayedSignal, io.in.bits)
  delayBuffer.delaySamples := ctrl.delaySamples
  delayBuffer.write_enable := io.in.valid
  delayedSignal := delayBuffer.out

  //Output = delayedSignal*mix + cleanSignal*(1-mix)
  io.out := InverseMultiply(ctrl.mixFraction, delayedSignal, io.in.bits)
}

