package EffectBox

import chisel3._
import chisel3.util.Decoupled


/**
  * A buffer between effects that stores samples until the effect is ready.
  */
class EffectBuffer() extends Module {
  val io = IO(new EffectBundle)

  val reg = Reg(Sample())
  val valid = Reg(Bool())
  val ready = Reg(Bool())

  io.in.ready := ready
  io.out.valid := valid
  io.out.bits := reg

  when (valid && io.out.ready) {
    valid := false.B
    ready := false.B
  }.otherwise {
    when (io.in.valid) {
      valid := true.B
      reg := io.in.bits
    }

    when (io.out.ready) {
      ready := true.B
    }
  }
}


object EffectBuffer {
  /**
    * A shortcut for creating a buffer between two effects.
    */
  def apply(in: Bundle, out: Bundle) {
    val buffer = Module(new EffectBuffer())
    buffer.io.in <> in
    buffer.io.out <> out
  }
}
