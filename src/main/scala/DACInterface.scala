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

  io.enable := false.B


  // Disable right channel (for now)
  io.bit_right := 0.U

  io.enable := false.B

  when(io.LRCLK) { // LRCLK high
    when(RisingEdge(io.LRCLK)) { // LRCLK rising edge
      io.enable := true.B
      sample_reg := io.sample.do_asUInt
      io.bit_left := io.sample.do_asUInt // bit left must be driven immediately, or previous sample bit will be used
    } // TODO: read in sample for right channel on LRCLK falling edge (must happen on cycle before falling edge)
    .otherwise {
        io.bit_left := sample_reg(15)
        sample_reg := sample_reg << 1
    }
  }.otherwise {
    io.bit_left := sample_reg(15)
  }
}
