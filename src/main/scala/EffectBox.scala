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

  //val distortion = Module(new Distortion)
  //distortion.ctrl <> control.distortion

  val tremolo = Module(new Tremolo)
  tremolo.ctrl <> control.tremolo

  val delay = Module(new Delay)
  delay.ctrl <> control.delay

  /*
   * Order effects
   */

  EffectBuffer(io.in, bitcrush.io.in)
  EffectBuffer(bitcrush.io.out, tremolo.io.in)
  EffectBuffer(tremolo.io.out, delay.io.in)
  EffectBuffer(delay.io.out, io.out)
}
