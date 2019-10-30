package EffectBox

import chisel3._
import chisel3.util._

class Tremolo extends Module{

  val io = IO(new Bundle {
    val in = Input(SInt(32.W))
    val out = Output(SInt(32.W))
  })

  val sine = Module(new SineWave).io
  val (n, _) = Counter(true.B, 180)

  sine.inc := (n % 18.U === 0.U)

//  val a = Multiply(io.in.asSInt(), sine.signal)
  val top = Wire(SInt(40.W))
  top := sine.signal.numerator
  val bot = Wire(SInt(40.W))
  bot := sine.signal.denominator.asSInt()
  val input = Wire(SInt(40.W))
  input := io.in
  val mult = input.asSInt() * (top.asSInt() + (3.S*bot)) / (4.S*bot)
  io.out := mult

}
