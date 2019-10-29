package EffectBox

import blackboxes.BRAM
import chisel3._
import chisel3.util._

class Delay(maxDelaySamples: Int = 8192) extends Module {

  // Alternatively let constructor set maxDelaySamples
  val delay_bits = log2Ceil(maxDelaySamples)

  val io = IO(new Bundle {
    val reset_buffer = Input(Bool())
    val data_in      = Input(SInt(32.W))
    val sample_delay = Input(UInt(delay_bits.W))
    val fbFraction   = Input(new Fraction)
    val mixFraction  = Input(new Fraction)
    val write_enable = Input(Bool())
    val bypass       = Input(Bool())

    //val emptyBuffer  = Input(Bool())

    val data_out = Output(SInt(32.W))
  })

  // Initiate BRAM
  val delayBuffer = Module(new DelayBuffer(maxDelaySamples)).io

  // Register to check if in reset mode. Reset mode default at startup.
  var reset_in_progress = RegInit(true.B)

  // Set reset register if reset signal is true
  when(io.reset_buffer) {
    reset_in_progress = true.B
  }

  // Clear reset register if enough samples is in the buffer1
  when(delayBuffer.head === io.sample_delay) {
    reset_in_progress = false.B
  }

  // Wire up the easy stuff
  delayBuffer.write_enable := io.write_enable
  delayBuffer.sample_delay := io.sample_delay

  val fb_numerator = Wire(UInt(8.W))
  fb_numerator := io.fbFraction.numerator

  val mix_numerator = Wire(UInt(8.W))
  mix_numerator := io.mixFraction.numerator

  when (reset_in_progress) {
    fb_numerator  := 0.U(8.W)
    mix_numerator := 0.U(8.W)
  }

  // Get the feedback as output of buffer mixed with the input signal
  val feedback = Multiply(fb_numerator, io.fbFraction.denominator, delayBuffer.data_out) + OneMinusMultiply(fb_numerator, io.fbFraction.denominator, io.data_in)

  // Write feedback to buffer
  delayBuffer.data_in := feedback

  // Get the mix of input and output of buffer
  val mix = Multiply(mix_numerator, io.mixFraction.denominator, delayBuffer.data_out) + OneMinusMultiply(mix_numerator, io.mixFraction.denominator, io.data_in)

  // Send mix out whenever bypass is false
  when(io.bypass) {
    io.data_out := io.data_in
  } .otherwise {
    io.data_out := mix
  }



}

class DelayBuffer(maxSize : Int) extends Module {

  // Find the number of bits needed to represent the address.
  val delay_bits = log2Ceil(maxSize)

  // Let the buffer use some logic, hiding read and write address.
  val io = IO ( new Bundle {
    val sample_delay  = Input(UInt(delay_bits.W))
    // Use write enable only on correct ADC/DAC frequency:
    val write_enable  = Input(Bool())
    val data_in       = Input(SInt(32.W))

    val data_out      = Output(SInt(32.W))
    val head          = Output(UInt(delay_bits.W))
  })


  // Use bram black_box.
  val bram = Module(new BRAM(UInt(32.W), delay_bits)).io

  // Let head start at address 0.
  val head = RegInit(0.U(delay_bits.W))

  // Wire write_enable in accordance to module input.
  bram.write_enable := io.write_enable

  // Read delayed sample.
  bram.read_addr := head-io.sample_delay

  // Let write address be at head.
  bram.write_addr := head

  // If it is written to head, let head increment with wraparound.
  when (io.write_enable) {
    head := head + 1.U
  }

  // Wire data in and out between bram and this module
  bram.data_in := io.data_in.asUInt()
  io.data_out := bram.data_out.asSInt()

  // Wire head address out
  io.head := head

}