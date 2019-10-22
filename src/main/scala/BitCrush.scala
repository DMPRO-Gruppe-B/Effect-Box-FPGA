package EffectBox

import chisel3._


class BitCrush extends Module {
  val io = IO(
    new Bundle {
      val dataIn      = Input(SInt(16.W))
      val nCrushBits  = Input(UInt(4.W)) // Represens all possible bitcrushes on 16 bits: 2^4 = 16
      val bypass      = Input(Bool()) 

      val dataOut     = Output(SInt(16.W))
    }
  )
  when (io.bypass) {
    io.dataOut := io.dataIn
  } .otherwise {

    val mask = 0xffff.S << io.nCrushBits
    io.dataOut := io.dataIn & mask.toSInt
  }
}
