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
    val pinout1     = Output(Bool())
    val pinout2     = Output(Bool())
    val pinout3     = Output(Clock())
    // val pinout4     = Output(Bool())
    //val pinout5     = Output(UInt(1.W))
    val pinout6     = Output(UInt(1.W))
    val pinout7     = Output(UInt(1.W))
    //val pinout8     = Output(UInt(1.W))
    //val pinout9     = Output(UInt(1.W))
    val sysClock    = Output(Clock())
    val bitClock    = Output(Bool())
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

  // Bits to write manually to DAC
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
    15.U -> 1.U)

  val dacMapLow: Array[(UInt, UInt)] = Array(
    0.U  -> 0.U,
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
    15.U -> 1.U)

   /*
    * Clock domain comClock = 2 x bitClock
    */

  withClock(comClock) {

    val BCLK = RegNext(false.B)
    val LRCLK = RegNext(true.B)

    // Bør være 4.W, men whatever, tør ikke endre uten å teste
    val bit_count = RegNext(0.U(6.W))   // Every other clock cycle = bit index in sample from MSB
    
    BCLK := !BCLK
    LRCLK := LRCLK
    bit_count := bit_count
    
    // Only necessary for testing DAC with square wave
    val wave_count = RegNext(0.U(16.W)) // Every half sample
    val square_wave = RegNext(true.B)   // Square wave high/low state
    
    wave_count := wave_count
    square_wave := square_wave

    when(BCLK) {
      bit_count := bit_count + 1.U
      when(bit_count === 15.U) {
        LRCLK := !LRCLK
        bit_count := 0.U
        // Square wave
        wave_count := wave_count + 1.U // sjekk at dette funker uten otherwise...
        when(wave_count === 79.U) { // 400 Hz
          square_wave := !square_wave
          wave_count := 0.U
        }
      }
    }

    // Either write bitmaps to dacOut
    // val value = Wire(UInt(1.W))
    // value := Mux(square_wave, MuxLookup(bit_count, 1.U(1.W), dacMapHigh), MuxLookup(bit_count, 1.U(1.W), dacMapLow))
    // io.dacOut := value

    // Or use a Mux
    //io.dacOut := Mux(square_wave && bit_count === 4.U, 0.U, 1.U)

    // Or try ADC -> DAC
    // val codec = Module(new Codec).io
    // codec.BCLK := BCLK
    // codec.LRCLK := LRCLK
    // codec.bit_count := bit_count
    // codec.adc_in := io.adcIn
    // codec.dac_out := io.dacOut
    
    // /* ADC -> DAC without Codec module
    val adc = Module(new ADCInterface).io
    val dac = Module(new DACInterface).io

    val enable = Wire(Bool())
    enable := Mux(bit_count === 0.U, true.B, false.B)
    // when(bit_count === 0.U) { enable := true.B }.otherwise{ enable := false.B }

    adc.BCLK := BCLK
    adc.LRCLK := LRCLK
    adc.bit := io.adcIn
  
    dac.BCLK := BCLK
    dac.enable := enable
    io.dacOut := dac.bit
  
    dac.sample := adc.sample


    /* OLD STUFF
    val sample_buffer = RegInit(UInt(16.W), 0.U)

    // Overwrite stored sample when ADC is ready
    when(adc.enable) {
      sample_buffer := adc.sample // Store sample for left channel
      dac.sample := adc.sample    // Write sample to right channel immediately
    }

     // Either drive DAC sample input with stored sample
    dac.sample := sample_buffer

    // Or drive DAC sample input only on DAC enable signal
    when(dac.enable) {
       dac.sample := sample_buffer
    }
    */

    // Clock outputs to codec
    io.sampleClock := LRCLK
    io.bitClock := BCLK
    
    // Pinouts
    io.pinout0 := sysClock
    io.pinout1 := BCLK
    io.pinout2 := LRCLK
    io.pinout3 := comClock
    // io.pinout4 := false.B
    // io.pinout5 := dac.enable
    io.pinout6 := io.adcIn
    io.pinout7 := io.dacOut
    //io.pinout8 := io.adcIn
    //io.pinout9 := io.dacOut
  }
}
