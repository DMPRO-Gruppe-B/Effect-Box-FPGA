package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

class TremoloControl extends Bundle {
  val periodMultiplier = Input(UInt(16.W))
  val bypass = Input(Bool())
  val depth = Input(SInt(8.W))
}

class Tremolo extends MultiIOModule {

  val io = IO(new EffectBundle)
  val ctrl = IO(new TremoloControl)

  val TREMOLO_DENOMINATOR = 20
  val sine = Module(new SineWave).io
  val counter = Reg(UInt(16.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid


  when(ctrl.bypass) {
    io.out.bits := io.in.bits
    sine.inc := false.B

  }.otherwise {
    when(io.in.valid) {

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

    val denominator = WireInit(UInt(8.W), (sine.signal.denominator >> 8).asUInt())
    val numerator = WireInit(SInt(9.W), (sine.signal.numerator >> 8).asSInt())
    val res = Wire(SInt(32.W))

    // equivalent to depth* sin(x) + (1 - depth)
    res := io.in.bits *
      ( (numerator * ctrl.depth).asSInt() +
        (TREMOLO_DENOMINATOR.S - ctrl.depth) * denominator) /
      (denominator * TREMOLO_DENOMINATOR.S)

    io.out.bits := res

  }
}
