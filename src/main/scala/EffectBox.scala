package EffectBox

import chisel3._
import chisel3.util.Decoupled
import chisel3.experimental.MultiIOModule
import io.SPIBus


class EffectBundle() extends Bundle {
  val in = Flipped(Decoupled(Input(SInt(32.W))))
  val out = Decoupled(Output(SInt(32.W)))
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

  //val bitcrush = Module(new BitCrush)
  //bitcrush.ctrl <> control.bitcrush

  val delay = Module(new Delay(16))
  delay.ctrl <> control.delay

  /*
   * Order effects
   */


  EffectBuffer(io.in, delay.io.in)
  EffectBuffer(delay.io.out, io.out)
}
