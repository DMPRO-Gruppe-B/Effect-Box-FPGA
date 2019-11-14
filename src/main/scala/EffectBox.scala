package EffectBox

import chisel3._
import chisel3.util.Decoupled
import chisel3.MultiIOModule
import io.SPIBus


object Sample {
  def apply() = SInt(32.W)
}


class EffectBundle() extends Bundle {
  val in = Flipped(Decoupled(Input(Sample())))
  val out = Decoupled(Output(Sample()))
}


/**
  * The main class for the effect box.
  * It takes input from the outside, puts it
  * through the effect modules, and outputs it.
  */
class EffectBox() extends MultiIOModule {
  val spi = IO(new SPIBus)
  val io = IO(new EffectBundle)
  val debug = IO(new Bundle {
    val bitcrushCtrl = Output(new BitCrushControl)
  })

  /*
   * Setup the SPI control module
   */

  val control = Module(new EffectControl)
  control.spi <> spi

  /*
   * Initialize effects
   */

  val bitcrush = Module(new BitCrush)
  bitcrush.ctrl <> control.bitcrush
  debug.bitcrushCtrl <> control.bitcrush

  //val highpass = Module(new FirFilter(32, Seq(1.U, 1.U, 1.U)))
  //highpass.ctrl <> control.fir_filter

  val tremolo = Module(new Tremolo)
  tremolo.ctrl <> control.tremolo

  /*
   * Order effects
   */

  //EffectBuffer(io.in, bitcrush.io.in)
  //EffectBuffer(bitcrush.io.out, io.out)

  EffectBuffer(io.in, bitcrush.io.in)
  EffectBuffer(bitcrush.io.out, tremolo.io.in)
  EffectBuffer(tremolo.io.out, io.out)

}
