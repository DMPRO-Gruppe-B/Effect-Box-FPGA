package EffectBox

import chisel3._


class DelayFilter(bitWidth: Int) extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(bitWidth.W))
    val out = Output(UInt(bitWidth.W))
  })
  
  val delayFilter = Module(new FirFilter(bitWidth, Seq(0.U, 1.U))).io
  delayFilter.in := io.in
  io.out := delayFilter.out
}