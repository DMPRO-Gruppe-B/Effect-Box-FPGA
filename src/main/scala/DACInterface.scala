package EffectBox

import chisel3._
import chisel3.util.{Counter}

class DACInterface extends Module {
  val io = IO(
    new Bundle {
      val BCLK = Input(Bool())  // DAC BCIN
      val LRCLK = Input(Bool()) // DAC LRCIN
      val sample = Input(UInt(16.W))

      val enable = Output(Bool())      
      val bit = Output(UInt(1.W)) // DAC DIN
    }
  )

  val sample_reg = RegInit(UInt(16.W), 0.U)

  io.enable := false.B

  when(io.BCLK) { // BCLK falling edge
    when(io.LRCLK) { // Left channel
      when(RisingEdge(io.LRCLK)) { // LRCLK rising edge
        io.enable := true.B
        sample_reg := io.sample
        io.bit := io.sample // bit left must be driven immediately, or previous sample bit will be used
      } // TODO: read in sample for right channel on LRCLK falling edge (must happen on cycle before falling edge)
      .otherwise {
          io.bit := sample_reg(15)
          sample_reg := sample_reg << 1
      }
    }.otherwise { // Right channel
      io.bit := sample_reg(15)
    }
  }
  .otherwise{
    io.bit := sample_reg(15)
  }
}
