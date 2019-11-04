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
  slave.reset := !reset.asBool()

  val config = RegInit(VecInit(Seq.fill(2)(0x0.U(16.W))))

  val bitCrush = Module(new BitCrush)
  bitCrush.ctrl.bypass := config(0) & 1.U(1.W)
  bitCrush.ctrl.nCrushBits := config(1) & 0xF.U(4.W)
  bitCrush.io.dataIn := 0.S

  val data = RegInit(0.U(16.W))

  io.pinout := 0.U
  io.led := slave.io.output(3, 0)

/*
  when(slave.io.output_valid) {
    val bytes = slave.io.output
    val addr: UInt = bytes(23, 16)
    val d: UInt = bytes(15, 0)
    config(addr) := d
    data := d
  }*/
}
