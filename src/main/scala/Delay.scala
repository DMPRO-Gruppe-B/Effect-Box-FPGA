package EffectBox

import chisel3._
import chisel3.util._

class DelayControl extends Bundle {
  val fbFraction = Input(new Fraction)
  val mixFraction = Input(new Fraction)
  val emptyBuffer = Input(Bool())
}

class Delay() extends Module {

  val io = IO(new Bundle {
    val in          = Input(SInt(32.W))
    val out = Output(SInt(32.W))
  })
  val ctrl = IO(new DelayControl)
  val delayBuffer = Module(new DelayBuffer).io
  val inDec = Wire(Flipped(Decoupled(SInt(32.W))))
  val outDec = Wire(Decoupled(SInt(32.W)))
  val delayedSignal = Wire(SInt(32.W))

  inDec.valid  := true.B
  inDec.ready  := true.B
  inDec.bits   := WeightedSum(ctrl.fbFraction.numerator, ctrl.fbFraction.denominator, delayedSignal, io.in)

  outDec.valid := true.B
  outDec.ready := false.B
  outDec.bits  := delayBuffer.out.bits

  io.out := 0.S
  delayedSignal := 0.S

  delayBuffer.in <> inDec

  outDec <> delayBuffer.out

  when(ctrl.emptyBuffer){
      outDec.ready := true.B
      delayedSignal := outDec.bits
  }

  //Output = delayedSignal*mix + cleanSignal*(1-mix)
  io.out := WeightedSum(ctrl.mixFraction.numerator, ctrl.mixFraction.denominator, delayedSignal, io.in)
}

class DelayBuffer() extends Module {
  
    val io = IO(new Bundle {
      val in = Flipped(Decoupled(SInt(32.W)))
      val out = Decoupled(SInt(32.W))
    })
    val queue = Module(new Queue(SInt(32.W),2000))
  
    queue.io.enq <> io.in
    io.out <> queue.io.deq
  }