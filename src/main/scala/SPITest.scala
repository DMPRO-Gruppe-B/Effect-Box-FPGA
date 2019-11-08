package EffectBox

import chisel3._
import chisel3.core.withReset
import io.SPIBus


class SPITest extends Module {
  val io = IO(new Bundle {
    val pinout0 = Output(Bool())
    //val pinout1 = Output(Bool())
    //val pinout2 = Output(Bool())
    val pinout3 = Output(Bool())
    //val pinout4     = Output(UInt(1.W))
    //val pinout5     = Output(UInt(1.W))
    //val pinout6 = Output(UInt(1.W))
    //val pinout7 = Output(UInt(1.W))

    // LEDs
    val pinout12 = Output(UInt(1.W))
    val pinout13 = Output(UInt(1.W))
    val pinout14 = Output(UInt(1.W))
    //val pinout15 = Output(UInt(1.W))

    val spi = new SPIBus
  })

  withReset(!reset.asBool()) {

    val effectControl = Module(new EffectControl)
    effectControl.spi <> io.spi

    val bitCrush = Module(new BitCrush)
    bitCrush.ctrl <> effectControl.bitcrush
    bitCrush.io.dataIn := 0.S

    val ledreg = RegInit(0.U(4.W))
    when(effectControl.debug.slave_output_valid) {
      ledreg := effectControl.debug.slave_output(3, 0)
    }
    io.pinout12 := ledreg(0)
    io.pinout13 := ledreg(1)
    io.pinout14 := ledreg(2)
    //io.pinout15 := ledreg(3)
    io.pinout0 := io.spi.cs_n
    io.pinout3 := effectControl.debug.slave_output_valid
  }
}
