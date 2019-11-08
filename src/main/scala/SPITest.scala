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
    val ledreg = RegInit(0.U(4.W))

    val slave = Module(new SPISlave)
    slave.io.spi <> io.spi

    val config = RegInit(VecInit(Seq.fill(2)(0x0.U(16.W))))

    when(slave.io.output_valid) {
      val bytes = slave.io.output
      val addr: UInt = bytes(23, 16)
      val data: UInt = bytes(15, 0)
      config(addr) := data

      ledreg := slave.io.output(3, 0)
    }

    val bitCrush = Module(new BitCrush)
    bitCrush.ctrl.bypass := config(0) & 1.U(1.W)
    bitCrush.ctrl.nCrushBits := config(1) & 0xF.U(4.W)
    bitCrush.io.dataIn := 0.S

    val d = slave.io.debug
    io.pinout := Vec(io.spi.cs_n, slave.io.output_valid, false.B, slave.io.output_valid, false.B, false.B, false.B, false.B, false.B, false.B, false.B, false.B, d(2), d(0), io.spi.cs_n, slave.io.output_valid).asUInt() // 0.U
    io.led := ledreg
  }
}
