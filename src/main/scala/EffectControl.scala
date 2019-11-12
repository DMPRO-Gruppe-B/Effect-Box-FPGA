package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import io.{SPIBus, SPISlave}

class EffectControl extends MultiIOModule {
  val spi = IO(new SPIBus)
  val debug = IO(new Bundle {
    val slave_output = Output(UInt(16.W))
    val slave_output_valid = Output(Bool())
  })

  val slave = Module(new SPISlave)
  slave.io.spi <> spi

  val config = RegInit(VecInit(Seq.fill(2)(0x0.U(16.W))))

  when(slave.io.output_valid) {
    val bytes = slave.io.output
    val addr: UInt = bytes(23, 16)
    val data: UInt = bytes(15, 0)
    config(addr) := data
  }

  val bitcrush = IO(Flipped(new BitCrushControl))
  bitcrush.bypass := config(0) & 1.U(1.W)
  bitcrush.nCrushBits := config(1) & 0xF.U(4.W)

  val delay = IO(Flipped(new DelayControl))

  val fbFraction = Wire(new Fraction)
  fbFraction.denominator := 1.U
  fbFraction.numerator := 2.U

  val mixFraction = Wire(new Fraction)
  mixFraction.denominator := 1.U
  mixFraction.numerator := 2.U

  val sampled = Wire(UInt(16.W))
  sampled := 32000.U

  delay.fbFraction := fbFraction
  delay.mixFraction := mixFraction
  delay.delaySamples := sampled

  debug.slave_output := slave.io.output
  debug.slave_output_valid := slave.io.output_valid
}
