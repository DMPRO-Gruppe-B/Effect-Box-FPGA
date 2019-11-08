package io

import chisel3._

class SPIBus extends Bundle {
  val mosi = Input(Bool()) // SPI Master out slave in
  val miso = Output(Bool()) // SPI Master in slave out
  val clk = Input(Bool()) // SPI clock
  val cs_n = Input(Bool()) // SPI chip select
}

class SPISlave extends Module {
  val io = IO(new Bundle {
    val spi = new SPIBus

    val output = Output(UInt(24.W))
    val output_valid = Output(Bool())

    val debug = Output(UInt(4.W))
  })
  io.spi.miso := false.B

  val buf = RegInit(0.U(24.W))
  val bits_left = RegInit(24.U(7.W))
  val prev_clk = RegNext(io.spi.clk)

  when(io.spi.cs_n) {
    bits_left := 24.U
    buf := 0x0.U
    prev_clk := false.B
  }.otherwise {
    when(io.spi.clk && !prev_clk) {
      val bl = bits_left - 1.U
      // Rising edge
      buf := buf | (io.spi.mosi.asUInt() << bl).asUInt()
      bits_left := bl
    }.elsewhen(!io.spi.clk && prev_clk) {
      //
    }
  }

  io.output_valid := (bits_left === 0.U)
  io.output := buf
  io.debug := io.spi.cs_n | (io.spi.mosi << 1).asUInt() | (io.spi.clk << 2).asUInt() | (prev_clk << 3).asUInt()
}
