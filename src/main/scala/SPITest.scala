package EffectBox

import blackboxes._
import chisel3._
import chisel3.core.withReset


class SPITest extends Module {
  val io = IO(new Bundle {
    val led = Output(UInt(4.W))
    val pinout = Output(UInt(16.W))

    val spi = new SPIBus
  })

  withReset(!reset.asBool()) {

    val effectControl = Module(new EffectControl)
    effectControl.spi <> io.spi

    val bitCrush = Module(new BitCrush)
    bitCrush.ctrl <> effectControl.bitcrush
    bitCrush.io.dataIn := 0.S

    val ledreg = RegInit(0.U(4.W))
    when (effectControl.debug.slave_output_valid) {
      ledreg := effectControl.debug.slave_output(3, 0)
    }
    io.led := ledreg
    io.pinout := Vec(io.spi.cs_n, false.B, false.B, effectControl.debug.slave_output_valid).asUInt()
  }
}
