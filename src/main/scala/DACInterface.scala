package EffectBox

import chisel3._
import chisel3.util.{Counter}

class DACInterface extends Module {
  val io = IO(
    new Bundle {
      val LRCLK = Input(Bool())   // DAC LRCIN (from FPGA)
      val sample = Input(SInt(16.W))

      val enable = Output(Bool())      
      val bit_left = Output(UInt(1.W)) // DAC DIN L
      val bit_right = Output(UInt(1.W)) // DAC DIN R
    }
  )

  val sample_reg = RegInit(UInt(16.W), 0.U)
  val temp = Wire(UInt(16.W))
  temp := 0.U

  sample_reg := io.sample.do_asUInt
  io.enable := false.B

  // Disable right channel (for now)
  io.bit_left := 0.U
  io.bit_right := 0.U

  when(io.LRCLK) { // LRCLK high
    when(!RegNext(io.LRCLK)) { // LRCLK rising edge
      // Enable sample read (sending module should write next sample to io.sample)
      io.enable := true.B
      io.bit_left := io.sample(15)
    }
    io.bit_left := sample_reg(15)
    // printf("%d, %d\n", io.sample, io.sample(15))
    printf("%d\n", sample_reg(15))
    temp := sample_reg << 1
    sample_reg := temp
  }
}
