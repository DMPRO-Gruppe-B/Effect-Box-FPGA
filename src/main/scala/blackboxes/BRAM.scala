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

  val bram = Module(new RAMV_Dual_Port(t, addr_width))

  bram.a      := io.write_addr
  bram.we     := io.write_enable
  bram.di     := io.data_in

  bram.dpra   := io.read_addr
  io.data_out := bram.dpo

  bram.clk    := clock
}


/**
  * A blackbox for the Dual-Port RAM With Asynchronous Read
  *
  * Full documentation on the parameters and functionality can be found here:
  * https://www.xilinx.com/support/documentation/sw_manuals/xilinx10/books/docs/xst/xst.pdf
  */
class RAMV_Dual_Port[T <: Data](val t: T, val addr_width: Int) extends ExtModule(Map(
  "DATA_WIDTH" -> IntParam(t.getWidth),
  "ADDR_WIDTH" -> IntParam(addr_width)
)) {
  // The name in the verilog file
  override def desiredName: String = "v_rams_11"

  val clk   = IO(Input(Clock()))
  val we    = IO(Input(Bool()))
  val a     = IO(Input(UInt(addr_width.W)))
  val dpra  = IO(Input(UInt(addr_width.W)))
  val di    = IO(Input(t))
  val spo   = IO(Output(t))
  val dpo   = IO(Output(t))
}
