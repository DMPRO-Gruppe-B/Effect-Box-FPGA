package EffectBox

import chisel3._


class BitCrush extends Module {
  val io = IO(
    new Bundle {
      val dataIn      = Input(Bits(16.W))
      val nCrushBits  = Input(UInt(4.W)) // Represens all possible bitcrushes on 16 bits: 2^4 = 16
      val bypass      = Input(Bool()) 

      val dataOut     = Output(Bits(16.W))
    }
  )
  // class InputSplitter extends Bundle {
  //   val top = Uint(12.W)
  //   val bot = Uint(4.W)
  // }
  when (io.bypass) {
    io.dataOut := io.dataIn

  } .otherwise {
    // val mask = 0xffff.U << io.nCrushBits 
    val temp = VecInit(io.dataIn.toBools)
    for (ii <- 0 until 4) {
      temp(ii) := 0.U
    }
    io.dataOut := temp.asUInt
  }
}
