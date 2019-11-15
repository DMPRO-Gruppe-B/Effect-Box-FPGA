package EffectBox

import chisel3._
import chisel3.util.Decoupled
import chisel3.experimental.MultiIOModule
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
  /*val debug = IO(new Bundle {
    val bitcrushCtrl = Output(new BitCrushControl)
  })*/

  /*
   * Setup the SPI control module
   */

  val control = Module(new EffectControl)
  control.spi <> spi

  /*
   * Initialize effects
   */

/*
  val bitcrush = Module(new BitCrush)
  bitcrush.ctrl <> control.bitcrush
  debug.bitcrushCtrl <> control.bitcrush
  */


  /*
   * Order effects
   */
  /*
  EffectBuffer(io.in, bitcrush.io.in)
  EffectBuffer(bitcrush.io.out, io.out)
  */

  val tremolo = Module(new Tremolo)
  tremolo.ctrl <> control.tremolo

  EffectBuffer(io.in, tremolo.io.in)
  EffectBuffer(tremolo.io.out, io.out)

}
