package EffectBox

import chisel3._

class Codec extends Module {
  val io = IO(
    new Bundle {
      val BCLK = Input(Bool())
      val LRCLK = Input(Bool())
      val bit_count = Input(UInt(6.W))

      val adc_in = Input(UInt(1.W))
      val dac_out = Input(UInt(1.W))
    }
  )

  val adc = Module(new ADCInterface).io
  val dac = Module(new DACInterface).io

  val enable = Wire(Bool())
  enable := false.B

  adc.BCLK := io.BCLK
  adc.LRCLK := io.LRCLK
  adc.bit := io.adc_in

  dac.BCLK := io.BCLK
  dac.enable := enable
  io.dac_out := dac.bit

  dac.sample := adc.sample

  // Load sample into DAC
  when(io.bit_count === 0.U) {
    enable := true.B
  }

// Alternative method using a buffer between ADC and DAC should be unnecessary 
// val sample_buffer = RegInit(UInt(16.W), 0.U)

}