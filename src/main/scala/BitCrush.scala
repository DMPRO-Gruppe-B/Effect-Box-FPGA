package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule


class BitCrush extends MultiIOModule {
  val io = IO(
    new Bundle {
      val dataIn      = Input(SInt(16.W))
      val dataOut     = Output(SInt(16.W))
    }
  )
  val ctrl = IO(new BitCrushControl)

  when (ctrl.bypass) {
    io.dataOut := io.dataIn
  } .otherwise {

    val mask = 0xffff.S << ctrl.nCrushBits
    io.dataOut := io.dataIn & mask.toSInt
  }
}
