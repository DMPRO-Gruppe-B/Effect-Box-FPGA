package EffectBox

import chisel3._
import chisel3.util.Counter
import chisel3.core.withClock
import blackboxes.{MMCME2, ClockConfig, BRAM}


/**
  * A test for using multiple clocks at the same time
  */
class Top extends Module {
  val io = IO(new Bundle {
    val pinout0     = Output(Clock())
    val pinout1     = Output(Clock())
    val pinout2     = Output(UInt(1.W))
    val pinout3     = Output(UInt(1.W))
    val pinout4     = Output(UInt(1.W))
    val pinout5     = Output(UInt(1.W))
    val pinout6     = Output(UInt(1.W))
    val pinout7     = Output(UInt(1.W))
    val pinout8     = Output(UInt(1.W))
    val pinout9     = Output(UInt(1.W))
    val sysClock    = Output(Clock())
    val bitClock    = Output(Clock())
    val sampleClock = Output(Bool())
    val adcIn       = Input(UInt(1.W))
    val dacLeft     = Output(UInt(1.W))
  })


  /*
   * SETUP CLOCKS
   */

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
    ClockConfig(8, 0.5, 0.0), // Bitclock, divided from the system clock
    ClockConfig.default,
    ClockConfig(74, 0.5, 0.0) // System clock
  )
  // The period of a 16 MHz clock is 62.5 nanoseconds
  // We have to multiply be 37.888 to get over the required 600 MHz
  val mmcm = Module(new MMCME2(62.5, 37.888, 1, 1.0, clockConfig, true))

  mmcm.CLKIN1   := clock
  mmcm.RST      := false.B //!reset.asBool()
  mmcm.PWRDWN   := false.B
  mmcm.CLKFBIN  := mmcm.CLKFBOUT

  val sysClock = mmcm.CLKOUT6
  val bitClock = mmcm.CLKOUT4

  io.sysClock := sysClock
  io.bitClock := bitClock

  io.pinout0 := sysClock
  io.pinout1 := bitClock
  io.pinout3 := reset.asBool()

  /*
   * SETUP DAC/ADC
   */

  withClock(bitClock) {
    val bitCount = RegNext(0.U(6.W))
    val LRCLK = RegNext(false.B)
    val reg = RegNext(true.B)
    val counter = RegNext(0.U(16.W))
    counter := counter
    reg := reg

    LRCLK := LRCLK
    bitCount := bitCount + 1.U
    io.sampleClock := LRCLK

    when (bitCount === 15.U) {
      LRCLK := !LRCLK
      bitCount := 0.U

      counter := counter + 1.U
      when (counter === 80.U) {
        counter := 0.U
        reg := !reg
      }
    }

    val adc = Module(new ADCInterface).io
    val dac = Module(new DACInterface).io

    adc.bit := io.adcIn
    adc.LRCLK := LRCLK

    dac.LRCLK := LRCLK

    val testBit = Mux(reg && bitCount === 4.U, 0.U, 1.U)
    io.dacLeft := testBit //dac.bit_left

    dac.sample := 0.S

    io.pinout2 := LRCLK
    io.pinout4 := adc.enable
    io.pinout5 := dac.enable
    io.pinout6 := io.adcIn
    io.pinout7 := testBit
    io.pinout8 := io.adcIn
    io.pinout9 := testBit
  }
}
