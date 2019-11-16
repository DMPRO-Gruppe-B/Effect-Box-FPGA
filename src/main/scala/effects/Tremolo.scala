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

  val periodChanged = ctrl.periodMultiplier === RegNext(ctrl.periodMultiplier)


  when (ctrl.bypass) {
    io.out.bits := io.in.bits
    sine.inc := false.B

  }.otherwise {
    //  val periodChanged = false.B
    when (!io.in.valid || periodChanged) {

      when(counter >= ctrl.periodMultiplier - 1.U) {
        counter := 0.U
        wrap := true.B // io.in.valid
      }.otherwise {
        counter := counter + 1.U
        wrap := false.B
      }

    }


    sine.inc := wrap

    val denominator = WireInit(UInt(9.W), sine.signal.denominator >> 8)
    val numerator = WireInit(SInt(9.W), sine.signal.numerator >> 8)
    val res = Wire(SInt(32.W))

//    res := (io.in.bits * sine.signal.numerator) >> 15
    res := io.in.bits * (numerator.asSInt() + 3.S * denominator.asSInt()) / (4.S*denominator.asSInt())

    //(top + (3.S * bot)) / (4.S * bot)
    io.out.bits := res

  }
}
