package EffectBox
import chisel3.experimental._
import chisel3._

  class BitCrushControl extends Bundle {
    val nCrushBits = UInt(4.W)  // Represens all possible bitcrushes on 16 bits: 2^4 = 16
    val bypass = Bool()
  }

class BitCrush extends MultiIOModule {
  val io = IO(
    new Bundle {
      val dataIn      = Input(SInt(16.W))
      val dataOut     = Output(SInt(16.W))
    }
  )
  val ctrl = IO(Input(new BitCrushControl))
  when (ctrl.bypass) {
    io.dataOut := io.dataIn
  } .otherwise {

    val mask = 0xffff.S << ctrl.nCrushBits
    io.dataOut := io.dataIn & mask.toSInt
  }
}
