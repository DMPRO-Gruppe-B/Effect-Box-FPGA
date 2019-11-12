package EffectBox

import chisel3._
import chisel3.util.Decoupled

class Codec extends Module {
  val io = IO(
    new Bundle {
      val BCLK    = Output(Bool())
      val LRCLK   = Output(Bool())

      val adc_in = Input(UInt(1.W))
      val dac_out = Output(UInt(1.W))

      val adc_sample = Decoupled(Sample())
      val dac_sample = Flipped(Decoupled(Sample()))
    }
  )

  val BCLK = RegNext(false.B)
  val LRCLK = RegNext(true.B)

  val dac_sample = Reg(UInt(16.W))

  // Bør være 4.W, men whatever, tør ikke endre uten å teste
  val bit_count = RegNext(0.U(6.W))   // Every other clock cycle = bit index in sample from MSB

  BCLK := !BCLK
  LRCLK := LRCLK
  bit_count := bit_count

  when(BCLK) {
    bit_count := bit_count + 1.U
    when(bit_count === 15.U) {
      LRCLK := !LRCLK
      bit_count := 0.U
    }
  }

  val adc = Module(new ADCInterface).io
  val dac = Module(new DACInterface).io

  val enable = Wire(Bool())
  enable := bit_count === 0.U

  adc.BCLK := BCLK
  adc.LRCLK := LRCLK
  adc.bit := io.adc_in

  dac.BCLK := BCLK
  dac.enable := enable
  dac.sample := 0.U
  io.dac_out := dac.bit

  io.BCLK := BCLK
  io.LRCLK := LRCLK

  io.adc_sample.bits := 0.S
  io.adc_sample.valid := false.B

  io.dac_sample.ready := true.B

  when (io.dac_sample.valid) {
    dac_sample := io.dac_sample.bits.asUInt()
  }

  when (enable) {
    io.adc_sample.bits := adc.sample.asSInt()
    io.adc_sample.valid := true.B

    dac.sample := dac_sample
  }
}
