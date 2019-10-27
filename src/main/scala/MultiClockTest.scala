package EffectBox

import chisel3._
import chisel3.core.withClock
import blackboxes.{MMCME2, ClockConfig, BRAM}


/**
  * A test for using multiple clocks at the same time
  */
class MultiClockTest extends Module {
  val io = IO(new Bundle {
    val sysClk = Output(Clock())
    val bitClk = Output(Clock())
    val testOut = Output(Bool())
  })

  // Normally we use the base mult and divide to get the frequency
  // to the range we want, and then the individual divides (up to 128)
  // to get the exact frequency for each clock. In this case the difference
  // between the system clock and sample clock is too big for that (256 times),
  // so we must use a special feature of the MMCME2 module. We can cascade the
  // divide value of clock 6 into clock 4, so we can divide a lot further.
  val clockConfig = List(
    ClockConfig.default,
    ClockConfig.default,
    ClockConfig.default,
    ClockConfig.default,
    ClockConfig(4, 0.5, 0.0), // Bitclock, divided from the system clock
    ClockConfig.default,
    ClockConfig(74, 0.5, 0.0) // System clock
  )
  // The period of a 16 MHz clock is 62.5 nanoseconds
  // We have to multiply be 37.888 to get over the required 600 MHz
  val mmcm = Module(new MMCME2(10.0, 6.062, 1, 1.0, clockConfig, true))

  mmcm.CLKIN1   := clock
  mmcm.RST      := !reset.asBool()
  mmcm.PWRDWN   := false.B
  mmcm.CLKFBIN  := mmcm.CLKFBOUT

  val sysClk = mmcm.CLKOUT6
  val bitClk = mmcm.CLKOUT4

  io.sysClk     := sysClk
  io.bitClk     := bitClk

  withClock(bitClk) {
    //val (bitCount, sampleEdge) = Counter(true.B, 32)
    val bitCount = RegNext(0.U(6.W))
    val sampleClk = RegNext(false.B)

    sampleClk := sampleClk
    bitCount := bitCount + 1.U
    io.testOut := sampleClk

    when (bitCount === 31.U) {
      sampleClk := !sampleClk
      bitCount := 0.U
    }
  }
}
