package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import io.{SPIBus, SPISlave}

class EffectControl extends MultiIOModule {
  val CONFIG_SIZE = 15

  val ADDR_BITCRUSH_BYPASS = 0
  val ADDR_BITCRUSH_DISTORTION = 13
  val ADDR_BITCRUSH_BITS = 1
  val ADDR_BITCRUSH_RATE = 2

  val ADDR_DELAY_BYPASS = 3
  val ADDR_DELAY_MILLISECONDS = 4
  val ADDR_DELAY_FEEDBACK = 7
  val ADDR_DELAY_MIX = 8

  val ADDR_TREMOLO_BYPASS = 5
  val ADDR_TREMOLO_PERIODMULT = 6
  val ADDR_TREMOLO_DEPTH = 9
  val ADDR_TREMOLO_WAVE = 14

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
    when(((addr + data1 + data2) & 0xFF.U) === ~checksum) {
      config(addr) := bytes(23, 8)
    }
  }

  /* Distortion */
  //val distortion = IO(Flipped(new DistortionControl))
  // Same bypass group as bitcrush
  //distortion.bypass := (config(ADDR_BITCRUSH_BYPASS) & 1.U(1.W))
  //distortion.distortion := config(ADDR_DISTORTION_DISTORTION) & 0xF.U(4.W)

  /* Bitcrush */
  val bitcrush = IO(Flipped(new BitCrushControl))
  bitcrush.bypass := (config(ADDR_BITCRUSH_BYPASS) & 1.U(1.W))
  bitcrush.distortion := config(ADDR_BITCRUSH_DISTORTION) & 0xF.U(4.W)
  bitcrush.bitReduction := config(ADDR_BITCRUSH_BITS) & 0xF.U(4.W)
  bitcrush.rateReduction := config(ADDR_BITCRUSH_RATE) & 0x3F.U(6.W)

  /* Tremolo */
  val tremolo = IO(Flipped(new TremoloControl))
  tremolo.bypass := (config(ADDR_TREMOLO_BYPASS) & 1.U(1.W))
  tremolo.periodMultiplier := config(ADDR_TREMOLO_PERIODMULT) //18.U
  tremolo.depth := config(ADDR_TREMOLO_DEPTH)
  tremolo.waveSelect := config(ADDR_TREMOLO_WAVE) // & 0x3.U(2.W)

  /* Delay */
  val delay = IO(Flipped(new DelayControl))

  delay.bypass := (config(ADDR_DELAY_BYPASS) & 1.U(1.W))
  delay.delaySamples := config(ADDR_DELAY_MILLISECONDS) * 64.U

  // Feedback and mix is sent as 0-10, representing 0-100%
  delay.fbFraction.numerator := config(ADDR_DELAY_FEEDBACK)
  delay.fbFraction.denominator := 10.U
  delay.mixFraction.numerator := config(ADDR_DELAY_MIX)
  delay.mixFraction.denominator := 10.U

  /* Debug */
  debug.slave_output := slave.io.output
  debug.slave_output_valid := slave.io.output_valid
}
