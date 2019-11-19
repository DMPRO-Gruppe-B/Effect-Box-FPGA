package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

class TremoloControl extends Bundle {
  val periodMultiplier = Input(UInt(16.W))
  val depth = Input(UInt(8.W))
  val waveSelect = Input(UInt(2.W))
}

class Tremolo extends MultiIOModule {
  val TREMOLO_DENOMINATOR = 20

  val io = IO(new EffectBundle)
  val ctrl = IO(new TremoloControl)


  val inc = WireInit(Bool(), false.B)

  val sine = Module(new SineWave).io
  val square = Module(new SquareWave).io
  val triangle = Module(new TriangleWave).io
  val sawtooth = Module(new SawtoothWave).io

  sine.inc := inc
  square.inc := inc
  triangle.inc := inc
  sawtooth.inc := inc

  val counter = Reg(UInt(16.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid

  when(io.in.valid) {

    when(counter >= ctrl.periodMultiplier - 1.U || counter < 0.U) {
      counter := 0.U
      inc := true.B
    }.otherwise {
      counter := counter + 1.U
      inc := false.B
    }

  }.otherwise {
    inc := false.B
  }

  val signal = MuxLookup(ctrl.waveSelect, sine.signal, Array(
    0.U -> sine.signal,
    1.U -> square.signal,
    2.U -> triangle.signal,
    3.U -> sawtooth.signal
  ))

  val denominator = WireInit(UInt(8.W), signal.denominator)
  val numerator = WireInit(SInt(8.W), signal.numerator)
  val res = Wire(SInt(32.W))

  // equivalent to depth* sin(x) + (1 - depth)
  res := io.in.bits *
    ( (numerator * ctrl.depth).asSInt() +
      (TREMOLO_DENOMINATOR.S - ctrl.depth.asSInt()) * denominator) /
    (denominator * TREMOLO_DENOMINATOR.S)

  io.out.bits := res
}
