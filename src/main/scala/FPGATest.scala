package EffectBox

import chisel3._


/**
  * A small test for the FPGA dev board
  */
class FPGATest extends Module {
  val io = IO(new Bundle {
    val rgbled_0 = Output(UInt(3.W))
    val rgbled_1 = Output(UInt(3.W))
    val rgbled_2 = Output(UInt(3.W))
    val rgbled_3 = Output(UInt(3.W))
  })

  io.rgbled_0 := 7.U
  io.rgbled_1 := 1.U
  io.rgbled_2 := 2.U
  io.rgbled_3 := 4.U
}
