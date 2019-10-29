package EffectBox

import chisel3._


class SPITest extends Module {
  val io = IO(new Bundle {
    /*val rgbled_0 = Output(UInt(3.W))
    val rgbled_1 = Output(UInt(3.W))
    val rgbled_2 = Output(UInt(3.W))
    val rgbled_3 = Output(UInt(3.W))

    val led = Output(UInt(4.W))*/
    val pinout = Output(UInt(16.W))

    val spi_clk = Input(Bool())
    val spi_mosi = Input(Bool())
    val spi_cs_n = Input(Bool())
    val spi_miso = Output(Bool())
  })

  /*
  //io.rgbled_0 := 7.U
  io.rgbled_1 := 0.U
  io.rgbled_2 := 0.U
  io.rgbled_3 := 0.U
  */

  val effect_control = Module(new EffectControl)
  effect_control.spi.clk := io.spi_clk
  effect_control.spi.mosi := io.spi_mosi
  effect_control.spi.cs_n := io.spi_cs_n
  io.spi_miso := effect_control.spi.miso

  val bitCrush = Module(new BitCrush)
  //bitCrush.ctrl.bypass := effect_control.bitcrush.bypass
  //bitCrush.ctrl.nCrushBits := effect_control.bitcrush.nCrushBits
  bitCrush.ctrl <> effect_control.bitcrush
  bitCrush.io.dataIn := 0.S

  /*
  when(io.spi_cs_n) {
    io.rgbled_0 := 0.U
  }.otherwise {
    io.rgbled_0 := 1.U
  }

  //io.led := effect_control.bitcrush.nCrushBits
  io.led := (effect_control.debug.addr & 0xF.U)
  */
  io.pinout := effect_control.debug.addr
}
