package EffectBox

import chisel3._
import chisel3.util._

class CombFilter() extends Module {

  val io = IO(new Bundle {
    val in          = Input(SInt(32.W))
    val gainFraction        = Input(new Fraction)
    val emptyBuffer = Input(Bool())

    val out = Output(SInt(32.W))
  })
  val delayBuffer = Module(new DelayBuffer).io
  val inDec = Wire(Flipped(Decoupled(SInt(32.W))))
  val outDec = Wire(Decoupled(SInt(32.W)))
  val delayedSignal = Wire(SInt(32.W))

  inDec.valid  := true.B
  inDec.ready  := true.B
  inDec.bits   := WeightedSum(io.gainFraction.numerator, io.gainFraction.denominator, delayedSignal, io.in)

  outDec.valid := true.B
  outDec.ready := false.B
  outDec.bits  := delayBuffer.out.bits

  io.out := 0.S
  delayedSignal := 0.S

  delayBuffer.in <> inDec

  outDec <> delayBuffer.out

  when(io.emptyBuffer){
      outDec.ready := true.B
      delayedSignal := outDec.bits
  }

  //Output = delayedSignal*mix + cleanSignal*(1-mix)
  //io.out := WeightedSum(io.mixFraction.numerator, io.mixFraction.denominator, delayedSignal, io.in)
  io.out := 0.S
}