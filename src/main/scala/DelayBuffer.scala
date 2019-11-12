package EffectBox

import chisel3._
import chisel3.util._
import blackboxes.BRAM

class DelayBuffer(val addr_width: Int) extends Module {
    val io = IO(new Bundle {
      val in = Input(SInt(16.W))
      val out = Input(SInt(16.W))
      val delaySamples = Input(UInt(addr_width.W))
      val write_enable = Input(Bool())
    })
    val effect_buffer = IO(new EffectBundle)

    val mem  = Module(new BRAM(UInt(16.W),addr_width)).io
    val writeHead = RegNext(0.U(UInt(addr_width.W)))
    
    mem.write_enable  := io.write_enable
    mem.read_addr     := writeHead - io.delaySamples
    mem.write_addr    := writeHead
    mem.data_in       := io.in
    mem.data_out      := io.out

    // By ab(using) overflow we don't need to reset the head value
    writeHead := Mux(io.write_enable, writeHead + 1.U, writeHead)
  }