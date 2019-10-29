package EffectBox

import chisel3._
import chisel3.util._


class SineWave extends Module{

  val io = IO(new Bundle {
    val inc = Input(Bool())
    val signal = Output(new SignedDoubleFraction)
    val w = Input(new Fraction)

  })

  val (i, wrap) = Counter(io.inc, 180)
  val (n, _) = Counter(wrap, 2)

//  val x = (360.U * i * io.w.numerator) / io.w.denominator
  val q = (360.U * i * io.w.numerator / io.w.denominator)
  val x = q % 180.U

  val neg = WireInit(false.B)

  neg := (q % 360.U) < 180.U
//  val x = Multiply(io.w.numerator, io.w.denominator, (360.U * i).asSInt()).asUInt()

  val top = WireInit(4.U * x * (180.U - x))
  val bot = WireInit(40500.U - x * (180.U - x))


  io.signal.denominator := bot
  when (neg) {
    io.signal.numerator := top.asSInt()
  }.otherwise {
    io.signal.numerator := (-top).asSInt()
  }
//  io.signal.negative := n


}
