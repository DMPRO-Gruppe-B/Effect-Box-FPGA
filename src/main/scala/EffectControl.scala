package EffectBox

import blackboxes.SPISlaveReadonly
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.{Enum, switch}

class BitCrushControl extends Bundle {
  val nCrushBits  = Input(UInt(4.W))
  val bypass      = Input(Bool())
}

class EffectControl extends MultiIOModule {
  val spi = IO(new Bundle {
    val mosi = Input(Bool())
    val clk  = Input(Bool())
    val cs_n = Input(Bool())
    val miso = Output(Bool())
  })

  val slave = Module(new SPISlaveReadonly()).io
  slave.spi_clk := spi.clk
  slave.spi_mosi := spi.mosi
  slave.spi_cs_n := spi.cs_n
  spi.miso := slave.spi_miso

  val config = RegInit(VecInit(Seq.fill(2)(0xA.U(16.W))))

  val bitcrush = IO(Flipped(new BitCrushControl))
  bitcrush.bypass := config(0) & 1.U(1.W)
  bitcrush.nCrushBits := config(1) & 0xF.U(4.W)

  val waiting :: readByte :: readTwoBytes :: finished = Enum(4)
  val state = RegInit(waiting)


}
