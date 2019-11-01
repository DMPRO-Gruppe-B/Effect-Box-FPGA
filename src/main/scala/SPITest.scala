package EffectBox

import blackboxes._
import chisel3._
import chisel3.util._


class SPITest extends Module {
  val io = IO(new Bundle {
    /*val rgbled_0 = Output(UInt(3.W))
    val rgbled_1 = Output(UInt(3.W))
    val rgbled_2 = Output(UInt(3.W))
    val rgbled_3 = Output(UInt(3.W))
    */

    val led = Output(UInt(4.W))
    val pinout = Output(UInt(16.W))

    val spi = new SPIBus
  })

  /*
  //io.rgbled_0 := 7.U
  io.rgbled_1 := 0.U
  io.rgbled_2 := 0.U
  io.rgbled_3 := 0.U
  */

  val slave = Module(new SPISlave)
  slave.io.spi <> io.spi

  val config = RegInit(VecInit(Seq.fill(2)(0xA.U(16.W))))

  val bitCrush = Module(new BitCrush)
  bitCrush.ctrl.bypass := config(0) & 1.U(1.W)
  bitCrush.ctrl.nCrushBits := config(1) & 0xF.U(4.W)
  bitCrush.io.dataIn := 0.S

  val addr = RegInit(0.U(8.W))
  val data = RegInit(0.U(16.W))
  val state = RegInit(0.U(4.W))

  //io.pinout := data// | 1.U
  io.pinout := slave.io.debug
  //io.led := state
  io.led := io.spi.cs_n.asUInt() | (io.spi.mosi << 1).asUInt() | (slave.io.output_valid << 2).asUInt() | (io.spi.clk << 3).asUInt()


  when(io.spi.cs_n) {
    state := 0.U
  }.otherwise {
    switch(state) {
      is(0.U) {
        when(slave.io.output_valid) {
          addr := slave.io.output
          state := 1.U
        }
      }
      is(1.U) {
        when(!slave.io.output_valid) {
          state := 2.U
        }
      }
      is(2.U) {
        when(slave.io.output_valid) {
          data := slave.io.output << 8
          state := 3.U
        }
      }
      is(3.U) {
        when(!slave.io.output_valid) {
          state := 4.U
        }
      }
      is(4.U) {
        when(slave.io.output_valid) {
          data := data | slave.io.output
          state := 5.U
        }
      }
      is(5.U) {
        when(!slave.io.output_valid) {
          state := 0.U
          //config(addr) := data // TODO wtf
        }
      }
    }
  }
}
