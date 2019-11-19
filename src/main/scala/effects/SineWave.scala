package EffectBox

import chisel3._
import chisel3.util._

class WaveControl extends Bundle {
  val inc = Input(Bool())
  val signal = Output(new SignedFraction)
}

//object WaveMux {
//  def apply(select: UInt): Wave ={
//
//    val sine = Module(new SineWave)
//    val square = Module(new SquareWave)
//    Mux[Wave](select === 0.U, sine, square)
//  }
//}
//

class Wave extends Module {
  val io = IO(new WaveControl)
}

class SquareWave extends Wave {
  val (x, wrap) = Counter(io.inc, 180)
  val (n, _) = Counter(wrap, 2)
  val SIG = 360

  when (n === 0.U) {
    io.signal.numerator := SIG.S
  }.otherwise {
    io.signal.numerator := (-SIG).S
  }
  io.signal.denominator := SIG.U
}

class TriangleWave extends Wave {
  val (x, wrap) = Counter(io.inc, 180)
  val (n, _) = Counter(wrap, 2)

  when(n === 0.U) {
    io.signal.numerator := (x.asSInt() - 90.S)
  }.otherwise {
    io.signal.numerator := (90.S - x.asSInt())
  }
  io.signal.denominator := 90.U
}

class SawtoothWave extends Wave {
  val (x, _) = Counter(io.inc, 360)

  io.signal.numerator := (x.asSInt() - 180.S) >> 1
  io.signal.denominator := 90.U
}

class SineWave extends Wave{
  val (x, wrap) = Counter(io.inc, 180)
  val (n, _) = Counter(wrap, 2)

  val top = WireInit(UInt(16.W), 4.U * x * (180.U - x))
  val bot = WireInit(UInt(16.W), 40500.U - x * (180.U - x))

  when (n === 0.U) {
    io.signal.numerator := (top >> 8).asSInt()
  }.otherwise {
    io.signal.numerator := (-1).S * (top >> 8).asSInt()
  }
  io.signal.denominator := bot >> 8
}
