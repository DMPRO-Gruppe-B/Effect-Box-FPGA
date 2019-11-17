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
  val counter = Reg(UInt(16.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid


  when (ctrl.bypass) {
    io.out.bits := io.in.bits
    sine.inc := false.B

  }.otherwise {
    when (io.in.valid) {

      when(counter >= ctrl.periodMultiplier - 1.U || counter < 0.U) {
        counter := 0.U
        sine.inc := true.B
      }.otherwise {
        counter := counter + 1.U
        sine.inc := false.B
      }

    }.otherwise {
      sine.inc := false.B
    }

    val denominator = WireInit(UInt(9.W), sine.signal.denominator >> 8)
    val numerator = WireInit(SInt(9.W), sine.signal.numerator >> 8)
    val res = Wire(SInt(32.W))

    // shift to move sine wave above y-axis, equivalent to 0.5 sin(x) + 0.5
    res := io.in.bits * (numerator.asSInt() + denominator.asSInt()) / (denominator << 1).asSInt()

    io.out.bits := res

  }
}
