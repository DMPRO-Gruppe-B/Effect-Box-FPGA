package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
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

//  when (io.in.valid) {
    when(counter >= ctrl.periodMultiplier - 1.U) {
      counter := 0.U
      wrap := true.B
    }.otherwise {
      counter := counter + 1.U
      wrap := false.B
    }

//  }
  sine.inc := wrap //&& io.in.valid

  when (!ctrl.bypass) {
    val top = Wire(SInt(40.W))
    top := sine.signal.numerator
    val bot = Wire(UInt(40.W))
    bot := sine.signal.denominator
    val input = Wire(SInt(40.W))
    input := io.in.bits

    val res = Wire(SInt(40.W))

    res := (input * top) >> 15
//    res := input * top / bot.asSInt()

    //(top + (3.S * bot)) / (4.S * bot)
    io.out.bits := res// ehh, glemte å dokumentere de magiske tallene her... :sad_face:
  } .otherwise {
    io.out.bits := io.in.bits
  }

}
