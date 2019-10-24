package EffectBox

import chisel3._


/**
  * A small test for the FPGA dev board
  */
class FPGATest extends Module {
  val io = IO(new Bundle {
    val pinout = Output(UInt(16.W))
    //val test = Output(Bool())
  })

  io.pinout := 0x5555.U
  //io.test := true.B
}
