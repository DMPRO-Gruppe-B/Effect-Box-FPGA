package EffectBox

import chisel3._


/**
  * The main class for the effect box.
  * It takes input from the outside, puts it
  * through the effect modules, and outputs it.
  */
class EffectBox() extends Module {
  val io = IO(new Bundle {
    val in = Input(SInt(32.W))
    val fbNum = Input(UInt(8.W))
    val fbDenom = Input(UInt(8.W))
    val write_enable = Input(Bool())
    val bypass =  Input(Bool())
    val sample_delay = Input(UInt(13.W))

    val mixNum = Input(UInt(8.W))
    val mixDenom = Input(UInt(8.W))

    //val emptyBuffer = Input(Bool())

    val out = Output(SInt(32.W))
  })
  val fbFraction = Wire(new Fraction)
  val mixFraction = Wire(new Fraction)

  fbFraction.numerator := io.fbNum
  fbFraction.denominator := io.fbDenom

  mixFraction.numerator := io.mixNum
  mixFraction.denominator := io.mixDenom

  val delay = Module(new Delay).io

  delay.data_in := io.in
  delay.fbFraction := fbFraction
  delay.mixFraction := mixFraction
  delay.bypass:= io.bypass
  delay.write_enable := io.write_enable
  delay.sample_delay :=  io.sample_delay

  io.out := delay.data_out
}
