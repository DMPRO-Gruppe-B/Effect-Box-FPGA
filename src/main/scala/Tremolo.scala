package EffectBox

import chisel3._
import chisel3.util._

class Tremolo extends Module{

  val io = IO(new Bundle {
    val in = Input(SInt(32.W))
    val out = Output(SInt(32.W))
    val periodMultiplier = Input(UInt(16.W))
  })

  val sine = Module(new SineWave).io
  val counter = RegInit(0.U(16.W))
  val wrap = WireInit(false.B)

  when (counter % io.periodMultiplier === io.periodMultiplier - 1.U) {
    counter := 0.U
    wrap := true.B
  }.otherwise {
    counter := counter.asUInt() + 1.U
    wrap := false.B
  }
  sine.inc := wrap

  val top = Wire(SInt(40.W))
  top := sine.signal.numerator
  val bot = Wire(SInt(40.W))
  bot := sine.signal.denominator.asSInt()
  val input = Wire(SInt(40.W))
  input := io.in

  io.out := input * (top + (3.S*bot)) / (4.S*bot)  // ehh, glemte å dokumentere de magiske tallene her... :sad_face:

}
