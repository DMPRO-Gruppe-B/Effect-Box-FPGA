package EffectBox

import blackboxes.BRAM
import chisel3._
import chisel3.util._

class Delay() extends Module {

  // Alternatively let constructor set maxDelaySamples
  val maxDelaySamples = 4096.U
  val delay_bits = log2Ceil(Int(maxDelaySamples))

  val io = IO(new Bundle {
    val data_in      = Input(SInt(32.W))
    val sample_delay = Input(UInt(delay_bits.W))
    val fbFraction   = Input(new Fraction)
    val mixFraction  = Input(new Fraction)
    val write_enable = Input(Bool())
    val bypass       = Input(Bool())

    //val emptyBuffer  = Input(Bool())

    val data_out = Output(SInt(32.W))
  })

  val delayBuffer = Module(new DelayBuffer(maxDelaySamples)).io

  // Wire up the easy stuff
  delayBuffer.write_enable := io.write_enable
  delayBuffer.sample_delay := io.sample_delay

  // Get the feedback as output of buffer mixed with the input signal
  val feedback = Multiply(io.fbFraction.numerator, io.fbFraction.denominator, delayBuffer.data_out) + OneMinusMultiply(io.fbFraction.numerator, io.fbFraction.denominator, io.data_in)

  // Write feedback to buffer
  delayBuffer.data_in := feedback

  // Get the mix of input and output of buffer
  val mix = Multiply(io.mixFraction.numerator, io.mixFraction.denominator, delayBuffer.data_out) + OneMinusMultiply(io.mixFraction.numerator, io.mixFraction.denominator, io.data_in)

  // Send mix out whenever bypass is false
  when(io.bypass) {
    io.data_out := io.data_in
  } .otherwise {
    io.data_out := mix
  }

}

// TODO: Delay-check is necessary?

class DelayBuffer(maxSize : UInt) extends Module {

  // Find the number of bits needed to represent the address.
  val delay_bits = log2Ceil(Int(maxSize))

  // Let the buffer use some logic, hiding read and write address.
  val io = IO ( new Bundle {
    val sample_delay = Input(UInt(delay_bits.W))
    // Use write enable only on correct ADC/DAC frequency:
    val write_enable = Input(Bool())
    val data_in = Input(SInt(32.W))

    val data_out = Output(SInt(32.W))
  })

  // With a buffer of f.ex. 4096, max delay is 4095! Check legal delay?

  // Use bram black_box.
  val bram = Module(new BRAM(SInt(32.W), Int(maxSize))).io

  // Let head start at address 0.
  val head = RegInit(0.U(delay_bits.W))

  // Wire write_enable in accordance to module input.
  bram.write_enable := io.write_enable

  // Read delayed sample. If out of bounds, wrap around.
  when (head-io.sample_delay < 0.U) {
    bram.read_addr := (head-io.sample_delay) + maxSize
  } .otherwise {
    bram.read_addr := head-io.sample_delay
  }

  // Let write address be at head.
  bram.write_addr := head

  // If it is written to head, let head increment with wraparound.
  when (io.write_enable) {
    head := (head + 1.U) % maxSize
  }

  // Wire data in and out between bram and this module
  bram.data_in := io.data_in
  io.data_out := bram.data_out

}