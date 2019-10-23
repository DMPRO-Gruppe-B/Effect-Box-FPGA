package EffectBox

import chisel3._
import chisel3.util.{Counter,Cat}


class ADCInterface extends Module {
  val io = IO(
    new Bundle {
      val bitIn       = Input(UInt(1.W))
      val BCLK        = Input(Bool())
      val LRCLK       = Input(Bool())

      val sampleOut     = Output(SInt(16.W))
    }
  )
  
  val counter = new Counter(16)
  val sample_reg = RegInit(UInt(16.W),0.U)
  io.sampleOut := 0.S

  when(io.LRCLK === true.B && io.BCLK === true.B){
      when(counter.value === 0.U){
        io.sampleOut := 0.S
        sample_reg := 0.U(1.W)
      }
      .otherwise    {sample_reg := sample_reg << 1}
      printf("Sample before is %d\n",sample_reg)
      sample_reg := sample_reg + io.bitIn
      printf("%d\n",io.bitIn)
      printf("Sample after is %d\n\n",sample_reg)

      when(counter.value === 15.U){
          // do_asSInt reinterprets SInt without adding sign bit
          //printf("\nSample_reg is %d\n",sample_reg.do_asSInt)
          io.sampleOut := sample_reg.do_asSInt
      }
      counter.inc()
  }
  


}
