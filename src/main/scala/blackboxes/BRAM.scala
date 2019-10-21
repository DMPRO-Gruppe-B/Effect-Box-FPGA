package blackboxes

import chisel3._
import chisel3.core.{IntParam, StringParam}
import chisel3.experimental.ExtModule


/**
  * A wrapper for a BRAM module.
  *
  * Parameters:
  * - t:          The type of data to store in this BRAM, like UInt(32.W)
  * - addr_width: The number of bits to use in the address. Limits the size of the BRAM
  */
class BRAM[T <: Data](val t: T, val addr_width: Int) extends Module {
  val io = IO(new Bundle {
    val write_enable  = Input(Bool())
    val read_addr     = Input(UInt(addr_width.W))
    val write_addr    = Input(UInt(addr_width.W))
    val data_in       = Input(t)
    val data_out      = Output(t)
  })

  // We only use 16 bits, so we only need one port A for reading and writing
  val bram = Module(new BRAMV_Dual_Port(t.getWidth, addr_width))

  // Always enable both ports
  bram.ena    := true.B
  bram.enb    := true.B

  // Disable write on port B
  bram.wea    := io.write_enable
  bram.web    := false.B
  bram.dib    := 0.U

  bram.addra  := io.write_addr
  bram.dia    := io.data_in

  bram.addrb  := io.read_addr
  io.data_out := bram.dob

  bram.clka   := clock
  bram.clkb   := clock
}


/**
  * A blackbox for the Dual-Port Block RAM With Two Write Ports verilog module.
  *
  * Full documentation on the parameters and functionality can be found here:
  * https://www.xilinx.com/support/documentation/sw_manuals/xilinx10/books/docs/xst/xst.pdf#G5.418134
  */
class BRAMV_Dual_Port(val data_width: Int, val addr_width: Int) extends ExtModule(Map(
  "DATA_WIDTH" -> IntParam(data_width),
  "ADDR_WIDTH" -> IntParam(addr_width)
)) {
  // The name in the verilog file
  override def desiredName: String = "v_rams_16"

  val clka  = IO(Input(Clock()))
  val clkb  = IO(Input(Clock()))
  val ena   = IO(Input(Bool()))
  val enb   = IO(Input(Bool()))
  val wea   = IO(Input(Bool()))
  val web   = IO(Input(Bool()))
  val addra = IO(Input(UInt(addr_width.W)))
  val addrb = IO(Input(UInt(addr_width.W)))
  val dia   = IO(Input(UInt(data_width.W)))
  val dib   = IO(Input(UInt(data_width.W)))
  val doa   = IO(Output(UInt(data_width.W)))
  val dob   = IO(Output(UInt(data_width.W)))
}
