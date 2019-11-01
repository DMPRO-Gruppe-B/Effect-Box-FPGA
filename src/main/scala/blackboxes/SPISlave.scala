package blackboxes

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

    val output = Output(UInt(8.W))
    val output_valid = Output(Bool())

    val debug = Output(UInt(4.W))
  })
  io.spi.miso := 0.U

  val buf = RegInit(0.U(8.W))
  val bits_read = RegInit(0.U(5.W))
  val prev_clk = RegInit(false.B)

  io.debug := bits_read | (prev_clk << 5).asUInt()

  when(!io.spi.cs_n) {
    when(io.spi.clk && !prev_clk) {
      // Rising edge
      prev_clk := true.B
      buf := buf | (io.spi.mosi.asUInt() << bits_read).asUInt()
      bits_read := bits_read + 1.U
    }.elsewhen(prev_clk && !io.spi.clk) {
      prev_clk := false.B
    }
  }.otherwise {
    bits_read := 0.U
    prev_clk := false.B
  }

  when(bits_read === 8.U) {
    bits_read := 0.U
    io.output_valid := true.B
  }.otherwise {
    io.output_valid := false.B
  }

  io.output := buf

}
