package EffectBox

import chisel3._

class ADCInterface extends Module {
  val io = IO(
    new Bundle {
      val BCLK = Input(Bool())
      val LRCLK = Input(Bool())
      val bit = Input(UInt(1.W))

      val sample = Output(UInt(16.W))
    }
  )

  val accumulator = RegInit(UInt(16.W), 0.U)

  io.sample := accumulator

  when(io.LRCLK && io.BCLK) {
    accumulator := (accumulator << 1) + io.bit
  }
}
