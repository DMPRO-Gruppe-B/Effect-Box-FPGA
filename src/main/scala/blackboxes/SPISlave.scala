package blackboxes

import chisel3._
import chisel3.core.IntParam
import chisel3.experimental.ExtModule

class SPIBus extends Bundle {
  val mosi = Input(Bool()) // SPI Master out slave in
  val miso = Output(Bool()) // SPI Master in slave out
  val clk = Input(Bool()) // SPI clock
  val cs_n = Input(Bool()) // SPI chip select
}


class SPISlaveReadonly extends Module {
  val io = IO(new Bundle {
    val recv_data = Output(UInt(8.W))
    val data_valid = Output(Bool())

    // SPI wires
    val spi_clk = Input(Bool())
    val spi_mosi = Input(Bool())
    val spi_cs_n = Input(Bool())
    val spi_miso = Output(Bool())
  })

  val ext_spi = Module(new SPI_Slave_External())
  ext_spi.i_Clk := clock
  ext_spi.i_Rst_L := !reset.asBool()

  ext_spi.i_TX_DV := false.B
  ext_spi.i_TX_Byte := 0.U(8)

  io.recv_data := ext_spi.o_RX_Byte
  io.data_valid := ext_spi.o_RX_DV

  ext_spi.i_SPI_Clk := io.spi_clk
  ext_spi.i_SPI_MOSI := io.spi_mosi
  ext_spi.i_SPI_CS_n := io.spi_cs_n
  io.spi_miso := ext_spi.o_SPI_MISO
}

class SPI_Slave_External extends ExtModule(Map("SPI_MODE" -> IntParam(0))) {
  override def desiredName: String = "SPI_Slave"

  // IO from SPI_Slave.v
  // Control/Data Signals
  val i_Clk: Clock = IO(Input(Clock())) //       FPGA Clock
  val i_Rst_L: Bool = IO(Input(Bool())) //       FPGA Reset
  val i_TX_DV: Bool = IO(Input(Bool())) //       Data Valid pulse to register i_TX_Byte
  val i_TX_Byte: UInt = IO(Input(UInt(8.W))) //  Byte to serialize to MISO.
  val o_RX_DV: Bool = IO(Output(Bool())) //      Data Valid pulse (1 clock cycle)
  val o_RX_Byte: UInt = IO(Output(UInt(8.W))) // Byte received on MOSI

  // SPI Interface
  val i_SPI_Clk: Bool = IO(Input(Bool()))
  val i_SPI_MOSI: Bool = IO(Input(Bool()))
  val i_SPI_CS_n: Bool = IO(Input(Bool()))
  val o_SPI_MISO: Bool = IO(Output(Bool()))
}
