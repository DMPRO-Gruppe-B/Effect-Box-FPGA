package EffectBox

import chisel3._

class DACInterface extends Module {
  val io = IO(
    new Bundle {
      val BCLK = Input(Bool())
      val enable = Input(Bool())
      val sample = Input(UInt(16.W))

      val bit = Output(UInt(1.W))
    }
  )

  val sample_reg = RegInit(UInt(16.W), 0.U)
  val prev_bit = RegNext(io.bit)

  io.bit := prev_bit

  when(!io.BCLK) {
    io.bit := sample_reg(15)
    sample_reg := sample_reg << 1
    
    when(io.enable) {
      io.bit := io.sample(15)
      sample_reg := io.sample << 1
    }
  }
}