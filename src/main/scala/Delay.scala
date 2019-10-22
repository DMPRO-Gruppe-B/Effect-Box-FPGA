package EffectBox

import chisel3._
import chisel3.util._

class Delay(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(bitWidth.W))
    val mixNum = Input(UInt(7.W))
    val mixDenom = Input(UInt(7.W))

    val fbNum = Input(UInt(7.W))
    val fbDenom  = Input(UInt(7.W))
    val emptyBuffer = Input(Bool())

    val out = Output(SInt(bitWidth.W))
  })
  val delayBuffer = Module(new DelayBuffer(bitWidth)).io
  
  val inDec = Wire(Flipped(Decoupled(SInt(bitWidth.W))))
  val outDec = Wire(Decoupled(SInt(bitWidth.W)))
  val delayedSignal = Wire(SInt(bitWidth.W))

  inDec.valid := true.B
  inDec.ready := true.B
  inDec.bits  := io.in+(delayedSignal*io.fbNum.asSInt)/io.fbDenom.asSInt

  io.out := 0.S
  delayedSignal := 0.S

  outDec.valid := true.B
  outDec.ready := false.B
  outDec.bits  := delayBuffer.out.bits

  delayBuffer.in <> inDec

  outDec <> delayBuffer.out

  when(io.emptyBuffer){
      outDec.ready := true.B
      delayedSignal := outDec.bits
  }

  //Output = delayedSignal*mix + cleanSignal*(1-mix)
  io.out := (delayedSignal*io.mixNum.asSInt)/io.mixDenom.asSInt + (io.in*(io.mixDenom-io.mixNum).asSInt)/io.mixDenom.asSInt
}

class DelayBuffer(bitWidth: Int) extends Module {
  
    val io = IO(new Bundle {
      val in = Flipped(Decoupled(SInt(bitWidth.W)))
      val out = Decoupled(SInt(bitWidth.W))
    })
    val queue = Module(new Queue(SInt(bitWidth.W),2000))
  
    queue.io.enq <> io.in
    io.out <> queue.io.deq
  }