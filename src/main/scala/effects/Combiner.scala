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

  val bitCrush = Module(new BitCrush)
  val delay = Module(new DelayFilter(bitWidth)).io
  delay.bypass := false.B
  bitCrush.ctrl.bypass := false.B
  bitCrush.ctrl.bitReduction := 4.U
  bitCrush.ctrl.rateReduction := 4.U

//  delay.in := io.in
//  bitCrush.dataIn := delay.out
//  io.out := bitCrush.dataOut

  bitCrush.io.in := io.in
  delay.in := bitCrush.io.out
  io.out := delay.out
}
