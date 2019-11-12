package EffectBox

import chisel3._
import chisel3.util._

class CombFilter() extends Module {

  val io = IO(new Bundle {
    val in           = Input(SInt(32.W))
    val gainFraction = Input(new Fraction)
    val emptyBuffer  = Input(Bool())
    val sampleDelay  = Input(UInt(16.W))

    val out = Output(SInt(32.W))
  })

  io.out := 0.S
}