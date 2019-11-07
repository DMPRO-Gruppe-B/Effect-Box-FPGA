package EffectBox

import chisel3._

class Codec extends Module {
  val io = IO(
    new Bundle {
      val adc_in = Input(UInt(1.W))
      val dac_in  = Input(UInt(16.W))

      val BCLK    = Output(Bool())
      val LRCLK   = Output(Bool())
      val adc_out = Output(UInt(16.W))
      val dac_out = Output(UInt(1.W))
      val ready   = Output(Bool())
    }
  )

  val BCLK = RegNext(false.B)
  val LRCLK = RegNext(true.B)

  // Bør være 4.W, men whatever, tør ikke endre uten å teste
  val bit_count = RegNext(0.U(4.W))   // Every other clock cycle = bit index in sample from MSB
  
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
  enable := Mux(bit_count === 0.U, true.B, false.B)
  // when(bit_count === 0.U) { enable := true.B }.otherwise{ enable := false.B }

  adc.BCLK := BCLK
  adc.LRCLK := LRCLK
  adc.bit := io.adc_in

  dac.BCLK := BCLK
  dac.enable := enable
  dac.sample := io.dac_in

  io.dac_out := dac.bit
  io.adc_out := adc.sample
  io.ready := dac.ready


  io.BCLK := BCLK
  io.LRCLK := LRCLK

// Alternative method using a buffer between ADC and DAC should be unnecessary 
// val sample_buffer = RegInit(UInt(16.W), 0.U)

}