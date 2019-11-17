package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import io.{SPIBus, SPISlave}

class EffectControl extends MultiIOModule {
  val CONFIG_SIZE = 7

  val ADDR_BITCRUSH_ENABLE = 0
  val ADDR_BITCRUSH_BITS = 1
  val ADDR_BITCRUSH_RATE = 2

  val ADDR_TREMOLO_BYPASS = 5
  val ADDR_TREMOLO_PERIODMULT = 6

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
    val addr: UInt = bytes(31, 24)
    val data1 = bytes(23, 16)
    val data2 = bytes(15, 8)
    val checksum: UInt = bytes(7, 0)
    when(((addr + data1 + data2) & 0xFF.U) === checksum) {
      config(addr) := bytes(23, 8)
    }
  }

  /* Bitcrush */
  val bitcrush = IO(Flipped(new BitCrushControl))
  bitcrush.bypass := !(config(ADDR_BITCRUSH_ENABLE) & 1.U(1.W))
  bitcrush.bitReduction := config(ADDR_BITCRUSH_BITS) & 0xF.U(4.W)
  bitcrush.rateReduction := config(ADDR_BITCRUSH_RATE) & 0x3F.U(6.W)

  /* Tremolo */
  val tremolo = IO(Flipped(new TremoloControl))
  tremolo.bypass := config(ADDR_TREMOLO_BYPASS) //false.B
  tremolo.periodMultiplier := config(ADDR_TREMOLO_PERIODMULT) //18.U

  /* Delay */
  val delay = IO(Flipped(new DelayControl))

  val fbFraction = Wire(new Fraction)
  fbFraction.numerator := 1.U
  fbFraction.denominator := 3.U

  val mixFraction = Wire(new Fraction)
  mixFraction.numerator := 1.U
  mixFraction.denominator := 2.U

  val sampled = Wire(UInt(16.W))
  sampled := 9600.U

  delay.fbFraction := fbFraction
  delay.mixFraction := mixFraction
  delay.delaySamples := sampled
  delay.bypass :=  config(0) & 1.U(1.W)

  /* Debug */
  debug.slave_output := slave.io.output
  debug.slave_output_valid := slave.io.output_valid
}
