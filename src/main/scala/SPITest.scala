package EffectBox

import blackboxes.{SPIBus, SPISlaveReadonly}
import chisel3._
import chisel3.util._


class SPITest extends Module {
  val io = IO(new Bundle {
    /*val rgbled_0 = Output(UInt(3.W))
    val rgbled_1 = Output(UInt(3.W))
    val rgbled_2 = Output(UInt(3.W))
    val rgbled_3 = Output(UInt(3.W))

    val led = Output(UInt(4.W))*/
    val pinout = Output(UInt(16.W))

    val spi = new SPIBus
  })

  /*
  //io.rgbled_0 := 7.U
  io.rgbled_1 := 0.U
  io.rgbled_2 := 0.U
  io.rgbled_3 := 0.U
  */
  val slave = Module(new SPISlaveReadonly()).io
  slave.spi <> io.spi

  val config = RegInit(VecInit(Seq.fill(2)(0xA.U(16.W))))

  val bitCrush = Module(new BitCrush)
  bitCrush.ctrl.bypass := config(0) & 1.U(1.W)
  bitCrush.ctrl.nCrushBits := config(1) & 0xF.U(4.W)
  bitCrush.io.dataIn := 0.S

  val addr = RegInit(0.U(8.W))
  val data = RegInit(0.U(16.W))
  //val waiting :: hasReadAddr :: hasReadTwoBytes :: yeet = Enum(4)
  val state = RegInit(0.U(2.W))
  io.pinout := addr

  when(io.spi.cs_n) {
    state := 0.U
  }.otherwise {
    switch(state) {
      is(0.U) {
        when(slave.data_valid) {
          addr := slave.recv_data
          state := 1.U
        }
      }
      is(1.U) {
        when(slave.data_valid) {
          data := slave.recv_data << 8
          state := 2.U
        }
      }
      is(2.U) {
        when(slave.data_valid) {
          data := data | slave.recv_data
          state := 0.U
          //config(addr) := data // TODO wtf
        }
      }
    }
  }

  /*
  when(io.spi_cs_n) {
    io.rgbled_0 := 0.U
  }.otherwise {
    io.rgbled_0 := 1.U
  }

  //io.led := effect_control.bitcrush.nCrushBits
  io.led := (effect_control.debug.addr & 0xF.U)
  */

}
