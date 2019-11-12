package EffectBox

import chisel3._
import chisel3.util._

class Delay(val addr_width: Int) extends Module {

  val io = IO(new Bundle {
    val in          = Input(SInt(32.W))
    val fbFraction  = Input(new Fraction)
    val mixFraction = Input(new Fraction)
    val delaySamples = Input(UInt(16.W))

    val out = Output(SInt(32.W))
  })
  val delayBuffer = Module(new DelayBuffer(addr_width)).io
  val delayedSignal = Wire(SInt(32.W))

  delayBuffer.in := InverseMultiply(io.fbFraction.numerator, io.fbFraction.denominator, delayedSignal, io.in)
  delayBuffer.delaySamples := io.delaySamples
  delayedSignal := delayBuffer.out

  //Output = delayedSignal*mix + cleanSignal*(1-mix)
  io.out := InverseMultiply(io.mixFraction.numerator, io.mixFraction.denominator, delayedSignal, io.in)
}

