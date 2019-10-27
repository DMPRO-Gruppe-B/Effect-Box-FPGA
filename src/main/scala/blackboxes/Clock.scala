package blackboxes

import chisel3._
import chisel3.core.{IntParam, DoubleParam, StringParam}
import chisel3.experimental.ExtModule

case class ClockConfig(divide: Int, duty_cycle: Double, phase: Double)

object ClockConfig {
  def default = ClockConfig(1, 0.5, 0.0)
}

/**
  * A blackbox for the MMCME2_BASE verilog macro.
  *
  * Full documentation on the parameters and functionality can be found here:
  * https://www.xilinx.com/support/documentation/sw_manuals/xilinx2012_2/ug953-vivado-7series-libraries.pdf#1023753241
  */
class MMCME2(
  period: Double,
  base_mult: Double,
  base_div: Int,
  clock0_divide: Double,
  clocks: List[ClockConfig],
  clock4_cascade: Boolean
) extends ExtModule(Map(
  "CLKIN1_PERIOD"      -> DoubleParam(period),
  "CLKFBOUT_MULT_F"    -> DoubleParam(base_mult),
  "DIVCLK_DIVIDE"      -> IntParam(base_div),
  "CLKOUT4_CASCADE"    -> StringParam(if (clock4_cascade) "TRUE" else "FALSE"),
  "CLKOUT0_DIVIDE_F"   -> DoubleParam(clocks(0).divide),
  "CLKOUT0_DUTY_CYCLE" -> DoubleParam(clocks(0).duty_cycle),
  "CLKOUT0_PHASE"      -> DoubleParam(clocks(0).phase),
  "CLKOUT1_DIVIDE"     -> IntParam(clocks(1).divide),
  "CLKOUT1_DUTY_CYCLE" -> DoubleParam(clocks(1).duty_cycle),
  "CLKOUT1_PHASE"      -> DoubleParam(clocks(1).phase),
  "CLKOUT2_DIVIDE"     -> IntParam(clocks(2).divide),
  "CLKOUT2_DUTY_CYCLE" -> DoubleParam(clocks(2).duty_cycle),
  "CLKOUT2_PHASE"      -> DoubleParam(clocks(2).phase),
  "CLKOUT3_DIVIDE"     -> IntParam(clocks(3).divide),
  "CLKOUT3_DUTY_CYCLE" -> DoubleParam(clocks(3).duty_cycle),
  "CLKOUT3_PHASE"      -> DoubleParam(clocks(3).phase),
  "CLKOUT4_DIVIDE"     -> IntParam(clocks(4).divide),
  "CLKOUT4_DUTY_CYCLE" -> DoubleParam(clocks(4).duty_cycle),
  "CLKOUT4_PHASE"      -> DoubleParam(clocks(4).phase),
  "CLKOUT5_DIVIDE"     -> IntParam(clocks(5).divide),
  "CLKOUT5_DUTY_CYCLE" -> DoubleParam(clocks(5).duty_cycle),
  "CLKOUT5_PHASE"      -> DoubleParam(clocks(5).phase),
  "CLKOUT6_DIVIDE"     -> IntParam(clocks(6).divide),
  "CLKOUT6_DUTY_CYCLE" -> DoubleParam(clocks(6).duty_cycle),
  "CLKOUT6_PHASE"      -> DoubleParam(clocks(6).phase)
)) {
  // The name in the verilog file
  override def desiredName: String = "MMCME2_BASE"

  val CLKIN1    = IO(Input(Clock()))
  val RST       = IO(Input(Bool())) // Reset is logic low
  val PWRDWN    = IO(Input(Bool()))
  val CLKFBIN   = IO(Input(Clock()))
  val CLKFBOUT  = IO(Output(Clock()))
  val CLKFBOUTB = IO(Output(Clock()))
  val LOCKED    = IO(Output(Bool()))
  val CLKOUT0   = IO(Output(Clock()))
  val CLKOUT0B  = IO(Output(Clock()))
  val CLKOUT1   = IO(Output(Clock()))
  val CLKOUT1B  = IO(Output(Clock()))
  val CLKOUT2   = IO(Output(Clock()))
  val CLKOUT2B  = IO(Output(Clock()))
  val CLKOUT3   = IO(Output(Clock()))
  val CLKOUT3B  = IO(Output(Clock()))
  val CLKOUT4   = IO(Output(Clock()))
  val CLKOUT5   = IO(Output(Clock()))
  val CLKOUT6   = IO(Output(Clock()))
}
