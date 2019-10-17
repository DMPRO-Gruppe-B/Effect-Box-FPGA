package blackboxes

import chisel3._
import chisel3.core.{IntParam, StringParam}
import chisel3.experimental.ExtModule


/**
  * A wrapper for a BRAM module
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


/**
  * A blackbox for the BRAM_36K verilog module, which creates a 36Kb BRAM region.
  *
  * Full documentation on the parameters and functionality can be found here:
  * https://www.xilinx.com/support/documentation/sw_manuals/xilinx2012_2/ug953-vivado-7series-libraries.pdf#1023770397
  *
  * The following table describes how the different arguments affect how the BRAM
  * is created and used.
  *
  * ///////////////////////////////////////////////////////////////////////
  * // READ_WIDTH  | BRAM_SIZE | READ Depth  | RDADDR Width |            //
  * // WRITE_WIDTH |           | WRITE Depth | WRADDR Width | WE Width   //
  * // ============|===========|=============|==============|============//
  * //    37-72    |   "36Kb"  |      512    |     9-bit    |   8-bit    //
  * //    19-36    |   "36Kb"  |     1024    |    10-bit    |   4-bit    //
  * //    10-18    |   "36Kb"  |     2048    |    11-bit    |   2-bit    //
  * //     5-9     |   "36Kb"  |     4096    |    12-bit    |   1-bit    //
  * //     3-4     |   "36Kb"  |     8192    |    13-bit    |   1-bit    //
  * //      2      |   "36Kb"  |    16384    |    14-bit    |   1-bit    //
  * //      1      |   "36Kb"  |    32768    |    15-bit    |   1-bit    //
  * ///////////////////////////////////////////////////////////////////////
  *
  * Parameters:
  * - width: Data width in bits, between 1 and 72
  * - ram_mode: The BRAM mode. Either "SDP" (Simple Dual Port) or "TDP" (True Dual Port)
  * - write_mode: The output behaviour if the same address is read and written at the same time.
  *               When using the SDP mode READ_FIRST should be used when both ports use the same
  *               clock, otherwise WRITE_FIRST should be used.
  *               "WRITE_FIRST": Written data is outputted.
  *               "READ_FIRST":  Previous data at that address is outputted.
  *               "NO_CHANGE":   The previously read value is outputted.
  */
class BRAMV_36K(
  read_width_a: Int,
  read_width_b: Int,
  write_width_a: Int,
  write_width_b: Int,
  ram_mode: String   = "SDP",
  write_mode: String = "READ_FIRST"
) extends ExtModule(Map(
  // Verilog parameters
  "READ_WIDTH_A"  -> IntParam(read_width_a),
  "READ_WIDTH_B"  -> IntParam(read_width_b),
  "WRITE_WIDTH_A" -> IntParam(write_width_a),
  "WRITE_WIDTH_B" -> IntParam(write_width_b),
  "RAM_MODE"      -> StringParam(ram_mode),
  "WRITE_MODE"    -> StringParam(write_mode)
)) {
  // The name in the verilog file
  override def desiredName: String = "BRAM_36K"

  // Part A
  val DOADO         = IO(Output(UInt(32.W)))
  val DOPADOP       = IO(Output(UInt(3.W)))
  val ADDRARDADDR   = IO(Input(UInt(16.W)))
  val DIADI         = IO(Input(UInt(32.W)))
  val DIPADIP       = IO(Input(UInt(3.W)))
  val WEA           = IO(Input(UInt(4.W)))
  val CLKARDCLK     = IO(Input(Clock()))
  val ENARDEN       = IO(Input(Bool()))
  val REGCEAREGCE   = IO(Input(Bool()))
  val RSTRAMARSTRAM = IO(Input(Bool()))
  val RSTREGARSTREG = IO(Input(Bool()))

  // Part B
  val DOBDO         = IO(Output(UInt(32.W)))
  val DOPBDOP       = IO(Output(UInt(3.W)))
  val ADDRBWRADDR   = IO(Input(UInt(16.W)))
  val DIBDI         = IO(Input(UInt(32.W)))
  val DIPBDIP       = IO(Input(UInt(4.W)))
  val WEBWE         = IO(Input(UInt(8.W)))
  val RSTRAMB       = IO(Input(Bool()))
  val RSTREGB       = IO(Input(Bool()))
  val CLKBWRCLK     = IO(Input(Clock()))
  val ENBWREN       = IO(Input(Bool()))
  val REGCEB        = IO(Input(Bool()))
}
