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
  val (n, _) = Counter(true.B, 0xff)

  sine.inc := (n % io.periodMultiplier === 0.U)

  val top = Wire(SInt(40.W))
  top := sine.signal.numerator
  val bot = Wire(SInt(40.W))
  bot := sine.signal.denominator.asSInt()
  val input = Wire(SInt(40.W))
  input := io.in

  io.out := input * (top + (3.S*bot)) / (4.S*bot)  // ehh, glemte å dokumentere de magiske tallene her... :sad_face:

}
