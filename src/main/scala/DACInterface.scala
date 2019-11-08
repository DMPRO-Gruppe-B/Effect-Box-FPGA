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

  val sample_reg = RegNext(0.U(16.W))
  val prev_bit = RegNext(io.bit)
  io.bit := prev_bit

  sample_reg := sample_reg

  when(!io.BCLK) {
    io.bit := sample_reg(15)

    // Need to use temp variable, because accumulator of type RegNext can't infer width when shifting
    // This should be changed to make the accumulator have explicit width
    val temp = Wire(UInt(16.W))
    temp := sample_reg << 1

    sample_reg := temp
    
    when(io.enable) {
      io.bit := io.sample(15)
      sample_reg := io.sample << 1
    }
  }
}
