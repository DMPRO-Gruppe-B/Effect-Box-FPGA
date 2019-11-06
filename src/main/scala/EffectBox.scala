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

    val mixNum = Input(UInt(8.W))
    val mixDenom = Input(UInt(8.W))

    val emptyBuffer = Input(Bool())

    val out = Output(SInt(32.W))
  })
  val fbFraction = Wire(new Fraction)
  val mixFraction = Wire(new Fraction)

  fbFraction.numerator := io.fbNum
  fbFraction.denominator := io.fbDenom

  mixFraction.numerator := io.mixNum
  mixFraction.denominator := io.mixDenom

  val delay = Module(new Delay).io
  val ctrl = Module(new Delay).ctrl

  delay.in := io.in
  ctrl.fbFraction := fbFraction
  ctrl.mixFraction := mixFraction
  ctrl.emptyBuffer := io.emptyBuffer

  io.out := delay.out
}
