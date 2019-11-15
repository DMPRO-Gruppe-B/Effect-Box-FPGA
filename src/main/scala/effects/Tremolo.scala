package EffectBox

import chisel3._
import chisel3.util._

class TremoloControl extends Bundle {
  val periodMultiplier = Input(UInt(16.W))
  val bypass = Input(Bool())
}

class Tremolo extends MultiIOModule {

  val io = IO(new EffectBundle)
  val ctrl = IO(new TremoloControl)

  val sine = Module(new SineWave).io
  val counter = RegNext(0.U(16.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid

  val wrap = WireInit(false.B)

  when (counter >= ctrl.periodMultiplier - 1.U) {
    counter := 0.U
    wrap := true.B
  }.otherwise {
    counter := counter + 1.U
    wrap := false.B
  }

  sine.inc := wrap

  val top = Wire(SInt(40.W))
  top := sine.signal.numerator
  val bot = Wire(SInt(40.W))
  bot := sine.signal.denominator.asSInt()
  val input = Wire(SInt(40.W))
  input := io.in.bits

  io.out.bits := input * (top + (3.S*bot)) / (4.S*bot)  // ehh, glemte å dokumentere de magiske tallene her... :sad_face:

}
