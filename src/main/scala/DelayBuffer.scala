package EffectBox

import chisel3._
import blackboxes.BRAM

class DelayBuffer extends Module {
  val io = IO(new Bundle {
    val in           = Input(Sample())
    val out          = Output(Sample())
    val enable       = Input(Bool())
    val delaySamples = Input(UInt(16.W))
  })

  val addr_width = 17
  val mem        = Module(new BRAM(Sample(), addr_width)).io
  val writeHead  = RegNext(0.U(addr_width.W))

  writeHead := Mux(io.enable, writeHead + 1.U, writeHead)

  mem.write_enable  := io.enable
  mem.read_addr     := writeHead - io.delaySamples
  mem.write_addr    := writeHead
  mem.data_in       := io.in

  io.out := mem.data_out
}
