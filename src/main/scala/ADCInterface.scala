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

  val accumulator = RegNext(0.U(16.W))
  accumulator := accumulator

  io.sample := accumulator

  when(io.LRCLK && io.BCLK) {
    val temp = Wire(UInt(16.W))
    temp := accumulator << 1
    accumulator := temp + io.bit
  }
  
}
