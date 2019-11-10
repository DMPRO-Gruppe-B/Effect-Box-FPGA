package EffectBox

import chisel3._
import chisel3.core.withClock
import freechips.asyncqueue.{AsyncQueue, AsyncQueueParams}
import blackboxes.{ClockConfig, MMCME2}
import io.SPIBus

/**
  * A test for using multiple clocks at the same time
  */
class Top extends Module {
  val io = IO(new Bundle {
    val pinout0     = Output(Clock())
    val pinout1     = Output(Bool())
    val pinout2     = Output(Bool())
    val pinout3     = Output(Clock())
    //val pinout4     = Output(UInt(1.W))
    //val pinout5     = Output(UInt(1.W))
    val pinout6     = Output(UInt(1.W))
    val pinout7     = Output(UInt(1.W))
    //val pinout8     = Output(UInt(1.W))
    //val pinout9     = Output(UInt(1.W))

    // LEDs on devkit
    //val pinout12 = Output(UInt(1.W))
    //val pinout13 = Output(UInt(1.W))
    //val pinout14 = Output(UInt(1.W))
    //val pinout15 = Output(UInt(1.W))

    val sysClock    = Output(Clock())
    val bitClock    = Output(Bool())
    val sampleClock = Output(Bool())
    val adcIn       = Input(UInt(1.W))
    val dacOut     = Output(UInt(1.W))

    val spi = new SPIBus
  })

  /*
   * Effects
   */

  val effectbox = Module(new EffectBox)
  effectbox.spi <> io.spi

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
    ClockConfig(4, 0.5, 0.0),  // comClock, divided from the system clock
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
  val comClock = mmcm.CLKOUT4

  io.sysClock := sysClock

   /*
    * Clock domain comClock = 2 x bitClock
    */

  val codec = withClock(comClock) { Module(new Codec).io }
  codec.adc_in := io.adcIn
  io.dacOut := codec.dac_out

  // Async queues, for communication across clock domains
  val adc_sample_queue = Module(new AsyncQueue(SInt(32.W), AsyncQueueParams.singleton()))
  val dac_sample_queue = Module(new AsyncQueue(SInt(32.W), AsyncQueueParams.singleton()))

  adc_sample_queue.io.enq_clock := comClock
  adc_sample_queue.io.enq <> codec.adc_sample
  adc_sample_queue.io.deq_clock := clock
  adc_sample_queue.io.deq <> effectbox.io.in
  adc_sample_queue.io.enq_reset := DontCare
  adc_sample_queue.io.deq_reset := DontCare

  dac_sample_queue.io.enq_clock := comClock
  dac_sample_queue.io.enq <> effectbox.io.out
  dac_sample_queue.io.deq_clock := clock
  dac_sample_queue.io.deq <> codec.dac_sample
  dac_sample_queue.io.enq_reset := DontCare
  dac_sample_queue.io.deq_reset := DontCare

  /*
   * Debug
   */

  withClock(comClock) {
    // Clock outputs to codec
    io.sampleClock := codec.LRCLK
    io.bitClock := codec.BCLK

    // Pinouts
    io.pinout0 := sysClock
    io.pinout1 := codec.BCLK
    io.pinout2 := codec.LRCLK
    io.pinout3 := comClock
    //io.pinout4 := dac.debug
    //io.pinout5 := adc.debug
    io.pinout6 := io.adcIn
    io.pinout7 := io.dacOut
    //io.pinout8 := io.adcIn
    //io.pinout9 := io.dacOut
  }
}
