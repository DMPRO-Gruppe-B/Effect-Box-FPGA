package EffectBox

import chisel3._


class Combiner(bitWidth: Int) extends Module {
  val io = IO(
    new Bundle {
      val in = Input(SInt(bitWidth.W))
      val out = Output(SInt(bitWidth.W))
      val n = Input(Bool()) // does nothing
    }
  )
  
  val bitCrush = Module(new BitCrush).io
  val delay = Module(new DelayFilter(bitWidth)).io
  bitCrush.bypass := false.B
  bitCrush.nCrushBits := 4.U

//  delay.in := io.in
//  bitCrush.dataIn := delay.out
//  io.out := bitCrush.dataOut

  bitCrush.dataIn := io.in
  delay.in := bitCrush.dataOut
  io.out := delay.out
}
