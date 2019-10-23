package EffectBox

import chisel3._


class DelayFilter(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(bitWidth.W))
    val out = Output(SInt(bitWidth.W))
  })
  
  val delayFilter = Module(new FirFilter(bitWidth, Seq(1.S, 2.S, 1.S))).io
  delayFilter.in := io.in
  io.out := delayFilter.out
}