package EffectBox

import chisel3._
import chisel3.util._
import blackboxes.BRAM

class DelayBuffer extends Module {
    val io = IO(new Bundle {
      val in = Flipped(Decoupled(Input(Sample())))
      val delaySamples = Input(UInt(16.W))

      val out = Output(SInt(16.W))
    })

    val mem  = Module(new BRAM(UInt(16.W),16)).io
    val writeHead = Reg(t=UInt(16.W), init=0.U)

    io.in.ready := true.B
    
    mem.write_enable  := io.in.valid
    mem.read_addr     := Mux(writeHead < io.delaySamples,65536.U + writeHead-io.delaySamples,writeHead)
    mem.write_addr    := writeHead
    mem.data_in       := io.in.bits.asUInt

    io.out := Mux(io.in.valid,mem.data_out.asSInt,0.S)

    writeHead := Mux(io.in.valid,writeHead + 1.U,writeHead)
  }