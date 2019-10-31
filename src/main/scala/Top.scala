package EffectBox

import chisel3._
import chisel3.core.withClock
import blackboxes.{MMCME2, ClockConfig, BRAM}
import chisel3.internal.firrtl.Width
import chisel3.util.MuxLookup

/**
  * A test for using multiple clocks at the same time
  */
class Top extends Module {
  val io = IO(new Bundle {
    val pinout0     = Output(Clock())
    val pinout1     = Output(Clock())
    val pinout2     = Output(UInt(1.W))
    val pinout3     = Output(UInt(1.W))
    //val pinout4     = Output(UInt(1.W))
    //val pinout5     = Output(UInt(1.W))
    val pinout6     = Output(UInt(1.W))
    val pinout7     = Output(UInt(1.W))
    val pinout8     = Output(UInt(1.W))
    val pinout9     = Output(UInt(1.W))
    val sysClock    = Output(Clock())
    val bitClock    = Output(Clock())
    val sampleClock = Output(Bool())
    val adcIn       = Input(UInt(1.W))
    val dacOut     = Output(UInt(1.W))
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

  def counter(max: UInt, width: Width) = {
    val x = RegInit(UInt(width), 0.U)
    x := Mux(x === max, 0.U, x + 1.U)
    x
  }

  val dacMapHigh: Array[(UInt, UInt)] = Array(
    0.U  -> 0.U,
    1.U  -> 1.U,
    2.U  -> 1.U,
    3.U  -> 1.U,
    4.U  -> 1.U,
    5.U  -> 1.U,
    6.U  -> 1.U,
    7.U  -> 1.U,
    8.U  -> 1.U,
    9.U  -> 1.U,
    10.U -> 1.U,
    11.U -> 1.U,
    12.U -> 1.U,
    13.U -> 1.U,
    14.U -> 1.U,
    15.U -> 1.U,
    16.U -> 1.U,
    17.U -> 1.U,
    18.U -> 1.U,
    19.U -> 1.U)

  val dacMapLow: Array[(UInt, UInt)] = Array(
    0.U  -> 1.U,
    1.U  -> 0.U,
    2.U  -> 0.U,
    3.U  -> 0.U,
    4.U  -> 0.U,
    5.U  -> 0.U,
    6.U  -> 0.U,
    7.U  -> 0.U,
    8.U  -> 0.U,
    9.U  -> 0.U,
    10.U -> 0.U,
    11.U -> 0.U,
    12.U -> 0.U,
    13.U -> 0.U,
    14.U -> 0.U,
    15.U -> 0.U,
    16.U -> 0.U,
    17.U -> 0.U,
    18.U -> 0.U,
    19.U -> 0.U)

  withClock(bitClock) {
    //val bit_count = counter(15.U, 6.W)
    val bit_count = RegNext(0.U(6.W))
    val LRCLK = RegNext(false.B)
    val wave_count = RegNext(0.U(16.W))
    val reg = RegNext(true.B)
    reg := reg
    wave_count := wave_count

    LRCLK := LRCLK
    io.sampleClock := LRCLK

    bit_count := bit_count + 1.U

    // Half sample period

    when (bit_count === 19.U) {
      bit_count := 0.U
      LRCLK := !LRCLK

      wave_count := wave_count + 1.U
      when (wave_count === 159.U) {
        wave_count := 0.U
        reg := !reg
      }

      // TODO: write sample_buffer based on value of reg
    }

    
    when (LRCLK) { // LEFT
      io.dacOut := Mux(reg, MuxLookup(bit_count, 1.U(1.W), dacMapHigh), MuxLookup(bit_count, 1.U(1.W), dacMapLow))   
    }.otherwise { // RIGHT
      io.dacOut := 1.U(1.W)
    }

    //io.dacOut := Mux(reg && bit_count === 4.U, 0.U, 1.U)

    // val adc = Module(new ADCInterface).io
    // val dac = Module(new DACInterface).io

    // adc.bit := io.adcIn
    // adc.LRCLK := LRCLK

    // io.dacOut := dac.bit_left
    // dac.LRCLK := LRCLK

    // val sample_buffer = RegInit(SInt(16.W), 0.U)

    // Overwrite stored sample when ADC is ready
    // when (adc.enable) {
    //   sample_buffer := adc.sample // Store sample for left channel
    //   dac.sample := adc.sample // Write sample to right channel immediately
    // }

    // Drive DAC sample input with stored sample
    // dac.sample := sample_buffer

    // when (dac.enable) {
    //   dac.sample := sample_buffer
    // }

    // val testBit = Mux(reg && bit_count === 4.U, 0.U, 1.U)

    io.pinout2 := LRCLK
    // io.pinout4 := adc.enable
    // io.pinout5 := dac.enable
    io.pinout6 := io.adcIn
    io.pinout7 := io.dacOut
    io.pinout8 := io.adcIn
    io.pinout9 := io.dacOut
  }
}
