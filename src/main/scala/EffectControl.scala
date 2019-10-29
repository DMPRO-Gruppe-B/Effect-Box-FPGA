package EffectBox

import blackboxes.SPISlaveReadonly
import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

class BitCrushControl extends Bundle {
  val nCrushBits = Input(UInt(4.W))
  val bypass = Input(Bool())
}

class EffectControl extends MultiIOModule {
  val spi = IO(new Bundle {
    val mosi = Input(Bool())
    val clk = Input(Bool())
    val cs_n = Input(Bool())
    val miso = Output(Bool())
  })
  val debug = IO(new Bundle {
    val addr = Output(UInt(8.W))
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

  val addr = RegInit(0.U(8.W))
  val data = RegInit(0.U(16.W))
  val waiting :: hasReadAddr :: hasReadTwoBytes :: yeet = Enum(4)
  val state = RegInit(waiting)
  debug.addr := addr

  when(slave.spi_cs_n) {
    state := waiting
  }.otherwise {
    switch(state) {
      is(waiting) {
        when(slave.data_valid) {
          addr := slave.recv_data
          state := hasReadAddr
        }
      }
      is(hasReadAddr) {
        when(slave.data_valid) {
          data := slave.recv_data << 8
          state := hasReadTwoBytes
        }
      }
      is(hasReadTwoBytes) {
        when(slave.data_valid) {
          data := data | slave.recv_data
          state := waiting
          //config(addr) := data // TODO wtf
        }
      }
    }
  }
}
