package EffectBox

import chisel3._
import chisel3.experimental.MultiIOModule
import chisel3.util.Decoupled

class BitCrushControl extends Bundle {
  val nCrushBits = Input(UInt(4.W))
  val bypass = Input(Bool())
}

class BitCrush extends MultiIOModule {
  val io = IO(new EffectBundle)
  val ctrl = IO(new BitCrushControl)

  io.in.ready := true.B
  io.out.valid := io.in.valid

  when (ctrl.bypass) {
    io.out.bits := io.in.bits
  } .otherwise {
    val mask = 0xFFFFFFFFl.U(32.W) << (ctrl.nCrushBits)
    io.out.bits := (io.in.bits.asUInt() & mask.asUInt()).asSInt()
  }
}
