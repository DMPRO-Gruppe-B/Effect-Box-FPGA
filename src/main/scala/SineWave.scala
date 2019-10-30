package EffectBox

import chisel3._
import chisel3.util._


class SineWave extends Module{

  val io = IO(new Bundle {
    val inc = Input(Bool())
    val signal = Output(new SignedDoubleFraction)
  })
  val (x, wrap) = Counter(io.inc, 180)
  val (n, _) = Counter(wrap, 2)

  val top = WireInit(4.U * x * (180.U - x))
  val bot = WireInit(40500.U - x * (180.U - x))

  io.signal.denominator := bot
  when (n === 0.U) {
    io.signal.numerator := top.asSInt()
  }.otherwise {
    io.signal.numerator := (-top).asSInt()
  }
}
