package EffectBox

import chisel3._
import chisel3.MultiIOModule
import chisel3.util.random.GaloisLFSR

/*
 * Bitcrush 2: Downsamples by reducing the sample rate.
 */

class BitCrush2Control extends Bundle {
  val nCrushBits = Input(UInt(4.W))
  val bypass = Input(Bool())
}

class BitCrush2 extends MultiIOModule {
  val io = IO(new EffectBundle)
  val ctrl = IO(new BitCrush2Control)

  val counter = Reg(UInt(32.W))
  val sample = Reg(SInt(32.W))

  val multiplier = 500.U

  io.in.ready := true.B
  io.out.valid := io.in.valid

  when (counter >= ctrl.nCrushBits * multiplier || counter < 0.U) {
    counter := 0.U
    sample := io.in.bits
  } .otherwise {
    counter := counter + 1.U
  }

  when (ctrl.bypass) {
    io.out.bits := io.in.bits
  } .otherwise {
    io.out.bits := sample
  }
}
