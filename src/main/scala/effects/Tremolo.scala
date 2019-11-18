package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util._

class TremoloControl extends Bundle {
  val periodMultiplier = Input(UInt(16.W))
  val bypass = Input(Bool())
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
  sine.inc := inc
  square.inc := inc
//  val wave = Mux(ctrl.waveSelect === 0.U, sine, square)
//  val wave = sine.io
//  when (ctrl.waveSelect === 0.U) {
//    wave := sine.io
//  }.otherwise {
//    wave := square.io
//  }

//  val wave = Module(new SineWave).io

  val counter = Reg(UInt(16.W))

  io.in.ready := true.B
  io.out.valid := io.in.valid

  when(ctrl.bypass) {
    io.out.bits := io.in.bits
    inc := false.B

  }.otherwise {
    when(io.in.valid) {

      when(counter >= ctrl.periodMultiplier - 1.U || counter < 0.U) {
        counter := 0.U
//        wave.inc := true.B
        inc := true.B
      }.otherwise {
        counter := counter + 1.U
        inc := false.B
      }

    }.otherwise {
      inc := false.B
    }

    val signal = Mux(ctrl.waveSelect === 0.U, sine.signal, square.signal)
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
}
