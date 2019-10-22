package EffectBox

import chisel3._


class ADCInterface extends Module {
  val io = IO(
    new Bundle {
      val bitIn      = Input(UInt(1.W))
      val BCLK        = Input(Bool())
      val LRCLK       = Input(Bool())

      val dataOut     = Output(SInt(16.W))
    }
  )
  io.dataOut := 0.S
  
}
