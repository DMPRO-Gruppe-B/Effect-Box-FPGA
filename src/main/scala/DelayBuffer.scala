package EffectBox

import chisel3._
import chisel3.util._
import blackboxes.BRAM

class DelayBuffer(val addr_width: Int) extends Module {
  
    val io = IO(new Bundle {
      val in = Input(SInt(16.W))
      val out = Input(SInt(16.W))
      val delaySamples = Input(UInt(16.W))
    })

    val mem  = Module(new BRAM(UInt(16.W),addr_width)).io
    val writeHead = RegInit(0.U(UInt(addr_width.W)))
    val maxValue : Int = scala.math.pow(2,addr_width.toDouble).toInt - 1

    mem.write_enable  := true.B
    mem.read_addr     := writeHead - io.delaySamples*0xFFFF.U
    mem.write_addr    := writeHead
    mem.data_in       := io.in
    mem.data_out      := io.out

    writeHead := writeHead + 0xFFFF.U
  }