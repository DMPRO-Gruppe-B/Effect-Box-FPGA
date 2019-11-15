package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import io.{SPIBus, SPISlave}

class EffectControl extends MultiIOModule {
  val CONFIG_SIZE = 5

  val ADDR_BITCRUSH_BYPASS = 0
  val ADDR_BITCRUSH_BITS = 1
  val ADDR_BITCRUSH_RATE = 2

  val ADDR_TREMOLO_BYPASS = 3
  val ADDR_TREMOLO_PERIODMULT = 4

  val spi = IO(new SPIBus)
  val debug = IO(new Bundle {
    val slave_output = Output(UInt(16.W))
    val slave_output_valid = Output(Bool())
  })

  val slave = Module(new SPISlave)
  slave.io.spi <> spi

  val config = RegInit(VecInit(Seq.fill(CONFIG_SIZE)(0x0.U(16.W))))

  when(slave.io.output_valid) {
    val bytes = slave.io.output
    val addr: UInt = bytes(23, 16)
    val data: UInt = bytes(15, 0)
    config(addr) := data
  }

  val bitcrush = IO(Flipped(new BitCrushControl))
  bitcrush.bypass := config(ADDR_BITCRUSH_BYPASS) & 1.U(1.W)
  bitcrush.bitReduction := config(ADDR_BITCRUSH_BITS) & 0xF.U(4.W)
  bitcrush.rateReduction := config(ADDR_BITCRUSH_RATE) & 0xF.U(4.W)

  val tremolo = IO(Flipped(new TremoloControl))
  tremolo.bypass := config(ADDR_TREMOLO_BYPASS) //false.B
  tremolo.periodMultiplier := config(ADDR_TREMOLO_PERIODMULT) //18.U

  debug.slave_output := slave.io.output
  debug.slave_output_valid := slave.io.output_valid
}
