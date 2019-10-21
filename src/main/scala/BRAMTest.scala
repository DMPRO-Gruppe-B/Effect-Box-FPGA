package EffectBox

import chisel3._
import chisel3.util._
import blackboxes.BRAM


/**
  * A test for using block ram
  */
class BRAMTest extends Module {
  val io = IO(new Bundle {
    val btn      = Input(UInt(4.W))
    val sw       = Input(UInt(4.W))
    val rgbled_0 = Output(UInt(3.W))
    //val rgbled_1 = Output(UInt(3.W))
    //val rgbled_2 = Output(UInt(3.W))
    //val rgbled_3 = Output(UInt(3.W))
  })

  val bram = Module(new BRAM(UInt(32.W), 9)).io

  val addr = 0x155.U + io.sw(2) + (io.sw(3) << 1.U)
  val value = io.sw(0) | (io.sw(1) << 1.U)

  bram.data_in := value
  bram.write_enable := io.btn(0)
  bram.read_addr := addr
  bram.write_addr := addr

  io.rgbled_0 := bram.data_out
}
