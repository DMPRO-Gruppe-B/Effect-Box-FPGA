package EffectBox

import chisel3._
import blackboxes.BRAM

class DelayBuffer extends Module {
  val io = IO(new EffectBundle {
    val delaySamples = Input(UInt(32.W))
  })

  val addr_width = 17
  val mem  = Module(new BRAM(SInt(32.W), addr_width)).io
  val writeHead = RegNext(0.U(addr_width.W))

  io.in.ready := true.B

  mem.write_enable  := io.in.valid
  mem.read_addr     := writeHead - io.delaySamples
  mem.write_addr    := writeHead
  mem.data_in       := io.in.bits

  io.out.bits  := mem.data_out
  io.out.valid := RegNext(io.in.valid)

  writeHead := Mux(io.in.valid, writeHead + 1.U, writeHead)
}
